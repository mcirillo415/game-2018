package com.d3games.engine.map;

import com.d3games.engine.GameManager;

public class HealerNPC extends NPC {

	public HealerNPC() {
		super(false);
	}

	public HealerNPC(Unit pos) {
		super(pos, false);
	}

	@Override
	public String prompt() {
		GameManager.getInstance().getParty().healAll();
		return "Your party has been fully healed!";
	}

	@Override
	public String toFile() {
		return "h" + pos.getMap().getId() + pos.getX() + pos.getY();
	}
}
