package Structures;

import android.app.Activity;
import android.widget.Toast;

import com.aleclownes.manageo.Citizen;
import com.aleclownes.manageo.CitizenHolder;
import com.aleclownes.manageo.Structure;
import com.aleclownes.manageo.StructureInterface;
import com.aleclownes.manageo.StructureType;
import com.aleclownes.manageo.Tile;

public class House extends Structure implements StructureInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5812612665981436992L;

	public House() {
		type = StructureType.HOUSE;
	}

	@SuppressWarnings("static-access")
	@Override
	public void interact(Activity act, Tile tile, float mag) {
		if (mag > 10){
			if (durability < type.durability()){
				durability += mag/10;
			}
			if (durability > type.durability()){
				Citizen cit = new Citizen(tile, act);
				Toast.makeText(act, "Citizen " + cit.getName() + " has moved into this house", Toast.LENGTH_LONG).show();
				CitizenHolder.getInstance().getCitizens().add(cit);
			}
		}
	}

}
