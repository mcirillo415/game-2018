package com.d3games.engine.map;

public class DialogueNPC extends NPC {
	private final String dialogue;

	public DialogueNPC(Unit pos, String dialogue) {
		super(pos, false);
		this.dialogue = dialogue;
	}

	@Override
	public String prompt() {
		return dialogue;
	}

	@Override
	public String toFile() {
		return "v" + pos.getMap().getId() + pos.getX() + pos.getY();
	}
}
