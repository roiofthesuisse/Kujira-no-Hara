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

import commandes.ModifierTexte;
import conditions.Condition;
import conditions.ConditionEtatQuete;
import main.Commande;
import main.Fenetre;
import menu.Listable;
import menu.Texte;
import utilitaire.InterpreteurDeJson;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Le joueur doit réussir des Quêtes durant le jeu.
 * Cette classe est une description inerte de la Quête, indépendante de l'action du joueur.
 */
public class Quete implements Listable {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Quete.class);
	public static final String NOM_ICONE_QUETE_PAS_FAITE_PAR_DEFAUT = "quete a faire icon.png";
	public static final String NOM_ICONE_QUETE_FAITE_PAR_DEFAUT = "quete faite icon.png";
	public static final HashMap<String, BufferedImage> ICONES_MEMORISEES = new HashMap<String, BufferedImage>();
	public static Quete[] quetesDuJeu;
	
	public Integer id; //Integer car clé d'une HashMap
	public ArrayList<String> nom;
	public ArrayList<String> description;
	private final String nomIconeQuetePasFaite;
	private BufferedImage iconeQuetePasFaite;
	private final String nomIconeQueteFaite;
	private BufferedImage iconeQueteFaite;
	public int xCarte;
	public int yCarte;
	
	/**
	 * Une Quête peut se présenter sous différents niveaux d'Avancement au fil du jeu.
	 */
	public enum AvancementQuete {
		INCONNUE("INCONNUE"), CONNUE("CONNUE"), FAITE("FAITE");
		
		public String nom;
		
		/**
		 * Constructeur explicite
		 * @param nom de l'Etat de Quête
		 */
		AvancementQuete(final String nom) {
			this.nom = nom;
		}
		
		/**
		 * Obtenir un Avancement de Quête à partir de son nom.
		 * @param nom de l'Etat de Quête
		 * @return Etat de Quête
		 */
		public static AvancementQuete getEtat(final String nom) {
			for (AvancementQuete etat : values()) {
				if (etat.nom.equals(nom)) {
					return etat;
				}
			}
			return INCONNUE;
		}
	}
	
	/**
	 * Constructeur explicite
	 * @param id de la Quête
	 * @param nom de la Quête
	 * @param description de la Quête
	 * @param nomIconeQuetePasFaite nom de l'icône affichée lorsque la Quête n'est pas encore faite
	 * @param nomIconeQueteFaite nom de l'icône affichée lorsque la Quête a été  faite
	 * @param xCarte position x sur la carte des Quêtes
	 * @param yCarte position y sur la carte des Quêtes
	 */
	private Quete(final int id, final ArrayList<String> nom, final ArrayList<String> description, final String nomIconeQuetePasFaite, 
			final String nomIconeQueteFaite, final int xCarte, final int yCarte) {
		this.id = id;
		this.nom = nom;
		this.description = description;
		this.nomIconeQuetePasFaite = nomIconeQuetePasFaite;
		this.nomIconeQueteFaite = nomIconeQueteFaite;
		this.xCarte = xCarte;
		this.yCarte = yCarte;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Quete(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("id"), 
			Texte.construireTexteMultilingue(parametres.get("nom")),
			Texte.construireTexteMultilingue(parametres.get("description")),
			(String) (parametres.containsKey("nomIconeQuetePasFaite") ? parametres.get("nomIconeQuetePasFaite") : NOM_ICONE_QUETE_PAS_FAITE_PAR_DEFAUT),
			(String) (parametres.containsKey("nomIconeQueteFaite") ? parametres.get("nomIconeQueteFaite") : NOM_ICONE_QUETE_FAITE_PAR_DEFAUT),
			(int) parametres.get("xCarte"),
			(int) parametres.get("yCarte")
		);
	}

	/**
	 * Charger les Quêtes du jeu via JSON.
	 * @return nombre de Quêtes dans le jeu
	 */
	public static int chargerLesQuetesDuJeu() {
		try {
			final JSONArray jsonQuetes = InterpreteurDeJson.ouvrirJsonQuetes();
			final ArrayList<Quete> quetes = new ArrayList<Quete>();
			int i = 0;
			for (Object objectQuete : jsonQuetes) {
				final JSONObject jsonQuete = (JSONObject) objectQuete;
				
				final HashMap<String, Object> parametres = new HashMap<String, Object>();
				parametres.put("numero", i);
				
				final Iterator<String> jsonParametres = jsonQuete.keys();
				while (jsonParametres.hasNext()) {
					final String parametre = jsonParametres.next();
					parametres.put(parametre, jsonQuete.get(parametre));
				}
				
				final Quete quete = new Quete(parametres);
				quetes.add(quete);
				i++;
			}
			
			quetesDuJeu = new Quete[quetes.size()];
			quetes.toArray(quetesDuJeu);
			return quetesDuJeu.length;
			
		} catch (FileNotFoundException e) {
			//problème lors de l'ouverture du fichier JSON
			LOG.error("Impossible de charger les quêtes du jeu.", e);
			quetesDuJeu = null;
			return 0;
		}
	}
	
	/**
	 * Obtenir l'icône de cette Quête lorsqu'elle n'a pas encore été faite par le joueur.
	 * @return icône de la Quête non faite
	 */
	private BufferedImage getIconeQuetePasFaite() {
		if (this.iconeQuetePasFaite == null) {
			if (ICONES_MEMORISEES.containsKey(this.nomIconeQuetePasFaite)) {
				this.iconeQuetePasFaite = ICONES_MEMORISEES.get(this.nomIconeQuetePasFaite);
			} else {
				try {
					this.iconeQuetePasFaite = Graphismes.ouvrirImage("Icons", this.nomIconeQuetePasFaite);
					ICONES_MEMORISEES.put(this.nomIconeQuetePasFaite, this.iconeQuetePasFaite);
				} catch (IOException e) {
					//l'image d'apparence n'existe pas
					this.iconeQuetePasFaite = null;
					ICONES_MEMORISEES.put(this.nomIconeQuetePasFaite, null);
					LOG.error("Impossible de trouver l'icône de Quete : " + this.nomIconeQuetePasFaite);
				}
			}
		}
		return this.iconeQuetePasFaite;
	}
	
	/**
	 * Obtenir l'icône de cette Quête lorsqu'elle a été faite par le joueur.
	 * @return icône de la Quête faite
	 */
	private BufferedImage getIconeQueteFaite() {
		if (this.iconeQueteFaite == null) {
			if (ICONES_MEMORISEES.containsKey(this.nomIconeQueteFaite)) {
				this.iconeQueteFaite = ICONES_MEMORISEES.get(this.nomIconeQueteFaite);
			} else {
				try {
					this.iconeQueteFaite = Graphismes.ouvrirImage("Icons", this.nomIconeQueteFaite);
					ICONES_MEMORISEES.put(nomIconeQueteFaite, this.iconeQueteFaite);
				} catch (IOException e) {
					//l'image d'apparence n'existe pas
					this.iconeQueteFaite = null;
					ICONES_MEMORISEES.put(this.nomIconeQueteFaite, null);
					LOG.error("Impossible de trouver l'icône de Quete : " + this.nomIconeQueteFaite);
				}
			}
		}
		return this.iconeQueteFaite;
	}
	
	/**
	 * Obtenir l'icône de la Quête.
	 * @return icône de la Quête faite ou non faite, selon si la Quête est fait ou non.
	 */
	public final BufferedImage getIcone() {
		if (Quete.AvancementQuete.FAITE.equals( Fenetre.getPartieActuelle().avancementDesQuetes[this.id]) ) {
			return this.getIconeQueteFaite();
		} else {
			return this.getIconeQuetePasFaite();
		}
	}

	/**
	 * Enumerer les Quêtes du jeu.
	 * @param possedes filtrer ou non sur les Quêtes connues
	 * @return association entre numero et Quête
	 */
	public static final Map<Integer, Listable> obtenirTousLesListables(final Boolean possedes) {
		final Map<Integer, Listable> listablesPossedes = new HashMap<Integer, Listable>();
		if (possedes) {
			// seulement les Quetes connues
			final AvancementQuete[] avancements = Fenetre.getPartieActuelle().avancementDesQuetes;
			for (int i = 0; i < avancements.length; i++) {
				if (avancements[i].equals(AvancementQuete.CONNUE) || avancements[i].equals(AvancementQuete.FAITE)) {
					listablesPossedes.put((Integer) i, quetesDuJeu[i]);
				}
			}
		} else {
			// toutes les Armes
			for (Quete quete : quetesDuJeu) {
				listablesPossedes.put((Integer) quete.id, quete);
			}
		}
		return listablesPossedes;
	}

	@Override
	public final BufferedImage construireImagePourListe() {
		final Texte texte = new Texte(this.nom);
		final BufferedImage imageTexte = texte.texteToImage();
		final int largeur = imageTexte.getWidth() + Texte.MARGE_A_DROITE + this.getIcone().getWidth();
		final int hauteur = Math.max(imageTexte.getHeight(), this.getIcone().getHeight());
		BufferedImage image = new BufferedImage(largeur, hauteur, Graphismes.TYPE_DES_IMAGES);
		image = Graphismes.superposerImages(image, this.getIcone(), 0, 0, false, 
				Graphismes.PAS_D_HOMOTHETIE, Graphismes.PAS_D_HOMOTHETIE, Graphismes.OPACITE_MAXIMALE, 
				ModeDeFusion.NORMAL, Graphismes.PAS_DE_ROTATION);
		image = Graphismes.superposerImages(image, imageTexte, this.getIcone().getWidth()+Texte.MARGE_A_DROITE, 0, 
				false, Graphismes.PAS_D_HOMOTHETIE, Graphismes.PAS_D_HOMOTHETIE, Graphismes.OPACITE_MAXIMALE, 
				ModeDeFusion.NORMAL, Graphismes.PAS_DE_ROTATION);
		return image;
	}
	
	@Override
	public final ArrayList<Condition> getConditions() {
		final ArrayList<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new ConditionEtatQuete(1, this.id, AvancementQuete.CONNUE));
		return conditions;
	}

	@Override
	public final ArrayList<Commande> getComportementConfirmation() {
		return null;
	}

	@Override
	public final ArrayList<Commande> getComportementSelection() {
		final ArrayList<Commande> comportementSelection = new ArrayList<Commande>();
		comportementSelection.add(new ModifierTexte(this.description));
		return comportementSelection;
	}

}
