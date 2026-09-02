package com.d3games.engine.menu;

import com.d3games.engine.GameManager;
import com.d3games.engine.GameMessage;
import com.d3games.engine.battle.Combatant;

public class PlayerStatsItem implements MenuItem {

	@Override
	public void trigger() throws GameMessage {
		Combatant player = GameManager.getInstance().getPlayerCombatant();
		throw new GameMessage(String.format(
				"Lv %d | HP %d/%d | ATK %d | DEF %d | EXP %d/%d",
				player.getLevel(), player.getHealth(), player.getMaximumHealth(),
				player.getAttackPower(), player.getDefense(),
				player.getExperience(), player.getExperienceToNextLevel()));
	}
}
