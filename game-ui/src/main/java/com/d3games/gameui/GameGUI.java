package com.d3games.gameui;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import com.d3games.engine.map.MapCreator;
import com.d3games.engine.menu.MenuCreator;

public class GameGUI extends JFrame {
	private static final long serialVersionUID = 1L;

	public GameGUI(File world) {
		this(world, false);
	}

	public GameGUI(File world, boolean skipTitleScreen) {
		MapCreator.getInstance();
		MenuCreator.getInstance();
		try {
			MapCreator.getInstance().generateWorld(world);
			MenuCreator.getInstance().generateMenu();
			MenuCreator.getInstance().generateBattleMenu();
			add(new GameScreen(skipTitleScreen));
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setTitle("GAME TITLE");
			setResizable(false);
			pack();
			setLocationRelativeTo(null);
			setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
