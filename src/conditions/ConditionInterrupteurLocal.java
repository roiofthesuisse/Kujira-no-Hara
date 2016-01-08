package conditions;

import map.Event;

/**
 * Condition pour vérifier la valeur d'un interrupteur local d'un Event
 */
public class ConditionInterrupteurLocal extends Condition {
	int numeroInterrupteur;
	boolean valeurQuIlEstCenseAvoir;
	Event eventConcerne;
	
	/**
	 * Constructeur explicite
	 * @param lettre 0 A ; 1 B ; 2 C ; 3 D
	 * @param event concerné
	 * @param valeur booléenne attendue
	 */
	public ConditionInterrupteurLocal(final int lettre, final Event event, final boolean valeur) {
		this.numeroInterrupteur = lettre;
		this.eventConcerne = event;
		this.valeurQuIlEstCenseAvoir = valeur;
	}
	
	@Override
	public final Boolean estVerifiee() {
		switch(numeroInterrupteur) {
			case 0 : return eventConcerne.interrupteurLocalA==valeurQuIlEstCenseAvoir;
			case 1 : return eventConcerne.interrupteurLocalB==valeurQuIlEstCenseAvoir;
			case 2 : return eventConcerne.interrupteurLocalC==valeurQuIlEstCenseAvoir;
			default : return eventConcerne.interrupteurLocalD==valeurQuIlEstCenseAvoir;
		}
	}

}