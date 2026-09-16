package com.d3games.engine.map;

import com.d3games.engine.GameManager;

public class ShopNPC extends NPC {

	public ShopNPC() {
		super(false);
	}

	public ShopNPC(Unit pos) {
		super(pos, false);
	}

	@Override
	public String prompt() {
		GameManager.getInstance().triggerShop();
		return "Welcome to the shop! Take a look around.";
	}

	@Override
	public String toFile() {
		return "k" + pos.getMap().getId() + pos.getX() + pos.getY();
	}
}
