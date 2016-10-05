package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import map.LecteurMap;
import utilitaire.GestionClavier;

/**
 * La touche action vient d'être pressée à l'instant.
 */
public class ConditionTouche extends Condition implements CommandeEvent {
	
	private final GestionClavier.ToucheRole toucheRole;
	
	/** 
	 * Constructeur explicite
	 * @param touche à vérifier 
	 */
	public ConditionTouche(GestionClavier.ToucheRole touche) {
		this.toucheRole = touche;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionTouche(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("touche") ? GestionClavier.ToucheRole.getToucheRole((String) parametres.get("touche")) : null);
	}
	
	@Override
	public final boolean estVerifiee() {
		final LecteurMap lecteur = this.page.event.map.lecteur;
		if (lecteur.frameActuelle > 1) { //pour éviter que l'Epée se déclenche en début de Map
			final Integer frameDAppui = this.toucheRole.touche.frameDAppui;
			if (frameDAppui != null && frameDAppui + 1 == lecteur.frameActuelle) {
				return true;
			}
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
