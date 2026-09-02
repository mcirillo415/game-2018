package com.d3games.engine.battle;

public class Combatant {
	private final String name;
	private final int maximumHealth;
	private final int attackPower;
	private final int defense;
	private int health;

	public Combatant(String name, int maximumHealth) {
		this(name, maximumHealth, 20, 0);
	}

	public Combatant(String name, int maximumHealth, int attackPower, int defense) {
		if (name == null || name.trim().isEmpty())
			throw new IllegalArgumentException("A combatant must have a name");
		if (maximumHealth <= 0)
			throw new IllegalArgumentException("Maximum health must be positive");
		if (attackPower < 0)
			throw new IllegalArgumentException("Attack power cannot be negative");
		if (defense < 0)
			throw new IllegalArgumentException("Defense cannot be negative");

		this.name = name;
		this.maximumHealth = maximumHealth;
		this.attackPower = attackPower;
		this.defense = defense;
		health = maximumHealth;
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

	public boolean isFainted() {
		return health == 0;
	}

	public void takeDamage(int damage) {
		if (damage < 0)
			throw new IllegalArgumentException("Damage cannot be negative");
		health = Math.max(0, health - damage);
	}

	public void heal(int amount) {
		if (amount < 0)
			throw new IllegalArgumentException("Healing cannot be negative");
		health = Math.min(maximumHealth, health + amount);
	}
}
