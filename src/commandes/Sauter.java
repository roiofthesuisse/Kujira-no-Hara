package commandes;

import java.util.HashMap;

import main.Fenetre;
import map.Deplacement;
import map.Event;

/**
 * Déplacer un Event dans une Direction et d'un certain nombre de cases
 */
//TODO cas du saut absolu ? par exemple : sauter vers la case (3;5)
public class Sauter extends Mouvement {
	//constantes
	private static final int DUREE_DU_SAUT_SUR_PLACE = 10;
	private static final int DUREE_DU_SAUT_PAR_CASE = 1;
	
	private int xEventAvantSaut;
	private int yEventAvantSaut;
	private int x;
	private int y;
	private int xEventApresSaut;
	private int yEventApresSaut;
	private int direction;
	private Integer distance = null;
	
	/**
	 * Constructeur explicite
	 * @param idEventADeplacer identifiant de l'Event qui subira le Mouvement
	 * @param x nombre de cases de déplacement en horizontal
	 * @param y nombre de cases de déplacement en vertical
	 */
	public Sauter(final Integer idEventADeplacer, final int x, final int y) {
		this.idEventADeplacer = idEventADeplacer;
		this.x = x;
		this.y = y;
		if (x>y) {
			//haut droite
			if (y>=-x) {
				//bas droite
				this.direction = Event.Direction.DROITE;
			} else {
				//haut gauche
				this.direction = Event.Direction.HAUT;
			}
		} else {
			//bas gauche
			if (y>-x) {
				//bas droite
				this.direction = Event.Direction.BAS;
			} else {
				//haut gauche
				this.direction = Event.Direction.GAUCHE;
			}
		}
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Sauter(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("idEventADeplacer") ? (int) parametres.get("idEventADeplacer") : null,
			  (int) parametres.get("x"), 
			  (int) parametres.get("y") );
	}
	
	/**
	 * Déplace l'Event pour son déplacement naturel ou pour un déplacement forcé.
	 * Vu qu'on utilise "deplacementActuel", un déplacement forcé devra être inséré artificiellement dans la liste.
	 * @param event subissant le Mouvement
	 */
	public final void calculDuMouvement(final Event event) {
		if (!event.saute) {
			//le Mouvement n'a pas encore commencé, on initialise
			event.saute = true;
			this.xEventAvantSaut = event.x;
			this.yEventAvantSaut = event.y;
			this.xEventApresSaut = xEventAvantSaut + this.x*Fenetre.TAILLE_D_UN_CARREAU;
			this.yEventApresSaut = yEventAvantSaut + this.y*Fenetre.TAILLE_D_UN_CARREAU;
			calculerDistance();
			this.ceQuiAEteFait = 0;
			this.etapes = DUREE_DU_SAUT_SUR_PLACE + DUREE_DU_SAUT_PAR_CASE*((int) (distance/Fenetre.TAILLE_D_UN_CARREAU));
			if (this.x==0 && this.y==0) {
				this.direction = event.direction;
			}
		}

		//déplacement :
		final double t = (double) ceQuiAEteFait /(double) etapes;
		final int x0 = xEventAvantSaut;
		final int y0 = yEventAvantSaut;
		final int xf = xEventApresSaut;
		final int yf = yEventApresSaut;
		
		final int xDroite = (int) Math.round( (1-t)*x0 + t*xf );
		final int yDroite = (int) Math.round( (1-t)*y0 + t*yf );
		
		final int yParabole = (int) Math.round( 1.5*(distance+2*Fenetre.TAILLE_D_UN_CARREAU)*(t*t-t) );
		event.coordonneeApparenteXLorsDuSaut = (int) xDroite;
		event.coordonneeApparenteYLorsDuSaut = (int) (yParabole + yDroite);
		event.directionLorsDuSaut = this.direction;
		this.ceQuiAEteFait++;
	}
	
	/**
	 * Initialiser la valeur de la distance parcourue au sol.
	 */
	private void calculerDistance() {
		if (this.distance == null) {
			final int deltaX = this.xEventApresSaut - this.xEventAvantSaut;
			final int deltaY = yEventApresSaut - yEventAvantSaut;
			this.distance = (int) Math.round( Math.sqrt(deltaX*deltaX + deltaY*deltaY) );
		}
	}
	
	/**
	 * Le Mouvement est-il possible pour cet Event ?
	 * @return true si le Mouvement est possible, false sinon
	 */
	public final boolean mouvementPossible() {
		//TODO à faire
		return true;
	}

	@Override
	protected final void terminerLeMouvementSpecifique(final Event event, final Deplacement deplacement) {
		event.saute = false;
		event.x = this.xEventApresSaut;
		event.y = this.yEventApresSaut;
		if (this.x!=0 || this.y!=0) {
			event.direction = this.direction; //on garde la direction prise à cause du saut
		}
	}

	@Override
	protected final void ignorerLeMouvementSpecifique(final Event event, final Deplacement deplacement) {
		event.saute = false;
	}

	@Override
	protected void reinitialiserSpecifique() {
		//rien
	}
	
}
