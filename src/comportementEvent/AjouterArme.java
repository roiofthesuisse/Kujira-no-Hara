package comportementEvent;

import java.util.ArrayList;

import main.Partie;
import utilitaire.Parametre;

public class AjouterArme extends CommandeEvent {
	int idArme;
	
	/**
	 * Constructeur spécifique
	 */
	public AjouterArme(int idArme){
		this.idArme = idArme;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AjouterArme(ArrayList<Parametre> parametres){
		this( (Integer) trouverParametre("idArme",parametres) );
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		if(!Partie.idArmesPossedees.contains(idArme)){
			Partie.idArmesPossedees.add(idArme);
		}
		return curseurActuel+1;
	}

}
