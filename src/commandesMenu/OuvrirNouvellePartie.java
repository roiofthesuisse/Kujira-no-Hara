package commandesMenu;

import main.Fenetre;

/**
 * Cr�er une nouvelle Partie vierge et y jouer
 */
public class OuvrirNouvellePartie extends CommandeMenu {
	
	@Override
	public final void executer() {
		final Fenetre fenetre = this.element.menu.lecteur.fenetre;
		System.out.println("nouvelle partie");
		fenetre.setPartieActuelle(null);
		fenetre.ouvrirLaPartie();
	}
	
}