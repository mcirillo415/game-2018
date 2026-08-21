package com.d3games.engine.menu;

import com.d3games.engine.GameManager;
import com.d3games.engine.GameMessage;
import com.d3games.engine.battle.Battle;

public class InventoryItem implements MenuItem {

	@Override
	public void trigger() throws Menu, GameMessage {
		Battle battle = GameManager.getInstance().getBattle();
		if (battle == null || !battle.isActive())
			throw new GameMessage("There is no active battle.");

		throw GameManager.getInstance().getInventoryMenu();
	}
}
