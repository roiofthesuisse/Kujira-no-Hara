package mouvements;

import commandes.Deplacement;
import map.Event;

/**
 * Toute CommandeEvent qui provoque le Mouvement d'un Event doit implémenter cette interface.
 */
public abstract class Mouvement {
	/** Nombre d'étapes du Mouvement qui ont été faites */
	protected int ceQuiAEteFait;
	/** Nombre d'étapes à faire */
	protected int etapes;
	/** Déplacement dont fait partie ce Mouvement */
	public Deplacement deplacement;

	/**
	 * Si la Page de comportement doit être rejouée, il faut réinitialiser cette Commande.
	 * Réinitialiser un mouvement le déclare non fait, et change la direction en cas de mouvement aléatoire.
	 */
	public final void reinitialiser() {
		//réinitialisation spécifique à ce type de Mouvement en particulier
		this.reinitialiserSpecifique();
		
		//rénitialisation commune à tous les Mouvements
		this.ceQuiAEteFait = 0;
	}
	
	/**
	 * Actions à effectuer pour pouvoir éventuellement rejouer le Mouvement encore.
	 * Spécifique à un type de Mouvement.
	 */
	protected abstract void reinitialiserSpecifique();
	
	/**
	 * Procéder aux modifications de données permettant au LecteurMap d'afficher l'Event au bon endroit.
	 * Méthode appelée lors de l'exécution des Déplacements.
	 * @param deplacement (naturel ou forcé d'un Event) dont fait partie ce Mouvement
	 */
	public final void executerLeMouvement(final Deplacement deplacement) {
		try {
			final Event event = this.deplacement.getEventADeplacer();

			if ( this.mouvementPossible() ) {
				//appliquer l'effet du Mouvement sur la Map et les Events
				calculDuMouvement(event);

				//quelle sera la commande suivante ?
				if ( this.ceQuiAEteFait >= this.etapes ) {
					//déclarer le Mouvement comme terminé (car il est réellement terminé)
					terminerLeMouvement(event);
				}
			} else {
				//déclarer le Mouvement comme terminé (car ignoré)
				ignorerLeMouvement(event);
			}
		} catch (Exception e) {
			System.err.println("Erreur lors du mouvement de l'évènement :");
			e.printStackTrace();
		}
	}
	
	/**
	 * Le Mouvement est-il possible sur cette Map ?
	 * @return true si le Mouvement est possible
	 */
	public abstract boolean mouvementPossible();
	
	
	
	/** 
	 * Applique l'effet du Mouvement sur la Map et les Events.
	 * Puis incrémente le compteur "ceQuiAEteFait".
	 * @param event subissant le Mouvement
	 */
	protected abstract void calculDuMouvement(final Event event);
	
	/** 
	 * Déclarer le Mouvement comme terminé.
	 * @param event subissant le Mouvement
	 */
	private void terminerLeMouvement(final Event event) {
		//finalisation spécifique à ce type de Mouvement en particulier
		terminerLeMouvementSpecifique(event); //dépend du type de Mouvement
		
		//finalisation commune à tous les Mouvements
		this.reinitialiser();
		
		//est-on dans un Déplacement naturel ou forcé ?
		Deplacement deplacementNaturelOuForce;
		if (this.deplacement.naturel) {
			deplacementNaturelOuForce = event.deplacementNaturelActuel;
		} else {
			deplacementNaturelOuForce = event.deplacementForce;
		}
		
		//si le Déplacement est perpétuel, on remet ce Mouvement en fin de liste
		if (this.deplacement.repeterLeDeplacement) {
			deplacementNaturelOuForce.mouvements.add(this);
		}
		
		//on retire ce Mouvement de la liste
		if (deplacementNaturelOuForce.mouvements.size() >= 1) {
			deplacementNaturelOuForce.mouvements.remove(0);
		} else {
			//cas théoriquement impossible
			System.err.println("Impossible de retirer le premier Mouvement " + this.toString() 
			+ " du Déplacement " + (this.deplacement.naturel ? "naturel" : "forcé")
			+ " de l'Event " + event.numero + " (" + event.nom + ")");
		}
	}
	
	/**
	 * Actions à effectuer lors de la fin du Mouvement.
	 * Spécifique à un type de Mouvement.
	 * @param event subissant le Mouvement
	 */
	protected abstract void terminerLeMouvementSpecifique(final Event event);
	
	/** 
	 * Déclarer le Mouvement comme terminé car ignoré.
	 * @param event subissant le Mouvement
	 */
	private void ignorerLeMouvement(final Event event) {
		//interruption spécifique à ce type de Mouvement en particulier
		ignorerLeMouvementSpecifique(event);
		
		//interruption commune à tous les Mouvements
		if (this.deplacement.ignorerLesMouvementsImpossibles) {
			//on ignore ce Mouvement impossible et on passe au suivantk
			terminerLeMouvement(event);
		}
	}
	
	/**
	 * Actions à effectuer lors de l'ignorage du Mouvement.
	 * Spécifique à un type de Mouvement.
	 * @param event subissant le Mouvement
	 */
	protected abstract void ignorerLeMouvementSpecifique(final Event event);
	
	/**
	 * Décrire le Mouvement textuellement
	 * @return description du Mouvement
	 */
	public abstract String toString();
}
