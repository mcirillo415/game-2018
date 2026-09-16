package com.d3games.engine.sound;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class SoundPlayerTest {

	@Test
	public void everyEffectHasAtLeastOneNote() {
		for (SoundEffect effect : SoundEffect.values())
			assertFalse(effect.getNotes().isEmpty());
	}

	@Test
	public void playingEveryEffectDoesNotThrow() {
		for (SoundEffect effect : SoundEffect.values())
			SoundPlayer.play(effect);
	}

	@Test
	public void playingTheSameEffectTwiceDoesNotThrow() {
		SoundPlayer.play(SoundEffect.MENU_MOVE);
		SoundPlayer.play(SoundEffect.MENU_MOVE);
	}
}
