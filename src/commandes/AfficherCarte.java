package commandes;

import java.awt.image.BufferedImage;
import java.util.List;

import main.Commande;

public class AfficherCarte extends Commande implements CommandeMenu {
	private final int numeroCarte;
	private final int xCarte, yCarte;
	private final BufferedImage icone;

	public AfficherCarte(int numeroCarte, int xCarte, int yCarte, BufferedImage icone) {
		this.numeroCarte = numeroCarte;
		this.xCarte = xCarte;
		this.yCarte = yCarte;
		this.icone = icone;
	}

	@Override
	public int executer(final int curseurActuel, final List<Commande> commandes) {
		this.element.menu.carte.changerCadrage(this.numeroCarte, this.xCarte, this.yCarte, this.icone);
		return curseurActuel + 1;
	}

}
