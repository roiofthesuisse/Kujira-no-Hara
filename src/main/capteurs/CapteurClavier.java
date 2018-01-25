package main.capteurs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import main.Main;
import utilitaire.GestionClavier;

/**
 * Actions à effectuer quand le Clavier est utilisé.
 */
public class CapteurClavier implements KeyListener {
	
	/**
	 * Constructeur explicite
	 */
	public CapteurClavier() { }

	@Override
	public final void keyPressed(final KeyEvent event) {
		final Integer keycode = event.getKeyCode();
		//this.mesuresDePerformance.add("press;"+keycode+";"+System.currentTimeMillis());
		final GestionClavier.ToucheRole touchePressee = GestionClavier.ToucheRole.getToucheRole(keycode);
		if (touchePressee != null && !touchePressee.enfoncee()) {
			touchePressee.enfoncee(true);
			touchePressee.frameDAppui((Integer) Main.lecteur.frameActuelle); // mémorisation de la frame d'appui
			
			Main.lecteur.keyPressed(touchePressee);
		}
	}

	@Override
	public final void keyReleased(final KeyEvent event) {
		final Integer keycode = event.getKeyCode();
		//this.mesuresDePerformance.add("release;"+keycode+";"+System.currentTimeMillis());
		final GestionClavier.ToucheRole toucheRelachee = GestionClavier.ToucheRole.getToucheRole(keycode);
		
		if (toucheRelachee != null && toucheRelachee.enfoncee()) {
			toucheRelachee.enfoncee(false);
			toucheRelachee.frameDAppui(null);
			
			Main.lecteur.keyReleased(toucheRelachee);
		}
	}

	@Override
	public void keyTyped(final KeyEvent event) {
		//rien
	}
}
