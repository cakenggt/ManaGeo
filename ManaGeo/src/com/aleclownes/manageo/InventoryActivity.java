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
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class InventoryActivity extends Activity {

	LocationManager locationManager;
	Coord curCoord;
	Tile curTile;
	List<Item> tileChecked = new ArrayList<Item>();
	List<Item> persChecked = new ArrayList<Item>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory);
		curCoord = InventoryHolder.getRecentCoord();
		curTile = TileHolder.getTiles().get(curCoord);
		refreshLists();

		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				Coord newCoord = new Coord(location);
				if (!newCoord.equals(InventoryHolder.getRecentCoord())){
					InventoryHolder.getRecentCoord().change(newCoord);
					finish();
				}
				refreshLists();
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**Refreshes the listviews
	 * 
	 */
	public void refreshLists(){
		((ListView)findViewById(R.id.tile_inventory_list)).setAdapter(new TileInventoryAdapter(this));
		((ListView)findViewById(R.id.personal_inventory_list)).setAdapter(new PersonalInventoryAdapter(this));
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
			if (persQuant-persMoveQuant+tileMoveQuant > InventoryHolder.getInventorySize()){
				Toast.makeText(this, "Transfer Failed: You cannot have more than " + InventoryHolder.getInventorySize() + " items in your personal inventory.",
						Toast.LENGTH_LONG).show();
			}
			if (tileQuant-tileMoveQuant+persMoveQuant > curTile.aboveGround.type.inventorySize()){
				Toast.makeText(this, "Transfer Failed: You cannot have more than " + curTile.aboveGround.type.inventorySize() + " items in this structure's inventory.",
						Toast.LENGTH_LONG).show();
			}
		}
		refreshLists();
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
			final View row = inflater.inflate(R.layout.inventory_row, null);
			final SeekBar quantBar = (SeekBar)row.findViewById(R.id.quantBar);
			TextView name = (TextView)row.findViewById(R.id.item_name);
			name.setText(curItem.type.toString() + " " + Integer.toString(curItem.quantity));
			quantBar.setMax(curItem.quantity);
			quantBar.setOnSeekBarChangeListener(new ItemSliderBarListener(row, tileChecked, curItem.copy()));
			TextView seekQuant = (TextView)row.findViewById(R.id.seekQuant);
			seekQuant.setText(Integer.toString(quantBar.getProgress()));
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
			final View row = inflater.inflate(R.layout.inventory_row, null);
			final SeekBar quantBar = (SeekBar)row.findViewById(R.id.quantBar);
			TextView name = (TextView)row.findViewById(R.id.item_name);
			name.setText(curItem.type.toString() + " " + Integer.toString(curItem.quantity));
			quantBar.setMax(curItem.quantity);
			quantBar.setOnSeekBarChangeListener(new ItemSliderBarListener(row, persChecked, curItem));
			TextView seekQuant = (TextView)row.findViewById(R.id.seekQuant);
			seekQuant.setText(Integer.toString(quantBar.getProgress()));
			return row;
		}
	}
	
	class ItemSliderBarListener implements OnSeekBarChangeListener {
		
		View row;
		Item item;
		List<Item> inventory;
		
		public ItemSliderBarListener(View row, List<Item> inventory, Item item){
			super();
			this.row = row;
			this.item = item;
			this.inventory = inventory;
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (progress > 0){
				boolean isInside = false;
				for (Item item : inventory){
					if (item.type == this.item.type){
						isInside = true;
						item.quantity = progress;
					}
				}
				if (!isInside){
					inventory.add(new Item(this.item.getType(), progress));
				}
			}
			else{
				Iterator<Item> it = inventory.iterator();
				while (it.hasNext()){
					Item item = it.next();
					if (item.getType() == this.item.getType()){
						it.remove();
					}
				}
			}
			((TextView)row.findViewById(R.id.seekQuant)).setText(Integer.toString(progress));			
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
	}

}
