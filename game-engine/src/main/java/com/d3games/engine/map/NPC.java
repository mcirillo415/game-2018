package com.d3games.engine.map;

import com.d3games.engine.GameManager;

public class NPC extends Player {
	private final boolean initiatesBattle;

	public NPC() {
		this(true);
	}

	public NPC(boolean initiatesBattle) {
		super();
		this.initiatesBattle = initiatesBattle;
	}

	public NPC(Unit pos) {
		this(pos, true);
	}

	public NPC(Unit pos, boolean initiatesBattle) {
		super(pos);
		this.initiatesBattle = initiatesBattle;
	}

	public String toFile() {
		return "n" + pos.getMap().getId() + pos.getX() + pos.getY();
	}

	public boolean initiatesBattle() {
		return initiatesBattle;
	}

	public String prompt() {
		if (initiatesBattle)
			GameManager.getInstance().triggerBattle(false,
					new MapCoordinate(pos.getMap().getId(), pos.getX(), pos.getY()));
		if (Math.random() > 0.5)
			return "Arf!";
		else
			return "Woof";
	}

}
