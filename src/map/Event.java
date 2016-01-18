package map;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import comportementEvent.Avancer;
import comportementEvent.CommandeEvent;
import conditions.Condition;
import main.Fenetre;

/**
 * Un Event est un élément actif du décor, voire interactif.
 * Ses éventuels comportements sont représentés par une liste de Pages.
 */
public class Event implements Comparable<Event> {
	//constantes
	public static final int VITESSE_PAR_DEFAUT = 4;
	public static final int FREQUENCE_PAR_DEFAUT = 4;
	public static final int LARGEUR_HITBOX_PAR_DEFAUT = Fenetre.TAILLE_D_UN_CARREAU;
	public static final int HAUTEUR_HITBOX_PAR_DEFAUT = Fenetre.TAILLE_D_UN_CARREAU;
	public static final boolean ANIME_A_L_ARRET_PAR_DEFAUT = false;
	public static final boolean ANIME_EN_MOUVEMENT_PAR_DEFAUT = true;
	public static final boolean TRAVERSABLE_PAR_DEFAUT = false;
	public static final boolean AU_DESSUS_DE_TOUT_PAR_DEFAUT = false;
	public static final boolean REPETER_LE_DEPLACEMENT_PAR_DEFAUT = false;
	public static final boolean IGNORER_LES_MOUVEMENTS_IMPOSSIBLES_PAR_DEFAUT = true;
	
	/** Map à laquelle cet Event appartient */
	public Map map;
	/** nom de l'Event */
	public String nom;
	/** numéro de l'Event sur la Map */
	public int numero;
	/** distance entre le bord gauche de la map et le bord gauche de la Hitbox de l'Event */
	public int x; //en pixels
	/** distance entre le bord haut de la map et le bord haut de la Hitbox de l'Event */
	public int y; //en pixels
	
	/** de combien de pixels avance l'Event à chaque pas ? */
	public int vitesseActuelle = VITESSE_PAR_DEFAUT; //1:trèsLent 2:lent 4:normal 8:rapide 16:trèsrapide 32:trèstrèsRapide
	/** toutes les combien de frames l'Event change-t-il d'animation ? */
	public int frequenceActuelle = FREQUENCE_PAR_DEFAUT; //1:trèsAgité 2:agité 4:normal 8:mou 16:trèsMou
	
	/** un Event peut être déplacé par une Commande Event externe à son déplacement naturel nominal */
	public ArrayList<CommandeEvent> deplacementForce;
	
	public int direction;
	public int animation;
	
	public BufferedImage imageActuelle = null;
	/** l'Event est-il en train d'avancer en ce moment même ? (utile pour l'animation) */
	public boolean avance = false;
	/** L'Event avançait-il à la frame précédente ? (utile pour l'animation) */
	protected boolean avancaitALaFramePrecedente = false;
	/**  L'Event est-il au contact du Héros ? (utile pour la Condition ArriveeAuContact */
	public boolean estAuContactDuHerosMaintenant = false;
	public boolean estAuContactDuHerosAvant = false;
	public int frameDuContact;
	
	/** 
	 * Ces paramètres sont remplis automatiquement au chargement de la page.
	 */
	public boolean animeALArretActuel = false;
	public boolean animeEnMouvementActuel = true;
	public ArrayList<CommandeEvent> deplacementNaturelActuel;
	public boolean repeterLeDeplacementActuel = true;
	public boolean ignorerLesMouvementsImpossiblesActuel = false;
	public boolean traversableActuel = false;
	public boolean auDessusDeToutActuel = false;
	public int largeurHitbox = LARGEUR_HITBOX_PAR_DEFAUT;
	public int hauteurHitbox = HAUTEUR_HITBOX_PAR_DEFAUT;
	/**
	 * Décale l'affichage vers le bas.
	 * En effet, décaler l'affichage dans les trois autres directions est possible en modifiant l'image.
	 */
	int offsetY = 0; 
	public ArrayList<PageDeComportement> pages;
	public PageDeComportement pageActive = null;
	
