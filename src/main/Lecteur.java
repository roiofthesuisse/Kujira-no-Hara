package main;

import java.awt.image.BufferedImage;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilitaire.GestionClavier.ToucheRole;
import utilitaire.graphismes.Graphismes;

/**
 * Le lecteur peut etre un lecteur de menu ou un lecteur de map.
 * Le role du lecteur est d'afficher dans la Fenetre la succession des ecrans au cours du temps.
 */
public abstract class Lecteur {	
	protected static final Logger LOG = LogManager.getLogger(Lecteur.class);
	
	private BufferedImage ecranAtuel;
	
	/**
	 * Duree minimale d'une frame (en millisecondes).
	 * Il est interdit qu'une frame dure moins longtemps, afin que l'animation soit compr�hensible.
	 * La frame peut durer plus longtemps si l'ordinateur a du mal a faire tourner le bousin.
	 */
	public static final int DUREE_FRAME = 30;
	
	/**
	 * Est-ce que le Lecteur est allume ?
	 * Si le Lecteur est allume, l'affichage de l'ecran est actualise en continu.
	 * Si le Lecteur est eteint, l'affichage arrete sa boucle, et la Fenetre doit Demarrer un nouveau Lecteur.
	 */
	public boolean allume = true;
	
	/** 
	 * Numero de la frame en cours (en temps reel).
	 * Une frame est une image affichee a l'ecran pendant une courte Duree.
	 * A chaque nouveau Lecteur, on repart de 0.
	 */
	public int frameActuelle = 0;
	
	/**
	 * Le role d'un Lecteur est de calculer l'ecran a afficher dans la Fenetre.
	 * @param frame de l'ecran a calculer
	 * @return ecran a afficher maintenant
	 */
	public abstract  BufferedImage calculerAffichage(int frame);
	
	/**
	 * Pr�venir le Lecteur qu'une touche a ete pressee, pour qu'il en d�duise une action a faire.
	 * La touche envoyee ne dois pas etre nulle !
	 * @param touchePressee touche pressee
	 */
	public abstract void keyPressed(ToucheRole touchePressee);
	
	/**
	 * Pr�venir le Lecteur qu'une touche a ete relach�e, pour qu'il en d�duise une action a faire.
	 * La touche envoyee ne dois pas etre nulle !
	 * @param toucheRelachee touche relach�e
	 */
	public abstract void keyReleased(ToucheRole toucheRelachee);
	
	/**
	 * Faire une capture de l'ecran actuel.
	 */
	public final void faireUneCaptureDEcran() {
		final String nomImage = "capture ecran kujira "+new Date().getTime();
		LOG.info("Capture d'ecran : "+nomImage);
		Graphismes.sauvegarderImage(this.ecranAtuel, nomImage);
	}
	
	/***
	 * recuperer le nom du BGM qu'il faut jouer pour accompagner le Menu ou la Map
	 */
	protected abstract void lireMusique();
	
	/**
	 * Demarrer le Lecteur.
	 * Le Lecteur est allume, la musique est lue, un ecran est affiche a chaque frame.
	 * Si jamais "allume" est mis a false, le Lecteur s'arrete et la Fenetre devra lancer le prochain Lecteur.
	 */
	public final void demarrer() {
		this.allume = true;
		LOG.info("-------------------------------------------------------------");
		LOG.info("Un nouveau "+this.typeDeLecteur()+" vient d'etre d�marr�.");
		//Demarrer la Musique de la Map/du Menu
		lireMusique();
		
		long t1, t2;
		long dureeEffectiveDeLaFrame;
		while (this.allume) {
			t1 = System.currentTimeMillis();
			this.ecranAtuel = calculerAffichage(this.frameActuelle);
			t2 = System.currentTimeMillis();
			dureeEffectiveDeLaFrame = t2-t1;
			if (dureeEffectiveDeLaFrame < Lecteur.DUREE_FRAME) {
				//si l'affichage a pris moins de temps que la Duree attendue, on attend que la frame se termine
				try {
					Thread.sleep(Lecteur.DUREE_FRAME-dureeEffectiveDeLaFrame);
				} catch (InterruptedException e) {
					LOG.error("La boucle de lecture du jeu dans Lecteur.demarrer() n'a pas reussi son sleep().", e);
				}
			} else {
				//l'affichage a pris trop de temps
				LOG.warn("Irrespect de la vitesse d'affichage : "+dureeEffectiveDeLaFrame+"/"+Lecteur.DUREE_FRAME);
			}
			Main.fenetre.actualiserAffichage(this.ecranAtuel);
			this.frameActuelle++;
		}
		//si on est ici, c'est qu'une Commande Event a eteint le Lecteur
		//la Fenetre va devoir le remplacer par le futur Lecteur (si elle en a un de rechange)
		LOG.info("Le "+typeDeLecteur()+" actuel vient d'etre arrete a la frame "+this.frameActuelle);
	}

	/**
	 * Quel est le type de ce Lecteur ?
	 * @return LecteurMap ou LecteurMenu
	 */
	protected abstract String typeDeLecteur();
	
}
