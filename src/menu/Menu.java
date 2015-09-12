package menu;

import java.util.ArrayList;
import java.util.Random;

public abstract class Menu {
	public int identite = (new Random()).nextInt(100);
	public LecteurMenu lecteur;
	public ArrayList<Texte> textes;
	public ArrayList<ElementDeMenu> elements;
	public Selectionnable elementSelectionne;
	public String nomBGM;
	
	public void gauche(){
		//TODO
	}
	public void droite(){
		//TODO
	}
	public abstract void quitter();
	
	public void confirmer() {
		if(elementSelectionne != null){
			elementSelectionne.confirmer();
		}else{
			System.out.println("l'élément sélectionné de ce menu est null.");
		}
	}
	
	public void haut() {
		Selectionnable elementASelectionner = chercherSelectionnableAuDessus();
		selectionner(elementASelectionner);
	}

	public void bas() {
		Selectionnable elementASelectionner = chercherSelectionnableEnDessous();
		selectionner(elementASelectionner);
	}
	
	public void selectionner(Selectionnable elementASelectionner){
		if(elementASelectionner != null){
			if(this.elementSelectionne != null){
				this.elementSelectionne.selectionne = false;
			}
			this.elementSelectionne = elementASelectionner;
			elementASelectionner.selectionne = true;
			elementASelectionner.comportementALArrivee();
		}
	}
	
	public ArrayList<Selectionnable> getSelectionnables(){
		ArrayList<Selectionnable> selectionnables = new ArrayList<Selectionnable>();
		for(Texte t : this.textes){
			if(t.selectionnable){
				selectionnables.add(t);
			}
		}
		for(ElementDeMenu e : this.elements){
			if(e.selectionnable){
				selectionnables.add(e);
			}
		}
		return selectionnables;
	}
	
	public Selectionnable chercherSelectionnableAuDessus(){
		Selectionnable elementASelectionner = null;
		ArrayList<Selectionnable> selectionnables = getSelectionnables();
		for(Selectionnable selectionnable : selectionnables){
			//il doit être au dessus
			if(selectionnable.y < elementSelectionne.y){
				if(elementASelectionner != null){
					//on prend le plus proche en ordonnée
					if(selectionnable.y > elementASelectionner.y){
						//on prend le plus proche en abscisse
						if( Math.abs(selectionnable.x - elementSelectionne.x) <= Math.abs(elementASelectionner.x - elementSelectionne.x) ){
							elementASelectionner = selectionnable;
						}
					}
				}else{
					elementASelectionner = selectionnable;
				}
			}
		}
		return elementASelectionner;
	}
	
	public Selectionnable chercherSelectionnableEnDessous(){
		Selectionnable elementASelectionner = null;
		ArrayList<Selectionnable> selectionnables = getSelectionnables();
		for(Selectionnable selectionnable : selectionnables){
			//il doit être en dessous
			if(selectionnable.y > elementSelectionne.y){
				if(elementASelectionner != null){
					//on prend le plus proche en ordonnée
					if(selectionnable.y < elementASelectionner.y){
						//on prend le plus proche en abscisse
						if( Math.abs(selectionnable.x - elementSelectionne.x) <= Math.abs(elementASelectionner.x - elementSelectionne.x) ){
							elementASelectionner = selectionnable;
						}
					}
				}else{
					elementASelectionner = selectionnable;
				}
			}
		}
		return elementASelectionner;
	}
	
	public Selectionnable chercherSelectionnableAGauche(){
		Selectionnable elementASelectionner = null;
		ArrayList<Selectionnable> selectionnables = getSelectionnables();
		for(Selectionnable selectionnable : selectionnables){
			//il doit être à gauche
			if(selectionnable.x < elementSelectionne.x){
				if(elementASelectionner != null){
					//on prend le plus proche en abscisse
					if(selectionnable.x > elementASelectionner.x){
						//on prend le plus proche en ordonnée
						if( Math.abs(selectionnable.y - elementSelectionne.y) <= Math.abs(elementASelectionner.y - elementSelectionne.y) ){
							elementASelectionner = selectionnable;
						}
					}
				}else{
					elementASelectionner = selectionnable;
				}
			}
		}
		return elementASelectionner;
	}
	
	public Selectionnable chercherSelectionnableADroite(){
		Selectionnable elementASelectionner = null;
		ArrayList<Selectionnable> selectionnables = getSelectionnables();
		for(Selectionnable selectionnable : selectionnables){
			//il doit être à droite
			if(selectionnable.x > elementSelectionne.x){
				if(elementASelectionner != null){
					//on prend le plus proche en abscisse
					if(selectionnable.x < elementASelectionner.x){
						//on prend le plus proche en ordonnée
						if( Math.abs(selectionnable.y - elementSelectionne.y) <= Math.abs(elementASelectionner.y - elementSelectionne.y) ){
							elementASelectionner = selectionnable;
						}
					}
				}else{
					elementASelectionner = selectionnable;
				}
			}
		}
		return elementASelectionner;
	}
	
}
