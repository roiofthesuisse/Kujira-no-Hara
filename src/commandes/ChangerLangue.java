package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;
import menu.ImageMenu;
import menu.LecteurMenu;
import menu.Listable;
import menu.Liste;
import menu.Menu;
import menu.Texte;

/**
 * Changer la langue du jeu.
 */
public class ChangerLangue extends Commande implements CommandeMenu {
	final private int nouvelleLangue;
	
	/**
	 * Constructeur explicite
	 * @param nouvelleLangue numéro de la nouvelle langue à adopter
	 */
	private ChangerLangue(final int nouvelleLangue){
		this.nouvelleLangue = nouvelleLangue;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ChangerLangue(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("langue") );
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int executer(int curseurActuel, ArrayList<Commande> commandes) {
		Fenetre.langue = this.nouvelleLangue;
		Menu menu = ((LecteurMenu) Fenetre.getFenetre().lecteur).menu;
		for (Texte texte : menu.textes) {
			texte.actualiserImage();
		}
		for (Liste<Listable> liste : menu.listes) {
			liste.elements = liste.genererLesImagesDesElements();
			for(ImageMenu element : liste.elements){
				element.menu = menu;
			}
			liste.determinerLesElementsAAfficher();
		}
		return curseurActuel+1;
	}

}
