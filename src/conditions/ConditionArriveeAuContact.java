package conditions;

import map.Event;

/**
 * Est-ce que le Héros vient d'entrer en contact avec l'Event ?
 * Le contact a deux sens :
 * - si l'Event est traversable, le contact signifie que le Héros est majoritairement superposé à lui ;
 * - si l'Event n'est pas traversable, le contact signifie que le Héros et l'Event se touchent par un côté de la Hitbox.
 */
public class ConditionArriveeAuContact extends Condition {

	@Override
	public final boolean estVerifiee() {
		final Event event = this.page.event;
		if ( event.frameDuContact != event.map.lecteur.frameActuelle ) {
			//on n'est pas à jour ! on calcule s'il y a contact :
			final ConditionContact conditionContactMaintenant = new ConditionContact();
			conditionContactMaintenant.page = this.page;
			conditionContactMaintenant.numero = this.numero;
			final boolean leHerosEstAuContactDeLEventMaintenant = conditionContactMaintenant.estVerifiee();
			
			event.estAuContactDuHerosAvant = event.estAuContactDuHerosMaintenant;
			event.estAuContactDuHerosMaintenant = leHerosEstAuContactDeLEventMaintenant;
			event.frameDuContact = event.map.lecteur.frameActuelle;
		}
		
		//on est à jour
		return event.estAuContactDuHerosMaintenant && !event.estAuContactDuHerosAvant;
	}

}
