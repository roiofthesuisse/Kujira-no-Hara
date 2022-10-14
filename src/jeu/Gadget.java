package jeu;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import commandes.EquiperGadget;
import commandes.ModifierTexte;
import conditions.Condition;
import conditions.ConditionGadgetPossede;
import main.Commande;
import main.Main;
import menu.Listable;
import menu.Texte;
import utilitaire.InterpreteurDeJson;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Le H�ros peut utiliser un certain nombre de Gadgets sur la Map. 
 */
public class Gadget implements Listable {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Gadget.class);
	public static final Gadget[] GADGETS_DU_JEU = chargerLesGadgetsDuJeu();
	
	/**
	 * Chaque Gadget poss�de un id propre. 
	 * 0 pour les bottes, 1 pour le panier, etc.
	 */
	public final int id;
	public final ArrayList<String> nom;
	private final ArrayList<String> description;
	public final boolean equipable;
	public BufferedImage icone;
	
	/**
	 * Constructeur explicite
	 * @param id chaque Gadget a un identifiant
	 * @param nom du Gadget (dans plusieurs langues)
	 * @param description a afficher dans les Menus (dans plusieurs langues)
	 * @param equipable peut-on �quiper le Gadget depuis le Menu ?
	 * @param nomIcone nom de l'image d'icone
	 */
	private Gadget(final int id, final ArrayList<String> nom, final ArrayList<String> description, 
			final boolean equipable, final String nomIcone) {
		this.id = id;
		this.nom = nom;
		this.description = description;
		this.equipable = equipable;
		try {
			this.icone = Graphismes.ouvrirImage("Icons", nomIcone);
		} catch (IOException e) {
			//erreur lors du chargement de l'icone
			e.printStackTrace();
		}
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public Gadget(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero"), 
			Texte.construireTexteMultilingue(parametres.get("nom")),
			Texte.construireTexteMultilingue(parametres.get("description")),
			parametres.containsKey("equipable") ? (boolean) parametres.get("equipable") : true,
			(String) parametres.get("nomIcone")
		);
	}
	
	/**
	 * @param idGadget identifiant du Gadget souhait�
	 * @return Gadget dont l'identifiant est idGadget
	 */
	public static Gadget getGadget(final int idGadget) {
		try {
			return GADGETS_DU_JEU[idGadget];
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Charger les Gadgets du jeu via JSON.
	 * @return nombre de Gadgets dans le jeu
	 */
	public static Gadget[] chargerLesGadgetsDuJeu() {
		final JSONArray jsonGadgets;
		try {
			jsonGadgets = InterpreteurDeJson.ouvrirJsonGadgets();
		} catch (Exception e) {
			//probleme lors de l'ouverture du fichier JSON
			LOG.error("Impossible de charger les gadgets du jeu.", e);
			return null;
		}
			
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
			i++;
		}
		
		final Gadget[] gadgetsDuJeu = new Gadget[gadgets.size()];
		gadgets.toArray(gadgetsDuJeu);
		return gadgetsDuJeu;
	}

	/**
	 * Enumerer les Gadgets du jeu.
	 * @param possedes filtrer ou non sur les Gadgets poss�d�s
	 * @return association entre numero et Gadget
	 */
	public static final Map<Integer, Listable> obtenirTousLesListables(final Boolean possedes) {
		final Map<Integer, Listable> listablesPossedes = new HashMap<Integer, Listable>();
		if (possedes) {
			// seulement les Gadgets poss�d�es
			final boolean[] gadgetsPossedes = Main.getPartieActuelle().gadgetsPossedes;
			for (int i = 0; i < gadgetsPossedes.length; i++) {
				if (gadgetsPossedes[i]) {
					listablesPossedes.put((Integer) i, GADGETS_DU_JEU[i]);
				}
			}
		} else {
			// toutes les Armes
			for (Gadget gadget : GADGETS_DU_JEU) {
				listablesPossedes.put((Integer) gadget.id, gadget);
			}
		}
		return listablesPossedes;
	}

	@Override
	public final BufferedImage construireImagePourListe(final int largeurMinimale, final int hauteurMinimale) {
		final Texte texte = new Texte(this.nom);
		final BufferedImage imageTexte = texte.texteToImage();
		
		int largeur = imageTexte.getWidth() + Texte.MARGE_A_DROITE + this.icone.getWidth();
		if (largeur < largeurMinimale) {
			largeur = largeurMinimale;
		}
		int hauteur = Math.max(imageTexte.getHeight(), this.icone.getHeight());
		if (hauteur < hauteurMinimale) {
			hauteur = hauteurMinimale;
		}
		
		BufferedImage image = new BufferedImage(largeur, hauteur, Graphismes.TYPE_DES_IMAGES);
		image = Graphismes.superposerImages(image, this.icone, 0, 0, false, 
				Graphismes.PAS_D_HOMOTHETIE, Graphismes.PAS_D_HOMOTHETIE, Graphismes.OPACITE_MAXIMALE, 
				ModeDeFusion.NORMAL, Graphismes.PAS_DE_ROTATION);
		image = Graphismes.superposerImages(image, imageTexte, this.icone.getWidth()+Texte.MARGE_A_DROITE, 0, 
				false, Graphismes.PAS_D_HOMOTHETIE, Graphismes.PAS_D_HOMOTHETIE, Graphismes.OPACITE_MAXIMALE, 
				ModeDeFusion.NORMAL, Graphismes.PAS_DE_ROTATION);
		return image;
	}
	
	@Override
	public final ArrayList<Condition> getConditions() {
		final ArrayList<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new ConditionGadgetPossede(1, this.id));
		return conditions;
	}

	@Override
	public final ArrayList<Commande> getComportementConfirmation() {
		final ArrayList<Commande> comportementConfirmation = new ArrayList<Commande>();
		comportementConfirmation.add(new ModifierTexte(this.description));
		return comportementConfirmation;
	}
	
	@Override
	public final ArrayList<Commande> getComportementSelection() {
		final ArrayList<Commande> comportementSelection = new ArrayList<Commande>();
		comportementSelection.add(new EquiperGadget(this.id));
		return comportementSelection;
	}

}
