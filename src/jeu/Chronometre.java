package jeu;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Fenetre;
import menu.Texte;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Le Chronometre s'affiche a l'ecran et indique les secondes restantes.
 * Il peut etre croissant ou d�croissant.
 */
public class Chronometre {
	
	public final boolean croissant;
	public final int secondesAuDepart;
	public int secondes;
	public final long momentDuDemarrage;
	private BufferedImage image;
	
	/**
	 * Constructeur explicite
	 * @param croissant le temps augmente-t-il ?
	 * @param secondes temps actuel
	 */
	public Chronometre (final boolean croissant, final int secondes) {
		this.croissant = croissant;
		this.secondes = secondes;
		this.secondesAuDepart = secondes;
		this.momentDuDemarrage = System.currentTimeMillis();
	}

	public BufferedImage dessinerChronometre(BufferedImage ecran) {
		final int secondeActuelle = calculerLaSecondeActuelle();
		
		// Recalculer l'image du Chronometre
		if (this.image==null || this.secondes!=secondeActuelle) {
			this.image = calculerLImageDuChronometre(secondeActuelle);
		}

		return Graphismes.superposerImages(ecran, this.image, Fenetre.LARGEUR_ECRAN/2, Fenetre.HAUTEUR_ECRAN/3, true, 
				Graphismes.PAS_D_HOMOTHETIE, Graphismes.PAS_D_HOMOTHETIE, 
				Graphismes.OPACITE_MAXIMALE, ModeDeFusion.NORMAL, Graphismes.PAS_DE_ROTATION);
	}
	
	private int calculerLaSecondeActuelle() {
		final int secondesEcoulees = (int) (System.currentTimeMillis() - momentDuDemarrage)/1000;
		final int croissance = croissant ? 1 : -1;
		return Math.max(this.secondesAuDepart + secondesEcoulees * croissance, 0);
	}
	
	private BufferedImage calculerLImageDuChronometre(final int secondeActuelle){
		this.secondes = secondeActuelle;
		String temps = "";
		if(secondeActuelle>=60){
			temps += secondeActuelle/60 + ":";
		}
		temps += String.format("%02d", secondeActuelle%60);
		ArrayList<String> tempsListe = new ArrayList<String>();
		tempsListe.add(temps);
		final Texte texte = new Texte(tempsListe, Color.white, Color.black, Texte.Taille.GRANDE);
		return texte.getImage();
	}
}
