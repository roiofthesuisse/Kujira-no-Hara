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
	
	protected Carte (final int x, final int y, final int largeur, final int hauteur, final int id) {
		super(
				id, 
				false, // non sélectionnable
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
			// Déplacement du cadrage
			this.xCarteActuel = 2*xCarteActuel/3 + xCarteFutur/3;
			this.yCarteActuel = 2*yCarteActuel/3 + yCarteFutur/3;
		}
		return imageTotale.getSubimage(xCarteActuel, yCarteActuel, largeur, hauteur);
	}

	public void changerCadrage(int numeroCarte, int xCarte, int yCarte) {
		if (this.numeroCarte != numeroCarte) {
			// Nouvelle image
			try {
				this.imageTotale = Graphismes.ouvrirImage("Pictures/Cartes", numeroCarte+".png");
				this.numeroCarte = numeroCarte;
				LOG.info("Nouvelle image de carte : "+numeroCarte);
				
				// Réinitialisation des coordonnées du cadrage
				this.xCarteActuel = -1;
				this.yCarteActuel = -1;
			} catch (IOException e) {
				LOG.error("Impossible d'ouvrir l'image de la carte "+numeroCarte);
			}
		}
		
		// Nouvelles coordonnées du cadrage
		this.xCarteFutur = xCarte;
		this.yCarteFutur = yCarte;
		
		// Ne pas dépasser l'image
		if (xCarte+largeur > imageTotale.getWidth()) {
			this.xCarteFutur = imageTotale.getWidth() - largeur;
		}
		if (yCarte+hauteur > imageTotale.getHeight()) {
			this.yCarteFutur = imageTotale.getHeight() - hauteur;
		}
	}

}
