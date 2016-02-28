package commandes;

import java.util.ArrayList;

import main.Commande;
import menu.LecteurMenu;

/**
 * Quitter le Menu pour revenir sur la Map mémorisée.
 */
public class RevenirAuJeu extends Commande implements CommandeMenu {
	private final LecteurMenu lecteurMenu;
	
	/**
	 * Constructeur explicite
	 * @param lecteurMenu du Menu qui appelle cette CommandeMenu
	 */
	public RevenirAuJeu(final LecteurMenu lecteurMenu) {
		this.lecteurMenu = lecteurMenu;
	}
	
	@Override
	public final void executer() {
		this.lecteurMenu.fenetre.futurLecteur = this.lecteurMenu.lecteurMapMemorise;
		this.lecteurMenu.allume = false;
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.executer();
		return curseurActuel+1;
	}

}
