package comportementEvent;

import java.util.ArrayList;

import main.Partie;

public class AjouterArme extends CommandeEvent {
	int idArme;
	
	public AjouterArme(int idArme){
		this.idArme = idArme;
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		Partie.idArmesPossedees.add(idArme);
		return curseurActuel+1;
	}

}
