package commandes;

import java.util.ArrayList;

import main.Commande;

public class AfficherCarte extends Commande implements CommandeMenu {
	private final int numeroCarte;
	private final int xCarte, yCarte;
	
	public AfficherCarte(int numeroCarte, int xCarte, int yCarte) {
		this.numeroCarte = numeroCarte;
		this.xCarte = xCarte;
		this.yCarte = yCarte;
	}

	@Override
	public int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.element.menu.carte.changerCadrage(this.numeroCarte, this.xCarte, this.yCarte);
		return curseurActuel+1;
	}

}
