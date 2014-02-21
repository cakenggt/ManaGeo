package com.aleclownes.manageo;

import java.io.Serializable;
import java.util.ArrayList;
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
					inventory.add(item);
				}
			}
			return true;
		}
		return false;
	}
	
}
