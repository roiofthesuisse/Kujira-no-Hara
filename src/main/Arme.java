package main;

import java.util.ArrayList;

import map.Hitbox;

public class Arme {
	/**
	 * Chaque arme possède un id propre. 
	 * 0 pour l'épée, 1 pour la torche etc.
	 */
	public int id;
	public String nom;
	public String nomImageAttaque;
	public String nomEffetSonoreAttaque;
	/**
	 * L'animation d'attaque est composée de plusieurs images.
	 * Pour faire rester une image plus longtemps à l'écran, l'ajouter plusieurs fois à la liste.
	 * La dernière image de la liste est affichée en premier, car l'affichage est décrémentaire.
	 */
	public ArrayList<Integer> framesDAnimation;
	private static Arme[] armesDuJeu = new Arme[10];
	public Hitbox hitbox;
	/**
	 * A partir de cette frame d'animation, l'attaque commence à avoir un effet.
	 * L'intéraction devient possible avec un ennemi.
	 */
	public int frameDebutCoup;
	/**
	 * A partir de cette frame d'animation, l'attaque arrête d'avoir un effet.
	 * L'intéraction n'est plus possible avec l'ennemi.
	 */
	public int frameFinCoup;
	
	/**
	 * @param id
	 * @param nom
	 * @param nomImageAttaque
	 * @param framesDAnimation
	 */
	public Arme(int id, String nom, String nomImageAttaque, String nomEffetSonoreAttaque, ArrayList<Integer> framesDAnimation, Hitbox hitbox, int frameDebutCoup, int frameFinCoup){
		this.id = id;
		this.nom = nom;
		this.nomImageAttaque = nomImageAttaque;
		this.nomEffetSonoreAttaque = nomEffetSonoreAttaque;
		this.framesDAnimation = framesDAnimation;
		this.hitbox = hitbox;
		this.frameDebutCoup = frameDebutCoup;
		this.frameFinCoup = frameFinCoup;
		
		//on ajoute l'arme à la liste des armes
		armesDuJeu[id] = this;
	}
	
	/**
	 * @param idArme identifiant de l'arme souhaitée
	 * @return arme dont l'identifiant est idArme
	 */
	public static Arme getArme(int idArme) {
		return armesDuJeu[idArme];
	}
	
	/**
	 * initialiser la liste des armes du jeu
	 */
	public static void initialiserLesArmesDuJeu(){
		//TODO importer les armes du jeu à partir d'un fichier texte
		
		//épée
			ArrayList<Integer> framesAnimationEpee = new ArrayList<Integer>();
			framesAnimationEpee.add(3);framesAnimationEpee.add(2);framesAnimationEpee.add(2);framesAnimationEpee.add(1);framesAnimationEpee.add(1);framesAnimationEpee.add(0);framesAnimationEpee.add(0);
			Hitbox hitboxEpee = new Hitbox(24,48);
			int idEpee = 0;
			int frameDebutCoupEpee = 1;
			int frameFinCoupEpee = 5;
			Arme epee = new Arme(idEpee,"epee","Jiyounasu AttaqueEpee character.png", "Epee.wav", framesAnimationEpee,hitboxEpee,frameDebutCoupEpee,frameFinCoupEpee);
			Arme.armesDuJeu[idEpee] = epee;
			
		//eventail
			ArrayList<Integer> framesAnimationEventail = new ArrayList<Integer>();
			framesAnimationEventail.add(0);framesAnimationEventail.add(0);framesAnimationEventail.add(0);framesAnimationEventail.add(1);framesAnimationEventail.add(1);framesAnimationEventail.add(1);
			framesAnimationEventail.add(0);framesAnimationEventail.add(0);
			Hitbox hitboxEventail = new Hitbox(5*32,16);
			int idEventail = 1;
			int frameDebutCoupEventail = 3;
			int frameFinCoupEventail = 7;
			Arme eventail = new Arme(idEventail,"eventail","Jiyounasu Eventail character.png", "Eventail.mp3", framesAnimationEventail,hitboxEventail,frameDebutCoupEventail,frameFinCoupEventail);
			Arme.armesDuJeu[idEventail] = eventail;
			
		//autre
			//TODO autres armes
	}
	
}
