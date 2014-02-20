package com.aleclownes.manageo;

import java.util.HashMap;
import java.util.Map;

/**This is the static singleton class which holds the map of coords to tiles
 * @author lownes
 *
 */
public class TileHolder {
	private static final Map<Coord, Tile> tiles;

	static {
		tiles = new HashMap<Coord, Tile>();
	}

	public static Map<Coord, Tile> getTiles() {return tiles;}
}
