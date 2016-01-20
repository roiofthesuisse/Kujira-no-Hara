package conditions;

import map.LecteurMap;

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
	
	/**
	 * Ce n'est pas une Condition qui implique une proximité avec le Héros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}
