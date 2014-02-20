package Structures;

import com.aleclownes.manageo.Structure;
import com.aleclownes.manageo.StructureInterface;
import com.aleclownes.manageo.StructureType;
import com.aleclownes.manageo.Tile;

public class Warehouse extends Structure implements StructureInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -488779297347714246L;
	
	public Warehouse(){
		type = StructureType.WAREHOUSE;
	}

	@Override
	public void interact(Tile tile, float mag) {}

}
