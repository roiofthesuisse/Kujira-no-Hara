package conditions;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import commandes.CommandeEvent;
import conditions.ConditionContact.TypeDeContact;
import main.Main;
import map.Event;

/**
 * Est-ce que le Héros vient d'entrer en contact avec l'Event ?
 * Le contact a deux sens :
 * - si l'Event est traversable, le contact signifie que le Héros est majoritairement superposé à lui ;
 * - si l'Event n'est pas traversable, le contact signifie que le Héros et l'Event se touchent par un côté de la Hitbox.
 */
public class ConditionArriveeAuContact extends Condition  implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(ConditionArriveeAuContact.class);
	
	private final TypeDeContact typeDeContact;
	
	/**
	 * Constructeur explicite
	 * @param numero de la condition
	 * @param typeDeContact (voir Contact)
	 */
	private ConditionArriveeAuContact(final int numero, final TypeDeContact typeDeContact) {
		this.numero = numero;
		this.typeDeContact = typeDeContact;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionArriveeAuContact(final HashMap<String, Object> parametres) {
		this(
				parametres.containsKey("numero") 
						? (int) parametres.get("numero") 
						: -1,
				parametres.containsKey("typeDeContact") 
						? TypeDeContact.obtenirParNom((String) parametres.get("typeDeContact")) 
						: TypeDeContact.SUPERPOSITION_MAJORITAIRE
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		final Event event = this.page.event;
		
		final int frameActuelle = Main.lecteur.frameActuelle;
		if ( event.frameDuContact != frameActuelle) {
			//on n'est pas à jour ! on calcule s'il y a contact :
			final ConditionContact conditionContactMaintenant = new ConditionContact(this.numero, 0, null, this.typeDeContact);
			conditionContactMaintenant.page = this.page;
			final boolean leHerosEstAuContactDeLEventMaintenant = conditionContactMaintenant.estVerifiee();
			
			event.estAuContactDuHerosAvant = event.estAuContactDuHerosMaintenant;
			event.estAuContactDuHerosMaintenant = leHerosEstAuContactDeLEventMaintenant;
			event.frameDuContact = frameActuelle;
		}
		//on est à jour
		
		//on était d'emblée sur l'Event à la première frame du LecteurMap
		if (frameActuelle <= 1 && event.estAuContactDuHerosMaintenant) {
			LOG.debug("Condition ArriveeAuContact ignorée si c'est la position initiale du Héros sur la Map.");
			event.estAuContactDuHerosAvant = true;
		}
		
		return event.estAuContactDuHerosMaintenant && !event.estAuContactDuHerosAvant;
	}
	
	/**
	 * C'est une Condition qui implique une proximité avec le Héros.
	 * @return true 
	 */
	public final boolean estLieeAuHeros() {
		return true;
	}

}
