package Structures;

import com.aleclownes.manageo.Structure;
import com.aleclownes.manageo.StructureInterface;
import com.aleclownes.manageo.StructureType;
import com.aleclownes.manageo.Tile;

public class Refinery extends Structure implements StructureInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2405077732027614917L;

	public Refinery() {
		type = StructureType.REFINERY;
	}
	
	@Override
	public void interact(Tile tile, float mag) {
		//TODO see if there exists ore in the player's inventory
		//then convert it into ingot form
	}

}
