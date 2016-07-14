package map;

import java.util.ArrayList;

import commandes.DemarrerAnimationAttaque;
import commandes.Deplacement;
import conditions.Condition;
import conditions.ConditionAnimationAttaque;
import conditions.ConditionArmeEquipee;
import conditions.ConditionPasDInterlocuteurAutour;
import conditions.ConditionStopEvent;
import conditions.ConditionTouche;
import jeu.Arme;
import main.Commande;
import main.Fenetre;
import mouvements.Avancer;
import mouvements.Mouvement;
import utilitaire.GestionClavier;
import utilitaire.GestionClavier.ToucheRole;

/**
 * Event particulier qui est déplacé par le joueur à l'aide du clavier
 */
public class Heros extends Event {
	//constantes
	private static final int LARGEUR_HEROS = 24;
	private static final int HAUTEUR_HEROS = 24;
	public static final int VITESSE_HEROS_PAR_DEFAUT = 4;
	public static final int FREQUENCE_HEROS_PAR_DEFAUT = 4;
	public static final boolean ANIME_A_L_ARRET_HEROS_PAR_DEFAUT = false;
	public static final boolean ANIME_EN_MOUVEMENT_HEROS_PAR_DEFAUT = true;
	public static final boolean TRAVERSABLE_HEROS_PAR_DEFAUT = false;
	public static final boolean DIRECTION_FIXE_HEROS_PAR_DEFAUT = false;
	public static final boolean AU_DESSUS_DE_TOUT_HEROS_PAR_DEFAUT = false;
	public static final String NOM_IMAGE_HEROS = "Jiyounasu character.png";
	
	/**
	 * L'animation d'attaque vaut 0 si le héros n'attaque pas.
	 * Au début d'une attaque, elle est mise au maximum (longueur de l'animation de l'attaque).
	 * A chaque frame, elle est affichée puis décrémentée.
	 */
	public int animationAttaque = 0;

	/**
	 * Constructeur explicite
	 * @param x position x du Héros sur la Map
	 * @param y position y du Héros sur la Map
	 * @param directionEnDebutDeMap directiondu Héros au début de la Map
	 */
	public Heros(final int x, final int y, final int directionEnDebutDeMap) {
		//super(x, y, MODELE.nom, MODELE.id, MODELE.pages, MODELE.largeurHitbox, MODELE.hauteurHitbox);
		super(x, y, "heros", 0, creerPages(), LARGEUR_HEROS, HAUTEUR_HEROS);
		this.direction = directionEnDebutDeMap;
	}
	
	/**
	 * Fabriquer les Pages de comportement du Héros
	 * @return liste des Pages de comportement du Héros
	 */
	public static ArrayList<PageEvent> creerPages() {
		final ArrayList<PageEvent> pages = new  ArrayList<PageEvent>();
		//TODO le Héros devrait être créé à partir d'un fichier JSON
		//pages
			//page 0 : marche normale
				final PageEvent page0 = new PageEvent(0, null, null, NOM_IMAGE_HEROS);
				pages.add(page0);
			//page 1 : déclenchement animation attaque épée
				final ArrayList<Condition> conditions1 = new ArrayList<Condition>();
				conditions1.add(new ConditionArmeEquipee(0));
				conditions1.add(new ConditionTouche(GestionClavier.ToucheRole.ACTION));
				conditions1.add(new ConditionStopEvent(false));
				conditions1.add(new ConditionPasDInterlocuteurAutour());
				final ArrayList<Commande> commandes1 = new ArrayList<Commande>();
				commandes1.add( new DemarrerAnimationAttaque());
				final String nomImageHerosEpee = Arme.getArme(0).nomImageAttaque;
				final PageEvent page1 = new PageEvent(1, conditions1, commandes1, nomImageHerosEpee);
				pages.add(page1);
			//page 2 : animation attaque épée
				final ArrayList<Condition> conditions2 = new ArrayList<Condition>();
				conditions2.add(new ConditionAnimationAttaque());
				conditions2.add(new ConditionArmeEquipee(0));
				final PageEvent page2 = new PageEvent(2, conditions2, null, nomImageHerosEpee);
				pages.add(page2);
			//page 3 : déclenchement animation attaque éventail
				final ArrayList<Condition> conditions3 = new ArrayList<Condition>();
				conditions3.add(new ConditionArmeEquipee(1));
				conditions3.add(new ConditionTouche(GestionClavier.ToucheRole.ACTION));
				conditions3.add(new ConditionStopEvent(false));
				final ArrayList<Commande> commandes3 = new ArrayList<Commande>();
				commandes3.add( new DemarrerAnimationAttaque());
				final String nomImageHerosEventail = Arme.getArme(1).nomImageAttaque;
				final PageEvent page3 = new PageEvent(3, conditions3, commandes3, nomImageHerosEventail);
				pages.add(page3);
			//page 4 : animation attaque éventail
				final ArrayList<Condition> conditions4 = new ArrayList<Condition>();
				conditions4.add(new ConditionAnimationAttaque());
				conditions4.add(new ConditionArmeEquipee(1));
				final PageEvent page4 = new PageEvent(4, conditions4, null, nomImageHerosEventail);
				pages.add(page4);
		//fin pages
		return pages;
	}
	
