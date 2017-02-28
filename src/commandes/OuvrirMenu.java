package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;
import main.Lecteur;
import map.LecteurMap;
import menu.LecteurMenu;
import menu.Menu;

/**
 * Ouvrir un Menu.
 */
public class OuvrirMenu extends Commande implements CommandeEvent, CommandeMenu {
	final String nomMenu;
	final int selectionInitiale;
	
	private boolean leMenuAEteOuvert;
	
	/**
	 * Constructeur explicite
	 * @param nomMenu du Menu
	 * @param selectionInitiale identifiant de l'ElementDeMenu à sélectionner au début
	 */
	public OuvrirMenu(final String nomMenu, final int selectionInitiale) {
		this.nomMenu = nomMenu;
		this.selectionInitiale = selectionInitiale;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public OuvrirMenu(final HashMap<String, Object> parametres) {
		this( 
				(String) parametres.get("nomMenu"),
				(int) parametres.get("selectionInitiale")
		);
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		if (!leMenuAEteOuvert) {
			// On ouvre le Menu
			
			final Fenetre fenetre = Fenetre.getFenetre();
			final Lecteur lecteur = fenetre.lecteur;
			final LecteurMenu nouveauLecteur;
			final Menu nouveauMenu = Menu.creerMenuDepuisJson(this.nomMenu, null); //pas de Menu parent car appelé depuis la Map
			if (lecteur instanceof LecteurMenu) {
				// Le Menu est ouvert depuis un autre Menu
				final LecteurMenu lecteurActuel = (LecteurMenu) lecteur;
				nouveauMenu.menuParent = lecteurActuel.menu;
				nouveauLecteur = new LecteurMenu(fenetre, nouveauMenu, lecteurActuel.lecteurMapMemorise, this.selectionInitiale);
			} else {
				// Le Menu est ouvert depuis une Map
				final LecteurMap lecteurActuel = (LecteurMap) lecteur;
				nouveauLecteur = new LecteurMenu(fenetre, nouveauMenu, lecteurActuel, this.selectionInitiale);
			}

			leMenuAEteOuvert = true;
			nouveauLecteur.changerMenu();
			return curseurActuel;
			
		} else {
			// On revient du Menu, on passe à la Commande suivante
			leMenuAEteOuvert = false;
			return curseurActuel+1;
		}
	}

}
