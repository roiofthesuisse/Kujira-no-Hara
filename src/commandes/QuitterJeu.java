package commandes;

import java.util.ArrayList;

import main.Commande;
import main.Fenetre;

/**
 * Eteindre le jeu.
 */
public class QuitterJeu extends Commande implements CommandeMenu {
	
	/**
	 * Constructeur vide
	 */
	public QuitterJeu() {
		
	}
	
	@Override
	public final void executer() {
		Fenetre.getFenetre().fermer();
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.executer();
		return curseurActuel+1;
	}

}
