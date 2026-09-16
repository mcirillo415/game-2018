package com.d3games.engine.battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Party {
	public static final int MAX_SIZE = 6;

	private final List<Combatant> members = new ArrayList<Combatant>();
	private int activeIndex = 0;

	public Party(Combatant starter) {
		members.add(Objects.requireNonNull(starter, "A party needs a starting combatant"));
	}

	public Combatant getActive() {
		return members.get(activeIndex);
	}

	public List<Combatant> getMembers() {
		return Collections.unmodifiableList(members);
	}

	public boolean isFull() {
		return members.size() >= MAX_SIZE;
	}

	public void add(Combatant member) {
		if (isFull())
			throw new IllegalStateException("The party is full");
		members.add(Objects.requireNonNull(member, "Cannot add a null combatant"));
	}

	public void setActive(Combatant member) {
		int index = members.indexOf(member);
		if (index < 0)
			throw new IllegalArgumentException("That combatant is not in the party");
		setActiveIndex(index);
	}

	public void setActiveIndex(int index) {
		if (index < 0 || index >= members.size())
			throw new IndexOutOfBoundsException("No party member at index " + index);
		if (members.get(index).isFainted())
			throw new IllegalArgumentException("A fainted party member cannot battle");
		activeIndex = index;
	}

	public boolean allFainted() {
		for (Combatant member : members)
			if (!member.isFainted())
				return false;
		return true;
	}

	public Combatant nextAlive(Combatant after) {
		int startIndex = members.indexOf(after);
		if (startIndex < 0)
			startIndex = 0;
		for (int offset = 1; offset <= members.size(); offset++) {
			Combatant candidate = members.get((startIndex + offset) % members.size());
			if (!candidate.isFainted())
				return candidate;
		}
		return null;
	}

	public void healAll() {
		for (Combatant member : members)
			member.heal(member.getMaximumHealth());
	}
}
