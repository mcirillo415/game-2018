package com.d3games.engine.menu;

import com.d3games.engine.GameManager;
import com.d3games.engine.GameMessage;
import com.d3games.engine.battle.Battle;
import com.d3games.engine.battle.BattleState;
import com.d3games.engine.battle.Combatant;
import com.d3games.engine.battle.Party;
import com.d3games.engine.item.ItemType;
import com.d3games.engine.sound.SoundEffect;
import com.d3games.engine.sound.SoundPlayer;

public class CatchItem implements MenuItem {
	private static final double MINIMUM_CATCH_CHANCE = 0.1;
	private static final double MAXIMUM_CATCH_CHANCE = 0.9;

	@Override
	public void trigger() throws GameMessage {
		Battle battle = GameManager.getInstance().getBattle();
		if (battle == null || !battle.isActive())
			throw new GameMessage("There is no active battle.");
		if (!battle.isEscapable())
			throw new GameMessage("You can't catch another trainer's creature!");
		if (GameManager.getInstance().getInventory().getCount(ItemType.POKEBALL) <= 0)
			throw new GameMessage("You're out of Pokeballs!");

		Party party = GameManager.getInstance().getParty();
		if (party.isFull())
			throw new GameMessage("Your party is full! Make room before catching more creatures.");

		GameManager.getInstance().getInventory().use(ItemType.POKEBALL);
		Combatant enemy = battle.getEnemy();
		double hpFraction = enemy.getHealth() / (double) enemy.getMaximumHealth();
		double catchChance = Math.min(MAXIMUM_CATCH_CHANCE, Math.max(MINIMUM_CATCH_CHANCE, 1.0 - hpFraction));

		if (Math.random() < catchChance) {
			battle.catchEnemy();
			party.add(new Combatant(enemy));
			SoundPlayer.play(SoundEffect.CATCH_SUCCESS);
			throw new GameMessage("Gotcha! " + enemy.getName() + " was caught!");
		}
		SoundPlayer.play(SoundEffect.CATCH_FAIL);

		Combatant playerBeforeCounter = battle.getPlayer();
		battle.endTurn();
		battle.enemyAttack();
		if (battle.isActive())
			battle.endTurn();

		StringBuilder resultMessage = new StringBuilder(
				"The Pokeball missed! " + enemy.getName() + " broke free!");
		resultMessage.append(battle.describeLastEnemyAction());
		if (battle.getState() == BattleState.PLAYER_LOST) {
			resultMessage.append(" ").append(playerBeforeCounter.getName()).append(" was defeated!");
			SoundPlayer.play(SoundEffect.GAME_OVER);
		} else if (battle.isActive() && battle.getPlayer() != playerBeforeCounter) {
			resultMessage.append(" ").append(playerBeforeCounter.getName()).append(" fainted! ")
					.append(battle.getPlayer().getName()).append(" was sent out!");
			SoundPlayer.play(SoundEffect.FAINT);
		}
		throw new GameMessage(resultMessage.toString());
	}
}
