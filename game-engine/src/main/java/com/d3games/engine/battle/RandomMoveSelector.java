package com.d3games.engine.battle;

import java.util.List;
import java.util.Random;

public class RandomMoveSelector implements MoveSelector {
	private static final Random RANDOM = new Random();

	@Override
	public Move selectMove(Combatant self, Combatant opponent) {
		List<Move> moves = self.getMoves();
		return moves.get(RANDOM.nextInt(moves.size()));
	}
}
