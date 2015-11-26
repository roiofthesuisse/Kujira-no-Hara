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
import main.GestionClavier;
import main.Partie;

public class Heros extends Event {
	public static String nomImageHeros = "Jiyounasu character.png";
	/**
	 * L'animation d'attaque vaut 0 si le héros n'attaque pas.
	 * Au début d'une attaque, elle est mise au maximum (longueur de l'animation de l'attaque).
	 * A chaque frame, elle est affichée puis décrémentée.
	 */
	public int animationAttaque = 0;

	Heros(Map map, int x, int y, int direction) {
		super(map, x, y, "heros", new ArrayList<PageDeComportement>(), 24, 24);
		this.pages = getPages();
		initialiserLesPages();
	}
	
	public ArrayList<PageDeComportement> getPages(){
		ArrayList<PageDeComportement> pages = new  ArrayList<PageDeComportement>();
		
		//pages
			//page 0 : marche normale
				ArrayList<Condition> conditions0 = null;
				PageDeComportement page0 = new PageDeComportement(conditions0, null, nomImageHeros, null);
				page0.animeEnMouvement = true;
				page0.vitesse=4;
				pages.add(page0);
			//page 1 : déclenchement animation attaque épée
				ArrayList<Condition> conditions1 = new ArrayList<Condition>();
				conditions1.add(new ConditionArmeEquipee(0));
				conditions1.add(new ConditionToucheAction());
				conditions1.add(new ConditionStopEvent(false));
				conditions1.add(new ConditionPasDInterlocuteurAutour());
				ArrayList<CommandeEvent> commandes1 = new ArrayList<CommandeEvent>();
				commandes1.add( new DemarrerAnimationAttaque());
				String nomImageHerosEpee = Arme.getArme(0).nomImageAttaque;
				PageDeComportement page1 = new PageDeComportement(conditions1, commandes1, nomImageHerosEpee, null);
				pages.add(page1);
			//page 2 : animation attaque épée
				ArrayList<Condition> conditions2 = new ArrayList<Condition>();
				conditions2.add(new ConditionAnimationAttaque());
				conditions2.add(new ConditionArmeEquipee(0));
				PageDeComportement page2 = new PageDeComportement(conditions2, null, nomImageHerosEpee, null);
				pages.add(page2);
			//page 3 : déclenchement animation attaque éventail
				ArrayList<Condition> conditions3 = new ArrayList<Condition>();
				conditions3.add(new ConditionArmeEquipee(1));
				conditions3.add(new ConditionToucheAction());
				conditions3.add(new ConditionStopEvent(false));
				ArrayList<CommandeEvent> commandes3 = new ArrayList<CommandeEvent>();
				commandes3.add( new DemarrerAnimationAttaque());
				String nomImageHerosEventail = Arme.getArme(1).nomImageAttaque;
				PageDeComportement page3 = new PageDeComportement(conditions3, commandes3, nomImageHerosEventail, null);
				pages.add(page3);
			//page 4 : animation attaque éventail
				ArrayList<Condition> conditions4 = new ArrayList<Condition>();
				conditions4.add(new ConditionAnimationAttaque());
				conditions4.add(new ConditionArmeEquipee(1));
				PageDeComportement page4 = new PageDeComportement(conditions4, null, nomImageHerosEventail, null);
				pages.add(page4);
		//fin pages
		this.pages = pages;
		return pages;
	}
	
	@Override
	public Boolean deplacementPossible(int sens){
		if(this.animationAttaque>0){ //le héros n'avance pas s'il est en animation d'attaque
			return false;
		}
		return super.deplacementPossible(sens);
	}
	
	@Override
	public void deplacer() {
		if(animationAttaque > 0){
			//pas de déplacement si animation d'attaque
			this.animation = Arme.getArme(Partie.idArmeEquipee).framesDAnimation.get(animationAttaque-1);
			
			animationAttaque--;
		}else{
			//déplacement selon les touches et les obstacles rencontrés
			Boolean ilYADeplacement = false;
			ArrayList<Integer> touchesPressees = this.map.lecteur.fenetre.touchesPressees;
			if( touchesPressees.contains(GestionClavier.codeToucheHaut()) && !touchesPressees.contains(GestionClavier.codeToucheBas()) ){
				if( deplacementPossible(3) ){
					ilYADeplacement = true;
					this.y-=pageActive.vitesse;
				}
			}
			if( touchesPressees.contains(GestionClavier.codeToucheBas()) && !touchesPressees.contains(GestionClavier.codeToucheHaut()) ){
				if( deplacementPossible(0) ){
					ilYADeplacement = true;
					this.y+=pageActive.vitesse;
				}
			}
			if( touchesPressees.contains(GestionClavier.codeToucheGauche()) && !touchesPressees.contains(GestionClavier.codeToucheDroite()) ){
				if( deplacementPossible(1) ){
					ilYADeplacement = true;
					this.x-=pageActive.vitesse;
				}
			}
			if( touchesPressees.contains(GestionClavier.codeToucheDroite()) && !touchesPressees.contains(GestionClavier.codeToucheGauche()) ){
				if( deplacementPossible(2) ){
					ilYADeplacement = true;
					this.x+=pageActive.vitesse;
				}
			}
			if(ilYADeplacement){
				this.avance = true;
				//on profite du déplacement pour remettre le héros dans la bonne direction
				this.map.lecteur.mettreHerosDansLaBonneDirection();
			}else{
				this.avance = false;
				//le héros n'attaque pas et ne bouge pas donc on remet sa première frame d'animation
				this.animation = 0;
			}
		}
	}
	
}
