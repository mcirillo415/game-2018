package com.d3games.engine.menu;

import com.d3games.engine.GameManager;
import com.d3games.engine.GameMessage;
import com.d3games.engine.battle.Battle;
import com.d3games.engine.battle.Combatant;

public class AttackMoveItem implements MenuItem {
	private final String moveName;
	private final double damageMultiplier;
	private final double recoilFraction;
	private final double lifestealFraction;
	private final double evadeChance;

	public AttackMoveItem(String moveName, double damageMultiplier, double recoilFraction,
			double lifestealFraction, double evadeChance) {
		this.moveName = moveName;
		this.damageMultiplier = damageMultiplier;
		this.recoilFraction = recoilFraction;
		this.lifestealFraction = lifestealFraction;
		this.evadeChance = evadeChance;
	}

	@Override
	public void trigger() throws GameMessage {
		Battle battle = GameManager.getInstance().getBattle();
		if (battle == null || !battle.isActive())
			throw new GameMessage("There is no active battle.");

		Combatant player = battle.getPlayer();
		int damage = Math.max(1, (int) Math.round(player.getAttackPower() * damageMultiplier));
		battle.playerAttack(damage);

		boolean tookRecoil = false;
		int healedAmount = 0;
		if (battle.isActive()) {
			if (recoilFraction > 0) {
				player.takeDamage(Math.max(1, (int) Math.round(damage * recoilFraction)));
				tookRecoil = true;
			}
			if (lifestealFraction > 0) {
				healedAmount = (int) Math.round(damage * lifestealFraction);
				if (healedAmount > 0)
					player.heal(healedAmount);
			}
		}

		boolean evaded = false;
		if (battle.isActive()) {
			battle.endTurn();
			evaded = evadeChance > 0 && Math.random() < evadeChance;
			if (!evaded)
				battle.enemyAttack(battle.getEnemy().getAttackPower());
			if (battle.isActive())
				battle.endTurn();
		}

		StringBuilder resultMessage = new StringBuilder(moveName + " selected.");
		if (tookRecoil)
			resultMessage.append(" You took recoil damage!");
		if (healedAmount > 0)
			resultMessage.append(" You recovered ").append(healedAmount).append(" HP!");
		if (evaded)
			resultMessage.append(" You dodged the counterattack!");
		throw new GameMessage(resultMessage.toString());
	}
}
