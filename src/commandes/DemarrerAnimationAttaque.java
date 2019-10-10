package commandes;

import java.util.HashMap;
import java.util.List;

import jeu.Arme;
import main.Commande;
import map.Heros;
import utilitaire.son.LecteurAudio;

/**
 * Lancer l'animation d'attaque du Heros
 */
public class DemarrerAnimationAttaque extends Commande implements CommandeEvent {

	/**
	 * Constructeur vide
	 */
	public DemarrerAnimationAttaque() {

	}

	/**
	 * Constructeur générique
	 * 
	 * @param parametres liste de paramètres issus de JSON
	 */
	public DemarrerAnimationAttaque(final HashMap<String, Object> parametres) {
		this();
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		final Heros heros = this.page.event.map.heros;
		// pas d'attaque si Mouvement forcé en cours
		if (heros.deplacementForce == null || heros.deplacementForce.mouvements.size() <= 0) {
			final Arme armeActuelle = getPartieActuelle().getArmeEquipee();
			heros.animationAttaque = armeActuelle.framesDAnimation.length;
			LecteurAudio.playSe(armeActuelle.nomEffetSonoreAttaque);
		}
		return curseurActuel + 1;
	}

}