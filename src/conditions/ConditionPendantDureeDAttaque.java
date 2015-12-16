package conditions;

import main.Arme;
import main.Partie;

/**
 * Si l'attaque en est au moment du coup donné.
 * En effet, toutes les images de l'animation d'attaque ne correspondent pas au coup donné.
 */
public class ConditionPendantDureeDAttaque extends Condition{
	
	public ConditionPendantDureeDAttaque(){}
	
	@Override
	public Boolean estVerifiee() {
		int animationAttaqueActuelle = this.page.event.map.heros.animationAttaque;
		Arme armeActuelle = Partie.getArmeEquipee();
		Boolean reponse = animationAttaqueActuelle >= armeActuelle.frameDebutCoup && animationAttaqueActuelle <= armeActuelle.frameFinCoup;
		return reponse;
	}

}

