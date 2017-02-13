package mouvements;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import commandes.Deplacement;
import map.Event;

/**
 * Toute CommandeEvent qui provoque le Mouvement d'un Event doit implémenter cette interface.
 */
public abstract class Mouvement {
	private static final Logger LOG = LogManager.getLogger(Mouvement.class);
	
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
				
				mettreEventDansLaDirectionDuMouvement();
			} else {
				//déclarer le Mouvement comme terminé (car ignoré)
				ignorerLeMouvement(event);
				event.avance = false;
			}
			
		} catch (Exception e) {
			LOG.error("Erreur lors du mouvement de l'évènement :", e);
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
	protected abstract void calculDuMouvement(Event event);
	
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
		final Deplacement deplacementNaturelOuForce;
		if (this.deplacement.naturel) {
			deplacementNaturelOuForce = event.deplacementNaturelActuel;
		} else {
			deplacementNaturelOuForce = event.deplacementForce;
		}
		
		//on regarde si la Page n'a pas changé,
		//car on ne remet pas en bout de file un Mouvement naturel issu d'une autre Page 
		final Integer pageAvant = this.deplacement.page.numero;
		final Integer pageMaintenant;
		if (event.pageActive != null) {
			pageMaintenant = event.pageActive.numero;
		} else if (event.pageDApparence != null) {
			pageMaintenant = event.pageDApparence.numero;
		} else {
			pageMaintenant = null;
		}
		final boolean laPageEstToujoursLaMeme = pageAvant.equals(pageMaintenant);
		
		
		//si le Déplacement est perpétuel, on remet ce Mouvement en fin de liste
		if (this.deplacement.repeterLeDeplacement) {
			if (!this.deplacement.naturel //un Mouvement forcé perpétuel ne s'arrête pas même si la Page de l'Event change
			|| laPageEstToujoursLaMeme) { //un Mouvement naturel perpétuel s'arrête si la Page change
				deplacementNaturelOuForce.mouvements.add(this);
			} else {
				LOG.warn("On ne remet pas en bout de file le Mouvement "+this.getClass().getName()
						+" car la Page de l'Event "+this.deplacement.page.event.numero+" ("
						+this.deplacement.page.event.nom+") a changé : de "+pageAvant+" vers "+pageMaintenant);
			}

		}
		
		//on retire ce Mouvement de la liste
		if (laPageEstToujoursLaMeme 
				&& deplacementNaturelOuForce.mouvements.size() >= 1
				&& deplacementNaturelOuForce.mouvements.get(0).equals(this)) {
			deplacementNaturelOuForce.mouvements.remove(0);
		} else {
			//cas théoriquement impossible
			LOG.error("Impossible de retirer le premier Mouvement " + this.toString() 
			+ " du Déplacement " + (this.deplacement.naturel ? "naturel" : "forcé")
			+ " de l'Event " + event.numero + " (" + event.nom + ")");
		}
	}
	
	/**
	 * Actions à effectuer lors de la fin du Mouvement.
	 * Spécifique à un type de Mouvement.
	 * @param event subissant le Mouvement
	 */
	protected abstract void terminerLeMouvementSpecifique(Event event);
	
	/** 
	 * Déclarer le Mouvement comme terminé car ignoré.
	 * @param event subissant le Mouvement
	 */
	private void ignorerLeMouvement(final Event event) {
		//interruption spécifique à ce type de Mouvement en particulier
		ignorerLeMouvementSpecifique(event);
		
		//interruption commune à tous les Mouvements
		if (this.deplacement.ignorerLesMouvementsImpossibles) {
			//on ignore ce Mouvement impossible et on passe au suivant
			terminerLeMouvement(event);
		}
	}
	
	/**
	 * Actions à effectuer lors de l'ignorage du Mouvement.
	 * Spécifique à un type de Mouvement.
	 * @param event subissant le Mouvement
	 */
	protected abstract void ignorerLeMouvementSpecifique(Event event);
	
	/**
	 * Décrire le Mouvement textuellement
	 * @return description du Mouvement
	 */
	public abstract String toString();
	
	/**
	 * Pendant le Mouvement, l'Event est suceptible de changer de direction.
	 * Il faut tourner l'Event dans la direction dictée par le Mouvement.
	 */
	public final void mettreEventDansLaDirectionDuMouvement() {
		final Event event = this.deplacement.getEventADeplacer();
		if (!event.directionFixeActuelle) {
			final int directionImposee = this.getDirectionImposee();
			if (directionImposee != -1) {
				event.direction = directionImposee;
			}
		}
	}
	
	/**
	 * Quelle est la direction imposée par le Mouvement à l'Event ?
	 * @return direction imposée par le Mouvement à l'Event ou -1 si aucune.
	 */
	public abstract int getDirectionImposee();
}
