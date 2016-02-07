package commandes;

import java.util.ArrayList;

import main.Commande;
import main.Fenetre;
import menu.ElementDeMenu;

/**
 * Créer une nouvelle Partie vierge et y jouer
 */
public class OuvrirNouvellePartie implements CommandeMenu {
	private ElementDeMenu element;
	
	@Override
	public final void executer() {
		final Fenetre fenetre = this.element.menu.lecteur.fenetre;
		System.out.println("nouvelle partie");
		fenetre.setPartieActuelle(null);
		fenetre.ouvrirLaPartie();
	}

	@Override
	public final ElementDeMenu getElement() {
		return element;
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
