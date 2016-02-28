package commandes;

import java.util.ArrayList;

import jeu.Arme;
import son.LecteurAudio;
import main.Commande;
import main.Fenetre;
import map.Heros;

/**
 * Lancer l'animation d'attaque du Heros
 */
public class DemarrerAnimationAttaque extends Commande implements CommandeEvent {
	
	/**
	 * Constructeur vide
	 */
	public DemarrerAnimationAttaque() {
		
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		if (this.page == null) {
			System.out.println("page nulle");
		}
		final Heros heros = this.page.event.map.heros;
		final Arme armeActuelle = Fenetre.getPartieActuelle().getArmeEquipee();
		heros.animationAttaque = armeActuelle.framesDAnimation.length;
		LecteurAudio.playSe(armeActuelle.nomEffetSonoreAttaque);
		return curseurActuel+1;
	}

}
