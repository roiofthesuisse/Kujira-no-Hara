package conditions;

import commandes.CommandeEvent;
import jeu.Arme;
import main.Fenetre;
import map.PageEvent;

/**
 * Si l'attaque en est au moment du coup donné.
 * En effet, toutes les images de l'animation d'attaque ne correspondent pas au coup donné.
 */
public class ConditionPendantDureeDAttaque extends Condition implements CommandeEvent {
	private PageEvent page;
	
	/**
	 * Constructeur vide
	 */
	public ConditionPendantDureeDAttaque() {
		
	}
	
	@Override
	public final boolean estVerifiee() {
		final int animationAttaqueActuelle = ((CommandeEvent) this).getPage().event.map.heros.animationAttaque;
		final Arme armeActuelle = Fenetre.getPartieActuelle().getArmeEquipee();
		if (armeActuelle!=null) {
			return animationAttaqueActuelle >= armeActuelle.frameDebutCoup && animationAttaqueActuelle <= armeActuelle.frameFinCoup;
		} else {
			return false;
		}
	}
	
	/**
	 * C'est une Condition qui implique une proximité avec le Héros.
	 * En effet, elle sert de signal du coup donné par le Héros à un Event avec une Arme.
	 * @return true 
	 */
	public final boolean estLieeAuHeros() {
		return true;
	}

	@Override
	public final PageEvent getPage() {
		return this.page;
	}

	@Override
	public final void setPage(final PageEvent page) {
		this.page = page;
	}

}

