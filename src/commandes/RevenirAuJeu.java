package commandes;

import java.util.ArrayList;

import main.Commande;
import menu.ElementDeMenu;
import menu.LecteurMenu;

/**
 * Quitter le Menu pour revenir sur la Map mémorisée.
 */
public class RevenirAuJeu implements CommandeMenu {
	private final LecteurMenu lecteurMenu;
	private ElementDeMenu element;
	
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
	public final ElementDeMenu getElement() {
		return this.element;
	}

	@Override
	public final void setElement(final ElementDeMenu element) {
		this.element = element;
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<? extends Commande> commandes) {
		this.executer();
		return curseurActuel+1;
	}

}
