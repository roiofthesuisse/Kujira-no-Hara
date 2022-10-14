package mouvements;

import java.util.HashMap;

import main.Commande;
import map.Event;

/**
 * Interpreter un script ruby
 */
public class AppelerUnScript extends Mouvement {

	private final String script;
	private Commande commande;
	
	/**
	 * Constructeur explicite
	 * @param script texte ruby a interpreter
	 */
	public AppelerUnScript( final String script){
		this.script = script;
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public AppelerUnScript(final HashMap<String, Object> parametres) {
		this((String) parametres.get("script"));
	}
	
	@Override
	protected void reinitialiserSpecifique() {
		// a priori vide
	}

	@Override
	public boolean mouvementPossible() {
		// a priori toujours possible d'interpreter un script
		return true;
	}

	@Override
	protected void calculDuMouvement(Event event) {
		this.etapes = 2;
		if(this.ceQuiAEteFait == 0) {
			// initialiser
			Commande commande = new commandes.AppelerUnScript(this.script);
			commande.page = this.deplacement.page;
			this.ceQuiAEteFait = 1;
		}
		
		int resultat = commande.executer(0, null);
		if(resultat > 0) {
			// Le script a ete execute, fin du mouvement
			this.ceQuiAEteFait = 2;
		}
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
		return "AppelerUnScript "+this.script;
	}

	@Override
	public int getDirectionImposee() {
		// aucune
		return -1;
	}

}
