package com.d3games.engine.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoomFileReader {

	public EditorWorld loadWorld(File manifestFile) throws IOException {
		EditorWorld world = new EditorWorld();
		List<String[]> entries = new ArrayList<String[]>();
		try (BufferedReader reader = new BufferedReader(new FileReader(manifestFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.indexOf('=') < 0)
					continue;
				String[] parts = line.split("=", 2);
				entries.add(new String[] { parts[0].trim(), parts[1].trim() });
			}
		}
		for (String[] entry : entries) {
			File roomFile = new File(manifestFile.getParentFile(), entry[1]);
			world.addRoom(loadRoom(roomFile, entry[0]));
		}
		return world;
	}

	public EditorRoom loadRoom(File roomFile, String roomName) throws IOException {
		EditorRoom[] roomHolder = new EditorRoom[1];
		try (BufferedReader reader = new BufferedReader(new FileReader(roomFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty())
					continue;
				if (line.indexOf(',') >= 0)
					parseCommaLine(line, roomName, roomHolder, roomFile);
				else
					parseCompactLine(line, roomName, roomHolder, roomFile);
			}
		}
		if (roomHolder[0] == null)
			throw new IllegalStateException("Room file missing its 'm' line: " + roomFile);
		return roomHolder[0];
	}

	private void parseCommaLine(String line, String roomName, EditorRoom[] roomHolder, File roomFile) {
		String[] values = line.split(",");
		char type = values[0].charAt(0);
		int x;
		int y;
		int valueOffset;
		if (values[0].length() > 1) {
			x = Integer.parseInt(values[1]);
			y = Integer.parseInt(values[2]);
			valueOffset = 3;
		} else {
			x = Integer.parseInt(values[2]);
			y = Integer.parseInt(values[3]);
			valueOffset = 4;
		}

		if (type == 'm') {
			int id = values[0].length() > 1
					? Integer.parseInt(values[0].substring(1))
					: Integer.parseInt(values[1]);
			roomHolder[0] = new EditorRoom(id, roomName, x, y);
			return;
		}
		requireRoom(roomHolder, roomFile);

		EditorCell cell = roomHolder[0].getCell(x, y);
		if (type == 's') {
			cell.setGroundType(EditorCell.GroundType.SPACE);
		} else if (type == 'o') {
			cell.setGroundType(EditorCell.GroundType.OBSTACLE);
		} else if (type == 'w') {
			cell.setGroundType(EditorCell.GroundType.WILD_GRASS);
		} else if (type == 'i') {
			cell.setGroundType(EditorCell.GroundType.ITEM_PICKUP);
			cell.setPickupKind(values[valueOffset]);
			cell.setPickupAmount(Integer.parseInt(values[valueOffset + 1]));
		} else if (type == 'd') {
			cell.setGroundType(EditorCell.GroundType.DOOR);
			cell.setTargetRoom(values[valueOffset]);
			cell.setTargetX(Integer.parseInt(values[valueOffset + 1]));
			cell.setTargetY(Integer.parseInt(values[valueOffset + 2]));
			int lockOffset = valueOffset + 3;
			if (values.length > lockOffset) {
				String lockKind = values[lockOffset];
				if ("KEY".equals(lockKind)) {
					cell.setLockType(EditorCell.LockType.KEY);
					cell.setLockKeyName(values[lockOffset + 1]);
				} else if ("NPC".equals(lockKind)) {
					cell.setLockType(EditorCell.LockType.NPC);
					cell.setLockNpcRoom(values[lockOffset + 1]);
					cell.setLockNpcX(Integer.parseInt(values[lockOffset + 2]));
					cell.setLockNpcY(Integer.parseInt(values[lockOffset + 3]));
				}
			}
		} else if (type == 'p') {
			cell.setCharacterType(EditorCell.CharacterType.PLAYER_START);
		} else if (type == 'n') {
			cell.setCharacterType(EditorCell.CharacterType.NPC);
			boolean initiatesBattle = values.length <= valueOffset
					|| Boolean.parseBoolean(values[valueOffset]);
			cell.setNpcInitiatesBattle(initiatesBattle);
		} else if (type == 'k') {
			cell.setCharacterType(EditorCell.CharacterType.SHOP);
		} else if (type == 'h') {
			cell.setCharacterType(EditorCell.CharacterType.HEALER);
		} else if (type == 'v') {
			cell.setCharacterType(EditorCell.CharacterType.DIALOGUE);
			cell.setDialogueText(String.join(",", Arrays.copyOfRange(values, valueOffset, values.length)));
		}
	}

	/** Mirrors MapCreator's legacy single-digit compact format, still present in some hand-authored room files. */
	private void parseCompactLine(String line, String roomName, EditorRoom[] roomHolder, File roomFile) {
		int x = Character.getNumericValue(line.charAt(2));
		int y = Character.getNumericValue(line.charAt(3));
		char type = line.charAt(0);

		if (type == 'm') {
			int id = Character.getNumericValue(line.charAt(1));
			roomHolder[0] = new EditorRoom(id, roomName, x, y);
			return;
		}
		requireRoom(roomHolder, roomFile);

		EditorCell cell = roomHolder[0].getCell(x, y);
		if (type == 's') {
			cell.setGroundType(EditorCell.GroundType.SPACE);
		} else if (type == 'o') {
			cell.setGroundType(EditorCell.GroundType.OBSTACLE);
		} else if (type == 'd') {
			cell.setGroundType(EditorCell.GroundType.DOOR);
			cell.setTargetRoom(String.valueOf(Character.getNumericValue(line.charAt(4))));
			cell.setTargetX(Character.getNumericValue(line.charAt(5)));
			cell.setTargetY(Character.getNumericValue(line.charAt(6)));
		} else if (type == 'p') {
			cell.setCharacterType(EditorCell.CharacterType.PLAYER_START);
		} else if (type == 'n') {
			cell.setCharacterType(EditorCell.CharacterType.NPC);
			cell.setNpcInitiatesBattle(true);
		}
	}

	private void requireRoom(EditorRoom[] roomHolder, File roomFile) {
		if (roomHolder[0] == null)
			throw new IllegalStateException("Room file missing its 'm' line: " + roomFile);
	}
}
