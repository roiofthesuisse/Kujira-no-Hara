package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;
import map.LecteurMap;

/**
 * Faire trembler l'écran.
 */
public class TremblementDeTerre extends Commande implements CommandeEvent {
	/** Nombre de millisecondes dans une seconde */
	private static final int MILLISECONDES = 1000;

	private final int intensite;
	private double vitesse;
	private final int nombreDeFrames;

	private boolean initialisationFaite = false;
	private int dejaFait;

	/**
	 * Constructeur explicite
	 * 
	 * @param intensite      du tremblement de terre
	 * @param vitesse        du tremblement de terre
	 * @param nombreDeFrames durée du tremblement de terre
	 */
	private TremblementDeTerre(final int intensite, final Object vitesse, final int nombreDeFrames) {
		this.intensite = intensite;
		try {
			this.vitesse = (double) vitesse;
		} catch (ClassCastException e) {
			this.vitesse = (double) ((int) vitesse) / 10.0;
		}
		this.nombreDeFrames = nombreDeFrames;
	}

	/**
	 * Constructeur générique
	 * 
	 * @param parametres liste de paramètres issus de JSON
	 */
	public TremblementDeTerre(final HashMap<String, Object> parametres) {
		this((int) parametres.get("intensite"), parametres.get("vitesse"), (int) parametres.get("nombreDeFrames"));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		if (!this.initialisationFaite) {
			this.dejaFait = 0;
			this.initialisationFaite = true;
		}

		final LecteurMap lecteur = this.page.event.map.lecteur;
		lecteur.tremblementDeTerre = (int) (this.intensite
				* Math.sin(Math.PI * this.dejaFait * this.vitesse * MILLISECONDES / LecteurMap.DUREE_FRAME));
		this.dejaFait += 1;

		if (this.dejaFait >= this.nombreDeFrames) {
			// fini
			this.initialisationFaite = false;
			lecteur.tremblementDeTerre = 0;
			return curseurActuel + 1;
		} else {
			// pas fini
			return curseurActuel;
		}
	}

}
