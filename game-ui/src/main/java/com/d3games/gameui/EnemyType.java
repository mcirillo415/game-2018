package com.d3games.gameui;

import java.util.Random;

import com.d3games.engine.battle.Combatant;

public enum EnemyType {
	ANGRY_DOG("Angry Dog", 80, 10, 0, 1, "angryDog.png"),
	ICE_DOG("Ice Dog", 70, 8, 4, 2, "iceDog.png"),
	WILD_DOG("Wild Dog", 90, 14, 1, 3, "wildDog.png");

	private static final Random RANDOM = new Random();

	private final String displayName;
	private final int maximumHealth;
	private final int attackPower;
	private final int defense;
	private final int level;
	private final String imagePath;

	EnemyType(String displayName, int maximumHealth, int attackPower, int defense, int level, String imagePath) {
		this.displayName = displayName;
		this.maximumHealth = maximumHealth;
		this.attackPower = attackPower;
		this.defense = defense;
		this.level = level;
		this.imagePath = imagePath;
	}

	public Combatant newCombatant() {
		return new Combatant(displayName, maximumHealth, attackPower, defense, level);
	}

	public String getImagePath() {
		return imagePath;
	}

	public static EnemyType random() {
		EnemyType[] types = values();
		return types[RANDOM.nextInt(types.length)];
	}
}
