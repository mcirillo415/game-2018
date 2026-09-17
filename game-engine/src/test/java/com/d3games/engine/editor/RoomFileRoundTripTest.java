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
import com.d3games.engine.map.MapCreator;

public class RoomFileRoundTripTest {

	private static final File REAL_MANIFEST = new File(
			"../pokemon-clone/pokemon-clone-core/src/main/resources/com/d3games/pokemon/world3/manifest.txt");

	@Test
	public void reloadedAndResavedRealWorldParsesIdenticallyThroughTheRealMapCreator() throws IOException {
		assertTrue("Expected to find the real world3 manifest at " + REAL_MANIFEST.getAbsolutePath(),
				REAL_MANIFEST.exists());

		EditorWorld world = new RoomFileReader().loadWorld(REAL_MANIFEST);

		File tempDir = Files.createTempDirectory("roomfileroundtrip").toFile();
		File tempManifest = new File(tempDir, "manifest.txt");
		new RoomFileWriter().saveWorld(world, tempManifest);

		GameManager gameManager = GameManager.getInstance();
		gameManager.resetForTesting();
		int baseId = gameManager.getAll().size();
		MapCreator.getInstance().generateWorld(tempManifest);

		GameMap hometown = findRoomByName(gameManager, baseId, "hometown");
		assertEquals(7, hometown.getWidth());
		assertEquals(5, hometown.getHeight());

		GameMap corridor4 = findRoomByName(gameManager, baseId, "corridor_4");
		Door lockedDoor = (Door) corridor4.get(1, 9);
		assertEquals(Door.LockType.KEY, lockedDoor.getLockType());

		GameMap corridor5 = findRoomByName(gameManager, baseId, "corridor_5");
		Door npcLockedDoor = (Door) corridor5.get(1, 9);
		assertEquals(Door.LockType.NPC_DEFEAT, npcLockedDoor.getLockType());
	}

	private GameMap findRoomByName(GameManager gameManager, int baseId, String name) {
		for (int i = baseId; i < gameManager.getAll().size(); i++) {
			GameMap map = gameManager.get(i);
			if (name.equals(map.getRoomName()))
				return map;
		}
		throw new AssertionError("Room not found after reload: " + name);
	}
}
