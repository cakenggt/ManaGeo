package com.aleclownes.manageo;

import java.io.Serializable;

public class Rock implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6141316337343351188L;
	public RockType type;
	public Ore ore;
	
	public Rock (RockType type, Ore ore){
		this.type = type;
		this.ore = ore;
	}
}