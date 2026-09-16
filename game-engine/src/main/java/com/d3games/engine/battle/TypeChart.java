package com.d3games.engine.battle;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class TypeChart {
	private static final double SUPER_EFFECTIVE = 2.0;
	private static final double NOT_VERY_EFFECTIVE = 0.5;
	private static final double NEUTRAL = 1.0;

	private static final Map<ElementType, Set<ElementType>> STRONG_AGAINST = new EnumMap<ElementType, Set<ElementType>>(ElementType.class);
	static {
		STRONG_AGAINST.put(ElementType.FIRE, EnumSet.of(ElementType.ICE));
		STRONG_AGAINST.put(ElementType.WATER, EnumSet.of(ElementType.FIRE));
		STRONG_AGAINST.put(ElementType.ICE, EnumSet.noneOf(ElementType.class));
		STRONG_AGAINST.put(ElementType.NORMAL, EnumSet.noneOf(ElementType.class));
	}

	private TypeChart() {
	}

	public static double getMultiplier(ElementType attacker, ElementType defender) {
		if (attacker == ElementType.NORMAL || defender == ElementType.NORMAL)
			return NEUTRAL;
		if (STRONG_AGAINST.get(attacker).contains(defender))
			return SUPER_EFFECTIVE;
		if (STRONG_AGAINST.get(defender).contains(attacker))
			return NOT_VERY_EFFECTIVE;
		return NEUTRAL;
	}
}
