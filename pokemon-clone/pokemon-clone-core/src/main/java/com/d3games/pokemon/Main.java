package com.d3games.pokemon;

import java.io.File;

import com.d3games.gameui.GameGUI;

public class Main {

	public static void main(String[] args) {
		boolean skipTitleScreen = shouldSkipTitleScreen(args);
		new GameGUI(new File(Main.class.getResource("world3.txt").getFile()), skipTitleScreen);
	}

	private static boolean shouldSkipTitleScreen(String[] args) {
		if (Boolean.getBoolean("game.skipTitle"))
			return true;
		for (String arg : args) {
			if ("--skip-title".equals(arg) || "-dev".equals(arg))
				return true;
		}
		return false;
	}

}
