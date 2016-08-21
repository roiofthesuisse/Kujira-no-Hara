package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import main.Commande;
import main.Fenetre;
import map.Event;
import map.LecteurMap;
import mouvements.Attendre;
import mouvements.Mouvement;
import utilitaire.InterpreteurDeJson;

/**
 * <p>Un Déplacement est un ensemble de Mouvements subis par un Event.</p>
 * 
 * <p>L'objet Déplacement a deux usages très différents :
 * <ol>
 * <li> Chaque Event possède un Déplacement naturel et un Déplacement forcé. 
 * Le Déplacement forcé sera effectué en priorité (sauf s'il est vide) sur le Déplacement naturel.
 * Cela a lieu lorsque les Events sont déplacés avec la méthode deplacer().
 * </li>
 * <li>
 * Un Déplacement est également une CommandeEvent qui permet d'ajouter des Mouvements dans le Déplacement forcé d'un Event.
 * L'Event qui ordonne le Déplacement n'est pas forcément celui qui le subit.
 * Ce Déplacement, qui contient des ordres, n'est jamais effectué. 
 * C'est le Déplacement forcé de l'Event-cible qui sera effectué une fois rempli.
 * Ce remplissage a lieu lors de l'execution des Commandes Event avec la méthode executer().
 * </li>
 * </ol>
 * </p>
 */
public class Deplacement extends Commande implements CommandeEvent {
	/** id de l'Event qui va être déplacé durant ce Mouvement */
	public Integer idEventADeplacer; //Integer car clé d'une HashMap, et null lorsque "cet Event"
	/** Mouvements constitutifs de ce Déplacement */
	public ArrayList<Mouvement> mouvements;
	/** faut-il interrompre les Mouvements impossibles, ou attendre qu'ils soient possibles ? */
	public boolean ignorerLesMouvementsImpossibles = false;
	/** faut-il rejouer le Déplacement lorsqu'on l'a terminé ? */
	public boolean repeterLeDeplacement = true;
	/** faut-il attendre la fin du Déplacement pour passer à la Commande suivante ? */
	public boolean attendreLaFinDuDeplacement = false;
	private boolean aEteAjouteAuxDeplacementsForces = false;
	public boolean naturel = false;
	
	/**
	 * Constructeur explicite
	 * @param idEventADeplacer id de l'Event à déplacer, null signifie "cet Event", 0 le Héros
	 * @param mouvements liste des Mouvements constitutifs du Déplacement
	 * @param ignorerLesMouvementsImpossibles faut-il interrompre les Mouvements impossibles, ou attendre qu'ils soient possibles ?
	 * @param repeterLeDeplacement faut-il rejouer le Déplacement lorsqu'on l'a terminé ?
	 * @param attendreLaFinDuDeplacement faut-il attendre la fin du Déplacement pour passer à la Commande suivante ?
	 */
	public Deplacement(final Integer idEventADeplacer, final ArrayList<Mouvement> mouvements, final boolean ignorerLesMouvementsImpossibles, final boolean repeterLeDeplacement, final boolean attendreLaFinDuDeplacement) {
		this.idEventADeplacer = idEventADeplacer;
		this.mouvements = mouvements;
		this.ignorerLesMouvementsImpossibles = ignorerLesMouvementsImpossibles;
		this.repeterLeDeplacement = repeterLeDeplacement;
		this.attendreLaFinDuDeplacement = attendreLaFinDuDeplacement;
		
		//on apprend aux Mouvements le Déplacement dont ils font partie
		for (Mouvement mouvement : this.mouvements) {
			mouvement.deplacement = this;
		}
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Deplacement(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("idEventADeplacer") ? (Integer) parametres.get("idEventADeplacer") : null,
			InterpreteurDeJson.recupererLesMouvements((JSONArray) parametres.get("mouvements")),
			parametres.containsKey("ignorerLesMouvementsImpossibles") ? (boolean) parametres.get("ignorerLesMouvementsImpossibles") : Event.IGNORER_LES_MOUVEMENTS_IMPOSSIBLES_PAR_DEFAUT,
			parametres.containsKey("repeterLeDeplacement") ? (boolean) parametres.get("repeterLeDeplacement") : Event.REPETER_LE_DEPLACEMENT_PAR_DEFAUT,
			parametres.containsKey("attendreLaFinDuDeplacement") ? (boolean) parametres.get("attendreLaFinDuDeplacement") : Event.ATTENDRE_LA_FIN_DU_DEPLACEMENT_PAR_DEFAUT
		);
	}
	
