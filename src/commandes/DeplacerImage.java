package commandes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import commandes.Sauvegarder.Sauvegardable;
import main.Commande;
import map.Picture;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Demarrer une transition progressive de l'�tat actuel de l'image vers un �tat
 * d'arriv�e. Cette transition peut concerner le position de l'image, son
 * opacit�, son zoom, son angle. Il est �galement possible de changer de mode de
 * fusion, mais ce changement sera imm�diat.
 */
public class DeplacerImage extends Commande implements CommandeEvent, Sauvegardable {
	protected static final Logger LOG = LogManager.getLogger(DeplacerImage.class);
	/** Le deplacement d'image est instantan� */
	private static final int INSTANTANE = 0;
	/** Par defaut, le deplacement n'est execut� qu'une fois */
	private static final boolean REPETER_LE_DEPLACEMENT_PAR_DEFAULT = false;
	/**
	 * Par defaut, on n'attend pas la fin du deplacement avant de passer a la
	 * Commande suivante
	 */
	private static final boolean ATTENDRE_LA_FIN_DU_DEPLACEMENT_PAR_DEFAULT = false;

	/** Numero de l'image a deplacer */
	private Integer numero; // Integer car utilis� comme cla d'une HashMap
	/** Duree (en frames) de la transition */
	private int nombreDeFrames;
	private int dejaFait;

	private Integer x;
	private Integer y;
	/** la nouvelle origine est-elle le centre de l'image ? */
	private Boolean centre;
	/** les Coordonnees sont-elles stock�es dans des variables ? */
	private boolean variables;

	private int xDebut;
	private int yDebut;
	private int xFin;
	private int yFin;
	private int zoomXDebut;
	private int zoomYDebut;
	private Integer zoomXFin;
	private Integer zoomYFin;
	private int opaciteDebut;
	private Integer opaciteFin;
	private ModeDeFusion modeDeFusion;
	private int angleDebut;
	private Integer angleFin;

	/** Faut-il Repeter en boucle le deplacement ? */
	private final boolean repeterLeDeplacement;
	/**
	 * Faut-il attendre la fin du deplacement pour passer a la Commande suivante ?
	 */
	private final boolean attendreLaFinDuDeplacement;

