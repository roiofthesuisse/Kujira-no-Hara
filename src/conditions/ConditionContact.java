package conditions;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import commandes.CommandeEvent;
import map.Event;
import map.Map;
import map.Passabilite;

/**
 * Est-ce que le Heros est en contact avec l'Event ?
 * Le contact a deux sens :
 * - si l'Event est traversable, le contact signifie que le Heros est majoritairement superpos� a lui ;
 * - si l'Event n'est pas traversable, le contact signifie que le Heros et l'Event se touchent par un c�t� de la Hitbox.
 */
public class ConditionContact extends Condition  implements CommandeEvent {
	protected static final Logger LOG = LogManager.getLogger(ConditionContact.class);
	
	private final Object idEvent1;
	private final Object idEvent2;
	private final TypeDeContact typeDeContact;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param idEvent1 identifiant (Numero ou nom) du premier Event ; par d�faut, le Heros
	 * @param idEvent2 identifiant (Numero ou nom) du second Event ; par d�faut, cet Event
	 * @param typeDeContact de la Condition
	 */
	public ConditionContact(final int numero, final Object idEvent1, final Object idEvent2, final TypeDeContact typeDeContact) {
		this.numero = numero;
		this.idEvent1 = idEvent1;
		this.idEvent2 = idEvent2;
		this.typeDeContact = typeDeContact;
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public ConditionContact(final HashMap<String, Object> parametres) {
		this( 
				parametres.containsKey("numero") ? (int) parametres.get("numero") : -1,
				parametres.containsKey("idEvent1") ? parametres.get("idEvent1") : 0, //par d�faut, le Heros
				parametres.containsKey("idEvent2") ? parametres.get("idEvent2") : null, //par defaut, cet Event
				parametres.containsKey("typeDeContact") ? TypeDeContact.obtenirParNom((String) parametres.get("typeDeContact")) : TypeDeContact.SUPERPOSITION_MAJORITAIRE
		);
	}
	
	
	/**
	 * La nature du Contact peut etre de plusieurs degr�s differents.
	 */
	public enum TypeDeContact {
		/**
		 * <ul>
		 * <li>Cas traversable : les hitboxes se croisent.</li>
		 * <li>Cas non-traversable : les bords des hitboxes se touchent.</li>
		 * </ul>
		 */
		SUPERPOSITION_PARTIELLE("partiel") {
			@Override
			public boolean ilYAContactExterne(final int xmin1, final int xmax1, final int ymin1, final int ymax1, 
					final int xmin2, final int xmax2, final int ymin2, final int ymax2) {
				return (xmin1==xmax2 || xmin2==xmax1) && (
						(ymin2<=ymin1&&ymax1<=ymax2) //un segment est dans l'autre
						// segments partiellement superpos�s :
						|| (ymin2<=ymin1&&ymin1<ymax2&&ymax2<=ymax1)
						|| (ymin1<=ymin2&&ymin2<ymax1&&ymax1<=ymax2)
					)
						||
					(ymin1==ymax2 || ymin2==ymax1) && (
						(xmin2<=xmin1&&xmax1<=xmax2) //un segment est dans l'autre
						// segments partiellement superpos�s :
						|| (xmin2<=xmin1&&xmin1<xmax2&&xmax2<=xmax1)
						|| (xmin1<=xmin2&&xmin2<xmax1&&xmax1<=xmax2)
					);
			}
			@Override
			public boolean ilYASuperposition(final int xmin1, final int xmax1, final int ymin1, final int ymax1, 
					final int xmin2, final int xmax2, final int ymin2, final int ymax2) {
				return ((xmin2<=xmin1&&xmax1<=xmax2) //vraiment dedans
						|| (xmin1<=xmin2&&xmin2<xmax1&&xmax1<=xmax2) //� cheval (Heros a gauche)
						|| (xmin2<=xmin1&&xmin1<xmax2&&xmax2<=xmax1))//� cheval (Heros a droite)
							&&
						((ymin2<=ymin1&&ymax1<=ymax2) //vraiment dedans
						|| (ymin1<=ymin2&&ymin2<ymax1&&ymax1<=ymax2)  //� cheval (Heros en haut)
						|| (ymin2<=ymin1&&ymin1<ymax2&&ymax2<=ymax1)); //� cheval (Heros en bas)
			}
		},
		/**
		 * <ul>
		 * <li>Cas traversable : les hitboxes se recouvrent majoritairement.</li>
		 * <li>Cas non-traversable : les bords des hitboxes se recouvrent majoritairement.</li>
		 * </ul>
		 */
		SUPERPOSITION_MAJORITAIRE("majoritaire") {
			@Override
			public boolean ilYAContactExterne(final int xmin1, final int xmax1, final int ymin1, final int ymax1, 
					final int xmin2, final int xmax2, final int ymin2, final int ymax2) {
				final int largeurHitbox = Math.min(xmax1 - xmin1, xmax2 - xmin2);
				final int hauteurHitbox = Math.min(ymax1 - ymin1, ymax2 - ymin2);
				return (xmin1==xmax2 || xmin2==xmax1) && (
						(ymin2<=ymin1&&ymax1<=ymax2) //vraiment dedans
					// majoritairement dedans :
					|| (ymin2<=ymin1&&ymin1<ymax2&&ymax2<=ymax1 && 2*(ymax2-ymin1)>=hauteurHitbox)
					|| (ymin1<=ymin2&&ymin2<ymax1&&ymax1<=ymax2 && 2*(ymax1-ymin2)>=hauteurHitbox)
					)
						||
					(ymin1==ymax2 || ymin2==ymax1) && (
						(xmin2<=xmin1&&xmax1<=xmax2) //vraiment dedans
					// majoritairement dedans :
					|| (xmin2<=xmin1&&xmin1<xmax2&&xmax2<=xmax1 && 2*(xmax2-xmin1)>=largeurHitbox)
					|| (xmin1<=xmin2&&xmin2<xmax1&&xmax1<=xmax2 && 2*(xmax1-xmin2)>=largeurHitbox));
			}
			@Override
			public boolean ilYASuperposition(final int xmin1, final int xmax1, final int ymin1, final int ymax1, 
					final int xmin2, final int xmax2, final int ymin2, final int ymax2) {
				final int largeurHitbox = Math.min(xmax1 - xmin1, xmax2 - xmin2);
				final int hauteurHitbox = Math.min(ymax1 - ymin1, ymax2 - ymin2);
				return ((xmin2<=xmin1&&xmax1<=xmax2) //vraiment dedans
						|| (xmin1<=xmin2&&xmin2<xmax1&&xmax1<=xmax2 && 2*(xmax1-xmin2)>=largeurHitbox) //� cheval mais beaucoup (Heros a gauche)
						|| (xmin2<=xmin1&&xmin1<xmax2&&xmax2<=xmax1 && 2*(xmax2-xmin1)>=largeurHitbox))//� cheval mais beaucoup (Heros a droite)
							&&
						((ymin2<=ymin1&&ymax1<=ymax2) //vraiment dedans
						|| (ymin1<=ymin2&&ymin2<ymax1&&ymax1<=ymax2 && 2*(ymax1-ymin2)>=hauteurHitbox)  //� cheval mais beaucoup (Heros en haut)
						|| (ymin2<=ymin1&&ymin1<ymax2&&ymax2<=ymax1 && 2*(ymax2-ymin1)>=hauteurHitbox)); //� cheval mais beaucoup (Heros en bas)
			}
		},
		/**
		 * <ul>
		 * <li>Cas traversable : une hitbox est incluse dans l'autre.</li>
		 * <li>Cas non-traversable : un bord d'une hitbox est inclus dans un bord de l'autre hitbox.</li>
		 * </ul>
		 */
		SUPERPOSITION_INCLUSIVE("inclusif") {
			@Override
			public boolean ilYAContactExterne(final int xmin1, final int xmax1, final int ymin1, final int ymax1, 
					final int xmin2, final int xmax2, final int ymin2, final int ymax2) {
				return ((xmin1==xmax2 || xmin2==xmax1) && (ymin2<=ymin1&&ymax1<=ymax2)) //vraiment dedans
					|| ((ymin1==ymax2 || ymin2==ymax1) && (xmin2<=xmin1&&xmax1<=xmax2)); //vraiment dedans
			}
			@Override
			public boolean ilYASuperposition(final int xmin1, final int xmax1, final int ymin1, final int ymax1, 
					final int xmin2, final int xmax2, final int ymin2, final int ymax2) {
				return (xmin2<=xmin1&&xmax1<=xmax2) //vraiment dedans
					&& (ymin2<=ymin1&&ymax1<=ymax2); //vraiment dedans
			}
		};
		
		private final String nom;
		
		/**
		 * Y a-t-il contact entre les deux Events ?
		 * @param traversable si l'un des deux Events est traversable
		 * @param xmin1 Coordonnee x minimale de l'Event 1
		 * @param xmax1 Coordonnee x maximale de l'Event 1
		 * @param ymin1 Coordonnee y minimale de l'Event 1
		 * @param ymax1 Coordonnee y maximale de l'Event 1
		 * @param xmin2 Coordonnee x minimale de l'Event 2
		 * @param xmax2 Coordonnee x maximale de l'Event 2
		 * @param ymin2 Coordonnee y minimale de l'Event 2
		 * @param ymax2 Coordonnee y maximale de l'Event 2
		 * @return true si contact, false sinon
		 */
		public boolean ilYAContact(final boolean traversable, final int xmin1, final int xmax1, final int ymin1, 
				final int ymax1, final int xmin2, final int xmax2, final int ymin2, final int ymax2) {
			if (traversable) {
				return ilYASuperposition(xmin1, xmax1, ymin1, ymax1, xmin2, xmax2, ymin2, ymax2);
			} else {
				return ilYAContactExterne(xmin1, xmax1, ymin1, ymax1, xmin2, xmax2, ymin2, ymax2);
			}
		}
		
		/** 
		 * Si aucun des deux events n'est traversable, le contact prend en compte les bords des Events.
		 * @param xmin1 Coordonnee x minimale de l'Event 1
		 * @param xmax1 Coordonnee x maximale de l'Event 1
		 * @param ymin1 Coordonnee y minimale de l'Event 1
		 * @param ymax1 Coordonnee y maximale de l'Event 1
		 * @param xmin2 Coordonnee x minimale de l'Event 2
		 * @param xmax2 Coordonnee x maximale de l'Event 2
		 * @param ymin2 Coordonnee y minimale de l'Event 2
		 * @param ymax2 Coordonnee y maximale de l'Event 2
		 * @return true si contact, false sinon
		 */
		public abstract boolean ilYAContactExterne(int xmin1, int xmax1, int ymin1, int ymax1, int xmin2, int xmax2, int ymin2, int ymax2);
		
		/** 
		 * Si un des deux events est traversable, le contact se fait par recouvrement
		 * @param xmin1 Coordonnee x minimale de l'Event 1
		 * @param xmax1 Coordonnee x maximale de l'Event 1
		 * @param ymin1 Coordonnee y minimale de l'Event 1
		 * @param ymax1 Coordonnee y maximale de l'Event 1
		 * @param xmin2 Coordonnee x minimale de l'Event 2
		 * @param xmax2 Coordonnee x maximale de l'Event 2
		 * @param ymin2 Coordonnee y minimale de l'Event 2
		 * @param ymax2 Coordonnee y maximale de l'Event 2
		 * @return true si contact, false sinon
		 */
		public abstract boolean ilYASuperposition(int xmin1, int xmax1, int ymin1, int ymax1, int xmin2, int xmax2, int ymin2, int ymax2);
		
		/**
		 * Constructeur explicite
		 * @param nom du type de contact
		 */
		TypeDeContact(final String nom) {
			this.nom = nom;
		}
		
		/**
		 * recuperer un type de contact a partir de son nom.
		 * @param nom du type de contact
		 * @return le type de contact voulu, ou null si le nom n'existe pas
		 */
		public static TypeDeContact obtenirParNom(final String nom) {
			for (TypeDeContact type : TypeDeContact.values()) {
				if (type.nom.equals(nom)) {
					return type;
				}
			}
			LOG.error("Type de contact inconnu !");
			return null;
		}
	}
	
	@Override
	public final boolean estVerifiee() {
		try {
			final int pageActive = this.page.event.pageActive.numero;
			final int cettePage = this.page.numero;
			//il faut d'abord que la page ne soit pas ouverte
			if (pageActive==cettePage) {
				return false;
			}
		} catch (NullPointerException e) {
			//pas de page active
		}

		final ArrayList<Event> events1 = recupererLesEventsCandidats(idEvent1);
		final ArrayList<Event> events2 = recupererLesEventsCandidats(idEvent2);
		final Map map = this.page.event.map;
		for (Event event1 : events1) {
			final boolean event1SurUnDecorPassable = map.lEventEstSurUnDecorTraversable(event1, event1.x, event1.y, 
					event1.x+event1.largeurHitbox, event1.y+event1.hauteurHitbox);
			for (Event event2 : events2) {
				final boolean event2SurUnDecorPassable = map.lEventEstSurUnDecorTraversable(event2, event2.x, event2.y,
						event2.x+event2.largeurHitbox, event2.y+event2.hauteurHitbox);
				
				//pas de contact si l'un des deux saute
				if (!event1.saute && !event2.saute) {
					
					final int xmin1 = event1.x;
					final int xmax1 = event1.x + event1.largeurHitbox;
					final int ymin1 = event1.y;
					final int ymax1 = event1.y + event1.hauteurHitbox;
					
					final int xmin2 = event2.x;
					final int xmax2 = event2.x + event2.largeurHitbox;
					final int ymin2 = event2.y;
					final int ymax2 = event2.y + event2.hauteurHitbox;
					
					// deux interpretations tr�s differentes du Contact selon la traversabilit� de l'event
					// si un event traversable est situe sur un d�cor impraticable, le contact est solide
					final boolean modeTraversable = event2.traversableActuel == Passabilite.PASSABLE && event2SurUnDecorPassable
							|| event1.traversableActuel == Passabilite.PASSABLE && event1SurUnDecorPassable
							// si l'un des deux est le Heros
							|| (event1.id == 0 && event2.traversableParLeHerosActuel) && event2SurUnDecorPassable
							|| (event2.id == 0 && event1.traversableParLeHerosActuel) && event1SurUnDecorPassable;
					
					if (this.typeDeContact.ilYAContact(modeTraversable, xmin1, xmax1, ymin1, ymax1, xmin2, xmax2, ymin2, ymax2)) {
						//au moins un couple d'events doit etre en contact
						return true;
					}
					
				}
				
			}
		}
		return false;
	}
	
	/**
	 * C'est une Condition qui implique une proximit� avec le Heros.
	 * @return true 
	 */
	public final boolean estLieeAuHeros() {
		//l'un des deux events en Contact est-il le Heros ?
		return idEvent1 instanceof Integer && (Integer) idEvent1 == 0 || idEvent2 instanceof Integer && (Integer) idEvent2 == 0;
	}
	
}
