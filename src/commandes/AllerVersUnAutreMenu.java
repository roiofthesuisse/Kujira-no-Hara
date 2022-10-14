package commandes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import map.LecteurMap;
import menu.LecteurMenu;
import menu.Menu;

/**
 * Passer a un autre Menu
 */
public class AllerVersUnAutreMenu extends Commande implements CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(AllerVersUnAutreMenu.class);
	private static final boolean EST_UN_MENU_FRERE_PAR_DEFAUT = false;

	private final String nomMenu;
	private final Integer selectionInitiale;
	private final boolean estUnMenuFrere;

	/**
	 * Constructeur explicite
	 * 
	 * @param nouveauMenu       Menu qui remplacera l'actuel
	 * @param selectionInitiale identifiant de l'ElementDeMenu a s�lectionner au
	 *                          d�but
	 * @param estUnMenuFrere    le Menu d'arriv�e est-il hierarchiquement �quivalent
	 *                          au Menu de d�part ?
	 */
	public AllerVersUnAutreMenu(final String nouveauMenu, final Integer selectionInitiale,
			final boolean estUnMenuFrere) {
		this.nomMenu = nouveauMenu;
		this.selectionInitiale = selectionInitiale;
		this.estUnMenuFrere = estUnMenuFrere;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public AllerVersUnAutreMenu(final HashMap<String, Object> parametres) {
		this((String) parametres.get("menu"), (Integer) parametres.get("selectionInitiale"),
				parametres.containsKey("frere") ? (Boolean) parametres.get("frere") : EST_UN_MENU_FRERE_PAR_DEFAUT);
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		// Quel est le Menu parent du prochain Menu ?
		Menu menuParent;
		if (estUnMenuFrere) {
			// Le Menu actuel est le fr�re du prochain Menu, ils ont le m�me Menu parent
			menuParent = this.element.menu.menuParent;
		} else {
			// Le Menu actuel est le p�re du prochain Menu
			menuParent = this.element.menu;
		}

		final Menu nouveauMenu = Menu.creerMenuDepuisJson(this.nomMenu, menuParent);
		LOG.info("Nom du nouveau menu : " + this.nomMenu);

		// Transmettre le lecteur de Map
		final LecteurMap lecteurMapMemorise = this.element.menu.lecteur.lecteurMapMemorise;

		// Demarrer le nouveau Menu
		final LecteurMenu nouveauLecteur = new LecteurMenu(nouveauMenu, lecteurMapMemorise, this.selectionInitiale);
		nouveauLecteur.changerMenu();

		return curseurActuel + 1;
	}

}
