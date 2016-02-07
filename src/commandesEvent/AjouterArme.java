package commandesEvent;

import java.util.ArrayList;
import java.util.HashMap;

import jeu.Partie;
import main.Fenetre;

/**
 * Ajouter une nouvelle Arme au Heros
 */
public class AjouterArme extends CommandeEvent {
	int idArme;
	
	/**
	 * Constructeur explicite
	 * @param idArme identifiant de l'Arme à ajouter
	 */
	public AjouterArme(final int idArme) {
		this.idArme = idArme;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AjouterArme(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("idArme") );
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		if (!partieActuelle.armesPossedees[idArme]) {
			partieActuelle.armesPossedees[idArme] = true;
			partieActuelle.nombreDArmesPossedees++;
		}
		return curseurActuel+1;
	}

}
