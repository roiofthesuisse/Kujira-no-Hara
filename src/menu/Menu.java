package menu;

import java.util.ArrayList;
import java.util.Random;

import son.LecteurAudio;

public abstract class Menu {
	public int identite = (new Random()).nextInt(100);
	public LecteurMenu lecteur;
	public ArrayList<Texte> textes;
	public ArrayList<ElementDeMenu> elements;
	public Selectionnable elementSelectionne;
	public String nomBGM;
	
	public abstract void quitter();
	
	public void confirmer() {
		if(elementSelectionne != null){
			elementSelectionne.confirmer();
		}else{
			System.out.println("l'élément sélectionné de ce menu est null.");
		}
	}
	
	public void selectionnerElementEnHaut() {
		Selectionnable elementASelectionner = chercherSelectionnableAuDessus();
		selectionner(elementASelectionner);
	}

	public void selectionnerElementEnBas() {
		Selectionnable elementASelectionner = chercherSelectionnableEnDessous();
		selectionner(elementASelectionner);
	}
	
	public void selectionnerElementAGauche(){
		Selectionnable elementASelectionner = chercherSelectionnableAGauche();
		selectionner(elementASelectionner);
	}
	public void selectionnerElementADroite(){
		Selectionnable elementASelectionner = chercherSelectionnableADroite();
		selectionner(elementASelectionner);
	}
	
	public void selectionner(Selectionnable elementASelectionner){
		if(elementASelectionner != null){
			//bruit de déplacement du curseur
			if(this.elementSelectionne!=null && !elementASelectionner.equals(this.elementSelectionne)){
				LecteurAudio.playSe("Curseur.wav");
			}
			//désélection du précédent
			if(this.elementSelectionne != null){
				this.elementSelectionne.selectionne = false;
			}
			//sélection du nouveau
			this.elementSelectionne = elementASelectionner;
			elementASelectionner.selectionne = true;
			//déclenchement du comportement
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
