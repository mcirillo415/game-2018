package com.d3games.engine.menu;

import com.d3games.engine.GameManager;
import com.d3games.engine.GameMessage;
import com.d3games.engine.item.ItemType;

public class ReviveItem implements MenuItem {
	private final Menu containingMenu;

	public ReviveItem(Menu containingMenu) {
		this.containingMenu = containingMenu;
	}

	@Override
	public void trigger() throws Menu, GameMessage {
		if (GameManager.getInstance().getInventory().getCount(ItemType.REVIVE) <= 0)
			throw new GameMessage("You're out of Revives!");

		ReviveMenu reviveMenu = new ReviveMenu();
		reviveMenu.setParentMenu(containingMenu);
		throw reviveMenu;
	}
}
