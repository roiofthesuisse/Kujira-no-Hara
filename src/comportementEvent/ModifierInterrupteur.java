package comportementEvent;

import java.util.ArrayList;

import main.Fenetre;

/**
 * Modifier la valeur d'un interrupteur
 */
public class ModifierInterrupteur extends CommandeEvent {
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
	public final int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
		ArrayList<Integer> interrupteurs = Fenetre.getPartieActuelle().interrupteurs;
		if (valeurADonner) {
			if (!interrupteurs.contains(numeroInterrupteur)) {
				interrupteurs.add(numeroInterrupteur);
			}
		} else {
			if (interrupteurs.contains(numeroInterrupteur)) {
				interrupteurs.remove(new Integer(numeroInterrupteur)); //ici c'est important de mettre un Integer au lieu d'un int
			}
		}
		return curseurActuel+1;
	}

}
