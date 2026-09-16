package com.d3games.engine.item;

public class Wallet {
	private int balance;

	public int getBalance() {
		return balance;
	}

	public void add(int amount) {
		if (amount < 0)
			throw new IllegalArgumentException("Cannot add a negative amount");
		balance += amount;
	}

	public void spend(int amount) {
		if (amount < 0)
			throw new IllegalArgumentException("Cannot spend a negative amount");
		if (amount > balance)
			throw new IllegalStateException("Not enough Bones");
		balance -= amount;
	}
}
