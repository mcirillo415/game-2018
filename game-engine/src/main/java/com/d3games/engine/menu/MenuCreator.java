package com.d3games.engine.menu;

import com.d3games.engine.GameManager;

public class MenuCreator {
	private static MenuCreator menuCreator;
	private Menu mainMenu;

	private MenuCreator() {
		
	}

	public static MenuCreator getInstance() {
		if (menuCreator == null)
			menuCreator = new MenuCreator();
		return menuCreator;
	}
	
	public Menu getMainMenu() {
		return mainMenu;
	}

	public void generateMenu() {
		mainMenu = new Menu();
		mainMenu.add("Player Stats", new PlayerStatsItem());
		mainMenu.add("Party", new PartyItem(mainMenu));
		mainMenu.add("Bag", new InventoryItem(mainMenu));
		Menu items = new Menu();
		items.add("Pokeball", new UselessItem("What the heck is this?"));
		items.add("Pokeflute", new UselessItem("Looks like a regular flute to me..."));
		items.add("Bicycle", new UselessItem("I don't know how to ride a bike..."));
		mainMenu.add("Items", items);
		Save save = new Save();
		mainMenu.add("Save", save);
		GameManager.getInstance().setMainMenu(mainMenu);
	}

	public void generateBattleMenu() {
		Menu battleMenu = new Menu();
		battleMenu.add("Attack", new AttackItem(battleMenu));
		battleMenu.add("Bag", new InventoryItem(battleMenu));
		battleMenu.add("Party", new PartyItem(battleMenu));
		battleMenu.add("Run", new EscapeItem());
		GameManager.getInstance().setBattleMenu(battleMenu);
	}

}
