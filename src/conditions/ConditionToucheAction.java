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
	public final boolean estVerifiee() {
		final LecteurMap lecteur = page.event.map.lecteur;
		if (lecteur.frameDAppuiDeLaToucheAction+1 == lecteur.frameActuelle) {
			return true;
		}
		return false;
	}
	
	public final boolean estLieeAuHeros() {
		//accompagne toujours Parler donc true
		return true;
	}

}
