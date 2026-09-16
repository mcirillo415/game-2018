package com.d3games.engine.menu;

import com.d3games.engine.GameManager;
import com.d3games.engine.GameMessage;
import com.d3games.engine.battle.Battle;

public class AttackItem implements MenuItem {
	private final Menu containingMenu;

	public AttackItem(Menu containingMenu) {
		this.containingMenu = containingMenu;
	}

	@Override
	public void trigger() throws Menu, GameMessage {
		Battle battle = GameManager.getInstance().getBattle();
		if (battle == null || !battle.isActive())
			throw new GameMessage("There is no active battle.");

		AttackMenu attackMenu = new AttackMenu(battle.getPlayer().getMoves());
		attackMenu.setParentMenu(containingMenu);
		throw attackMenu;
	}
}
