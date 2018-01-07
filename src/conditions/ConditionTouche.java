package conditions;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import commandes.CommandeEvent;
import map.LecteurMap;
import utilitaire.GestionClavier.ToucheRole;

/**
 * La touche action vient d'être pressée à l'instant.
 */
public class ConditionTouche extends Condition implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(ConditionTouche.class);
	
	private final ArrayList<ToucheRole> touchesRoles = new ArrayList<>();
	private final boolean toucheMaintenue;
	
	/** 
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param touches à vérifier (séparées par des points-virgules)
	 * @param toucheMaintenue touche actuellement enfoncée VS appui récent
	 */
	public ConditionTouche(final int numero, final String touches, final boolean toucheMaintenue) {
		this.numero = numero;
		final String[] touchesParsees = touches.split(";");
		for (String touche : touchesParsees) {
			this.touchesRoles.add(ToucheRole.getToucheRole(touche));
		}
		this.toucheMaintenue = toucheMaintenue;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionTouche(final HashMap<String, Object> parametres) {
		this(
				parametres.containsKey("numero") ? (int) parametres.get("numero") : -1,
				(String) parametres.get("touche"),
				parametres.containsKey("toucheMaintenue") ? (boolean) parametres.get("toucheMaintenue") : true
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		if (this.toucheMaintenue) {
			// La touche est-elle enfoncée en ce moment ?
				for (ToucheRole toucheRole : this.touchesRoles) {
					if (toucheRole == null || toucheRole.touche == null) {
						LOG.error("Touche inexistante !", this.touchesRoles);
					}
					if (toucheRole.touche.enfoncee) {
						return true;
					}
				}
				return false;
			
		} else {
			// Le joueur vient-il de presser la touche à l'instant ?
			final LecteurMap lecteur = this.page.event.map.lecteur;
			if (lecteur.frameActuelle > 1) { //pour éviter que l'Epée se déclenche en début de Map
				for (ToucheRole toucheRole : this.touchesRoles) {
					final Integer frameDAppui = toucheRole.touche.frameDAppui;
					if (frameDAppui != null && frameDAppui + 1 == lecteur.frameActuelle) {
						return true;
					}
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
