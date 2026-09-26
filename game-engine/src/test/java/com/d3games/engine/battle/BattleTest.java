package com.d3games.engine.battle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

	@Test
	public void poisonAloneCanFinishOffTheEnemyAndStillAwardsExperience() {
		Combatant weakEnemy = new Combatant("Weak", 2, 100, 0);
		weakEnemy.setStatus(StatusEffect.POISON);
		Battle battle = new Battle(party, weakEnemy, true);

		battle.playerAttack(1, null);

		assertTrue(weakEnemy.isFainted());
		assertEquals(BattleState.PLAYER_WON, battle.getState());
		assertEquals(weakEnemy.getExperienceReward(), starter.getExperience());
	}

	@Test
	public void statusDamageDoesNotTickForANewlySwappedInMember() {
		Combatant lowHealthStarter = new Combatant("Starter", 2, 5, 0);
		Combatant freshBackup = new Combatant("Backup", 10, 5, 0);
		freshBackup.setStatus(StatusEffect.POISON);
		Party localParty = new Party(lowHealthStarter);
		localParty.add(freshBackup);
		Combatant strongEnemy = new Combatant("Strong", 10, 100, 0);

		Battle battle = new Battle(localParty, strongEnemy, true);
		battle.enemyAttack();

		assertEquals(freshBackup, battle.getPlayer());
		assertEquals(10, freshBackup.getHealth());
	}

	@Test
	public void rollParalysisSkipIsAlwaysFalseWhenNotParalyzed() {
		Battle battle = new Battle(party, enemy, true);
		for (int i = 0; i < 20; i++)
			assertFalse(battle.rollParalysisSkip(starter));
	}

	@Test
	public void moveWithFullStatusChanceAppliesStatusOnHit() {
		Move alwaysBurns = new Move("Scorch", ElementType.FIRE, 1.0, 0, 0, 0, StatusEffect.BURN, 1.0);
		Battle battle = new Battle(party, enemy, true);

		battle.playerAttack(1, alwaysBurns);

		assertEquals(StatusEffect.BURN, enemy.getStatus());
	}

	@Test
	public void moveStatusNeverOverwritesAnExistingStatus() {
		Move alwaysBurns = new Move("Scorch", ElementType.FIRE, 1.0, 0, 0, 0, StatusEffect.BURN, 1.0);
		enemy.setStatus(StatusEffect.POISON);
		Battle battle = new Battle(party, enemy, true);

		battle.playerAttack(1, alwaysBurns);

		assertEquals(StatusEffect.POISON, enemy.getStatus());
	}
}
