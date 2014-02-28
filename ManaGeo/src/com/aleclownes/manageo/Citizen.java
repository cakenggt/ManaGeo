package com.aleclownes.manageo;

import java.io.Serializable;

public class Citizen implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5840618882458765383L;
	final Tile home;
	Tile work;
	Tile dest;
	Material mat;
	final long name;

	public Citizen (Tile home){
		this.home = home;
		this.name = System.currentTimeMillis()/1000;
	}

	/**Gets the material a warehouse worker transports. Can be null if the worker is not
	 * a warehouse worker.
	 * @return material or null
	 */
	public Material getMaterial() {
		return mat;
	}

	public void setMaterial(Material m){
		mat = m;
	}

	public Tile getHome(){
		return home;
	}

	/**Gets work of citizen. Can be null if unemployed.
	 * @return work tile or null
	 */
	public Tile getWork(){
		return work;
	}

	public void setWork(Tile til){
		work = til;
	}

	public long getName(){
		return name;
	}
	
	/**Gets the courier destination of citizen. Can be null if not a courier or unemployed
	 * @return destination tile or null
	 */
	public Tile getDestination(){
		return dest;
	}
	
	public void setDestination(Tile tile){
		dest = tile;
	}

	@Override
	public boolean equals(Object obj){
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof Citizen))
			return false;

		Citizen c = (Citizen) obj;
		if (c.getName() == name){
			return true;
		}
		else{
			return false;
		}
	}
}