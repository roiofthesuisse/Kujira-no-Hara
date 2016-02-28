package commandes;

import java.util.ArrayList;

import main.Commande;
import main.Fenetre;

/**
 * Créer une nouvelle Partie vierge et y jouer
 */
public class OuvrirNouvellePartie extends Commande implements CommandeMenu {
	
	@Override
	public final void executer() {
		final Fenetre fenetre = this.element.menu.lecteur.fenetre;
		System.out.println("nouvelle partie");
		fenetre.setPartieActuelle(null);
		fenetre.ouvrirLaPartie();
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.executer();
		return curseurActuel+1;
	}
	
}
