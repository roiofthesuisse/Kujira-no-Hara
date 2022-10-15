package mouvements;

import java.util.HashMap;

import main.Main;
import map.Event;

/**
 * D�placer un Event sur une case destination d�finie
 */
public class SautAbsolu extends Sauter {
	
	private int xDestination;
	private int yDestination;

	/**
	 * Constructeur explicite
	 * @param xDestination : Coordonnee x de la case destination (en nombre de carreaux)
	 * @param yDestination : Coordonnee y de la case destination (en nombre de carreaux)
	 */
	public SautAbsolu(final int xDestination, final int yDestination) {
		super(0, 0);
		this.xDestination = xDestination;
		this.yDestination = yDestination;
	}

	/**
	 * Constructeur generique
	 * @param parametres liste de parametres issus de JSON
	 */
	public SautAbsolu(final HashMap<String, Object> parametres) {
		this((int) parametres.get("xDestination"), (int) parametres.get("yDestination"));
	}
	
	/**
	 * Le Mouvement est-il possible pour cet Event ?
	 * @return true si le Mouvement est possible, false sinon
	 */
	public final boolean mouvementPossible() {
		final Event eventADeplacer = this.deplacement.getEventADeplacer();
		this.x = this.xDestination - (eventADeplacer.x/Main.TAILLE_D_UN_CARREAU);
		this.y = this.yDestination - (eventADeplacer.y/Main.TAILLE_D_UN_CARREAU);
		calculerDirectionSaut();
		return super.mouvementPossible();
	}

}
