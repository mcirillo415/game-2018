package com.d3games.engine.menu;

import com.d3games.engine.GameManager;
import com.d3games.engine.GameMessage;
import com.d3games.engine.battle.Battle;
import com.d3games.engine.battle.BattleState;
import com.d3games.engine.battle.Combatant;
import com.d3games.engine.item.ItemType;
import com.d3games.engine.sound.SoundEffect;
import com.d3games.engine.sound.SoundPlayer;

public class ReviveTargetItem implements MenuItem {
	private final Combatant target;

	public ReviveTargetItem(Combatant target) {
		this.target = target;
	}

	@Override
	public void trigger() throws GameMessage {
		if (!target.isFainted())
			throw new GameMessage(target.getName() + " hasn't fainted!");

		GameManager.getInstance().getInventory().use(ItemType.REVIVE);
		int reviveAmount = (target.getMaximumHealth() + 1) / 2;
		target.heal(reviveAmount);

		StringBuilder resultMessage = new StringBuilder(
				target.getName() + " was revived with " + reviveAmount + " HP!");

		Battle battle = GameManager.getInstance().getBattle();
		if (battle != null && battle.isActive()) {
			Combatant activeBeforeCounter = battle.getPlayer();
			battle.endTurn();
			battle.enemyAttack();
			if (battle.isActive())
				battle.endTurn();

			resultMessage.append(battle.describeLastEnemyAction());
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
