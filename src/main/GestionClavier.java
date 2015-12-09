package main;

public class GestionClavier {
	
	/**
	 * Association entre les touches du clavier et leur keycode
	 */
	private class Touche{
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
	public static int codeToucheHaut(){
		return Touche.Z;
	}
	public static int codeToucheBas(){
		return Touche.S;
	}
	public static int codeToucheGauche(){
		return Touche.Q;
	}
	public static int codeToucheDroite(){
		return Touche.D;
	}
	public static int codeToucheAction() {
		return Touche.K;
	}
	
	public static Boolean toucheConnue(int keycode){
		switch(keycode){
		case Touche.Z : return true; //z
		case Touche.Q : return true; //q
		case Touche.S : return true; //s
		case Touche.D : return true; //d
		case Touche.ESPACE : return true; //espace
		case Touche.ENTREE : return true; //entrée
		case Touche.O : return true; //o
		case Touche.K : return true; //k
		case Touche.L : return true; //l
		case Touche.M : return true; //m
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
