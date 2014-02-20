package com.aleclownes.manageo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class InventoryActivity extends Activity {

	LocationManager locationManager;
	Coord curCoord;
	Tile curTile;
	Location curLoc;
	List<Item> tileChecked = new ArrayList<Item>();
	List<Item> persChecked = new ArrayList<Item>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory);

		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				curLoc = location;
				curCoord = new Coord(curLoc);
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

	@Override
	public void onResume(){
		super.onResume();
		Location curLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (curLoc != null){
			curCoord = new Coord(curLoc);
			curTile = TileHolder.getTiles().get(curCoord);
			((ListView)findViewById(R.id.tile_inventory_list)).setAdapter(new TileInventoryAdapter(this));
			((ListView)findViewById(R.id.personal_inventory_list)).setAdapter(new PersonalInventoryAdapter(this));
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**What gets run when the transfer button is pressed
	 * @param v
	 */
	public void transfer(View v){
		List<Item> tileInventory = curTile.aboveGround.inventory;
		List<Item> persInventory = InventoryHolder.getInventory();
		int tileQuant = 0;
		int persQuant = 0;
		int tileMoveQuant = 0;
		int persMoveQuant = 0;
		for (Item item : tileInventory){
			tileQuant += item.quantity;
		}
		for (Item item : persInventory){
			persQuant += item.quantity;
		}
		for (Item item : tileChecked){
			tileMoveQuant += item.quantity;
		}
		for (Item item : persChecked){
			persMoveQuant += item.quantity;
		}
		if (persQuant-persMoveQuant+tileMoveQuant <= InventoryHolder.getInventorySize() &&
				tileQuant-tileMoveQuant+persMoveQuant <= curTile.aboveGround.type.inventorySize()){
			for (Item item : tileChecked){
				Iterator<Item> it = tileInventory.iterator();
				while (it.hasNext()){
					Item next = it.next();
					if (next.type == item.type){
						if (next.quantity - item.quantity > 0){
							next.quantity -= item.quantity;
						}
						else{
							it.remove();
						}
					}
				}
			}
			curTile.aboveGround.addItems(persChecked);
			for (Item item : persChecked){
				Iterator<Item> it = persInventory.iterator();
				while (it.hasNext()){
					Item next = it.next();
					if (next.type == item.type){
						if (next.quantity - item.quantity > 0){
							next.quantity -= item.quantity;
						}
						else{
							it.remove();
						}
					}
				}
			}
			InventoryHolder.addItems(tileChecked);
		}
		else{
			//TODO pop up error screen (like in duolingo) saying you cant move items
			//because of inventory size restrictions
		}
		((ListView)findViewById(R.id.tile_inventory_list)).setAdapter(new TileInventoryAdapter(this));
		((ListView)findViewById(R.id.personal_inventory_list)).setAdapter(new PersonalInventoryAdapter(this));
		tileChecked.clear();
		persChecked.clear();
	}

	@SuppressWarnings("rawtypes")
	class TileInventoryAdapter extends ArrayAdapter {
		Activity context;
		Item curItem;

		@SuppressWarnings("unchecked")
		TileInventoryAdapter(Activity context){
			super(context, R.layout.interact_row, curTile.aboveGround.inventory);

			this.context = context;
		}

		public View getView(int position, View convertView, ViewGroup parent){
			curItem = curTile.aboveGround.inventory.get(position);
			LayoutInflater inflater = context.getLayoutInflater();
			View row = inflater.inflate(R.layout.inventory_row, null);
			final SeekBar quantBar = (SeekBar)row.findViewById(R.id.quantBar);
			CheckBox name = (CheckBox)row.findViewById(R.id.item_name_and_check);
			name.setText(curItem.type.toString());
			name.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					if (cb.isChecked()){
						tileChecked.add(new Item(curItem.type, quantBar.getProgress()));
					}
					else{
						Iterator<Item> it = tileChecked.iterator();
						while (it.hasNext()){
							Item next = it.next();
							if (next.type == curItem.type){
								it.remove();
							}
						}
					}
				}
			});
			TextView quant = (TextView)row.findViewById(R.id.item_quant);
			quant.setText(Integer.toString(curItem.quantity));
			quantBar.setMax(curItem.quantity);
			quantBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					for (Item item : tileChecked){
						if (item.type == curItem.type){
							item.quantity = progress;
						}
					}
				}
				public void onStartTrackingTouch(SeekBar seekBar) {}
				public void onStopTrackingTouch(SeekBar seekBar) {}
			});
			TextView seekQuant = (TextView)row.findViewById(R.id.seekQuant);
			seekQuant.setText(Integer.toString(quantBar.getProgress()));
			//TODO finish this to implement the seekbar
			//use onProgressChanged to delete the only other
			//stack of that material in it's corresponding list
			//whether it is persChecked or tileChecked
			//and then replace it with the new one
			//make sure to delete any stacks with 0 quant
			return row;
		}
	}

	@SuppressWarnings("rawtypes")
	class PersonalInventoryAdapter extends ArrayAdapter {
		Activity context;
		Item curItem;

		@SuppressWarnings("unchecked")
		PersonalInventoryAdapter(Activity context){
			super(context, R.layout.interact_row, InventoryHolder.getInventory());

			this.context = context;

		}

		public View getView(int position, View convertView, ViewGroup parent){
			curItem = InventoryHolder.getInventory().get(position);
			LayoutInflater inflater = context.getLayoutInflater();
			View row = inflater.inflate(R.layout.inventory_row, null);
			final SeekBar quantBar = (SeekBar)row.findViewById(R.id.quantBar);
			CheckBox name = (CheckBox)row.findViewById(R.id.item_name_and_check);
			name.setText(curItem.type.toString());
			name.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					if (cb.isChecked()){
						persChecked.add(curItem);
					}
					else{
						Iterator<Item> it = persChecked.iterator();
						while (it.hasNext()){
							Item next = it.next();
							if (next.type == curItem.type){
								it.remove();
							}
						}
					}
				}
			});
			TextView quant = (TextView)row.findViewById(R.id.item_quant);
			quant.setText(Integer.toString(curItem.quantity));
			quantBar.setMax(curItem.quantity);
			quantBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					for (Item item : persChecked){
						if (item.type == curItem.type){
							item.quantity = progress;
						}
					}
				}
				public void onStartTrackingTouch(SeekBar seekBar) {}
				public void onStopTrackingTouch(SeekBar seekBar) {}
			});
			TextView seekQuant = (TextView)row.findViewById(R.id.seekQuant);
			seekQuant.setText(Integer.toString(quantBar.getProgress()));
			//TODO finish this to implement the seekbar
			//use onProgressChanged to delete the only other
			//stack of that material in it's corresponding list
			//whether it is persChecked or tileChecked
			//and then replace it with the new one
			//make sure to delete any stacks with 0 quant in transfer
			return row;
		}
	}

}
