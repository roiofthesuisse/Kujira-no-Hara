package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import jeu.Arme;
import jeu.Partie;
import main.Commande;
import main.Fenetre;
import map.PageEvent;

/**
 * Ajouter une nouvelle Arme au Heros
 */
public class AjouterArme implements CommandeEvent {
	private PageEvent page;
	int idArme;
	
	/**
	 * Constructeur explicite
	 * @param arme identifiant de l'Arme à ajouter : son numéro ou son nom
	 */
	public AjouterArme(final Object arme) {
		try {
			//l'identifiant de l'Arme est son numéro
			this.idArme = (Integer) arme;
		} catch (Exception e) {
			//l'identifiant de l'Arme est son numéro
			this.idArme = Arme.armesDuJeuHash.get((String) arme).id;
		}
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AjouterArme(final HashMap<String, Object> parametres) {
		this( (Object) (parametres.containsKey("idArme") ? parametres.get("idArme") : parametres.get("nomArme")) );
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<? extends Commande> commandes) {
		final Partie partieActuelle = Fenetre.getPartieActuelle();
		if (!partieActuelle.armesPossedees[idArme]) {
			partieActuelle.armesPossedees[idArme] = true;
			partieActuelle.nombreDArmesPossedees++;
		}
		return curseurActuel+1;
	}

	@Override
	public final PageEvent getPage() {
		return this.page;
	}

	@Override
	public final void setPage(final PageEvent page) {
		this.page = page;
	}

}
