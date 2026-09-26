package com.d3games.engine.map;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.d3games.engine.GameManager;

public class MapCreator {
	private static MapCreator mc;

	private MapCreator() {

	}

	public static MapCreator getInstance() {
		if (mc == null)
			mc = new MapCreator();
		return mc;
	}

	public void generateMap(File world) throws IOException {
		Map<String, Integer> roomNameToId = new HashMap<String, Integer>();
		try (Scanner scanner = new Scanner(world)) {
			while (scanner.hasNextLine())
				parseLine(scanner.nextLine(), world, null, roomNameToId);
		}
	}

	public void generateWorld(File manifestFile) throws IOException {
		Map<String, Integer> roomNameToId = new HashMap<String, Integer>();
		try (Scanner manifestScanner = new Scanner(manifestFile)) {
			while (manifestScanner.hasNextLine()) {
				String manifestLine = manifestScanner.nextLine().trim();
				if (manifestLine.isEmpty() || manifestLine.indexOf('=') < 0)
					continue;
				String[] parts = manifestLine.split("=", 2);
				String roomName = parts[0].trim();
				File roomFile = new File(manifestFile.getParentFile(), parts[1].trim());
				try (Scanner roomScanner = new Scanner(roomFile)) {
					while (roomScanner.hasNextLine())
						parseLine(roomScanner.nextLine(), roomFile, roomName, roomNameToId);
				}
			}
		}
	}

	private void parseLine(String line, File world, String roomName, Map<String, Integer> roomNameToId) {
		GameManager gameManager = GameManager.getInstance();
		if (line.indexOf(',') >= 0) {
			String[] values = line.split(",");
			char type = values[0].charAt(0);
			int mapRef;
			int x;
			int y;
			int valueOffset;
			if (values[0].length() > 1) {
				mapRef = Integer.parseInt(values[0].substring(1));
				x = Integer.parseInt(values[1]);
				y = Integer.parseInt(values[2]);
				valueOffset = 3;
			} else {
				mapRef = Integer.parseInt(values[1]);
				x = Integer.parseInt(values[2]);
				y = Integer.parseInt(values[3]);
				valueOffset = 4;
			}
			if (type == 'm') {
				GameMap newMap = new GameMap(world.getName(), mapRef, x, y);
				gameManager.add(newMap);
				if (roomName != null) {
					newMap.setRoomName(roomName);
					roomNameToId.put(roomName, mapRef);
				}
			} else if (type == 's') {
				gameManager.get(mapRef).add(new Space(), x, y);
			} else if (type == 'o') {
				gameManager.get(mapRef).add(new Obstacle(), x, y);
			} else if (type == 'w') {
				gameManager.get(mapRef).add(new WildGrass(), x, y);
			} else if (type == 'd') {
				int targetRef = resolveMapRef(values[valueOffset], roomNameToId);
				int targetX = Integer.parseInt(values[valueOffset + 1]);
				int targetY = Integer.parseInt(values[valueOffset + 2]);
				Door door = new Door(targetRef, targetX, targetY);
				int lockOffset = valueOffset + 3;
				if (values.length > lockOffset) {
					String lockKind = values[lockOffset];
					if ("KEY".equals(lockKind)) {
						door.setKeyLock(values[lockOffset + 1]);
					} else if ("NPC".equals(lockKind)) {
						int npcMapId = resolveMapRef(values[lockOffset + 1], roomNameToId);
						int npcX = Integer.parseInt(values[lockOffset + 2]);
						int npcY = Integer.parseInt(values[lockOffset + 3]);
						door.setNpcLock(new MapCoordinate(npcMapId, npcX, npcY));
					}
				}
				gameManager.get(mapRef).add(door, x, y);
			} else if (type == 'p') {
				gameManager.setPlayer(new Player(gameManager.get(mapRef).get(x, y)));
			} else if (type == 'n') {
				boolean initiatesBattle = values.length <= valueOffset
						|| Boolean.parseBoolean(values[valueOffset]);
				new NPC(gameManager.get(mapRef).get(x, y), initiatesBattle);
			} else if (type == 'i') {
				String kind = values[valueOffset];
				int amount = Integer.parseInt(values[valueOffset + 1]);
				gameManager.get(mapRef).add(new ItemPickup(kind, amount), x, y);
			} else if (type == 'k') {
				new ShopNPC(gameManager.get(mapRef).get(x, y));
			} else if (type == 'v') {
				String dialogue = String.join(",", Arrays.copyOfRange(values, valueOffset, values.length));
				new DialogueNPC(gameManager.get(mapRef).get(x, y), dialogue);
			} else if (type == 'h') {
				new HealerNPC(gameManager.get(mapRef).get(x, y));
			}
			return;
		}
		int mapRef = Character.getNumericValue(line.charAt(1));
		int x = Character.getNumericValue(line.charAt(2));
		int y = Character.getNumericValue(line.charAt(3));
		if (line.charAt(0) == 'm') {
			GameMap newMap = new GameMap(world.getName(), mapRef, x, y);
			gameManager.add(newMap);
			if (roomName != null) {
				newMap.setRoomName(roomName);
				roomNameToId.put(roomName, mapRef);
			}
		} else if (line.charAt(0) == 's') {
			gameManager.get(mapRef).add(new Space(), x, y);
		} else if (line.charAt(0) == 'o') {
			gameManager.get(mapRef).add(new Obstacle(), x, y);
		} else if (line.charAt(0) == 'd') {
			int targetRef = Character.getNumericValue(line.charAt(4));
			int targetX = Character.getNumericValue(line.charAt(5));
			int targetY = Character.getNumericValue(line.charAt(6));
			gameManager.get(mapRef).add(
					new Door(targetRef, targetX, targetY), x, y);
		} else if (line.charAt(0) == 'p') {
			gameManager.setPlayer(new Player(gameManager.get(mapRef).get(x,
					y)));
		} else if (line.charAt(0) == 'n') {
			new NPC(gameManager.get(mapRef).get(x, y));
		}
	}

	private int resolveMapRef(String token, Map<String, Integer> roomNameToId) {
		try {
			return Integer.parseInt(token);
		} catch (NumberFormatException ex) {
			Integer resolved = roomNameToId.get(token);
			if (resolved == null)
				throw new IllegalArgumentException("Unknown map reference: " + token);
			return resolved;
		}
	}

	public void saveMap() throws IOException {
		File outputFile = new File("world3.txt");
		FileWriter fw = new FileWriter(outputFile, false);
		BufferedWriter bw = new BufferedWriter(fw);
		for (GameMap map : GameManager.getInstance().getAll()) {
			bw.write("m" + map.getId() + map.getWidth() + map.getHeight());
			bw.newLine();
			for (int x = 0; x < map.getWidth(); x++) {
				for (int y = 0; y < map.getHeight(); y++) {
					if (map.get(x, y) != null) {
						bw.write(map.get(x, y).toFile());
						bw.newLine();
						if (map.get(x, y).hasCharacter()) {
							bw.write(map.get(x, y).getCharacter().toFile());
							bw.newLine();
						}
					}
				}
			}
		}
		fw.flush();
		bw.flush();
		fw.close();
		bw.close();
	}
}
