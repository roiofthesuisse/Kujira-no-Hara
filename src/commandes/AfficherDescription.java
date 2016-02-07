package commandes;

import java.util.ArrayList;

import main.Commande;
import main.Fenetre;
import menu.ElementDeMenu;
import menu.LecteurMenu;
import menu.Texte;

/**
 * Afficher la description de l'Elément sélectionné dans le Menu.
 */
public class AfficherDescription implements CommandeMenu {
	private final String description;
	private ElementDeMenu element;
	
	/**
	 * Constructeur explicite
	 * @param description de l'Elément sélectionné dans le Menu
	 */
	public AfficherDescription(final String description) {
		this.description = description;
	}
	
	@Override
	public final void executer() {
		((LecteurMenu) Fenetre.getFenetre().lecteur).menu.texteDescriptif = new Texte(description);
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
