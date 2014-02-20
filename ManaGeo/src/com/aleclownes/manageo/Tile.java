package com.aleclownes.manageo;

import java.io.Serializable;

import Structures.Forest;

public class Tile implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2829609460471113477L;
	public Structure aboveGround;
	public Rock belowGround;
	
	public Tile() {
		aboveGround = new Forest();
		//This line will change
		belowGround = new Rock(RockType.GRANITE, new Ore(OreType.IRON, 1, 0.9));
	}
}