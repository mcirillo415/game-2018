package com.d3games.engine.menu;

public class InventoryMenu extends Menu {
	private static final long serialVersionUID = 1L;

	public InventoryMenu() {
		add("Potion", new UselessItem("There are no usable potions yet."));
		add("Pokeball", new UselessItem("There are no usable Pokeballs yet."));
	}
}
