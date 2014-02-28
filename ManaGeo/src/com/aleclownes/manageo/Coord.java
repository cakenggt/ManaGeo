package com.aleclownes.manageo;

import java.io.Serializable;

import android.location.Location;

/**This class converts gps coordinates into coord for use as keys
 * in the tile holder. The x is bound by [0, 1296000]. The y is bound 
 * by [0, 648000]
 * @author lownes
 *
 */
public class Coord implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -633543775001528004L;
	int x;
	int y;
	
	public Coord(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public Coord(Location loc){
		x = (int) (loc.getLongitude()*3600);
		y = (int) (loc.getLatitude()*3600);
	}
	
	public Coord getRelative(int dx, int dy){
		int x = (this.x + dx)%1296000;
		int y = (this.y + dy)%648000;
		return new Coord(x, y);
	}
	
	/**This mutates the coord into the parameter
	 * @param c - coord to mutate this into
	 */
	public void change(Coord c){
		x = c.x;
		y = c.y;
	}
	
	@Override
	public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Coord))
            return false;

        Coord c = (Coord) obj;
        if (c.x == x && c.y == y){
        	return true;
        }
        else{
        	return false;
        }
    }
	
	@Override
	public int hashCode() {
		int hash = 23;
		hash = hash * 31 + x;
		hash = hash * 31 + y;
		return hash;
	}
}
