package menu;

import java.awt.image.BufferedImage;

import main.Fenetre;
import main.Lecteur;
import map.LecteurMap;
import utilitaire.GestionClavier;

/**
 * Le Lecteur de Menu a pour rôle de produire l'image à afficher à l'écran s'il s'agit d'un Menu.
 */
public class LecteurMenu extends Lecteur {
	public Menu menu;
	public LecteurMap lecteurMapMemorise;
	
	/**
	 * Constructeur explicite
	 * @param fenetre dans laquelle on doit afficher le Menu
	 * @param menu que va lire ce Lecteur
	 * @param lecteurMapMemorise Lecteur de la Map sur laquelle on se trouvait avant d'entrer dans le Menu
	 */
	public LecteurMenu(final Fenetre fenetre, final Menu menu, final LecteurMap lecteurMapMemorise) {
		this.menu = menu;
		menu.lecteur = this; //on prévient le Lecteur qu'il a un Menu
		
		this.fenetre = fenetre;
		this.lecteurMapMemorise = lecteurMapMemorise;
		this.allume = true; //TODO est-ce utile ?
	}
	
	/**
	 * Constituer l'image de l'écran, avec tous les éléments du Menu
	 * @return écran
	 */
	public final BufferedImage calculerAffichage() {
		BufferedImage image = ecranNoir();
		//affichage des éléments de menu
		for (ElementDeMenu element : menu.elements) {
			image = superposerImages(image, element.image, element.x, element.y);
		}
		//affichage de la sélection
		final Selectionnable selectionnable = menu.elementSelectionne;
		if (selectionnable!=null && selectionnable.selectionnable && selectionnable.selectionne) {
			final BufferedImage selection = selectionnable.creerImageDeSelection();
			image = superposerImages(image, selection, selectionnable.x-ElementDeMenu.CONTOUR, selectionnable.y-ElementDeMenu.CONTOUR);
		}

		//affichage des textes
		for (Texte texte : menu.textes) {
			final BufferedImage imgtxt = texte.texteToImage();			
			image = superposerImages(image, imgtxt, texte.x, texte.y);
		}
		return image;
	}

	@Override
	public final void keyPressed(final Integer keycode) {
		switch(keycode) {
			case GestionClavier.ToucheRole.ACTION : menu.confirmer(); break;
			case GestionClavier.ToucheRole.HAUT : menu.selectionnerElementEnHaut(); break; //z
			case GestionClavier.ToucheRole.GAUCHE : menu.selectionnerElementAGauche(); break; //q
			case GestionClavier.ToucheRole.BAS : menu.selectionnerElementEnBas(); break; //s
			case GestionClavier.ToucheRole.DROITE : menu.selectionnerElementADroite(); break; //d
			case GestionClavier.ToucheRole.RETOUR : menu.quitter(); break; //o
			default : break;
		}
	}
	
	/**
	 * Ouvrir un autre Menu.
	 * @warning cette méthode ne doit être appelée que par le nouveau Lecteur !
	 */
	public final void changerMenu() {
		Fenetre.getFenetre().futurLecteur = this;
		Fenetre.getFenetre().lecteur.allume = false;
	}

	@Override
	public void keyReleased(final Integer keycode) {
		// TODO Auto-generated method stub
		
	}
	
}
