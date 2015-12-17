package comportementEvent;

import java.util.ArrayList;

import main.Partie;

public class EquiperArme extends CommandeEvent {
	int idArme;
	
	public EquiperArme(int idArme){
		this.idArme = idArme;
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		Partie.equiperArme(this.idArme);
		return curseurActuel+1;
	}

}
