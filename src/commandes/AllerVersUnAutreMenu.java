package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import main.Fenetre;
import map.LecteurMap;
import menu.LecteurMenu;
import menu.Menu;

/**
 * Passer à un autre Menu
 */
public class AllerVersUnAutreMenu extends Commande implements CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(AllerVersUnAutreMenu.class);
	
	private final String nomMenu;
	private final Integer selectionInitiale;
	
	/**
	 * Constructeur explicite
	 * @param nouveauMenu Menu qui remplacera l'actuel
	 * @param selectionInitiale identifiant de l'ElementDeMenu à sélectionner au début
	 */
	public AllerVersUnAutreMenu(final String nouveauMenu, final Integer selectionInitiale) {
		this.nomMenu = nouveauMenu;
		this.selectionInitiale = selectionInitiale;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AllerVersUnAutreMenu(final HashMap<String, Object> parametres) {
		this(
				(String) parametres.get("menu"),
				(Integer) parametres.get("selectionInitiale")
		);
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final Menu nouveauMenu = Menu.creerMenuDepuisJson(this.nomMenu, this.element.menu);
		LOG.info("Nom du nouveau menu : "+this.nomMenu);
		
		final LecteurMap lecteurMapMemorise = this.element.menu.lecteur.lecteurMapMemorise;
		final LecteurMenu nouveauLecteur = new LecteurMenu(Fenetre.getFenetre(), nouveauMenu, lecteurMapMemorise, this.selectionInitiale);
		nouveauLecteur.changerMenu();
		
		return curseurActuel+1;
	}

}
