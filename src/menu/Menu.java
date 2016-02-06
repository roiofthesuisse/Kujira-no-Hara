package menu;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import map.Event;
import son.LecteurAudio;

/**
 * Un Menu est constitué d'images et de Textes, éventuellement Sélectionnables.
 */
public abstract class Menu {
	//constantes
	protected static final int LARGEUR_ELEMENT_PAR_DEFAUT = 48;
	protected static final int HAUTEUR_ELEMENT_PAR_DEFAUT = 32;
	
	public LecteurMenu lecteur;
	public BufferedImage fond;
	public final ArrayList<Texte> textes = new ArrayList<Texte>();
	public final ArrayList<Element> elements = new ArrayList<Element>();
	private ArrayList<Selectionnable> selectionnables;
	public Selectionnable elementSelectionne;
	public String nomBGM;
	public Menu menuSuivant;
	public Menu menuPrecedent;
	public Menu menuParent;

	/**
	 * Confirmer l'Elément de Menu sélectionné.
	 */
	public final void confirmer() {
		if (elementSelectionne != null) {
			LecteurAudio.playSe("Confirmer.wav");
			elementSelectionne.confirmer();
		} else {
			System.out.println("l'élément sélectionné de ce menu est null.");
		}
	}
	
	/**
	 * Sélectionner l'Elément Sélectionnable situé dans cette direction
	 * @param direction dans laquelle on recherche un nouvel Elément à sélectionner
	 */
	public final void selectionnerElementDansLaDirection(final int direction) {
		final Selectionnable elementASelectionner = chercherSelectionnableDansLaDirection(direction);
		selectionner(elementASelectionner);
	}
	
	/**
	 * Sélectionner cet Elément de Menu.
	 * @param elementASelectionner nouvel Element sélectionné
	 */
	public final void selectionner(final Selectionnable elementASelectionner) {
		if (elementASelectionner != null) {
			//bruit de déplacement du curseur
			if (this.elementSelectionne!=null 
				&& (elementASelectionner.x!=this.elementSelectionne.x || elementASelectionner.y!=this.elementSelectionne.y)
			) {
				LecteurAudio.playSe("DeplacementCurseur.wav");
			}
			//désélection du précédent
			if (this.elementSelectionne != null) {
				this.elementSelectionne.selectionne = false;
			}
			//sélection du nouveau
			this.elementSelectionne = elementASelectionner;
			elementASelectionner.selectionne = true;
			
			//déclenchement du comportement
			elementASelectionner.executerLeComportementALArrivee();
		}
	}
	
	/**
	 * Obtenir la liste des Eléments Sélectionnables de ce Menu.
	 * @return liste des Sélectionnables
	 */
	public final ArrayList<Selectionnable> getSelectionnables() {
		if (this.selectionnables==null) {
			//on ne l'a pas encore créée
			this.selectionnables = new ArrayList<Selectionnable>();
			for (Texte t : this.textes) {
				if (t.selectionnable) {
					this.selectionnables.add(t);
				}
			}
			for (Element e : this.elements) {
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
	private Selectionnable chercherSelectionnableDansLaDirection(final int direction) {
		Selectionnable elementASelectionner = null;
		final ArrayList<Selectionnable> lesSelectionnables = getSelectionnables();
		int deltaX;
		int deltaY;
		int distance;
		Integer distanceMin = null;
		for (Selectionnable s : lesSelectionnables) {
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
