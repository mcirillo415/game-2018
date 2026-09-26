package com.d3games.engine.editor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RoomFileWriter {

	public void saveWorld(EditorWorld world, File manifestFile) throws IOException {
		File dir = manifestFile.getParentFile();
		StringBuilder manifest = new StringBuilder();
		for (EditorRoom room : world.getRooms()) {
			String fileName = room.getName() + ".txt";
			saveRoom(room, new File(dir, fileName));
			manifest.append(room.getName()).append("=").append(fileName).append(System.lineSeparator());
		}
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(manifestFile, false))) {
			writer.write(manifest.toString());
		}
	}

	public void saveRoom(EditorRoom room, File roomFile) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(roomFile, false))) {
			writer.write("m" + room.getId() + "," + room.getWidth() + "," + room.getHeight());
			writer.newLine();
			for (int x = 0; x < room.getWidth(); x++) {
				for (int y = 0; y < room.getHeight(); y++) {
					EditorCell cell = room.getCell(x, y);
					writeGroundLine(writer, room.getId(), x, y, cell);
					writeCharacterLine(writer, room.getId(), x, y, cell);
				}
			}
		}
	}

	private void writeGroundLine(BufferedWriter writer, int mapId, int x, int y, EditorCell cell) throws IOException {
		String prefix = "," + mapId + "," + x + "," + y;
		switch (cell.getGroundType()) {
			case SPACE:
				write(writer, "s" + prefix);
				break;
			case OBSTACLE:
				write(writer, "o" + prefix);
				break;
			case WILD_GRASS:
				write(writer, "w" + prefix);
				break;
			case ITEM_PICKUP:
				write(writer, "i" + prefix + "," + cell.getPickupKind() + "," + cell.getPickupAmount());
				break;
			case DOOR:
				StringBuilder line = new StringBuilder("d" + prefix + ","
						+ cell.getTargetRoom() + "," + cell.getTargetX() + "," + cell.getTargetY());
				if (cell.getLockType() == EditorCell.LockType.KEY) {
					line.append(",KEY,").append(cell.getLockKeyName());
				} else if (cell.getLockType() == EditorCell.LockType.NPC) {
					line.append(",NPC,").append(cell.getLockNpcRoom()).append(",")
							.append(cell.getLockNpcX()).append(",").append(cell.getLockNpcY());
				}
				write(writer, line.toString());
				break;
			case EMPTY:
			default:
				break;
		}
	}

	private void writeCharacterLine(BufferedWriter writer, int mapId, int x, int y, EditorCell cell) throws IOException {
		String prefix = "," + mapId + "," + x + "," + y;
		switch (cell.getCharacterType()) {
			case PLAYER_START:
				write(writer, "p" + prefix);
				break;
			case NPC:
				write(writer, "n" + prefix + "," + cell.isNpcInitiatesBattle());
				break;
			case SHOP:
				write(writer, "k" + prefix);
				break;
			case HEALER:
				write(writer, "h" + prefix);
				break;
			case DIALOGUE:
				write(writer, "v" + prefix + "," + cell.getDialogueText());
				break;
			case NONE:
			default:
				break;
		}
	}

	private void write(BufferedWriter writer, String line) throws IOException {
		writer.write(line);
		writer.newLine();
	}
}
