package com.d3games.engine.map;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.d3games.engine.GameManager;
import com.d3games.engine.battle.Combatant;
import com.d3games.engine.battle.Party;

public class HealerNPCTest {

	@Test
	public void promptHealsAndRevivesTheWholeParty() {
		GameManager gameManager = GameManager.getInstance();

		Combatant damaged = new Combatant("Damaged", 30, 5, 0);
		damaged.setHealth(10);
		Party party = new Party(damaged);
		Combatant fainted = new Combatant("Fainted", 20, 5, 0);
		fainted.setHealth(0);
		party.add(fainted);
		gameManager.setParty(party);

		GameMap map = new GameMap(gameManager.getAll().size(), 1, 1);
		gameManager.add(map);
		Space spot = new Space();
		map.add(spot, 0, 0);
		HealerNPC healer = new HealerNPC(spot);

		String message = healer.prompt();

		assertEquals(30, damaged.getHealth());
		assertEquals(20, fainted.getHealth());
		assertEquals("Your party has been fully healed!", message);
	}
}
