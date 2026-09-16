package com.d3games.engine.battle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class CombatantTest {

	@Test
	public void defaultsToASingleTackleMove() {
		Combatant combatant = new Combatant("Test", 10, 5, 0);
		assertEquals(1, combatant.getMoves().size());
		assertEquals("Tackle", combatant.getMoves().get(0).getName());
		assertEquals(ElementType.NORMAL, combatant.getElementType());
	}

	@Test
	public void copyConstructorPreservesElementTypeAndMoves() {
		Combatant original = new Combatant("Ice Dog", 10, 5, 0);
		original.setElementType(ElementType.ICE);
		original.setMoves(Arrays.asList(
				new Move("Bite", ElementType.NORMAL, 1.0, 0, 0, 0),
				new Move("Frost Bite", ElementType.ICE, 1.3, 0, 0, 0)));

		Combatant copy = new Combatant(original);

		assertEquals(ElementType.ICE, copy.getElementType());
		assertEquals(2, copy.getMoves().size());
		assertEquals("Frost Bite", copy.getMoves().get(1).getName());
	}

	@Test
	public void currencyRewardScalesWithLevel() {
		Combatant levelOne = new Combatant("Test", 10, 5, 0, 1);
		Combatant levelThree = new Combatant("Test", 10, 5, 0, 3);
		assertTrue(levelThree.getCurrencyReward() > levelOne.getCurrencyReward());
	}
}
