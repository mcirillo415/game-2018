package com.d3games.engine.item;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class KeyRing {
	private final Set<String> keys = new HashSet<String>();

	public void add(String keyName) {
		keys.add(keyName);
	}

	public boolean has(String keyName) {
		return keys.contains(keyName);
	}

	public Set<String> getAll() {
		return Collections.unmodifiableSet(keys);
	}
}
