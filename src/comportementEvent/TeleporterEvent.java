package comportementEvent;

import java.util.ArrayList;
import main.Partie;
import map.Event;

public class TeleporterEvent extends CommandeEvent{
	private int nouveauX;
	private int nouveauY;
	private boolean utiliserVariables; //false:valeurs true:variables 
	
	/**
	 * Constructeur spécifique
	 * @param nouveauX
	 * @param nouveauY
	 * @param utiliserVariables false si valeurs fixes, true si numéros de variables
	 */
	public TeleporterEvent(int nouveauX, int nouveauY, boolean utiliserVariables){
		this.nouveauX = nouveauX;
		this.nouveauY = nouveauY;
		this.utiliserVariables = utiliserVariables;
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		Event cetEvent = commandes.get(0).page.event;
		if(utiliserVariables){
			Partie partie = cetEvent.map.lecteur.fenetre.partie;
			cetEvent.x = partie.variables[nouveauX]*32;
			cetEvent.y = partie.variables[nouveauY]*32;
		}else{
			cetEvent.x = nouveauX*32;
			cetEvent.y = nouveauY*32;
		}
		return curseurActuel+1;
	}

}
