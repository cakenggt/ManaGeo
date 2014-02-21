package Structures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aleclownes.manageo.Item;
import com.aleclownes.manageo.Material;
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
		if (mag > 10){
			int num = (int)mag/10;
			Iterator<Item> it = inventory.iterator();
			List<Item> toAdd = new ArrayList<Item>();
			while (it.hasNext()){
				Item item = it.next();
				String itemType = item.getType().toString();
				if (itemType.endsWith("_ORE")){
					Material ingotMat = Material.valueOf(itemType.substring(0, itemType.length()-4).concat("_INGOT"));
					if (num >= item.getQuantity()){
						num -= item.getQuantity();
						item.setType(ingotMat);
					}
					else{
						item.setQuantity(item.getQuantity()-num);
						Item ingot = new Item(ingotMat, num);
						toAdd.add(ingot);
						num = -1;
					}
					
				}
				if (num <= 0){
					break;
				}
			}
			addItems(toAdd);
		}
	}

}
