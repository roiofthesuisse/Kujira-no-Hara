package map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main.Fenetre;
import main.Lecteur;

/**
 * Un Autotile est un carreau liable. 
 * Selon la nature de ses voisins, il prendra une apparence différente.
 */
public class Autotile {
	// Constantes
	public static final int LARGEUR_AUTOTILE_FIXE = 3*Fenetre.TAILLE_D_UN_CARREAU;
	public static final int NOMBRE_VIGNETTES_AUTOTILE_ANIME = 4;
	public static final int LARGEUR_AUTOTILE_ANIME = NOMBRE_VIGNETTES_AUTOTILE_ANIME*LARGEUR_AUTOTILE_FIXE;
	public static final int HAUTEUR_AUTOTILE = 4*Fenetre.TAILLE_D_UN_CARREAU;
	public static final int TAILLE_MORCEAU = Fenetre.TAILLE_D_UN_CARREAU/2;
	
	private String nomImage;
	private BufferedImage image;
	public boolean passabilite;
	public int altitude;
	public boolean anime;
	public ArrayList<Integer> cousins;
	
	/**
	 * Constructeur explicite
	 * @param nomImage nom de l'image de l'Autotile
	 * @param passabilite peut-on marcher sur cette case ?
	 * @param altitude d'affichage dans le décor
	 * @param cousins autres autotiles qui peuvent se lier à celui-ci
	 * @throws IOException impossible de charger l'image de l'Autotile
	 */
	public Autotile(final String nomImage, final boolean passabilite, final int altitude, final ArrayList<Integer> cousins) throws IOException {
		this.nomImage = nomImage;
		this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Autotile\\"+this.nomImage));
		final int largeurAutotile = this.image.getWidth();
		if (largeurAutotile == LARGEUR_AUTOTILE_FIXE) {
			this.anime = false;
		} else if (largeurAutotile == LARGEUR_AUTOTILE_ANIME) {
			this.anime = true;
		} else {
			System.err.println("L'Autotile n'a pas la bonne taille : "+largeurAutotile);
			throw new IOException();
		}
		final int hauteurAutotile = this.image.getHeight();
		if (hauteurAutotile != HAUTEUR_AUTOTILE) {
			System.err.println("L'Autotile n'a pas la bonne taille : "+largeurAutotile);
			throw new IOException();
		}

		this.passabilite = passabilite;
		this.altitude = altitude;
		this.cousins = cousins;
	}
	
	/**
	 * Calculer l'apparence du carreau en fonction de son voisinnage.
	 * @param xEcran
	 * @param yEcran
	 * @param largeurMap
	 * @param hauteurMap
	 * @param numeroCarreau
	 * @param layer
	 * @return carreau avec la bonne apparence
	 */
	public BufferedImage calculerAutotile(final int xEcran, final int yEcran, final int largeurMap, final int hauteurMap, final int numeroCarreau, final int[][] layer) {
		// On veut déterminer les connexions du carreau
		boolean connexionBas = false;
		boolean connexionGauche = false;
		boolean connexionDroite = false;
		boolean connexionHaut = false;
		boolean connexionBasGauche = false;
		boolean connexionBasDroite = false;
		boolean connexionHautGauche = false;
		boolean connexionHautDroite = false;
		
		// On considère que le bord de l'écran est liable lui aussi
		if (yEcran == 0) {
			//bord supérieur de l'écran
			connexionHaut = true;
			connexionHautGauche = true;
			connexionHautDroite = true;
		} else if (yEcran == largeurMap-1) {
			//bord inférieur de l'écran
			connexionBas = true;
			connexionBasGauche = true;
			connexionBasDroite = true;
		}
		if (xEcran == 0) {
			//bord gauche de l'écran
			connexionGauche = true;
			connexionHautGauche = true;
			connexionBasGauche = true;
		} else if (xEcran == hauteurMap-1) {
			//bord droit de l'écran
			connexionDroite = true;
			connexionHautDroite = true;
			connexionBasDroite = true;
		}
		
		// On regarde les connexions possibles avec les carreaux voisins
		int numeroVoisin;
		if (!connexionHaut) {
			numeroVoisin = layer[xEcran][yEcran-1];
			connexionHaut = numeroVoisin == numeroCarreau || this.cousins.contains(numeroVoisin);
		}
		if (!connexionBas) {
			numeroVoisin = layer[xEcran][yEcran+1];
			connexionBas = numeroVoisin == numeroCarreau || this.cousins.contains(numeroVoisin);
		}
		if (!connexionGauche) {
			numeroVoisin = layer[xEcran-1][yEcran];
			connexionGauche = numeroVoisin == numeroCarreau || this.cousins.contains(numeroVoisin);
		}
		if (!connexionDroite) {
			numeroVoisin = layer[xEcran+1][yEcran];
			connexionDroite = numeroVoisin == numeroCarreau || this.cousins.contains(numeroVoisin);
		}
		
		// Selon les cas, ceux-là ne sont pas forcément utiles pour dessiner le carreau
		if (!connexionHautGauche) {
			numeroVoisin = layer[xEcran-1][yEcran-1];
			connexionHaut = numeroVoisin == numeroCarreau || this.cousins.contains(numeroVoisin);
		}
		if (!connexionHautDroite) {
			numeroVoisin = layer[xEcran+1][yEcran-1];
			connexionDroite = numeroVoisin == numeroCarreau || this.cousins.contains(numeroVoisin);
		}
		if (!connexionBasGauche) {
			numeroVoisin = layer[xEcran-1][yEcran+1];
			connexionGauche = numeroVoisin == numeroCarreau || this.cousins.contains(numeroVoisin);
		}
		if (!connexionBasDroite) {
			numeroVoisin = layer[xEcran+1][yEcran+1];
			connexionBas = numeroVoisin == numeroCarreau || this.cousins.contains(numeroVoisin);
		}
		
		BufferedImage resultat = new BufferedImage(Fenetre.TAILLE_D_UN_CARREAU, Fenetre.TAILLE_D_UN_CARREAU, Lecteur.TYPE_DES_IMAGES);
		final Graphics2D g2d = (Graphics2D) resultat.createGraphics();
		int xMorceauChoisi = 0;
		int yMorceauChoisi = 0;
		g2d.drawImage(resultat, 0, 0, TAILLE_MORCEAU, TAILLE_MORCEAU, 
				xMorceauChoisi, yMorceauChoisi, xMorceauChoisi+TAILLE_MORCEAU, yMorceauChoisi+TAILLE_MORCEAU, null);
		
		//TODO calculer le morceau à choisir dans l'image de l'autotile en fonction des connexions
		
		
		return resultat;
	}
}
