package com.d3games.engine;

import java.util.ArrayList;
import java.util.List;

import com.d3games.engine.battle.Battle;
import com.d3games.engine.map.GameMap;
import com.d3games.engine.map.Player;
import com.d3games.engine.menu.Menu;

public class GameManager {
	private static GameManager manager;
	private List<GameMap> maps = new ArrayList<GameMap>();
	private Player player;
	private Battle battle;
	private Menu mainMenu;
	private Menu battleMenu;
	private Menu inventoryMenu;
	private Menu activeMenu;

	private GameManager() {
		
	}

	public static GameManager getInstance() {
		if (manager == null)
			manager = new GameManager();
		return manager;
	}

	public void add(GameMap map) {
		maps.add(map);
	}

	public GameMap get(int i) {
		return maps.get(i);
	}

	public List<GameMap> getAll() {
		return maps;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Battle getBattle() {
		return battle;
	}

	public void setBattle(Battle battle) {
		this.battle = battle;
	}

	public Menu getMainMenu() {
		return mainMenu;
	}

	public void setMainMenu(Menu mainMenu) {
		this.mainMenu = mainMenu;
	}

	public Menu getBattleMenu() {
		return battleMenu;
	}

	public void setBattleMenu(Menu battleMenu) {
		this.battleMenu = battleMenu;
	}	

	public Menu getInventoryMenu() {
		return inventoryMenu;
	}

	public void setInventoryMenu(Menu inventoryMenu) {
		this.inventoryMenu = inventoryMenu;
	}

	public Menu getActiveMenu() {
		return activeMenu;
	}
	
	public void setActiveMenu(Menu activeMenu) {
		this.activeMenu = activeMenu;
	}
}
