package commandesEvent;

import java.util.ArrayList;
import java.util.HashMap;

import main.Fenetre;
import map.Deplacement;
import map.Event;

/**
 * Déplacer un Event dans une Direction et d'un certain nombre de cases
 */
//TODO cas du saut absolu ? par exemple : sauter vers la case (3;5)
public class Sauter extends CommandeEvent implements Mouvement {
	//constantes
	private static final int NOMBRE_D_ETAPES_POUR_LE_SAUT_SUR_PLACE = 8;
	
	private int xEventAvantSaut;
	private int yEventAvantSaut;
	private int x;
	private int y;
	private int xEventApresSaut;
	private int yEventApresSaut;
	public int etapes;
	public int etapesFaites;
	
	/**
	 * Constructeur explicite
	 * @param x nombre de cases de déplacement en horizontal
	 * @param y nombre de cases de déplacement en vertical
	 */
	public Sauter(final int x, final int y) {
		this.x = x;
		this.y = y;
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
	 * Si la Page de comportement doit être rejouée, il faut réinitialiser cette Commande.
	 */
	public void reinitialiser() {
		//TODO
	}

	//TODO cette méthode doit ajouter un Mouvement dans le Déplacement forcé, rien d'autre
	//le LecteurMap prendra le relais
	@Override
	public final int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
		final Event event = this.page.event;
		if (!event.saute) {
			//le mouvement n'a pas commencé
			event.saute = true;
			this.xEventAvantSaut = event.x;
			this.yEventAvantSaut = event.y;
			this.xEventApresSaut = xEventAvantSaut + this.x*Fenetre.TAILLE_D_UN_CARREAU;
			this.yEventApresSaut = yEventAvantSaut + this.y*Fenetre.TAILLE_D_UN_CARREAU;
			this.etapes = NOMBRE_D_ETAPES_POUR_LE_SAUT_SUR_PLACE + ((Double) Math.sqrt(x*x+y+y)).intValue();
			this.etapesFaites = 0;
		}
		
		if (etapesFaites>=etapes) {
			event.saute = false; //le mouvement est terminé
			return curseurActuel+1;
		}
		
		//le mouvement est en cours
		event.deplacer();
		return curseurActuel;
	}
	
	/**
	 * Déplace l'Event pour son déplacement naturel ou pour un déplacement forcé.
	 * Vu qu'on utilise "deplacementActuel", un déplacement forcé devra être inséré artificiellement dans la liste.
	 * @param event qui doit avancer
	 * @param deplacement deplacement dont est issu le mouvement (soit déplacement naturel, soit déplacement forcé)
	 */
	public final void executerLeMouvement(final Event event, final Deplacement deplacement) {
		/*
		try {
			if ( this.mouvementPossible(event) ) {
				event.saute = true;
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
				if ( this.ceQuiAEteFait >= this.nombreDeCarreaux*Fenetre.TAILLE_D_UN_CARREAU ) {
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
		*/
	}
	
	public boolean mouvementPossible(Event e){
		//TODO à faire
		return true;
	}
	
}
