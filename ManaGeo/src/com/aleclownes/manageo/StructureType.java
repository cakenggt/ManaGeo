package com.aleclownes.manageo;


public enum StructureType {
	// StructureType durability inventorySize interactButtonText iconFile
	FOREST (100, 10, "Cut", R.drawable.forest),
	MINE (200, 20, "Mine", R.drawable.mine),
	REFINERY (300, 30, "Refine", R.drawable.refinery),
	WAREHOUSE (400, 100, "Store", R.drawable.warehouse),
	PLAIN (0, 10, "Build", R.drawable.plain),
	HOUSE (100, 10, "Live", R.drawable.house);
	
	private final double durability;
	private final int inventorySize;
	private final String interactText;
	private final int icon;
	
	StructureType (double durability, int inventorySize, String interactText, int icon){
		this.durability = durability;
		this.inventorySize = inventorySize;
		this.interactText = interactText;
		this.icon = icon;
	}
	public double durability() {return durability;}
	public int inventorySize() {return inventorySize;}
	public String interactText() {return interactText;}
	public int icon() {return icon;}
}