package jeu.courrier;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import conditions.Condition;
import main.Commande;
import menu.Listable;

/**
 * Lettre recue par un personnage du jeu via la poste.
 */
public class LettreRecue extends Lettre implements Listable {

	@Override
	public BufferedImage construireImagePourListe(int largeur, int hauteur) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Condition> getConditions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Commande> getComportementSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Commande> getComportementConfirmation() {
		// TODO Auto-generated method stub
		return null;
	}

}
