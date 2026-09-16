package com.d3games.engine.menu;

import com.d3games.engine.GameManager;
import com.d3games.engine.GameMessage;
import com.d3games.engine.item.ItemType;
import com.d3games.engine.item.Wallet;
import com.d3games.engine.sound.SoundEffect;
import com.d3games.engine.sound.SoundPlayer;

public class BuyItemItem implements MenuItem {
	private final ItemType itemType;
	private final int price;

	public BuyItemItem(ItemType itemType, int price) {
		this.itemType = itemType;
		this.price = price;
	}

	@Override
	public void trigger() throws GameMessage {
		Wallet wallet = GameManager.getInstance().getWallet();
		if (wallet.getBalance() < price)
			throw new GameMessage("You don't have enough Bones for that!");

		wallet.spend(price);
		GameManager.getInstance().getInventory().add(itemType, 1);
		SoundPlayer.play(SoundEffect.PURCHASE);
		throw new GameMessage("Bought a " + itemType.getDisplayName() + " for " + price + " Bones!");
	}
}
