package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import son.LecteurAudio;
import utilitaire.GestionClavier.ToucheRole;
import map.LecteurMap;
import menu.LecteurMenu;

/**
 * Le lecteur peut être un lecteur de menu ou un lecteur de map.
 * Le rôle du lecteur est d'afficher dans la fenêtre la succession des écrans au cours du temps.
 */
public abstract class Lecteur {	
	private BufferedImage ecranAtuel;
	public Fenetre fenetre = null;
	
	/**
	 * Durée minimale d'une frame (en millisecondes).
	 * Il est interdit qu'une frame dure moins longtemps, afin que l'animation soit compréhensible.
	 * La frame peut durer plus longtemps si l'ordinateur a du mal à faire tourner le bousin.
	 */
	private static final long DUREE_FRAME = 30;
	
	public static final int TYPE_DES_IMAGES = BufferedImage.TYPE_INT_ARGB;
	
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
	 * Afficher ou masquer la boîte des Messages.
	 */
	public static boolean afficherBoiteMessage = true;
	
	/**
	 * Le rôle d'un Lecteur est de calculer l'écran à afficher dans la Fenêtre.
	 * @param frame de l'écran à calculer
	 * @return écran à afficher maintenant
	 */
	public abstract  BufferedImage calculerAffichage(final int frame);
	
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
	 * Produire un rectangle noir pour l'afficher comme écran
	 * @return un rectangle noir
	 */
	public final BufferedImage ecranNoir() {
		BufferedImage image = new BufferedImage(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN, Lecteur.TYPE_DES_IMAGES);
		Graphics2D graphics = image.createGraphics();
		graphics.setPaint(Color.black);
		graphics.fillRect(0, 0, Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN);
		return image;
	}
	
	/**
	 * Produire un rectangle vide
	 * @param largeur du rectangle
	 * @param hauteur du rectangle
	 * @return un rectangle sans couleur
	 */
	public final BufferedImage imageVide(final int largeur, final int hauteur) {
		BufferedImage image = new BufferedImage(largeur, hauteur, Lecteur.TYPE_DES_IMAGES);
		final Color couleur = new Color(0, 0, 0, 0);
		Graphics2D graphics = image.createGraphics();
		graphics.setPaint(couleur);
		graphics.fillRect(0, 0, largeur, hauteur);
		return image;
	}
	
	/**
	 * Produire un rectangle vide pour l'afficher comme écran
	 * @return un rectangle vide
	 */
	public final BufferedImage ecranVide() {
		return imageVide(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN);
	}
	
	/***
	 * Récupérer le nom du BGM qu'il faut jouer pour accompagner le Manu ou la Map
	 * @return nom du BGM à jouer
	 */
	public final String getNomBgm() {
		if (this instanceof LecteurMap) {
			return ((LecteurMap) this).map.nomBGM;
		} else if (this instanceof LecteurMenu) {
			if (((LecteurMenu) this).menu==null) {
				System.err.println("Le menu est null pour le lecteur");
			}
			return ((LecteurMenu) this).menu.nomBGM;
		}
		return null;
	}
	
	/**
	 * Démarrer le Lecteur.
	 * Le Lecteur est allumé, la musique est lue, un écran est affiché à chaque frame.
	 * Si jamais "allume" est mis à false, le Lecteur s'arrête et la Fenetre devra lancer le prochain Lecteur.
	 */
	public final void demarrer() {
		this.allume = true;
		final String typeLecteur = this.getClass().getName().equals(LecteurMap.class.getName()) ? "LecteurMap" : "LecteurMenu";
		System.out.println("-------------------------------------------------------------");
		System.out.println("Un nouveau "+typeLecteur+" vient d'être démarré.");
		LecteurAudio.playBgm(getNomBgm(), 0.5f);
		//TODO LecteurAudio.playBgs(getNomBgs(), 1.0f);
		
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
					System.err.println("La boucle de lecture du jeu dans Lecteur.demarrer() n'a pas réussi son sleep().");
					e.printStackTrace();
				}
			}
			this.fenetre.actualiserAffichage(this.ecranAtuel);
			this.frameActuelle++;
		}
		//si on est ici, c'est qu'une Commande Event a éteint le Lecteur
		//la Fenêtre va devoir le remplacer par le futur Lecteur (si elle en a un de rechange)
		System.out.println("Le "+typeLecteur+" actuel vient d'être arrêté à la frame "+this.frameActuelle);
	}
	
}
