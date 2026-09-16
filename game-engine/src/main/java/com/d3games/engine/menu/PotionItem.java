package com.d3games.engine.menu;

import com.d3games.engine.GameManager;
import com.d3games.engine.GameMessage;
import com.d3games.engine.item.ItemType;

public class PotionItem implements MenuItem {
	private final Menu containingMenu;

	public PotionItem(Menu containingMenu) {
		this.containingMenu = containingMenu;
	}

	@Override
	public void trigger() throws Menu, GameMessage {
		if (GameManager.getInstance().getInventory().getCount(ItemType.POTION) <= 0)
			throw new GameMessage("You're out of Potions!");

		HealMenu healMenu = new HealMenu();
		healMenu.setParentMenu(containingMenu);
		throw healMenu;
	}
}
