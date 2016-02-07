package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;
import map.Deplacement;
import map.Event;
import map.LecteurMap;
import map.PageEvent;

/**
 * Déplacer un Event dans une Direction et d'un certain nombre de cases
 */
//TODO cas du saut absolu ? par exemple : sauter vers la case (3;5)
public class Sauter implements CommandeEvent, Mouvement {
	private PageEvent page;
	
	//constantes
	private static final int DUREE_DU_SAUT_SUR_PLACE = 10;
	private static final int DUREE_DU_SAUT_PAR_CASE = 1;
	
	private Integer idEventADeplacer; //Integer car clé d'une HashMap
	private int xEventAvantSaut;
	private int yEventAvantSaut;
	private int x;
	private int y;
	private int xEventApresSaut;
	private int yEventApresSaut;
	private int etapes;
	private int etapesFaites;
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
		this( (int) parametres.get("idEventADeplacer"),
			  (int) parametres.get("x"), 
			  (int) parametres.get("y") );
	}
	
	/**
	 * Si la Page de comportement doit être rejouée, il faut réinitialiser cette Commande.
	 */
	public void reinitialiser() {
		//rien
	}
	
	/**
	 * Ajouter ce Mouvement à la liste des Mouvements forcés pour cet Event.
	 */
	@Override
	public final int executer(final int curseurActuel, final ArrayList<? extends Commande> commandes) {
		final Event event = this.getEventADeplacer();
		this.reinitialiser();
		event.deplacementForce.mouvements.add(this);
		return curseurActuel+1;
	}
	
	/**
	 * Déplace l'Event pour son déplacement naturel ou pour un déplacement forcé.
	 * Vu qu'on utilise "deplacementActuel", un déplacement forcé devra être inséré artificiellement dans la liste.
	 * @param deplacement deplacement dont est issu le mouvement (soit déplacement naturel, soit déplacement forcé)
	 */
	public final void executerLeMouvement(final Deplacement deplacement) {
		final Event event = this.getEventADeplacer();
		if ( this.mouvementPossible() ) {

			if (!event.saute) {
				//le mouvement n'a pas encore commencé, on initialise
				event.saute = true;
				this.xEventAvantSaut = event.x;
				this.yEventAvantSaut = event.y;
				this.xEventApresSaut = xEventAvantSaut + this.x*Fenetre.TAILLE_D_UN_CARREAU;
				this.yEventApresSaut = yEventAvantSaut + this.y*Fenetre.TAILLE_D_UN_CARREAU;
				calculerDistance();
				this.etapesFaites = 0;
				this.etapes = DUREE_DU_SAUT_SUR_PLACE + DUREE_DU_SAUT_PAR_CASE*((int) (distance/Fenetre.TAILLE_D_UN_CARREAU));
				if (this.x==0 && this.y==0) {
					this.direction = event.direction;
				}
			}

			//déplacement :
			final double t = (double) etapesFaites /(double) etapes;
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
			this.etapesFaites++;

			if (etapesFaites>=etapes) {
				//le mouvement est terminé
				event.saute = false;
				event.x = this.xEventApresSaut;
				event.y = this.yEventApresSaut;
				if (this.x!=0 || this.y!=0) {
					event.direction = this.direction; //on garde la direction prise à cause du saut
				}
				//quelle sera la commande suivante ?
				if (deplacement.repeterLeDeplacement) {
					//on le réinitialise et on le met en bout de file
					this.reinitialiser();
					deplacement.mouvements.add(this);
				}
				//on passe au mouvement suivant
				deplacement.mouvements.remove(0);
			}
		} else {
			event.saute = false;
			if (deplacement.ignorerLesMouvementsImpossibles) {
				if (deplacement.repeterLeDeplacement) {
					//on le réinitialise et on le met en bout de file
					this.reinitialiser();
					deplacement.mouvements.add(this);
				}
				//on passe au mouvement suivant
				deplacement.mouvements.remove(0);
			}
		}
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
	
	/**
	 * Tout Mouvement déplace un Event de la Map en particulier.
	 * @return Event qui va être déplacé
	 */
	public final Event getEventADeplacer() {
		return ((LecteurMap) Fenetre.getFenetre().lecteur).map.eventsHash.get((Integer) this.idEventADeplacer);
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
