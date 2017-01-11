package jeu;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import main.Commande;
import main.Fenetre;
import menu.Listable;
import utilitaire.InterpreteurDeJson;
import utilitaire.graphismes.Graphismes;

/**
 * Le Héros peut utiliser un certain nombre de Gadgets sur la Map. 
 */
public class Gadget implements Listable {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Gadget.class);
	private static Gadget[] gadgetsDuJeu;
	public static HashMap<String, Gadget> gadgetsDuJeuHash = new HashMap<String, Gadget>();
	
	/**
	 * Chaque Gadget possède un id propre. 
	 * 0 pour les bottes, 1 pour le panier, etc.
	 */
	public final int id;
	public String nom;
	public BufferedImage icone;
	
	/**
	 * Constructeur explicite
	 * @param id chaque Gadget a un identifiant
	 * @param nom chaque Gadget a un nom
	 * @param nomIcone nom de l'image d'icone
	 */
	private Gadget(final int id, final String nom, final String nomIcone) {
		this.id = id;
		this.nom = nom;
		try {
			this.icone = Graphismes.ouvrirImage("Icons", nomIcone);
		} catch (IOException e) {
			//erreur lors du chargement de l'icone
			e.printStackTrace();
		}
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Gadget(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero"), 
			(String) parametres.get("nom"),
			(String) parametres.get("nomIcone")
		);
	}
	
	/**
	 * @param idGadget identifiant du Gadget souhaité
	 * @return Gadget dont l'identifiant est idGadget
	 */
	public static Gadget getGadget(final int idGadget) {
		try {
			return gadgetsDuJeu[idGadget];
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Charger les Gadgets du jeu via JSON.
	 * @return nombre de Gadgets dans le jeu
	 */
	public static int chargerLesGadgetsDuJeu() {
		try {
			final JSONArray jsonGadgets = InterpreteurDeJson.ouvrirJsonGadgets();
			final ArrayList<Gadget> gadgets = new ArrayList<Gadget>();
			int i = 0;
			for (Object objectGadget : jsonGadgets) {
				final JSONObject jsonGadget = (JSONObject) objectGadget;
				
				final HashMap<String, Object> parametres = new HashMap<String, Object>();
				parametres.put("numero", i);
				
				final Iterator<String> jsonParametres = jsonGadget.keys();
				while (jsonParametres.hasNext()) {
					final String parametre = jsonParametres.next();
					parametres.put(parametre, jsonGadget.get(parametre));
				}
				
				final Gadget gadget = new Gadget(parametres);
				gadgets.add(gadget);
				gadgetsDuJeuHash.put(gadget.nom, gadget);
				i++;
			}
			
			gadgetsDuJeu = new Gadget[gadgets.size()];
			gadgets.toArray(gadgetsDuJeu);
			return gadgetsDuJeu.length;
			
		} catch (FileNotFoundException e) {
			//problème lors de l'ouverture du fichier JSON
			LOG.error("Impossible de charger les gadgets du jeu.", e);
			gadgetsDuJeu = null;
			return 0;
		}
	}

	@Override
	public final Map<Integer, Listable> obtenirTousLesListables(final Boolean possedes) {
		final Map<Integer, Listable> listablesPossedes = new HashMap<Integer, Listable>();
		if (possedes) {
			// seulement les Gadgets possédées
			final boolean[] gadgetsPossedes = Fenetre.getPartieActuelle().gadgetsPossedes;
			for (int i = 0; i < gadgetsPossedes.length; i++) {
				if (gadgetsPossedes[i]) {
					listablesPossedes.put((Integer) i, gadgetsDuJeu[i]);
				}
			}
		} else {
			// toutes les Armes
			for (Gadget gadget : gadgetsDuJeu) {
				listablesPossedes.put((Integer) gadget.id, gadget);
			}
		}
		return listablesPossedes;
	}

	@Override
	public final BufferedImage construireImagePourListe(final ArrayList<String> information) {
		// TODO Auto-generated method stub
		return this.icone;
	}

	@Override
	public final ArrayList<Commande> getComportementConfirmation() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public final ArrayList<Commande> getComportementSelection() {
		// TODO Auto-generated method stub
		return null;
	}

}
