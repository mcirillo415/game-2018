package com.d3games.engine.menu;

public class AttackMenu extends Menu {
	private static final long serialVersionUID = 1L;

	public AttackMenu() {
		add("Tackle", new AttackMoveItem("Tackle", 1.0, 0, 0, 0));
		add("Power Bite", new AttackMoveItem("Power Bite", 1.5, 0.15, 0, 0));
		add("Quick Nip", new AttackMoveItem("Quick Nip", 0.6, 0, 0, 0.4));
		add("Leech Bite", new AttackMoveItem("Leech Bite", 0.7, 0, 0.5, 0));
	}
}
