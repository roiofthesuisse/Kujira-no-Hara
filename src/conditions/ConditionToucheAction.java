package conditions;

import main.GestionClavier;
import map.Map;

public class ConditionToucheAction extends Condition{
	
	public ConditionToucheAction(){
		
	}
	
	@Override
	public Boolean estVerifiee() {
		Map map = page.event.map;
		if( map.toucheActionPressee && map.lecteur.fenetre.touchesPressees.contains(GestionClavier.ToucheRole.ACTION) ){
			map.toucheActionPressee = false;
			return true;
		}
		return false;
	}

}
