package com.d3games.engine.menu;

import com.d3games.engine.item.ItemType;

public class ShopMenu extends Menu {
	private static final long serialVersionUID = 1L;

	private static final int POTION_PRICE = 10;
	private static final int POKEBALL_PRICE = 15;

	public ShopMenu() {
		add("Potion - " + POTION_PRICE + " Bones", new BuyItemItem(ItemType.POTION, POTION_PRICE));
		add("Pokeball - " + POKEBALL_PRICE + " Bones", new BuyItemItem(ItemType.POKEBALL, POKEBALL_PRICE));
	}
}
