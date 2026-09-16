package com.d3games.engine.menu;

public class PartyItem implements MenuItem {
	private final Menu containingMenu;

	public PartyItem(Menu containingMenu) {
		this.containingMenu = containingMenu;
	}

	@Override
	public void trigger() throws Menu {
		PartyMenu partyMenu = new PartyMenu();
		partyMenu.setParentMenu(containingMenu);
		throw partyMenu;
	}
}
