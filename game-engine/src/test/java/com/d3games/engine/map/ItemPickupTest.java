package com.d3games.engine.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

import com.d3games.engine.GameManager;
import com.d3games.engine.item.ItemType;

public class ItemPickupTest {

	@After
	public void cleanup() {
		// These tests use ad-hoc GameMap ids that are never registered with
		// GameManager, so clear the collected-pickup records they leave behind
		// rather than letting them leak into other tests (e.g. save/load).
		GameManager.getInstance().clearCollectedPickups();
	}

	@Test
	public void grantsCurrencyAndTurnsIntoPlainGrass() {
		GameMap map = new GameMap(99, 2, 2);
		ItemPickup pickup = new ItemPickup("CURRENCY", 15);
		map.add(pickup, 0, 0);
		int before = GameManager.getInstance().getWallet().getBalance();

		Unit result = pickup.approach();

		assertEquals(before + 15, GameManager.getInstance().getWallet().getBalance());
		assertFalse(result instanceof ItemPickup);
		assertFalse(map.get(0, 0) instanceof ItemPickup);
		assertTrue(GameManager.getInstance().isMessagePending());
		assertEquals("Found 15 Bones!", GameManager.getInstance().consumePendingMessage());
	}

	@Test
	public void grantsItemsAndBuildsSingularMessageForOne() {
		GameMap map = new GameMap(98, 2, 2);
		ItemPickup pickup = new ItemPickup("POTION", 1);
		map.add(pickup, 0, 0);
		int before = GameManager.getInstance().getInventory().getCount(ItemType.POTION);

		pickup.approach();

		assertEquals(before + 1, GameManager.getInstance().getInventory().getCount(ItemType.POTION));
		assertEquals("Found a Potion!", GameManager.getInstance().consumePendingMessage());
	}
}
