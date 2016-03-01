package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import main.Commande;
import main.Fenetre;
import map.Event;
import map.LecteurMap;
import map.PageEvent;
import mouvements.Attendre;
import mouvements.Mouvement;
import utilitaire.InterpreteurDeJson;

/**
 * Un Déplacement est un ensemble de mouvements subis par un Event.
 * Selon son utilisation, le Déplacement pourra être naturel (situé dans la description JSON de l'Event)
 * ou forcé (provoqué par des Commandes Event).
 */
public class Deplacement extends Commande implements CommandeEvent {
	/** id de l'Event qui va être déplacé durant ce Mouvement */
	public Integer idEventADeplacer; //Integer car clé d'une HashMap, et null lorsque "cet Event"
	/** Mouvements constitutifs de ce Déplacement */
	public final ArrayList<Mouvement> mouvements;
	/** faut-il interrompre les Mouvements impossibles, ou attendre qu'ils soient possibles ? */
	public boolean ignorerLesMouvementsImpossibles = false;
	/** faut-il rejouer le Déplacement lorsqu'on l'a terminé ? */
	public boolean repeterLeDeplacement = true;
	/** faut-il attendre la fin du Déplacement pour passer à la Commande suivante ? */
	public boolean attendreLaFinDuDeplacement = false;
	
	/**
	 * Constructeur explicite
	 * @param idEventADeplacer id de l'Event à déplacer, null signifie "cet Event", 0 le Héros
	 * @param mouvements liste des Mouvements constitutifs du Déplacement
	 * @param ignorerLesMouvementsImpossibles faut-il interrompre les Mouvements impossibles, ou attendre qu'ils soient possibles ?
	 * @param repeterLeDeplacement faut-il rejouer le Déplacement lorsqu'on l'a terminé ?
	 * @param attendreLaFinDuDeplacement faut-il attendre la fin du Déplacement pour passer à la Commande suivante ?
	 */
	public Deplacement(final Integer idEventADeplacer, final ArrayList<Mouvement> mouvements, final boolean ignorerLesMouvementsImpossibles, final boolean repeterLeDeplacement, final boolean attendreLaFinDuDeplacement, final PageEvent page) {
		this.idEventADeplacer = idEventADeplacer;
		this.mouvements = mouvements;
		this.ignorerLesMouvementsImpossibles = ignorerLesMouvementsImpossibles;
		this.repeterLeDeplacement = repeterLeDeplacement;
		this.attendreLaFinDuDeplacement = attendreLaFinDuDeplacement;
		this.page = page;
		
		//on apprend aux Mouvements le Déplacement dont ils font partie
		for (Mouvement mouvement : this.mouvements) {
			mouvement.deplacement = this;
		}
	}
	
	//TODO utiliser l'un des autres constructeurs
	/**
	 * Constructeur batard
	 * @param deplacementJSON fichier JSON décrivant le Déplacement
	 * @param page de l'Event qui contient le Mouvement
	 */
	public Deplacement(final JSONObject deplacementJSON, final PageEvent page) {
		this( deplacementJSON.has("idEventADeplacer") ? (Integer) deplacementJSON.get("idEventADeplacer") : null, 
			creerListeDesMouvements(deplacementJSON, page), 
			deplacementJSON.has("ignorerLesMouvementsImpossibles") ? (boolean) deplacementJSON.get("ignorerLesMouvementsImpossibles") : Event.IGNORER_LES_MOUVEMENTS_IMPOSSIBLES_PAR_DEFAUT, 
			deplacementJSON.has("repeterLeDeplacement") ? (boolean) deplacementJSON.get("repeterLeDeplacement") : Event.REPETER_LE_DEPLACEMENT_PAR_DEFAUT,
			deplacementJSON.has("attendreLaFinDuDeplacement") ? (boolean) deplacementJSON.get("attendreLaFinDuDeplacement") : Event.ATTENDRE_LA_FIN_DU_DEPLACEMENT_PAR_DEFAUT,
			page
		);
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
			parametres.containsKey("attendreLaFinDuDeplacement") ? (boolean) parametres.get("attendreLaFinDuDeplacement") : Event.ATTENDRE_LA_FIN_DU_DEPLACEMENT_PAR_DEFAUT,
			parametres.containsKey("page") ? (PageEvent) parametres.get("page") : null
		);
	}
	
	/**
	 * Crééer la liste des Mouvements à partir du fichier JSON du Déplacement
	 * @param deplacementJSON fichier JSON décrivant le Déplacement
	 * @param page de l'Event qui contient le Mouvement
	 * @return liste des Mouvements
	 */
	private static ArrayList<Mouvement> creerListeDesMouvements(final JSONObject deplacementJSON, final PageEvent page) {
		final ArrayList<Mouvement> mvts = new ArrayList<Mouvement>();
		for (Object actionDeplacementJSON : deplacementJSON.getJSONArray("mouvements")) {
			mvts.add( InterpreteurDeJson.recupererUnMouvement((JSONObject) actionDeplacementJSON) );
		}
		return mvts;
	}
	
	/**
	 * Ajouter ce Mouvement à la liste des Mouvements forcés pour cet Event.
	 * Méthode appelée lors de l'exécution des Commandes.
	 */
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		//on ajoute à la liste les Mouvements reçus
		final Event event = this.getEventADeplacer();
		this.page = event.pageActive; //TODO plutôt la page active de l'Event qui appelle la Commande
		for (Mouvement mvt : this.mouvements) {
			mvt.reinitialiser();
			event.deplacementForce.mouvements.add(mvt);
		}

		if (!this.attendreLaFinDuDeplacement) {
			//après la planification, on passe à la Commande suivante
			return curseurActuel+1;
		} else {
			//on attend la fin du Déplacement avant de passer à la Commande suivante
			if (this.mouvements.size() <= 0) {
				//la liste a été vidée, on passe à la Commande suivante
				return curseurActuel+1;
			} else {
				//la liste contient encore des Mouvements à effectuer, on reste ici
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
	 */
	public final void executerLePremierMouvement() {
		final Mouvement premierMouvement = this.mouvements.get(0);
		//si le stopEvent est activé, on n'effectue pas les Mouvements
		//sauf s'il s'agit d'Attendre
		if (!((LecteurMap) Fenetre.getFenetre().lecteur).stopEvent || premierMouvement instanceof Attendre) {
			premierMouvement.executerLeMouvement(this);
		}
	}
	
	
}
