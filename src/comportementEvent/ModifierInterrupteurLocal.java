package comportementEvent;

import java.util.ArrayList;

import main.Fenetre;

/**
 * Modifier la valeur d'un interrupteur
 */
public class ModifierInterrupteurLocal extends CommandeEvent {
	final boolean valeurADonner;
	final int numeroMap;
	final int numeroEvent;
	final int numeroInterrupteurLocal;
	
	/**
	 * Constructeur explicite
	 * @param numeroMap numéro de la Map où se situe l'interrupteur local à modifier
	 * @param numeroEvent numéro de l'Event auquel appartient l'interrupteur local à modifier
	 * @param numeroInterrupteurLocal 0, 1, 2 ou 3 pour dire A, B, C ou D
	 * @param valeur à donner à l'interrupteur local
	 */
	public ModifierInterrupteurLocal(final int numeroMap, final int numeroEvent, final int numeroInterrupteurLocal, final boolean valeur) {
		this.numeroMap = numeroMap;
		this.numeroEvent = numeroEvent;
		this.numeroInterrupteurLocal = numeroInterrupteurLocal;
		this.valeurADonner = valeur;
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
		String code = "m"+this.numeroMap+"e"+this.numeroEvent+"i"+this.numeroInterrupteurLocal;
		Fenetre.getPartieActuelle().interrupteursLocaux.put(code, valeurADonner);
		return curseurActuel+1;
	}

}
