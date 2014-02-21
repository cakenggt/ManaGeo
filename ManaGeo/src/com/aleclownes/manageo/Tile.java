package com.aleclownes.manageo;

import java.io.Serializable;
import java.util.Random;

import Structures.Forest;

public class Tile implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2829609460471113477L;
	public Structure aboveGround;
	public Rock belowGround;
	
	public Tile(Coord coord) {
		aboveGround = new Forest();
		Random rng = new Random(coord.x+coord.y);
		RockType rockType = RockType.values()[(int) (rng.nextDouble()*RockType.values().length)];
		OreType oreType = OreType.values()[(int) (rng.nextDouble()*OreType.values().length)];
		double density = rng.nextDouble();
		double depth = 1.0-(rng.nextDouble()*0.1);
		belowGround = new Rock(rockType, new Ore(oreType, density, depth));
	}
}