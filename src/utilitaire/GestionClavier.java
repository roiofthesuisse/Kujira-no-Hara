package utilitaire;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Enumération des touches utilisées par le jeu
 */
public abstract class GestionClavier {
	private static final Logger LOG = LogManager.getLogger(GestionClavier.class);
	
	/**
	 * Association entre les touches du clavier et leur keycode
	 */
	public enum ToucheClavier {
		Z(90, "Z"),
		Q(81, "Q"),
		S(83, "S"),
		D(68, "D"),
		ESPACE(32, "ESPACE"),
		C(67, "C"),
		B(66, "B"),
		ENTREE(10, "ENTREE"),
		ECHAP(27, "ECHAP"),
		O(79, "O"),
		K(75, "K"),
		L(76, "L"),
		M(77, "M");
		
		public final int keycode;
		public final String nom;
		public Integer frameDAppui = null;
		public boolean enfoncee = false;
		
		/**
		 * Constructeur explicite
		 * @param keycode code officiel représentant la touche du clavier
		 * @param nom écrit sur la touche du clavier
		 */
		ToucheClavier(final int keycode, final String nom) {
			this.keycode = keycode;
			this.nom = nom;
		}
	}
	
	/**
	 * Association entre les touches du clavier et leur rôle
	 */
	public enum ToucheRole {
		ACTION("ACTION", ToucheClavier.K),
		HAUT("HAUT", ToucheClavier.Z),
		BAS("BAS", ToucheClavier.S),
		GAUCHE("GAUCHE", ToucheClavier.Q),
		DROITE("DROITE", ToucheClavier.D),
		ARME_SUIVANTE("ARME_SUIVANTE", ToucheClavier.O),
		//PAGE_MENU_SUIVANTE("PAGE_MENU_SUIVANTE", ToucheClavier.O), //TODO ?
		ARME_PRECEDENTE("ARME_PRECEDENTE", ToucheClavier.L),
		//PAGE_MENU_PRECEDENTE("PAGE_MENU_PRECEDENTE", ToucheClavier.L), //TODO ?
		ACTION_SECONDAIRE("ACTION_SECONDAIRE", ToucheClavier.M),
		MENU("MENU", ToucheClavier.ENTREE, ToucheClavier.ECHAP),
		CAPTURE_D_ECRAN("CAPTURE_D_ECRAN", ToucheClavier.C),
		DEBUG("BREAKPOINT", ToucheClavier.B);
		
		private final ToucheClavier[] touches;
		public final String nom;
		
		/**
		 * Constructeur explicite
		 * @param touches du clavier
		 * @param nom du rôle associé à la touche
		 */
		ToucheRole(final String nom, final ToucheClavier... touches) {
			this.touches = touches;
			this.nom = nom;
		}
		
		/**
		 * Obtenir une Touche à partir de son keycode.
		 * @param keycode de la Touche
		 * @return Touche qui a ce keycode
		 */
		public static ToucheRole getToucheRole(final int keycode) {
			for (ToucheRole role : ToucheRole.values()) {
				for (ToucheClavier touche : role.touches) {
					if (keycode == touche.keycode) {
						return role;
					}
				}
			}
			LOG.warn("une touche inconnue a été pressée : "+keycode); 
			return null;
		}
		
		/**
		 * Obtenir une Touche à partir de son nom.
		 * @param nom de la Touche
		 * @return Touche qui porte ce nom
		 */
		public static ToucheRole getToucheRole(final String nom) {
			for (ToucheRole role : ToucheRole.values()) {
				for (ToucheClavier touche : role.touches) {
					if (role.nom.equals(nom) || touche.nom.equals(nom)) {
						return role;
					}
				}
			}
			LOG.error("une touche inconnue a été mentionnée : "+nom); 
			return null;
		}
		
		/**
		 * Une des touches du clavier correspondant à ce rôle est-elle enfoncée.
		 * @return true ou false
		 */
		public boolean enfoncee() {
			for (ToucheClavier touche : this.touches) {
				if (touche.enfoncee) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Frame d'appui d'une des touches du clavier correspondant à ce rôle.
		 * @return la frame
		 */
		public Integer frameDAppui() {
			for (ToucheClavier touche : this.touches) {
				if (touche.frameDAppui != null) {
					return touche.frameDAppui;
				}
			}
			return null;
		}

		/**
		 * Mutateur de l'enfoncement de toutes les touches du clavier correspondant à ce rôle.
		 * @param b noter les touches du clavier comme enfoncées ou non
		 */
		public void enfoncee(final boolean b) {
			for (ToucheClavier touche : this.touches) {
				touche.enfoncee = b;
			}
		}

		/**
		 * Mutateur de la frame d'appui de toutes les touches du clavier correspondant à ce rôle.
		 * @param frame d'appui à assigner aux touches de ce rôle
		 */
		public void frameDAppui(final Integer frame) {
			for (ToucheClavier touche : this.touches) {
				touche.frameDAppui = frame;
			}
		}
	}
	
	/**
	 * Dit si la touche est une touche du clavier utilisée par le jeu.
	 * @param keycode numéro de la touche
	 * @return true si la touche est utile, false sinon
	 */
	public static final boolean toucheConnue(final int keycode) {
		for (ToucheClavier c : ToucheClavier.values()) {
	    	if (c.keycode == keycode) {
	        	return true;
	        }
	    }
		LOG.warn("une touche inconnue a été pressée : "+keycode); 
		return false;
	}
	
}
