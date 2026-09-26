package com.d3games.engine.menu;

import java.io.File;
import java.io.IOException;

import com.d3games.engine.GameManager;
import com.d3games.engine.GameMessage;
import com.d3games.engine.SaveManager;
import com.d3games.engine.battle.Battle;

public class Save implements MenuItem {

	@Override
	public void trigger() throws GameMessage {
		Battle battle = GameManager.getInstance().getBattle();
		if (battle != null && battle.isActive())
			throw new GameMessage("Can't save during a battle!");

		try {
			SaveManager.getInstance().save(new File("save.txt"));
			throw new GameMessage("Game saved!");
		} catch (IOException ex) {
			throw new GameMessage("Failed to save the game.");
		}
	}

}
