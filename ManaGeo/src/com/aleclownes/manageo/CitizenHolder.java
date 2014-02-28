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
	private static long lastCitizen;
	private static List<Citizen> citizens;
	
	static {
		instance = new CitizenHolder();
		lastCitizen = 0;
		citizens = new ArrayList<Citizen>();
	}

	/**Gets the instance of the singleton.
	 * @return CitizenHolder singleton instance
	 */
	public static CitizenHolder getInstance() {return instance;}
	/**Get the system time when the last citizen was added to this list.
	 * Only use this on the CitizenHolder gotten from getInstance().
	 * @return system time when last citizen was added to list
	 */
	public static long getLastCitizen() {return lastCitizen;}
	/**Gets the list of citizens currently in your world.
	 * @return List of active citizens
	 */
	public static List<Citizen> getCitizens() {return citizens;}
	/**Sets the time the last citizen was added.
	 * 
	 */
	public static void setLastCitizen() {lastCitizen = System.currentTimeMillis();}
	public static void setInstance(CitizenHolder c) {instance = c;}
	
}
