package commandes;

import java.util.ArrayList;

import jeu.Partie;
import main.Commande;
import main.Fenetre;
import menu.ElementDeMenu;

/**
 * Charger une Partie dans le menu dédié
 */
public class ChargerPartie implements CommandeMenu {
	private ElementDeMenu element;
	private final int numeroDeSauvegarde;
	
	/**
	 * Constructeur explicite
	 * @param numeroDeSauvegarde numéro du fichier de sauvegarde à charger
	 */
	public ChargerPartie(final int numeroDeSauvegarde) {
		this.numeroDeSauvegarde = numeroDeSauvegarde;
	}
	
	@Override
	public final void executer() {
		final Fenetre fenetre = this.element.menu.lecteur.fenetre;
		System.out.println("chargement de la partie numero "+numeroDeSauvegarde);
		fenetre.setPartieActuelle( Partie.chargerPartie(this.numeroDeSauvegarde) );
		fenetre.ouvrirLaPartie();
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
