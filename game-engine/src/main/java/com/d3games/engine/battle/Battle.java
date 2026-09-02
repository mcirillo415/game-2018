package com.d3games.engine.battle;

import java.util.Objects;

public class Battle {
	private final Combatant player;
	private final Combatant enemy;
	private final boolean escapable;
	private BattleState state = BattleState.ACTIVE;
    private Combatant currentTurn;

	public Battle(Combatant player, Combatant enemy) {
		this(player, enemy, true);
	}

	public Battle(Combatant player, Combatant enemy, boolean escapable) {
		this.player = Objects.requireNonNull(player, "Player combatant is required");
		this.enemy = Objects.requireNonNull(enemy, "Enemy combatant is required");
		this.escapable = escapable;
        currentTurn = player; // Player starts first
	}

	public boolean isEscapable() {
		return escapable;
	}

	public Combatant getPlayer() {
		return player;
	}

	public Combatant getEnemy() {
		return enemy;
	}

	public Combatant getCurrentTurn() {
		return currentTurn;
	}

    public void endTurn() {
        if (!isActive())
            throw new IllegalStateException("The battle is no longer active");
		System.out.println(currentTurn.getName() + "'s turn ended.");
        currentTurn = (currentTurn == player) ? enemy : player;
		System.out.println("It is now " + currentTurn.getName() + "'s turn.");
    }
    
	public BattleState getState() {
		return state;
	}

	public boolean isActive() {
		return state == BattleState.ACTIVE;
	}

	public void playerAttack(int damage) {
		ensureActive();
		int actualDamage = applyDefense(damage, enemy);
		System.out.printf("%s attacks %s for %d damage.%n",
				player.getName(), enemy.getName(), actualDamage);
		enemy.takeDamage(actualDamage);
		System.out.printf("%s has %d/%d HP remaining.%n",
				enemy.getName(), enemy.getHealth(), enemy.getMaximumHealth());
		if (enemy.isFainted())
		{
			state = BattleState.PLAYER_WON;
			System.out.println("Player won the battle.");
			int experienceReward = enemy.getExperienceReward();
			int levelsGained = player.gainExperience(experienceReward);
			System.out.printf("%s gained %d experience.%n", player.getName(), experienceReward);
			if (levelsGained > 0)
				System.out.printf("%s leveled up to level %d!%n", player.getName(), player.getLevel());
		}
	}

	public void enemyAttack(int damage) {
		ensureActive();
		int actualDamage = applyDefense(damage, player);
		System.out.printf("%s attacks %s for %d damage.%n",
				enemy.getName(), player.getName(), actualDamage);
		player.takeDamage(actualDamage);
		System.out.printf("%s has %d/%d HP remaining.%n",
				player.getName(), player.getHealth(), player.getMaximumHealth());
		if (player.isFainted())
		{
			state = BattleState.PLAYER_LOST;
			System.out.println("Player lost the battle.");
		}
	}

	public void escape() {
		ensureActive();
		if (!escapable)
			throw new IllegalStateException("This battle cannot be escaped");
		state = BattleState.ESCAPED;
		System.out.println(player.getName() + " escaped from the battle.");
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
