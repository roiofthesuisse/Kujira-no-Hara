package main.capteurs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import main.Fenetre;

/**
 * Actions à effectuer quand la souris est utilisée
 */
public class CapteurSouris implements MouseListener {
	
	/**
	 * Constructeur explicite
	 */
	public CapteurSouris() { }

	@Override
	public final void mouseClicked(final MouseEvent e) {
		if (e.getClickCount() == 2) {
			Fenetre.pleinEcran();
		}
	}

	@Override
	public void mouseEntered(final MouseEvent arg0) {
		//rien
	}

	@Override
	public void mouseExited(final MouseEvent arg0) {
		//rien
	}

	@Override
	public void mousePressed(final MouseEvent arg0) {
		//rien
	}

	@Override
	public void mouseReleased(final MouseEvent arg0) {
		//rien
	}

}
