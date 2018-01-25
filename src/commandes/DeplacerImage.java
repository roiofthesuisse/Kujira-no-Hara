package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import commandes.Sauvegarder.Sauvegardable;
import main.Commande;
import map.Picture;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Démarrer une transition progressive de l'état actuel de l'image vers un état d'arrivée.
 * Cette transition peut concerner le position de l'image, son opacité, son zoom, son angle.
 * Il est également possible de changer de mode de fusion, mais ce changement sera immédiat.
 */
public class DeplacerImage extends Commande implements CommandeEvent, Sauvegardable {
	protected static final Logger LOG = LogManager.getLogger(DeplacerImage.class);
	/** Le déplacement d'image est instantané */
	private static final int INSTANTANE = 0;
	/** Par défaut, le déplacement n'est exécuté qu'une fois */
	private static final boolean REPETER_LE_DEPLACEMENT_PAR_DEFAULT = false;
	/** Par défaut, on n'attend pas la fin du déplacement avant de passer à  la Commande suivante */
	private static final boolean ATTENDRE_LA_FIN_DU_DEPLACEMENT_PAR_DEFAULT = false;
	
	/** numéro de l'image à  déplacer */
	private Integer numero; //Integer car utilisé comme clé d'une HashMap
	/** durée (en frames) de la transition */
	private int nombreDeFrames;
	private int dejaFait;
	
	private Integer x;
	private Integer y;
	/** la nouvelle origine est-elle le centre de l'image ? */
	private Boolean centre;
	/** les coordonnées sont-elles stockées dans des variables ? */
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
	
	/** Faut-il répéter en boucle le déplacement ? */
	private final boolean repeterLeDeplacement;
	/** Faut-il attendre la fin du déplacement pour passer à  la Commande suivante ? */
	private final boolean attendreLaFinDuDeplacement;
	
	/**
	 * Constructeur explicite
	 * @param numero de l'image à  modifier
	 * @param nombreDeFrames durée de la transition
	 * @param centre l'origine pour les nouvelles coordonnées de l'image est elle son centre ?
	 * @param variables les nouvelles coordonnées sont-elles stockées dans des variables ?
	 * @param x coordonnée d'arrivée
	 * @param y coordonnée d'arrivée
	 * @param zoomX zoom d'arrivée
	 * @param zoomY zoom d'arrivée
	 * @param opacite opacité d'arrivée
	 * @param modeDeFusion d'arrivée
	 * @param angle d'arrivée
	 * @param repeterLeDeplacement le déplacement boucle-t-il ?
	 * @param attendreLaFinDuDeplacement faut-il passer à  la Commande suivante immédiatement ?
	 */
	public DeplacerImage(final Integer numero, final int nombreDeFrames, final Boolean centre, 
			final boolean variables, final Integer x, final Integer y, final Integer zoomX, final Integer zoomY, 
			final Integer opacite, final ModeDeFusion modeDeFusion, final Integer angle, 
			final boolean repeterLeDeplacement, final boolean attendreLaFinDuDeplacement) {
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
	 * Constructeur générique
	 * @param parametres liste de paramà¨tres issus de JSON
	 */
	public DeplacerImage(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero"),
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
				parametres.containsKey("repeterLeDeplacement") ? (boolean) parametres.get("repeterLeDeplacement") : REPETER_LE_DEPLACEMENT_PAR_DEFAULT,
				parametres.containsKey("attendreLaFinDuDeplacement") ? (boolean) parametres.get("attendreLaFinDuDeplacement") : ATTENDRE_LA_FIN_DU_DEPLACEMENT_PAR_DEFAULT
		);
	}
	
