package com.aleclownes.manageo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Structures.Plain;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class MainActivity extends Activity implements SensorEventListener{

	LocationManager locationManager;
	SensorManager mSensorManager;
	Sensor mMagnet;
	Location location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		load();

		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		mMagnet = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				drawMap(location);
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

	@Override
	public void onResume(){
		super.onResume();
		save();
		drawMap(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
		mSensorManager.registerListener(this, mMagnet, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onPause(){
		super.onPause();
		save();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// do nothing
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float x = event.values[0];
		float y = event.values[1];
		drawDir(x, y);
	}

	private void save(){
		System.out.println("Saving files");
		String fileName = "tileFile.dat";		
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(openFileOutput(fileName, MODE_PRIVATE));
			oos.writeObject(TileHolder.getTiles());
			oos.flush();
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		fileName = "invFile.dat";
		try {
			oos = new ObjectOutputStream(openFileOutput(fileName, MODE_PRIVATE));
			oos.writeObject(InventoryHolder.getInventory());
			oos.flush();
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void load(){
		System.out.println("Loading files");
		String fileName = "tileFile.dat";
		File tileFile = new File(this.getFilesDir(), fileName);
		//tileFile.delete();
		if (tileFile.exists()){
			ObjectInputStream ois;
			Object result = null;
			try {
				ois = new ObjectInputStream(openFileInput(fileName));
				result = ois.readObject();
				ois.close();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			Map<Coord, Tile> holder = TileHolder.getTiles();
			for (Coord coord : ((HashMap<Coord, Tile>)result).keySet()){
				holder.put(coord, ((HashMap<Coord, Tile>)result).get(coord));
			}
		}
		fileName = "invFile.dat";
		File invFile = new File(this.getFilesDir(), fileName);
		//invFile.delete();
		if (invFile.exists()){
			ObjectInputStream ois;
			Object result = null;
			try {
				ois = new ObjectInputStream(openFileInput(fileName));
				result = ois.readObject();
				ois.close();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			List<Item> holder = InventoryHolder.getInventory();
			for (Item item : ((ArrayList<Item>)result)){
				holder.add(item);
			}
		}
	}

	/**Draws the map and sets the interact button text
	 * @param location
	 */
	private void drawMap(Location location){
		this.location = location;
		Map<Coord, Tile> tiles = TileHolder.getTiles();
		int[] squares = {R.id.imageView1,R.id.imageView2,R.id.imageView3,
				R.id.imageView4,R.id.imageView5,R.id.imageView6,
				R.id.imageView7,R.id.imageView8,R.id.imageView9};
		Coord cur = null;
		if (location != null){
			cur = new Coord(location);
		}
		for (int y = 0; y < 3; y++){
			for (int x = 0; x < 3; x++){
				ImageView square = (ImageView)findViewById(squares[x+(y*3)]);
				if (cur != null) {
					Coord disp = cur.getRelative(x - 1, y - 1);
					if (!tiles.containsKey(disp)) {
						tiles.put(disp, new Tile());
					}
					square.setImageResource(tiles.get(disp).aboveGround.type.icon());
				}
				else{
					square.setImageResource(R.drawable.no_gps);
				}
			}
		}
		//set interact button text
		Button interact = (Button)findViewById(R.id.interact);
		if (cur != null){
			interact.setText(tiles.get(cur).aboveGround.type.interactText());
		}
	}

	private void drawDir(float x, float y){
		Matrix matrix=new Matrix();
		ImageView dir = (ImageView) findViewById(R.id.direction);
		dir.setScaleType(ScaleType.MATRIX);   //required
		double angle = Math.toDegrees(Math.atan(x/y));
		matrix.postRotate((float) angle, dir.getDrawable().getBounds().width()/2, dir.getDrawable().getBounds().height()/2);
		//reset the image back to facing up before rotating
		dir.setImageResource(R.drawable.direction);
		dir.setImageMatrix(matrix);
	}
	
	/**The method that fires on the inventory button's onClick
	 * @param v
	 */
	public void inventory(View v){
		Intent intent = new Intent(this, InventoryActivity.class);
		startActivity(intent);
	}

	/**The method that fires on the interact button's onClick
	 * 
	 */
	public void interact(View v){
		Tile curTile = TileHolder.getTiles().get(new Coord(location));
		if (curTile.aboveGround instanceof Plain){
			Intent intent = new Intent(this, BuildActivity.class);
			startActivity(intent);
		}
		else{
			Intent intent = new Intent(this, TileInteractActivity.class);
			startActivity(intent);
		}
	}

}
