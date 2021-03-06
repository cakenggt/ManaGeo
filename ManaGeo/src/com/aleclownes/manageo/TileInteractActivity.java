package com.aleclownes.manageo;

import Structures.Forest;
import Structures.House;
import Structures.Plain;
import Structures.Warehouse;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TileInteractActivity extends Activity implements SensorEventListener{

	LocationManager locationManager;
	SensorManager mSensorManager;
	Sensor mAccel;
	Tile curTile;
	Coord curCoord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tile_interact);
		curCoord = InventoryHolder.getRecentCoord();

		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				curTile = TileHolder.getTiles().get(curCoord);
				Coord newCoord = new Coord(location);
				if (!newCoord.equals(InventoryHolder.getRecentCoord())){
					InventoryHolder.getRecentCoord().change(newCoord);
					finish();
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

	@Override
	public void onResume(){
		super.onResume();
		mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_NORMAL);
		curTile = TileHolder.getTiles().get(InventoryHolder.getRecentCoord());
		((ListView)findViewById(R.id.tile_inventory_list)).setAdapter(new ItemAdapter(this));
		//Setting whether you are interacting or building the structure
		TextView interactText = (TextView)findViewById(R.id.mineCost);
		ProgressBar progress = (ProgressBar)findViewById(R.id.progressBar1);
		if (!(curTile.aboveGround instanceof Forest)){
			if (curTile.aboveGround.durability < curTile.aboveGround.type.durability()){
				interactText.setText("Building");
				progress.setIndeterminate(false);
				progress.setMax((int) curTile.aboveGround.type.durability());
				progress.setProgress((int) curTile.aboveGround.durability);
			}
			else{
				interactText.setText(curTile.aboveGround.type.interactText());
				progress.setIndeterminate(true);
			}
		}
		else{
			progress.setIndeterminate(false);
			progress.setMax((int) curTile.aboveGround.type.durability());
			progress.setProgress((int) curTile.aboveGround.durability);
			interactText.setText("Cut");
		}
	}

	@Override
	public void onPause(){
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// do nothing

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (curTile != null){
			float mag = 0;
			for (float value : event.values){
				mag += Math.abs(value);
			}
			if (curTile.aboveGround instanceof Plain || (curTile.aboveGround instanceof House || curTile.aboveGround instanceof Warehouse) && curTile.aboveGround.durability >= curTile.aboveGround.type.durability()){
				finish();
				return;
			}
			((StructureInterface) curTile.aboveGround).interact(this, curTile, mag);
			((ListView)findViewById(R.id.tile_inventory_list)).setAdapter(new ItemAdapter(this));
			//Setting whether you are interacting or building the structure
			TextView interactText = (TextView)findViewById(R.id.mineCost);
			ProgressBar progress = (ProgressBar)findViewById(R.id.progressBar1);
			if (!(curTile.aboveGround instanceof Forest)){
				if (curTile.aboveGround.durability < curTile.aboveGround.type.durability()){
					interactText.setText("Building");
					progress.setIndeterminate(false);
					progress.setMax((int) curTile.aboveGround.type.durability());
					progress.setProgress((int) curTile.aboveGround.durability);
				}
				else{
					interactText.setText(curTile.aboveGround.type.interactText());
					progress.setIndeterminate(true);
				}
			}
			else{
				interactText.setText("Cutting");
				progress.setIndeterminate(false);
				progress.setMax((int) curTile.aboveGround.type.durability());
				progress.setProgress((int) curTile.aboveGround.durability);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	class ItemAdapter extends ArrayAdapter {
		Activity context;

		@SuppressWarnings("unchecked")
		ItemAdapter(Activity context){
			super(context, R.layout.interact_row, curTile.aboveGround.inventory);

			this.context = context;
		}

		public View getView(int position, View convertView, ViewGroup parent){
			Item curItem = curTile.aboveGround.inventory.get(position);
			LayoutInflater inflater = context.getLayoutInflater();
			View row = inflater.inflate(R.layout.interact_row, null);
			TextView name = (TextView)row.findViewById(R.id.item_name);
			name.setText(curItem.type.toString());
			TextView quant = (TextView)row.findViewById(R.id.item_quant);
			quant.setText(Integer.toString(curItem.quantity));
			return row;
		}
	}

}
