package mouvements;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import commandes.CommandeEvent;
import main.Commande;
import map.Event;
import map.PageEvent;

/**
 * Executer une Commande comme si c'�tait un Mouvement.
 * Attention : �a ne marche qu'avec des Commandes tres simples.
 */
public class AppelerUneCommande extends Mouvement implements CommandeEvent {
	final Commande commande;
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 * @throws ClassNotFoundException cette Commande n'existe pas
	 * @throws NoSuchMethodException cette Commande n'a pas de constructeur generique
	 * @throws SecurityException l'ins�curit� r�gne
	 * @throws InstantiationException parametres manquants lors de l'instanciation de la Commande
	 * @throws IllegalAccessException acc�s interdit
	 * @throws IllegalArgumentException mauvais arguments
	 * @throws InvocationTargetException impossible d'invoquer cette Commande
	 */
	public AppelerUneCommande(final HashMap<String, Object> parametres) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final String nomClasseCommande = (String) parametres.get("nomCommande");
		final Class<?> classeCommande = Class.forName("commandes." + nomClasseCommande);
		final Constructor<?> constructeurCommande = classeCommande.getConstructor(parametres.getClass());
		// On remplace le nom du Mouvement par le nom de la Commande
		parametres.put("nom", nomClasseCommande);
		this.commande = (Commande) constructeurCommande.newInstance(parametres);
	}

	@Override
	protected void reinitialiserSpecifique() {
		// rien
	}

	@Override
	public boolean mouvementPossible() {
		return true;
	}

	@Override
	protected void calculDuMouvement(Event event) {
		final PageEvent page = this.deplacement.page;
		commande.executer(page.curseurCommandes, page.commandes);
	}

	@Override
	protected void terminerLeMouvementSpecifique(Event event) {
		// rien
	}

	@Override
	protected void ignorerLeMouvementSpecifique(Event event) {
		// rien
	}

	@Override
	public String toString() {
		return "Appeler la commande "+this.commande.toString();
	}

	@Override
	public int getDirectionImposee() {
		return -1;
	}

}
