package Structures;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

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
	public void interact(Activity act, Tile tile, float mag) {
		if (mag > 10){
			List<Item> toAdd = new ArrayList<Item>();
			toAdd.add(new Item(Material.WOOD, (int)(mag/10)));
			addItems(toAdd);
			durability -= mag/10;
		}
		if (durability <= 0){
			@SuppressWarnings({ "unchecked", "rawtypes" })
			List<Item> copy = (List<Item>) ((ArrayList)tile.aboveGround.inventory).clone();
			tile.aboveGround = new Plain();
			tile.aboveGround.addItems(copy);
		}
	}

}
