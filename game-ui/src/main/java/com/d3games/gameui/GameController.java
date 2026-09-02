package com.d3games.gameui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import com.d3games.engine.GameManager;
import com.d3games.engine.GameMessage;
import com.d3games.engine.InvalidMoveException;
import com.d3games.engine.battle.Battle;
import com.d3games.engine.battle.BattleState;
import com.d3games.engine.battle.Combatant;
import com.d3games.engine.map.Player;
import com.d3games.engine.menu.AttackMenu;
import com.d3games.engine.menu.InventoryMenu;
import com.d3games.engine.menu.Menu;

public class GameController extends KeyAdapter {
	private final Runnable onChange;
	private final KeyBindings keyBindings = KeyBindings.getInstance();

	private Menu activeMenu;
	private Battle battle;
	private EnemyType enemyType = EnemyType.ANGRY_DOG;
	private Player player;
	private String message;
	private boolean returnToBattle;
	private GameMode gameMode = GameMode.WORLD_MAP;

	public GameController(Runnable onChange) {
		this.onChange = onChange;
		GameManager gameManager = GameManager.getInstance();
		player = gameManager.getPlayer();
		battle = gameManager.getBattle();
		activeMenu = gameManager.getActiveMenu();
	}

	public Menu getActiveMenu() {
		return activeMenu;
	}

	public Battle getBattle() {
		return battle;
	}

	public EnemyType getEnemyType() {
		return enemyType;
	}

	public Player getPlayer() {
		return player;
	}

	public String getMessage() {
		return message;
	}

	public GameMode getGameMode() {
		return gameMode;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		message = null;
		GameAction action = keyBindings.resolve(e.getKeyCode());
		try {
			if (gameMode == GameMode.WORLD_MAP) {
				if (action == GameAction.MOVE_LEFT)
					player.moveLeft();
				else if (action == GameAction.MOVE_RIGHT)
					player.moveRight();
				else if (action == GameAction.MOVE_UP)
					player.moveUp();
				else if (action == GameAction.MOVE_DOWN)
					player.moveDown();
				else if (action == GameAction.CONFIRM)
					message = player.engage();
				else if (action == GameAction.MENU) {
					setActiveMenu(GameManager.getInstance().getMainMenu());
					gameMode = GameMode.MENU;
				}
				else if (action == GameAction.BATTLE)
					startBattle();

				if (gameMode == GameMode.WORLD_MAP && GameManager.getInstance().isBattlePending()) {
					GameManager.getInstance().clearPendingBattle();
					startBattle();
				}
			}
			else if (gameMode == GameMode.MENU || gameMode == GameMode.BATTLE) {
				if (action == GameAction.MENU) {
					if (returnToBattle) {
						setActiveMenu(GameManager.getInstance().getBattleMenu());
						gameMode = GameMode.BATTLE;
						returnToBattle = false;
					}
					else {
						setActiveMenu(GameManager.getInstance().getMainMenu());
						gameMode = GameMode.WORLD_MAP;
					}
				}
				else if (action == GameAction.BATTLE) {
					setActiveMenu(GameManager.getInstance().getBattleMenu());
					gameMode = GameMode.BATTLE;
				}
				else if (action == GameAction.MOVE_UP)
					activeMenu.up();
				else if (action == GameAction.MOVE_DOWN)
					activeMenu.down();
				else if (action == GameAction.MOVE_RIGHT)
					activeMenu.accessSelected();
				else if (action == GameAction.CONFIRM)
					activeMenu.triggerSelected();
				else if (action == GameAction.MOVE_LEFT) {
					if (returnToBattle) {
						setActiveMenu(GameManager.getInstance().getBattleMenu());
						gameMode = GameMode.BATTLE;
						returnToBattle = false;
					}
					else
						setActiveMenu(activeMenu.back());
				}
			}
			else if (gameMode == GameMode.GAME_OVER) {
				if (action == GameAction.CONFIRM) {
					Combatant playerCombatant = GameManager.getInstance().getPlayerCombatant();
					playerCombatant.heal(playerCombatant.getMaximumHealth());
					setActiveMenu(GameManager.getInstance().getMainMenu());
					gameMode = GameMode.WORLD_MAP;
				}
			}
		} catch (InvalidMoveException ex) {
			System.out.println(ex.getError());
		} catch (Menu newMenu) {
			setActiveMenu(newMenu);
			if (newMenu instanceof InventoryMenu) {
				gameMode = GameMode.MENU;
				returnToBattle = true;
			} else if (newMenu instanceof AttackMenu) {
				returnToBattle = true;
			}
		} catch (GameMessage gameMessage) {
			message = gameMessage.getMessage();
			returnToWorldIfBattleEnded();
		}
		onChange.run();
	}

	private void startBattle() {
		enemyType = EnemyType.random();
		battle = new Battle(
			GameManager.getInstance().getPlayerCombatant(),
			enemyType.newCombatant()
		);
		GameManager.getInstance().setBattle(battle);
		setActiveMenu(GameManager.getInstance().getBattleMenu());
		gameMode = GameMode.BATTLE;
	}

	private void returnToWorldIfBattleEnded() {
		if (battle == null || battle.isActive())
			return;
		if (battle.getState() == BattleState.PLAYER_LOST) {
			message = battle.getPlayer().getName() + " was defeated by " + battle.getEnemy().getName() + "!";
			gameMode = GameMode.GAME_OVER;
		} else {
			setActiveMenu(GameManager.getInstance().getMainMenu());
			gameMode = GameMode.WORLD_MAP;
		}
	}

	private void setActiveMenu(Menu menu) {
		activeMenu = menu;
		GameManager.getInstance().setActiveMenu(menu);
	}
}
