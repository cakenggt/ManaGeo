package com.aleclownes.manageo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;

public class Citizen implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5840618882458765383L;
	final Tile home;
	Tile work;
	Tile dest;
	Material mat;
	final String name;

	@SuppressWarnings("static-access")
	public Citizen (Tile home, Context context){
		this.home = home;
		Resources resources = context.getResources();
		InputStream iS = null;
		try {
			iS = resources.getAssets().open("census-derived-all-first.txt");
		} catch (IOException e) {
			this.name = "JOHN";
			return;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(iS));
		String line;
		try {
			line = reader.readLine();
		} catch (IOException e) {
			this.name = "JOHN";
			return;
		}
		List<String> nameList = new ArrayList<String>();
		while (line != null){
			nameList.add(line.split("\\s+")[0]);
			try {
				line = reader.readLine();
			} catch (IOException e) {
				this.name = "JOHN";
				return;
			}
		}
		List<String> usedNames = new ArrayList<String>();
		for (Citizen cit : CitizenHolder.getInstance().getCitizens()){
			usedNames.add(cit.getName());
		}
		String newName = "";
		do {
			newName = nameList.get((int) (Math.random()*nameList.size()));
		} while(usedNames.contains(newName));
		this.name = newName;
	}

	/**Gets the material a warehouse worker transports. Can be null if the worker is not
	 * a warehouse worker.
	 * @return material or null
	 */
	public Material getMaterial() {
		return mat;
	}

	public void setMaterial(Material m){
		mat = m;
	}

	public Tile getHome(){
		return home;
	}

	/**Gets work of citizen. Can be null if unemployed.
	 * @return work tile or null
	 */
	public Tile getWork(){
		return work;
	}

	public void setWork(Tile til){
		work = til;
	}

	public String getName(){
		return name;
	}
	
	/**Gets the courier destination of citizen. Can be null if not a courier or unemployed
	 * @return destination tile or null
	 */
	public Tile getDestination(){
		return dest;
	}
	
	public void setDestination(Tile tile){
		dest = tile;
	}

	@Override
	public boolean equals(Object obj){
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof Citizen))
			return false;

		Citizen c = (Citizen) obj;
		if (c.getName() == name){
			return true;
		}
		else{
			return false;
		}
	}
}