package com.d3games.engine.map;

import com.d3games.engine.GameManager;
import com.d3games.engine.InvalidMoveException;

public class Door extends Unit {
	public enum LockType {
		NONE, KEY, NPC_DEFEAT
	}

	private int targetKey;
	private int targetX;
	private int targetY;
	private LockType lockType = LockType.NONE;
	private String requiredKeyName;
	private MapCoordinate requiredNpc;

	public Door() {
		super();
	}

	public Door(GameMap target, int x, int y) {
		this();
		setTarget(target.getId(), x, y);
	}

	public Door(int target, int x, int y) {
		this();
		setTarget(target, x, y);
	}

	@Override
	protected String getBackgroundUrl() {
		return "door.png";
	}

	public void setTarget(int targetKey, int x, int y) {
		this.targetKey = targetKey;
		targetX = x;
		targetY = y;
	}

	public void setKeyLock(String keyName) {
		lockType = LockType.KEY;
		requiredKeyName = keyName;
	}

	public void setNpcLock(MapCoordinate npc) {
		lockType = LockType.NPC_DEFEAT;
		requiredNpc = npc;
	}

	public LockType getLockType() {
		return lockType;
	}

	@Override
	public Unit approach() {
		if (lockType == LockType.KEY && !GameManager.getInstance().getKeyRing().has(requiredKeyName))
			throw new InvalidMoveException("This door is locked. You need the " + requiredKeyName + ".");
		if (lockType == LockType.NPC_DEFEAT && !GameManager.getInstance().isNpcDefeated(requiredNpc))
			throw new InvalidMoveException("This door is locked until you defeat the guard.");

		Unit target = GameManager.getInstance().get(targetKey).get(targetX, targetY);
		if (target == null)
			throw new InvalidMoveException("Door locked!");
		return target;
	}

	@Override
	public String prompt() {
		return "It's a door.";
	}

	@Override
	public String toFile() {
		return "d" + map.getId() + xLoc + yLoc + targetKey + targetX + targetY;
	}

}
