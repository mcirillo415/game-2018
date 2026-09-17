package com.d3games.gameui.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import com.d3games.engine.editor.EditorCell.CharacterType;
import com.d3games.engine.editor.EditorCell.GroundType;
import com.d3games.engine.editor.EditorRoom;
import com.d3games.engine.editor.EditorWorld;
import com.d3games.engine.editor.RoomFileReader;
import com.d3games.engine.editor.RoomFileWriter;

public class MapEditorFrame extends JFrame {

	private EditorWorld world = new EditorWorld();
	private File currentManifest;

	private final DefaultListModel<EditorRoom> roomListModel = new DefaultListModel<>();
	private final JList<EditorRoom> roomList = new JList<>(roomListModel);
	private final MapEditorPanel mapPanel = new MapEditorPanel();

	public MapEditorFrame() {
		super("Map Editor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mapPanel.setWorld(world);

		setJMenuBar(buildMenuBar());
		add(buildRoomListPanel(), BorderLayout.WEST);
		add(buildPalettePanel(), BorderLayout.EAST);
		add(new JScrollPane(mapPanel), BorderLayout.CENTER);

		setSize(1000, 700);
		setLocationRelativeTo(null);
	}

	private JMenuBar buildMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");

		JMenuItem newWorld = new JMenuItem("New World");
		newWorld.addActionListener(this::onNewWorld);
		JMenuItem open = new JMenuItem("Open World...");
		open.addActionListener(this::onOpen);
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(this::onSave);
		JMenuItem saveAs = new JMenuItem("Save As...");
		saveAs.addActionListener(this::onSaveAs);

		fileMenu.add(newWorld);
		fileMenu.add(open);
		fileMenu.add(save);
		fileMenu.add(saveAs);
		menuBar.add(fileMenu);
		return menuBar;
	}

	private JPanel buildRoomListPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Rooms"));
		panel.setPreferredSize(new java.awt.Dimension(180, 0));

