package com.d3games.engine.menu;

import java.util.List;

import com.d3games.engine.battle.Move;

public class AttackMenu extends Menu {
	private static final long serialVersionUID = 1L;

	public AttackMenu(List<Move> moves) {
		for (Move move : moves)
			add(move.getName(), new AttackMoveItem(move));
	}
}
