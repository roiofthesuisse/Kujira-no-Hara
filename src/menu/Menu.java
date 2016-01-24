package menu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import son.LecteurAudio;

/**
 * Un Menu est constitué d'images et de Textes, éventuellement Sélectionnables.
 */
public abstract class Menu {
	//constantes
	protected static final BufferedImage ICONE_VIDE = chargerIconeVide(); 
	//TODO utiliser un constructeur largeur;hauteur au lieu d'une icone vide
	//un rectangle fictif est plus rapide à superposer qu'une image vide
	
	public LecteurMenu lecteur;
	public BufferedImage fond;
	public final ArrayList<Texte> textes = new ArrayList<Texte>();
	public final ArrayList<ElementDeMenu> elements = new ArrayList<ElementDeMenu>();
	private ArrayList<Selectionnable> selectionnables;
	public Selectionnable elementSelectionne;
	public String nomBGM;
	
	/**
	 * Quitter ce Menu.
	 */
	public abstract void quitter();

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
	 * Sélectionner l'Elément Sélectionnable situé juste au dessus.
	 */
	public final void selectionnerElementEnHaut() {
		final Selectionnable elementASelectionner = chercherSelectionnableAuDessus();
		selectionner(elementASelectionner);
	}

	/**
	 * Sélectionner l'Elément Sélectionnable situé juste en dessous.
	 */
	public final void selectionnerElementEnBas() {
		final Selectionnable elementASelectionner = chercherSelectionnableEnDessous();
		selectionner(elementASelectionner);
	}
	
	/**
	 * Sélectionner l'Elément Sélectionnable situé juste à gauche.
	 */
	public final void selectionnerElementAGauche() {
		final Selectionnable elementASelectionner = chercherSelectionnableAGauche();
		selectionner(elementASelectionner);
	}
	
	/**
	 * Sélectionner l'Elément Sélectionnable situé juste à droite.
	 */
	public final void selectionnerElementADroite() {
		final Selectionnable elementASelectionner = chercherSelectionnableADroite();
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
			for (ElementDeMenu e : this.elements) {
				if (e.selectionnable) {
					this.selectionnables.add(e);
				}
			}
		}
		//on l'a créée
		return this.selectionnables;
	}
	
	/**
	 * Calculer quel est l'Elément de Menu Sélectionnable situé au dessus de celui-ci
	 * @return Elément de Menu situé au dessus
	 */
	private Selectionnable chercherSelectionnableAuDessus() {
		Selectionnable elementASelectionner = null;
		final ArrayList<Selectionnable> lesSelectionnables = getSelectionnables();
		int deltaY;
		Integer deltaYMin = null;
		for (Selectionnable s : lesSelectionnables) {
			if ( Math.abs(this.elementSelectionne.x-s.x) <= 2*this.elementSelectionne.largeur 
				&& this.elementSelectionne.y > s.y
			) {
				deltaY = Math.abs(this.elementSelectionne.y-s.y);
				if (deltaYMin==null || deltaY<deltaYMin) {
					elementASelectionner = s;
					deltaYMin = deltaY; //on mémorise le plus proche rencontré
				}
			}
		}
		return elementASelectionner;
	}
	
	/**
	 * Calculer quel est l'Elément de Menu Sélectionnable situé en dessous de celui-ci
	 * @return Elément de Menu situé en dessous
	 */
	private Selectionnable chercherSelectionnableEnDessous() {
		Selectionnable elementASelectionner = null;
		final ArrayList<Selectionnable> lesSelectionnables = getSelectionnables();
		int deltaY;
		Integer deltaYMin = null;
		for (Selectionnable s : lesSelectionnables) {
			if ( Math.abs(this.elementSelectionne.x-s.x) <= 2*this.elementSelectionne.largeur 
				&& this.elementSelectionne.y < s.y
			) {
				deltaY = Math.abs(this.elementSelectionne.y-s.y);
				if (deltaYMin==null || deltaY<deltaYMin) {
					elementASelectionner = s;
					deltaYMin = deltaY; //on mémorise le plus proche rencontré
				}
			}
		}
		return elementASelectionner;
	}
	
	/**
	 * Calculer quel est l'Elément de Menu Sélectionnable situé à gauche de celui-ci
	 * @return Elément de Menu situé à gauche
	 */
	private Selectionnable chercherSelectionnableAGauche() {
		Selectionnable elementASelectionner = null;
		final ArrayList<Selectionnable> lesSelectionnables = getSelectionnables();
		int deltaX;
		Integer deltaXMin = null;
		for (Selectionnable s : lesSelectionnables) {
			if ( Math.abs(this.elementSelectionne.y-s.y) <= 2*this.elementSelectionne.hauteur 
				&& this.elementSelectionne.x > s.x
			) {
				deltaX = Math.abs(this.elementSelectionne.x-s.x);
				if (deltaXMin==null || deltaX<deltaXMin) {
					elementASelectionner = s;
					deltaXMin = deltaX; //on mémorise le plus proche rencontré
				}
			}
		}
		return elementASelectionner;
	}
	
	/**
	 * Calculer quel est l'Elément de Menu Sélectionnable situé à droite de celui-ci
	 * @return Elément de Menu situé à droite
	 */
	private Selectionnable chercherSelectionnableADroite() {
		Selectionnable elementASelectionner = null;
		final ArrayList<Selectionnable> lesSelectionnables = getSelectionnables();
		int deltaX;
		Integer deltaXMin = null;
		for (Selectionnable s : lesSelectionnables) {
			if ( Math.abs(this.elementSelectionne.y-s.y) <= 2*this.elementSelectionne.hauteur 
				&& this.elementSelectionne.x < s.x
			) {
				deltaX = Math.abs(this.elementSelectionne.x-s.x);
				if (deltaXMin==null || deltaX<deltaXMin) {
					elementASelectionner = s;
					deltaXMin = deltaX; //on mémorise le plus proche rencontré
				}
			}
		}
		return elementASelectionner;
	}
	
	/**
	 * Icone vide pour les objets non possédés dans les Menus
	 * @return image d'icône vide
	 */
	private static BufferedImage chargerIconeVide() {
		try {
			return ImageIO.read(new File(".\\ressources\\Graphics\\Icons\\icone vide32.png"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
