package main;

public class GestionClavier {
	
	public static String nomTouche(int keycode){
		String nom = "inconnu";
		switch(keycode){
		case 32 : nom="espace";break;
		case 10 : nom="entree";break;
		case 90 : nom="z";break;
		case 81 : nom="q";break;
		case 83 : nom="s";break;
		case 68 : nom="d";break;
		case 38 : nom="haut";break;
		case 37 : nom="gauche";break;
		case 40 : nom="bas";break;
		case 39 : nom="droite";break;
		case 79 : nom="o";break;
		case 75 : nom="k";break;
		case 76 : nom="l";break;
		case 77 : nom="m";break;
		default : System.out.println(keycode);
		}
		return nom;
	}
	
	public static int codeToucheHaut(){
		return 90;
	}
	public static int codeToucheBas(){
		return 83;
	}
	public static int codeToucheGauche(){
		return 81;
	}
	public static int codeToucheDroite(){
		return 68;
	}
	public static int codeToucheAction() {
		return 75;
	}
	
	public static Boolean toucheConnue(int keycode){
		switch(keycode){
		case 32 : return true; //espace
		case 10 : return true; //entrée
		case 90 : return true; //z
		case 81 : return true; //q
		case 83 : return true; //s
		case 68 : return true; //d
		case 38 : return true; //haut
		case 37 : return true; //gauche
		case 40 : return true; //bas
		case 39 : return true; //droite
		case 79 : return true; //o
		case 75 : return true; //k
		case 76 : return true; //l
		case 77 : return true; //m
		default : System.out.println(""+keycode); return false;
		}
	}
	
	public static void keyPressed(int keycode, Fenetre fenetre){
		if(toucheConnue(keycode)){
			fenetre.lecteur.keyPressed(keycode);
		}
	}

	public static void keyReleased(Integer keycode, Fenetre fenetre) {
		if(toucheConnue(keycode)){
			fenetre.lecteur.keyReleased(keycode);
		}
	}
	
}
