package com.d3games.engine.menu;

import com.d3games.engine.GameManager;
import com.d3games.engine.battle.Combatant;
import com.d3games.engine.battle.Party;

public class PartyMenu extends Menu {
	private static final long serialVersionUID = 1L;

	public PartyMenu() {
		Party party = GameManager.getInstance().getParty();
		Combatant active = party.getActive();
		for (Combatant member : party.getMembers()) {
			StringBuilder label = new StringBuilder();
			if (member == active)
				label.append("*");
			label.append(member.getName()).append(" Lv").append(member.getLevel()).append(" ");
			if (member.isFainted())
				label.append("OUT");
			else
				label.append(member.getHealth()).append("/").append(member.getMaximumHealth());
			add(label.toString(), new SwitchToMemberItem(member));
		}
	}
}
