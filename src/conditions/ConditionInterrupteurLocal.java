package conditions;

import java.util.HashMap;

import commandes.CommandeEvent;
import main.Fenetre;
import map.PageEvent;

/**
 * Condition pour vérifier la valeur d'un interrupteur local d'un Event
 */
public class ConditionInterrupteurLocal extends Condition  implements CommandeEvent {
	private PageEvent page;
	
	boolean valeurQuIlEstCenseAvoir;
	private Integer numeroMap;
	private Integer numeroEvent;
	private final int numeroInterrupteurLocal;
	
	/**
	 * Constructeur explicite
	 * @param numeroMap numero de la Map où se trouve l'interrupteur local à vérifier
	 * @param numeroEvent numéro de l'Event auquel appartient l'interrupteur local à vérifier
	 * @param numeroInterrupteurLocal à vérifier (0 A ; 1 B ; 2 C ; 3 D)
	 * @param valeur booléenne attendue
	 */
	public ConditionInterrupteurLocal(final Integer numeroMap, final Integer numeroEvent, final int numeroInterrupteurLocal, final boolean valeur) {
		this.numeroMap = numeroMap;
		this.numeroEvent = numeroEvent;
		this.numeroInterrupteurLocal = numeroInterrupteurLocal;
		this.valeurQuIlEstCenseAvoir = valeur;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionInterrupteurLocal(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("numeroMap") ? (int) parametres.get("numeroMap") : null,
			parametres.containsKey("numeroEvent") ? (int) parametres.get("numeroEvent") : null,
			(int) parametres.get("numeroInterrupteurLocal"),
			(boolean) parametres.get("valeurQuIlEstCenseAvoir")
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		//null signifie "cette Map"
		if (this.numeroMap == null) {
			this.numeroMap = ((CommandeEvent) this).getPage().event.map.numero;
		}
		//null signifie "cet Event"
		if (this.numeroEvent == null) {
			this.numeroEvent = ((CommandeEvent) this).getPage().event.numero;
		}
		final String code = "m"+this.numeroMap+"e"+this.numeroEvent+"i"+this.numeroInterrupteurLocal;
		
		final boolean reponse = Fenetre.getPartieActuelle().interrupteursLocaux.contains(code) == valeurQuIlEstCenseAvoir;
		return reponse;
	}
	
	/**
	 * Ce n'est pas une Condition qui implique une proximité avec le Héros.
	 * @return false 
	 */
	public final boolean estLieeAuHeros() {
		return false;
	}

	@Override
	public final PageEvent getPage() {
		return this.page;
	}

	@Override
	public final void setPage(final PageEvent page) {
		this.page = page;
	}

}