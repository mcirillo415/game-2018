package com.d3games.engine.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;

import com.d3games.engine.GameManager;
import com.d3games.engine.map.Door;
import com.d3games.engine.map.GameMap;
import com.d3games.engine.map.HealerNPC;
import com.d3games.engine.map.MapCreator;
import com.d3games.engine.map.Unit;

public class EditorWorldGameCompatibilityTest {

	@Test
	public void handBuiltWorldCoveringEveryEntityTypeLoadsThroughTheRealMapCreator() throws IOException {
		GameManager gameManager = GameManager.getInstance();
		int alphaId = gameManager.getAll().size();
		int betaId = alphaId + 1;

		EditorWorld world = new EditorWorld();

		EditorRoom alpha = new EditorRoom(alphaId, "alpha", 3, 3);
		setSpace(alpha, 0, 0);
		setSpace(alpha, 1, 0);
		setObstacle(alpha, 2, 0);
		setWildGrass(alpha, 0, 1);
		setPickup(alpha, 1, 1, "CURRENCY", 10);
		setSpace(alpha, 2, 1);
		setSpace(alpha, 0, 2);
		setDoor(alpha, 1, 2, String.valueOf(betaId), 0, 0, EditorCell.LockType.KEY, "TestKey", null, 0, 0);
		setSpace(alpha, 2, 2);
		setCharacter(alpha, 0, 0, EditorCell.CharacterType.PLAYER_START, false, null);
		setCharacter(alpha, 1, 0, EditorCell.CharacterType.NPC, true, null);
		setCharacter(alpha, 0, 1, EditorCell.CharacterType.SHOP, false, null);
		setCharacter(alpha, 2, 1, EditorCell.CharacterType.DIALOGUE, false, "Hello traveler!");
		setCharacter(alpha, 0, 2, EditorCell.CharacterType.HEALER, false, null);
		world.addRoom(alpha);

		EditorRoom beta = new EditorRoom(betaId, "beta", 2, 2);
		setSpace(beta, 0, 0);
		setDoor(beta, 0, 0, String.valueOf(alphaId), 1, 2, EditorCell.LockType.NONE, null, null, 0, 0);
		world.addRoom(beta);

		File tempDir = Files.createTempDirectory("editorworldcompat").toFile();
		File manifest = new File(tempDir, "manifest.txt");
		new RoomFileWriter().saveWorld(world, manifest);

		MapCreator.getInstance().generateWorld(manifest);

		GameMap alphaMap = gameManager.get(alphaId);
		GameMap betaMap = gameManager.get(betaId);
		assertEquals("alpha", alphaMap.getRoomName());
		assertEquals("beta", betaMap.getRoomName());

		assertEquals(gameManager.getPlayer().getMap(), alphaMap);
		assertEquals(0, gameManager.getPlayer().getXPos());
		assertEquals(0, gameManager.getPlayer().getYPos());

		Door door = (Door) alphaMap.get(1, 2);
		assertEquals(Door.LockType.KEY, door.getLockType());
		gameManager.getKeyRing().add("TestKey");
		Unit landing = door.approach();
		assertEquals(betaMap, landing.getMap());

		HealerNPC healer = (HealerNPC) alphaMap.get(0, 2).getCharacter();
		gameManager.getPlayerCombatant().takeDamage(50);
		assertTrue(gameManager.getPlayerCombatant().getHealth() < gameManager.getPlayerCombatant().getMaximumHealth());
		healer.prompt();
		assertEquals(gameManager.getPlayerCombatant().getMaximumHealth(), gameManager.getPlayerCombatant().getHealth());
	}

	private void setSpace(EditorRoom room, int x, int y) {
		room.getCell(x, y).setGroundType(EditorCell.GroundType.SPACE);
	}

	private void setObstacle(EditorRoom room, int x, int y) {
		room.getCell(x, y).setGroundType(EditorCell.GroundType.OBSTACLE);
	}

	private void setWildGrass(EditorRoom room, int x, int y) {
		room.getCell(x, y).setGroundType(EditorCell.GroundType.WILD_GRASS);
	}

	private void setPickup(EditorRoom room, int x, int y, String kind, int amount) {
		EditorCell cell = room.getCell(x, y);
		cell.setGroundType(EditorCell.GroundType.ITEM_PICKUP);
		cell.setPickupKind(kind);
		cell.setPickupAmount(amount);
	}

	private void setDoor(EditorRoom room, int x, int y, String targetRoom, int targetX, int targetY,
			EditorCell.LockType lockType, String keyName, String lockNpcRoom, int lockNpcX, int lockNpcY) {
		EditorCell cell = room.getCell(x, y);
		cell.setGroundType(EditorCell.GroundType.DOOR);
		cell.setTargetRoom(targetRoom);
		cell.setTargetX(targetX);
		cell.setTargetY(targetY);
		cell.setLockType(lockType);
		if (lockType == EditorCell.LockType.KEY)
			cell.setLockKeyName(keyName);
		if (lockType == EditorCell.LockType.NPC) {
			cell.setLockNpcRoom(lockNpcRoom);
			cell.setLockNpcX(lockNpcX);
			cell.setLockNpcY(lockNpcY);
		}
	}

	private void setCharacter(EditorRoom room, int x, int y, EditorCell.CharacterType type,
			boolean npcInitiatesBattle, String dialogue) {
		EditorCell cell = room.getCell(x, y);
		cell.setCharacterType(type);
		cell.setNpcInitiatesBattle(npcInitiatesBattle);
		cell.setDialogueText(dialogue);
	}
}