	@Override
	public final void deplacer() {
		if (animationAttaque > 0) {
			//pas de déplacement si animation d'attaque
			this.animation = Fenetre.getPartieActuelle().getArmeEquipee().framesDAnimation[animationAttaque-1];
			
			animationAttaque--;
		} else if (this.deplacementForce!=null && this.deplacementForce.mouvements.size()>0) {
			//il y a un déplacement forcé
			this.deplacementForce.executerLePremierMouvement();
		} else {
			//déplacement selon les touches et les obstacles rencontrés
			boolean ilYADeplacement = false;
			if ( GestionClavier.ToucheRole.HAUT.pressee && !GestionClavier.ToucheRole.BAS.pressee ) {
				if ( unPasVers(Event.Direction.HAUT).mouvementPossible() ) {
					ilYADeplacement = true;
					this.y -= pageActive.vitesse;
				}
			}
			if ( GestionClavier.ToucheRole.BAS.pressee && !GestionClavier.ToucheRole.HAUT.pressee ) {
				if ( unPasVers(Event.Direction.BAS).mouvementPossible() ) {
					ilYADeplacement = true;
					this.y += pageActive.vitesse;
				}
			}
			if ( GestionClavier.ToucheRole.GAUCHE.pressee && !GestionClavier.ToucheRole.DROITE.pressee ) {
				if ( unPasVers(Event.Direction.GAUCHE).mouvementPossible() ) {
					ilYADeplacement = true;
					this.x -= pageActive.vitesse;
				}
			}
			if ( GestionClavier.ToucheRole.DROITE.pressee && !GestionClavier.ToucheRole.GAUCHE.pressee ) {
				if ( unPasVers(Event.Direction.DROITE).mouvementPossible() ) {
					ilYADeplacement = true;
					this.x += pageActive.vitesse;
				}
			}
			if (ilYADeplacement) {
				this.avance = true;
				//on profite du déplacement pour remettre le Héros dans la bonne direction
				this.mettreDansLaBonneDirection();
			} else {
				this.avance = false;
				//le Héros n'attaque pas et ne bouge pas donc on remet sa première frame d'animation
				this.animation = 0;
			}
		}
	}
	
	/**
	 * Créer un pas dans la direction voulue.
	 * @param dir direction du pas
	 * @return un pas dans la direction demandée
	 */
	private Mouvement unPasVers(final int dir) {
		final Mouvement pas = new Avancer(dir, pageActive.vitesse);
		pas.deplacement = new Deplacement(0, new ArrayList<Mouvement>(), true, false, false);
		pas.deplacement.page = this.pageActive; //on apprend au Déplacement quelle est sa Page
		return pas;
	}
	
	/**
	 * Tourner le Héros dans la bonne Direction selon l'entrée clavier.
	 * Il faut qu'à ce moment le Héros soit libre de ses Mouvements.
	 */
	public final void mettreDansLaBonneDirection() {
		final Heros heros = map.heros;
		if (!this.map.lecteur.stopEvent //pas de gel des Events
				&& heros.animationAttaque <= 0 //pas en attaque
				&& (this.deplacementForce == null || this.deplacementForce.mouvements.size() <= 0) //pas de Déplacement forcé
		) {
			if ( GestionClavier.ToucheRole.GAUCHE.pressee ) {
				heros.direction = Event.Direction.GAUCHE;
			} else if ( GestionClavier.ToucheRole.DROITE.pressee ) {
				heros.direction = Event.Direction.DROITE;
			} else if ( GestionClavier.ToucheRole.BAS.pressee ) {
				heros.direction = Event.Direction.BAS;
			} else if ( GestionClavier.ToucheRole.HAUT.pressee ) {
				heros.direction = Event.Direction.HAUT;
			}
		}
	}
	
}
