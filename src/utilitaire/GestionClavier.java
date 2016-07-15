package utilitaire;

/**
 * Enumération des touches utilisées par le jeu
 */
public abstract class GestionClavier {
	
	/**
	 * Association entre les touches du clavier et leur keycode
	 */
	private static enum ToucheClavier {
		Z(90, "Z"),
		Q(81, "Q"),
		S(83, "S"),
		D(68, "D"),
		ESPACE(32, "ESPACE"),
		ENTREE(10, "ENTREE"),
		O(79, "O"),
		K(75, "K"),
		L(76, "L"),
		M(77, "M");
		
		public final int keycode;
		public final String nom;
		
		private ToucheClavier(int keycode, String nom) {
			this.keycode = keycode;
			this.nom = nom;
		}
	}
	
	/**
	 * Association entre les touches du clavier et leur rôle
	 */
	public static enum ToucheRole {
		ACTION(ToucheClavier.K, "ACTION"),
		HAUT(ToucheClavier.Z, "HAUT"),
		BAS(ToucheClavier.S, "BAS"),
		GAUCHE(ToucheClavier.Q, "GAUCHE"),
		DROITE(ToucheClavier.D, "DROITE"),
		ARME_SUIVANTE(ToucheClavier.O, "ARME_SUIVANTE"),
		PAGE_MENU_SUIVANTE(ToucheClavier.O, "PAGE_MENU_SUIVANTE"),
		ARME_PRECEDENTE(ToucheClavier.L, "ARME_PRECEDENTE"),
		PAGE_MENU_PRECEDENTE(ToucheClavier.L, "PAGE_MENU_PRECEDENTE"),
		ACTION_SECONDAIRE(ToucheClavier.M, "ACTION_SECONDAIRE"),
		MENU(ToucheClavier.ENTREE, "MENU");
		
		public final ToucheClavier touche;
		public boolean pressee = false;
		private final String nom;
		
		private ToucheRole(ToucheClavier touche, String nom) {
			this.touche = touche;
			this.nom = nom;
		}
		
		/**
		 * Obtenir une Touche à partir de son keycode.
		 * @param keycode de la Touche
		 * @return Touche qui a ce keycode
		 */
		public static ToucheRole getToucheRole(int keycode) {
			for (ToucheRole role : ToucheRole.values()) {
				if (keycode == role.touche.keycode) {
					return role;
				}
			}
			return null;
		}
		
		/**
		 * Obtenir une Touche à partir de son nom.
		 * @param nom de la Touche
		 * @return Touche qui porte ce nom
		 */
		public static ToucheRole getToucheRole(String nom) {
			for (ToucheRole role : ToucheRole.values()) {
				if (role.nom.equals(nom) || role.touche.nom.equals(nom)) {
					return role;
				}
			}
			return null;
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
		System.out.println("une touche inconnue a été pressée : "+keycode); 
		return false;
	}
	
}