	/**
	 * Vide la liste des Mouvements forcés de l'Event, puis ajoute les nouveaux Mouvements.
	 * Méthode appelée lors de l'exécution des Commandes.
	 * On passe à la Commande suivante selon s'il faut attendre la fin du Déplacement.
	 */
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		// IMPORTANT
		// Nous nous trouvons actuellement dans le Déplacement qui contient les Mouvements à ajouter au Déplacement forcé d'un Event.
		
		final Event event = this.getEventADeplacer();
		
		if (!this.aEteAjouteAuxDeplacementsForces) {
			this.page = commandes.get(curseurActuel).page; //page de l'Event qui a ordonné ce Déplacement
			
			//interrompre l'ancier Déplacement forcé de l'Event
			event.deplacementForce.mouvements = new ArrayList<Mouvement>();
			
			//à la place, on ajoute dans la liste les nouveaux Mouvements forcés
			for (Mouvement mvt : this.mouvements) {
				mvt.reinitialiser();
				event.deplacementForce.mouvements.add(mvt);
			}
			//les nouvelles caractéristiques de Déplacement sont assignées au Déplacement forcé
			event.deplacementForce.attendreLaFinDuDeplacement = this.attendreLaFinDuDeplacement;
			event.deplacementForce.ignorerLesMouvementsImpossibles = this.ignorerLesMouvementsImpossibles;
			event.deplacementForce.repeterLeDeplacement = this.repeterLeDeplacement;
			//voilà, les nouveaux Mouvements ont été planifiés
			
			this.aEteAjouteAuxDeplacementsForces = true;
		}
		
		//quand est-ce qu'on passe à la Commande suivante ?
		if (!this.attendreLaFinDuDeplacement) {
			//on ne se soucie pas du déroulement du Déplacement
			
			//on réinitialise le Deplacement (au cas où il est à nouveau executé dans le futur)
			this.aEteAjouteAuxDeplacementsForces = false;
			//on passe immédiatement à la Commande suivante
			return curseurActuel+1;
		} else {
			//on attend la fin du Déplacement avant de passer à la Commande suivante
			if (event.deplacementForce.mouvements.size() <= 0) { 
				//la liste a été totalement consommée
				
				//on réinitialise le Deplacement (au cas où il est à nouveau executé dans le futur)
				this.aEteAjouteAuxDeplacementsForces = false;
				//on passe à la Commande suivante
				return curseurActuel+1;
			} else {
				//la liste contient encore des Mouvements à effectuer

				//on reste ici
				return curseurActuel;
			}
		}
	}
	
	/**
	 * Tout Mouvement déplace un Event de la Map en particulier.
	 * @return Event qui va être déplacé
	 */
	public final Event getEventADeplacer() {
		if (this.idEventADeplacer !=null) {
			//un numéro d'Event à déplacer a été spécifié dans le JSON
			return ((LecteurMap) Fenetre.getFenetre().lecteur).map.eventsHash.get((Integer) this.idEventADeplacer);
		} else {
			//aucun numéro n'a été spécifié, on déplace l'Event qui a lancé la Commande
			return this.page.event;
		}
	}
	
	/**
	 * Executer le premier Mouvement du Déplacement.
	 * Méthode appelée lorsqu'il faut déplacer les Events.
	 */
	public final void executerLePremierMouvement() {
		// IMPORTANT
		// Nous nous trouvons actuellement dans le Déplacement forcé ou naturel d'un Event.

		final Mouvement premierMouvement = this.mouvements.get(0);
		
		//si le stopEvent est activé, on n'effectue pas les Mouvements
		//sauf s'il s'agit d'Attendre
		if (!((LecteurMap) Fenetre.getFenetre().lecteur).stopEvent || premierMouvement instanceof Attendre) {
			premierMouvement.executerLeMouvement(this);
		}
	}

}
