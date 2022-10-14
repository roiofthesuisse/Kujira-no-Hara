package conditions;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import commandes.CommandeEvent;
import conditions.ConditionContact.TypeDeContact;
import main.Main;
import map.Event;

/**
 * Est-ce que le H�ros vient d'entrer en contact avec l'Event ?
 * Le contact a deux sens :
 * - si l'Event est traversable, le contact signifie que le H�ros est majoritairement superpos� a lui ;
 * - si l'Event n'est pas traversable, le contact signifie que le H�ros et l'Event se touchent par un c�t� de la Hitbox.
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
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
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
			//on n'est pas a jour ! on calcule s'il y a contact :
			final ConditionContact conditionContactMaintenant = new ConditionContact(this.numero, 0, null, this.typeDeContact);
			conditionContactMaintenant.page = this.page;
			final boolean leHerosEstAuContactDeLEventMaintenant = conditionContactMaintenant.estVerifiee();
			
			event.estAuContactDuHerosAvant = event.estAuContactDuHerosMaintenant;
			event.estAuContactDuHerosMaintenant = leHerosEstAuContactDeLEventMaintenant;
			event.frameDuContact = frameActuelle;
		}
		//on est a jour
		
		//on �tait d'embl�e sur l'Event a la premi�re frame du LecteurMap
		if (frameActuelle <= 1 && event.estAuContactDuHerosMaintenant) {
			LOG.debug("Condition ArriveeAuContact ignor�e si c'est la position initiale du H�ros sur la Map.");
			event.estAuContactDuHerosAvant = true;
		}
		
		return event.estAuContactDuHerosMaintenant && !event.estAuContactDuHerosAvant;
	}
	
	/**
	 * C'est une Condition qui implique une proximit� avec le H�ros.
	 * @return true 
	 */
	public final boolean estLieeAuHeros() {
		return true;
	}

}
