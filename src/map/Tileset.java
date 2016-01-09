package map;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.imageio.ImageIO;

import main.Fenetre;

/**
 * Le Tileset associe à chaque brique de décor une passabilité et une altitude.
 */
public class Tileset {
	//constantes
	public static final int LARGEUR_TILESET = 8; //chaque ligne de Tileset contient 8 carreaux
	
	public String nom;
	private BufferedImage image;
	public boolean[] passabilite;
	int[] altitude; //0:sol 1:objetSurSol 2:objetSurObjetSurSol 3:heros 4:objetSurHeros 5:ObjetSurObjetSurHeros
	public BufferedImage[] carreaux;
	
	/**
	 * Constructeur explicite
	 * @param nomTileset nom de l'image de décor
	 */
	public Tileset(final String nomTileset) {
		this.nom = nomTileset;
		try {
			this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Tilesets\\"+nomTileset));
			int nombreDeLignesTileset = (image.getHeight()/Fenetre.TAILLE_D_UN_CARREAU);
			int nombreDeCarreauxTileset = nombreDeLignesTileset * LARGEUR_TILESET;
			
			this.carreaux = new BufferedImage[nombreDeCarreauxTileset];
			for (int i = 0; i<LARGEUR_TILESET; i++) {
				for (int j = 0; j<nombreDeLignesTileset; j++) {
					carreaux[LARGEUR_TILESET*j + i] = this.image.getSubimage(i*Fenetre.TAILLE_D_UN_CARREAU, j*Fenetre.TAILLE_D_UN_CARREAU, Fenetre.TAILLE_D_UN_CARREAU, Fenetre.TAILLE_D_UN_CARREAU);
				}
			}
			
			this.passabilite = new boolean[nombreDeCarreauxTileset];
			//par défaut les passabilités sont vraie pour la case 0, faux pour les autres
			passabilite[0] = true;
			//passabilités obtenues par le fichier csv du tileset
			BufferedReader buff;
			try {
				//TODO remplacer ça par un JSON qui contient TOUTES les autres infos sur le tileset (brouillard, etc.)
				buff = new BufferedReader(new FileReader(".\\ressources\\Data\\Tilesets\\"+nomTileset+"\\passabilite.csv"));
				String ligne = buff.readLine();
				if (ligne != null) {
					String[] listePassabilite = ligne.split(";", -1); //le -1 de split signifie qu'il gère les cases vides
					for (int i = 0; i<listePassabilite.length; i++) {
						passabilite[i] = listePassabilite[i].equals("0");
					}
				}
				buff.close();
			} catch (Exception e) {
				System.out.println("Erreur lors de l'ouverture de la couche 0 de la map :");
				e.printStackTrace();
			}
			
			//TODO extraire les altitudes d'un fichier JSON
			this.altitude = new int[nombreDeCarreauxTileset];
			for (int i = 0; i<nombreDeCarreauxTileset; i++) {
				altitude[i] = 0;
			}
		} catch (Exception e) {
			System.out.println("Erreur lors du chargement du tileset :");
			e.printStackTrace();
		}
	}
}
