package main;

import java.util.ArrayList;

import map.Hitbox;

public class Arme {
	public int id;
	public String nom;
	public String nomImageAttaque;
	public ArrayList<Integer> framesDAnimation;
	private static Arme[] armesDuJeu = new Arme[10];
	public Hitbox hitbox;
	
	/**
	 * @param id
	 * @param nom
	 * @param nomImageAttaque
	 * @param framesDAnimation
	 */
	public Arme(int id, String nom, String nomImageAttaque, ArrayList<Integer> framesDAnimation, Hitbox hitbox){
		this.id = id;
		this.nom = nom;
		this.nomImageAttaque = nomImageAttaque;
		this.framesDAnimation = framesDAnimation;
		this.hitbox = hitbox;
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
			Hitbox hitboxEpee = new Hitbox(32,64);
			int idEpee = 0;
			Arme epee = new Arme(idEpee,"epee","Jiyounasu AttaqueEpee character.png",framesAnimationEpee,hitboxEpee);
			Arme.armesDuJeu[idEpee] = epee;
			Partie.idArmesPossedees.add(idEpee);
		//eventail
			ArrayList<Integer> framesAnimationEventail = new ArrayList<Integer>();
			framesAnimationEventail.add(0);framesAnimationEventail.add(1);framesAnimationEventail.add(1);framesAnimationEventail.add(1);framesAnimationEventail.add(0);framesAnimationEventail.add(0);
			Hitbox hitboxEventail = new Hitbox(5*32,16);
			int idEventail = 1;
			Arme eventail = new Arme(idEventail,"eventail","Jiyounasu Eventail character.png",framesAnimationEventail,hitboxEventail);
			Arme.armesDuJeu[idEventail] = eventail;
			Partie.idArmesPossedees.add(idEventail);
		//autre
			//TODO autres armes
	}
	
}
