package map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.IIOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.Color;
import main.Fenetre;
import utilitaire.InterpreteurDeJson;
import utilitaire.graphismes.Graphismes;

/**
 * Le Tileset associe à chaque brique de décor une passabilité et une altitude.
 */
public class Tileset {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Tileset.class);
	public static final int LARGEUR_TILESET = 8; //chaque ligne de Tileset contient 8 carreaux
	public static final int PARALLAXE_PAR_DEFAUT = 0; //le Panorama est fixe
	
	/** nom du fichier JSON du Tileset */
	public final String nom;
	/** nom de l'image du Tileset */
	private final String nomImage;
	/** image complète du Tileset */
	private BufferedImage image;
	/** Peut-on marcher sur cette case ? Ou bien est-ce un obstacle ? */
	private final boolean[] passabilite;
	/** Altitude d'affichage du carreau (0:sol, 2:héros) */
	private final int[] altitude; 
	/** Terrain du carreau */
	private final int[] terrain; 
	/** carreaux découpés dans l'image du Tileset */
	public final BufferedImage[] carreaux;
	
	/** Nom de l'image de Panorama */
	private final String nomImagePanorama;
	/** Image à afficher derrière le décor de la Map */
	public BufferedImage imagePanorama;
	/** Parallaxe (en pourcents) du Panorama par rapport au décor de la Map */
	public final int parallaxe;
	/** Image du Brouillard à afficher sur la Map */
	public Brouillard brouillard;
	/** Ton de l'écran */
	public int[] ton;
	/** Carreaux qui se lient entre eux automatiquement */
	public HashMap<Integer, Autotile> autotiles;
	
	/**
	 * Constructeur explicite
	 * @param nomTileset nom de l'image de décor
	 * @throws Exception erreur lors de l'ouverture du fichier JSON ou des images
	 */
	public Tileset(final String nomTileset) throws Exception {
		this.nom = nomTileset;

		final JSONObject jsonTileset = InterpreteurDeJson.ouvrirJsonTileset(nomTileset);
		
		//lecture des passabilités
		final JSONArray jsonPassabilite = jsonTileset.getJSONArray("passabilite");
		final int nombreDeLignesTileset = jsonPassabilite.length();
		final int nombreDeCarreauxTileset = nombreDeLignesTileset * LARGEUR_TILESET;
		this.passabilite = new boolean[nombreDeCarreauxTileset];
		try {
			for (int i = 0; i<nombreDeCarreauxTileset; i++ ) {
				this.passabilite[i] = ((Integer) jsonPassabilite.get(i)) == 0;
			}
		} catch (JSONException e) {
			LOG.error("Taille incorrecte pour le tableau des passabilités du Tileset JSON : "+this.nom, e);
		}
		
		//image du tileset
		this.nomImage = jsonTileset.getString("nomImage");
		try {
			this.image = Graphismes.ouvrirImage("Tilesets", this.nomImage);
		} catch (IIOException ioe) {
			// image manquante, on crée une fausse image
			LOG.warn("Impossible de charger l'image du tileset "+this.nomImage, ioe);
			final int largeurImageTileset = LARGEUR_TILESET * Fenetre.TAILLE_D_UN_CARREAU;
			final int hauteurImageTileset = nombreDeLignesTileset * Fenetre.TAILLE_D_UN_CARREAU;
			this.image = new BufferedImage(largeurImageTileset, hauteurImageTileset, Graphismes.TYPE_DES_IMAGES);
			Graphics2D g2d = (Graphics2D) this.image.getGraphics();
			g2d.setPaint(Color.LIGHT_GRAY); //passages
			g2d.fillRect(0, 0, largeurImageTileset, hauteurImageTileset);
			g2d.setPaint(Color.DARK_GRAY); //obstacles
			for (int i = 0; i<LARGEUR_TILESET; i++) {
				for (int j = 0; j<nombreDeLignesTileset; j++) {
					if (this.passabilite[i+j*8]) {
						g2d.fillRect(i*Fenetre.TAILLE_D_UN_CARREAU, j*Fenetre.TAILLE_D_UN_CARREAU, Fenetre.TAILLE_D_UN_CARREAU, Fenetre.TAILLE_D_UN_CARREAU);
					}
				}
			}
		}
		if (this.image.getHeight() != nombreDeLignesTileset*Fenetre.TAILLE_D_UN_CARREAU) {
			LOG.warn("Incompatibilité entre le tableau des passabilités du tileset JSON et l'image du tileset : "+this.nom);
		}
		
		//découpage des carreaux
		this.carreaux = new BufferedImage[nombreDeCarreauxTileset];
		for (int i = 0; i<LARGEUR_TILESET; i++) {
			for (int j = 0; j<nombreDeLignesTileset; j++) {
				carreaux[LARGEUR_TILESET*j + i] = this.image.getSubimage(i*Fenetre.TAILLE_D_UN_CARREAU, j*Fenetre.TAILLE_D_UN_CARREAU, Fenetre.TAILLE_D_UN_CARREAU, Fenetre.TAILLE_D_UN_CARREAU);
			}
		}
		
		
		
		//lecture des altitudes
		this.altitude = new int[nombreDeCarreauxTileset];
		final JSONArray jsonAltitude = jsonTileset.getJSONArray("altitude");
		try {
			for (int i = 0; i<nombreDeCarreauxTileset; i++ ) {
				this.altitude[i] = (Integer) jsonAltitude.get(i);
			}
		} catch (JSONException e) {
			LOG.error("Incompatibilité entre le tableau des altitudes du Tileset JSON et de l'image du Tileset : "+this.nom, e);
		}
		
		//lecture des terrains
		this.terrain = new int[nombreDeCarreauxTileset];
		final JSONArray jsonTerrain = jsonTileset.getJSONArray("terrain");
		try {
			for (int i = 0; i<nombreDeCarreauxTileset; i++ ) {
				this.terrain[i] = (Integer) jsonTerrain.get(i);
			}
		} catch (JSONException e) {
			LOG.error("Incompatibilité entre le tableau des terrains du Tileset JSON et de l'image du Tileset : "+this.nom, e);
		}
		
		//panorama
		this.nomImagePanorama = jsonTileset.getString("panorama");
		try {
			this.imagePanorama = Graphismes.ouvrirImage("Panorama", this.nomImagePanorama);
		} catch (IOException e) {
			LOG.warn("Pas d'image de panorama pour le Tileset : "+this.nom);
			this.imagePanorama = null;
		}
		this.parallaxe = jsonTileset.has("parallaxe") ? jsonTileset.getInt("parallaxe") : PARALLAXE_PAR_DEFAUT;
		
		//brouillard
		this.brouillard  = Brouillard.creerBrouillardAPartirDeJson(jsonTileset);
		
		//ton de l'écran
		try {
			final Iterator<Object> jsonTon = jsonTileset.getJSONArray("tonDeLEcran").iterator();
			final int rouge = (int) jsonTon.next();
			final int vert = (int) jsonTon.next();
			final int bleu = (int) jsonTon.next();
			final int gris = (int) jsonTon.next();
			this.ton = new int[] {gris, rouge, vert, bleu};
		} catch (JSONException e) {
			LOG.warn("Pas de ton d'écran pour le tileset : "+this.nom);
		}
			
		//autotiles
		this.autotiles = Autotile.chargerAutotiles(jsonTileset, this);
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

	/**
	 * Obtenir le terrain associé à cette case du Tileset.
	 * @param numeroCarreau numéro de la case du Tileset
	 * @return identitifaint de terrain
	 */
	public final int terrainDeLaCase(final int numeroCarreau) {
		if (numeroCarreau >= -1) { //case normale
			return this.terrain[numeroCarreau];
		} else { //autotile
			return this.autotiles.get(numeroCarreau).terrain;
		}
	}

}
