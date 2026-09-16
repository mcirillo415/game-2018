package com.d3games.engine.item;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class WalletTest {
	private Wallet wallet;

	@Before
	public void setup() {
		wallet = new Wallet();
	}

	@Test
	public void startsWithZeroBalance() {
		assertEquals(0, wallet.getBalance());
	}

	@Test
	public void addIncreasesBalance() {
		wallet.add(10);
		wallet.add(5);
		assertEquals(15, wallet.getBalance());
	}

	@Test
	public void spendDecreasesBalance() {
		wallet.add(20);
		wallet.spend(15);
		assertEquals(5, wallet.getBalance());
	}

	@Test(expected = IllegalStateException.class)
	public void spendThrowsWhenInsufficientFunds() {
		wallet.add(10);
		wallet.spend(15);
	}
}
