package com.d3games.engine.battle;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class RandomMoveSelectorTest {

	@Test
	public void selectsOneOfTheCombatantsOwnMoves() {
		Move bite = new Move("Bite", ElementType.NORMAL, 1.0, 0, 0, 0);
		Move frostBite = new Move("Frost Bite", ElementType.ICE, 1.3, 0, 0, 0);
		Combatant combatant = new Combatant("Ice Dog", 10, 5, 0);
		combatant.setMoves(Arrays.asList(bite, frostBite));

		RandomMoveSelector selector = new RandomMoveSelector();
		for (int i = 0; i < 20; i++) {
			Move selected = selector.selectMove(combatant, combatant);
			assertTrue(selected == bite || selected == frostBite);
		}
	}
}
