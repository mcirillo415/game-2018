package com.d3games.engine.map;

import com.d3games.engine.GameManager;
import com.d3games.engine.item.ItemType;
import com.d3games.engine.sound.SoundEffect;
import com.d3games.engine.sound.SoundPlayer;

public class ItemPickup extends Space {
	private static final String CURRENCY_KIND = "CURRENCY";
	private static final String KEY_PREFIX = "KEY:";

	private final String kind;
	private final int amount;

	public ItemPickup(String kind, int amount) {
		super();
		this.kind = kind;
		this.amount = amount;
	}

	@Override
	public Unit approach() {
		super.approach();

		if (CURRENCY_KIND.equals(kind))
			GameManager.getInstance().getWallet().add(amount);
		else if (kind.startsWith(KEY_PREFIX))
			GameManager.getInstance().getKeyRing().add(kind.substring(KEY_PREFIX.length()));
		else
			GameManager.getInstance().getInventory().add(ItemType.valueOf(kind), amount);
		SoundPlayer.play(SoundEffect.ITEM_PICKUP);
		GameManager.getInstance().triggerMessage(buildPickupMessage());
		GameManager.getInstance().recordPickupCollected(map.getId(), xLoc, yLoc);

		Space collectedTile = new Space();
		map.replace(collectedTile, xLoc, yLoc);
		return collectedTile;
	}

	private String buildPickupMessage() {
		if (CURRENCY_KIND.equals(kind))
			return "Found " + amount + " Bones!";
		if (kind.startsWith(KEY_PREFIX))
			return "Found the " + kind.substring(KEY_PREFIX.length()) + "!";
		String name = ItemType.valueOf(kind).getDisplayName();
		return amount == 1 ? "Found a " + name + "!" : "Found " + amount + " " + name + "s!";
	}

	@Override
	public String toFile() {
		return "i" + map.getId() + xLoc + yLoc;
	}
}
