package com.d3games.engine.editor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EditorWorld {
	private final List<EditorRoom> rooms = new ArrayList<EditorRoom>();
	private final Map<String, EditorRoom> roomsByName = new LinkedHashMap<String, EditorRoom>();

	public void addRoom(EditorRoom room) {
		rooms.add(room);
		roomsByName.put(room.getName(), room);
	}

	public void removeRoom(EditorRoom room) {
		rooms.remove(room);
		roomsByName.remove(room.getName());
	}

	public List<EditorRoom> getRooms() {
		return rooms;
	}

	public EditorRoom getRoomByName(String name) {
		return roomsByName.get(name);
	}
}
