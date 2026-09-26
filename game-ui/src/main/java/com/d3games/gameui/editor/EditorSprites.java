package com.d3games.gameui.editor;

import java.awt.Image;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.ImageIcon;

import com.d3games.engine.editor.EditorCell.CharacterType;
import com.d3games.engine.editor.EditorCell.GroundType;

/** Loads the same sprite PNGs the live game uses (via com.d3games.gameui's resources), so the editor renders identically. */
public class EditorSprites {

	private static final Map<GroundType, Image> GROUND_IMAGES = new EnumMap<>(GroundType.class);
	private static final Map<CharacterType, Image> CHARACTER_IMAGES = new EnumMap<>(CharacterType.class);

	static {
		GROUND_IMAGES.put(GroundType.SPACE, load("/com/d3games/gameui/grass.png"));
		GROUND_IMAGES.put(GroundType.OBSTACLE, load("/com/d3games/gameui/brick.png"));
		GROUND_IMAGES.put(GroundType.WILD_GRASS, load("/com/d3games/gameui/wildGrass.png"));
		GROUND_IMAGES.put(GroundType.ITEM_PICKUP, load("/com/d3games/gameui/itemPickup.png"));
		GROUND_IMAGES.put(GroundType.DOOR, load("/com/d3games/gameui/door.png"));

		CHARACTER_IMAGES.put(CharacterType.NPC, load("/com/d3games/gameui/characters/npc/dog.png"));
		CHARACTER_IMAGES.put(CharacterType.SHOP, load("/com/d3games/gameui/characters/npc/shopkeeper.png"));
		CHARACTER_IMAGES.put(CharacterType.DIALOGUE, load("/com/d3games/gameui/characters/npc/villager.png"));
		CHARACTER_IMAGES.put(CharacterType.HEALER, load("/com/d3games/gameui/characters/npc/healer.png"));
		CHARACTER_IMAGES.put(CharacterType.PLAYER_START, load("/com/d3games/gameui/characters/player/playerFront.png"));
	}

	private EditorSprites() {
	}

	private static Image load(String resourcePath) {
		URL url = EditorSprites.class.getResource(resourcePath);
		if (url == null)
			return null;
		return new ImageIcon(url).getImage();
	}

	public static Image getGroundImage(GroundType groundType) {
		return GROUND_IMAGES.get(groundType);
	}

	public static Image getCharacterImage(CharacterType characterType) {
		return CHARACTER_IMAGES.get(characterType);
	}
}
