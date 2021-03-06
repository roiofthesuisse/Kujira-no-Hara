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
 * Un D�placement est un ensemble de Mouvements subis par un Event.
 * </p>
 * 
 * <p>
 * L'objet D�placement a deux usages tr�s diff�rents :
 * <ol>
 * <li>Chaque Event poss�de un D�placement naturel et un D�placement forc�. Le
 * D�placement forc� sera effectu� en priorit� (sauf s'il est vide) sur le
 * D�placement naturel. Cela a lieu lorsque les Events sont d�plac�s avec la
 * m�thode deplacer().</li>
 * <li>Un D�placement est �galement une CommandeEvent qui permet d'ajouter des
 * Mouvements dans le D�placement forc� d'un Event. L'Event qui ordonne le
 * D�placement n'est pas forc�ment celui qui le subit. Ce D�placement, qui
 * contient des ordres, n'est jamais effectu�. C'est le D�placement forc� de
 * l'Event-cible qui sera effectu� une fois rempli. Ce remplissage a lieu lors
 * de l'execution des Commandes Event avec la m�thode executer().</li>
 * </ol>
 * </p>
 */
public class Deplacement extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(Deplacement.class);

	/** id de l'Event qui va �tre d�plac� */
	public Integer idEventADeplacer; // Integer car cl� d'une HashMap, et null lorsque "cet Event"
	/** id de l'Event qui a r�clam� ce D�placement */
	public Integer idEventCommanditaire;
	/** Mouvements constitutifs de ce D�placement */
	public ArrayList<Mouvement> mouvements;
	/**
	 * faut-il interrompre les Mouvements impossibles, ou attendre qu'ils soient
	 * possibles ?
	 */
	public boolean ignorerLesMouvementsImpossibles = false;
	/** faut-il rejouer le D�placement lorsqu'on l'a termin� ? */
	public boolean repeterLeDeplacement = true;
	/**
	 * faut-il attendre la fin du D�placement pour passer � la Commande suivante ?
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
	 * @param idEventADeplacer                id de l'Event � d�placer, null
	 *                                        signifie "cet Event", 0 le H�ros
	 * @param mouvements                      liste des Mouvements constitutifs du
	 *                                        D�placement
	 * @param ignorerLesMouvementsImpossibles faut-il interrompre les Mouvements
	 *                                        impossibles, ou attendre qu'ils soient
	 *                                        possibles ?
	 * @param repeterLeDeplacement            faut-il rejouer le D�placement
	 *                                        lorsqu'on l'a termin� ?
	 * @param attendreLaFinDuDeplacement      faut-il attendre la fin du D�placement
	 *                                        pour passer � la Commande suivante ?
	 */
	public Deplacement(final Integer idEventADeplacer, final ArrayList<Mouvement> mouvements,
			final boolean ignorerLesMouvementsImpossibles, final boolean repeterLeDeplacement,
			final boolean attendreLaFinDuDeplacement) {
		this.idEventADeplacer = idEventADeplacer;
		this.mouvements = mouvements;
		this.ignorerLesMouvementsImpossibles = ignorerLesMouvementsImpossibles;
		this.repeterLeDeplacement = repeterLeDeplacement;
		this.attendreLaFinDuDeplacement = attendreLaFinDuDeplacement;

		// on apprend aux Mouvements le D�placement dont ils font partie
		for (Mouvement mouvement : this.mouvements) {
			mouvement.deplacement = this;
		}
	}

	/**
	 * Constructeur g�n�rique
	 * 
	 * @param parametres liste de param�tres issus de JSON
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
	 * Mouvements. M�thode appel�e lors de l'ex�cution des Commandes. On passe � la
	 * Commande suivante selon s'il faut attendre la fin du D�placement.
	 */
	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		// IMPORTANT
		// Nous nous trouvons actuellement dans le D�placement qui contient les
		// Mouvements � ajouter au D�placement forc� d'un Event.

		final Event event = this.getEventADeplacer();
		if (event == null) {
			// Event introuvable
			LOG.error("D�placement impossible : l'Event " + this.idEventADeplacer + " n'existe pas !");
			// on r�initialise le Deplacement (au cas o� il est � nouveau execut� dans le
			// futur)
			this.aEteAjouteAuxDeplacementsForces = false;
			// on passe � la Commande suivante
			return curseurActuel + 1;
		}

		if (!this.aEteAjouteAuxDeplacementsForces) {
			// interrompre l'ancien D�placement forc� de l'Event
			if (event.deplacementForce.mouvements != null && event.deplacementForce.mouvements.size() >= 1) {
				LOG.warn("Le d�placement de l'event " + this.idEventADeplacer + " a �t� interrompu et remplac�.");
			}
			event.deplacementForce.mouvements = new ArrayList<Mouvement>();

			// � la place, on ajoute dans la liste les nouveaux Mouvements forc�s
			for (Mouvement mvt : this.mouvements) {
				mvt.reinitialiser();
				event.deplacementForce.mouvements.add(mvt);
				event.deplacementForce.page = this.page; // on indique le commanditaire de ce D�placement
			}
			// on pr�cise le commanditaire qui a impos� ce D�placement � un autre event
			event.deplacementForce.idEventCommanditaire = this.page.event.id;
			// les nouvelles caract�ristiques de D�placement sont assign�es au D�placement
			// forc�
			event.deplacementForce.attendreLaFinDuDeplacement = this.attendreLaFinDuDeplacement;
			event.deplacementForce.ignorerLesMouvementsImpossibles = this.ignorerLesMouvementsImpossibles;
			event.deplacementForce.repeterLeDeplacement = this.repeterLeDeplacement;
			// voil�, les nouveaux Mouvements ont �t� planifi�s

			this.aEteAjouteAuxDeplacementsForces = true;
		}

		// quand est-ce qu'on passe � la Commande suivante ?
		if (!this.attendreLaFinDuDeplacement) {
			// on ne se soucie pas du d�roulement du D�placement

			// on r�initialise le Deplacement (au cas o� il est � nouveau execut� dans le
			// futur)
			this.aEteAjouteAuxDeplacementsForces = false;
			// on passe imm�diatement � la Commande suivante
			return curseurActuel + 1;
		} else {
			// on attend la fin du D�placement avant de passer � la Commande suivante
			if (event.deplacementForce.mouvements.size() <= 0) {
				// la liste a �t� totalement consomm�e

				// on r�initialise le Deplacement (au cas o� il est � nouveau execut� dans le
				// futur)
				this.aEteAjouteAuxDeplacementsForces = false;
				// on passe � la Commande suivante
				return curseurActuel + 1;
			} else {
				// la liste contient encore des Mouvements � effectuer

				// on reste ici
				return curseurActuel;
			}
		}
	}

	/**
	 * Tout Mouvement d�place un Event de la Map en particulier.
	 * 
	 * @return Event qui va �tre d�plac�
	 */
	public final Event getEventADeplacer() {
		if (this.idEventADeplacer != null) {
			// un num�ro d'Event � d�placer a �t� sp�cifi� dans le JSON
			return ((LecteurMap) Main.lecteur).map.eventsHash.get((Integer) this.idEventADeplacer);
		} else {
			// aucun num�ro n'a �t� sp�cifi�, on d�place l'Event qui a lanc� la Commande
			return this.page.event;
		}
	}

	/**
	 * Executer le premier Mouvement du D�placement. M�thode appel�e lorsqu'il faut
	 * d�placer les Events.
	 */
	public final void executerLePremierMouvement() {
		// IMPORTANT
		// Nous nous trouvons actuellement dans le D�placement forc� ou naturel d'un
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
