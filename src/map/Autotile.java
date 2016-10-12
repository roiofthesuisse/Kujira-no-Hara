package map;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Un Autotile est un carreau liable. 
 * Selon la nature de ses voisins, il prendra une apparence différente.
 */
public class Autotile {
	// Constantes
	public static int NOMBRE_VIGNETTES_AUTOTILE_ANIME = 4;
	
	private String nomImage;
	private BufferedImage image;
	public boolean passabilite;
	public int altitude;
	public boolean anime;
	public ArrayList<Integer> cousins;
	
	private Autotile (final String nomImage, final boolean passabilite, final int altitude, final ArrayList<Integer> cousins) {
		this.nomImage = nomImage;
		//this.image = f(nomImage);
		//this.anime = f(this.image);

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
		return null;
	}
}
