package com.aleclownes.manageo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Structures.Forest;
import Structures.House;
import Structures.Plain;
import Structures.Warehouse;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener{

	LocationManager locationManager;
	SensorManager mSensorManager;
	Sensor mMagnet;
	Sensor mAccele;
	Coord curCoord;
	float [] accele = new float [3];
	float [] magnet = new float [3];
	private Handler mHandler;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		load();
		curCoord = InventoryHolder.getRecentCoord();

		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		mMagnet = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mAccele = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				curCoord = new Coord(location);
				drawMap();
				InventoryHolder.getRecentCoord().change(curCoord);
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
		mHandler = new Handler();
		startRepeatingTask();
	}

	@Override
	public void onResume(){
		super.onResume();
		save();
		drawMap();
		mSensorManager.registerListener(this, mMagnet, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mAccele, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onPause(){
		super.onPause();
		save();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		stopRepeatingTask();
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
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			accele = event.values;
		}
		else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
			magnet = event.values;
		}
		float R[] = new float[9];
		float I[] = new float[9];
		boolean success = SensorManager.getRotationMatrix(R, I, accele, magnet);
		float azimuth = 0;
		if(success) {
			float orientation[] = new float[3];
			SensorManager.getOrientation(R, orientation);
			azimuth = orientation[0]; // contains azimuth, pitch, roll
		}
		drawDir(azimuth);
		drawButtons();
	}

	@SuppressWarnings("static-access")
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
		fileName = "citFile.dat";
		try {
			oos = new ObjectOutputStream(openFileOutput(fileName, MODE_PRIVATE));
			oos.writeObject(CitizenHolder.getInstance().getCitizens());
			oos.flush();
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "static-access" })
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
			holder.clear();
			for (Item item : ((ArrayList<Item>)result)){
				holder.add(item);
			}
		}
		fileName = "citFile.dat";
		File citFile = new File(this.getFilesDir(), fileName);
		//citFile.delete();
		if (citFile.exists()){
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
			List<Citizen> holder = CitizenHolder.getInstance().getCitizens();
			holder.clear();
			for (Citizen cit : ((ArrayList<Citizen>)result)){
				holder.add(cit);
			}
		}
	}

	/**Draws the map and sets the interact button text
	 * @param location
	 */
	private void drawMap(){
		TextView coordinates = (TextView)findViewById(R.id.coordinates);
		coordinates.setText("Coordinates: " + curCoord.x + " " + curCoord.y);
		Map<Coord, Tile> tiles = TileHolder.getTiles();
		int[] squares = {R.id.imageView1,R.id.imageView2,R.id.imageView3,
				R.id.imageView4,R.id.imageView5,R.id.imageView6,
				R.id.imageView7,R.id.imageView8,R.id.imageView9};
		for (int y = 0; y < 3; y++){
			for (int x = 0; x < 3; x++){
				ImageView square = (ImageView)findViewById(squares[x+(y*3)]);
				if (curCoord != null) {
					Coord disp = curCoord.getRelative(x - 1, y - 1);
					if (!tiles.containsKey(disp)) {
						tiles.put(disp, new Tile(disp));
					}
					square.setImageResource(tiles.get(disp).aboveGround.type.icon());
				}
				else{
					square.setImageResource(R.drawable.no_gps);
				}
			}
		}
		drawButtons();
	}

	private void drawButtons(){
		//set text and visibility for buttons
		Button interact = (Button)findViewById(R.id.interact);
		Button destroy = (Button)findViewById(R.id.destroy);
		Button inventory = (Button)findViewById(R.id.inventory);
		if (curCoord != null){
			interact.setVisibility(View.VISIBLE);
			inventory.setVisibility(View.VISIBLE);
			Tile tile = TileHolder.getTiles().get(curCoord);
			if (tile.aboveGround.durability < tile.aboveGround.type.durability()){
				interact.setText("Build");
			}
			else{
				if (tile.aboveGround instanceof Warehouse || tile.aboveGround instanceof House){
					interact.setVisibility(View.GONE);
				}
				interact.setText(tile.aboveGround.type.interactText());
			}
			if (tile.aboveGround instanceof Plain || tile.aboveGround instanceof Forest){
				destroy.setVisibility(View.GONE);
			}
			else{
				destroy.setVisibility(View.VISIBLE);
			}
		}
		else{
			interact.setVisibility(View.GONE);
			inventory.setVisibility(View.GONE);
		}
	}

	private void drawDir(float azimuth){
		Matrix matrix=new Matrix();
		ImageView dir = (ImageView) findViewById(R.id.direction);
		dir.setScaleType(ScaleType.MATRIX);   //required
		double angle = Math.toDegrees(-azimuth);
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
		Tile curTile = TileHolder.getTiles().get(curCoord);
		if (curTile.aboveGround instanceof Plain){
			Intent intent = new Intent(this, BuildActivity.class);
			startActivity(intent);
		}
		else{
			Intent intent = new Intent(this, TileInteractActivity.class);
			startActivity(intent);
		}
	}

	/**The method that fires on the destroy button's onClick
	 * 
	 */
	public void destroy(View v){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);

		// set title
		alertDialogBuilder.setTitle("Destroy? This will delete this structure's inventory");

		// set dialog message
		alertDialogBuilder
		.setMessage("Click no to exit")
		.setCancelable(false)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				TileHolder.getTiles().get(curCoord).aboveGround = new Plain();
				drawMap();
				dialog.cancel();
			}
		})
		.setNegativeButton("No",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	public void citizens(View v){
		Intent intent = new Intent(this, CitizenActivity.class);
		startActivity(intent);
	}

	Runnable mStatusChecker = new Runnable() {
		@SuppressWarnings("static-access")
		@Override 
		public void run() {
			mHandler.postDelayed(mStatusChecker, 5000);
			for (Citizen citizen : CitizenHolder.getInstance().getCitizens()){
				if (citizen.getWork() != null){
					Tile work = citizen.getWork();
					if (citizen.getDestination() != null && citizen.getMaterial() != null){
						//This citizen is a courier
						List<Item> transfers = new ArrayList<Item>();
						Tile dest = citizen.getDestination();
						Material mat = citizen.getMaterial();
						Iterator<Item> it = work.aboveGround.inventory.iterator();
						while (it.hasNext()){
							Item item = it.next();
							if (item.getType() == mat){
								if (item.getQuantity() > 10){
									item.setQuantity(item.getQuantity()-10);
									transfers.add(new Item(item.getType(), 10));
								}
								else{
									transfers.add(item.copy());
									it.remove();
								}
								break;
							}
						}
						if (!dest.aboveGround.addItems(transfers)){
							work.aboveGround.addItems(transfers);
						}
					}
					else{
						//This citizen is not a courier
						if (!(work.aboveGround instanceof House)){
							((StructureInterface)work.aboveGround).interact(MainActivity.this, work, 11);
						}
					}
				}
			}
		}
	};

	void startRepeatingTask() {
		mStatusChecker.run(); 
	}

	void stopRepeatingTask() {
		mHandler.removeCallbacks(mStatusChecker);
	}

}
