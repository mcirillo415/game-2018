package com.d3games.engine.sound;

public class Note {
	private final double frequencyHz;
	private final int durationMs;

	public Note(double frequencyHz, int durationMs) {
		this.frequencyHz = frequencyHz;
		this.durationMs = durationMs;
	}

	public double getFrequencyHz() {
		return frequencyHz;
	}

	public int getDurationMs() {
		return durationMs;
	}
}
