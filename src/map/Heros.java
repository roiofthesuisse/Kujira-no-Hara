package map;

import java.util.ArrayList;

import comportementEvent.CommandeEvent;
import comportementEvent.DemarrerAnimationAttaque;
import conditions.Condition;
import conditions.ConditionAnimationAttaque;
import conditions.ConditionArmeEquipee;
import conditions.ConditionPasDInterlocuteurAutour;
import conditions.ConditionStopEvent;
import conditions.ConditionToucheAction;
import main.Arme;
import main.Fenetre;
import utilitaire.GestionClavier;

/**
 * Event particulier qui est déplacé par le joueur à l'aide du clavier
 */
public class Heros extends Event {
	//constantes
	private static final int LARGEUR_HEROS = 24;
	private static final int HAUTEUR_HEROS = 24;
	public static final int VITESSE_HEROS_PAR_DEFAUT = 4;
	public static final String NOM_IMAGE_HEROS = "Jiyounasu character.png";
	/**
	 * L'animation d'attaque vaut 0 si le héros n'attaque pas.
	 * Au début d'une attaque, elle est mise au maximum (longueur de l'animation de l'attaque).
	 * A chaque frame, elle est affichée puis décrémentée.
	 */
	public int animationAttaque = 0;

	/**
	 * Constructeur explicite
	 * @param map sur laquelle le Héros se trouve actuellement
	 * @param x position x du Héros sur la Map
	 * @param y position y du Héros sur la Map
	 * @param direction dans laquelle l Héros regarde
	 */
	public Heros(final Map map, final int x, final int y, final int direction) {
		super(map, x, y, direction, "heros", creerPages(), LARGEUR_HEROS, HAUTEUR_HEROS);
	}
	
	/**
	 * Fabriquer les Pages de comportement du Héros
	 * @return liste des Pages de comportement du Héros
	 */
	public static ArrayList<PageDeComportement> creerPages() {
		final ArrayList<PageDeComportement> pages = new  ArrayList<PageDeComportement>();
		
		//pages
			//page 0 : marche normale
				final ArrayList<Condition> conditions0 = null;
				final PageDeComportement page0 = new PageDeComportement(conditions0, null, NOM_IMAGE_HEROS);
				pages.add(page0);
			//page 1 : déclenchement animation attaque épée
				final ArrayList<Condition> conditions1 = new ArrayList<Condition>();
				conditions1.add(new ConditionArmeEquipee(0));
				conditions1.add(new ConditionToucheAction());
				conditions1.add(new ConditionStopEvent(false));
				conditions1.add(new ConditionPasDInterlocuteurAutour());
				final ArrayList<CommandeEvent> commandes1 = new ArrayList<CommandeEvent>();
				commandes1.add( new DemarrerAnimationAttaque());
				final String nomImageHerosEpee = Arme.getArme(0).nomImageAttaque;
				final PageDeComportement page1 = new PageDeComportement(conditions1, commandes1, nomImageHerosEpee);
				pages.add(page1);
			//page 2 : animation attaque épée
				final ArrayList<Condition> conditions2 = new ArrayList<Condition>();
				conditions2.add(new ConditionAnimationAttaque());
				conditions2.add(new ConditionArmeEquipee(0));
				final PageDeComportement page2 = new PageDeComportement(conditions2, null, nomImageHerosEpee);
				pages.add(page2);
			//page 3 : déclenchement animation attaque éventail
				final ArrayList<Condition> conditions3 = new ArrayList<Condition>();
				conditions3.add(new ConditionArmeEquipee(1));
				conditions3.add(new ConditionToucheAction());
				conditions3.add(new ConditionStopEvent(false));
				final ArrayList<CommandeEvent> commandes3 = new ArrayList<CommandeEvent>();
				commandes3.add( new DemarrerAnimationAttaque());
				final String nomImageHerosEventail = Arme.getArme(1).nomImageAttaque;
				final PageDeComportement page3 = new PageDeComportement(conditions3, commandes3, nomImageHerosEventail);
				pages.add(page3);
			//page 4 : animation attaque éventail
				final ArrayList<Condition> conditions4 = new ArrayList<Condition>();
				conditions4.add(new ConditionAnimationAttaque());
				conditions4.add(new ConditionArmeEquipee(1));
				final PageDeComportement page4 = new PageDeComportement(conditions4, null, nomImageHerosEventail);
				pages.add(page4);
		//fin pages
		return pages;
	}
	
	@Override
	public final boolean deplacementPossible(final int sens) {
		if (this.animationAttaque>0) { //le héros n'avance pas s'il est en animation d'attaque
			return false;
		}
		return super.deplacementPossible(sens);
	}
	
	@Override
	public final void deplacer() {
		if (animationAttaque > 0) {
			//pas de déplacement si animation d'attaque
			this.animation = Fenetre.getPartieActuelle().getArmeEquipee().framesDAnimation[animationAttaque-1];
			
			animationAttaque--;
		} else {
			//déplacement selon les touches et les obstacles rencontrés
			boolean ilYADeplacement = false;
			final ArrayList<Integer> touchesPressees = this.map.lecteur.fenetre.touchesPressees;
			if ( touchesPressees.contains(GestionClavier.ToucheRole.HAUT) && !touchesPressees.contains(GestionClavier.ToucheRole.BAS) ) {
				if ( deplacementPossible(Event.Direction.HAUT) ) {
					ilYADeplacement = true;
					this.y -= pageActive.vitesse;
				}
			}
			if ( touchesPressees.contains(GestionClavier.ToucheRole.BAS) && !touchesPressees.contains(GestionClavier.ToucheRole.HAUT) ) {
				if ( deplacementPossible(Event.Direction.BAS) ) {
					ilYADeplacement = true;
					this.y += pageActive.vitesse;
				}
			}
			if ( touchesPressees.contains(GestionClavier.ToucheRole.GAUCHE) && !touchesPressees.contains(GestionClavier.ToucheRole.DROITE) ) {
				if ( deplacementPossible(Event.Direction.GAUCHE) ) {
					ilYADeplacement = true;
					this.x -= pageActive.vitesse;
				}
			}
			if ( touchesPressees.contains(GestionClavier.ToucheRole.DROITE) && !touchesPressees.contains(GestionClavier.ToucheRole.GAUCHE) ) {
				if ( deplacementPossible(Event.Direction.DROITE) ) {
					ilYADeplacement = true;
					this.x += pageActive.vitesse;
				}
			}
			if (ilYADeplacement) {
				this.avance = true;
				//on profite du déplacement pour remettre le héros dans la bonne direction
				this.map.lecteur.mettreHerosDansLaBonneDirection();
			} else {
				this.avance = false;
				//le héros n'attaque pas et ne bouge pas donc on remet sa première frame d'animation
				this.animation = 0;
			}
		}
	}
	
}
