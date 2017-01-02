package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import map.PageCommune;

public class AppelerPageCommune extends Commande implements CommandeEvent, CommandeMenu {

	private int curseurInterne;
	private PageCommune pageCommune;
	
	/**
	 * Constructeur explicite
	 */
	public AppelerPageCommune(int numeroPageCommune) {
		if (this.element != null) {
			// Menu
			this.pageCommune = this.element.menu.lecteur.pagesCommunes.get(numeroPageCommune);
		} else {
			// Map
			this.pageCommune = this.page.event.map.lecteur.pagesCommunes.get(numeroPageCommune);
		}
		this.curseurInterne = 0;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AppelerPageCommune(final HashMap<String, Object> parametres) {
		this(
				(int) parametres.get("numeroPageCommune")
		);
	}
	
	@Override
	public void executer() {
		ArrayList<Commande> commandes = this.pageCommune.commandes;
		int nouveauCurseurInterne;
		boolean commandeInstantanee = true;
		try {
			while (commandeInstantanee) {
				nouveauCurseurInterne = this.pageCommune.commandes.get(curseurInterne).executer(curseurInterne, commandes);
				commandeInstantanee = (nouveauCurseurInterne != curseurInterne);
				curseurInterne = nouveauCurseurInterne;
			}
		} catch (Exception e) {
			// la Page Commune a été lue en entier
			e.printStackTrace();
		}
	}

	@Override
	public int executer(int curseurActuel, ArrayList<Commande> commandes) {
		executer();
		
		if (this.curseurInterne >= this.pageCommune.commandes.size()) {
			//fini
			curseurInterne = 0;
			return curseurActuel+1;
		} else {
			//pas fini
			return curseurActuel;
		}
	}

}
