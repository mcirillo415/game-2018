package com.d3games.gameui;

import java.util.Arrays;
import java.util.List;

import com.d3games.engine.battle.Combatant;
import com.d3games.engine.battle.ElementType;
import com.d3games.engine.battle.Move;
import com.d3games.engine.battle.StatusEffect;

/**
 * The eight trainer battles guarding corridor_3 through corridor_10, one per room id, in
 * escalating difficulty order. Unlike wild EnemyType encounters, these are fixed rather than
 * random, so the corridor gauntlet always plays out the same way from start to finish.
 */
public enum TrainerType {
	PEBBLE(3, "Pebble", 60, 9, 1, 2, ElementType.NORMAL, EnemyType.WILD_DOG, Arrays.asList(
			new Move("Nip", ElementType.NORMAL, 1.0, 0, 0, 0),
			new Move("Pounce", ElementType.NORMAL, 1.3, 0, 0, 0.15))),
	EMBER(4, "Ember", 75, 12, 2, 4, ElementType.FIRE, EnemyType.ANGRY_DOG, Arrays.asList(
			new Move("Bite", ElementType.NORMAL, 1.0, 0, 0, 0),
			new Move("Cinder Snap", ElementType.FIRE, 1.3, 0, 0, 0, StatusEffect.BURN, 0.25))),
	FROST(5, "Frost", 90, 14, 3, 6, ElementType.ICE, EnemyType.ICE_DOG, Arrays.asList(
			new Move("Bite", ElementType.NORMAL, 1.0, 0, 0, 0),
			new Move("Glacial Bite", ElementType.ICE, 1.4, 0, 0, 0))),
	TORRENT(6, "Torrent", 105, 16, 4, 8, ElementType.WATER, EnemyType.WATER_DOG, Arrays.asList(
			new Move("Bite", ElementType.NORMAL, 1.0, 0, 0, 0),
			new Move("Riptide", ElementType.WATER, 1.4, 0, 0.1, 0))),
	FANG(7, "Fang", 120, 19, 5, 10, ElementType.NORMAL, EnemyType.WILD_DOG, Arrays.asList(
			new Move("Savage Bite", ElementType.NORMAL, 1.3, 0, 0, 0, StatusEffect.POISON, 0.3),
			new Move("Rampage", ElementType.NORMAL, 1.6, 0.1, 0, 0))),
	BLAZE(8, "Blaze", 135, 22, 6, 12, ElementType.FIRE, EnemyType.ANGRY_DOG, Arrays.asList(
			new Move("Flame Bite", ElementType.FIRE, 1.4, 0, 0, 0, StatusEffect.BURN, 0.35),
			new Move("Inferno", ElementType.FIRE, 1.8, 0.15, 0, 0))),
	GLACIER(9, "Glacier", 150, 25, 7, 14, ElementType.ICE, EnemyType.ICE_DOG, Arrays.asList(
			new Move("Ice Fang", ElementType.ICE, 1.4, 0, 0, 0),
			new Move("Blizzard Bite", ElementType.ICE, 1.8, 0, 0, 0.2))),
	CHAMPION(10, "Champion", 180, 30, 9, 16, ElementType.NORMAL, EnemyType.WILD_DOG, Arrays.asList(
			new Move("Crushing Bite", ElementType.NORMAL, 1.5, 0, 0, 0),
			new Move("Final Howl", ElementType.NORMAL, 2.0, 0.1, 0.2, 0)));

	private static final int FIRST_ROOM_ID = 3;

	private final int roomId;
	private final String displayName;
	private final int maximumHealth;
	private final int attackPower;
	private final int defense;
	private final int level;
	private final ElementType elementType;
	private final EnemyType spriteType;
	private final List<Move> moves;

	TrainerType(int roomId, String displayName, int maximumHealth, int attackPower, int defense, int level,
			ElementType elementType, EnemyType spriteType, List<Move> moves) {
		this.roomId = roomId;
		this.displayName = displayName;
		this.maximumHealth = maximumHealth;
		this.attackPower = attackPower;
		this.defense = defense;
		this.level = level;
		this.elementType = elementType;
		this.spriteType = spriteType;
		this.moves = moves;
	}

	public Combatant newCombatant() {
		Combatant combatant = new Combatant(displayName, maximumHealth, attackPower, defense, level);
		combatant.setElementType(elementType);
		combatant.setMoves(moves);
		return combatant;
	}

	public int getRoomId() {
		return roomId;
	}

	public EnemyType getSpriteType() {
		return spriteType;
	}

	/** Returns the trainer guarding the corridor room with this id, or null if it isn't a trainer room. */
	public static TrainerType forRoomId(int roomId) {
		int index = roomId - FIRST_ROOM_ID;
		TrainerType[] values = values();
		if (index < 0 || index >= values.length)
			return null;
		return values[index];
	}

	public static boolean isFinalTrainerRoom(int roomId) {
		return forRoomId(roomId) == CHAMPION;
	}
}
