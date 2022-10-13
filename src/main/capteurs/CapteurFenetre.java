package main.capteurs;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Main;
import utilitaire.son.LecteurAudio;

/**
 * Actions a effectuer lorsque la Fen�tre du jeu est modifi�e.
 */
public class CapteurFenetre extends WindowAdapter {
	private static final Logger LOG = LogManager.getLogger(CapteurFenetre.class);
	
	/**
	 * Constructeur explicite
	 */
	public CapteurFenetre() { }
	
	@Override
	public final void windowGainedFocus(final WindowEvent arg0) {
		LOG.info("Fen�tre r�activ�e");
		LecteurAudio.redemarrerToutesLesMusiques();
	}

	@Override
	public final void windowLostFocus(final WindowEvent arg0) {
		LOG.info("Fen�tre d�sactiv�e");
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
