package utilitaire;

import main.Lecteur;

/**
 * Enumération des touches utilisées par le jeu
 */
public abstract class GestionClavier {
	
	/**
	 * Association entre les touches du clavier et leur keycode
	 */
	public abstract class ToucheCode {
		public static final int Z = 90;
		public static final int Q = 81;
		public static final int S = 83;
		public static final int D = 68;
		public static final int ESPACE = 32;
		public static final int ENTREE = 10;
		public static final int O = 79;
		public static final int K = 75;
		public static final int L = 76;
		public static final int M = 77;
	}
	
	/**
	 * Association entre les touches du clavier et leur rôle
	 */
	public abstract class ToucheRole {
		public static final int ACTION = ToucheCode.K;
		public static final int HAUT = ToucheCode.Z;
		public static final int BAS = ToucheCode.S;
		public static final int GAUCHE = ToucheCode.Q;
		public static final int DROITE = ToucheCode.D;
		public static final int ARME_SUIVANTE = ToucheCode.O;
		public static final int RETOUR = ToucheCode.O;
		public static final int ARME_PRECEDENTE = ToucheCode.L;
		public static final int ACTION_SECONDAIRE = ToucheCode.M;
		public static final int MENU = ToucheCode.ENTREE;
	}
	
	/**
	 * Dit si la touche est une touche du clavier utilisée par le jeu.
	 * @param keycode numéro de la touche
	 * @return true si la touche est utile, false sinon
	 */
	public static final Boolean toucheConnue(final int keycode) {
		switch(keycode) {
			case ToucheCode.Z : return true;
			case ToucheCode.Q : return true;
			case ToucheCode.S : return true;
			case ToucheCode.D : return true;
			case ToucheCode.ESPACE : return true;
			case ToucheCode.ENTREE : return true;
			case ToucheCode.O : return true;
			case ToucheCode.K : return true;
			case ToucheCode.L : return true;
			case ToucheCode.M : return true;
			default : System.out.println("une touche inconnue a été pressée : "+keycode); return false;
		}
	}
	
	/**
	 * On actualise la liste des touches qui sont pressées actuellement.
	 * @param keycode numéro de la touche qui a été pressée
	 * @param lecteur qui doit agir en fonction de la touche pressée
	 */
	public static void keyPressed(final int keycode, final Lecteur lecteur) {
		if (toucheConnue(keycode)) {
			lecteur.keyPressed(keycode);
		}
	}

	/**
	 * On actualise la liste des touches qui sont pressées actuellement.
	 * @param keycode numéro de la touche relachée
	 * @param lecteur qui doit agir en fonction de la touche relachée 
	 */
	public static void keyReleased(final Integer keycode, final Lecteur lecteur) {
		if (toucheConnue(keycode)) {
			lecteur.keyReleased(keycode);
		}
	}
	
}
