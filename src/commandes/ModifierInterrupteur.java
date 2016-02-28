package commandes;

import java.util.ArrayList;

import main.Commande;
import main.Fenetre;

/**
 * Modifier la valeur d'un interrupteur
 */
public class ModifierInterrupteur extends Commande implements CommandeEvent {
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
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		Fenetre.getPartieActuelle().interrupteurs[numeroInterrupteur] = valeurADonner;
		return curseurActuel+1;
	}

}
