package com.d3games.engine.map;

public class MapCoordinate {
	public final int mapId;
	public final int x;
	public final int y;

	public MapCoordinate(int mapId, int x, int y) {
		this.mapId = mapId;
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof MapCoordinate))
			return false;
		MapCoordinate other = (MapCoordinate) obj;
		return mapId == other.mapId && x == other.x && y == other.y;
	}

	@Override
	public int hashCode() {
		return (mapId * 31 + x) * 31 + y;
	}
}
