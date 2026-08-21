package com.d3games.engine.battle;

public class Combatant {
	private final String name;
	private final int maximumHealth;
	private int health;

	public Combatant(String name, int maximumHealth) {
		if (name == null || name.trim().isEmpty())
			throw new IllegalArgumentException("A combatant must have a name");
		if (maximumHealth <= 0)
			throw new IllegalArgumentException("Maximum health must be positive");

		this.name = name;
		this.maximumHealth = maximumHealth;
		health = maximumHealth;
	}

	public String getName() {
		return name;
	}

	public int getMaximumHealth() {
		return maximumHealth;
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
}
