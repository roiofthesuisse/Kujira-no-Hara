package menu;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import commandes.FermerMenu;
import main.Commande;
import main.Main;
import map.Event;
import utilitaire.InterpreteurDeJson;
import utilitaire.graphismes.Graphismes;
import utilitaire.son.LecteurAudio;

/**
 * Un Menu est constitue d'images et de Textes, eventuellement Selectionnables.
 * Le Menu preexiste a son Lecteur. Le Menu ne connaitra son Lecteur que lorsque le Lecteur sera instancie.
 */
public class Menu {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Menu.class);
	protected static final int LARGEUR_ELEMENT_PAR_DEFAUT = 48;
	protected static final int HAUTEUR_ELEMENT_PAR_DEFAUT = 32;
	public static final String BRUIT_DEPLACEMENT_CURSEUR = "DeplacementCurseur.wav";
	public static final String BRUIT_CONFIRMER_SELECTION = "Confirmer.wav";
	
	public LecteurMenu lecteur;
	public BufferedImage fond;
	public ArrayList<Texte> textes;
	public Texte texteDescriptif;
	public ArrayList<ImageMenu> images;
	@SuppressWarnings("rawtypes")
	public ArrayList<Liste> listes;
	public Carte carte;
	public HashMap<Integer, ElementDeMenu> elements;
	private ArrayList<ElementDeMenu> selectionnables;
	public ElementDeMenu elementSelectionne;
	public ArrayList<Commande> comportementAnnulation;
	public String nomBGM;
	public Float volumeBGM;
	public Menu menuSuivant;
	public Menu menuPrecedent;
	public Menu menuParent;

	/**
	 * Constructeur explicite
	 * @param fond image de fond du Menu
	 * @param textes du Menu
	 * @param images du Menu
	 * @param listes tableaux bidimensionnels d'ElementsDeMenu
	 * @param cartes du Menu
	 * @param selectionInitiale ElementDeMenu selectionne au debut
	 * @param idTexteDescriptif identifiant de l'ElementDeMenu affichant les descriptions
	 * @param menuParent Menu qui a appele ce Menu
	 * @param comportementAnnulation comportement du Menu a l'annulation
	 */
	@SuppressWarnings("unchecked")
	public Menu(final BufferedImage fond, final ArrayList<Texte> textes, final ArrayList<ImageMenu> images, 
			@SuppressWarnings("rawtypes") final ArrayList<Liste> listes, final ArrayList<Carte> cartes,
			final ElementDeMenu selectionInitiale, final int idTexteDescriptif, final Menu menuParent,
			final ArrayList<Commande> comportementAnnulation) 
	{
		// Image de fond du menu
		this.fond = fond;
		
		this.elements = new HashMap<Integer, ElementDeMenu>();
		// Ajout des ElementsDeMenu de type Texte
		this.textes = textes;
		for (Texte texte : textes) {
			texte.menu = this;
			this.elements.put((Integer) texte.id, texte);
		}
		
		// Ajout des ElementsDeMenu de type Image
		this.images = images;
		for (ImageMenu image : images) {
			image.menu = this;
			this.elements.put((Integer) image.id, image);
		}
		
		// Ajout des ElementsDeMenu contenus dans une Liste
		// chercher des identifiants libres pour les ElementsDeMenu de la Liste
		int maxId = -1;
		for (ElementDeMenu element : this.elements.values()) {
			if (maxId < element.id) {
				maxId = element.id;
			}
		}
		this.listes = listes;
		for (@SuppressWarnings("rawtypes") Liste liste : listes) {
			for (ImageMenu image : (ArrayList<ImageMenu>) liste.elements) {
				image.menu = this;
				maxId++;
				image.id = maxId;
				this.elements.put(Integer.valueOf(maxId), image);
			}
		}
		
		// Ajout de l'eventuel ElementDeMenu de type Carte
		if (cartes.size() >= 1) {
			this.carte = cartes.get(0);
			carte.menu = this;
			LOG.info("Ce menu contient une carte.");
		}
		
		// Texte descriptif de l'Element selectionne par le curseur
		this.texteDescriptif = (Texte) this.elements.get((Integer) idTexteDescriptif);
		
		// Selection du curseur au depart
		this.elementSelectionne = selectionInitiale;
		if (selectionInitiale != null) {
			selectionInitiale.selectionne = true;
			selectionInitiale.executionDesCommandesDeSurvol = true;
		}
		
		// Menu parent (si on quitte ce Menu, on y retourne)
		this.menuParent = menuParent;
		
		// Comportement en cas d'annulation du Menu
		this.comportementAnnulation = comportementAnnulation;
	}
	
	/**
	 * Confirmer l'Element de Menu selectionne.
	 */
	public final void confirmer() {
		if (this.elementSelectionne != null) {
			LecteurAudio.playSe(BRUIT_CONFIRMER_SELECTION);
			this.elementSelectionne.executionDesCommandesDeConfirmation = true;
			this.elementSelectionne.curseurComportementConfirmation = 0;
		} else {
			LOG.error("L'element selectionne de ce menu est null.");
		}
	}
	
	/**
	 * Selectionner l'Element Selectionnable situe dans cette direction
	 * @param direction dans laquelle on recherche un nouvel Element a selectionner
	 */
	public final void selectionnerElementDansLaDirection(final int direction) {
		LOG.debug("Deplacement depuis l'element de menu "+this.elementSelectionne.id+" dans la direction "+direction);
		ElementDeMenu elementASelectionner;
		if (this.elementSelectionne.liste != null) {
			// L'ElementDeMenu est dans une Liste
			elementASelectionner = this.elementSelectionne.liste.selectionnerUnAutreElementDansLaListe(direction);
			if (elementASelectionner == null) {
				// On sort de la Liste
				elementASelectionner = chercherSelectionnableDansLaDirection(direction);
			}
			// On reste dans la Liste
		} else {
			// L'ElementDeMenu n'appartient pas a une Liste
			elementASelectionner = chercherSelectionnableDansLaDirection(direction);
		}
		selectionner(elementASelectionner);
	}
	
	/**
	 * Selectionner cet Element de Menu.
	 * @param elementASelectionner nouvel Element selectionne
	 */
	public final void selectionner(final ElementDeMenu elementASelectionner) {
		if (elementASelectionner != null) {
			//bruit de deplacement du curseur
			if (this.elementSelectionne!=null 
				&& (elementASelectionner.x!=this.elementSelectionne.x || elementASelectionner.y!=this.elementSelectionne.y)
			) {
				LecteurAudio.playSe(BRUIT_DEPLACEMENT_CURSEUR);
			}
			
			//deselection du precedent
			if (this.elementSelectionne != null) {
				this.elementSelectionne.selectionne = false;
				this.elementSelectionne.executionDesCommandesDeSurvol = false;
				this.elementSelectionne.curseurComportementSurvol = 0;
			}
			
			//selection du nouveau
			this.elementSelectionne = elementASelectionner;
			elementASelectionner.selectionne = true;
			this.elementSelectionne.executionDesCommandesDeSurvol = true;
			this.elementSelectionne.curseurComportementSurvol = 0;
		}
	}
	
	/**
	 * Obtenir la liste des Elements Selectionnables de ce Menu.
	 * @return liste des Selectionnables
	 */
	public final ArrayList<ElementDeMenu> getSelectionnables() {
		if (this.selectionnables==null) {
			//on ne l'a pas encore creee
			this.selectionnables = new ArrayList<ElementDeMenu>();
			for (Texte t : this.textes) {
				if (t.selectionnable) {
					this.selectionnables.add(t);
				}
			}
			for (ImageMenu e : this.images) {
				if (e.selectionnable) {
					this.selectionnables.add(e);
				}
			}
		}
		//on l'a creee
		return this.selectionnables;
	}
	
	/**
	 * Verifie si l'Element (x2,y2) est situe dans la bonne direction par rapport a l'Element de reference (x1,y1).
	 * @param direction dans laquelle il faut que l'Element teste se situe (par rapport a l'Element de reference) pour etre valide
	 * @param x1 coordonnee x de l'Element de reference
	 * @param y1 coordonnee y de l'Element de reference
	 * @param x2 coordonnee x de l'Element teste
	 * @param y2 coordonnee y de l'Element teste
	 * @param largeur toleree pour l'ecart avec la direction voulue
	 * @param hauteur toleree pour l'ecart avec la direction voulue
	 * @return true si l'Element teste est dans la bonne direction
	 */
	private Boolean estCandidatALaSelection(final int direction, final int x1, final int y1, final int x2, final int y2, final int largeur, final int hauteur) {
		switch(direction) {
			case Event.Direction.HAUT :
				return (Math.abs(x2-x1) <= 2*largeur) && (y2 > y1);
			case Event.Direction.BAS :
				return (Math.abs(x2-x1) <= 2*largeur) && (y2 < y1);
			case Event.Direction.GAUCHE :
				return (Math.abs(y2-y1) <= 2*hauteur) && (x2 > x1);
			case Event.Direction.DROITE :
				return (Math.abs(y2-y1) <= 2*hauteur) && (x2 < x1);
			default :
				return false;
		}
	}
	
	/**
	 * Calculer quel est l'Element de Menu Selectionnable situe dans une certaine direction par rapport a celui-ci
	 * @param direction dans laquelle on doit rechercher un Element a selectionner
	 * @return Element de Menu situe dans cette direction
	 */
	private ElementDeMenu chercherSelectionnableDansLaDirection(final int direction) {
		ElementDeMenu elementASelectionner = null;
		final ArrayList<ElementDeMenu> lesSelectionnables = getSelectionnables();
		int deltaX;
		int deltaY;
		int distance;
		Integer distanceMin = null;
		for (ElementDeMenu s : lesSelectionnables) {
			if ( estCandidatALaSelection(direction, s.x, s.y, this.elementSelectionne.x, this.elementSelectionne.y, this.elementSelectionne.largeur, this.elementSelectionne.hauteur) ) {
				deltaX = this.elementSelectionne.x-s.x;
				deltaY = this.elementSelectionne.y-s.y;
				distance = deltaX*deltaX + deltaY*deltaY;
				if (distanceMin==null || distance<distanceMin) {
					elementASelectionner = s;
					distanceMin = distance; //on memorise le plus proche rencontre
				}
			}
		}
		return elementASelectionner;
	}
	
	
	/**
	 * Reatualiser tous les Textes du Menu.
	 */
	public void reactualiserTousLesTextes() {
		for (ElementDeMenu element : this.elements.values()) {
			if (element instanceof Texte) {
				element.image = ((Texte) element).texteToImage();
			}
		}
	}
	
	/**
	 * Cree un objet Menu a partir d'un fichier JSON.
	 * @param nom du fichier JSON decrivant le Menu
	 * @param menuParent eventuel Menu qui a appele ce Menu
	 * @return Menu instancie
	 */
	public static Menu creerMenuDepuisJson(final String nom, final Menu menuParent) {
		final JSONObject jsonObject;
		try {
			jsonObject = InterpreteurDeJson.ouvrirJson(nom, "./ressources/Data/Menus/");
		} catch (Exception e) {
			LOG.error("Impossible d'ouvrir ke fichier JSON du menu "+nom, e);
			return null;
		}
		
		// Identifiant de l'ElementDeMenu deja selectionne par defaut
		final int idSelectionInitiale;
		if (jsonObject.has("selectionInitiale")) {
			if (jsonObject.get("selectionInitiale") instanceof String) {
				String nomSelectionInitiale = jsonObject.getString("selectionInitiale");
				switch (nomSelectionInitiale) {
				case "numeroPartie":
					idSelectionInitiale = Main.getPartieActuelle().id;
					break;
				default:
					LOG.error("La selection initiale du menu \""+nom+"\" est inconnue : "+nomSelectionInitiale);
					idSelectionInitiale = 0;
					break;
				}
			} else {
				idSelectionInitiale = jsonObject.getInt("selectionInitiale");
			}
		} else {
			idSelectionInitiale = 0;
		}
		
		// Identifiant de l'ElementDeMenu contenant les descriptions d'Objects
		final int idDescription = jsonObject.has("idDescription") ? (int) jsonObject.get("idDescription") : -1;
		
		// Image de fond du Menu
		BufferedImage fond = null;
		if (jsonObject.has("fond")) {
			final String nomFond = (String) jsonObject.get("fond");
			try {
				fond = Graphismes.ouvrirImage("Pictures", nomFond);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Comportement du Menu en cas d'annulation
		final ArrayList<Commande> comportementAnnulation = new ArrayList<Commande>();
		try {
			final JSONArray jsonComportementAnnulation = jsonObject.getJSONArray("comportementAnnulation");
			Commande.recupererLesCommandes(comportementAnnulation, jsonComportementAnnulation);
		} catch (JSONException e) {
			LOG.warn("Pas de comportement en cas d'annulation specifie pour le menu "+nom+".\n"
					+ "Comportement par defaut : revenir au menu parent ou revenir a la map.");
			comportementAnnulation.add(new FermerMenu());
		}

		// Elements du Menu
		final JSONArray jsonElements = jsonObject.getJSONArray("elements");
		final ArrayList<Texte> textes = new ArrayList<Texte>();
		final ArrayList<ImageMenu> images = new ArrayList<ImageMenu>();
		@SuppressWarnings("rawtypes")
		final ArrayList<Liste> listes = new ArrayList<Liste>();
		final ArrayList<Carte> cartes = new ArrayList<Carte>();
		final ElementDeMenu selectionInitiale = ElementDeMenu.recupererLesElementsDeMenu(idSelectionInitiale, jsonElements, images, textes, listes, cartes, nom);
		
		//instanciation
		final Menu menu = new Menu(fond, textes, images, listes, cartes, selectionInitiale, idDescription, menuParent, comportementAnnulation);	
		
		//associer un ElementDeMenu arbitraire aux Commandes en cas d'annulation pour qu'elles trouvent leur Menu
		for (Commande commande : menu.comportementAnnulation) {
			commande.element = menu.elements.get(0);
		}
		
		return menu;
	}
	
}
