package commandes;

import java.util.ArrayList;

import main.Commande;
import menu.Texte;

/**
 * Afficher la description de l'Elément sélectionné dans le Menu.
 */
public class AfficherDescription extends Commande implements CommandeMenu {
	private final String description;
	
	/**
	 * Constructeur explicite
	 * @param description de l'Elément sélectionné dans le Menu
	 */
	public AfficherDescription(final String description) {
		this.description = description;
	}
	
	@Override
	public final void executer() {
		this.element.menu.texteDescriptif.contenu = description;
		this.element.menu.texteDescriptif.image = ((Texte) this.element.menu.texteDescriptif).texteToImage();
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.executer();
		return curseurActuel+1;
	}

}
