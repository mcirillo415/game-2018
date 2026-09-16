package com.d3games.engine.item;

public enum ItemType {
	POTION("Potion", 3),
	POKEBALL("Pokeball", 5);

	private final String displayName;
	private final int startingCount;

	ItemType(String displayName, int startingCount) {
		this.displayName = displayName;
		this.startingCount = startingCount;
	}

	public String getDisplayName() {
		return displayName;
	}

	public int getStartingCount() {
		return startingCount;
	}
}
