package com.d3games.engine.menu;

import com.d3games.engine.GameManager;
import com.d3games.engine.GameMessage;
import com.d3games.engine.battle.Battle;
import com.d3games.engine.battle.BattleState;
import com.d3games.engine.battle.Combatant;
import com.d3games.engine.item.ItemType;
import com.d3games.engine.sound.SoundEffect;
import com.d3games.engine.sound.SoundPlayer;

public class HealTargetItem implements MenuItem {
	private static final int POTION_HEAL_AMOUNT = 20;

	private final Combatant target;

	public HealTargetItem(Combatant target) {
		this.target = target;
	}

	@Override
	public void trigger() throws GameMessage {
		if (target.isFainted())
			throw new GameMessage(target.getName() + " has fainted and can't be healed with a Potion!");
		if (target.getHealth() >= target.getMaximumHealth())
			throw new GameMessage(target.getName() + " is already at full health!");

		GameManager.getInstance().getInventory().use(ItemType.POTION);
		int healthBefore = target.getHealth();
		target.heal(POTION_HEAL_AMOUNT);
		int healedAmount = target.getHealth() - healthBefore;

		StringBuilder resultMessage = new StringBuilder(
				"Used a Potion on " + target.getName() + "! Restored " + healedAmount + " HP.");

		Battle battle = GameManager.getInstance().getBattle();
		if (battle != null && battle.isActive()) {
			Combatant activeBeforeCounter = battle.getPlayer();
			battle.endTurn();
			battle.enemyAttack();
			if (battle.isActive())
				battle.endTurn();

			if (battle.getLastEnemyMove() != null)
				resultMessage.append(" ").append(battle.getEnemy().getName()).append(" used ")
						.append(battle.getLastEnemyMove().getName()).append("!");
			if (battle.getState() == BattleState.PLAYER_LOST) {
				resultMessage.append(" ").append(activeBeforeCounter.getName()).append(" was defeated!");
				SoundPlayer.play(SoundEffect.GAME_OVER);
			} else if (battle.isActive() && battle.getPlayer() != activeBeforeCounter) {
				resultMessage.append(" ").append(activeBeforeCounter.getName()).append(" fainted! ")
						.append(battle.getPlayer().getName()).append(" was sent out!");
				SoundPlayer.play(SoundEffect.FAINT);
			}
		}

		throw new GameMessage(resultMessage.toString());
	}
}
