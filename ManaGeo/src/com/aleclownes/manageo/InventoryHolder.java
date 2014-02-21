package com.aleclownes.manageo;

import java.util.ArrayList;
import java.util.List;

/**Singleton class to hold the player's inventory
 * @author lownes
 *
 */
public class InventoryHolder {
	private static final int inventorySize = 10;
	private static final List<Item> inventory;

	static {
		inventory = new ArrayList<Item>();
	}

	public static List<Item> getInventory() {return inventory;}
	public static int getInventorySize() {return inventorySize;}

	/**Adds a list of items to the player's inventory if the inventory is big enough
	 * If an item is given with 0 quantity, it will not be added.
	 * @param items - List of items to be added
	 * @return true if the items were able to be added, false if not
	 */
	public static boolean addItems (List<Item> items){
		int total = 0;
		for (Item inv : inventory){
			total += inv.quantity;
		}
		int addingTotal = 0;
		for (Item item : items){
			addingTotal += item.quantity;
		}
		if (total + addingTotal <= inventorySize){
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
