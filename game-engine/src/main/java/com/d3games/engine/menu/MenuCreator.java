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
		GameManager.getInstance().setInventoryMenu(new InventoryMenu());
		GameManager.getInstance().setAttackMenu(new AttackMenu());
		battleMenu.add("Attack", new AttackItem());
		battleMenu.add("Bag", new InventoryItem());
		battleMenu.add("Run", new EscapeItem());
		GameManager.getInstance().setBattleMenu(battleMenu);
	}

}
