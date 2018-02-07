package mouvements;

import java.util.HashMap;

import conditions.ConditionContact;
import conditions.ConditionContact.TypeDeContact;
import main.Main;
import map.Event;
import map.Event.Direction;

/**
 * Rapprocher un Event auprès d'un autre, jusqu'à ce qu'ils soient face à face.
 */
public class SeRapprocher extends Avancer {
	private final Integer idEventARapprocher;
	private final Integer idEventCible;
	private Event eventARapprocher;
	private Event eventCible;
	
	/**
	 * Constructeur explicite
	 * @param idEventARapprocher Event qui doit se rapprocher d'un autre
	 * @param idEventCible Event vers lequel on se rapproche
	 */
	public SeRapprocher(final Integer idEventARapprocher, final Integer idEventCible) {
		//le -1 est bidon, il sera remplacé par la direction de l'Event lors de la vérification
		super(-1, 1);
		
		this.idEventARapprocher = idEventARapprocher;
		this.idEventCible = idEventCible;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public SeRapprocher(final HashMap<String, Object> parametres) {
		this(
				parametres.containsKey("idEventARapprocher") ? (int) parametres.get("idEventARapprocher") : 0, //par défaut, le Héros
				parametres.containsKey("idEventCible") ? (int) parametres.get("idEventCible") : null //par défaut, cet Event
		);
	}

	@Override
	public final boolean mouvementPossible() {
		this.eventARapprocher = this.deplacement.page.event.map.eventsHash.get(this.idEventARapprocher);
		this.eventCible = this.deplacement.page.event.map.eventsHash.get(this.idEventCible);
		
		final ConditionContact contactInclusif = new ConditionContact(-1, this.idEventARapprocher, 
				this.idEventCible, TypeDeContact.SUPERPOSITION_INCLUSIVE);
		contactInclusif.page = this.deplacement.page;
		if (contactInclusif.estVerifiee()) {
			// Pas la peine de rapprocher les Events, ils sont déjà bien positionnés.
			return false;
		}
		
		// Calculer le nombre d'étapes
		//TODO
		
		// Est-ce possible ?
		//TODO
		return true;
	}
	
	@Override
	public final void calculDuMouvement(final Event event) {
		event.avance = true;
		
		//déplacement :
		//TODO
		
		this.ceQuiAEteFait++;
	}
}
