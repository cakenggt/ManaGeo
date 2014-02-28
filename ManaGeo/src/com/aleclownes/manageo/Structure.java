package com.aleclownes.manageo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Structure implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -133214663817253437L;
	public StructureType type = StructureType.FOREST;
	public double durability = 0;
	public List<Item> inventory = new ArrayList<Item>();

	/**Adds the list of items to the inventory of this structure. If a 
	 * stack of 0 items is given, it will not be added.
	 * @param items - List of items to be added
	 * @return - True or false depending on whether or not the items 
	 * were able to be added due to inventory size restrictions.
	 */
	public boolean addItems (List<Item> items){
		int total = 0;
		for (Item inv : inventory){
			total += inv.quantity;
		}
		int addingTotal = 0;
		for (Item item : items){
			addingTotal += item.quantity;
		}
		if (total + addingTotal <= type.inventorySize()){
			boolean toAdd = true;
			for (Item item : items){
				for (Item inv : inventory){
					if (inv.type == item.type && toAdd){
						inv.quantity += item.quantity;
						toAdd = false;
					}
				}
				if (toAdd && item.quantity != 0){
					inventory.add(item.copy());
				}
			}
			return true;
		}
		return false;
	}

	/**Tells whether or not the inventory has this amount of each item or not
	 * @param items - List of Items
	 * @return - True if yes, false if no
	 */
	public boolean hasItems (List<Item> items){
		for (Item listItem : items){
			Material mat = listItem.getType();
			int quant = listItem.getQuantity();
			boolean has = false;
			for (Item invItem : inventory){
				if (invItem.getType() == mat){
					if (invItem.getQuantity() >= quant){
						has = true;
					}
					else{
						return false;
					}
				}
			}
			if (!has){
				return false;
			}
		}
		return true;
	}
	
	/**Removes the list of items from the inventory. If an item in the list is 
	 * not in the inventory, no items will be removed from the inventory and it will
	 * return false. If all of the items are in the inventory, then they will
	 * be removed and it will return true
	 * @param items
	 * @return - True if the items were able to be removed, false if not.
	 */
	public boolean removeItems (List<Item> items){
		if (hasItems(items)){
			Iterator<Item> it = inventory.iterator();
			while (it.hasNext()){
				Item item = it.next();
				for (Item toRemove : items){
					if (toRemove.getType() == item.getType()){
						if (toRemove.getQuantity() < item.getQuantity()){
							item.setQuantity(item.getQuantity()-toRemove.getQuantity());
						}
						else{
							it.remove();
						}
					}
				}
			}
			return true;
		}
		else{
			return false;
		}
	}

}
