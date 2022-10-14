package menu;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import conditions.Condition;
import main.Commande;


/**
 * Un Listable peut avoir ses informations affichees dans une Liste.
 */
public interface Listable {
	/** Nom du package.dossier */
	String PREFIXE_NOM_CLASSE = "jeu.";
	
	/**
	 * Construire une image qui sera l'apparence de l'ElementDeMenu dans la Liste. 
	 * @param largeur minimale des �l�ments de la liste
	 * @param hauteur minimale des �l�ments de la liste
	 * @return image d'informations sur le collectable
	 */
	BufferedImage construireImagePourListe(final int largeur, final int hauteur);
	
	/**
	 * Obtenir les Conditions requises a l'affichage de l'ElementDeMenu.
	 * @return Conditions d'affichage
	 */
	ArrayList<Condition> getConditions();
	
	/**
	 * Comportement au survol de l'ElementDeMenu.
	 * @return CommandesMenu a executer
	 */
	ArrayList<Commande> getComportementSelection();
	
	/**
	 * Comportement a la confirmation de l'ElementDeMenu.
	 * @return CommandesMenu a executer
	 */
	ArrayList<Commande> getComportementConfirmation();
	
}
