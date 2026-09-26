package com.d3games.engine.battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Combatant {
	private static final int BASE_EXPERIENCE_TO_LEVEL = 100;
	private static final int EXPERIENCE_TO_LEVEL_INCREMENT = 25;
	private static final int BASE_EXPERIENCE_REWARD = 20;
	private static final int EXPERIENCE_REWARD_PER_LEVEL = 15;
	private static final int BASE_CURRENCY_REWARD = 15;
	private static final int CURRENCY_REWARD_PER_LEVEL = 5;
	private static final int LEVEL_UP_HEALTH_GAIN = 10;
	private static final int LEVEL_UP_ATTACK_GAIN = 3;
	private static final int LEVEL_UP_DEFENSE_GAIN = 1;
	private static final int POISON_DAMAGE_DIVISOR = 8;
	private static final int BURN_DAMAGE_DIVISOR = 16;

	private static final Move DEFAULT_MOVE = new Move("Tackle", ElementType.NORMAL, 1.0, 0, 0, 0);

	private final String name;
	private int maximumHealth;
	private int attackPower;
	private int defense;
	private int health;
	private int level;
	private int experience;
	private int experienceToNextLevel;
	private ElementType elementType = ElementType.NORMAL;
	private List<Move> moves = new ArrayList<Move>(Collections.singletonList(DEFAULT_MOVE));
	private StatusEffect status = StatusEffect.NONE;

	public Combatant(String name, int maximumHealth) {
		this(name, maximumHealth, 20, 0);
	}

	public Combatant(String name, int maximumHealth, int attackPower, int defense) {
		this(name, maximumHealth, attackPower, defense, 1);
	}

	public Combatant(String name, int maximumHealth, int attackPower, int defense, int level) {
		if (name == null || name.trim().isEmpty())
			throw new IllegalArgumentException("A combatant must have a name");
		if (maximumHealth <= 0)
			throw new IllegalArgumentException("Maximum health must be positive");
		if (attackPower < 0)
			throw new IllegalArgumentException("Attack power cannot be negative");
		if (defense < 0)
			throw new IllegalArgumentException("Defense cannot be negative");
		if (level < 1)
			throw new IllegalArgumentException("Level must be at least 1");

		this.name = name;
		this.maximumHealth = maximumHealth;
		this.attackPower = attackPower;
		this.defense = defense;
		this.level = level;
		this.health = maximumHealth;
		this.experienceToNextLevel = experienceToLevel(level);
	}

	public Combatant(Combatant other) {
		this.name = other.name;
		this.maximumHealth = other.maximumHealth;
		this.attackPower = other.attackPower;
		this.defense = other.defense;
		this.level = other.level;
		this.health = other.health;
		this.experience = other.experience;
		this.experienceToNextLevel = other.experienceToNextLevel;
		this.elementType = other.elementType;
		this.moves = new ArrayList<Move>(other.moves);
		this.status = other.status;
	}

	public String getName() {
		return name;
	}

	public int getMaximumHealth() {
		return maximumHealth;
	}

	public int getAttackPower() {
		return attackPower;
	}

	public int getDefense() {
		return defense;
	}

	public int getHealth() {
		return health;
	}

	public int getLevel() {
		return level;
	}

	public int getExperience() {
		return experience;
	}

	public int getExperienceToNextLevel() {
		return experienceToNextLevel;
	}

	public int getExperienceReward() {
		return BASE_EXPERIENCE_REWARD + (level - 1) * EXPERIENCE_REWARD_PER_LEVEL;
	}

	public int getCurrencyReward() {
		return BASE_CURRENCY_REWARD + (level - 1) * CURRENCY_REWARD_PER_LEVEL;
	}

	public ElementType getElementType() {
		return elementType;
	}

	public void setElementType(ElementType elementType) {
		this.elementType = elementType;
	}

	public List<Move> getMoves() {
		return Collections.unmodifiableList(moves);
	}

	public void setMoves(List<Move> moves) {
		this.moves = new ArrayList<Move>(moves);
	}

	public StatusEffect getStatus() {
		return status;
	}

	public void setStatus(StatusEffect status) {
		this.status = status;
	}

	public int applyStatusDamage() {
		int damage;
		if (status == StatusEffect.POISON)
			damage = Math.max(1, maximumHealth / POISON_DAMAGE_DIVISOR);
		else if (status == StatusEffect.BURN)
			damage = Math.max(1, maximumHealth / BURN_DAMAGE_DIVISOR);
		else
			return 0;
		takeDamage(damage);
		return damage;
	}

	public boolean isFainted() {
		return health == 0;
	}

	public void takeDamage(int damage) {
		if (damage < 0)
			throw new IllegalArgumentException("Damage cannot be negative");
		health = Math.max(0, health - damage);
		if (health == 0)
			status = StatusEffect.NONE;
	}

	public void heal(int amount) {
		if (amount < 0)
			throw new IllegalArgumentException("Healing cannot be negative");
		health = Math.min(maximumHealth, health + amount);
	}

	public void setHealth(int health) {
		if (health < 0 || health > maximumHealth)
			throw new IllegalArgumentException("Health must be between 0 and maximum health");
		this.health = health;
	}

	public void setExperience(int experience) {
		if (experience < 0)
			throw new IllegalArgumentException("Experience cannot be negative");
		this.experience = experience;
	}

	public int gainExperience(int amount) {
		if (amount < 0)
			throw new IllegalArgumentException("Experience cannot be negative");
		experience += amount;
		int levelsGained = 0;
		while (experience >= experienceToNextLevel) {
			experience -= experienceToNextLevel;
			levelUp();
			levelsGained++;
		}
		return levelsGained;
	}

	private void levelUp() {
		level++;
		maximumHealth += LEVEL_UP_HEALTH_GAIN;
		attackPower += LEVEL_UP_ATTACK_GAIN;
		defense += LEVEL_UP_DEFENSE_GAIN;
		health = maximumHealth;
		experienceToNextLevel = experienceToLevel(level);
	}

	private static int experienceToLevel(int level) {
		return BASE_EXPERIENCE_TO_LEVEL + (level - 1) * EXPERIENCE_TO_LEVEL_INCREMENT;
	}
}
