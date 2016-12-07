package menu;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import map.Event;
import son.LecteurAudio;

/**
 * Un Menu est constitué d'images et de Textes, éventuellement Sélectionnables.
 * Le Menu préexiste à son Lecteur. Le Menu ne connaîtra son Lecteur que lorsque le Lecteur sera instancié.
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
	public  ArrayList<Image> images;
	public HashMap<Integer, ElementDeMenu> elements;
	private ArrayList<ElementDeMenu> selectionnables;
	public ElementDeMenu elementSelectionne;
	public String nomBGM;
	public Menu menuSuivant;
	public Menu menuPrecedent;
	public Menu menuParent;

	/**
	 * Constructeur explicite
	 * @param fond image de fond du Menu
	 * @param textes du Menu
	 * @param images du Menu
	 * @param selectionInitiale ElementDeMenu sélectionné au début
	 * @param menuParent Menu qui a appelé ce Menu
	 */
	public Menu(final BufferedImage fond, final ArrayList<Texte> textes, final ArrayList<Image> images, final ElementDeMenu selectionInitiale, final int idTexteDescriptif, final Menu menuParent) {
		this.fond = fond;
		
		this.elements = new HashMap<Integer, ElementDeMenu>();
		this.textes = textes;
		for (Texte texte : textes) {
			texte.menu = this;
			this.elements.put((Integer) texte.id, texte);
		}
		this.images = images;
		for (Image image : images) {
			image.menu = this;
			this.elements.put((Integer) image.id, image);
		}
		
		this.texteDescriptif = (Texte) this.elements.get((Integer) idTexteDescriptif);
		
		this.elementSelectionne = selectionInitiale;
		selectionInitiale.selectionner();
		
		this.menuParent = menuParent;
	}
	
	/**
	 * Confirmer l'Elément de Menu sélectionné.
	 */
	public final void confirmer() {
		if (elementSelectionne != null) {
			LecteurAudio.playSe(BRUIT_CONFIRMER_SELECTION);
			elementSelectionne.confirmer();
		} else {
			LOG.error("l'élément sélectionné de ce menu est null.");
		}
	}
	
	/**
	 * Sélectionner l'Elément Sélectionnable situé dans cette direction
	 * @param direction dans laquelle on recherche un nouvel Elément à sélectionner
	 */
	public final void selectionnerElementDansLaDirection(final int direction) {
		final ElementDeMenu elementASelectionner = chercherSelectionnableDansLaDirection(direction);
		selectionner(elementASelectionner);
	}
	
	/**
	 * Sélectionner cet Elément de Menu.
	 * @param elementASelectionner nouvel Element sélectionné
	 */
	public final void selectionner(final ElementDeMenu elementASelectionner) {
		if (elementASelectionner != null) {
			//bruit de déplacement du curseur
			if (this.elementSelectionne!=null 
				&& (elementASelectionner.x!=this.elementSelectionne.x || elementASelectionner.y!=this.elementSelectionne.y)
			) {
				LecteurAudio.playSe(BRUIT_DEPLACEMENT_CURSEUR);
			}
			//désélection du précédent
			if (this.elementSelectionne != null) {
				this.elementSelectionne.deselectionner();
			}
			//sélection du nouveau
			this.elementSelectionne = elementASelectionner;
			elementASelectionner.selectionner();
		}
	}
	
	/**
	 * Obtenir la liste des Eléments Sélectionnables de ce Menu.
	 * @return liste des Sélectionnables
	 */
	public final ArrayList<ElementDeMenu> getSelectionnables() {
		if (this.selectionnables==null) {
			//on ne l'a pas encore créée
			this.selectionnables = new ArrayList<ElementDeMenu>();
			for (Texte t : this.textes) {
				if (t.selectionnable) {
					this.selectionnables.add(t);
				}
			}
			for (Image e : this.images) {
				if (e.selectionnable) {
					this.selectionnables.add(e);
				}
			}
		}
		//on l'a créée
		return this.selectionnables;
	}
	
	/**
	 * Vérifie si l'Elément (x2,y2) est situé dans la bonne direction par rapport à l'Elément de référence (x1,y1).
	 * @param direction dans laquelle il faut que l'Elément testé se situe (par rapport à l'Elément de référence) pour être valide
	 * @param x1 coordonnée x de l'Elément de référence
	 * @param y1 coordonnée y de l'Elément de référence
	 * @param x2 coordonnée x de l'Elément testé
	 * @param y2 coordonnée y de l'Elément testé
	 * @param largeur tolérée pour l'écart avec la direction voulue
	 * @param hauteur tolérée pour l'écart avec la direction voulue
	 * @return true si l'Elément testé est dans la bonne direction
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
	 * Calculer quel est l'Elément de Menu Sélectionnable situé dans une certaine direction par rapport à celui-ci
	 * @param direction dans laquelle on doit rechercher un Elément à sélectionner
	 * @return Elément de Menu situé dans cette direction
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
					distanceMin = distance; //on mémorise le plus proche rencontré
				}
			}
		}
		return elementASelectionner;
	}
	
}
