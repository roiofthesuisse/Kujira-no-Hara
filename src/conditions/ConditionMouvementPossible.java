package conditions;

import java.io.FileNotFoundException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import commandes.CommandeEvent;
import commandes.Mouvement;
import map.PageEvent;
import utilitaire.InterpreteurDeJson;

public class ConditionMouvementPossible extends Condition implements CommandeEvent{
	
	private PageEvent page;
	
	Mouvement mouvement;

	public ConditionMouvementPossible(Mouvement mouvement) {
		this.mouvement = mouvement;
	}
	
	public ConditionMouvementPossible(final HashMap<String, Object> parametres) throws JSONException, FileNotFoundException {
		this(InterpreteurDeJson.recupererUnMouvement((JSONObject)parametres.get("mouvement")));
	}

	@Override
	public boolean estVerifiee() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean estLieeAuHeros() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PageEvent getPage() {
		// TODO Auto-generated method stub
		return page;
	}

	@Override
	public void setPage(PageEvent page) {
		this.page = page;
		
	}

}
