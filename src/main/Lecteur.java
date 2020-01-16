package main;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilitaire.GestionClavier.ToucheRole;
import utilitaire.graphismes.Graphismes;
import map.PageCommune;

/**
 * Le lecteur peut être un lecteur de menu ou un lecteur de map.
 * Le rôle du lecteur est d'afficher dans la fenêtre la succession des écrans au cours du temps.
 */
public abstract class Lecteur {	
	protected static final Logger LOG = LogManager.getLogger(Lecteur.class);
	
	private BufferedImage ecranAtuel;
	
	/**
	 * Durée minimale d'une frame (en millisecondes).
	 * Il est interdit qu'une frame dure moins longtemps, afin que l'animation soit compréhensible.
	 * La frame peut durer plus longtemps si l'ordinateur a du mal à faire tourner le bousin.
	 */
	public static final int DUREE_FRAME = 30;
	
	/**
	 * Est-ce que le Lecteur est allumé ?
	 * Si le Lecteur est allumé, l'affichage de l'écran est actualisé en continu.
	 * Si le Lecteur est éteint, l'affichage arrête sa boucle, et la Fenêtre doit démarrer un nouveau Lecteur.
	 */
	public boolean allume = true;
	
	/** 
	 * Numéro de la frame en cours (en temps réel).
	 * Une frame est une image affichée à l'écran pendant une courte durée.
	 * A chaque nouveau Lecteur, on repart de 0.
	 */
	public int frameActuelle = 0;
	
	/**
	 * Le rôle d'un Lecteur est de calculer l'écran à afficher dans la Fenêtre.
	 * @param frame de l'écran à calculer
	 * @return écran à afficher maintenant
	 */
	public abstract  BufferedImage calculerAffichage(int frame);
	
	/**
	 * Prévenir le Lecteur qu'une touche a été pressée, pour qu'il en déduise une action à faire.
	 * La touche envoyée ne dois pas être nulle !
	 * @param touchePressee touche pressée
	 */
	public abstract void keyPressed(ToucheRole touchePressee);
	
	/**
	 * Prévenir le Lecteur qu'une touche a été relachée, pour qu'il en déduise une action à faire.
	 * La touche envoyée ne dois pas être nulle !
	 * @param toucheRelachee touche relachée
	 */
	public abstract void keyReleased(ToucheRole toucheRelachee);
	
	/**
	 * Faire une capture de l'écran actuel.
	 */
	public final void faireUneCaptureDEcran() {
		final String nomImage = "capture ecran kujira "+new Date().getTime();
		LOG.info("Capture d'écran : "+nomImage);
		Graphismes.sauvegarderImage(this.ecranAtuel, nomImage);
	}
	
	/***
	 * Récupérer le nom du BGM qu'il faut jouer pour accompagner le Menu ou la Map
	 */
	protected abstract void lireMusique();
	
	/**
	 * Démarrer le Lecteur.
	 * Le Lecteur est allumé, la musique est lue, un écran est affiché à chaque frame.
	 * Si jamais "allume" est mis à false, le Lecteur s'arrête et la Fenetre devra lancer le prochain Lecteur.
	 */
	public final void demarrer() {
		this.allume = true;
		LOG.info("-------------------------------------------------------------");
		LOG.info("Un nouveau "+this.typeDeLecteur()+" vient d'être démarré.");
		//démarrer la Musique de la Map/du Menu
		lireMusique();
		
		long t1, t2;
		long dureeEffectiveDeLaFrame;
		while (this.allume) {
			t1 = System.currentTimeMillis();
			this.ecranAtuel = calculerAffichage(this.frameActuelle);
			t2 = System.currentTimeMillis();
			dureeEffectiveDeLaFrame = t2-t1;
			if (dureeEffectiveDeLaFrame < Lecteur.DUREE_FRAME) {
				//si l'affichage a pris moins de temps que la durée attendue, on attend que la frame se termine
				try {
					Thread.sleep(Lecteur.DUREE_FRAME-dureeEffectiveDeLaFrame);
				} catch (InterruptedException e) {
					LOG.error("La boucle de lecture du jeu dans Lecteur.demarrer() n'a pas réussi son sleep().", e);
				}
			} else {
				//l'affichage a pris trop de temps
				LOG.warn("Irrespect de la vitesse d'affichage : "+dureeEffectiveDeLaFrame+"/"+Lecteur.DUREE_FRAME);
			}
			Main.fenetre.actualiserAffichage(this.ecranAtuel);
			this.frameActuelle++;
		}
		//si on est ici, c'est qu'une Commande Event a éteint le Lecteur
		//la Fenêtre va devoir le remplacer par le futur Lecteur (si elle en a un de rechange)
		LOG.info("Le "+typeDeLecteur()+" actuel vient d'être arrêté à la frame "+this.frameActuelle);
	}

	/**
	 * Quel est le type de ce Lecteur ?
	 * @return LecteurMap ou LecteurMenu
	 */
	protected abstract String typeDeLecteur();
	
}
