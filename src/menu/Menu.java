package menu;

import java.util.ArrayList;

import son.LecteurAudio;

/**
 * Un Menu est constitué d'images et de Textes, éventuellement Sélectionnables.
 */
public abstract class Menu {
	public LecteurMenu lecteur;
	public ArrayList<Texte> textes;
	public ArrayList<ElementDeMenu> elements;
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
		Selectionnable elementASelectionner = chercherSelectionnableAuDessus();
		selectionner(elementASelectionner);
	}

	/**
	 * Sélectionner l'Elément Sélectionnable situé juste en dessous.
	 */
	public final void selectionnerElementEnBas() {
		Selectionnable elementASelectionner = chercherSelectionnableEnDessous();
		selectionner(elementASelectionner);
	}
	
	/**
	 * Sélectionner l'Elément Sélectionnable situé juste à gauche.
	 */
	public final void selectionnerElementAGauche() {
		Selectionnable elementASelectionner = chercherSelectionnableAGauche();
		selectionner(elementASelectionner);
	}
	
	/**
	 * Sélectionner l'Elément Sélectionnable situé juste à droite.
	 */
	public final void selectionnerElementADroite() {
		Selectionnable elementASelectionner = chercherSelectionnableADroite();
		selectionner(elementASelectionner);
	}
	
	/**
	 * Sélectionner cet Elément de Menu.
	 * @param elementASelectionner nouvel Element sélectionné
	 */
	public final void selectionner(final Selectionnable elementASelectionner) {
		if (elementASelectionner != null) {
			//bruit de déplacement du curseur
			if (this.elementSelectionne!=null && !elementASelectionner.equals(this.elementSelectionne)) {
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
	 * Obtenir la liste des Eléments Sélectionnables.
	 * @return liste des Sélectionnables
	 */
	public final ArrayList<Selectionnable> getSelectionnables() {
		ArrayList<Selectionnable> selectionnables = new ArrayList<Selectionnable>();
		for (Texte t : this.textes) {
			if (t.selectionnable) {
				selectionnables.add(t);
			}
		}
		for (ElementDeMenu e : this.elements) {
			if (e.selectionnable) {
				selectionnables.add(e);
			}
		}
		return selectionnables;
	}
	
	/**
	 * Calculer quel est l'Elément de Menu Sélectionnable situé au dessus de celui-ci
	 * @return Elément de Menu situé au dessus
	 */
	private Selectionnable chercherSelectionnableAuDessus() {
		Selectionnable elementASelectionner = null;
		ArrayList<Selectionnable> selectionnables = getSelectionnables();
		for (Selectionnable selectionnable : selectionnables) {
			//il doit être au dessus
			if (selectionnable.y < elementSelectionne.y) {
				if (elementASelectionner != null) {
					//on prend le plus proche en ordonnée
					if (selectionnable.y > elementASelectionner.y) {
						//on prend le plus proche en abscisse
						if ( Math.abs(selectionnable.x - elementSelectionne.x) <= Math.abs(elementASelectionner.x - elementSelectionne.x) ) {
							elementASelectionner = selectionnable;
						}
					}
				} else {
					elementASelectionner = selectionnable;
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
		ArrayList<Selectionnable> selectionnables = getSelectionnables();
		for (Selectionnable selectionnable : selectionnables) {
			//il doit être en dessous
			if (selectionnable.y > elementSelectionne.y) {
				if (elementASelectionner != null) {
					//on prend le plus proche en ordonnée
					if (selectionnable.y < elementASelectionner.y) {
						//on prend le plus proche en abscisse
						if ( Math.abs(selectionnable.x - elementSelectionne.x) <= Math.abs(elementASelectionner.x - elementSelectionne.x) ) {
							elementASelectionner = selectionnable;
						}
					}
				} else {
					elementASelectionner = selectionnable;
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
		ArrayList<Selectionnable> selectionnables = getSelectionnables();
		for (Selectionnable selectionnable : selectionnables) {
			//il doit être à gauche
			if (selectionnable.x < elementSelectionne.x) {
				if (elementASelectionner != null) {
					//on prend le plus proche en abscisse
					if (selectionnable.x > elementASelectionner.x) {
						//on prend le plus proche en ordonnée
						if ( Math.abs(selectionnable.y - elementSelectionne.y) <= Math.abs(elementASelectionner.y - elementSelectionne.y) ) {
							elementASelectionner = selectionnable;
						}
					}
				} else {
					elementASelectionner = selectionnable;
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
		ArrayList<Selectionnable> selectionnables = getSelectionnables();
		for (Selectionnable selectionnable : selectionnables) {
			//il doit être à droite
			if (selectionnable.x > elementSelectionne.x) {
				if (elementASelectionner != null) {
					//on prend le plus proche en abscisse
					if (selectionnable.x < elementASelectionner.x) {
						//on prend le plus proche en ordonnée
						if ( Math.abs(selectionnable.y - elementSelectionne.y) <= Math.abs(elementASelectionner.y - elementSelectionne.y) ) {
							elementASelectionner = selectionnable;
						}
					}
				} else {
					elementASelectionner = selectionnable;
				}
			}
		}
		return elementASelectionner;
	}
	
}
