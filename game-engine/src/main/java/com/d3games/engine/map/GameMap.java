package com.d3games.engine.map;

public class GameMap {
	private int id;
	private int size = 0;
	private int width = 0;
	private int height = 0;
	private Unit[][] map;
	String worldName;
	private String roomName;

	public GameMap(int id, int x, int y) {
		this.id = id;
		map = new Unit[x][y];
		width = x;
		height = y;
	}

	public GameMap(String worldName, int id, int x, int y) {
		this.worldName=worldName;
		this.id = id;
		map = new Unit[x][y];
		width = x;
		height = y;
	}

	public int getId() {
		return id;
	}

	public String getWorldName() {
		return worldName;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public void add(Unit unit, int x, int y) {
		if (map[x][y] != null)
			throw new InvalidMapSetupException();
		map[x][y] = unit;
		unit.setLocation(this, x, y);
		size++;
	}

	public void replace(Unit unit, int x, int y) {
		map[x][y] = unit;
		unit.setLocation(this, x, y);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int size() {
		return size;
	}

	public int maxSize() {
		return width * height;
	}

	public Unit get(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height)
			return null;
		return map[x][y];
	}

}
