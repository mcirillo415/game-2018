package com.d3games.engine.menu;

import com.d3games.engine.GameManager;
import com.d3games.engine.GameMessage;
import com.d3games.engine.battle.Battle;

public class EscapeItem implements MenuItem {

	@Override
	public void trigger() throws GameMessage {
		Battle battle = GameManager.getInstance().getBattle();
		if (battle == null || !battle.isActive())
			throw new GameMessage("There is no active battle.");
		if (!battle.isEscapable())
			throw new GameMessage("You can't run from this battle!");

		battle.escape();

		throw new GameMessage("Run selected.");
	}
}
