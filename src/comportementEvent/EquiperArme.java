package comportementEvent;

import java.util.ArrayList;
import java.util.HashMap;

import main.Fenetre;
import main.Partie;

/**
 * Equiper le Heros avec une Arme qu'il possède
 */
public class EquiperArme extends CommandeEvent {
	int idArme;
	
	/**
	 * Constructeur explicite
	 * @param idArme identifiant de l'Arme à équiper
	 */
	public EquiperArme(final int idArme) {
		this.idArme = idArme;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public EquiperArme(final HashMap<String, Object> parametres) {
		this( (Integer) parametres.get("idArme") );
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
		Fenetre.getPartieActuelle().equiperArme(this.idArme);
		return curseurActuel+1;
	}

}
