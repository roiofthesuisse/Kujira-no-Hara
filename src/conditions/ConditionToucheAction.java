package conditions;

import map.LecteurMap;
import utilitaire.GestionClavier;

/**
 * La touche action vient d'être pressée à l'instant.
 */
public class ConditionToucheAction extends Condition {
	
	/** Constructeur vide */
	public ConditionToucheAction() {
		
	}
	
	@Override
	public final boolean estVerifiee() {
		final LecteurMap lecteur = page.event.map.lecteur;
		if (lecteur.frameActuelle > 1 //pour éviter que l'Epée se déclenche en début de Map
		&& lecteur.frameDAppuiDeLaToucheAction+1 == lecteur.frameActuelle
		) {
			return true;
		}
		return false;
	}
	
	public final boolean estLieeAuHeros() {
		return false;
	}

}
