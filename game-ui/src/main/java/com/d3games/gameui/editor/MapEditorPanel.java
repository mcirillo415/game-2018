package com.d3games.gameui.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import com.d3games.engine.editor.EditorCell;
import com.d3games.engine.editor.EditorCell.CharacterType;
import com.d3games.engine.editor.EditorCell.GroundType;
import com.d3games.engine.editor.EditorRoom;
import com.d3games.engine.editor.EditorWorld;

/** Renders the active room as a grid of sprites and applies the selected palette Tool on click. */
public class MapEditorPanel extends JPanel {

	private static final int CELL_SIZE = 50;

	private EditorWorld world;
	private EditorRoom activeRoom;
	private Tool activeTool;

	public MapEditorPanel() {
		setBackground(Color.DARK_GRAY);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				handleClick(e.getX(), e.getY());
			}
		});
	}

	public void setWorld(EditorWorld world) {
		this.world = world;
	}

	public void setActiveRoom(EditorRoom activeRoom) {
		this.activeRoom = activeRoom;
		revalidate();
		repaint();
	}

	public void setActiveTool(Tool activeTool) {
		this.activeTool = activeTool;
	}

	@Override
	public Dimension getPreferredSize() {
		if (activeRoom == null)
			return new Dimension(200, 200);
		return new Dimension(activeRoom.getWidth() * CELL_SIZE, activeRoom.getHeight() * CELL_SIZE);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (activeRoom == null)
			return;

		Graphics2D g2d = (Graphics2D) g;
		for (int x = 0; x < activeRoom.getWidth(); x++) {
			for (int y = 0; y < activeRoom.getHeight(); y++) {
				EditorCell cell = activeRoom.getCell(x, y);
				int px = x * CELL_SIZE;
				int py = y * CELL_SIZE;

				if (cell.getGroundType() == GroundType.EMPTY) {
					g2d.setColor(Color.LIGHT_GRAY);
					g2d.fillRect(px, py, CELL_SIZE, CELL_SIZE);
					g2d.setColor(Color.GRAY);
					g2d.drawLine(px, py, px + CELL_SIZE, py + CELL_SIZE);
					g2d.drawLine(px, py + CELL_SIZE, px + CELL_SIZE, py);
				} else {
					Image groundImage = EditorSprites.getGroundImage(cell.getGroundType());
					if (groundImage != null)
						g2d.drawImage(groundImage, px, py, CELL_SIZE, CELL_SIZE, this);
				}

				if (cell.getCharacterType() != CharacterType.NONE) {
					Image characterImage = EditorSprites.getCharacterImage(cell.getCharacterType());
					if (characterImage != null)
						g2d.drawImage(characterImage, px, py, CELL_SIZE, CELL_SIZE, this);
				}

				g2d.setColor(Color.BLACK);
				g2d.drawRect(px, py, CELL_SIZE, CELL_SIZE);
			}
		}
	}

	private void handleClick(int mouseX, int mouseY) {
		if (activeRoom == null || activeTool == null)
			return;
		int x = mouseX / CELL_SIZE;
		int y = mouseY / CELL_SIZE;
		if (x < 0 || y < 0 || x >= activeRoom.getWidth() || y >= activeRoom.getHeight())
			return;

		EditorCell cell = activeRoom.getCell(x, y);
		if (activeTool.getKind() == Tool.Kind.GROUND)
			applyGroundTool(cell, activeTool.getGroundType());
		else
			applyCharacterTool(cell, activeTool.getCharacterType());
		repaint();
	}

	private void applyGroundTool(EditorCell cell, GroundType type) {
		GroundType previous = cell.getGroundType();
		cell.setGroundType(type);
		boolean confirmed = true;
		if (type == GroundType.DOOR)
			confirmed = EditorDialogs.configureDoor(this, world, activeRoom, cell);
		else if (type == GroundType.ITEM_PICKUP)
			confirmed = EditorDialogs.configureItemPickup(this, cell);
		if (!confirmed)
			cell.setGroundType(previous);
	}

	private void applyCharacterTool(EditorCell cell, CharacterType type) {
		CharacterType previous = cell.getCharacterType();
		cell.setCharacterType(type);
		boolean confirmed = true;
		if (type == CharacterType.NPC)
			confirmed = EditorDialogs.configureNpc(this, cell);
		else if (type == CharacterType.DIALOGUE)
			confirmed = EditorDialogs.configureDialogue(this, cell);
		if (!confirmed)
			cell.setCharacterType(previous);
	}
}
