package conditions;

import commandes.CommandeEvent;
import main.Main;
import map.Event;
import map.Heros;
import map.Hitbox;

/**
 * Vérifier si le Héros est armé, et si l'Event se trouve dans la zone d'action de son Arme
 */
public class ConditionDansZoneDAttaque extends Condition implements CommandeEvent {
	
	/**
	 * Constructeur vide
	 */
	public ConditionDansZoneDAttaque() {
		
	}
	
	@Override
	public final boolean estVerifiee() {
		final boolean estCeQueLeHerosAUneArme = (getPartieActuelle().nombreDArmesPossedees > 0);
		if (estCeQueLeHerosAUneArme) {
			final Heros heros = this.page.event.map.heros;
			final Event event = this.page.event;
			final Hitbox zoneDAttaqueDuHeros = Main.getPartieActuelle().getArmeEquipee().hitbox;
			final boolean reponse = zoneDAttaqueDuHeros.estDansZoneDAttaque(event, heros);
			return reponse;
		} else {
			return false;
		}
	}
	
	/**
	 * C'est une Condition qui implique une proximité avec le Héros.
	 * @return true 
	 */
	public final boolean estLieeAuHeros() {
		return true;
	}

}