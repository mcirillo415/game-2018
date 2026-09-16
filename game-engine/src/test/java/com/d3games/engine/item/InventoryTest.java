package com.d3games.engine.item;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class InventoryTest {
	private Inventory inventory;

	@Before
	public void setup() {
		inventory = new Inventory();
	}

	@Test
	public void startsWithDefaultCounts() {
		assertEquals(ItemType.POTION.getStartingCount(), inventory.getCount(ItemType.POTION));
		assertEquals(ItemType.POKEBALL.getStartingCount(), inventory.getCount(ItemType.POKEBALL));
	}

	@Test
	public void useDecrementsCount() {
		int before = inventory.getCount(ItemType.POTION);
		inventory.use(ItemType.POTION);
		assertEquals(before - 1, inventory.getCount(ItemType.POTION));
	}

	@Test(expected = IllegalStateException.class)
	public void useThrowsWhenEmpty() {
		for (int i = 0; i < ItemType.POTION.getStartingCount(); i++)
			inventory.use(ItemType.POTION);
		inventory.use(ItemType.POTION);
	}

	@Test
	public void addIncreasesCount() {
		int before = inventory.getCount(ItemType.POKEBALL);
		inventory.add(ItemType.POKEBALL, 3);
		assertEquals(before + 3, inventory.getCount(ItemType.POKEBALL));
	}
}
