package map;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import commandes.Deplacement;
import conditions.Condition;
import main.Commande;
import main.Fenetre;
import mouvements.Mouvement;

/**
 * Un Event est un élément actif du décor, voire interactif.
 * Ses éventuels comportements sont représentés par une liste de Pages.
 */
public class Event implements Comparable<Event> {
	//constantes
	public static final int VITESSE_PAR_DEFAUT = 4;
	public static final int FREQUENCE_PAR_DEFAUT = 4;
	public static final int DIRECTION_PAR_DEFAUT = Event.Direction.BAS;
	public static final int LARGEUR_HITBOX_PAR_DEFAUT = Fenetre.TAILLE_D_UN_CARREAU;
	public static final int HAUTEUR_HITBOX_PAR_DEFAUT = Fenetre.TAILLE_D_UN_CARREAU;
	public static final int NOMBRE_DE_VIGNETTES_PAR_IMAGE = 4;
	public static final boolean ANIME_A_L_ARRET_PAR_DEFAUT = false;
	public static final boolean ANIME_EN_MOUVEMENT_PAR_DEFAUT = true;
	public static final boolean TRAVERSABLE_PAR_DEFAUT = false; //si une Page est active
	public static final boolean TRAVERSABLE_SI_VIDE = true; //si aucune Page active
	public static final boolean DIRECTION_FIXE_PAR_DEFAUT = false;
	public static final boolean AU_DESSUS_DE_TOUT_PAR_DEFAUT = false;
	public static final boolean REPETER_LE_DEPLACEMENT_PAR_DEFAUT = false;
	public static final boolean IGNORER_LES_MOUVEMENTS_IMPOSSIBLES_PAR_DEFAUT = true;
	public static final boolean ATTENDRE_LA_FIN_DU_DEPLACEMENT_PAR_DEFAUT = true;
	
	/** Map à laquelle cet Event appartient */
	public Map map;
	/** nom de l'Event */
	public String nom;
	/** identifiant de l'Event */
	public Integer id;
	/** numéro de l'Event sur la Map */
	public int numero;
	/** distance entre le bord gauche de l'écran et le corps de l'Event */
	public int x; //en pixels
	/** distance entre le bord haut de l'écran et le corps de l'Event */
	public int y; //en pixels
	
	/** de combien de pixels avance l'Event à chaque pas ? */
	public int vitesseActuelle = VITESSE_PAR_DEFAUT; //1:trèsLent 2:lent 4:normal 8:rapide 16:trèsrapide 32:trèstrèsRapide
	/** toutes les combien de frames l'Event change-t-il d'animation ? */
	public int frequenceActuelle = FREQUENCE_PAR_DEFAUT; //1:trèsAgité 2:agité 4:normal 8:mou 16:trèsMou
	
	/** un Event peut être déplacé par une Commande Event externe à son déplacement naturel nominal */
	public Deplacement deplacementForce;
	
	public BufferedImage imageActuelle = null;
	private boolean estPetitActuel; //si image < 32, considéré comme au sol
	public int direction;
	public int animation;
	
	/** l'Event est-il en train d'avancer en ce moment même ? (utile pour l'animation) */
	public boolean avance = false;
	/** L'Event avançait-il à la frame précédente ? (utile pour l'animation) */
	public boolean avancaitALaFramePrecedente = false;
	/** l'Event est-il en train de sauter en ce moment même ? (utile pour l'animation) */
	public boolean saute = false;
	public int coordonneeApparenteXLorsDuSaut; //en pixels
	public int coordonneeApparenteYLorsDuSaut; //en pixels
	
	/**  L'Event est-il au contact du Héros ? (utile pour la Condition ArriveeAuContact */
	public boolean estAuContactDuHerosMaintenant = false;
	public boolean estAuContactDuHerosAvant = false;
	public int frameDuContact;
	
	/** 
	 * Ces paramètres sont remplis automatiquement au chargement de la page.
	 */
	public boolean animeALArretActuel;
	public boolean animeEnMouvementActuel;
	public boolean traversableActuel;
	public boolean directionFixeActuelle;
	public boolean auDessusDeToutActuel;
	public Deplacement deplacementNaturelActuel;
	
	public int largeurHitbox = LARGEUR_HITBOX_PAR_DEFAUT;
	public int hauteurHitbox = HAUTEUR_HITBOX_PAR_DEFAUT;
	
