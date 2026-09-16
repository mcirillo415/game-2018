package com.d3games.engine.menu;

import com.d3games.engine.GameManager;
import com.d3games.engine.GameMessage;
import com.d3games.engine.battle.Battle;
import com.d3games.engine.battle.BattleState;
import com.d3games.engine.battle.Combatant;
import com.d3games.engine.battle.Move;
import com.d3games.engine.battle.TypeChart;
import com.d3games.engine.sound.SoundEffect;
import com.d3games.engine.sound.SoundPlayer;

public class AttackMoveItem implements MenuItem {
	private final Move move;

	public AttackMoveItem(Move move) {
		this.move = move;
	}

	@Override
	public void trigger() throws GameMessage {
		Battle battle = GameManager.getInstance().getBattle();
		if (battle == null || !battle.isActive())
			throw new GameMessage("There is no active battle.");

		Combatant player = battle.getPlayer();
		Combatant enemy = battle.getEnemy();
		int levelBeforeAttack = player.getLevel();

		double effectiveness = TypeChart.getMultiplier(move.getElementType(), enemy.getElementType());
		int damage = Math.max(1, (int) Math.round(player.getAttackPower() * move.getDamageMultiplier() * effectiveness));
		battle.playerAttack(damage);
		if (effectiveness > 1.0)
			SoundPlayer.play(SoundEffect.SUPER_EFFECTIVE);
		else if (effectiveness < 1.0)
			SoundPlayer.play(SoundEffect.NOT_VERY_EFFECTIVE);
		else
			SoundPlayer.play(SoundEffect.ATTACK_HIT);

		boolean enemyDefeated = battle.getState() == BattleState.PLAYER_WON;
		if (enemyDefeated)
			SoundPlayer.play(SoundEffect.FAINT);

		boolean tookRecoil = false;
		int healedAmount = 0;
		if (battle.isActive()) {
			if (move.getRecoilFraction() > 0) {
				player.takeDamage(Math.max(1, (int) Math.round(damage * move.getRecoilFraction())));
				tookRecoil = true;
			}
			if (move.getLifestealFraction() > 0) {
				healedAmount = (int) Math.round(damage * move.getLifestealFraction());
				if (healedAmount > 0)
					player.heal(healedAmount);
			}
		}

		boolean evaded = false;
		Combatant activeBeforeCounter = battle.isActive() ? battle.getPlayer() : null;
		if (battle.isActive()) {
			battle.endTurn();
			evaded = move.getEvadeChance() > 0 && Math.random() < move.getEvadeChance();
			if (!evaded)
				battle.enemyAttack();
			if (battle.isActive())
				battle.endTurn();
		}

		StringBuilder resultMessage = new StringBuilder(move.getName() + " selected.");
		if (effectiveness > 1.0)
			resultMessage.append(" It's super effective!");
		else if (effectiveness < 1.0)
			resultMessage.append(" It's not very effective...");
		if (tookRecoil)
			resultMessage.append(" You took recoil damage!");
		if (healedAmount > 0)
			resultMessage.append(" You recovered ").append(healedAmount).append(" HP!");
		if (evaded)
			resultMessage.append(" You dodged the counterattack!");
		else if (activeBeforeCounter != null && battle.getLastEnemyMove() != null)
			resultMessage.append(" ").append(enemy.getName()).append(" used ")
					.append(battle.getLastEnemyMove().getName()).append("!");
		if (enemyDefeated) {
			resultMessage.append(" ").append(enemy.getName()).append(" fainted! You gained ")
					.append(enemy.getExperienceReward()).append(" EXP.");
			if (!battle.isEscapable()) {
				int currencyReward = enemy.getCurrencyReward();
				GameManager.getInstance().getWallet().add(currencyReward);
				resultMessage.append(" You found ").append(currencyReward).append(" Bones!");
			}
			if (player.getLevel() > levelBeforeAttack) {
				resultMessage.append(" Leveled up to level ").append(player.getLevel()).append("!");
				SoundPlayer.play(SoundEffect.LEVEL_UP);
			}
		}
		if (battle.getState() == BattleState.PLAYER_LOST) {
			resultMessage.append(" ").append(activeBeforeCounter.getName()).append(" was defeated!");
			SoundPlayer.play(SoundEffect.GAME_OVER);
		} else if (activeBeforeCounter != null && battle.isActive() && battle.getPlayer() != activeBeforeCounter) {
			resultMessage.append(" ").append(activeBeforeCounter.getName()).append(" fainted! ")
					.append(battle.getPlayer().getName()).append(" was sent out!");
			SoundPlayer.play(SoundEffect.FAINT);
		}
		throw new GameMessage(resultMessage.toString());
	}
}
