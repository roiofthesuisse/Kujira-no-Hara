package conditions;

import map.LecteurMap;
import utilitaire.GestionClavier;

/**
 * La touche action est pressée.
 */
public class ConditionToucheAction extends Condition {
	
	/** Constructeur vide */
	public ConditionToucheAction() {
		
	}
	
	@Override
	public final Boolean estVerifiee() {
		LecteurMap lecteur = page.event.map.lecteur;
		if ( lecteur.toucheActionPressee && lecteur.fenetre.touchesPressees.contains(GestionClavier.ToucheRole.ACTION) ) {
			lecteur.toucheActionPressee = false;
			return true;
		}
		return false;
	}

}
