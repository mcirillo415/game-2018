package com.d3games.engine.map;

import com.d3games.engine.GameManager;

public class WildGrass extends Space {
	public static final double ALWAYS_ENCOUNTER = 1.0;

	private final double encounterChance;

	public WildGrass() {
		this(ALWAYS_ENCOUNTER);
	}

	public WildGrass(double encounterChance) {
		super();
		if (encounterChance < 0 || encounterChance > 1)
			throw new IllegalArgumentException("Encounter chance must be between 0 and 1");
		this.encounterChance = encounterChance;
	}

	@Override
	public Unit approach() {
		Unit target = super.approach();
		if (Math.random() < encounterChance)
			GameManager.getInstance().triggerBattle();
		return target;
	}

	@Override
	public String toFile() {
		return "w" + map.getId() + xLoc + yLoc;
	}
}
