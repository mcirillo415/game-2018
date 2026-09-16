package com.d3games.engine.menu;

import com.d3games.engine.GameManager;
import com.d3games.engine.item.ItemType;

public class InventoryMenu extends Menu {
	private static final long serialVersionUID = 1L;

	public InventoryMenu() {
		int potions = GameManager.getInstance().getInventory().getCount(ItemType.POTION);
		int pokeballs = GameManager.getInstance().getInventory().getCount(ItemType.POKEBALL);
		add("Potion x" + potions, new PotionItem(this));
		add("Pokeball x" + pokeballs, new CatchItem());
	}
}
