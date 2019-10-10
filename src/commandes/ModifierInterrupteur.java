package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;

/**
 * Modifier la valeur d'un interrupteur
 */
public class ModifierInterrupteur extends Commande implements CommandeEvent {
	int numeroInterrupteur;
	boolean valeurADonner;

	/**
	 * Constructeur explicite
	 * 
	 * @param numero de l'interrupteur à modifier
	 * @param valeur à donner à l'interrupteur
	 */
	public ModifierInterrupteur(final int numero, final boolean valeur) {
		numeroInterrupteur = numero;
		valeurADonner = valeur;
	}

	/**
	 * Constructeur générique
	 * 
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ModifierInterrupteur(final HashMap<String, Object> parametres) {
		this((int) parametres.get("numeroInterrupteur"), (boolean) parametres.get("valeur"));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		getPartieActuelle().interrupteurs[numeroInterrupteur] = valeurADonner;
		return curseurActuel + 1;
	}

}
