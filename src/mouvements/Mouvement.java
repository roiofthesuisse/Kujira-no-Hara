package mouvements;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import commandes.Deplacement;
import map.Event;

/**
 * Toute CommandeEvent qui provoque le Mouvement d'un Event doit impl�menter cette interface.
 */
public abstract class Mouvement {
	private static final Logger LOG = LogManager.getLogger(Mouvement.class);
	
	/** Nombre d'�tapes du Mouvement qui ont �t� faites */
	protected int ceQuiAEteFait;
	/** Nombre d'�tapes a faire */
	protected int etapes;
	/** D�placement dont fait partie ce Mouvement */
	public Deplacement deplacement;

	/** "suivre" ou "fuir" l'Event observ� */
	protected enum Sens {
		SUIVRE, FUIR
	};
	
	/**
	 * Si la Page de comportement doit �tre rejou�e, il faut r�initialiser cette Commande.
	 * R�initialiser un mouvement le d�clare non fait, et change la direction en cas de mouvement al�atoire.
	 */
	public final void reinitialiser() {
		// R�initialisation sp�cifique a ce type de Mouvement en particulier
		this.reinitialiserSpecifique();
		
		// R�initialisation commune a tous les Mouvements
		this.ceQuiAEteFait = 0;
	}
	
	/**
	 * Actions a effectuer pour pouvoir �ventuellement rejouer le Mouvement encore.
	 * Sp�cifique a un type de Mouvement.
	 */
	protected abstract void reinitialiserSpecifique();
	
	/**
	 * Proc�der aux modifications de donn�es permettant au LecteurMap d'afficher l'Event au bon endroit.
	 * Methode appel�e lors de l'ex�cution des D�placements.
	 * @param deplacement (naturel ou forc� d'un Event) dont fait partie ce Mouvement
	 */
	public final void executerLeMouvement(final Deplacement deplacement) {
		try {
			final Event event = this.deplacement.getEventADeplacer();

			if ( this.mouvementPossible() ) {
				// Appliquer l'effet du Mouvement sur la Map et les Events
				calculDuMouvement(event);

				// Quelle sera la commande suivante ?
				if ( this.ceQuiAEteFait >= this.etapes ) {
					// D�clarer le Mouvement comme termin� (car il est r�ellement termin�)
					terminerLeMouvement(event);
				}
				
				mettreEventDansLaDirectionDuMouvement();
				
			} else {
				// D�clarer le Mouvement comme termin� (car ignor�)
				ignorerLeMouvement(event);
				event.avance = false;
			}
			
		} catch (Exception e) {
			LOG.error("Erreur lors du mouvement de l'�v�nement :", e);
		}
	}
	
	/**
	 * Le Mouvement est-il possible sur cette Map ?
	 * @return true si le Mouvement est possible
	 */
	public abstract boolean mouvementPossible();
	
	
	
	/** 
	 * Applique l'effet du Mouvement sur la Map et les Events.
	 * Puis incr�mente le compteur "ceQuiAEteFait".
	 * @param event subissant le Mouvement
	 */
	protected abstract void calculDuMouvement(Event event);
	
	/** 
	 * D�clarer le Mouvement comme termin�.
	 * @param event subissant le Mouvement
	 */
	private void terminerLeMouvement(final Event event) {
		// Finalisation sp�cifique a ce type de Mouvement en particulier
		terminerLeMouvementSpecifique(event); //d�pend du type de Mouvement
		
		// Finalisation commune a tous les Mouvements
		this.reinitialiser();
		
		// Est-on dans un D�placement naturel ou forc� ?
		final Deplacement deplacementNaturelOuForce;
		if (this.deplacement.naturel) {
			deplacementNaturelOuForce = event.deplacementNaturelActuel;
		} else {
			deplacementNaturelOuForce = event.deplacementForce;
		}
		
		// On regarde si la Page n'a pas chang�,
		// Car on ne remet pas en bout de file un Mouvement naturel issu d'une autre Page 
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
		
		
		// Si le D�placement est perp�tuel, on remet ce Mouvement en fin de liste
		if (this.deplacement.repeterLeDeplacement) {
			if (!this.deplacement.naturel //un Mouvement forc� perp�tuel ne s'arr�te pas m�me si la Page de l'Event change
			|| laPageEstToujoursLaMeme) { //un Mouvement naturel perp�tuel s'arr�te si la Page change
				deplacementNaturelOuForce.mouvements.add(this);
			} else {
				LOG.warn("On ne remet pas en bout de file le Mouvement "+this.getClass().getName()
						+" car la Page de l'Event "+this.deplacement.page.event.id+" ("
						+this.deplacement.page.event.nom+") a chang� : de "+pageAvant+" vers "+pageMaintenant);
			}

		}
		
		// On retire ce Mouvement de la liste
		final String messageDErreurPotentiel = "Impossible de retirer le premier Mouvement " + this.toString() 
		+ " du D�placement " + (this.deplacement.naturel ? "naturel" : "forc�")
		+ " de l'Event " + event.id + " (" + event.nom + ")";
		if (laPageEstToujoursLaMeme || !this.deplacement.naturel) { //un D�placement forc� est au niveau de l'Event, pas de la Page
				if (deplacementNaturelOuForce.mouvements.size() >= 1) {
					if (deplacementNaturelOuForce.mouvements.get(0).equals(this)) {
						deplacementNaturelOuForce.mouvements.remove(0);
					} else {
						LOG.error(messageDErreurPotentiel+" car le premier mouvement du d�placement est un tout autre mouvement : "
								+ deplacementNaturelOuForce.mouvements.get(0).getClass().getName());
					}
				} else {
					LOG.error(messageDErreurPotentiel+" car le d�placement est vide.");
				}
		} else {
			LOG.error(messageDErreurPotentiel+" car la page active a chang� (page "+pageAvant+" -> page "+pageMaintenant+").");
		}
	}
	
