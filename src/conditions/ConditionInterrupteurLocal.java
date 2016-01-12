package conditions;

import main.Fenetre;

/**
 * Condition pour vérifier la valeur d'un interrupteur local d'un Event
 */
public class ConditionInterrupteurLocal extends Condition {
	boolean valeurQuIlEstCenseAvoir;
	private final int numeroMap;
	private final int numeroEvent;
	private final int numeroInterrupteurLocal;
	
	/**
	 * Constructeur explicite
	 * @param numeroMap numero de la Map où se trouve l'interrupteur local à vérifier
	 * @param numeroEvent numéro de l'Event auquel appartient l'interrupteur local à vérifier
	 * @param numeroInterrupteurLocal à vérifier (0 A ; 1 B ; 2 C ; 3 D)
	 * @param valeur booléenne attendue
	 */
	public ConditionInterrupteurLocal(final int numeroMap, final int numeroEvent, final int numeroInterrupteurLocal, final boolean valeur) {
		this.numeroMap = numeroMap;
		this.numeroEvent = numeroEvent;
		this.numeroInterrupteurLocal = numeroInterrupteurLocal;
		this.valeurQuIlEstCenseAvoir = valeur;
	}
	
	@Override
	public final boolean estVerifiee() {
		String code = "m"+this.numeroMap+"e"+this.numeroEvent+"i"+this.numeroInterrupteurLocal;
		return Fenetre.getPartieActuelle().interrupteursLocaux.get(code) == valeurQuIlEstCenseAvoir;
	}

}