	/**
	 * Lorsque ce marqueur est à true, on considère l'event comme supprimé.
	 * Ce n'est qu'un simple marqueur : l'event n'est réellement supprimé qu'après la boucle for sur les events.
	 */
	public boolean supprime = false;
	
	/**
	 * Tout Event regarde dans une Direction.
	 */
	public static class Direction {
		public static final int BAS = 0;
		public static final int GAUCHE = 1;
		public static final int DROITE = 2;
		public static final int HAUT = 3;
	}
	
	/**
	 * Constructeur explicite de l'Event
	 * @param map Map à laquelle l'Event appartient
	 * @param x numero du carreau où se trouve l'Event, en abscisse, de gauche à droite
	 * @param y numero du carreau où se trouve l'Event, en ordonnée, de haut en bas
	 * @param direction de l'Event
	 * @param nom de l'Event
	 * @param pages ensemble de Pages décrivant le comportement de l'Event
	 * @param largeurHitbox largeur de la boîte de collision
	 * @param hauteurHitbox hauteur de la boîte de collision
	 */
	protected Event(final Map map, final Integer x, final Integer y, final Integer direction, final String nom, final ArrayList<PageDeComportement> pages, final int largeurHitbox, final int hauteurHitbox) {
		this.map = map;
		this.x = x * Fenetre.TAILLE_D_UN_CARREAU;
		this.y = y * Fenetre.TAILLE_D_UN_CARREAU;
		this.direction = direction;
		this.nom = nom;
		this.pages = pages;
		this.largeurHitbox = largeurHitbox;
		this.hauteurHitbox = hauteurHitbox;
		initialiserLesPages();
		if (pages!=null && pages.size()>=1) {
			attribuerLesProprietesActuelles(pages.get(0)); //par défaut, propriétés de la première page
		}
	}
	
	/**
	 * Constructeur de l'Event utilisant un tableau de pages JSON
	 * @param map Map à laquelle l'Event appartient
	 * @param x numero du carreau où se trouve l'Event, en abscisse, de gauche à droite
	 * @param y numero du carreau où se trouve l'Event, en ordonnée, de haut en bas
	 * @param direction de l'Event
	 * @param nom de l'Event
	 * @param tableauDesPages tableau JSON contenant les Pages de comportement
	 * @param largeurHitbox largeur de la boîte de collision
	 * @param hauteurHitbox hauteur de la boîte de collision
	 */
	protected Event(final Map map, final Integer x, final Integer y, final Integer direction, final String nom, final JSONArray tableauDesPages, final int largeurHitbox, final int hauteurHitbox) {
		this(map, x, y, direction, nom, creerListeDesPagesViaJson(tableauDesPages), largeurHitbox, hauteurHitbox);
	}

	/**
	 * Prend le tableau JSON des pages et crée la liste des Pages avec.
	 * @param tableauDesPages au format JSON
	 * @return liste des Pages de l'Event
	 */
	private static ArrayList<PageDeComportement> creerListeDesPagesViaJson(final JSONArray tableauDesPages) {
		final ArrayList<PageDeComportement> listeDesPages = new ArrayList<PageDeComportement>();
		for (Object pageJSON : tableauDesPages) {
			listeDesPages.add( new PageDeComportement((JSONObject) pageJSON) );
		}
		return listeDesPages;
	}

	/**
	 * On numérote les Pages de l'Event.
	 * On relie les Conditions et les Commandes à leur Page.
	 */
	protected final void initialiserLesPages() {
		int numeroCondition = 0;
		int numeroPage = 0;
		try {
			for (PageDeComportement page : this.pages) {
				page.event = this;
				if (page!=null) {
					page.numero = numeroPage;
					//numérotation des conditions et on apprend aux conditions qui est leur page
					try {
						for (Condition cond : page.conditions) {
							cond.numero = numeroCondition;
							cond.page = page;
							numeroCondition++;
						}
					} catch (NullPointerException e1) {
						//pas de conditions pour déclencher cette page
					}
					//on apprend aux commandes qui est leur page
					try {
						for (CommandeEvent comm : page.commandes) {
							comm.page = page;
						}
					} catch (NullPointerException e2) {
						//pas de commandes dans cette page
					}
					numeroPage++;
				}
			}
		} catch (NullPointerException e3) {
			//pas de pages dans cet event
		}
	}

