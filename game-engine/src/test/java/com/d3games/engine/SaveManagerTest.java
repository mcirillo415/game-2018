package com.d3games.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.Test;

import com.d3games.engine.battle.Combatant;
import com.d3games.engine.battle.ElementType;
import com.d3games.engine.battle.Move;
import com.d3games.engine.battle.Party;
import com.d3games.engine.battle.StatusEffect;
import com.d3games.engine.item.ItemType;
import com.d3games.engine.item.KeyRing;
import com.d3games.engine.item.Wallet;
import com.d3games.engine.map.GameMap;
import com.d3games.engine.map.ItemPickup;
import com.d3games.engine.map.MapCoordinate;
import com.d3games.engine.map.Player;
import com.d3games.engine.map.Space;

public class SaveManagerTest {

	@Test
	public void roundTripsPartyInventoryWalletPositionAndCollectedPickups() throws IOException {
		GameManager gameManager = GameManager.getInstance();
		gameManager.clearCollectedPickups();
		gameManager.clearDefeatedNpcs();
		gameManager.setKeyRing(new KeyRing());

		int mapId = gameManager.getAll().size();
		GameMap map = new GameMap(mapId, 4, 4);
		gameManager.add(map);
		Space start = new Space();
		map.add(start, 1, 1);
		Space endSpot = new Space();
		map.add(endSpot, 3, 3);
		Player player = new Player(start);
		gameManager.setPlayer(player);

		Combatant leader = new Combatant("Ice Dog", 40, 12, 3, 3);
		leader.setElementType(ElementType.ICE);
		leader.setMoves(Arrays.asList(
				new Move("Bite", ElementType.NORMAL, 1.0, 0, 0, 0),
				new Move("Frost Bite", ElementType.ICE, 1.3, 0, 0.1, 0.05, StatusEffect.PARALYSIS, 0.2)));
		leader.setHealth(17);
		leader.setExperience(9);
		leader.setStatus(StatusEffect.POISON);
		Party party = new Party(leader);
		Combatant backup = new Combatant("Backup", 30, 8, 1, 1);
		party.add(backup);
		party.setActiveIndex(1);
		gameManager.setParty(party);

		gameManager.getWallet().add(37);
		int expectedBalance = gameManager.getWallet().getBalance();

		ItemPickup pickup = new ItemPickup("POTION", 1);
		map.add(pickup, 2, 2);
		pickup.approach();

		gameManager.getInventory().setCount(ItemType.POTION, 4);
		gameManager.getInventory().setCount(ItemType.POKEBALL, 2);
		gameManager.getInventory().setCount(ItemType.REVIVE, 3);

		gameManager.getKeyRing().add("BronzeKey");
		MapCoordinate guardLocation = new MapCoordinate(mapId, 0, 0);
		gameManager.recordNpcDefeated(guardLocation);

		player.setPos(map.get(3, 3));

		File saveFile = Files.createTempFile("savemanagertest", ".txt").toFile();
		saveFile.deleteOnExit();
		SaveManager.getInstance().save(saveFile);

		// Simulate relaunching: fresh party/inventory/wallet, and the pickup tile
		// exists again as it would after a fresh world parse.
		gameManager.setParty(new Party(new Combatant("Fresh Start", 10, 5, 0)));
		gameManager.getInventory().setCount(ItemType.POTION, 0);
		gameManager.getInventory().setCount(ItemType.POKEBALL, 0);
		gameManager.getInventory().setCount(ItemType.REVIVE, 0);
		gameManager.setWallet(new Wallet());
		map.replace(new ItemPickup("POTION", 1), 2, 2);
		player.setPos(map.get(1, 1));
		gameManager.setKeyRing(new KeyRing());
		gameManager.clearDefeatedNpcs();

		SaveManager.getInstance().load(saveFile);

		Party loadedParty = gameManager.getParty();
		assertEquals(2, loadedParty.getMembers().size());
		Combatant loadedLeader = loadedParty.getMembers().get(0);
		assertEquals("Ice Dog", loadedLeader.getName());
		assertEquals(3, loadedLeader.getLevel());
		assertEquals(ElementType.ICE, loadedLeader.getElementType());
		assertEquals(17, loadedLeader.getHealth());
		assertEquals(9, loadedLeader.getExperience());
		assertEquals(2, loadedLeader.getMoves().size());
		assertEquals("Frost Bite", loadedLeader.getMoves().get(1).getName());
		assertEquals(StatusEffect.PARALYSIS, loadedLeader.getMoves().get(1).getInflictedStatus());
		assertEquals(0.2, loadedLeader.getMoves().get(1).getStatusChance(), 0.0001);
		assertEquals(StatusEffect.POISON, loadedLeader.getStatus());
		assertEquals(loadedParty.getMembers().get(1), loadedParty.getActive());

		assertEquals(4, gameManager.getInventory().getCount(ItemType.POTION));
		assertEquals(2, gameManager.getInventory().getCount(ItemType.POKEBALL));
		assertEquals(3, gameManager.getInventory().getCount(ItemType.REVIVE));
		assertEquals(expectedBalance, gameManager.getWallet().getBalance());

		assertEquals(3, player.getXPos());
		assertEquals(3, player.getYPos());

		assertFalse(map.get(2, 2) instanceof ItemPickup);
		assertTrue(map.get(2, 2) instanceof Space);

		assertTrue(gameManager.getKeyRing().has("BronzeKey"));
		assertTrue(gameManager.isNpcDefeated(guardLocation));
	}
}
