package commandes;

import java.util.ArrayList;

import main.Commande;
import main.Fenetre;
import map.Deplacement;
import map.Event;
import map.LecteurMap;

/**
 * Toute CommandeEvent qui provoque le Mouvement d'un Event doit implémenter cette interface.
 */
public abstract class Mouvement extends Commande implements CommandeEvent {
	/** id de l'Event qui va être déplacé durant ce Mouvement */
	protected Integer idEventADeplacer; //Integer car clé d'une HashMap, et null lorsque "cet Event"
	/** Le Mouvement est-il commencé ? */
	protected boolean commence;
	/** Le Mouvement est-il terminé ? */
	protected boolean termine;
	/** Nombre d'étapes du Mouvement qui ont été faites */
	protected int ceQuiAEteFait;
	/** Nombre d'étapes à faire */
	protected int etapes;
	
	/**
	 * Si la Page de comportement doit être rejouée, il faut réinitialiser cette Commande.
	 * Réinitialiser un mouvement le déclare non fait, et change la direction en cas de mouvement aléatoire.
	 */
	public final void reinitialiser() {
		this.commence = false;
		this.termine = false;
		this.ceQuiAEteFait = 0;
		reinitialiserSpecifique();
	}
	
	/**
	 * Actions à effectuer pour pouvoir éventuellement rejouer le Mouvement encore.
	 * Spécifique à un type de Mouvement.
	 */
	protected abstract void reinitialiserSpecifique();
	
	/**
	 * Ajouter ce Mouvement à la liste des Mouvements forcés pour cet Event.
	 */
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		if (!this.commence) {
			//le Mouvement n'a pas encore été ajouté à la liste des Mouvements forcés
			final Event event = this.getEventADeplacer();
			event.deplacementForce.mouvements.add(this);
		}
		
		if (this.termine) {
			//le Mouvement est terminé, on passe à la CommandeEvent suivante
			this.reinitialiser();
			return curseurActuel+1;
		} else {
			//le Mouvement n'est pas terminé, on attend avant de passer à la CommandeEvent suivante
			return curseurActuel;
		}
	}
	
	/**
	 * Procéder aux modifications de données permettant au LecteurMap d'afficher l'Event au bon endroit.
	 * @param deplacement dont fait partie ce mouvement
	 */
	public final void executerLeMouvement(final Deplacement deplacement) {
		try {
			final Event event = this.getEventADeplacer();
			if ( this.mouvementPossible() ) {
				//appliquer l'effet du Mouvement sur la Map et les Events
				calculDuMouvement(event);
				
				//quelle sera la commande suivante ?
				if ( this.ceQuiAEteFait >= this.etapes ) {
					//déclarer le Mouvement comme terminé (car il est réellement terminé)
					terminerLeMouvementSpecifique(event, deplacement); //dépend du type de Mouvement
					terminerLeMouvement(deplacement); //factorisé dans la classe mère
				}
			} else {
				//déclarer le Mouvement comme terminé (car ignoré)
				ignorerLeMouvementSpecifique(event, deplacement); //dépend du type de Mouvement
				ignorerLeMouvement(event, deplacement); //factorisé dans la classe mère
			}
		} catch (Exception e) {
			System.out.println("Erreur lors du mouvement de l'évènement :");
			e.printStackTrace();
		}
	}
	
	/**
	 * Le Mouvement est-il possible sur cette Map ?
	 * @return true si le Mouvement est possible
	 */
	public abstract boolean mouvementPossible();
	
	/**
	 * Tout Mouvement déplace un Event de la Map en particulier.
	 * @return Event qui va être déplacé
	 */
	public final Event getEventADeplacer() {
		if (this.idEventADeplacer !=null) {
			//un numéro d'Event à déplacer a été spécifié dans le JSON
			return ((LecteurMap) Fenetre.getFenetre().lecteur).map.eventsHash.get((Integer) this.idEventADeplacer);
		} else {
			//aucun numéro n'a été spéifié, on déplace l'Event qui a lancé la Commande
			return this.page.event;
		}
		
	}
	
	/** 
	 * Appliquer l'effet du Mouvement sur la Map et les Events.
	 * @param event subissant le Mouvement
	 */
	protected abstract void calculDuMouvement(final Event event);
	
	/** 
	 * Déclarer le Mouvement comme terminé.
	 * @param deplacement dont fait partie ce mouvement
	 */
	private void terminerLeMouvement(final Deplacement deplacement) {
		System.out.println("terminerLeMouvement "+this.getClass().getName());
		//le Mouvement est terminé
		this.termine = true;
		
		//on retire ce Mouvement de la liste
		deplacement.mouvements.remove(0);
		
		//si le Déplacement est perpétuel, on remet ce Mouvement en fin de liste
		if (deplacement.repeterLeDeplacement) {
			//on le réinitialise et on le met en bout de file
			this.reinitialiser();
			deplacement.mouvements.add(this);
		}
	}
	
	/**
	 * Actions à effectuer lors de la fin du Mouvement.
	 * Spécifique à un type de Mouvement.
	 * @param event subissant le Mouvement
	 * @param deplacement dont fait partie le Mouvement
	 */
	protected abstract void terminerLeMouvementSpecifique(final Event event, final Deplacement deplacement);
	
	/** 
	 * Déclarer le Mouvement comme terminé car ignoré.
	 * @param event subissant le Mouvement
	 * @param deplacement dont fait partie ce mouvement
	 */
	private void ignorerLeMouvement(final Event event, final Deplacement deplacement) {
		event.avance = false;
		event.saute = false;
		if (deplacement.ignorerLesMouvementsImpossibles) {
			//on ignore ce Mouvement impossible et on passe au suivant
			this.termine = true;
			if (deplacement.repeterLeDeplacement) {
				//on le réinitialise et on le met en bout de file
				this.reinitialiser();
				deplacement.mouvements.add(this);
			}
			//on passe au mouvement suivant
			deplacement.mouvements.remove(0);
		}
	}
	
	/**
	 * Actions à effectuer lors de l'ignorage du Mouvement.
	 * Spécifique à un type de Mouvement.
	 * @param event subissant le Mouvement
	 * @param deplacement dont fait partie le Mouvement
	 */
	protected abstract void ignorerLeMouvementSpecifique(final Event event, final Deplacement deplacement);
	
}