	/**
	 * Décale l'affichage vers le bas.
	 * En effet, décaler l'affichage dans les trois autres directions est possible en modifiant l'image.
	 */
	int offsetY = 0; 
	public ArrayList<PageEvent> pages;
	public PageEvent pageActive = null;
	
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
	 * @param x numero du carreau où se trouve l'Event, en abscisse, de gauche à droite
	 * @param y numero du carreau où se trouve l'Event, en ordonnée, de haut en bas
	 * @param nom de l'Event
	 * @param id identifiant numérique de l'Event
	 * @param pages ensemble de Pages décrivant le comportement de l'Event
	 * @param largeurHitbox largeur de la boîte de collision
	 * @param hauteurHitbox hauteur de la boîte de collision
	 */
	protected Event(final Integer x, final Integer y, final String nom, final Integer id, final ArrayList<PageEvent> pages, final int largeurHitbox, final int hauteurHitbox) {
		this.x = x * Fenetre.TAILLE_D_UN_CARREAU;
		this.y = y * Fenetre.TAILLE_D_UN_CARREAU;
		this.id = id;
		this.nom = nom;
		this.pages = pages;
		this.largeurHitbox = largeurHitbox;
		this.hauteurHitbox = hauteurHitbox;
		this.deplacementForce = new Deplacement(0, new ArrayList<Mouvement>(), true, false, false);
		initialiserLesPages();
		if (pages!=null && pages.size()>=1) {
			attribuerLesProprietesActuelles(pages.get(0)); //par défaut, propriétés de la première page
		}
	}
	
	/**
	 * Constructeur de l'Event utilisant un tableau de pages JSON
	 * @param x numero du carreau où se trouve l'Event, en abscisse, de gauche à droite
	 * @param y numero du carreau où se trouve l'Event, en ordonnée, de haut en bas
	 * @param nom de l'Event
	 * @param id identifiant numérique de l'Event
	 * @param tableauDesPages tableau JSON contenant les Pages de comportement
	 * @param largeurHitbox largeur de la boîte de collision
	 * @param hauteurHitbox hauteur de la boîte de collision
	 */
	public Event(final Integer x, final Integer y, final String nom, final Integer id, final JSONArray tableauDesPages, final int largeurHitbox, final int hauteurHitbox) {
		this(x, y, nom, id, creerListeDesPagesViaJson(tableauDesPages, id), largeurHitbox, hauteurHitbox);
	}

	/**
	 * Prend le tableau JSON des pages et crée la liste des Pages avec.
	 * @param idEvent identifiant de l'Event
	 * @param tableauDesPages au format JSON
	 * @return liste des Pages de l'Event
	 */
	protected static ArrayList<PageEvent> creerListeDesPagesViaJson(final JSONArray tableauDesPages, final Integer idEvent) {
		final ArrayList<PageEvent> listeDesPages = new ArrayList<PageEvent>();
		int i = 0;
		for (Object pageJSON : tableauDesPages) {
			listeDesPages.add( new PageEvent(i, (JSONObject) pageJSON, idEvent) );
			i++;
		}
		return listeDesPages;
	}