		roomList.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting())
				mapPanel.setActiveRoom(roomList.getSelectedValue());
		});
		panel.add(new JScrollPane(roomList), BorderLayout.CENTER);

		JPanel buttons = new JPanel(new GridLayout(2, 1));
		JButton addButton = new JButton("+ New Room");
		addButton.addActionListener(this::onNewRoom);
		JButton deleteButton = new JButton("Delete Room");
		deleteButton.addActionListener(this::onDeleteRoom);
		buttons.add(addButton);
		buttons.add(deleteButton);
		panel.add(buttons, BorderLayout.SOUTH);

		return panel;
	}

	private JPanel buildPalettePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder("Palette"));
		panel.setPreferredSize(new java.awt.Dimension(180, 0));

		ButtonGroup group = new ButtonGroup();

		panel.add(new JLabel("Ground"));
		addTool(panel, group, Tool.ground(GroundType.EMPTY, "Erase Ground"), true);
		addTool(panel, group, Tool.ground(GroundType.SPACE, "Space"), false);
		addTool(panel, group, Tool.ground(GroundType.OBSTACLE, "Obstacle"), false);
		addTool(panel, group, Tool.ground(GroundType.WILD_GRASS, "Wild Grass"), false);
		addTool(panel, group, Tool.ground(GroundType.ITEM_PICKUP, "Item Pickup..."), false);
		addTool(panel, group, Tool.ground(GroundType.DOOR, "Door..."), false);

		panel.add(Box.createVerticalStrut(10));
		panel.add(new JLabel("Character"));
		addTool(panel, group, Tool.character(CharacterType.NONE, "Erase Character"), false);
		addTool(panel, group, Tool.character(CharacterType.PLAYER_START, "Player Start"), false);
		addTool(panel, group, Tool.character(CharacterType.NPC, "NPC (Battle)..."), false);
		addTool(panel, group, Tool.character(CharacterType.SHOP, "Shop NPC"), false);
		addTool(panel, group, Tool.character(CharacterType.DIALOGUE, "Dialogue NPC..."), false);
		addTool(panel, group, Tool.character(CharacterType.HEALER, "Healer NPC"), false);

		panel.add(Box.createVerticalGlue());
		return panel;
	}

	private void addTool(JPanel panel, ButtonGroup group, Tool tool, boolean selected) {
		JToggleButton button = new JToggleButton(tool.toString(), selected);
		button.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		button.addActionListener(e -> mapPanel.setActiveTool(tool));
		group.add(button);
		panel.add(button);
		if (selected)
			mapPanel.setActiveTool(tool);
	}

	private void onNewWorld(ActionEvent e) {
		int confirm = JOptionPane.showConfirmDialog(this, "Discard the current world and start a new one?",
				"New World", JOptionPane.OK_CANCEL_OPTION);
		if (confirm != JOptionPane.OK_OPTION)
			return;
		world = new EditorWorld();
		currentManifest = null;
		mapPanel.setWorld(world);
		mapPanel.setActiveRoom(null);
		roomListModel.clear();
	}

	private void onOpen(ActionEvent e) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Open manifest.txt");
		if (currentManifest != null)
			chooser.setCurrentDirectory(currentManifest.getParentFile());
		if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
			return;

		File manifestFile = chooser.getSelectedFile();
		try {
			world = new RoomFileReader().loadWorld(manifestFile);
			currentManifest = manifestFile;
			mapPanel.setWorld(world);
			roomListModel.clear();
			for (EditorRoom room : world.getRooms())
				roomListModel.addElement(room);
			if (!roomListModel.isEmpty()) {
				roomList.setSelectedIndex(0);
			} else {
				mapPanel.setActiveRoom(null);
			}
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "Failed to open world: " + ex.getMessage(), "Open failed",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void onSave(ActionEvent e) {
		if (currentManifest == null) {
			onSaveAs(e);
			return;
		}
		saveTo(currentManifest);
	}

	private void onSaveAs(ActionEvent e) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Save manifest.txt as");
		if (currentManifest != null)
			chooser.setSelectedFile(currentManifest);
		else
			chooser.setSelectedFile(new File("manifest.txt"));
		if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
			return;
		saveTo(chooser.getSelectedFile());
	}

	private void saveTo(File manifestFile) {
		try {
			new RoomFileWriter().saveWorld(world, manifestFile);
			currentManifest = manifestFile;
			JOptionPane.showMessageDialog(this, "Saved to " + manifestFile.getAbsolutePath());
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "Failed to save world: " + ex.getMessage(), "Save failed",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void onNewRoom(ActionEvent e) {
		int nextId = 0;
		for (EditorRoom room : world.getRooms())
			nextId = Math.max(nextId, room.getId() + 1);

		JTextField nameField = new JTextField("room" + nextId);
		JTextField idField = new JTextField(String.valueOf(nextId));
		JTextField widthField = new JTextField("5");
		JTextField heightField = new JTextField("5");

		JPanel panel = new JPanel(new GridLayout(0, 2, 4, 4));
		panel.add(new JLabel("Name:"));
		panel.add(nameField);
		panel.add(new JLabel("Id:"));
		panel.add(idField);
		panel.add(new JLabel("Width:"));
		panel.add(widthField);
		panel.add(new JLabel("Height:"));
		panel.add(heightField);

		int result = JOptionPane.showConfirmDialog(this, panel, "New Room", JOptionPane.OK_CANCEL_OPTION);
		if (result != JOptionPane.OK_OPTION)
			return;

		try {
			String name = nameField.getText().trim();
			if (name.isEmpty() || world.getRoomByName(name) != null) {
				JOptionPane.showMessageDialog(this, "Room name must be non-empty and unique.", "Invalid room",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			int id = Integer.parseInt(idField.getText().trim());
			for (EditorRoom room : world.getRooms()) {
				if (room.getId() == id) {
					JOptionPane.showMessageDialog(this, "Room id " + id + " is already in use.", "Invalid room",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			int width = Integer.parseInt(widthField.getText().trim());
			int height = Integer.parseInt(heightField.getText().trim());
			if (width <= 0 || height <= 0) {
				JOptionPane.showMessageDialog(this, "Width and height must be positive.", "Invalid room",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			EditorRoom room = new EditorRoom(id, name, width, height);
			world.addRoom(room);
			roomListModel.addElement(room);
			roomList.setSelectedValue(room, true);
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Id, width and height must be numbers.", "Invalid room",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void onDeleteRoom(ActionEvent e) {
		EditorRoom selected = roomList.getSelectedValue();
		if (selected == null)
			return;
		int confirm = JOptionPane.showConfirmDialog(this, "Delete room '" + selected.getName() + "'?",
				"Delete Room", JOptionPane.OK_CANCEL_OPTION);
		if (confirm != JOptionPane.OK_OPTION)
			return;
		world.removeRoom(selected);
		roomListModel.removeElement(selected);
		mapPanel.setActiveRoom(roomList.getSelectedValue());
	}
}
