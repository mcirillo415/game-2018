package com.d3games.engine.battle;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TypeChartTest {
	private static final double DELTA = 0.0001;

	@Test
	public void waterIsStrongAgainstFire() {
		assertEquals(2.0, TypeChart.getMultiplier(ElementType.WATER, ElementType.FIRE), DELTA);
		assertEquals(0.5, TypeChart.getMultiplier(ElementType.FIRE, ElementType.WATER), DELTA);
	}

	@Test
	public void fireIsStrongAgainstIce() {
		assertEquals(2.0, TypeChart.getMultiplier(ElementType.FIRE, ElementType.ICE), DELTA);
		assertEquals(0.5, TypeChart.getMultiplier(ElementType.ICE, ElementType.FIRE), DELTA);
	}

	@Test
	public void unrelatedTypesAreNeutral() {
		assertEquals(1.0, TypeChart.getMultiplier(ElementType.WATER, ElementType.ICE), DELTA);
		assertEquals(1.0, TypeChart.getMultiplier(ElementType.ICE, ElementType.WATER), DELTA);
	}

	@Test
	public void normalIsAlwaysNeutral() {
		for (ElementType type : ElementType.values()) {
			assertEquals(1.0, TypeChart.getMultiplier(ElementType.NORMAL, type), DELTA);
			assertEquals(1.0, TypeChart.getMultiplier(type, ElementType.NORMAL), DELTA);
		}
	}
}
