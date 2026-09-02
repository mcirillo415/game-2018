package com.d3games.gameui;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class KeyBindings {
	private static final String CONFIG_RESOURCE = "keybindings.properties";
	private static KeyBindings instance;

	private final Map<Integer, GameAction> keyCodeToAction = new HashMap<>();

	private KeyBindings() {
		Properties defaults = new Properties();
		defaults.setProperty("move.up", "UP");
		defaults.setProperty("move.down", "DOWN");
		defaults.setProperty("move.left", "LEFT");
		defaults.setProperty("move.right", "RIGHT");
		defaults.setProperty("confirm", "SPACE");
		defaults.setProperty("menu", "P");
		defaults.setProperty("battle", "B");

		Properties properties = new Properties(defaults);
		try (InputStream in = KeyBindings.class.getResourceAsStream(CONFIG_RESOURCE)) {
			if (in != null)
				properties.load(in);
		} catch (IOException e) {
			System.out.println("Unable to load " + CONFIG_RESOURCE + ", using default key bindings: " + e.getMessage());
		}

		bind(GameAction.MOVE_UP, properties.getProperty("move.up"));
		bind(GameAction.MOVE_DOWN, properties.getProperty("move.down"));
		bind(GameAction.MOVE_LEFT, properties.getProperty("move.left"));
		bind(GameAction.MOVE_RIGHT, properties.getProperty("move.right"));
		bind(GameAction.CONFIRM, properties.getProperty("confirm"));
		bind(GameAction.MENU, properties.getProperty("menu"));
		bind(GameAction.BATTLE, properties.getProperty("battle"));
	}

	public static KeyBindings getInstance() {
		if (instance == null)
			instance = new KeyBindings();
		return instance;
	}

	public GameAction resolve(int keyCode) {
		return keyCodeToAction.get(keyCode);
	}

	private void bind(GameAction action, String keyName) {
		keyCodeToAction.put(resolveKeyCode(keyName), action);
	}

	private int resolveKeyCode(String keyName) {
		try {
			return KeyEvent.class.getField("VK_" + keyName.trim().toUpperCase()).getInt(null);
		} catch (ReflectiveOperationException e) {
			throw new IllegalArgumentException("Unrecognized key name in " + CONFIG_RESOURCE + ": " + keyName, e);
		}
	}
}
