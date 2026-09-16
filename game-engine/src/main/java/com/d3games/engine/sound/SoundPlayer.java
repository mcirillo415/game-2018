package com.d3games.engine.sound;

import java.io.ByteArrayOutputStream;
import java.util.EnumMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

public class SoundPlayer {
	private static final float SAMPLE_RATE = 44100f;
	private static final double AMPLITUDE = Short.MAX_VALUE * 0.3;
	private static final AudioFormat FORMAT = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);

	private static final Map<SoundEffect, byte[]> CACHE = new EnumMap<SoundEffect, byte[]>(SoundEffect.class);

	private SoundPlayer() {
	}

	public static void play(SoundEffect effect) {
		try {
			byte[] pcm = CACHE.computeIfAbsent(effect, SoundPlayer::synthesize);
			Clip clip = AudioSystem.getClip();
			clip.open(FORMAT, pcm, 0, pcm.length);
			clip.start();
		} catch (LineUnavailableException | IllegalArgumentException ex) {
			// No audio device available (or unsupported format on this system); sound is
			// non-essential to gameplay, so fail silently rather than interrupting the game.
		}
	}

	private static byte[] synthesize(SoundEffect effect) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		for (Note note : effect.getNotes()) {
			int sampleCount = (int) (SAMPLE_RATE * note.getDurationMs() / 1000.0);
			for (int i = 0; i < sampleCount; i++) {
				double time = i / SAMPLE_RATE;
				double wave = Math.signum(Math.sin(2 * Math.PI * note.getFrequencyHz() * time));
				double envelope = 1.0 - (double) i / sampleCount;
				short sample = (short) (wave * AMPLITUDE * envelope);
				buffer.write(sample & 0xFF);
				buffer.write((sample >> 8) & 0xFF);
			}
		}
		return buffer.toByteArray();
	}
}
