package commandesEvent;

import java.util.ArrayList;
import java.util.HashMap;

import main.Fenetre;
import map.Deplacement;
import map.Event;
import map.LecteurMap;

/**
 * D�placer un Event dans une Direction et d'un certain nombre de cases
 */
//TODO cas du saut absolu ? par exemple : sauter vers la case (3;5)
public class Sauter extends CommandeEvent implements Mouvement {
	//constantes
	private static final int DUREE_DU_SAUT_SUR_PLACE = 10;
	private static final int DUREE_DU_SAUT_PAR_CASE = 1;
	
	private Integer idEventADeplacer; //Integer car cl� d'une HashMap
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
	 * @param x nombre de cases de d�placement en horizontal
	 * @param y nombre de cases de d�placement en vertical
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
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public Sauter(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("idEventADeplacer"),
			  (int) parametres.get("x"), 
			  (int) parametres.get("y") );
	}
	
	/**
	 * Si la Page de comportement doit �tre rejou�e, il faut r�initialiser cette Commande.
	 */
	public void reinitialiser() {
		//rien
	}
	
	/**
	 * Ajouter ce Mouvement � la liste des Mouvements forc�s pour cet Event.
	 */
	@Override
	public final int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
		final Event event = this.getEventADeplacer();
		this.reinitialiser();
		event.deplacementForce.mouvements.add(this);
		return curseurActuel+1;
	}
	
	/**
	 * D�place l'Event pour son d�placement naturel ou pour un d�placement forc�.
	 * Vu qu'on utilise "deplacementActuel", un d�placement forc� devra �tre ins�r� artificiellement dans la liste.
	 * @param deplacement deplacement dont est issu le mouvement (soit d�placement naturel, soit d�placement forc�)
	 */
	public final void executerLeMouvement(final Deplacement deplacement) {
		final Event event = this.getEventADeplacer();
		if ( this.mouvementPossible() ) {

			if (!event.saute) {
				//le mouvement n'a pas encore commenc�, on initialise
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

			//d�placement :
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
				//le mouvement est termin�
				event.saute = false;
				event.x = this.xEventApresSaut;
				event.y = this.yEventApresSaut;
				if (this.x!=0 || this.y!=0) {
					event.direction = this.direction; //on garde la direction prise � cause du saut
				}
				//quelle sera la commande suivante ?
				if (deplacement.repeterLeDeplacement) {
					//on le r�initialise et on le met en bout de file
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
					//on le r�initialise et on le met en bout de file
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
		//TODO � faire
		return true;
	}
	
	/**
	 * Tout Mouvement d�place un Event de la Map en particulier.
	 * @return Event qui va �tre d�plac�
	 */
	public final Event getEventADeplacer() {
		return ((LecteurMap) Fenetre.getFenetre().lecteur).map.eventsHash.get((Integer) this.idEventADeplacer);
	}
	
}