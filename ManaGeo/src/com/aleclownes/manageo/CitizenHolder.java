package com.aleclownes.manageo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CitizenHolder implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -325304322238927963L;
	private static CitizenHolder instance;
	private static List<Citizen> citizens;
	
	static {
		instance = new CitizenHolder();
		citizens = new ArrayList<Citizen>();
	}

	/**Gets the instance of the singleton.
	 * @return CitizenHolder singleton instance
	 */
	public static CitizenHolder getInstance() {return instance;}
	/**Gets the list of citizens currently in your world.
	 * @return List of active citizens
	 */
	public static List<Citizen> getCitizens() {return citizens;}
	/**Sets the time the last citizen was added.
	 * 
	 */
	public static void setInstance(CitizenHolder c) {instance = c;}
	
}
