package com.d3games.engine.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class DialogueNPCTest {

	@Test
	public void promptReturnsTheConfiguredDialogueAndNeverInitiatesBattle() {
		GameMap map = new GameMap(0, 1, 1);
		Space spot = new Space();
		map.add(spot, 0, 0);
		DialogueNPC villager = new DialogueNPC(spot, "Welcome to our town!");

		assertEquals("Welcome to our town!", villager.prompt());
		assertFalse(villager.initiatesBattle());
	}
}