	/**
	 * Calculer le déplacement de l'image.
	 * C'est un état intermédiaire entre l'état de début et l'état de fin.
	 */
	private void calculerDeplacement() {
		final double progression = (double) this.dejaFait / (double) this.nombreDeFrames;
		final Picture picture = getPartieActuelle().images.get(this.numero);
		
		//initialisation des extrémums
		if (this.dejaFait <= 0) {
			this.xDebut = picture.x;
			this.yDebut = picture.y;
			
			if (this.x != null && this.y != null) {
				if (this.variables) {
					//valeurs stockées dans des variables
					this.xFin = getPartieActuelle().variables[this.x];
					this.yFin = getPartieActuelle().variables[this.y];
				} else {
					//valeurs brutes
					this.xFin = this.x;
					this.yFin = this.y;
				}
			}
			
			this.zoomXDebut = picture.zoomX;
			this.zoomYDebut = picture.zoomY;
			this.angleDebut = picture.angle;
			this.opaciteDebut = picture.opacite;
			
			//n'est modifié que ce qui a été explicitement spécifié
			if (this.modeDeFusion != null) {
				picture.modeDeFusion = this.modeDeFusion;
			}
			if (this.centre != null) {
				picture.centre = this.centre;
			}
		}
		
		//n'est modifié que ce qui a été explicitement spécifié
		if (this.x != null) {
			picture.x = (int) Math.round(progression * this.xFin + (1-progression) * this.xDebut);
		}
		if (this.y != null) {
			picture.y = (int) Math.round(progression * this.yFin + (1-progression) * this.yDebut);
		}
		if (this.zoomXFin != null) {
			picture.zoomX = (int) Math.round(progression * this.zoomXFin + (1-progression) * this.zoomXDebut);
		}
		if (this.zoomYFin != null) {
			picture.zoomY = (int) Math.round(progression * this.zoomYFin + (1-progression) * this.zoomYDebut);
		}
		if (this.opaciteFin != null) {
			picture.opacite = (int) Math.round(progression * this.opaciteFin + (1-progression) * this.opaciteDebut);
		}
		if (this.angleFin != null) {
			picture.angle = (int) Math.round(progression * (this.angleDebut+this.angleFin) + (1-progression) * this.angleDebut);
		}
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		if (this.attendreLaFinDuDeplacement) {
			calculerDeplacement();
			// Tant que ce n'est pas terminé, on reste sur cette Commande
			if (this.dejaFait < this.nombreDeFrames) {
				//pas fini
				this.dejaFait++;
				return curseurActuel;
			} else {
				//fini
				this.dejaFait = 0;
				
				if (this.repeterLeDeplacement) {
					// On recommence à  zéro le déplacement
					LOG.info("On recommence le déplacement d'image.");
					return curseurActuel;
				} else {
					// On passe à  la Commande suivante
					LOG.info("Fin du déplacement d'image.");
					return curseurActuel+1;
				}
			}
		} else {
			// On indique que la Picture a un déplacement propre, et on passe à  la Commande suivante
			final Picture picture = getPartieActuelle().images.get(this.numero);
			this.dejaFait = 0;
			LOG.info("Déplacement d'image délégué au lecteur de map.");
			picture.deplacementActuel = this;
			return curseurActuel+1;
		}
	}
	
	/**
	 * Le déplacement de l'image a été délégué par la Commande au LecteurMap afin de passer immédiatement à  la Commande suivante.
	 * Ce déplacement s'effectue donc en parallà¨le, de manià¨re indépendante.
	 * @param picture image à  déplacer
	 */
	public final void executerCommeUnDeplacementPropre(final Picture picture) {
		calculerDeplacement();
		
		if (this.dejaFait < this.nombreDeFrames) {
			//pas fini
			this.dejaFait++;
		} else {
			//fini
			this.dejaFait = 0;
			
			if (!this.repeterLeDeplacement) {
				// on empêche le renouvellement du déplacement
				LOG.info("Fin du déplacement d'image délégué.");
				picture.deplacementActuel = null;
			} else {
				LOG.info("On recommence le déplacement d'image délégué.");
			}
		}
	}
	
	/**
	 * Générer un JSON du déplacement actuel de la Picture pour la Sauvegarde.
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
	 * Si le Déplacement d'image est reconstitué à partir d'une Sauvegarde,
	 * ses attributs doivent être configurés comme ils l'étaient au moment de la Sauvegarde.
	 * @param dejaFait avancement du déplacement
	 * @param xDebut coordonnées (en pixels) avant le déplacement
	 * @param yDebut coordonnées (en pixels) avant le déplacement
	 * @param zoomXDebut aggrandissement (en pourcents) avant le déplacement
	 * @param zoomYDebut aggrandissement (en pourcents) avant le déplacement
	 * @param angleDebut inclinaison avant le déplacement
	 * @param opaciteDebut opacité avant le déplacement
	 * @param xFin coordonnées (en pixels) après le déplacement
	 * @param yFin coordonnées (en pixels) après le déplacement
	 */
	public void configurerEnCoursDeRoute(final int dejaFait, final int xDebut, final int yDebut, 
			final int zoomXDebut, final int zoomYDebut, final int angleDebut, final int opaciteDebut,
			final int xFin, final int yFin) {
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
