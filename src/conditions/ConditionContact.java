package conditions;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import commandes.CommandeEvent;
import map.Event;
import map.Heros;

/**
 * Est-ce que le Héros est en contact avec l'Event ?
 * Le contact a deux sens :
 * - si l'Event est traversable, le contact signifie que le Héros est majoritairement superposé à lui ;
 * - si l'Event n'est pas traversable, le contact signifie que le Héros et l'Event se touchent par un côté de la Hitbox.
 */
public class ConditionContact extends Condition  implements CommandeEvent {
	protected static final Logger LOG = LogManager.getLogger(ConditionContact.class);
	
	private final TypeDeContact typeDeContact;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param typeDeContact de la Condition
	 */
	public ConditionContact(final int numero, final TypeDeContact typeDeContact) {
		this.numero = numero;
		this.typeDeContact = typeDeContact;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionContact(final HashMap<String, Object> parametres) {
		this( 
				parametres.containsKey("numero") ? (int) parametres.get("numero") : -1,
				parametres.containsKey("typeDeContact") ? TypeDeContact.obtenirParNom((String) parametres.get("typeDeContact")) : TypeDeContact.SUPERPOSITION_MAJORITAIRE
		);
	}
	
	
	/**
	 * La nature du Contact peut être de plusieurs degrés différents.
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
			public boolean ilYAContactExterne(int xmin1, int xmax1, int ymin1, int ymax1, int xmin2, int xmax2, int ymin2, int ymax2){
				return (xmin1==xmax2 || xmin2==xmax1) && (
						(ymin2<=ymin1&&ymax1<=ymax2) //un segment est dans l'autre
						// segments partiellement superposés :
						|| (ymin2<=ymin1&&ymin1<ymax2&&ymax2<=ymax1)
						|| (ymin1<=ymin2&&ymin2<ymax1&&ymax1<=ymax2)
					)
						||
					(ymin1==ymax2 || ymin2==ymax1) && (
						(xmin2<=xmin1&&xmax1<=xmax2) //un segment est dans l'autre
						// segments partiellement superposés :
						|| (xmin2<=xmin1&&xmin1<xmax2&&xmax2<=xmax1)
						|| (xmin1<=xmin2&&xmin2<xmax1&&xmax1<=xmax2)
					);
			}
			@Override
			public boolean ilYASuperposition(int xmin1, int xmax1, int ymin1, int ymax1, int xmin2, int xmax2, int ymin2, int ymax2){
				return ((xmin2<=xmin1&&xmax1<=xmax2) //vraiment dedans
						|| (xmin1<=xmin2&&xmin2<xmax1&&xmax1<=xmax2) //à cheval (héros à gauche)
						|| (xmin2<=xmin1&&xmin1<xmax2&&xmax2<=xmax1))//à cheval (héros à droite)
							&&
						((ymin2<=ymin1&&ymax1<=ymax2) //vraiment dedans
						|| (ymin1<=ymin2&&ymin2<ymax1&&ymax1<=ymax2)  //à cheval (héros en haut)
						|| (ymin2<=ymin1&&ymin1<ymax2&&ymax2<=ymax1));//à cheval (héros en bas)
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
			public boolean ilYAContactExterne(int xmin1, int xmax1, int ymin1, int ymax1, int xmin2, int xmax2, int ymin2, int ymax2){
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
			public boolean ilYASuperposition(int xmin1, int xmax1, int ymin1, int ymax1, int xmin2, int xmax2, int ymin2, int ymax2){
				final int largeurHitbox = Math.min(xmax1 - xmin1, xmax2 - xmin2);
				final int hauteurHitbox = Math.min(ymax1 - ymin1, ymax2 - ymin2);
				return ((xmin2<=xmin1&&xmax1<=xmax2) //vraiment dedans
						|| (xmin1<=xmin2&&xmin2<xmax1&&xmax1<=xmax2 && 2*(xmax1-xmin2)>=largeurHitbox) //à cheval mais beaucoup (héros à gauche)
						|| (xmin2<=xmin1&&xmin1<xmax2&&xmax2<=xmax1 && 2*(xmax2-xmin1)>=largeurHitbox))//à cheval mais beaucoup (héros à droite)
							&&
						((ymin2<=ymin1&&ymax1<=ymax2) //vraiment dedans
						|| (ymin1<=ymin2&&ymin2<ymax1&&ymax1<=ymax2 && 2*(ymax1-ymin2)>=hauteurHitbox)  //à cheval mais beaucoup (héros en haut)
						|| (ymin2<=ymin1&&ymin1<ymax2&&ymax2<=ymax1 && 2*(ymax2-ymin1)>=hauteurHitbox));//à cheval mais beaucoup (héros en bas)
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
			public boolean ilYAContactExterne(int xmin1, int xmax1, int ymin1, int ymax1, int xmin2, int xmax2, int ymin2, int ymax2) {
				return ((xmin1==xmax2 || xmin2==xmax1) && (ymin2<=ymin1&&ymax1<=ymax2)) //vraiment dedans
					|| ((ymin1==ymax2 || ymin2==ymax1) && (xmin2<=xmin1&&xmax1<=xmax2)); //vraiment dedans
			}
			@Override
			public boolean ilYASuperposition(int xmin1, int xmax1, int ymin1, int ymax1, int xmin2, int xmax2, int ymin2, int ymax2) {
				return (xmin2<=xmin1&&xmax1<=xmax2) //vraiment dedans
					&& (ymin2<=ymin1&&ymax1<=ymax2); //vraiment dedans
			}
		};
		
		private final String nom;
		
		public boolean ilYAContact(boolean traversable, int xmin1, int xmax1, int ymin1, int ymax1, int xmin2, int xmax2, int ymin2, int ymax2) {
			if (traversable) {
				return ilYASuperposition(xmin1, xmax1, ymin1, ymax1, xmin2, xmax2, ymin2, ymax2);
			} else {
				return ilYAContactExterne(xmin1, xmax1, ymin1, ymax1, xmin2, xmax2, ymin2, ymax2);
			}
		}
		
		/** 
		 * Si aucun des deux events n'est traversable.
		 */
		public abstract boolean ilYAContactExterne(int xmin1, int xmax1, int ymin1, int ymax1, int xmin2, int xmax2, int ymin2, int ymax2);
		
		/** 
		 * Si un des deux events est traversable.
		 */
		public abstract boolean ilYASuperposition(int xmin1, int xmax1, int ymin1, int ymax1, int xmin2, int xmax2, int ymin2, int ymax2);
		
		TypeDeContact(final String nom){
			this.nom = nom;
		}
		
		public static TypeDeContact obtenirParNom(String nom) {
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
		
		final Event event = this.page.event;
		final Heros heros = event.map.heros;
		
		//pas de contact si l'un des deux saute
		if (heros.saute || event.saute) {
			return false;
		}
		
		final int xmin1 = heros.x;
		final int xmax1 = heros.x+heros.largeurHitbox;
		final int ymin1 = heros.y;
		final int ymax1 = heros.y+heros.hauteurHitbox;
		final int xmin2 = event.x;
		final int xmax2 = event.x+event.largeurHitbox;
		final int ymin2 = event.y;
		final int ymax2 = event.y+event.hauteurHitbox;
		
		//deux interprétations très différentes du Contact selon la traversabilité de l'event
		boolean modeTraversable = event.traversableActuel || heros.traversableActuel;
		return this.typeDeContact.ilYAContact(modeTraversable, xmin1, xmax1, ymin1, ymax1, xmin2, xmax2, ymin2, ymax2);
	}
	
	/**
	 * C'est une Condition qui implique une proximité avec le Héros.
	 * @return true 
	 */
	public final boolean estLieeAuHeros() {
		return true;
	}
	
}
