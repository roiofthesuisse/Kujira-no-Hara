package comportementEvent;

import java.util.ArrayList;

public class SupprimerEvent extends CommandeEvent {
	
	public SupprimerEvent(){
		
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		int numeroEventASupprimer = this.page.event.numero;
		this.page.event.pageActive = null;
		this.page.event.map.supprimerEvenement(numeroEventASupprimer);
		return curseurActuel+1;
	}

}
