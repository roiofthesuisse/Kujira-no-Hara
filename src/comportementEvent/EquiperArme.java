package comportementEvent;

import java.util.ArrayList;

import main.Partie;
import utilitaire.Parametre;

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
	public EquiperArme(ArrayList<Parametre> parametres){
		this( (Integer) trouverParametre("idArme",parametres) );
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		Partie.equiperArme(this.idArme);
		return curseurActuel+1;
	}

}
