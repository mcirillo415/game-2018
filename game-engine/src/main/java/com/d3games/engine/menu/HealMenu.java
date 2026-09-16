package com.d3games.engine.menu;

import com.d3games.engine.GameManager;
import com.d3games.engine.battle.Combatant;
import com.d3games.engine.battle.Party;

public class HealMenu extends Menu {
	private static final long serialVersionUID = 1L;

	public HealMenu() {
		Party party = GameManager.getInstance().getParty();
		for (Combatant member : party.getMembers()) {
			StringBuilder label = new StringBuilder(member.getName())
					.append(" Lv").append(member.getLevel()).append(" ");
			if (member.isFainted())
				label.append("OUT");
			else
				label.append(member.getHealth()).append("/").append(member.getMaximumHealth());
			add(label.toString(), new HealTargetItem(member));
		}
	}
}
