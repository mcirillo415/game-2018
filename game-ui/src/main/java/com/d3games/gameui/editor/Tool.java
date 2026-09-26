package com.d3games.gameui.editor;

import com.d3games.engine.editor.EditorCell.CharacterType;
import com.d3games.engine.editor.EditorCell.GroundType;

/** One palette entry: either paints a ground type or a character overlay onto the clicked cell. */
public class Tool {

	public enum Kind {
		GROUND, CHARACTER
	}

	private final Kind kind;
	private final GroundType groundType;
	private final CharacterType characterType;
	private final String label;

	private Tool(Kind kind, GroundType groundType, CharacterType characterType, String label) {
		this.kind = kind;
		this.groundType = groundType;
		this.characterType = characterType;
		this.label = label;
	}

	public static Tool ground(GroundType groundType, String label) {
		return new Tool(Kind.GROUND, groundType, null, label);
	}

	public static Tool character(CharacterType characterType, String label) {
		return new Tool(Kind.CHARACTER, null, characterType, label);
	}

	public Kind getKind() {
		return kind;
	}

	public GroundType getGroundType() {
		return groundType;
	}

	public CharacterType getCharacterType() {
		return characterType;
	}

	@Override
	public String toString() {
		return label;
	}
}