	/**
	 * Est-ce que cet Event pourra aller à cet endroit dans le futur, 
	 * sachant qu'un autre Event se trouve à tel endroit ?
	 * @param xFutur position x de l'Event dans le futur
	 * @param yFutur position y de l'Event dans le futur
	 * @param largHitbox largeur de la boîte de collision de cet Event
	 * @param hautHitbox hauteur de la boîte de collision de cet Event
	 * @param xAutre position x actuelle de l'autre Event
	 * @param yAutre position y actuelle de l'autre Event
	 * @param largHitboxAutre largeur de la boîte de collision de l'autre Event
	 * @param hautHitboxAutre hauteur de la boîte de collision de l'autre Event
	 * @return true si les deux Events vont se marcher dessus, false s'ils vont rester distants
	 */
	private boolean lesHitboxesSeChevauchent(final int xFutur, final int yFutur, final int largHitbox, final int hautHitbox, final int xAutre, final int yAutre, final int largHitboxAutre, final int hautHitboxAutre) {
		final int deltaX = (xFutur-xAutre);
		final int deltaY = (yFutur-yAutre);
		final int rayon = (Math.max(largHitbox, hautHitbox)+Math.max(largHitboxAutre, hautHitboxAutre));
		if (deltaX*deltaX+deltaY*deltaY > rayon*rayon/2) {
			return false; //si les deux events sont trop éloignés l'un de l'autre, il n'y a pas de collision
		} else { 
			final int x1min = xFutur;
			final int x1max = xFutur+largHitbox;
			final int x2min = xAutre;
			final int x2max = xAutre+largHitboxAutre;
			final int y1min = yFutur;
			final int y1max = yFutur+hautHitbox;
			final int y2min = yAutre;
			final int y2max = yAutre+hautHitboxAutre;
			return Hitbox.lesDeuxRectanglesSeChevauchent(x1min, x1max, y1min, y1max, x2min, x2max, y2min, y2max, largHitbox, hautHitbox, largHitboxAutre, hautHitboxAutre);
		}
	}
	
