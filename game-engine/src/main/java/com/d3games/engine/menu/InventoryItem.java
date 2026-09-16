package com.d3games.engine.menu;

public class InventoryItem implements MenuItem {
	private final Menu containingMenu;

	public InventoryItem(Menu containingMenu) {
		this.containingMenu = containingMenu;
	}

	@Override
	public void trigger() throws Menu {
		InventoryMenu inventoryMenu = new InventoryMenu();
		inventoryMenu.setParentMenu(containingMenu);
		throw inventoryMenu;
	}
}
