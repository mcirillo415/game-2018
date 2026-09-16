package com.d3games.engine.map;

import com.d3games.engine.GameManager;
import com.d3games.engine.item.ItemType;
import com.d3games.engine.sound.SoundEffect;
import com.d3games.engine.sound.SoundPlayer;

public class ItemPickup extends Space {
	private static final String CURRENCY_KIND = "CURRENCY";

	private final String kind;
	private final int amount;
	private boolean collected;

	public ItemPickup(String kind, int amount) {
		super();
		this.kind = kind;
		this.amount = amount;
	}

	@Override
	public Unit approach() {
		Unit target = super.approach();
		if (!collected) {
			collected = true;
			if (CURRENCY_KIND.equals(kind))
				GameManager.getInstance().getWallet().add(amount);
			else
				GameManager.getInstance().getInventory().add(ItemType.valueOf(kind), amount);
			SoundPlayer.play(SoundEffect.ITEM_PICKUP);
		}
		return target;
	}

	@Override
	public String toFile() {
		return "i" + map.getId() + xLoc + yLoc;
	}
}
