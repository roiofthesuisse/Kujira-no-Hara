package map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.IIOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.Color;
import main.Main;
import utilitaire.InterpreteurDeJson;
import utilitaire.graphismes.Graphismes;

/**
 * Le Tileset associe a chaque brique de decor une passabilite et une altitude.
 */
public class Tileset {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Tileset.class);
	public static final int LARGEUR_TILESET = 8; //chaque ligne de Tileset contient 8 carreaux
	public static final int PARALLAXE_PAR_DEFAUT = 0; //le Panorama est fixe
	private static final int ALTITUDE_DE_LA_CASE_VIDE = 5;
	
	/** nom du fichier JSON du Tileset */
	public final String nom;
	/** nom de l'image du Tileset */
	private final String nomImage;
	/** image complete du Tileset */
	private BufferedImage image;
	/** Peut-on marcher sur cette case ? Ou bien est-ce un obstacle ? */
	private final Passabilite[] passabilite;
	/** Altitude d'affichage du carreau (0:sol, 2:heros) */
	private final int[] altitude; 
	/** Terrain du carreau */
	private final int[] terrain;
	/** Carreaux du Tileset consideres comme des portes */
	public final ArrayList<Integer> portes;
	/** carreaux decoupes dans l'image du Tileset */
	public final BufferedImage[] carreaux;
	
	/** Nom de l'image de Panorama */
	private final String nomImagePanorama;
	/** Image a afficher derriere le decor de la Map */
	public BufferedImage imagePanorama;
	/** Parallaxe (en pourcents) du Panorama par rapport au decor de la Map */
	public final int parallaxe;
	/** Image du Brouillard a afficher sur la Map */
	public Brouillard brouillard;
	/** Ton de l'ecran */
	public int[] ton;
	/** Carreaux qui se lient entre eux automatiquement */
	public HashMap<Integer, Autotile> autotiles;
	
	/**
	 * Constructeur explicite
	 * @param nomTileset nom de l'image de decor
	 * @throws Exception erreur lors de l'ouverture du fichier JSON ou des images
	 */
	public Tileset(final String nomTileset) throws Exception {
		this.nom = nomTileset;

		final JSONObject jsonTileset = InterpreteurDeJson.ouvrirJsonTileset(nomTileset);
		
		//lecture des passabilites
		final JSONArray jsonPassabilite = jsonTileset.getJSONArray("passabilite");
		final int nombreDeLignesTileset = jsonPassabilite.length();
		final int nombreDeCarreauxTileset = nombreDeLignesTileset * LARGEUR_TILESET;
		this.passabilite = new Passabilite[nombreDeCarreauxTileset];
		for (int l = 0; l<nombreDeLignesTileset; l++) {
			for (int c = 0; c<LARGEUR_TILESET; c++) {
				this.passabilite[l*LARGEUR_TILESET+c] = Passabilite.parCode(jsonPassabilite.getJSONArray(l).getInt(c));
			}
		}
		
		//image du tileset
		this.nomImage = jsonTileset.getString("nomImage");
		try {
			this.image = Graphismes.ouvrirImage("Tilesets", this.nomImage);
		} catch (IIOException ioe) {
			// image manquante, on cree une fausse image
			LOG.warn("Impossible de charger l'image du tileset "+this.nomImage, ioe);
			final int largeurImageTileset = LARGEUR_TILESET * Main.TAILLE_D_UN_CARREAU;
			final int hauteurImageTileset = nombreDeLignesTileset * Main.TAILLE_D_UN_CARREAU;
			this.image = new BufferedImage(largeurImageTileset, hauteurImageTileset, Graphismes.TYPE_DES_IMAGES);
			final Graphics2D g2d = (Graphics2D) this.image.getGraphics();
			g2d.setPaint(Color.LIGHT_GRAY); //passages
			g2d.fillRect(0, 0, largeurImageTileset, hauteurImageTileset);
			g2d.setPaint(Color.DARK_GRAY); //obstacles
			for (int i = 0; i<LARGEUR_TILESET; i++) {
				for (int j = 0; j<nombreDeLignesTileset; j++) {
					if (this.passabilite[i+j*LARGEUR_TILESET] != Passabilite.OBSTACLE) {
						g2d.fillRect(i*Main.TAILLE_D_UN_CARREAU, j*Main.TAILLE_D_UN_CARREAU, Main.TAILLE_D_UN_CARREAU, Main.TAILLE_D_UN_CARREAU);
					}
				}
			}
		}
		
		//decoupage des carreaux
		this.carreaux = new BufferedImage[nombreDeCarreauxTileset];
		for (int i = 0; i<LARGEUR_TILESET; i++) {
			for (int j = 0; j<nombreDeLignesTileset; j++) {
				try {
					carreaux[LARGEUR_TILESET*j + i] = this.image.getSubimage(i*Main.TAILLE_D_UN_CARREAU, 
							j*Main.TAILLE_D_UN_CARREAU, 
							Main.TAILLE_D_UN_CARREAU, 
							Main.TAILLE_D_UN_CARREAU);
				} catch (RasterFormatException rfe) {
					LOG.error("Impossible de découper en carreaux de Tileset ! Vérifier que l'image et le JSON coïncident.", rfe);
				}
			}
		}
		
		//lecture des altitudes
		this.altitude = new int[nombreDeCarreauxTileset];
		final JSONArray jsonAltitude = jsonTileset.getJSONArray("altitude");
		try {
			for (int l = 0; l<nombreDeLignesTileset; l++ ) {
				for (int c = 0; c<LARGEUR_TILESET; c++) {
					this.altitude[l*LARGEUR_TILESET+c] = (Integer) jsonAltitude.getJSONArray(l).getInt(c);
				}
			}
		} catch (JSONException e) {
			LOG.error("Incompatibilite entre le tableau des altitudes du Tileset JSON et de l'image du Tileset : "+this.nom, e);
		}
		
		//lecture des terrains
		this.terrain = new int[nombreDeCarreauxTileset];
		final JSONArray jsonTerrain = jsonTileset.getJSONArray("terrain");
		try {
			for (int l = 0; l<nombreDeLignesTileset; l++ ) {
				for (int c = 0; c<LARGEUR_TILESET; c++) {
					this.terrain[l*LARGEUR_TILESET+c] = (Integer) jsonTerrain.getJSONArray(l).getInt(c);
				}
			}
		} catch (JSONException e) {
			LOG.error("Incompatibilite entre le tableau des terrains du Tileset JSON et de l'image du Tileset : "+this.nom, e);
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
		
		//ton de l'ecran
		try {
			final Iterator<Object> jsonTon = jsonTileset.getJSONArray("tonDeLEcran").iterator();
			final int rouge = (int) jsonTon.next();
			final int vert = (int) jsonTon.next();
			final int bleu = (int) jsonTon.next();
			final int gris = (int) jsonTon.next();
			this.ton = new int[] {gris, rouge, vert, bleu};
		} catch (JSONException e) {
			LOG.warn("Pas de ton d'ecran pour le tileset : "+this.nom);
		}
		
		//autotiles
		final JSONObject jsonTilesetCousins = InterpreteurDeJson.ouvrirJson(this.nom, "./ressources/Data/Tilesets/Cousins/");
		this.autotiles = Autotile.chargerAutotiles(jsonTileset, jsonTilesetCousins, this);
		
		//portes
		this.portes = new ArrayList<Integer>();
		try {
			final JSONObject jsonTilesetPortes = InterpreteurDeJson.ouvrirJson(this.nom, "./ressources/Data/Tilesets/Portes/");
			if (jsonTilesetPortes.has("portes")) {
				final JSONArray jsonPortes = jsonTilesetPortes.getJSONArray("portes");
				for (int i = 0; i<jsonPortes.length(); i++) {
					final int carreauPorte = jsonPortes.getInt(i);
					this.portes.add(carreauPorte);
				}
			} else {
				LOG.warn("Pas de portes declarees dans le tileset : "+this.nom);
			}
		} catch (NoSuchFileException e) {
			LOG.error("Pas de fichier listant les portes pour le tileset : "+this.nom, e);
		}
	}
	
	/**
	 * La case de decor est-elle traversable ?
	 * @param numeroDeLaCaseDansLeTileset numerotation du Tileset
	 * @return true si obstacle, false si passable
	 */
	public final Passabilite passabiliteDeLaCase(final int numeroDeLaCaseDansLeTileset) {
		if (numeroDeLaCaseDansLeTileset == -1) { //case vide
			return Passabilite.PASSABLE;
		} else if (numeroDeLaCaseDansLeTileset >= 0) { //case normale
			return this.passabilite[numeroDeLaCaseDansLeTileset]; 
		} else { //autotile
			return this.autotiles.get((Integer) numeroDeLaCaseDansLeTileset).passabilite ? Passabilite.PASSABLE : Passabilite.OBSTACLE;
		}
	}
	
	/**
	 * Recupere l'altitude associee a ce carreau de Tileset.
	 * @param numeroCarreau dans le Tileset
	 * @return true si obstacle, false si passable
	 */
	public final int altitudeDeLaCase(final int numeroCarreau) {
		if (numeroCarreau == -1) { //case vide
			return ALTITUDE_DE_LA_CASE_VIDE;
		} else if (numeroCarreau > -1) { //case normale
			return this.altitude[numeroCarreau];
		} else { //autotile
			return this.autotiles.get(numeroCarreau).altitude;
		}
	}

	/**
	 * Obtenir le terrain associe a cette case du Tileset.
	 * @param numeroCarreau numero de la case du Tileset
	 * @return identitifaint de terrain
	 */
	public final int terrainDeLaCase(final int numeroCarreau) {
		if (numeroCarreau == -1) { //case vide
			return 0;
		} else if (numeroCarreau > -1) { //case normale
			return this.terrain[numeroCarreau];
		} else { //autotile
			return this.autotiles.get(numeroCarreau).terrain;
		}
	}

}
