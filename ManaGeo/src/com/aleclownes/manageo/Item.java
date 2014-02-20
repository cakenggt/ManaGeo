package com.aleclownes.manageo;

import java.io.Serializable;

public class Item implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8948389075738166029L;
	Material type;
	int quantity;
	
	public Item (Material type, int quantity){
		this.type = type;
		this.quantity = quantity;
	}
	
	@Override
	public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Item))
            return false;

        Item c = (Item) obj;
        if (c.type == type && c.quantity == quantity){
        	return true;
        }
        else{
        	return false;
        }
    }
}