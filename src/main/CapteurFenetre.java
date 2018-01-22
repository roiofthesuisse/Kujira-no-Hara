package main;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import utilitaire.son.LecteurAudio;

public class CapteurFenetre implements WindowFocusListener {

	public CapteurFenetre() {
		
	}
	
	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		LecteurAudio.redemarrerToutesLesMusiques();
	}

	@Override
	public void windowLostFocus(WindowEvent arg0) {
		LecteurAudio.mettreEnPauseToutesLesMusiques();
	}

}
