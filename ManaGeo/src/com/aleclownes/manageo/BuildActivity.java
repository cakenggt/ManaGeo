package com.aleclownes.manageo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Structures.Mine;
import Structures.Refinery;
import Structures.Warehouse;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NavUtils;

public class BuildActivity extends Activity {

	LocationManager locationManager;
	SensorManager mSensorManager;
	Sensor mAccel;
	Coord curCoord;
	Tile curTile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_build);
		// Show the Up button in the action bar.
		setupActionBar();

		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				curCoord = new Coord(location);
				curTile = TileHolder.getTiles().get(curCoord);
				Coord newCoord = new Coord(location);
				if (curCoord != null){
					if (!newCoord.equals(curCoord)){
						finish();
					}
				}
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};

		try {
			// Register the listener with the Location Manager to receive location updates
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.build, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume(){
		super.onResume();
		Location curLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		curCoord = new Coord(curLoc);
		curTile = TileHolder.getTiles().get(curCoord);
	}

	/**If the mine button is clicked
	 * @param view
	 */
	public void buildMine(View view){
		//tileInventory resource check for 5 wood
		boolean wood = false;
		List<Item> newInv = new ArrayList<Item>();
		Iterator<Item> it = curTile.aboveGround.inventory.iterator();
		while (it.hasNext()){
			Item item = it.next();
			if (item.type == Material.WOOD && item.getQuantity() >= 5){
				wood = true;
				Item copy = item.copy();
				copy.setQuantity(item.getQuantity()-5);
				newInv.add(copy);
			}
			else{
				newInv.add(item.copy());
			}
		}
		if (wood){
			curTile.aboveGround = new Mine();
			curTile.aboveGround.addItems(newInv);
			finish();
		}
		else{
			//TODO display notification that they dont have enough materials
		}
	}

	/**If the refinery button is clicked
	 * @param view
	 */
	public void buildRefinery(View view){
		//tileInventory resource check for 7 wood, 3 granite
		boolean wood = false;
		boolean granite = false;
		List<Item> newInv = new ArrayList<Item>();
		Iterator<Item> it = curTile.aboveGround.inventory.iterator();
		while (it.hasNext()){
			Item item = it.next();
			if (item.type == Material.WOOD && item.getQuantity() >= 5){
				wood = true;
				Item copy = item.copy();
				copy.setQuantity(item.getQuantity()-5);
				newInv.add(copy);
			}
			else if (item.type == Material.GRANITE && item.quantity >= 3){
				granite = true;
				Item copy = item.copy();
				copy.setQuantity(item.getQuantity()-3);
				newInv.add(copy);
			}
			else{
				newInv.add(item.copy());
			}
		}
		if (wood && granite){
			curTile.aboveGround = new Refinery();
			curTile.aboveGround.addItems(newInv);
			finish();
		}
		else{
			//TODO display notification that they dont have enough materials
		}
	}

	/**If the warehouse button is clicked
	 * @param view
	 */
	public void buildWarehouse(View view){
		//tileInventory resource check for 5 wood, 2 granite, 3 iron
		boolean wood = false;
		boolean granite = false;
		boolean iron = false;
		List<Item> newInv = new ArrayList<Item>();
		Iterator<Item> it = curTile.aboveGround.inventory.iterator();
		while (it.hasNext()){
			Item item = it.next();
			if (item.type == Material.WOOD && item.getQuantity() >= 5){
				wood = true;
				Item copy = item.copy();
				copy.setQuantity(item.getQuantity()-5);
				newInv.add(copy);
			}
			else if (item.type == Material.GRANITE && item.quantity >= 2){
				granite = true;
				Item copy = item.copy();
				copy.setQuantity(item.getQuantity()-2);
				newInv.add(copy);
			}
			else if (item.type == Material.IRON_INGOT && item.quantity >= 3){
				iron = true;
				Item copy = item.copy();
				copy.setQuantity(item.getQuantity()-3);
				newInv.add(copy);
			}
			else{
				newInv.add(item.copy());
			}
		}
		if (wood && granite && iron){
			curTile.aboveGround = new Warehouse();
			curTile.aboveGround.addItems(newInv);
			finish();
		}
		else{
			//TODO display notification that they dont have enough materials
		}
	}

}
