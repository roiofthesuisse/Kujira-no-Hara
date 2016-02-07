package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import map.PageEvent;

/**
 * Supprimer un Event de la Map
 */
public class SupprimerEvent implements CommandeEvent {
	private PageEvent page;
	
	/**
	 * Constructeur vide
	 */
	public SupprimerEvent() {
		
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public SupprimerEvent(final HashMap<String, Object> parametres) {
		this();
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<? extends Commande> commandes) {
		final int numeroEventASupprimer = this.page.event.numero;
		this.page.event.pageActive = null;
		this.page.event.map.supprimerEvenement(numeroEventASupprimer);
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
