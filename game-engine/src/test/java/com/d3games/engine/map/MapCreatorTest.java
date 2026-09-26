package com.d3games.engine.map;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;

import com.d3games.engine.GameManager;

public class MapCreatorTest {

	@Test
	public void loadsMultipleRoomFilesAndResolvesNumericAndNamedDoorTargets() throws IOException {
		File tempDir = Files.createTempDirectory("mapcreatortest").toFile();
		tempDir.deleteOnExit();

		GameManager gameManager = GameManager.getInstance();
		int firstId = gameManager.getAll().size();
		int secondId = firstId + 1;

		writeFile(new File(tempDir, "first.txt"),
				"m" + firstId + ",2,2",
				"s" + firstId + ",0,0",
				"s" + firstId + ",1,0",
				"d" + firstId + ",0,1," + secondId + ",1,0");
		writeFile(new File(tempDir, "second.txt"),
				"m" + secondId + ",2,2",
				"s" + secondId + ",0,0",
				"s" + secondId + ",1,0",
				"d" + secondId + ",0,1,first,0,0");
		File manifest = new File(tempDir, "manifest.txt");
		writeFile(manifest,
				"first=first.txt",
				"second=second.txt");

		MapCreator.getInstance().generateWorld(manifest);

		GameMap firstMap = gameManager.get(firstId);
		GameMap secondMap = gameManager.get(secondId);

		assertEquals("first", firstMap.getRoomName());
		assertEquals("second", secondMap.getRoomName());
		assertEquals(2, firstMap.getWidth());
		assertEquals(2, secondMap.getWidth());

		Door doorInFirst = (Door) firstMap.get(0, 1);
		Unit resolvedFromFirst = doorInFirst.approach();
		assertEquals(secondMap, resolvedFromFirst.getMap());

		Door doorInSecond = (Door) secondMap.get(0, 1);
		Unit resolvedFromSecond = doorInSecond.approach();
		assertEquals(firstMap, resolvedFromSecond.getMap());
	}

	private void writeFile(File file, String... lines) throws IOException {
		try (FileWriter writer = new FileWriter(file)) {
			for (String line : lines) {
				writer.write(line);
				writer.write(System.lineSeparator());
			}
		}
	}
}
