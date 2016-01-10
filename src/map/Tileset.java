package map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import main.Fenetre;
import utilitaire.InterpreteurDeJson;

/**
 * Le Tileset associe à chaque brique de décor une passabilité et une altitude.
 */
public class Tileset {
	//constantes
	public static final int LARGEUR_TILESET = 8; //chaque ligne de Tileset contient 8 carreaux
	
	public final String nom;
	private final String nomImage;
	private final BufferedImage image;
	public final boolean[] passabilite;
	public final int[] altitude; //0:sol 1:objetSurSol 2:objetSurObjetSurSol 3:heros 4:objetSurHeros 5:ObjetSurObjetSurHeros
	public final BufferedImage[] carreaux;
	
	/**
	 * Constructeur explicite
	 * @param nomTileset nom de l'image de décor
	 * @throws IOException erreur lors de l'ouverture du fichier JSON ou des images
	 */
	public Tileset(final String nomTileset) throws IOException {
		this.nom = nomTileset;

		final JSONObject jsonTileset = InterpreteurDeJson.ouvrirJsonTileset(nomTileset);
		
		//image du tileset
		this.nomImage = jsonTileset.getString("nomImage");
		this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Tilesets\\"+this.nomImage));
		final int nombreDeLignesTileset = (image.getHeight()/Fenetre.TAILLE_D_UN_CARREAU);
		final int nombreDeCarreauxTileset = nombreDeLignesTileset * LARGEUR_TILESET;
		System.out.println("nombreDeCarreauxTileset:"+nombreDeCarreauxTileset);
		
		//découpage des carreaux
		this.carreaux = new BufferedImage[nombreDeCarreauxTileset];
		for (int i = 0; i<LARGEUR_TILESET; i++) {
			for (int j = 0; j<nombreDeLignesTileset; j++) {
				carreaux[LARGEUR_TILESET*j + i] = this.image.getSubimage(i*Fenetre.TAILLE_D_UN_CARREAU, j*Fenetre.TAILLE_D_UN_CARREAU, Fenetre.TAILLE_D_UN_CARREAU, Fenetre.TAILLE_D_UN_CARREAU);
			}
		}
		
		//lecture des passabilités
		this.passabilite = new boolean[nombreDeCarreauxTileset];
		JSONArray jsonPassabilite = jsonTileset.getJSONArray("passabilite");
		try {
			for (int i = 0; i<nombreDeCarreauxTileset; i++ ) {
				this.passabilite[i] = ((Integer) jsonPassabilite.get(i)) == 0;
			}
		} catch (JSONException e) {
			System.err.println("Incompatibilité entre le tableau des passabilités du Tileset JSON et de l'image du Tileset.");
			e.printStackTrace();
		}
		
		//lecture des altitudes
		this.altitude = new int[nombreDeCarreauxTileset];
		JSONArray jsonAltitude = jsonTileset.getJSONArray("altitude");
		try {
			for (int i = 0; i<nombreDeCarreauxTileset; i++ ) {
				this.altitude[i] = (Integer) jsonAltitude.get(i);
			}
		} catch (JSONException e) {
			System.err.println("Incompatibilité entre le tableau des altitudes du Tileset JSON et de l'image du Tileset.");
			e.printStackTrace();
		}
		
		//TODO panorama
		
		//TODO brouillard
		
		//TODO autotiles
		
		//TODO type de terrain
	}
}
