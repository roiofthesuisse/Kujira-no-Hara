package commandes;

import java.util.HashMap;
import java.util.List;

import main.Commande;
import main.Main;
import map.LecteurMap;
import utilitaire.graphismes.Graphismes;

/**
 * Modifier le ton de l'écran. Utile pour les ambiances lumineuses. Le ton de
 * l'écran est propre au Tileset, donc le changement de ton est conservé d'une
 * Map à l'autre si le Tileset est le même
 */
public class ModifierTonDeLEcran extends Commande implements CommandeEvent {
	// constante
	private static final int MEDIANE = Graphismes.OPACITE_MAXIMALE / 2;

	private final int[] tonFinal = new int[4];
	private int[] tonInitial = new int[4];
	private final int dureeTransition;

	private int dejaFait;

	/**
	 * Constructeur explicite
	 * 
	 * @param rouge           importance du rouge dans le nouveau ton de l'écran
	 * @param vert            importance du vert dans le nouveau ton de l'écran
	 * @param bleu            importance du bleu dans le nouveau ton de l'écran
	 * @param gris            désaturation de l'image
	 * @param dureeTransition (en nombre de frames)
	 */
	public ModifierTonDeLEcran(final int rouge, final int vert, final int bleu, final int gris,
			final int dureeTransition) {
		this.tonFinal[1] = rouge;
		this.tonFinal[2] = vert;
		this.tonFinal[3] = bleu;
		this.tonFinal[0] = gris;
		this.dureeTransition = dureeTransition;
		this.dejaFait = 0;
	}

	/**
	 * Constructeur générique
	 * 
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ModifierTonDeLEcran(final HashMap<String, Object> parametres) {
		this(parametres.containsKey("rouge") ? (int) parametres.get("rouge") : MEDIANE,
				parametres.containsKey("vert") ? (int) parametres.get("vert") : MEDIANE,
				parametres.containsKey("bleu") ? (int) parametres.get("bleu") : MEDIANE,
				parametres.containsKey("gris") ? (int) parametres.get("gris") : 0,
				parametres.containsKey("dureeTransition") ? (int) parametres.get("dureeTransition") : 0);
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		// Début de la transition
		if (this.dejaFait == 0) {
			if (((LecteurMap) Main.lecteur).tonActuel != null) {
				for (int i = 0; i < 4; i++) {
					this.tonInitial[i] = ((LecteurMap) Main.lecteur).tonActuel[i];
				}
			} else {
				this.tonInitial[0] = 0; // pas de gris
				for (int i = 1; i < 4; i++) {
					this.tonInitial[i] = MEDIANE;
				}
				((LecteurMap) Main.lecteur).tonActuel = new int[4];
			}
		}

		if (this.dureeTransition == 0) {
			// transition instantanée
			for (int i = 0; i < 4; i++) {
				((LecteurMap) Main.lecteur).tonActuel[i] = this.tonFinal[i];
			}
		} else {
			// transition lente
			for (int i = 0; i < 4; i++) {
				((LecteurMap) Main.lecteur).tonActuel[i] = (tonInitial[i] * (this.dureeTransition - this.dejaFait)
						+ this.tonFinal[i] * this.dejaFait) / this.dureeTransition;
			}
		}

		if (this.dejaFait < this.dureeTransition) {
			// la transition continue
			this.dejaFait++;
			return curseurActuel;
		} else {
			// fin de la transition
			this.dejaFait = 0;
			return curseurActuel + 1;
		}
	}

}
