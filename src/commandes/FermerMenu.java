package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import menu.LecteurMenu;

/**
 * Fermer le Menu actif.
 * Fermer le Menu sur la Map est donc IMPOSSIBLE.
 * Dans un Menu, on sort du sous-Menu ou bien on revient sur la Map (s'il n'y a pas de sur-Menu), ou encore on quitte le jeu s'il n'y a pas de Map non plus.
 */
public class FermerMenu extends Commande implements CommandeMenu {
	
	/**
	 * Constructeur explicite
	 */
	public FermerMenu() {
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public FermerMenu(final HashMap<String, Object> parametres) {
		this();
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final LecteurMenu lecteur = this.element.menu.lecteur;
		if (lecteur.menu.menuParent!=null) {
			//revenir au Menu parent
			new LecteurMenu(lecteur.fenetre, lecteur.menu.menuParent, lecteur.lecteurMapMemorise).changerMenu();
		} else if (lecteur.lecteurMapMemorise!=null) {
			//revenir au jeu
			new RevenirAuJeu(lecteur).executer(0, null);
		} else {
			//quitte le jeu
			lecteur.fenetre.dispose();
		}
		
		return curseurActuel+1;
	}

}
