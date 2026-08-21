package com.d3games.engine.menu;

import com.d3games.engine.GameManager;
import com.d3games.engine.GameMessage;
import com.d3games.engine.battle.Battle;

public class AttackItem implements MenuItem {
	private static final int PLAYER_DAMAGE = 20;
	private static final int ENEMY_DAMAGE = 10;

	@Override
	public void trigger() throws GameMessage {
		Battle battle = GameManager.getInstance().getBattle();
		if (battle == null || !battle.isActive())
			throw new GameMessage("There is no active battle.");

		battle.playerAttack(PLAYER_DAMAGE);
		if (battle.isActive()) {
			battle.endTurn();
			battle.enemyAttack(ENEMY_DAMAGE);
			if (battle.isActive())
				battle.endTurn();
		}

		throw new GameMessage("Attack selected.");
	}
}
