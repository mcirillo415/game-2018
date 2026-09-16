package com.d3games.engine.menu;

import com.d3games.engine.GameManager;
import com.d3games.engine.GameMessage;
import com.d3games.engine.battle.Battle;
import com.d3games.engine.battle.BattleState;
import com.d3games.engine.battle.Combatant;
import com.d3games.engine.battle.Party;
import com.d3games.engine.sound.SoundEffect;
import com.d3games.engine.sound.SoundPlayer;

public class SwitchToMemberItem implements MenuItem {
	private final Combatant target;

	public SwitchToMemberItem(Combatant target) {
		this.target = target;
	}

	@Override
	public void trigger() throws GameMessage {
		Party party = GameManager.getInstance().getParty();
		if (target.isFainted())
			throw new GameMessage(target.getName() + " has fainted and can't battle!");
		if (party.getActive() == target)
			throw new GameMessage(target.getName() + " is already in battle!");

		party.setActive(target);

		StringBuilder resultMessage = new StringBuilder("Went with " + target.getName() + "!");

		Battle battle = GameManager.getInstance().getBattle();
		if (battle != null && battle.isActive()) {
			battle.endTurn();
			battle.enemyAttack();
			if (battle.isActive())
				battle.endTurn();

			if (battle.getLastEnemyMove() != null)
				resultMessage.append(" ").append(battle.getEnemy().getName()).append(" used ")
						.append(battle.getLastEnemyMove().getName()).append("!");
			if (battle.getState() == BattleState.PLAYER_LOST) {
				resultMessage.append(" ").append(target.getName()).append(" was defeated!");
				SoundPlayer.play(SoundEffect.GAME_OVER);
			} else if (battle.isActive() && battle.getPlayer() != target) {
				resultMessage.append(" ").append(target.getName()).append(" fainted! ")
						.append(battle.getPlayer().getName()).append(" was sent out!");
				SoundPlayer.play(SoundEffect.FAINT);
			}
		}

		throw new GameMessage(resultMessage.toString());
	}
}
