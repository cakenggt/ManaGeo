package com.aleclownes.manageo;

import java.util.List;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class CitizenActivity extends Activity {

	LocationManager locationManager;
	Coord curCoord;
	Tile curTile;
	List<Citizen> employedHere;
	List<Citizen> unemployed;

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
	@SuppressWarnings("static-access")
	public void refreshLists(){
		for (Citizen cit : CitizenHolder.getInstance().getCitizens()){
			if (cit.getWork().equals(curTile)){
				employedHere.add(cit);
			}
			else if (cit.getWork() == null){
				unemployed.add(cit);
			}
		}
		((ListView)findViewById(R.id.employed_list)).setAdapter(new TileEmployeeAdapter(this));
		((ListView)findViewById(R.id.unemployed_list)).setAdapter(new UnemployedAdapter(this));
	}

	@SuppressWarnings("rawtypes")
	class TileEmployeeAdapter extends ArrayAdapter {
		Activity context;

		@SuppressWarnings("unchecked")
		TileEmployeeAdapter(Activity context){
			super(context, R.layout.citizen_row, employedHere);

			this.context = context;
		}

		public View getView(int position, View convertView, ViewGroup parent){
			Citizen cit = employedHere.get(position);
			LayoutInflater inflater = context.getLayoutInflater();
			final View row = inflater.inflate(R.layout.citizen_row, null);
			TextView name = (TextView)row.findViewById(R.id.citizen_name);
			name.setText(Long.toString(cit.getName()));
			Button button = (Button)row.findViewById(R.id.hire_fire_button);
			button.setText("Fire");
			button.setOnClickListener(new CitizenFireListener(row, cit));
			return row;
		}
	}

	@SuppressWarnings("rawtypes")
	class UnemployedAdapter extends ArrayAdapter {
		Activity context;
		Citizen curCit;

		@SuppressWarnings("unchecked")
		UnemployedAdapter(Activity context){
			super(context, R.layout.citizen_row, unemployed);

			this.context = context;

		}

		public View getView(int position, View convertView, ViewGroup parent){
			Citizen cit = unemployed.get(position);
			LayoutInflater inflater = context.getLayoutInflater();
			final View row = inflater.inflate(R.layout.citizen_row, null);
			TextView name = (TextView)row.findViewById(R.id.citizen_name);
			name.setText(Long.toString(cit.getName()));
			Button button = (Button)row.findViewById(R.id.hire_fire_button);
			button.setText("Hire");
			button.setOnClickListener(new CitizenHireListener(row, cit));
			return row;
		}
	}

	class CitizenHireListener implements OnClickListener {

		Citizen citizen;

		public CitizenHireListener(View row, Citizen citizen){
			super();
			this.citizen = citizen;
		}

		@Override
		public void onClick(View v) {
			// TODO Always open up the destination and material dialog
			// and check in that dialog if the submitted answers are
			// empty, and if they are, set those to null and have the
			// citizen do normal interact() labor in this tile
			Intent intent = new Intent(CitizenActivity.this, HireActivity.class);
			intent.putExtra("citizenName", citizen.getName());
			// TODO get this name by calling getIntent() and then
			// getLongExtra("citizenName")
			startActivity(intent);
			citizen.setWork(curTile);
			CitizenActivity.this.refreshLists();
		}

	}

	class CitizenFireListener implements OnClickListener {

		Citizen citizen;

		public CitizenFireListener(View row, Citizen citizen){
			super();
			this.citizen = citizen;
		}

		@Override
		public void onClick(View v) {
			// Set the citizen's work tile and material to null
			citizen.setWork(null);
			citizen.setDestination(null);
			citizen.setMaterial(null);
			CitizenActivity.this.refreshLists();
		}

	}

}
