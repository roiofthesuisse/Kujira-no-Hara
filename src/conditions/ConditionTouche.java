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
	private final boolean toucheMaintenue;
	
	/** 
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param touche à vérifier
	 * @param toucheMaintenue touche actuellement enfoncée VS appui récent
	 */
	public ConditionTouche(final int numero, final GestionClavier.ToucheRole touche, final boolean toucheMaintenue) {
		this.numero = numero;
		this.toucheRole = touche;
		this.toucheMaintenue = toucheMaintenue;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionTouche(final HashMap<String, Object> parametres) {
		this(
				parametres.containsKey("numero") ? (int) parametres.get("numero") : -1,
				parametres.containsKey("touche") ? GestionClavier.ToucheRole.getToucheRole((String) parametres.get("touche")) : null,
				parametres.containsKey("toucheMaintenue") ? (boolean) parametres.get("toucheMaintenue") : true
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		if (this.toucheMaintenue) {
			// La touche est-elle enfoncée en ce moment ?
			if (this.toucheRole != null) {
				return this.toucheRole.touche.enfoncee;
			} else { 
				return false;
			}
			
		} else {
			// Le joueur vient-il de presser la touche à l'instant ?
			final LecteurMap lecteur = this.page.event.map.lecteur;
			if (lecteur.frameActuelle > 1) { //pour éviter que l'Epée se déclenche en début de Map
				final Integer frameDAppui = this.toucheRole.touche.frameDAppui;
				if (frameDAppui != null && frameDAppui + 1 == lecteur.frameActuelle) {
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximité avec le Héros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}
