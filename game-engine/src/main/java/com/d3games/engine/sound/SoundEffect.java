package com.d3games.engine.sound;

import java.util.Arrays;
import java.util.List;

public enum SoundEffect {
	MENU_MOVE(new Note(220, 30)),
	MENU_SELECT(new Note(440, 30), new Note(660, 40)),
	ATTACK_HIT(new Note(150, 70)),
	SUPER_EFFECTIVE(new Note(523, 60), new Note(659, 60), new Note(784, 80)),
	NOT_VERY_EFFECTIVE(new Note(300, 60), new Note(220, 90)),
	CATCH_SUCCESS(new Note(392, 60), new Note(523, 60), new Note(659, 60), new Note(784, 100)),
	CATCH_FAIL(new Note(250, 80), new Note(180, 120)),
	LEVEL_UP(new Note(523, 70), new Note(659, 70), new Note(784, 70), new Note(1047, 120)),
	FAINT(new Note(400, 80), new Note(300, 80), new Note(200, 120)),
	GAME_OVER(new Note(392, 120), new Note(330, 120), new Note(262, 120), new Note(196, 220)),
	ITEM_PICKUP(new Note(587, 50), new Note(880, 90)),
	PURCHASE(new Note(440, 40), new Note(554, 40), new Note(659, 70));

	private final List<Note> notes;

	SoundEffect(Note... notes) {
		this.notes = Arrays.asList(notes);
	}

	public List<Note> getNotes() {
		return notes;
	}
}
