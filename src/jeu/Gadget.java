package jeu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import utilitaire.InterpreteurDeJson;

/**
 * Le H�ros peut utiliser un certain nombre de Gadgets sur la Map. 
 */
public class Gadget {
	//constantes
	private static Gadget[] gadgetsDuJeu;
	public static HashMap<String, Gadget> gadgetsDuJeuHash = new HashMap<String, Gadget>();
	
	/**
	 * Chaque Gadget poss�de un id propre. 
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
			this.icone = ImageIO.read(new File(".\\ressources\\Graphics\\Icons\\"+nomIcone));
		} catch (IOException e) {
			//erreur lors du chargement de l'icone
			e.printStackTrace();
		}
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public Gadget(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero"), 
			(String) parametres.get("nom"),
			(String) parametres.get("nomIcone")
		);
	}
	
	/**
	 * @param idGadget identifiant du Gadget souhait�
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
			//probl�me lors de l'ouverture du fichier JSON
			System.err.println("Impossible de charger les gadgets du jeu.");
			e.printStackTrace();
			gadgetsDuJeu = null;
			return 0;
		}
	}
}