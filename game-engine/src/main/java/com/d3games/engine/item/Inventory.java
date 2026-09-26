package com.d3games.engine.item;

import java.util.EnumMap;
import java.util.Map;

public class Inventory {
	private final Map<ItemType, Integer> counts = new EnumMap<ItemType, Integer>(ItemType.class);

	public Inventory() {
		for (ItemType type : ItemType.values())
			counts.put(type, type.getStartingCount());
	}

	public int getCount(ItemType type) {
		return counts.get(type);
	}

	public void use(ItemType type) {
		int count = getCount(type);
		if (count <= 0)
			throw new IllegalStateException("No " + type.getDisplayName() + " left");
		counts.put(type, count - 1);
	}

	public void add(ItemType type, int amount) {
		if (amount < 0)
			throw new IllegalArgumentException("Cannot add a negative amount");
		counts.put(type, getCount(type) + amount);
	}

	public void setCount(ItemType type, int amount) {
		if (amount < 0)
			throw new IllegalArgumentException("Count cannot be negative");
		counts.put(type, amount);
	}
}
