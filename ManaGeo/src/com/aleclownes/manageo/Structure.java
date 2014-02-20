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
	
	/**
	 * @param items
	 * @return
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
