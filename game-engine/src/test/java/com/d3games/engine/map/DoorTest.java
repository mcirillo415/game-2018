package com.d3games.engine.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.d3games.engine.GameManager;
import com.d3games.engine.InvalidMoveException;
import com.d3games.engine.item.KeyRing;

public class DoorTest {
	private GameMap map1;
	private GameMap map2;
	private Space space1;
	private Space space2;
	private Door door1;
	private Obstacle obstacle;
	private Player player;

	@Before
	public void setup() {
		map1 = new GameMap(0, 1, 2);
		map2 = new GameMap(1, 1, 2);
		space1 = new Space();
		space2 = new Space();
		obstacle = new Obstacle();
		player = new Player();
	}

	//@Test
	public void doorToNewMap() {
		map1.add(space1, 0, 0);
		map2.add(space2, 0, 1);
		door1 = new Door(map2, 0, 1);
		map1.add(door1, 0, 1);
		player.setPos(space1);
		player.moveDown();
		assertEquals(map2, player.getMap());
	}

	@Test(expected = InvalidMoveException.class)
	public void obstacle() {
		map1.add(space1, 0, 0);
		map1.add(obstacle, 0, 1);
		player.setPos(space1);
		player.moveDown();
		assertEquals(map1, player.getMap());
		assertEquals(0, player.getYPos());
	}

	@Test
	public void keyLockedDoorBlocksApproachUntilKeyIsHeld() {
		GameManager gameManager = GameManager.getInstance();
		gameManager.setKeyRing(new KeyRing());

		int targetMapId = gameManager.getAll().size();
		GameMap targetMap = new GameMap(targetMapId, 2, 2);
		gameManager.add(targetMap);
		Space targetSpace = new Space();
		targetMap.add(targetSpace, 0, 0);

		Door door = new Door(targetMapId, 0, 0);
		door.setKeyLock("BronzeKey");

		try {
			door.approach();
			fail("Expected the door to be locked");
		} catch (InvalidMoveException expected) {
			// expected
		}

		gameManager.getKeyRing().add("BronzeKey");
		assertEquals(targetSpace, door.approach());
	}

	@Test
	public void npcDefeatLockedDoorBlocksApproachUntilThatSpecificNpcIsDefeated() {
		GameManager gameManager = GameManager.getInstance();
		gameManager.clearDefeatedNpcs();

		int targetMapId = gameManager.getAll().size();
		GameMap targetMap = new GameMap(targetMapId, 2, 2);
		gameManager.add(targetMap);
		Space targetSpace = new Space();
		targetMap.add(targetSpace, 0, 0);

		MapCoordinate guardLocation = new MapCoordinate(targetMapId, 3, 3);
		Door door = new Door(targetMapId, 0, 0);
		door.setNpcLock(guardLocation);

		try {
			door.approach();
			fail("Expected the door to be locked");
		} catch (InvalidMoveException expected) {
			// expected
		}

		gameManager.recordNpcDefeated(new MapCoordinate(targetMapId, 5, 5));
		try {
			door.approach();
			fail("Defeating a different NPC should not unlock this door");
		} catch (InvalidMoveException expected) {
			// expected
		}

		gameManager.recordNpcDefeated(guardLocation);
		assertEquals(targetSpace, door.approach());
	}
}
