package com.d3games.engine;

import java.util.ArrayList;
import java.util.List;

import java.util.Arrays;

import com.d3games.engine.battle.Battle;
import com.d3games.engine.battle.Combatant;
import com.d3games.engine.battle.ElementType;
import com.d3games.engine.battle.Move;
import com.d3games.engine.battle.Party;
import com.d3games.engine.item.Inventory;
import com.d3games.engine.item.Wallet;
import com.d3games.engine.map.GameMap;
import com.d3games.engine.map.Player;
import com.d3games.engine.menu.Menu;

public class GameManager {
	private static final int STARTING_BONES = 20;

	private static GameManager manager;
	private List<GameMap> maps = new ArrayList<GameMap>();
	private Player player;
	private Party party;
	private Inventory inventory;
	private Wallet wallet;
	private Battle battle;
	private Menu mainMenu;
	private Menu battleMenu;
	private Menu activeMenu;
	private boolean battlePending;
	private boolean pendingBattleEscapable = true;
	private boolean shopPending;

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

	public Combatant getPlayerCombatant() {
		return getParty().getActive();
	}

	public Party getParty() {
		if (party == null) {
			Combatant starter = new Combatant("Player", 100, 20, 5);
			starter.setMoves(Arrays.asList(
					new Move("Tackle", ElementType.NORMAL, 1.0, 0, 0, 0),
					new Move("Power Bite", ElementType.NORMAL, 1.5, 0.15, 0, 0),
					new Move("Quick Nip", ElementType.NORMAL, 0.6, 0, 0, 0.4),
					new Move("Leech Bite", ElementType.NORMAL, 0.7, 0, 0.5, 0)));
			party = new Party(starter);
		}
		return party;
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

	public Inventory getInventory() {
		if (inventory == null)
			inventory = new Inventory();
		return inventory;
	}

	public Wallet getWallet() {
		if (wallet == null) {
			wallet = new Wallet();
			wallet.add(STARTING_BONES);
		}
		return wallet;
	}

	public Menu getActiveMenu() {
		return activeMenu;
	}
	
	public void setActiveMenu(Menu activeMenu) {
		this.activeMenu = activeMenu;
	}

	public void triggerBattle() {
		triggerBattle(true);
	}

	public void triggerBattle(boolean escapable) {
		battlePending = true;
		pendingBattleEscapable = escapable;
	}

	public boolean isBattlePending() {
		return battlePending;
	}

	public boolean isPendingBattleEscapable() {
		return pendingBattleEscapable;
	}

	public void clearPendingBattle() {
		battlePending = false;
	}

	public void triggerShop() {
		shopPending = true;
	}

	public boolean isShopPending() {
		return shopPending;
	}

	public void clearPendingShop() {
		shopPending = false;
	}
}
