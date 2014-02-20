package Structures;

import java.util.ArrayList;
import java.util.List;

import com.aleclownes.manageo.Item;
import com.aleclownes.manageo.Material;
import com.aleclownes.manageo.Structure;
import com.aleclownes.manageo.StructureInterface;
import com.aleclownes.manageo.Tile;

public class Forest extends Structure implements StructureInterface{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7360206892267459764L;

	public Forest() {
		durability = type.durability();
	}

	@Override
	public void interact(Tile tile, float mag) {
		if (mag > 10){
			System.out.println("Swing was " + mag);
			System.out.println("Durability is now " + durability);
			List<Item> toAdd = new ArrayList<Item>();
			toAdd.add(new Item(Material.WOOD, (int)(mag/10)));
			addItems(toAdd);
			durability -= mag/10;
		}
		if (durability <= 0){
			tile.aboveGround = new Plain();
		}
	}

}
