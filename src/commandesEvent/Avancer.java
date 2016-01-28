package commandesEvent;

import java.util.ArrayList;
import java.util.HashMap;

import main.Fenetre;
import map.Deplacement;
import map.Event;
import map.Heros;
import map.Hitbox;
import map.LecteurMap;
import map.Event.Direction;

/**
 * Déplacer un Event dans une Direction et d'un certain nombre de cases
 */
public class Avancer extends CommandeEvent implements Mouvement {
	protected Integer idEventADeplacer; //Integer car clé d'une HashMap
	protected int direction;
	public int nombreDePixels;
	public int ceQuiAEteFait = 0; //avancée en pixel, doit atteindre nombreDeCarreaux*32
	
	/**
	 * Constructeur explicite
	 * @param idEventADeplacer identifiant de l'Event qui subira le Mouvement
	 * @param direction dans laquelle l'Event doit avancer
	 * @param nombreDePixels distance parcourue
	 */
	public Avancer(final int idEventADeplacer, final int direction, final int nombreDePixels) {
		this.idEventADeplacer = idEventADeplacer;
		this.direction = direction;
		this.nombreDePixels = nombreDePixels;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Avancer(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("idEventADeplacer"),
			  (int) parametres.get("direction"), 
			  (int) parametres.get("nombreDeCarreaux")*Fenetre.TAILLE_D_UN_CARREAU );
	}
	
	/**
	 * Obtenir la Direction du mouvement
	 * @return la Direction du mouvement
	 */
	public final int getDirection() {
		return direction;
	}
	
	/**
	 * Si la Page de comportement doit être rejouée, il faut réinitialiser cette Commande.
	 * Réinitialiser un mouvement le déclare non fait, et change la direction en cas de mouvement aléatoire.
	 */
	public void reinitialiser() {
		ceQuiAEteFait = 0;
	}
	
	/**
	 * Ajouter ce Mouvement à la liste des Mouvements forcés pour cet Event.
	 */
	@Override
	public final int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
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
		try {
			final Event event = this.getEventADeplacer();
			final int sens = this.getDirection();
			if ( this.mouvementPossible() ) {
				event.avance = true;
				//déplacement :
				switch (sens) {
					case Direction.BAS : 
						event.y += event.vitesseActuelle; 
						break;
					case Direction.GAUCHE : 
						event.x -= event.vitesseActuelle; 
						break;
					case Direction.DROITE : 
						event.x += event.vitesseActuelle; 
						break;
					case Direction.HAUT : 
						event.y -= event.vitesseActuelle; 
						break;
				}
				this.ceQuiAEteFait += event.vitesseActuelle;
				//*ancien emplacement de l'animation*
				//quelle sera la commande suivante ?
				if ( this.ceQuiAEteFait >= this.nombreDePixels ) {
					if (deplacement.repeterLeDeplacement) {
						//on le réinitialise et on le met en bout de file
						this.reinitialiser();
						deplacement.mouvements.add(this);
					}
					//on passe au mouvement suivant
					deplacement.mouvements.remove(0);
				}
			} else {
				event.avance = false;
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
			
		} catch (NullPointerException e1) {
			//pas de mouvement pour cet évènement
		} catch (Exception e) {
			System.out.println("Erreur lors du mouvement de l'évènement :");
			e.printStackTrace();
		}
	}
	
