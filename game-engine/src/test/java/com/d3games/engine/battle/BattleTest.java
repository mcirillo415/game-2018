package com.d3games.engine.battle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class BattleTest {
	private Combatant starter;
	private Combatant backup;
	private Party party;
	private Combatant enemy;

	@Before
	public void setup() {
		starter = new Combatant("Starter", 10, 5, 0);
		backup = new Combatant("Backup", 10, 5, 0);
		party = new Party(starter);
		party.add(backup);
		enemy = new Combatant("Enemy", 10, 100, 0);
	}

	@Test
	public void catchEnemyEndsBattleAsCaughtWhenEscapable() {
		Battle battle = new Battle(party, enemy, true);
		battle.catchEnemy();
		assertEquals(BattleState.CAUGHT, battle.getState());
	}

	@Test(expected = IllegalStateException.class)
	public void catchEnemyRejectedWhenNotEscapable() {
		Battle battle = new Battle(party, enemy, false);
		battle.catchEnemy();
	}

	@Test
	public void activeMemberFaintingWithSurvivorsAutoSwapsInsteadOfEndingBattle() {
		Battle battle = new Battle(party, enemy, true);
		battle.enemyAttack();

		assertTrue(starter.isFainted());
		assertTrue(battle.isActive());
		assertEquals(backup, battle.getPlayer());
		assertEquals(BattleState.ACTIVE, battle.getState());
	}

	@Test
	public void wholePartyFaintingEndsBattleAsPlayerLost() {
		Battle battle = new Battle(party, enemy, true);
		battle.enemyAttack();
		battle.enemyAttack();

		assertTrue(party.allFainted());
		assertEquals(BattleState.PLAYER_LOST, battle.getState());
	}

	@Test
	public void enemyAttackUsesConfiguredMoveSelector() {
		Move signatureMove = new Move("Signature Move", ElementType.NORMAL, 1.0, 0, 0, 0);
		enemy.setMoves(java.util.Collections.singletonList(signatureMove));

		Battle battle = new Battle(party, enemy, true);
		battle.setEnemyMoveSelector((self, opponent) -> signatureMove);
		battle.enemyAttack();

		assertEquals(signatureMove, battle.getLastEnemyMove());
	}
}
