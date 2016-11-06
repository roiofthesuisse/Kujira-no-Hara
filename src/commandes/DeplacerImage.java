package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import utilitaire.Graphismes;
import utilitaire.Graphismes.ModeDeFusion;

public class DeplacerImage extends Commande implements CommandeEvent {
	/** numéro de l'image à déplacer */
	private int numero;
	/** durée (en frames) de la transition */
	private int nombreDeFrames;
	private int dejaFait;
	
	/** la nouvelle origine est-elle le centre de l'image ? */
	private boolean centre;
	/** les coordonnées sont-elles stockées dans des variables ? */
	private boolean variables;
	private int x;
	private int y;
	
	private int xDebut;
	private int yDebut;
	private int xFin;
	private int yFin;
	private int zoomXDebut;
	private int zoomYDebut;
	private int zoomXFin;
	private int zoomYFin;
	private int opaciteDebut;
	private int opaciteFin;
	private ModeDeFusion modeDeFusion;
	private int angleFin;
	
	public DeplacerImage(final int numero, final int nombreDeFrames, final boolean centre, final boolean variables, final int x, final int y, 
			final int zoomX, final int zoomY, final int opacite, final ModeDeFusion modeDeFusion, final int angle) {
		this.numero = numero;
		this.centre = centre;
		this.variables = variables;
		this.x = x;
		this.y = y;
		this.zoomXFin = zoomX;
		this.zoomXFin = zoomY;
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
				(int) parametres.get("nombreDeFrames"),
				parametres.containsKey("centre") ? (boolean) parametres.get("centre") : false,
				parametres.containsKey("variables") ? (boolean) parametres.get("variables") : false,
				parametres.containsKey("x") ? (int) parametres.get("x") : 0,
				parametres.containsKey("y") ? (int) parametres.get("y") : 0,
				parametres.containsKey("zoomX") ? (int) parametres.get("zoomX") : Graphismes.PAS_D_HOMOTHETIE,
				parametres.containsKey("zoomY") ? (int) parametres.get("zoomY") : Graphismes.PAS_D_HOMOTHETIE,
				parametres.containsKey("opacite") ? (int) parametres.get("opacite") : Graphismes.OPACITE_MAXIMALE,
				ModeDeFusion.parNom(parametres.get("modeDeFusion")),
				parametres.containsKey("angle") ? (int) parametres.get("angle") : Graphismes.PAS_DE_ROTATION
		);
	}

	@Override
	public int executer(int curseurActuel, ArrayList<Commande> commandes) {


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
