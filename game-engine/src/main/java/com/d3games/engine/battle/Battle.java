package com.d3games.engine.battle;

import java.util.Objects;

import com.d3games.engine.map.MapCoordinate;

public class Battle {
	private static final double PARALYSIS_SKIP_CHANCE = 0.25;

	private final Party party;
	private final Combatant enemy;
	private final boolean escapable;
	private final MapCoordinate sourceNpc;
	private BattleState state = BattleState.ACTIVE;
    private Combatant currentTurn;
	private MoveSelector enemyMoveSelector = new RandomMoveSelector();
	private Move lastEnemyMove;
	private boolean lastEnemyActionSkippedByParalysis;

	public Battle(Party party, Combatant enemy) {
		this(party, enemy, true);
	}

	public Battle(Party party, Combatant enemy, boolean escapable) {
		this(party, enemy, escapable, null);
	}

	public Battle(Party party, Combatant enemy, boolean escapable, MapCoordinate sourceNpc) {
		this.party = Objects.requireNonNull(party, "A party is required");
		this.enemy = Objects.requireNonNull(enemy, "Enemy combatant is required");
		this.escapable = escapable;
		this.sourceNpc = sourceNpc;
        currentTurn = party.getActive(); // Player starts first
	}

	public boolean isEscapable() {
		return escapable;
	}

	public MapCoordinate getSourceNpc() {
		return sourceNpc;
	}

	public Party getParty() {
		return party;
	}

	public Combatant getPlayer() {
		return party.getActive();
	}

	public Combatant getEnemy() {
		return enemy;
	}

	public Combatant getCurrentTurn() {
		return currentTurn;
	}

	public void setEnemyMoveSelector(MoveSelector enemyMoveSelector) {
		this.enemyMoveSelector = Objects.requireNonNull(enemyMoveSelector, "A move selector is required");
	}

	public Move getLastEnemyMove() {
		return lastEnemyMove;
	}

	public String describeLastEnemyAction() {
		if (lastEnemyActionSkippedByParalysis)
			return " " + enemy.getName() + " is paralyzed and can't move!";
		if (lastEnemyMove != null)
			return " " + enemy.getName() + " used " + lastEnemyMove.getName() + "!";
		return "";
	}

	public boolean rollParalysisSkip(Combatant combatant) {
		return combatant.getStatus() == StatusEffect.PARALYSIS && Math.random() < PARALYSIS_SKIP_CHANCE;
	}

    public void endTurn() {
        if (!isActive())
            throw new IllegalStateException("The battle is no longer active");
		System.out.println(currentTurn.getName() + "'s turn ended.");
        currentTurn = (currentTurn == enemy) ? party.getActive() : enemy;
		System.out.println("It is now " + currentTurn.getName() + "'s turn.");
    }

	public BattleState getState() {
		return state;
	}

	public boolean isActive() {
		return state == BattleState.ACTIVE;
	}

	public void playerAttack(int damage) {
		playerAttack(damage, null);
	}

	public void playerAttack(int damage, Move move) {
		ensureActive();
		Combatant player = party.getActive();
		int actualDamage = applyDefense(damage, enemy);
		System.out.printf("%s attacks %s for %d damage.%n",
				player.getName(), enemy.getName(), actualDamage);
		enemy.takeDamage(actualDamage);
		System.out.printf("%s has %d/%d HP remaining.%n",
				enemy.getName(), enemy.getHealth(), enemy.getMaximumHealth());
		if (move != null)
			applyMoveStatus(move, enemy);
		resolveEnemyOutcome(player);

		if (isActive()) {
			int statusDamage = enemy.applyStatusDamage();
			if (statusDamage > 0) {
				System.out.printf("%s is hurt by its %s!%n", enemy.getName(), enemy.getStatus());
				resolveEnemyOutcome(player);
			}
		}
	}

	public void enemyAttack() {
		ensureActive();
		Combatant player = party.getActive();

		if (enemy.getStatus() == StatusEffect.PARALYSIS && Math.random() < PARALYSIS_SKIP_CHANCE) {
			lastEnemyMove = null;
			lastEnemyActionSkippedByParalysis = true;
			System.out.println(enemy.getName() + " is paralyzed and can't move!");
		} else {
			lastEnemyActionSkippedByParalysis = false;
			Move move = enemyMoveSelector.selectMove(enemy, player);
			lastEnemyMove = move;
			double effectiveness = TypeChart.getMultiplier(move.getElementType(), player.getElementType());
			int rawDamage = Math.max(1, (int) Math.round(enemy.getAttackPower() * move.getDamageMultiplier() * effectiveness));
			int actualDamage = applyDefense(rawDamage, player);
			System.out.printf("%s used %s!%n", enemy.getName(), move.getName());
			System.out.printf("%s attacks %s for %d damage.%n",
					enemy.getName(), player.getName(), actualDamage);
			player.takeDamage(actualDamage);
			System.out.printf("%s has %d/%d HP remaining.%n",
					player.getName(), player.getHealth(), player.getMaximumHealth());
			applyMoveStatus(move, player);
			resolvePlayerOutcome(player);
		}

		if (isActive() && party.getActive() == player) {
			int statusDamage = player.applyStatusDamage();
			if (statusDamage > 0) {
				System.out.printf("%s is hurt by its %s!%n", player.getName(), player.getStatus());
				resolvePlayerOutcome(player);
			}
		}
	}

	public void escape() {
		ensureActive();
		if (!escapable)
			throw new IllegalStateException("This battle cannot be escaped");
		state = BattleState.ESCAPED;
		System.out.println(party.getActive().getName() + " escaped from the battle.");
	}

	public void catchEnemy() {
		ensureActive();
		if (!escapable)
			throw new IllegalStateException("This battle's enemy cannot be caught");
		state = BattleState.CAUGHT;
		System.out.println(enemy.getName() + " was caught!");
	}

	private void resolveEnemyOutcome(Combatant player) {
		if (!isActive() || !enemy.isFainted())
			return;
		state = BattleState.PLAYER_WON;
		System.out.println("Player won the battle.");
		int experienceReward = enemy.getExperienceReward();
		int levelsGained = player.gainExperience(experienceReward);
		System.out.printf("%s gained %d experience.%n", player.getName(), experienceReward);
		if (levelsGained > 0)
			System.out.printf("%s leveled up to level %d!%n", player.getName(), player.getLevel());
	}

	private void resolvePlayerOutcome(Combatant player) {
		if (!isActive() || !player.isFainted())
			return;
		if (party.allFainted()) {
			state = BattleState.PLAYER_LOST;
			System.out.println("Player lost the battle.");
		} else {
			Combatant next = party.nextAlive(player);
			party.setActive(next);
			System.out.printf("%s fainted! %s was sent out.%n", player.getName(), next.getName());
		}
	}

	private void applyMoveStatus(Move move, Combatant target) {
		if (move.getInflictedStatus() == StatusEffect.NONE)
			return;
		if (target.isFainted() || target.getStatus() != StatusEffect.NONE)
			return;
		if (Math.random() < move.getStatusChance()) {
			target.setStatus(move.getInflictedStatus());
			System.out.printf("%s was afflicted with %s!%n", target.getName(), move.getInflictedStatus());
		}
	}

	private void ensureActive() {
		if (!isActive())
			throw new IllegalStateException("The battle is no longer active");
	}

	private int applyDefense(int damage, Combatant target) {
		if (damage < 0)
			throw new IllegalArgumentException("Damage cannot be negative");
		return Math.max(1, damage - target.getDefense());
	}
}
