package com.d3games.gameui;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.d3games.engine.battle.Combatant;
import com.d3games.engine.battle.ElementType;
import com.d3games.engine.battle.Move;

public enum EnemyType {
	ANGRY_DOG("Angry Dog", 80, 10, 0, 1, "characters/enemies/angryDog.png", ElementType.FIRE, Arrays.asList(
			new Move("Bite", ElementType.NORMAL, 1.0, 0, 0, 0),
			new Move("Flame Snap", ElementType.FIRE, 1.3, 0, 0, 0))),
	ICE_DOG("Ice Dog", 70, 8, 4, 2, "characters/enemies/iceDog.png", ElementType.ICE, Arrays.asList(
			new Move("Bite", ElementType.NORMAL, 1.0, 0, 0, 0),
			new Move("Frost Bite", ElementType.ICE, 1.3, 0, 0, 0))),
	WILD_DOG("Wild Dog", 90, 14, 1, 3, "characters/enemies/wildDog.png", ElementType.NORMAL, Arrays.asList(
			new Move("Bite", ElementType.NORMAL, 1.0, 0, 0, 0),
			new Move("Savage Bite", ElementType.NORMAL, 1.4, 0.1, 0, 0))),
	WATER_DOG("Water Dog", 75, 9, 2, 2, "characters/enemies/waterDog.png", ElementType.WATER, Arrays.asList(
			new Move("Bite", ElementType.NORMAL, 1.0, 0, 0, 0),
			new Move("Water Snap", ElementType.WATER, 1.3, 0, 0, 0)));

	private static final Random RANDOM = new Random();

	private final String displayName;
	private final int maximumHealth;
	private final int attackPower;
	private final int defense;
	private final int level;
	private final String imagePath;
	private final ElementType elementType;
	private final List<Move> moves;

	EnemyType(String displayName, int maximumHealth, int attackPower, int defense, int level, String imagePath,
			ElementType elementType, List<Move> moves) {
		this.displayName = displayName;
		this.maximumHealth = maximumHealth;
		this.attackPower = attackPower;
		this.defense = defense;
		this.level = level;
		this.imagePath = imagePath;
		this.elementType = elementType;
		this.moves = moves;
	}

	public Combatant newCombatant() {
		Combatant combatant = new Combatant(displayName, maximumHealth, attackPower, defense, level);
		combatant.setElementType(elementType);
		combatant.setMoves(moves);
		return combatant;
	}

	public String getImagePath() {
		return imagePath;
	}

	public static EnemyType random() {
		EnemyType[] types = values();
		return types[RANDOM.nextInt(types.length)];
	}

	public static EnemyType fromDisplayName(String name) {
		for (EnemyType type : values())
			if (type.displayName.equals(name))
				return type;
		return null;
	}
}
