package com.d3games.engine.battle;

public class Move {
	private final String name;
	private final ElementType elementType;
	private final double damageMultiplier;
	private final double recoilFraction;
	private final double lifestealFraction;
	private final double evadeChance;

	public Move(String name, ElementType elementType, double damageMultiplier,
			double recoilFraction, double lifestealFraction, double evadeChance) {
		this.name = name;
		this.elementType = elementType;
		this.damageMultiplier = damageMultiplier;
		this.recoilFraction = recoilFraction;
		this.lifestealFraction = lifestealFraction;
		this.evadeChance = evadeChance;
	}

	public String getName() {
		return name;
	}

	public ElementType getElementType() {
		return elementType;
	}

	public double getDamageMultiplier() {
		return damageMultiplier;
	}

	public double getRecoilFraction() {
		return recoilFraction;
	}

	public double getLifestealFraction() {
		return lifestealFraction;
	}

	public double getEvadeChance() {
		return evadeChance;
	}
}
