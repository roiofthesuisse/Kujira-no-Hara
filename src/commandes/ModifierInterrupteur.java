package commandes;

import java.util.ArrayList;

import main.Commande;
import main.Fenetre;
import map.PageEvent;

/**
 * Modifier la valeur d'un interrupteur
 */
public class ModifierInterrupteur implements CommandeEvent {
	private PageEvent page;
	
	int numeroInterrupteur;
	boolean valeurADonner;
	
	/**
	 * Constructeur explicite
	 * @param numero de l'interrupteur à modifier
	 * @param valeur à donner à l'interrupteur
	 */
	public ModifierInterrupteur(final int numero, final boolean valeur) {
		numeroInterrupteur = numero;
		valeurADonner = valeur;
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<? extends Commande> commandes) {
		Fenetre.getPartieActuelle().interrupteurs[numeroInterrupteur] = valeurADonner;
		return curseurActuel+1;
	}

	@Override
	public final PageEvent getPage() {
		return this.page;
	}

	@Override
	public final void setPage(final PageEvent page) {
		this.page = page;
	}

}
