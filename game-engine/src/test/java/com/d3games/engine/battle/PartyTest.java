package com.d3games.engine.battle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class PartyTest {
	private Combatant starter;
	private Party party;

	@Before
	public void setup() {
		starter = new Combatant("Starter", 10, 5, 0);
		party = new Party(starter);
	}

	@Test
	public void startsWithOneActiveMember() {
		assertEquals(starter, party.getActive());
		assertEquals(1, party.getMembers().size());
		assertFalse(party.isFull());
	}

	@Test(expected = IllegalStateException.class)
	public void cannotExceedMaxSize() {
		for (int i = 0; i < Party.MAX_SIZE; i++)
			party.add(new Combatant("Member" + i, 10, 5, 0));
	}

	@Test
	public void nextAliveSkipsFaintedAndWrapsAround() {
		Combatant second = new Combatant("Second", 10, 5, 0);
		Combatant third = new Combatant("Third", 10, 5, 0);
		party.add(second);
		party.add(third);

		second.takeDamage(10);
		assertTrue(second.isFainted());

		assertEquals(third, party.nextAlive(starter));
		assertEquals(starter, party.nextAlive(third));
	}

	@Test
	public void nextAliveReturnsNullWhenAllFainted() {
		starter.takeDamage(10);
		assertNull(party.nextAlive(starter));
		assertTrue(party.allFainted());
	}

	@Test(expected = IllegalArgumentException.class)
	public void setActiveRejectsFaintedMember() {
		Combatant second = new Combatant("Second", 10, 5, 0);
		party.add(second);
		second.takeDamage(10);
		party.setActive(second);
	}

	@Test
	public void healAllRestoresEveryMember() {
		Combatant second = new Combatant("Second", 10, 5, 0);
		party.add(second);
		starter.takeDamage(5);
		second.takeDamage(9);

		party.healAll();

		assertEquals(starter.getMaximumHealth(), starter.getHealth());
		assertEquals(second.getMaximumHealth(), second.getHealth());
	}
}
