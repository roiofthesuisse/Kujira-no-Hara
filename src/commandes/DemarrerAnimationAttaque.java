package commandes;

import java.util.ArrayList;

import jeu.Arme;
import son.LecteurAudio;
import main.Commande;
import main.Fenetre;
import map.Heros;
import map.PageEvent;

/**
 * Lancer l'animation d'attaque du Heros
 */
public class DemarrerAnimationAttaque implements CommandeEvent {
	private PageEvent page;
	
	/**
	 * Constructeur vide
	 */
	public DemarrerAnimationAttaque() {
		
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<? extends Commande> commandes) {
		if (this.page == null) {
			System.out.println("page nulle");
		}
		final Heros heros = this.page.event.map.heros;
		final Arme armeActuelle = Fenetre.getPartieActuelle().getArmeEquipee();
		heros.animationAttaque = armeActuelle.framesDAnimation.length;
		LecteurAudio.playSe(armeActuelle.nomEffetSonoreAttaque);
		return curseurActuel+1;
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