	/**
	 * Actions a effectuer lors de la fin du Mouvement.
	 * Sp�cifique a un type de Mouvement.
	 * @param event subissant le Mouvement
	 */
	protected abstract void terminerLeMouvementSpecifique(Event event);
	
	/** 
	 * D�clarer le Mouvement comme termin� car ignor�.
	 * @param event subissant le Mouvement
	 */
	private void ignorerLeMouvement(final Event event) {
		// Interruption sp�cifique a ce type de Mouvement en particulier
		ignorerLeMouvementSpecifique(event);
		
		// Interruption commune a tous les Mouvements
		if (this.deplacement.ignorerLesMouvementsImpossibles) {
			//on ignore ce Mouvement impossible et on passe au suivant
			terminerLeMouvement(event);
		}
	}
	
	/**
	 * Actions a effectuer lors de l'ignorage du Mouvement.
	 * Sp�cifique a un type de Mouvement.
	 * @param event subissant le Mouvement
	 */
	protected abstract void ignorerLeMouvementSpecifique(Event event);
	
	/**
	 * D�crire le Mouvement textuellement
	 * @return description du Mouvement
	 */
	public abstract String toString();
	
	/**
	 * Pendant le Mouvement, l'Event est suceptible de changer de direction.
	 * Il faut tourner l'Event dans la direction dict�e par le Mouvement.
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
	 * Quelle est la direction impos�e par le Mouvement a l'Event ?
	 * @return direction impos�e par le Mouvement a l'Event ou -1 si aucune.
	 */
	public abstract int getDirectionImposee();
	
	/**
	 * Traduit un JSONArray repr�sentant les Mouvements en une liste de Mouvements.
	 * @param mouvementsJSON JSONArray repr�sentant les Mouvements
	 * @return liste des Mouvements
	 */
	public static ArrayList<Mouvement> recupererLesMouvements(final JSONArray mouvementsJSON) {
		final ArrayList<Mouvement> mouvements = new ArrayList<Mouvement>();
		for (Object object : mouvementsJSON) {
			final JSONObject mouvementJson = (JSONObject) object;
			final Mouvement mouvement = recupererUnMouvement(mouvementJson);
			mouvements.add(mouvement);
		}
		return mouvements;
	}
	
	/**
	 * Traduit un objet JSON repr�sentant un Mouvement en un vrai objet Mouvement.
	 * @param mouvementJSON objet JSON repr�sentant un Mouvement
	 * @return un objet Mouvement
	 */
	private static Mouvement recupererUnMouvement(final JSONObject mouvementJSON) {
		final Class<?> classeMouvement;
		final String nomClasseMouvement = ((JSONObject) mouvementJSON).getString("nom");
		Mouvement mouvement = null;
		try {
			classeMouvement = Class.forName("mouvements." + nomClasseMouvement);
			final Iterator<String> parametresNoms = ((JSONObject) mouvementJSON).keys();
			String parametreNom; //nom du param�tre pour instancier le mouvement
			Object parametreValeur; //valeur du param�tre pour instancier le mouvement
			final HashMap<String, Object> parametres = new HashMap<String, Object>();
			while (parametresNoms.hasNext()) {
				parametreNom = parametresNoms.next();
				if (!parametreNom.equals("nom")) { //le nom servait a trouver la classe, ici on ne s'int�resse qu'aux param�tres
					parametreValeur = ((JSONObject) mouvementJSON).get(parametreNom);
					parametres.put( parametreNom, parametreValeur );
				}
			}
			final Constructor<?> constructeurMouvement = classeMouvement.getConstructor(parametres.getClass());
			mouvement = (Mouvement) constructeurMouvement.newInstance(parametres);
		} catch (Exception e) {
			LOG.error("Impossible de traduire l'objet JSON en Mouvement "+nomClasseMouvement, e);
		}
		return mouvement;
	}
	
}
