package main;

public class GestionClavier {
	
	/**
	 * Association entre les touches du clavier et leur keycode
	 */
	private class ToucheCode{
		public final static int Z = 90;
		public final static int Q = 81;
		public final static int S = 83;
		public final static int D = 68;
		public final static int ESPACE = 32;
		public final static int ENTREE = 10;
		public final static int O = 79;
		public final static int K = 75;
		public final static int L = 76;
		public final static int M = 77;
	}
	
	/**
	 * Association entre les touches du clavier et leur rôle
	 */
	public class ToucheRole{
		public final static int ACTION = ToucheCode.K;
		public final static int HAUT = ToucheCode.Z;
		public final static int BAS = ToucheCode.S;
		public final static int GAUCHE = ToucheCode.Q;
		public final static int DROITE = ToucheCode.D;
		public final static int ARME_SUIVANTE = ToucheCode.O;
		public final static int RETOUR = ToucheCode.O;
		public final static int ARME_PRECEDENTE = ToucheCode.L;
		public final static int ACTION_SECONDAIRE = ToucheCode.M;
		
	}
	
	public static Boolean toucheConnue(int keycode){
		switch(keycode){
		case ToucheCode.Z : return true; //z
		case ToucheCode.Q : return true; //q
		case ToucheCode.S : return true; //s
		case ToucheCode.D : return true; //d
		case ToucheCode.ESPACE : return true; //espace
		case ToucheCode.ENTREE : return true; //entrée
		case ToucheCode.O : return true; //o
		case ToucheCode.K : return true; //k
		case ToucheCode.L : return true; //l
		case ToucheCode.M : return true; //m
		default : System.out.println("une touche inconnue a été pressée : "+keycode); return false;
		}
	}
	
	/**
	 * On actualise la liste des touches qui sont pressées actuellement.
	 */
	public static void keyPressed(int keycode, Fenetre fenetre){
		if(toucheConnue(keycode)){
			fenetre.lecteur.keyPressed(keycode);
		}
	}

	/**
	 * On actualise la liste des touches qui sont pressées actuellement.
	 */
	public static void keyReleased(Integer keycode, Fenetre fenetre) {
		if(toucheConnue(keycode)){
			fenetre.lecteur.keyReleased(keycode);
		}
	}
	
}