	/**
	 * Le mouvement dans cette Direction est-il possible ?
	 * @return si le mouvement est possible oui ou non
	 */
	public final boolean mouvementPossible() {
		final Event event = this.getEventADeplacer();
		final int sens = this.getDirection();
		
		//si c'est le Héros, il n'avance pas s'il est en animation d'attaque
		if (event instanceof Heros && ((Heros) event).animationAttaque > 0) { 
			return false;
		}
		
		//si l'Event est lui-même traversable, il peut faire son mouvement
		if (event.traversableActuel) {
			return true;
		}
		
		boolean reponse = true;
		int xAInspecter = event.x; //pour le décor
		int yAInspecter = event.y;
		int xAInspecter2 = event.x; //pour le décor, deuxième case à vérifier si entre deux cases
		int yAInspecter2 = event.y;
		int xAInspecter3 = event.x; //pour les events
		int yAInspecter3 = event.y;
		switch(sens) {
		case Event.Direction.BAS : 
			yAInspecter += event.hauteurHitbox;   
			yAInspecter2 += event.hauteurHitbox;   
			xAInspecter2 += Fenetre.TAILLE_D_UN_CARREAU; 
			yAInspecter3 += event.vitesseActuelle; 
			break;
		case Event.Direction.GAUCHE : 
			xAInspecter -= event.vitesseActuelle; 
			xAInspecter2 -= event.vitesseActuelle; 
			yAInspecter2 += Fenetre.TAILLE_D_UN_CARREAU; 
			xAInspecter3 -= event.vitesseActuelle; 
			break;
		case Event.Direction.DROITE : 
			xAInspecter += event.largeurHitbox;   
			xAInspecter2 += event.largeurHitbox;   
			yAInspecter2 += Fenetre.TAILLE_D_UN_CARREAU; 
			xAInspecter3 += event.vitesseActuelle; 
			break;
		case Event.Direction.HAUT : 
			yAInspecter -= event.vitesseActuelle; 
			yAInspecter2 -= event.vitesseActuelle; 
			xAInspecter2 += Fenetre.TAILLE_D_UN_CARREAU; 
			yAInspecter3 -= event.vitesseActuelle; 
			break;
		default : 
			break;
		}
		try {
			//si rencontre avec un élément de décor non passable -> false
			if (!event.map.casePassable[xAInspecter/Fenetre.TAILLE_D_UN_CARREAU][yAInspecter/Fenetre.TAILLE_D_UN_CARREAU]) {
				return false;
			}
			if ((sens==Direction.BAS||sens==Direction.HAUT) && ((event.x+event.largeurHitbox-1)/Fenetre.TAILLE_D_UN_CARREAU!=(event.x/Fenetre.TAILLE_D_UN_CARREAU)) && !event.map.casePassable[xAInspecter2/Fenetre.TAILLE_D_UN_CARREAU][yAInspecter2/Fenetre.TAILLE_D_UN_CARREAU]) {
				return false;
			}
			if ((sens==Direction.GAUCHE||sens==Direction.DROITE) && ((event.y+event.hauteurHitbox-1)/Fenetre.TAILLE_D_UN_CARREAU!=(event.y/Fenetre.TAILLE_D_UN_CARREAU)) && !event.map.casePassable[xAInspecter2/Fenetre.TAILLE_D_UN_CARREAU][yAInspecter2/Fenetre.TAILLE_D_UN_CARREAU]) {
				return false;
			}
			//voilà
			
			//si rencontre avec un autre évènement non traversable -> false
			for (Event autreEvent : event.map.events) {
				if (event.numero != autreEvent.numero 
					&& !autreEvent.traversableActuel
					&& Hitbox.lesHitboxesSeChevauchent(xAInspecter3, yAInspecter3, event.largeurHitbox, event.hauteurHitbox, autreEvent.x, autreEvent.y, autreEvent.largeurHitbox, autreEvent.hauteurHitbox) 
				) {
					return false;
				}
			}
		} catch (Exception e) {
			//on sort de la map !
			e.printStackTrace();
			reponse = true;
		}
		return reponse;
	}
	
	/**
	 * Tout Mouvement déplace un Event de la Map en particulier.
	 * @return Event qui va être déplacé
	 */
	public final Event getEventADeplacer() {
		return ((LecteurMap) Fenetre.getFenetre().lecteur).map.eventsHash.get((Integer) this.idEventADeplacer);
	}
	
}