	/**
	 * On relie les Conditions et les Commandes à leur Page.
	 */
	protected final void initialiserLesPages() {
		int numeroCondition = 0;
		try {
			for (PageEvent page : this.pages) {
				page.event = this;
				if (page!=null) {
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
						for (Commande comm : page.commandes) {
							comm.page = page;
						}
					} catch (NullPointerException e2) {
						//pas de commandes dans cette page
					}
				}
			}
		} catch (NullPointerException e3) {
			//pas de pages dans cet event
		}
	}

	/**
	 * Faire faire un Mouvement à l'Event.
	 * Ce Mouvement est soit issu du Déplacement naturel de l'Event, soit de son éventuel Déplacement forcé.
	 * @Warning Cette méthode est overridée pour le Héros.
	 */
	public void deplacer() {
		if (this.deplacementForce!=null && this.deplacementForce.mouvements.size()>0) {
			//il y a un Déplacement forcé
			this.deplacementForce.executerLePremierMouvement();
		} else {
			//pas de Déplacement forcé : on execute le Déplacement naturel
			if (deplacementNaturelActuel!=null && this.deplacementNaturelActuel.mouvements.size()>0) {
				//il y a un Déplacement naturel
				this.deplacementNaturelActuel.executerLePremierMouvement();
			} else {
				//pas de Déplacement du tout
				this.avance = false;
				this.saute = false;
				if (!this.animeALArretActuel && !this.avancaitALaFramePrecedente && !this.avance) {
					//l'event ne bouge plus depuis 2 frames, on arrête son animation
					this.animation = 0;
				}
			}
		}
	}

	@Override
	/**
	 * Permet de dire si un Event est devant ou derrière un autre en terme d'affichage.
	 */
	public final int compareTo(final Event e) {
		final int eEstDevant = -1;
		final int thisEstDevant = 1;
		if (this.auDessusDeToutActuel) {
			if (e.auDessusDeToutActuel) {
				//les deux sont au dessus de tout, on applique la logique inversée
				if (this.y > e.y) {
					return eEstDevant;
				}
				if (this.y < e.y) {
					return thisEstDevant;
				}
			} else {
				return thisEstDevant;
			}
		} else {
			if (e.auDessusDeToutActuel) {
				return eEstDevant;
			} else {
				//y'en a-t-il un au sol ?
				if (this.estPetitActuel && !e.estPetitActuel) {
					return eEstDevant;
				} else if (e.estPetitActuel && !this.estPetitActuel) {
					return thisEstDevant;
				}
				
				//aucun n'est au dessus de tout, on applique la logique normale
				if (this.y > e.y) {
					return thisEstDevant;
				}
				if (this.y < e.y) {
					return eEstDevant;
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
		PageEvent pageQuOnChoisitEnRemplacement = null;
		boolean onATrouveLaPageActive = false;
		boolean cettePageConvientPourLesCommandes = true;
		try {
			for (int i = pages.size()-1; i>=0 && !onATrouveLaPageActive; i--) {
				final PageEvent page = pages.get(i);
				cettePageConvientPourLesCommandes = true;
				boolean cettePageConvientPourLApparence = true;
				if (page.conditions!=null && page.conditions.size()>0) {
					//la Page a des Conditions de déclenchement, on les analyse

					//si une Condition est fausse, la Page ne convient pas
					for (int j = 0; j<page.conditions.size() && cettePageConvientPourLApparence; j++) {
						final Condition cond = page.conditions.get(j);
						if (!cond.estVerifiee()) {
							//la Condition n'est pas vérifiée
							cettePageConvientPourLesCommandes = false;
							if (!cond.estLieeAuHeros()) {
								cettePageConvientPourLApparence = false;
							}
						}
					}
				} else {
					//pas de conditions pour cette Page, donc la Page est choisie
					cettePageConvientPourLesCommandes = true;
					cettePageConvientPourLApparence = true;
				}
				
				//si une Page exigible a été trouvée, pas besoin d'essayer les autres Pages
				if (cettePageConvientPourLApparence) {
					pageQuOnChoisitEnRemplacement = page;
					onATrouveLaPageActive = true;
				}
			}
		} catch (NullPointerException e2) {
			//pas de Pages pour cet Event
			System.err.println("L'event "+this.numero+" ("+this.nom+") n'a pas de pages.");
			e2.printStackTrace();
		}
		if (!onATrouveLaPageActive) {
			//aucune Page ne convient, l'Event n'est pas affiché
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
	private void attribuerLesProprietesActuelles(final PageEvent page) {
		//apparence
		this.imageActuelle = page.image;
		if (!(this instanceof Heros) ) { //le Héros n'est pas redirigé aux changements de Page
			this.direction = page.directionInitiale;
		}
		estPetitActuel = page.estPetit;
		
		//propriétés
		this.vitesseActuelle = page.vitesse;
		this.frequenceActuelle = page.frequence;
		this.animeALArretActuel = page.animeALArret;
		this.auDessusDeToutActuel = page.auDessusDeTout;
		this.animeEnMouvementActuel = page.animeEnMouvement;
		this.traversableActuel = page.traversable;
		this.directionFixeActuelle = page.directionFixe;
		
		//déplacement
		this.deplacementNaturelActuel = page.deplacementNaturel;
	}
	
	/**
	 * Faire disparaître l'Event à l'écran.
	 */
	private void viderLesProprietesActuelles() {
		//apparence
		this.imageActuelle = null;
		if (!(this instanceof Heros) ) { //le Héros n'est pas redirigé aux changements de Page
			this.direction = Event.DIRECTION_PAR_DEFAUT;
		}
		estPetitActuel = true;
		
		//propriétés
		this.vitesseActuelle = Event.VITESSE_PAR_DEFAUT;
		this.frequenceActuelle = Event.FREQUENCE_PAR_DEFAUT;
		this.animeALArretActuel = Event.ANIME_A_L_ARRET_PAR_DEFAUT;
		this.auDessusDeToutActuel = Event.AU_DESSUS_DE_TOUT_PAR_DEFAUT;
		this.animeEnMouvementActuel = Event.ANIME_EN_MOUVEMENT_PAR_DEFAUT;
		this.traversableActuel = Event.TRAVERSABLE_SI_VIDE;
		this.directionFixeActuelle = Event.DIRECTION_FIXE_PAR_DEFAUT;
	
		//déplacement
		this.deplacementNaturelActuel = null;
	}
	
	/**
	 * Où afficher l'Event ?
	 * @return position x de l'image de l'Event
	 */
	public final int xImage() {
		int xBase;
		if (this.saute) {
			xBase = this.coordonneeApparenteXLorsDuSaut;
		} else {
			xBase = this.x;
		}
		final int largeurVignette = this.imageActuelle.getWidth()/4;
		return xBase + (this.largeurHitbox - largeurVignette)/2;
	}
	
	/**
	 * Où afficher l'Event ?
	 * @return position y de l'image de l'Event
	 */
	public final int yImage() {
		int yBase;
		if (this.saute) {
			yBase = this.coordonneeApparenteYLorsDuSaut;
		} else {
			yBase = this.y;
		}
		final int hauteurVignette = this.imageActuelle.getHeight()/4;
		return yBase + this.hauteurHitbox - hauteurVignette + this.offsetY;
	}
	
}