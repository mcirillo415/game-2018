package com.d3games.engine.editor;

public class EditorCell {
	public enum GroundType {
		EMPTY, SPACE, OBSTACLE, WILD_GRASS, ITEM_PICKUP, DOOR
	}

	public enum CharacterType {
		NONE, NPC, SHOP, DIALOGUE, HEALER, PLAYER_START
	}

	public enum LockType {
		NONE, KEY, NPC
	}

	private GroundType groundType = GroundType.EMPTY;

	private String pickupKind;
	private int pickupAmount;

	private String targetRoom;
	private int targetX;
	private int targetY;
	private LockType lockType = LockType.NONE;
	private String lockKeyName;
	private String lockNpcRoom;
	private int lockNpcX;
	private int lockNpcY;

	private CharacterType characterType = CharacterType.NONE;
	private boolean npcInitiatesBattle = true;
	private String dialogueText;

	public GroundType getGroundType() {
		return groundType;
	}

	public void setGroundType(GroundType groundType) {
		this.groundType = groundType;
	}

	public String getPickupKind() {
		return pickupKind;
	}

	public void setPickupKind(String pickupKind) {
		this.pickupKind = pickupKind;
	}

	public int getPickupAmount() {
		return pickupAmount;
	}

	public void setPickupAmount(int pickupAmount) {
		this.pickupAmount = pickupAmount;
	}

	public String getTargetRoom() {
		return targetRoom;
	}

	public void setTargetRoom(String targetRoom) {
		this.targetRoom = targetRoom;
	}

	public int getTargetX() {
		return targetX;
	}

	public void setTargetX(int targetX) {
		this.targetX = targetX;
	}

	public int getTargetY() {
		return targetY;
	}

	public void setTargetY(int targetY) {
		this.targetY = targetY;
	}

	public LockType getLockType() {
		return lockType;
	}

	public void setLockType(LockType lockType) {
		this.lockType = lockType;
	}

	public String getLockKeyName() {
		return lockKeyName;
	}

	public void setLockKeyName(String lockKeyName) {
		this.lockKeyName = lockKeyName;
	}

	public String getLockNpcRoom() {
		return lockNpcRoom;
	}

	public void setLockNpcRoom(String lockNpcRoom) {
		this.lockNpcRoom = lockNpcRoom;
	}

	public int getLockNpcX() {
		return lockNpcX;
	}

	public void setLockNpcX(int lockNpcX) {
		this.lockNpcX = lockNpcX;
	}

	public int getLockNpcY() {
		return lockNpcY;
	}

	public void setLockNpcY(int lockNpcY) {
		this.lockNpcY = lockNpcY;
	}

	public CharacterType getCharacterType() {
		return characterType;
	}

	public void setCharacterType(CharacterType characterType) {
		this.characterType = characterType;
	}

	public boolean isNpcInitiatesBattle() {
		return npcInitiatesBattle;
	}

	public void setNpcInitiatesBattle(boolean npcInitiatesBattle) {
		this.npcInitiatesBattle = npcInitiatesBattle;
	}

	public String getDialogueText() {
		return dialogueText;
	}

	public void setDialogueText(String dialogueText) {
		this.dialogueText = dialogueText;
	}
}
