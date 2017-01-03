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
import utilitaire.InterpreteurDeJson;

/**
 * Passer à un autre Menu
 */
public class AllerVersUnAutreMenu extends Commande implements CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(AllerVersUnAutreMenu.class);
	
	private final String nomMenu;
	public Menu nouveauMenu;
	
	/**
	 * Constructeur explicite
	 * @param nouveauMenu Menu qui remplacera l'actuel
	 */
	public AllerVersUnAutreMenu(final String nouveauMenu) {
		this.nomMenu = nouveauMenu;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AllerVersUnAutreMenu(final HashMap<String, Object> parametres) {
		this((String) parametres.get("menu"));
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.nouveauMenu = InterpreteurDeJson.creerMenuDepuisJson(this.nomMenu, this.element.menu);
		LOG.info(this.nomMenu);
		
		final LecteurMap lecteurMapMemorise = this.element.menu.lecteur.lecteurMapMemorise;
		final LecteurMenu nouveauLecteur = new LecteurMenu(Fenetre.getFenetre(), this.nouveauMenu, lecteurMapMemorise);
		nouveauLecteur.changerMenu();
		
		return curseurActuel+1;
	}

}
