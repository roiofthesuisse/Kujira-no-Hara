package map;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import main.Fenetre;
import utilitaire.InterpreteurDeJson;
import utilitaire.graphismes.Graphismes;

/**
 * Le Tileset associe à chaque brique de décor une passabilité et une altitude.
 */
public class Tileset {
	//constantes
	public static final int LARGEUR_TILESET = 8; //chaque ligne de Tileset contient 8 carreaux
	
	/** nom du fichier JSON du Tileset */
	public final String nom;
	/** nom de l'image du Tileset */
	private final String nomImage;
	/** image complète du Tileset */
	private final BufferedImage image;
	/** Peut-on marcher sur cette case ? Ou bien est-ce un obstacle ? */
	private final boolean[] passabilite;
	/** Altitude d'affichage du carreau (0:sol, 2:héros) */
	private final int[] altitude; 
	/** carreaux découpés dans l'image du Tileset */
	public final BufferedImage[] carreaux;
	
	private final String nomImagePanorama;
	public BufferedImage imagePanorama;
	private final String nomImageBrouillard;
	public BufferedImage imageBrouillard;
	public HashMap<Integer, Autotile> autotiles;
	
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
		this.image = Graphismes.ouvrirImage("Tilesets", this.nomImage);
		final int nombreDeLignesTileset = (image.getHeight()/Fenetre.TAILLE_D_UN_CARREAU);
		final int nombreDeCarreauxTileset = nombreDeLignesTileset * LARGEUR_TILESET;
		
		//découpage des carreaux
		this.carreaux = new BufferedImage[nombreDeCarreauxTileset];
		for (int i = 0; i<LARGEUR_TILESET; i++) {
			for (int j = 0; j<nombreDeLignesTileset; j++) {
				carreaux[LARGEUR_TILESET*j + i] = this.image.getSubimage(i*Fenetre.TAILLE_D_UN_CARREAU, j*Fenetre.TAILLE_D_UN_CARREAU, Fenetre.TAILLE_D_UN_CARREAU, Fenetre.TAILLE_D_UN_CARREAU);
			}
		}
		
		//lecture des passabilités
		this.passabilite = new boolean[nombreDeCarreauxTileset];
		final JSONArray jsonPassabilite = jsonTileset.getJSONArray("passabilite");
		try {
			for (int i = 0; i<nombreDeCarreauxTileset; i++ ) {
				this.passabilite[i] = ((Integer) jsonPassabilite.get(i)) == 0;
			}
		} catch (JSONException e) {
			System.err.println("Incompatibilité entre le tableau des passabilités du Tileset JSON et de l'image du Tileset : "+this.nom);
			e.printStackTrace();
		}
		
		//lecture des altitudes
		this.altitude = new int[nombreDeCarreauxTileset];
		final JSONArray jsonAltitude = jsonTileset.getJSONArray("altitude");
		try {
			for (int i = 0; i<nombreDeCarreauxTileset; i++ ) {
				this.altitude[i] = (Integer) jsonAltitude.get(i);
			}
		} catch (JSONException e) {
			System.err.println("Incompatibilité entre le tableau des altitudes du Tileset JSON et de l'image du Tileset : "+this.nom);
			e.printStackTrace();
		}
		
		//panorama
		this.nomImagePanorama = jsonTileset.getString("panorama");
		try {
			this.imagePanorama = Graphismes.ouvrirImage("Panoramas", this.nomImagePanorama);
		} catch (IOException e) {
			System.err.println("Pas d'image de panorama pour le Tileset : "+this.nom);
			this.imagePanorama = null;
		}
		
		//brouillard
		//TODO opacité du brouillard, couleur, mode de superposition...
		this.nomImageBrouillard = jsonTileset.getString("brouillard");
		try {
			this.imageBrouillard = Graphismes.ouvrirImage("Fogs", this.nomImageBrouillard);
		} catch (IOException e) {
			System.err.println("Pas d'image de brouillard pour le Tileset : "+this.nom);
			this.imageBrouillard = null;
		}
		
		//autotiles
		this.autotiles = InterpreteurDeJson.chargerAutotiles(jsonTileset, this);
		
		//TODO type de terrain
	}
	
	/**
	 * La case de décor est-elle un obstacle ?
	 * @param numeroDeLaCaseDansLeTileset numérotation du Tileset
	 * @return true si obstacle, false si passable
	 */
	public final boolean laCaseEstUnObstacle(final int numeroDeLaCaseDansLeTileset) {
		if (numeroDeLaCaseDansLeTileset >= 0) { //case normale
			return !this.passabilite[numeroDeLaCaseDansLeTileset]; 
		} else if (numeroDeLaCaseDansLeTileset < -1) { //autotile
			return !this.autotiles.get((Integer) numeroDeLaCaseDansLeTileset).passabilite;
		} else { //case vide
			return false;
		}
	}
	
	/**
	 * Récupère l'altitude associée à ce carreau de Tileset.
	 * @param numeroCarreau dans le Tileset
	 * @return true si obstacle, false si passable
	 */
	public final int altitudeDeLaCase(final int numeroCarreau) {
		if (numeroCarreau >= -1) { //case normale
			return this.altitude[numeroCarreau];
		} else { //autotile
			return this.autotiles.get(numeroCarreau).altitude;
		}
	}

}
