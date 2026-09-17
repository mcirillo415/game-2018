package com.d3games.engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.d3games.engine.battle.Combatant;
import com.d3games.engine.battle.ElementType;
import com.d3games.engine.battle.Move;
import com.d3games.engine.battle.Party;
import com.d3games.engine.battle.StatusEffect;
import com.d3games.engine.item.Inventory;
import com.d3games.engine.item.ItemType;
import com.d3games.engine.item.KeyRing;
import com.d3games.engine.item.Wallet;
import com.d3games.engine.map.MapCoordinate;
import com.d3games.engine.map.Player;
import com.d3games.engine.map.Space;

public class SaveManager {
	private static SaveManager instance;

	private SaveManager() {
	}

	public static SaveManager getInstance() {
		if (instance == null)
			instance = new SaveManager();
		return instance;
	}

	public void save(File file) throws IOException {
		GameManager gameManager = GameManager.getInstance();
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
			Player player = gameManager.getPlayer();
			writer.write("player=" + player.getMap().getId() + "," + player.getXPos() + "," + player.getYPos());
			writer.newLine();

			writer.write("wallet=" + gameManager.getWallet().getBalance());
			writer.newLine();

			StringBuilder inventoryLine = new StringBuilder("inventory=");
			for (ItemType type : ItemType.values()) {
				if (inventoryLine.charAt(inventoryLine.length() - 1) != '=')
					inventoryLine.append(",");
				inventoryLine.append(type.name()).append(":").append(gameManager.getInventory().getCount(type));
			}
			writer.write(inventoryLine.toString());
			writer.newLine();

			Party party = gameManager.getParty();
			List<Combatant> members = party.getMembers();
			writer.write("party.active=" + members.indexOf(party.getActive()));
			writer.newLine();

			for (int i = 0; i < members.size(); i++) {
				Combatant member = members.get(i);
				writer.write("party.member=" + member.getName() + "," + member.getLevel() + ","
						+ member.getMaximumHealth() + "," + member.getAttackPower() + "," + member.getDefense() + ","
						+ member.getHealth() + "," + member.getExperience() + "," + member.getElementType() + ","
						+ member.getStatus());
				writer.newLine();
				for (Move move : member.getMoves()) {
					writer.write("party.move=" + i + "," + move.getName() + "," + move.getElementType() + ","
							+ move.getDamageMultiplier() + "," + move.getRecoilFraction() + ","
							+ move.getLifestealFraction() + "," + move.getEvadeChance() + ","
							+ move.getInflictedStatus() + "," + move.getStatusChance());
					writer.newLine();
				}
			}

			for (MapCoordinate pickup : gameManager.getCollectedPickups()) {
				writer.write("collected=" + pickup.mapId + "," + pickup.x + "," + pickup.y);
				writer.newLine();
			}

			for (MapCoordinate npc : gameManager.getDefeatedNpcs()) {
				writer.write("defeated=" + npc.mapId + "," + npc.x + "," + npc.y);
				writer.newLine();
			}

			for (String keyName : gameManager.getKeyRing().getAll()) {
				writer.write("key=" + keyName);
				writer.newLine();
			}
		}
	}

	public void load(File file) throws IOException {
		String playerLine = null;
		String walletLine = null;
		String inventoryLine = null;
		int activeIndex = 0;
		List<String> memberLines = new ArrayList<String>();
		Map<Integer, List<String>> moveLinesByMember = new HashMap<Integer, List<String>>();
		List<String> collectedLines = new ArrayList<String>();
		List<String> defeatedLines = new ArrayList<String>();
		List<String> keyLines = new ArrayList<String>();

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				int splitIndex = line.indexOf('=');
				if (splitIndex < 0)
					continue;
				String key = line.substring(0, splitIndex);
				String value = line.substring(splitIndex + 1);
				if ("player".equals(key)) {
					playerLine = value;
				} else if ("wallet".equals(key)) {
					walletLine = value;
				} else if ("inventory".equals(key)) {
					inventoryLine = value;
				} else if ("party.active".equals(key)) {
					activeIndex = Integer.parseInt(value);
				} else if ("party.member".equals(key)) {
					memberLines.add(value);
				} else if ("party.move".equals(key)) {
					int commaIndex = value.indexOf(',');
					int memberIndex = Integer.parseInt(value.substring(0, commaIndex));
					String moveValue = value.substring(commaIndex + 1);
					if (!moveLinesByMember.containsKey(memberIndex))
						moveLinesByMember.put(memberIndex, new ArrayList<String>());
					moveLinesByMember.get(memberIndex).add(moveValue);
				} else if ("collected".equals(key)) {
					collectedLines.add(value);
				} else if ("defeated".equals(key)) {
					defeatedLines.add(value);
				} else if ("key".equals(key)) {
					keyLines.add(value);
				}
			}
		}

		GameManager gameManager = GameManager.getInstance();

		List<Combatant> members = new ArrayList<Combatant>();
		for (int i = 0; i < memberLines.size(); i++) {
			String[] parts = memberLines.get(i).split(",");
			Combatant combatant = new Combatant(parts[0], Integer.parseInt(parts[2]),
					Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[1]));
			combatant.setElementType(ElementType.valueOf(parts[7]));
			combatant.setHealth(Integer.parseInt(parts[5]));
			combatant.setExperience(Integer.parseInt(parts[6]));
			if (parts.length > 8)
				combatant.setStatus(StatusEffect.valueOf(parts[8]));

			List<String> moveLines = moveLinesByMember.containsKey(i)
					? moveLinesByMember.get(i)
					: Collections.<String>emptyList();
			List<Move> moves = new ArrayList<Move>();
			for (String moveLine : moveLines) {
				String[] moveParts = moveLine.split(",");
				StatusEffect inflictedStatus = moveParts.length > 6
						? StatusEffect.valueOf(moveParts[6])
						: StatusEffect.NONE;
				double statusChance = moveParts.length > 7 ? Double.parseDouble(moveParts[7]) : 0;
				moves.add(new Move(moveParts[0], ElementType.valueOf(moveParts[1]),
						Double.parseDouble(moveParts[2]), Double.parseDouble(moveParts[3]),
						Double.parseDouble(moveParts[4]), Double.parseDouble(moveParts[5]),
						inflictedStatus, statusChance));
			}
			if (!moves.isEmpty())
				combatant.setMoves(moves);

			members.add(combatant);
		}

		Party party = new Party(members.get(0));
		for (int i = 1; i < members.size(); i++)
			party.add(members.get(i));
		party.setActiveIndex(activeIndex);
		gameManager.setParty(party);

		Inventory inventory = new Inventory();
		for (String entry : inventoryLine.split(",")) {
			String[] parts = entry.split(":");
			inventory.setCount(ItemType.valueOf(parts[0]), Integer.parseInt(parts[1]));
		}
		gameManager.setInventory(inventory);

		Wallet wallet = new Wallet();
		wallet.add(Integer.parseInt(walletLine));
		gameManager.setWallet(wallet);

		gameManager.clearCollectedPickups();
		for (String collectedLine : collectedLines) {
			String[] parts = collectedLine.split(",");
			int mapId = Integer.parseInt(parts[0]);
			int x = Integer.parseInt(parts[1]);
			int y = Integer.parseInt(parts[2]);
			gameManager.get(mapId).replace(new Space(), x, y);
			gameManager.recordPickupCollected(mapId, x, y);
		}

		gameManager.clearDefeatedNpcs();
		for (String defeatedLine : defeatedLines) {
			String[] parts = defeatedLine.split(",");
			gameManager.recordNpcDefeated(new MapCoordinate(
					Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
		}

		KeyRing keyRing = new KeyRing();
		for (String keyName : keyLines)
			keyRing.add(keyName);
		gameManager.setKeyRing(keyRing);

		String[] playerParts = playerLine.split(",");
		int playerMapId = Integer.parseInt(playerParts[0]);
		int playerX = Integer.parseInt(playerParts[1]);
		int playerY = Integer.parseInt(playerParts[2]);
		gameManager.getPlayer().setPos(gameManager.get(playerMapId).get(playerX, playerY));
	}
}
