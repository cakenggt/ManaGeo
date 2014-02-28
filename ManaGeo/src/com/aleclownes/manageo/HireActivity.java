package com.aleclownes.manageo;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class HireActivity extends Activity {
	
	Citizen citizen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hire);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hire, menu);
		return true;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void onResume(){
		Spinner materialSpinner = (Spinner)findViewById(R.id.materialSpinner);
		ArrayAdapter<Material> spinnerArrayAdapter = new ArrayAdapter<Material>(this, android.R.layout.simple_spinner_dropdown_item, Material.values());
		materialSpinner.setAdapter(spinnerArrayAdapter);
		long citizenName = getIntent().getLongExtra("citizenName", 0);
		for (Citizen cit : CitizenHolder.getInstance().getCitizens()){
			if (cit.getName() == citizenName){
				citizen = cit;
				return;
			}
		}
		finish();
	}
	
	/**Called with the finish button is clicked.
	 * @param v
	 */
	public void finishButton(View v){
		EditText sourceX = (EditText)findViewById(R.id.source_x);
		EditText sourceY = (EditText)findViewById(R.id.source_y);
		Spinner materialSpinner = (Spinner)findViewById(R.id.materialSpinner);
		int x = 0;
		int y = 0;
		try {
			x = Integer.parseInt(sourceX.getText().toString());
			y = Integer.parseInt(sourceY.getText().toString());
		} catch (NumberFormatException e) {
			finish();
		}
		Material mat = (Material) materialSpinner.getSelectedItem();
		if (mat != null){
			//x y and mat all exist here
			Coord newC = new Coord(x, y);
			if (TileHolder.getTiles().containsKey(newC)){
				TileHolder.getTiles().put(newC, new Tile(newC));
			}
			citizen.setDestination(TileHolder.getTiles().get(newC));
			citizen.setMaterial(mat);
		}
		finish();
	}

}
