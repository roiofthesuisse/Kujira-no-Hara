package menu;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;

import main.Commande;


/**
 * Un Listable peut avoir ses informations affichées dans une Liste.
 */
public interface Listable {
	/** Nom du package.dossier */
	String PREFIXE_NOM_CLASSE = "jeu.";
	
	Map<Integer, Listable> obtenirTousLesListables(Boolean possedes);
	
	BufferedImage construireImagePourListe(ArrayList<String> information);
	
	ArrayList<Commande> getComportementConfirmation();
	
	ArrayList<Commande> getComportementSelection();
}
