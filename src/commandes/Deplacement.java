package commandes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import main.Commande;
import main.Main;
import map.Event;
import map.Heros;
import map.LecteurMap;
import mouvements.Mouvement;

/**
 * <p>
 * Un Deplacement est un ensemble de Mouvements subis par un Event.
 * </p>
 * 
 * <p>
 * L'objet Deplacement a deux usages tr�s differents :
 * <ol>
 * <li>Chaque Event poss�de un Deplacement naturel et un Deplacement forc�. Le
 * Deplacement forc� sera effectu� en priorit� (sauf s'il est vide) sur le
 * Deplacement naturel. Cela a lieu lorsque les Events sont d�plac�s avec la
 * Methode deplacer().</li>
 * <li>Un Deplacement est �galement une CommandeEvent qui permet d'ajouter des
 * Mouvements dans le Deplacement forc� d'un Event. L'Event qui ordonne le
 * Deplacement n'est pas forc�ment celui qui le subit. Ce Deplacement, qui
 * contient des ordres, n'est jamais effectu�. C'est le Deplacement forc� de
 * l'Event-cible qui sera effectu� une fois rempli. Ce remplissage a lieu lors
 * de l'execution des Commandes Event avec la Methode executer().</li>
 * </ol>
 * </p>
 */
public class Deplacement extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(Deplacement.class);

	/** id de l'Event qui va etre d�plac� */
	public Integer idEventADeplacer; // Integer car cl� d'une HashMap, et null lorsque "cet Event"
	/** id de l'Event qui a r�clam� ce Deplacement */
	public Integer idEventCommanditaire;
	/** Mouvements constitutifs de ce Deplacement */
	public ArrayList<Mouvement> mouvements;
	/**
	 * faut-il interrompre les Mouvements impossibles, ou attendre qu'ils soient
	 * possibles ?
	 */
	public boolean ignorerLesMouvementsImpossibles = false;
	/** faut-il rejouer le Deplacement lorsqu'on l'a termine ? */
	public boolean repeterLeDeplacement = true;
	/**
	 * faut-il attendre la fin du Deplacement pour passer a la Commande suivante ?
	 */
	public boolean attendreLaFinDuDeplacement = false;
	private boolean aEteAjouteAuxDeplacementsForces = false;
	/**
	 * le Mouvement est-il naturel ? (cette valeur est r�assign�e lors de
	 * l'instanciation de la Page)
	 */
	public boolean naturel = false;

	/**
	 * Constructeur explicite
	 * 
	 * @param idEventADeplacer                id de l'Event a d�placer, null
	 *                                        signifie "cet Event", 0 le Heros
	 * @param mouvements                      liste des Mouvements constitutifs du
	 *                                        Deplacement
	 * @param ignorerLesMouvementsImpossibles faut-il interrompre les Mouvements
	 *                                        impossibles, ou attendre qu'ils soient
	 *                                        possibles ?
	 * @param repeterLeDeplacement            faut-il rejouer le Deplacement
	 *                                        lorsqu'on l'a termine ?
	 * @param attendreLaFinDuDeplacement      faut-il attendre la fin du Deplacement
	 *                                        pour passer a la Commande suivante ?
	 */
	public Deplacement(final Integer idEventADeplacer, final ArrayList<Mouvement> mouvements,
			final boolean ignorerLesMouvementsImpossibles, final boolean repeterLeDeplacement,
			final boolean attendreLaFinDuDeplacement) {
		this.idEventADeplacer = idEventADeplacer;
		this.mouvements = mouvements;
		this.ignorerLesMouvementsImpossibles = ignorerLesMouvementsImpossibles;
		this.repeterLeDeplacement = repeterLeDeplacement;
		this.attendreLaFinDuDeplacement = attendreLaFinDuDeplacement;

		// on apprend aux Mouvements le Deplacement dont ils font partie
		for (Mouvement mouvement : this.mouvements) {
			mouvement.deplacement = this;
		}
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public Deplacement(final HashMap<String, Object> parametres) {
		this(parametres.containsKey("idEventADeplacer") ? (Integer) parametres.get("idEventADeplacer") : null,
				Mouvement.recupererLesMouvements((JSONArray) parametres.get("mouvements")),
				parametres.containsKey("ignorerLesMouvementsImpossibles")
						? (boolean) parametres.get("ignorerLesMouvementsImpossibles")
						: Event.IGNORER_LES_MOUVEMENTS_IMPOSSIBLES_PAR_DEFAUT,
				parametres.containsKey("repeterLeDeplacement") ? (boolean) parametres.get("repeterLeDeplacement")
						: Event.REPETER_LE_DEPLACEMENT_PAR_DEFAUT,
				parametres.containsKey("attendreLaFinDuDeplacement")
						? (boolean) parametres.get("attendreLaFinDuDeplacement")
						: Event.ATTENDRE_LA_FIN_DU_DEPLACEMENT_PAR_DEFAUT);
	}

	/**
	 * Vide la liste des Mouvements forc�s de l'Event, puis ajoute les nouveaux
	 * Mouvements. Methode appel�e lors de l'execution des Commandes. On passe a la
	 * Commande suivante selon s'il faut attendre la fin du Deplacement.
	 */
	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		// IMPORTANT
		// Nous nous trouvons actuellement dans le Deplacement qui contient les
		// Mouvements a ajouter au Deplacement forc� d'un Event.

		final Event event = this.getEventADeplacer();
		if (event == null) {
			// Event introuvable
			LOG.error("Deplacement impossible : l'Event " + this.idEventADeplacer + " n'existe pas !");
			// on r�initialise le Deplacement (au cas Ou il est a nouveau execut� dans le
			// futur)
			this.aEteAjouteAuxDeplacementsForces = false;
			// on passe a la Commande suivante
			return curseurActuel + 1;
		}

		if (!this.aEteAjouteAuxDeplacementsForces) {
			// interrompre l'ancien Deplacement forc� de l'Event
			if (event.deplacementForce.mouvements != null && event.deplacementForce.mouvements.size() >= 1) {
				LOG.warn("Le deplacement de l'event " + this.idEventADeplacer + " a ete interrompu et remplac�.");
			}
			event.deplacementForce.mouvements = new ArrayList<Mouvement>();

			// a la place, on ajoute dans la liste les nouveaux Mouvements forc�s
			for (Mouvement mvt : this.mouvements) {
				mvt.reinitialiser();
				event.deplacementForce.mouvements.add(mvt);
				event.deplacementForce.page = this.page; // on indique le commanditaire de ce Deplacement
			}
			// on pr�cise le commanditaire qui a impos� ce Deplacement a un autre event
			event.deplacementForce.idEventCommanditaire = this.page.event.id;
			// les nouvelles caract�ristiques de Deplacement sont assign�es au Deplacement
			// forc�
			event.deplacementForce.attendreLaFinDuDeplacement = this.attendreLaFinDuDeplacement;
			event.deplacementForce.ignorerLesMouvementsImpossibles = this.ignorerLesMouvementsImpossibles;
			event.deplacementForce.repeterLeDeplacement = this.repeterLeDeplacement;
			// voil�, les nouveaux Mouvements ont ete planifi�s

			this.aEteAjouteAuxDeplacementsForces = true;
		}

		// quand est-ce qu'on passe a la Commande suivante ?
		if (!this.attendreLaFinDuDeplacement) {
			// on ne se soucie pas du d�roulement du Deplacement

			// on r�initialise le Deplacement (au cas Ou il est a nouveau execut� dans le
			// futur)
			this.aEteAjouteAuxDeplacementsForces = false;
			// on passe imm�diatement a la Commande suivante
			return curseurActuel + 1;
		} else {
			// on attend la fin du Deplacement avant de passer a la Commande suivante
			if (event.deplacementForce.mouvements.size() <= 0) {
				// la liste a ete totalement consomm�e

				// on r�initialise le Deplacement (au cas Ou il est a nouveau execut� dans le
				// futur)
				this.aEteAjouteAuxDeplacementsForces = false;
				// on passe a la Commande suivante
				return curseurActuel + 1;
			} else {
				// la liste contient encore des Mouvements a effectuer

				// on reste ici
				return curseurActuel;
			}
		}
	}

	/**
	 * Tout Mouvement d�place un Event de la Map en particulier.
	 * 
	 * @return Event qui va etre d�plac�
	 */
	public final Event getEventADeplacer() {
		if (this.idEventADeplacer != null) {
			// un Numero d'Event a d�placer a ete sp�cifi� dans le JSON
			return ((LecteurMap) Main.lecteur).map.eventsHash.get((Integer) this.idEventADeplacer);
		} else {
			// aucun Numero n'a ete sp�cifi�, on d�place l'Event qui a lanc� la Commande
			return this.page.event;
		}
	}

	/**
	 * Executer le premier Mouvement du Deplacement. Methode appel�e lorsqu'il faut
	 * d�placer les Events.
	 */
	public final void executerLePremierMouvement() {
		// IMPORTANT
		// Nous nous trouvons actuellement dans le Deplacement forc� ou naturel d'un
		// Event.

		final Mouvement premierMouvement = this.mouvements.get(0);

		final LecteurMap lecteurMap = (LecteurMap) Main.lecteur;
		// si le stopEvent est activ�, on n'effectue pas les Mouvements
		// sauf s'il s'agit d'Attendre
		final boolean lesMouvementsSontBloques = lecteurMap.stopEvent
				|| (lecteurMap.stopHeros && getEventADeplacer() instanceof Heros);
		if (lesMouvementsSontBloques) {
			// les Mouvements sont bloqu�s

			final int idBloqueur = lecteurMap.eventQuiALanceStopEvent.id;
			final int idCommanditaireDeCeDeplacement = this.page.event.id;
			if (!this.naturel && idBloqueur == idCommanditaireDeCeDeplacement) {
				// on effectue toutefois :
				// - les Mouvements commandit�s par le bloqueur
				// - et les Mouvements forc�s
				premierMouvement.executerLeMouvement(this);
			}
		} else {
			// les Mouvements ne sont pas bloqu�s
			premierMouvement.executerLeMouvement(this);
		}
	}

}
