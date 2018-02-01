package main.capteurs;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Main;
import utilitaire.son.LecteurAudio;

/**
 * Actions à effectuer lorsque la Fenêtre du jeu est modifiée.
 */
public class CapteurFenetre extends WindowAdapter {
	private static final Logger LOG = LogManager.getLogger(CapteurFenetre.class);
	
	/**
	 * Constructeur explicite
	 */
	public CapteurFenetre() { }
	
	@Override
	public final void windowGainedFocus(final WindowEvent arg0) {
		LOG.info("Fenêtre réactivée");
		LecteurAudio.redemarrerToutesLesMusiques();
	}

	@Override
	public final void windowLostFocus(final WindowEvent arg0) {
		LOG.info("Fenêtre désactivée");
		LecteurAudio.mettreEnPauseToutesLesMusiques();
	}
	
	@Override
	public final void windowClosing(final java.awt.event.WindowEvent windowEvent) {
		LOG.info("Fermeture manuelle du jeu");
		Main.quitterLeJeu = true;
		Main.futurLecteur = null;
		Main.lecteur.allume = false;
    }

}
