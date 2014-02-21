package Structures;

import java.util.ArrayList;
import java.util.List;

import com.aleclownes.manageo.Item;
import com.aleclownes.manageo.Material;
import com.aleclownes.manageo.Ore;
import com.aleclownes.manageo.Rock;
import com.aleclownes.manageo.Structure;
import com.aleclownes.manageo.StructureInterface;
import com.aleclownes.manageo.StructureType;
import com.aleclownes.manageo.Tile;

public class Mine extends Structure implements StructureInterface{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9065110312088331404L;
	
	public Mine() {
		type = StructureType.MINE;
	}
	
	@Override
	public void interact(Tile tile, float mag) {
		if (mag > 10){
			if (durability < type.durability()){
				durability += mag/10;
			}
			else{
				Rock rock = tile.belowGround;
				Ore ore = rock.ore;
				if (Math.random()/(mag/10) < ore.density){
					List<Item> toAdd = new ArrayList<Item>();
					toAdd.add(new Item(Material.valueOf(ore.type.toString() + "_ORE"), 1));
					if (addItems(toAdd)){
						ore.density = ore.density * ore.depth;
					}
				}
			}
		}
	}

}