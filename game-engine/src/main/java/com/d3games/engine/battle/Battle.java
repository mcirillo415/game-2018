package com.d3games.engine.battle;

import java.util.Objects;

public class Battle {
	private final Combatant player;
	private final Combatant enemy;
	private BattleState state = BattleState.ACTIVE;
    private Combatant currentTurn;

	public Battle(Combatant player, Combatant enemy) {
		this.player = Objects.requireNonNull(player, "Player combatant is required");
		this.enemy = Objects.requireNonNull(enemy, "Enemy combatant is required");
        currentTurn = player; // Player starts first
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
		System.out.printf("%s attacks %s for %d damage.%n",
				player.getName(), enemy.getName(), damage);
		enemy.takeDamage(damage);
		System.out.printf("%s has %d/%d HP remaining.%n",
				enemy.getName(), enemy.getHealth(), enemy.getMaximumHealth());
		if (enemy.isFainted())
		{
			state = BattleState.PLAYER_WON;
			System.out.println("Player won the battle.");
		}
	}

	public void enemyAttack(int damage) {
		ensureActive();
		System.out.printf("%s attacks %s for %d damage.%n",
				enemy.getName(), player.getName(), damage);
		player.takeDamage(damage);
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
		state = BattleState.ESCAPED;
		System.out.println(player.getName() + " escaped from the battle.");
	}

	private void ensureActive() {
		if (!isActive())
			throw new IllegalStateException("The battle is no longer active");
	}
}
