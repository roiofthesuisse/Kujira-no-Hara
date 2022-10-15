package mouvements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import main.Main;
import map.Event;
import map.LecteurMap;
import map.Event.Direction;

/**
 * Sauter d'un nombre de cases vers un autre Event
 *
 */
public class SauterVersEvent extends Sauter {
	
	private int idEventObserve;
	private int nombreDeCases;
	//private boolean directionDecidee;
	private int directionPossibleVerticale;
	private int directionPossibleHorizontale;

	/**
	 * Constructeur explicite
	 * @param idEventObserve : id de l'Event vers lequel on saute
	 * @param nombreDeCases : nombre de cases dont on se rapproche
	 */
	public SauterVersEvent(final int idEventObserve, final int nombreDeCases) {
		super(0, 0);
		this.idEventObserve = idEventObserve;
		this.nombreDeCases = nombreDeCases;
		//this.directionDecidee = false;
	}

	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public SauterVersEvent(final HashMap<String, Object> parametres) {
		this((int) parametres.get("idEventObserve"), (int) parametres.get("nombreDeCases"));
	}

	/**
	 * Cases possibles pour l'arriv�e du saut.
	 */
	private final class Carreau implements Comparable<Carreau> {
		int x;
		int y;
		int distanceAuCarre;
		
		/**
		 * Constructeur explicite
		 * @param x Coordonnee x en carreaux
		 * @param y Coordonnee y en carreaux
		 * @param distanceAuCarre distance entre le carreau et l'Event observ�.
		 */
		private Carreau(final int x, final int y, final int distanceAuCarre) {
			this.x = x;
			this.y = y;
			this.distanceAuCarre = distanceAuCarre;
		}

		@Override
		public int compareTo(final Carreau carreau2) {
			return this.distanceAuCarre - carreau2.distanceAuCarre;
		}
	}
	
	/**
	 * Le mouvement dans cette Direction est-il possible ?
	 * @return si le mouvement est possible oui ou non
	 */
	@Override
	public final boolean mouvementPossible() {
		final Event eventObservateur = this.deplacement.getEventADeplacer();
		final Event eventObserve = ((LecteurMap) Main.lecteur).map.eventsHash.get((Integer) this.idEventObserve);
		final ArrayList<Carreau> listeDeCarreaux = new ArrayList<Carreau>();
		
		final int signeX, signeY;
		final int distanceVerticale = eventObservateur.y - eventObserve.y;
		final int distanceHorizontale = eventObservateur.x - eventObserve.x;
		calculerDirection(distanceVerticale, distanceHorizontale);
		if (this.directionPossibleVerticale==Direction.BAS) {
			signeY = 1;
		} else {
			signeY = -1;
		}
		if (this.directionPossibleHorizontale==Direction.DROITE) {
			signeX = 1;
		} else {
			signeX = -1;
		}
		for (int i = 0; i <= this.nombreDeCases; i++) {
			final int j = this.nombreDeCases - i;
			final int deltaXSauteurArrivee = signeX*i*Main.TAILLE_D_UN_CARREAU;
			final int deltaYSauteurArrivee = signeY*j*Main.TAILLE_D_UN_CARREAU;
			final int xArrivee = eventObservateur.x + deltaXSauteurArrivee;
			final int yArrivee = eventObservateur.y + deltaYSauteurArrivee;
			final int deltaXArriveeCible = eventObserve.x - xArrivee;
			final int deltaYArriveeCible = eventObserve.y - yArrivee;
			// On n'ajoute le carreau dans la liste que si l'arriv�e se trouve entre le sauteur et la cible
			if (deltaXSauteurArrivee * deltaXArriveeCible >= 0 && deltaYSauteurArrivee * deltaYArriveeCible >= 0) {
				final int distanceAuCarre = deltaXArriveeCible * deltaXArriveeCible + deltaYArriveeCible * deltaYArriveeCible;
				final Carreau carreau = new Carreau(i, j, distanceAuCarre);
				listeDeCarreaux.add(carreau);
			}
		}
		final int tailleListe = listeDeCarreaux.size();
		// Si aucune case ne convient, on ne saute pas
		if (tailleListe > 0) {
			Collections.sort(listeDeCarreaux);
			for (int i = 0; i < tailleListe; i++) {
				this.x = signeX * listeDeCarreaux.get(i).x;
				this.y = signeY * listeDeCarreaux.get(i).y;
				if (super.mouvementPossible()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * D�termine la direction du Mouvement pour suivre l'event observ�
	 * @param distanceVerticale difference entre le y destination et le y actuel
	 * @param distanceHorizontale difference entre le x destination et le x actuel
	 */
	public final void calculerDirection(final int distanceVerticale, final int distanceHorizontale) {
		if (distanceVerticale < 0) {
			this.directionPossibleVerticale = Direction.BAS;
		} else {
			this.directionPossibleVerticale = Direction.HAUT;
		}
		if (distanceHorizontale < 0) {
			this.directionPossibleHorizontale = Direction.DROITE;
		} else {
			this.directionPossibleHorizontale = Direction.GAUCHE;
		}
		
		if (Math.abs(distanceVerticale) > Math.abs(distanceHorizontale)) {
			this.direction = this.directionPossibleVerticale;
		} else {
			this.direction = this.directionPossibleHorizontale;
		}
	}
	
	@Override
	protected final void reinitialiserSpecifique() {
		super.reinitialiserSpecifique();
		//this.directionDecidee = false;
		this.directionPossibleHorizontale = -1;
		this.directionPossibleVerticale = -1;
		this.x = 0;
		this.y = 0;
	}
	
}
