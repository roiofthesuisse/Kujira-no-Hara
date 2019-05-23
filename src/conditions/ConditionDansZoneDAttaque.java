package conditions;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import commandes.CommandeEvent;
import main.Main;
import map.Event;
import map.Hitbox;

/**
 * Vérifier si le Héros est armé, et si l'Event se trouve dans la zone d'action de son Arme.<br>
 * Il est possible de paramétrer cette condition pour faire attaquer egalement un event quelconque 
 * par un autre vent quelconque avec une zone d'attaque specifiee.
 */
public class ConditionDansZoneDAttaque extends Condition implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(ConditionDansZoneDAttaque.class);

    private final Integer idEventAttaquant;
    private final Integer idEventCible;
    private final String nomZone;

    /**
     * Constructeur implicite : le Heros attaque cet Event avec son Arme actuellement equipee
     */
    public ConditionDansZoneDAttaque() {
        this(-1, // une condition hors code event n'a pas besoin de numero
                0, // par defaut, Heros
                null, // par defaut, cet Event 
                null // par defaut, zone de l'Arme equipee du Heros
        );
    }

    /**
     * Constructeur explicite
     * @param numero de la Condition
     * @param idEventAttaquant identifiant de l'Event qui attaque
     * @param idEventCible identifiant de l'Event qui recoit l'attaque
     * @param idZone nom de la zone d'attaque
     */
    public ConditionDansZoneDAttaque(final int numero, final Integer idEventAttaquant, final Integer idEventCible,
            final String nomZone) {
        this.numero = numero;
        this.idEventAttaquant = idEventAttaquant;
        this.idEventCible = idEventCible;
        this.nomZone = nomZone;
}

    /**
     * Constructeur générique
     * @param parametres liste de paramètres issus de JSON
     */
    public ConditionDansZoneDAttaque(final HashMap<String, Object> parametres) {
        this(parametres.containsKey("numero") ? (int) parametres.get("numero") : -1,
                parametres.containsKey("eventAttaquant") ? (int) parametres.get("eventAttaquant") : 0, // par defaut, Heros
                parametres.containsKey("eventCible") ? (int) parametres.get("eventCible") : null, // par defaut, cet Event 
                parametres.containsKey("zone") ? (String) parametres.get("zone") : null // par defaut, zone de l'Arme equipee du Heros
        );
    }

    @Override
    public final boolean estVerifiee() {
        Event eventAttaquant = null;
        Event eventCible = null;
        Hitbox zoneDAttaque = null;
        
        // On cherche l'Event qui attaque
        if (this.idEventAttaquant == 0) {
            // C'est le Heros qui attaque
            eventAttaquant = this.page.event.map.heros;
            boolean leHerosAttaqueAvecSonArme = (this.nomZone == null);
            if (leHerosAttaqueAvecSonArme) {
                final boolean leHerosAUneArme = (getPartieActuelle().nombreDArmesPossedees > 0);
                if (!leHerosAUneArme) {
                    // Le Heros n'a pas d'Arme !
                    return false; 
                }
                zoneDAttaque = Main.getPartieActuelle().getArmeEquipee().hitbox;
            }
        } else if (this.idEventAttaquant == null) {
            // C'est cet Event qui attaque
            eventAttaquant = this.page.event;
        } else {
            // C'est un Event en particulier qui attaque
            eventAttaquant = this.page.event.map.eventsHash.get(this.idEventAttaquant);
        }
        
        // On cherche l'event qui recoit l'attaque
        if (this.idEventCible == null) {
            // C'est cet Event qui recoit l'attaque
            eventCible = this.page.event;
        } else if (this.idEventCible == 0) {
            // C'est le Heros qui recoit l'attaque
            eventCible = this.page.event.map.heros;
        } else {
            // C'est un Event en particulier qui recoit l'attaque
            eventCible = this.page.event.map.eventsHash.get(this.idEventCible);
        }
        
        if (zoneDAttaque == null) {
            if (this.nomZone == null) {
                LOG.error("Zone d'attaque non spécifiée !");
                return false;
            } else {
                // TODO trouver la zone d'attaque a partir de son nom
                // zoneDAttaque = ;
            }
        }
        
        return zoneDAttaque.estDansZoneDAttaque(eventCible, eventAttaquant);
    }

    /**
     * C'est une Condition qui implique une proximité avec le Héros.
     * @return true 
     */
    public final boolean estLieeAuHeros() {
        return true;
    }

}
