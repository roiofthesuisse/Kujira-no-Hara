package conditions;

import main.Arme;
import main.Fenetre;

/**
 * Si l'attaque en est au moment du coup donné.
 * En effet, toutes les images de l'animation d'attaque ne correspondent pas au coup donné.
 */
public class ConditionPendantDureeDAttaque extends Condition {
	
	/**
	 * Constructeur vide
	 */
	public ConditionPendantDureeDAttaque() {
		
	}
	
	@Override
	public final boolean estVerifiee() {
		final int animationAttaqueActuelle = this.page.event.map.heros.animationAttaque;
		final Arme armeActuelle = Fenetre.getPartieActuelle().getArmeEquipee();
		if(armeActuelle!=null){
			return animationAttaqueActuelle >= armeActuelle.frameDebutCoup && animationAttaqueActuelle <= armeActuelle.frameFinCoup;
		}else{
			return false;
		}
	}
	
	public final boolean estLieeAuHeros() {
		return true;
	}

}

