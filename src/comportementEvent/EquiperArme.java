package comportementEvent;

import java.util.ArrayList;
import java.util.HashMap;

import main.Partie;

public class EquiperArme extends CommandeEvent {
	int idArme;
	
	/**
	 * Constructeur spécifique
	 */
	public EquiperArme(int idArme){
		this.idArme = idArme;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public EquiperArme(HashMap<String,Object> parametres){
		this( (Integer) parametres.get("idArme") );
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		Partie.equiperArme(this.idArme);
		return curseurActuel+1;
	}

}
