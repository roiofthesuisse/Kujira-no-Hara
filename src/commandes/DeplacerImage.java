package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;
import map.Picture;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Démarrer une transition progressive de l'état actuel de l'image vers un état d'arrivée.
 * Cette transition peut concerner le position de l'image, son opacité, son zoom, son angle.
 * Il est également possible de changer de mode de fusion, mais ce changement sera immédiat.
 */
public class DeplacerImage extends Commande implements CommandeEvent {
	/** Le déplacement d'image est instantané */
	private static final int INSTANTANE = 0;
	
	/** numéro de l'image à déplacer */
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
	
	/**
	 * Constructeur explicite
	 * @param numero de l'image à modifier
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
	 */
	private DeplacerImage(final Integer numero, final int nombreDeFrames, final Boolean centre, 
			final boolean variables, final Integer x, final Integer y, final Integer zoomX, final Integer zoomY, 
			final Integer opacite, final ModeDeFusion modeDeFusion, final Integer angle) {
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
		
		this.nombreDeFrames = nombreDeFrames;
		this.dejaFait = 0;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
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
				parametres.containsKey("angle") ? (int) parametres.get("angle") : null
		);
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final double progression = (double) this.dejaFait / (double) this.nombreDeFrames;
		final Picture picture = Fenetre.getPartieActuelle().images.get(this.numero);
		
		//initialisation des extrémums
		if (this.dejaFait <= 0) {
			this.xDebut = picture.x;
			this.yDebut = picture.y;
			
			if (this.x != null && this.y != null) {
				if (this.variables) {
					//valeurs stockées dans des variables
					this.xFin = Fenetre.getPartieActuelle().variables[this.x];
					this.yFin = Fenetre.getPartieActuelle().variables[this.y];
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
			picture.angle = (int) Math.round(progression * this.angleFin + (1-progression) * this.angleDebut);
		}

		if (this.dejaFait < this.nombreDeFrames) {
			//pas fini
			this.dejaFait++;
			return curseurActuel;
		} else {
			//fini
			this.dejaFait = 0;
			return curseurActuel+1;
		}
	}
	
}