	/**
	 * Le déplacement dans cette direction est-il possible ?
	 * @param sens direction dans laquelle l'Event compte avancer
	 * @return si le déplacement est possible oui ou non
	 */
	protected boolean deplacementPossible(final int sens) {
		//si l'Event est lui-même traversable, il peut faire son mouvement
		if (this.traversableActuel) {
			return true;
		}
		
		boolean reponse = true;
		int xAInspecter = this.x; //pour le décor
		int yAInspecter = this.y;
		int xAInspecter2 = this.x; //pour le décor, deuxième case à vérifier si entre deux cases
		int yAInspecter2 = this.y;
		int xAInspecter3 = this.x; //pour les events
		int yAInspecter3 = this.y;
		switch(sens) {
		case Event.Direction.BAS : 
			yAInspecter += hauteurHitbox;   
			yAInspecter2 += hauteurHitbox;   
			xAInspecter2 += Fenetre.TAILLE_D_UN_CARREAU; 
			yAInspecter3 += vitesseActuelle; 
			break;
		case Event.Direction.GAUCHE : 
			xAInspecter -= vitesseActuelle; 
			xAInspecter2 -= vitesseActuelle; 
			yAInspecter2 += Fenetre.TAILLE_D_UN_CARREAU; 
			xAInspecter3 -= vitesseActuelle; 
			break;
		case Event.Direction.DROITE : 
			xAInspecter += largeurHitbox;   
			xAInspecter2 += largeurHitbox;   
			yAInspecter2 += Fenetre.TAILLE_D_UN_CARREAU; 
			xAInspecter3 += vitesseActuelle; 
			break;
		case Event.Direction.HAUT : 
			yAInspecter -= vitesseActuelle; 
			yAInspecter2 -= vitesseActuelle; 
			xAInspecter2 += Fenetre.TAILLE_D_UN_CARREAU; 
			yAInspecter3 -= vitesseActuelle; 
			break;
		default : 
			break;
		}
		try {
			//si rencontre avec un élément de décor non passable -> false
			if (!map.casePassable[xAInspecter/Fenetre.TAILLE_D_UN_CARREAU][yAInspecter/Fenetre.TAILLE_D_UN_CARREAU]) {
				return false;
			}
			if ((sens==Direction.BAS||sens==Direction.HAUT) && ((x+largeurHitbox-1)/Fenetre.TAILLE_D_UN_CARREAU!=(x/Fenetre.TAILLE_D_UN_CARREAU)) && !map.casePassable[xAInspecter2/Fenetre.TAILLE_D_UN_CARREAU][yAInspecter2/Fenetre.TAILLE_D_UN_CARREAU]) {
				return false;
			}
			if ((sens==Direction.GAUCHE||sens==Direction.DROITE) && ((y+hauteurHitbox-1)/Fenetre.TAILLE_D_UN_CARREAU!=(y/Fenetre.TAILLE_D_UN_CARREAU)) && !map.casePassable[xAInspecter2/Fenetre.TAILLE_D_UN_CARREAU][yAInspecter2/Fenetre.TAILLE_D_UN_CARREAU]) {
				return false;
			}
			//voilà
			
			//si rencontre avec un autre évènement non traversable -> false
			for (Event autreEvent : this.map.events) {
				if (this.numero != autreEvent.numero 
					&& !autreEvent.traversableActuel
					&& lesHitboxesSeChevauchent(xAInspecter3, yAInspecter3, largeurHitbox, hauteurHitbox, autreEvent.x, autreEvent.y, autreEvent.largeurHitbox, autreEvent.hauteurHitbox) 
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
	 * Faire faire un mouvement à l'Event.
	 * Ce mouvement est soit issu du déplacement naturel de l'Event, soit de son éventuel déplacement forcé.
	 */
	public void deplacer() {
		if (this.deplacementForce!=null && this.deplacementForce.size()>=0) {
			//il y a un déplacement forcé
			executerLeDeplacement(this.deplacementForce);
		} else {
			//pas de déplacement forcé : on execute le déplacement naturel
			if (pageActive!=null || (deplacementNaturelActuel!=null&&deplacementNaturelActuel.size()>0)) {
				//TODO actuellement le déplacement naturel est ainsi choisi :
				//"si la page active est nulle, le deplacementActuel est celui de la dernière page qui a été activée"
				//mais le déplacement naturel doit être choisi de la même façon que l'apparence et les propriétés,
				//c'est-à-dire en comptant les conditions d'activation, SAUF Parole/Contact/ArrivéeAuContact.			
				
				//il y a un déplacement naturel
				executerLeDeplacement(this.deplacementNaturelActuel);
			}
		}
	}
	
	/**
	 * Executer un à un les mouvements du déplacement naturel de l'Event.
	 * Le déplacement naturel figure dans la description de l'Event dès sa création.
	 */
	private void executerLeDeplacement(ArrayList<CommandeEvent> deplacement) {
		try {
			final CommandeEvent mouvementActuel = deplacement.get(0);
			final String typeMouvement = mouvementActuel.getClass().getName();
			//TODO on ne devrait pas faire de switch !
			//il faut utiliser la programmation orientée objet,
			//c'est-à-dire faire mouvementActuel.executer()
			//et déporter une partie du code de executerLeMouvement() dedans.
			switch(typeMouvement) {
				case "comportementEvent.Avancer" : 
					executerLeMouvement((Avancer) mouvementActuel, deplacement); 
					break;
				case "comportementEvent.AvancerAleatoirement" : 
					executerLeMouvement((Avancer) mouvementActuel, deplacement); 
					break;
				//autres mouvements...
				default : 
					break;
			}
		} catch (NullPointerException e1) {
			//pas de déplacement pour cet event
		} catch (IndexOutOfBoundsException e2) {
			//le déplacement ne contient aucune commande
		}
	}
	
	/**
	 * Déplace l'Event pour son déplacement naturel ou pour un déplacement forcé.
	 * Vu qu'on utilise "deplacementActuel", un déplacement forcé devra être inséré artificiellement dans la liste.
	 * @param mouvementActuel mouvement à executer
	 * @param deplacement deplacement dont est issu le mouvement (soit déplacement naturel, soit déplacement forcé)
	 */
	@Deprecated 
	public final void executerLeMouvement(final Avancer mouvementActuel, final ArrayList<CommandeEvent> deplacement) {
		try {
			final int sens = ((Avancer) mouvementActuel).getDirection();
			if ( deplacementPossible(sens) ) {
				this.avance = true;
				//déplacement :
				switch (sens) {
					case Direction.BAS : 
						this.y += this.vitesseActuelle; 
						break;
					case Direction.GAUCHE : 
						this.x -= this.vitesseActuelle; 
						break;
					case Direction.DROITE : 
						this.x += this.vitesseActuelle; 
						break;
					case Direction.HAUT : 
						this.y -= this.vitesseActuelle; 
						break;
				}
				((Avancer) mouvementActuel).ceQuiAEteFait += vitesseActuelle;
				//*ancien emplacement de l'animation*
				//quelle sera la commande suivante ?
				if ( ((Avancer) mouvementActuel).ceQuiAEteFait >= ((Avancer) mouvementActuel).nombreDeCarreaux*Fenetre.TAILLE_D_UN_CARREAU) {
					//TODO créer un objet "Deplacement" 
					//qui contient une liste de Commandes Event "mouvements"
					//et les deux attributs booléens "repeterDeplacement" et "ignorerLesMouvementsImpossibles"
					
					//TODO puis, ici, remplacer le "repeterLeDeplacementActuel" par "deplacement.repeterDeplacement"
					if (repeterLeDeplacementActuel) {
						//on le réinitialise et on le met en bout de file
						((Avancer) mouvementActuel).reinitialiser();
						deplacement.add(mouvementActuel);
					}
					//on passe au mouvement suivant
					deplacement.remove(0);
				}
			} else {
				this.avance = false;
				if (ignorerLesMouvementsImpossiblesActuel) {
					if (repeterLeDeplacementActuel) {
						//on le réinitialise et on le met en bout de file
						((Avancer) mouvementActuel).reinitialiser();
						deplacement.add(mouvementActuel);
					}
					//on passe au mouvement suivant
					deplacement.remove(0);
				}
			}
			
		} catch (NullPointerException e1) {
			//pas de mouvement pour cet évènement
		} catch (Exception e) {
			System.out.println("Erreur lors du mouvement de l'évènement :");
			e.printStackTrace();
		}
	}

	@Override
	/**
	 * Permet de dire si un event est devant ou derrière un autre en terme d'affichage.
	 */
	public final int compareTo(final Event e) {
		if (auDessusDeToutActuel) {
			if (e.auDessusDeToutActuel) {
				//les deux sont au dessus de tout, on applique la logique inversée
				if (y > e.y) {
					return -1;
				}
				if (y < e.y) {
					return 1;
				}
			} else {
				//this est plus grand
				return 1;
			}
		} else {
			if (e.auDessusDeToutActuel) {
				//e est plus grand
				return -1;
			} else {
				//aucun n'est au dessus de tout, on applique la logique normale
				if (y > e.y) {
					return 1;
				}
				if (y < e.y) {
					return -1;
				}
			}
		}
		return 0;
	}

	/**
	 * Active la Page de l'Event qui vérifie toutes les Conditions de déclenchement.
	 * S'il y a plusieurs Pages valides, on prend la dernière.
	 * Les Commandes Event de la Page choisie seront executées.
	 */
	public final void activerUnePage() {
		PageDeComportement pageQuOnChoisitEnRemplacement = null;
		boolean onATrouveLaPageActive = false;
		boolean cettePageConvientPourLesCommandes = true;
		try {
			for (int i = pages.size()-1; i>=0 && !onATrouveLaPageActive; i--) {
				final PageDeComportement page = pages.get(i);
				cettePageConvientPourLesCommandes = true;
				boolean cettePageConvientPourLApparence = true;
				try {
					//si une condition est fausse, la page ne convient pas
					for (int j = 0; j<page.conditions.size() && cettePageConvientPourLApparence; j++) {
						final Condition cond = page.conditions.get(j);
						//if(this instanceof Heros && i==1){
						//	System.out.println("condition "+j+" "+cond.getClass().getName());
						//}
						if (!cond.estVerifiee()) {
							//if(this instanceof Heros && i==1){
							//	System.out.println("pas verifiee");
							//}
							//la Condition n'est pas vérifiée
							cettePageConvientPourLesCommandes = false;
							if (!cond.estLieeAuHeros()) {
								cettePageConvientPourLApparence = false;
							}
						}else{
							//if(this instanceof Heros && i==1){
							//	System.out.println("verifiee");
							//}
						}
					}
				} catch (NullPointerException e1) {
					//pas de conditions pour cette page
				}
				if (cettePageConvientPourLApparence) {
					pageQuOnChoisitEnRemplacement = page;
					onATrouveLaPageActive = true;
				}
			}
		} catch (NullPointerException e2) {
			//pas de Pages pour cet Event
			System.err.println("L'event "+this.numero+" n'a pas de pages.");
		}
		
		if (!onATrouveLaPageActive) {
			//aucune Page ne correspond, l'Event n'est pas affiché
			this.pageActive = null;
			viderLesProprietesActuelles();
			return;
		} else {
			//une Page correspond au moins pour les Conditions non liées au Héros, on donne son apparence à l'Event
			attribuerLesProprietesActuelles(pageQuOnChoisitEnRemplacement);
			
			this.pageActive = null;
			if (cettePageConvientPourLesCommandes) {
				//même les Conditions liées au Héros correspondent, on execute la Page
				this.pageActive = pageQuOnChoisitEnRemplacement;
			}
		}
	}

	/**
	 * On assigne les propriétés actuelles en utilisant celles d'une Page donnée.
	 * @param page dont on récupère les propriétés pour les donner à l'Event
	 */
	private void attribuerLesProprietesActuelles(final PageDeComportement page) {
		//apparence
		this.imageActuelle = page.image;
		if (!(this instanceof Heros) ) { //le Héros n'est pas redirigé aux changements de Page
			this.direction = page.directionInitiale;
		}
		
		//propriétés
		this.vitesseActuelle = page.vitesse;
		this.frequenceActuelle = page.frequence;
		this.animeALArretActuel = page.animeALArret;
		this.auDessusDeToutActuel = page.auDessusDeTout;
		this.animeEnMouvementActuel = page.animeEnMouvement;
		this.traversableActuel = page.traversable;
		//déplacement
		this.deplacementNaturelActuel = page.deplacementNaturel;
		this.repeterLeDeplacementActuel = page.repeterLeDeplacement;
		this.ignorerLesMouvementsImpossiblesActuel = page.ignorerLesMouvementsImpossibles;
	}
	
	/**
	 * Faire disparaître l'Event à l'écran.
	 */
	private void viderLesProprietesActuelles() {
		//apparence
		this.imageActuelle = null;
		if (!(this instanceof Heros) ) { //le Héros n'est pas redirigé aux changements de Page
			this.direction = Event.Direction.BAS;
		}
		
		//propriétés
		this.vitesseActuelle = Event.VITESSE_PAR_DEFAUT;
		this.frequenceActuelle = Event.FREQUENCE_PAR_DEFAUT;
		this.animeALArretActuel = false;
		this.auDessusDeToutActuel = false;
		this.animeEnMouvementActuel = false;
		this.traversableActuel = true;
		//déplacement
		this.deplacementNaturelActuel = null;
		this.repeterLeDeplacementActuel = false;
		this.ignorerLesMouvementsImpossiblesActuel = true;
	}
	
}