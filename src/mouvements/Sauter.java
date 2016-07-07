package mouvements;

import java.util.HashMap;

import main.Fenetre;
import map.Event;

/**
 * Déplacer un Event dans une Direction et d'un certain nombre de cases
 */
public class Sauter extends Mouvement {
	//constantes
	private static final int DUREE_DU_SAUT_SUR_PLACE = 10;
	private static final int DUREE_DU_SAUT_PAR_CASE = 1;
	
	private int xEventAvantSaut;
	private int yEventAvantSaut;
	protected int x;
	protected int y;
	private int xEventApresSaut;
	private int yEventApresSaut;
	protected int direction;
	private Integer distance = null;
	
	/**
	 * Constructeur explicite
	 * @param x nombre de cases de déplacement en horizontal
	 * @param y nombre de cases de déplacement en vertical
	 */
	public Sauter(final int x, final int y) {
		this.x = x;
		this.y = y;
		calculerDirectionSaut();
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Sauter(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("x"), 
			  (int) parametres.get("y") );
	}
	
	/**
	 * La direction du Saut est calculée selon le x et le y du Saut.
	 */
	protected final void calculerDirectionSaut() {
		if (this.x > this.y) {
			//haut droite
			if (this.y >= -this.x) {
				//bas droite
				this.direction = Event.Direction.DROITE;
			} else {
				//haut gauche
				this.direction = Event.Direction.HAUT;
			}
		} else {
			//bas gauche
			if (this.y > -this.x) {
				//bas droite
				this.direction = Event.Direction.BAS;
			} else {
				//haut gauche
				this.direction = Event.Direction.GAUCHE;
			}
		}
	}
	
	/** 
	 * Applique l'effet du Mouvement sur la Map et les Events.
	 * Puis incrémente le compteur "ceQuiAEteFait".
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
	public boolean mouvementPossible() {
		//TODO à faire
		return true;
	}

	@Override
	protected final void terminerLeMouvementSpecifique(final Event event) {
		event.saute = false;
		event.x = this.xEventApresSaut;
		event.y = this.yEventApresSaut;
		if (this.x!=0 || this.y!=0) {
			event.direction = this.direction; //on garde la direction prise à cause du saut
		}
	}

	@Override
	protected final void ignorerLeMouvementSpecifique(final Event event) {
		event.saute = false;
	}

	@Override
	protected void reinitialiserSpecifique() {
		//rien
	}
	
	@Override
	public final int getDirectionImposee() {
		return this.direction;
	}
	
	@Override
	public final String toString() {
		return "Sauter de "+this.x+" en X et de "+this.y+" en Y";
	}
	
}
