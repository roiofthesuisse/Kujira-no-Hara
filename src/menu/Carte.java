package menu;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilitaire.graphismes.Graphismes;

public class Carte extends ElementDeMenu {
	private static final Logger LOG = LogManager.getLogger(Carte.class);
	
	private int numeroCarte = -1;
	private BufferedImage imageTotale;
	private int xCarteActuel = -1, yCarteActuel = -1;
	private int xCarteFutur, yCarteFutur;
	private BufferedImage icone;
	
	protected Carte (final int x, final int y, final int largeur, final int hauteur, final int id) {
		super(
				id, 
				false, // non s�lectionnable
				x, y, 
				null,
				null,
				null
		);
		this.largeur = largeur;
		this.hauteur = hauteur;
	}
	
	@Override
	public BufferedImage getImage() {
		if (xCarteActuel<0 || yCarteActuel<0) {
			// Cadrage initial
			this.xCarteActuel = xCarteFutur;
			this.yCarteActuel = yCarteFutur;
		} else if (xCarteActuel != xCarteFutur || yCarteActuel != yCarteFutur) {
			// Deplacement du cadrage
			this.xCarteActuel = 2*xCarteActuel/3 + xCarteFutur/3;
			this.yCarteActuel = 2*yCarteActuel/3 + yCarteFutur/3;
		}
		
		int xCadrage = xCarteActuel-largeur/2;
		int yCadrage = yCarteActuel-hauteur/2;
		
		// Ne pas d�passer les bords de l'image
		if (xCadrage < 0) {
			xCadrage = 0;
		}
		if (yCadrage < 0) {
			yCadrage = 0;
		}
		if (xCadrage + largeur > imageTotale.getWidth()) {
			xCadrage = imageTotale.getWidth() - largeur;
		}
		if (yCadrage + hauteur > imageTotale.getHeight()) {
			yCadrage = imageTotale.getHeight() - hauteur;
		}
		
		BufferedImage copieImage = Graphismes.clonerUneImage(imageTotale);
		copieImage = Graphismes.superposerImages(copieImage, icone, xCarteFutur+7, yCarteFutur+14); //dessiner l'icone
		BufferedImage sousImage = copieImage.getSubimage(xCadrage, yCadrage, largeur, hauteur);
		return sousImage;
	}

	public void changerCadrage(int numeroCarte, int xCarte, int yCarte, BufferedImage icone) {
		if (this.numeroCarte != numeroCarte) {
			// Nouvelle image
			try {
				this.imageTotale = Graphismes.ouvrirImage("Pictures/Cartes", numeroCarte+".png");
				this.numeroCarte = numeroCarte;
				LOG.info("Nouvelle image de carte : "+numeroCarte);
				
				// reinitialisation des Coordonnees du cadrage
				this.xCarteActuel = -1;
				this.yCarteActuel = -1;
			} catch (IOException e) {
				LOG.error("Impossible d'ouvrir l'image de la carte "+numeroCarte);
			}
		}
		
		// Nouvelles Coordonnees du cadrage
		this.xCarteFutur = xCarte;
		this.yCarteFutur = yCarte;
		
		// Nouvelle icone
		this.icone = icone;
	}

}
