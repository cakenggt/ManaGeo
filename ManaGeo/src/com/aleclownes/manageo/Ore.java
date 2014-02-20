package com.aleclownes.manageo;

import java.io.Serializable;

public class Ore implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8666771734847848411L;
	public OreType type;
	public double density;
	public double depth;
	
	public Ore (OreType type, double density, double depth){
		this.type = type;
		this.density = density;
		this.depth = depth;
	}
}