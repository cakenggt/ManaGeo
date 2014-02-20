package Structures;

import com.aleclownes.manageo.Structure;
import com.aleclownes.manageo.StructureInterface;
import com.aleclownes.manageo.StructureType;
import com.aleclownes.manageo.Tile;

public class Plain extends Structure implements StructureInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9046321246479458990L;

	public Plain() {
		type = StructureType.PLAIN;
	}
	
	@Override
	public void interact(Tile tile, float mag) {}

}