	/**
	 * Constructeur explicite
	 * 
	 * @param numero                     de l'image a modifier
	 * @param nombreDeFrames             Duree de la transition
	 * @param centre                     l'origine pour les nouvelles Coordonnees de
	 *                                   l'image est elle son centre ?
	 * @param variables                  les nouvelles Coordonnees sont-elles
	 *                                   stock�es dans des variables ?
	 * @param x                          Coordonnee d'arriv�e
	 * @param y                          Coordonnee d'arriv�e
	 * @param zoomX                      zoom d'arriv�e
	 * @param zoomY                      zoom d'arriv�e
	 * @param opacite                    opacit� d'arriv�e
	 * @param modeDeFusion               d'arriv�e
	 * @param angle                      d'arriv�e
	 * @param repeterLeDeplacement       le deplacement boucle-t-il ?
	 * @param attendreLaFinDuDeplacement faut-il passer a la Commande suivante
	 *                                   imm�diatement ?
	 */
	public DeplacerImage(final Integer numero, final int nombreDeFrames, final Boolean centre, final boolean variables,
			final Integer x, final Integer y, final Integer zoomX, final Integer zoomY, final Integer opacite,
			final ModeDeFusion modeDeFusion, final Integer angle, final boolean repeterLeDeplacement,
			final boolean attendreLaFinDuDeplacement) {
		this.numero = numero;
		this.centre = centre;
		this.variables = variables;
		this.x = x;
		this.y = y;
		this.zoomXFin = zoomX;
		this.zoomYFin = zoomY;
		this.opaciteFin = opacite;
		this.modeDeFusion = modeDeFusion;
		this.angleFin = angle;

		this.repeterLeDeplacement = repeterLeDeplacement;
		this.attendreLaFinDuDeplacement = attendreLaFinDuDeplacement;

		this.nombreDeFrames = nombreDeFrames;
		this.dejaFait = 0;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public DeplacerImage(final HashMap<String, Object> parametres) {
		this((int) parametres.get("numero"),
				parametres.containsKey("nombreDeFrames") ? (int) parametres.get("nombreDeFrames") : INSTANTANE,
				parametres.containsKey("centre") ? (boolean) parametres.get("centre") : null,
				parametres.containsKey("variables") ? (boolean) parametres.get("variables") : false,
				parametres.containsKey("x") ? (int) parametres.get("x") : null,
				parametres.containsKey("y") ? (int) parametres.get("y") : null,
				parametres.containsKey("zoomX") ? (int) parametres.get("zoomX") : null,
				parametres.containsKey("zoomY") ? (int) parametres.get("zoomY") : null,
				parametres.containsKey("opacite") ? (int) parametres.get("opacite") : null,
				parametres.containsKey("modeDeFusion") ? ModeDeFusion.parNom(parametres.get("modeDeFusion")) : null,
				parametres.containsKey("angle") ? (int) parametres.get("angle") : null,
				parametres.containsKey("repeterLeDeplacement") ? (boolean) parametres.get("repeterLeDeplacement")
						: REPETER_LE_DEPLACEMENT_PAR_DEFAULT,
				parametres.containsKey("attendreLaFinDuDeplacement")
						? (boolean) parametres.get("attendreLaFinDuDeplacement")
						: ATTENDRE_LA_FIN_DU_DEPLACEMENT_PAR_DEFAULT);
	}

	/**
	 * Calculer le deplacement de l'image. C'est un �tat interm�diaire entre l'�tat
	 * de d�but et l'�tat de fin.
	 */
	private void calculerDeplacement() {
		final double progression = (double) this.dejaFait / (double) this.nombreDeFrames;
		final Picture picture = getPartieActuelle().images[this.numero];
		if (picture == null) {
			LOG.error("Impossible de deplacer la Picture numero " + this.numero + " car elle est nulle.");
			return;
		}

		// initialisation des extr�mums
		if (this.dejaFait <= 0) {
			this.xDebut = picture.x;
			this.yDebut = picture.y;

			if (this.x != null && this.y != null) {
				if (this.variables) {
					// valeurs stock�es dans des variables
					this.xFin = getPartieActuelle().variables[this.x];
					this.yFin = getPartieActuelle().variables[this.y];
				} else {
					// valeurs brutes
					this.xFin = this.x;
					this.yFin = this.y;
				}
			}

			this.zoomXDebut = picture.zoomX;
			this.zoomYDebut = picture.zoomY;
			this.angleDebut = picture.angle;
			this.opaciteDebut = picture.opacite;

			// n'est modifi� que ce qui a ete explicitement sp�cifi�
			if (this.modeDeFusion != null) {
				picture.modeDeFusion = this.modeDeFusion;
			}
			if (this.centre != null) {
				picture.centre = this.centre;
			}
		}

		// n'est modifi� que ce qui a ete explicitement sp�cifi�
		if (this.x != null) {
			picture.x = (int) Math.round(progression * this.xFin + (1 - progression) * this.xDebut);
		}
		if (this.y != null) {
			picture.y = (int) Math.round(progression * this.yFin + (1 - progression) * this.yDebut);
		}
		if (this.zoomXFin != null) {
			picture.zoomX = (int) Math.round(progression * this.zoomXFin + (1 - progression) * this.zoomXDebut);
		}
		if (this.zoomYFin != null) {
			picture.zoomY = (int) Math.round(progression * this.zoomYFin + (1 - progression) * this.zoomYDebut);
		}
		if (this.opaciteFin != null) {
			picture.opacite = (int) Math.round(progression * this.opaciteFin + (1 - progression) * this.opaciteDebut);
		}
		if (this.angleFin != null) {
			picture.angle = (int) Math
					.round(progression * (this.angleDebut + this.angleFin) + (1 - progression) * this.angleDebut);
		}
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		if (this.attendreLaFinDuDeplacement) {
			calculerDeplacement();
			// Tant que ce n'est pas termine, on reste sur cette Commande
			if (this.dejaFait < this.nombreDeFrames) {
				// pas fini
				this.dejaFait++;
				return curseurActuel;
			} else {
				// fini
				this.dejaFait = 0;

				if (this.repeterLeDeplacement) {
					// On recommence a z�ro le deplacement
					LOG.info("On recommence le deplacement d'image.");
					return curseurActuel;
				} else {
					// On passe a la Commande suivante
					LOG.info("Fin du deplacement d'image.");
					return curseurActuel + 1;
				}
			}
		} else {
			// On indique que la Picture a un deplacement propre, et on passe a la Commande
			// suivante
			final Picture picture = getPartieActuelle().images[this.numero];
			this.dejaFait = 0;
			LOG.info("Deplacement d'image d�l�gu� au lecteur de map.");
			picture.deplacementActuel = this;
			return curseurActuel + 1;
		}
	}

	/**
	 * Le deplacement de l'image a ete d�l�gu� par la Commande au LecteurMap afin de
	 * passer imm�diatement a la Commande suivante. Ce deplacement s'effectue donc
	 * en parallele, de maniere ind�pendante.
	 * 
	 * @param picture image a deplacer
	 */
	public final void executerCommeUnDeplacementPropre(final Picture picture) {
		calculerDeplacement();

		if (this.dejaFait < this.nombreDeFrames) {
			// pas fini
			this.dejaFait++;
		} else {
			// fini
			this.dejaFait = 0;

			if (!this.repeterLeDeplacement) {
				// on emp�che le renouvellement du deplacement
				LOG.info("Fin du deplacement d'image d�l�gu�.");
				picture.deplacementActuel = null;
			} else {
				LOG.info("On recommence le deplacement d'image d�l�gu�.");
			}
		}
	}

	/**
	 * G�n�rer un JSON du deplacement actuel de la Picture pour la Sauvegarde.
	 */
	@Override
	public JSONObject sauvegarderEnJson() {
		final JSONObject jsonDeplacementActuel = new JSONObject();
		jsonDeplacementActuel.put("nombreDeFrames", this.nombreDeFrames);
		jsonDeplacementActuel.put("dejaFait", this.dejaFait);
		jsonDeplacementActuel.put("xDebut", this.xDebut);
		jsonDeplacementActuel.put("yDebut", this.yDebut);
		jsonDeplacementActuel.put("zoomXDebut", this.zoomXDebut);
		jsonDeplacementActuel.put("zoomYDebut", this.zoomYDebut);
		jsonDeplacementActuel.put("angleDebut", this.angleDebut);
		jsonDeplacementActuel.put("opaciteDebut", this.opaciteDebut);
		if (this.centre != null) {
			jsonDeplacementActuel.put("centre", this.centre);
		}
		if (this.x != null) {
			jsonDeplacementActuel.put("x", this.x);
		}
		if (this.y != null) {
			jsonDeplacementActuel.put("y", this.y);
		}
		jsonDeplacementActuel.put("variables", this.variables);
		jsonDeplacementActuel.put("xFin", this.xFin);
		jsonDeplacementActuel.put("yFin", this.yFin);
		if (this.zoomXFin != null) {
			jsonDeplacementActuel.put("zoomXFin", this.zoomXFin);
		}
		if (this.zoomYFin != null) {
			jsonDeplacementActuel.put("zoomYFin", this.zoomYFin);
		}
		if (this.opaciteFin != null) {
			jsonDeplacementActuel.put("opaciteFin", this.opaciteFin);
		}
		if (this.modeDeFusion != null) {
			jsonDeplacementActuel.put("modeDeFusion", this.modeDeFusion);
		}
		if (this.angleFin != null) {
			jsonDeplacementActuel.put("angleFin", this.angleFin);
		}
		jsonDeplacementActuel.put("repeterLeDeplacement", this.repeterLeDeplacement);
		jsonDeplacementActuel.put("attendreLaFinDuDeplacement", this.attendreLaFinDuDeplacement);
		return jsonDeplacementActuel;
	}

	/**
	 * Si le Deplacement d'image est reconstitu� a partir d'une Sauvegarde, ses
	 * attributs doivent etre configur�s comme ils l'�taient au moment de la
	 * Sauvegarde.
	 * 
	 * @param dejaFait     avancement du deplacement
	 * @param xDebut       Coordonnees (en pixels) avant le deplacement
	 * @param yDebut       Coordonnees (en pixels) avant le deplacement
	 * @param zoomXDebut   aggrandissement (en pourcents) avant le deplacement
	 * @param zoomYDebut   aggrandissement (en pourcents) avant le deplacement
	 * @param angleDebut   inclinaison avant le deplacement
	 * @param opaciteDebut opacit� avant le deplacement
	 * @param xFin         Coordonnees (en pixels) apres le deplacement
	 * @param yFin         Coordonnees (en pixels) apres le deplacement
	 */
	public void configurerEnCoursDeRoute(final int dejaFait, final int xDebut, final int yDebut, final int zoomXDebut,
			final int zoomYDebut, final int angleDebut, final int opaciteDebut, final int xFin, final int yFin) {
		this.dejaFait = dejaFait;
		this.xDebut = xDebut;
		this.yDebut = yDebut;
		this.zoomXDebut = zoomXDebut;
		this.zoomYDebut = zoomYDebut;
		this.angleDebut = angleDebut;
		this.opaciteDebut = opaciteDebut;
		this.xFin = xFin;
		this.yFin = yFin;
	}

}
