package main;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import map.Tileset;
import utilitaire.InterpreteurDeJson;

/**
 * Adapter les fichiers JSON du jeu au moteur Java sur des spécificités.
 */
public abstract class Nettoyeur {
	private static final Logger LOG = LogManager.getLogger(Nettoyeur.class);
	
	/**
	 * Lancer le nettoyeur.
	 * @param args rien du tout
	 */
	public static void main(final String[] args) {
		// Vérifier si le nettoyage a déjà été fait
		if (leNettoyageADejaEteFait()) {
			return;
		}
		
		// Autotiles cousins
		calculerLesAutotilesCousins();
		
		// Noms des touches du clavier dans les messages
		reecrireLesTouchesDuClavier();
		
		// Egaliser les musiques
		egaliserLesMusiques();
		
	}

	private static boolean leNettoyageADejaEteFait() {
		// TODO Auto-generated method stub
		return false;
	}

	private static void calculerLesAutotilesCousins() {
		final File dossierTileset = new File(".\\ressources\\Data\\Tilesets\\");
		final File[] fichiersTileset = dossierTileset.listFiles();
		for (File fichierTileset : fichiersTileset) {
			try {
				final JSONObject jsonTileset = InterpreteurDeJson.ouvrirJsonTileset(fichierTileset.getName());
				//TODO
			} catch (Exception e) {
				LOG.error("Impossible d'ouvrir le tilset "+fichierTileset.getName(), e);
			}
		}
	}

	private static void reecrireLesTouchesDuClavier() {
		// TODO Auto-generated method stub
	}

	private static void egaliserLesMusiques() {
		//TODO calculer le volume moyen de chaque musique
		//TODO définir 1.0 comme volume par défaut pour la plus basse
		//TODO déduire les autres volumes par défaut par produit en croix
		//TODO recenser les occurences de chaque musique dans le jeu avec leur volume assigné
		//TODO pour le plus grand volumed'usage, utiliser le volume par défaut
		//TODO déduire les remplacements des autres volumes d'usage par produit en croix 
	}
}
