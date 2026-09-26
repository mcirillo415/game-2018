package com.d3games.engine.battle;

public class Move {
	private final String name;
	private final ElementType elementType;
	private final double damageMultiplier;
	private final double recoilFraction;
	private final double lifestealFraction;
	private final double evadeChance;
	private final StatusEffect inflictedStatus;
	private final double statusChance;

	public Move(String name, ElementType elementType, double damageMultiplier,
			double recoilFraction, double lifestealFraction, double evadeChance) {
		this(name, elementType, damageMultiplier, recoilFraction, lifestealFraction, evadeChance,
				StatusEffect.NONE, 0);
	}

	public Move(String name, ElementType elementType, double damageMultiplier,
			double recoilFraction, double lifestealFraction, double evadeChance,
			StatusEffect inflictedStatus, double statusChance) {
		this.name = name;
		this.elementType = elementType;
		this.damageMultiplier = damageMultiplier;
		this.recoilFraction = recoilFraction;
		this.lifestealFraction = lifestealFraction;
		this.evadeChance = evadeChance;
		this.inflictedStatus = inflictedStatus;
		this.statusChance = statusChance;
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

	public StatusEffect getInflictedStatus() {
		return inflictedStatus;
	}

	public double getStatusChance() {
		return statusChance;
	}
}
