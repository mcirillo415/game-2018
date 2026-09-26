package com.d3games.engine.menu;

import java.io.File;
import java.io.IOException;

import com.d3games.engine.GameManager;
import com.d3games.engine.GameMessage;
import com.d3games.engine.SaveManager;
import com.d3games.engine.battle.Battle;

public class Load implements MenuItem {

	@Override
	public void trigger() throws GameMessage {
		Battle battle = GameManager.getInstance().getBattle();
		if (battle != null && battle.isActive())
			throw new GameMessage("Can't load during a battle!");

		File file = new File("save.txt");
		if (!file.exists())
			throw new GameMessage("No save file found!");

		try {
			SaveManager.getInstance().load(file);
			throw new GameMessage("Game loaded!");
		} catch (IOException ex) {
			throw new GameMessage("Failed to load the game.");
		}
	}

}
