package menu;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import conditions.Condition;
import main.Commande;


/**
 * Un Listable peut avoir ses informations affichées dans une Liste.
 */
public interface Listable {
	/** Nom du package.dossier */
	String PREFIXE_NOM_CLASSE = "jeu.";
	
	/**
	 * Construire une image qui sera l'apparence de l'ElementDeMenu dans la Liste. 
	 * @return image d'informations sur le collectable
	 */
	BufferedImage construireImagePourListe();
	
	/**
	 * Obtenir les Conditions requises à l'affichage de l'ElementDeMenu.
	 * @return Conditions d'affichage
	 */
	ArrayList<Condition> getConditions();
	
	/**
	 * Comportement au survol de l'ElementDeMenu.
	 * @return CommandesMenu à executer
	 */
	ArrayList<Commande> getComportementSelection();
	
	/**
	 * Comportement à la confirmation de l'ElementDeMenu.
	 * @return CommandesMenu à executer
	 */
	ArrayList<Commande> getComportementConfirmation();
	
}
