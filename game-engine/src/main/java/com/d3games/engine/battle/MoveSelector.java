package com.d3games.engine.battle;

public interface MoveSelector {
	Move selectMove(Combatant self, Combatant opponent);
}
