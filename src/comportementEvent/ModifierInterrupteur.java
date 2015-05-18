package comportementEvent;

import java.util.ArrayList;

public class ModifierInterrupteur extends CommandeEvent {
	int numeroInterrupteur;
	boolean valeurADonner;
	
	public ModifierInterrupteur(int interrupteur, boolean value){
		numeroInterrupteur = interrupteur;
		valeurADonner = value;
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		this.page.event.map.lecteur.fenetre.partie.interrupteurs[numeroInterrupteur] = valeurADonner;
		return curseurActuel+1;
	}

}
