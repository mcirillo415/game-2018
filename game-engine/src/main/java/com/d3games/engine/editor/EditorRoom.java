package com.d3games.engine.editor;

public class EditorRoom {
	private final int id;
	private String name;
	private final int width;
	private final int height;
	private final EditorCell[][] cells;

	public EditorRoom(int id, String name, int width, int height) {
		this.id = id;
		this.name = name;
		this.width = width;
		this.height = height;
		cells = new EditorCell[width][height];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				cells[x][y] = new EditorCell();
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public EditorCell getCell(int x, int y) {
		return cells[x][y];
	}
}
