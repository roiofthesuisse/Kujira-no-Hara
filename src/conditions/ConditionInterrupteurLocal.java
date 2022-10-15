package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;

/**
 * Condition pour v�rifier la valeur d'un interrupteur local d'un Event
 */
public class ConditionInterrupteurLocal extends Condition  implements CommandeEvent {
	boolean valeurQuIlEstCenseAvoir;
	private Integer numeroMap;
	private Integer idEvent;
	private final int numeroInterrupteurLocal;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param numeroMap numero de la Map Ou se trouve l'interrupteur local a v�rifier
	 * @param idEvent id de l'Event auquel appartient l'interrupteur local a v�rifier
	 * @param numeroInterrupteurLocal a v�rifier (0 A ; 1 B ; 2 C ; 3 D)
	 * @param valeur bool�enne attendue
	 */
	public ConditionInterrupteurLocal(final int numero, final Integer numeroMap, final Integer idEvent, final int numeroInterrupteurLocal, final boolean valeur) {
		this.numero = numero;
		this.numeroMap = numeroMap;
		this.idEvent = idEvent;
		this.numeroInterrupteurLocal = numeroInterrupteurLocal;
		this.valeurQuIlEstCenseAvoir = valeur;
	}
	
	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public ConditionInterrupteurLocal(final HashMap<String, Object> parametres) {
		this( parametres.get("numero") != null ? (int) parametres.get("numero") : -1,
			parametres.containsKey("numeroMap") ? (int) parametres.get("numeroMap") : null, //null signifie "cette Map"
			parametres.containsKey("idEvent") ? (int) parametres.get("idEvent") : null, //null signifie "cet Event"
			(int) parametres.get("numeroInterrupteurLocal"),
			parametres.containsKey("valeurQuIlEstCenseAvoir") ? (boolean) parametres.get("valeurQuIlEstCenseAvoir") : true
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		//null signifie "cette Map"
		if (this.numeroMap == null) {
			this.numeroMap = this.page.event.map.numero;
		}
		//null signifie "cet Event"
		if (this.idEvent == null) {
			this.idEvent = this.page.event.id;
		}
		final String code = "m"+this.numeroMap+"e"+this.idEvent+"i"+this.numeroInterrupteurLocal;
		
		final boolean reponse = getPartieActuelle().interrupteursLocaux.contains(code) == valeurQuIlEstCenseAvoir;
		return reponse;
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximit� avec le Heros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

}