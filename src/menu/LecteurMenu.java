package menu;

import java.awt.image.BufferedImage;

import main.Fenetre;
import main.GestionClavier;
import main.Lecteur;
import map.LecteurMap;

public class LecteurMenu extends Lecteur{
	public Menu menu;
	public LecteurMap lecteurMapMemorise;
	
	public LecteurMenu(Fenetre fenetre, LecteurMap lecteurMapMemorise){
		this.fenetre = fenetre;
		this.lecteurMapMemorise = lecteurMapMemorise;
		this.allume = true;
		
	}
	
	public BufferedImage calculerAffichage(){
		BufferedImage image = ecranNoir();
		//affichage des éléments de menu
		for(ElementDeMenu element : menu.elements){
			image = superposerImages(image,element.image,element.x,element.y);
		}
		//affichage de la sélection
		Selectionnable selectionnable = menu.elementSelectionne;
		if(selectionnable!=null && selectionnable.selectionnable && selectionnable.selectionne){
			BufferedImage selection = selectionnable.creerImageDeSelection();
			image = superposerImages(image, selection, selectionnable.x-ElementDeMenu.contour, selectionnable.y-ElementDeMenu.contour);
		}

		//affichage des textes
		for(Texte texte : menu.textes){
			BufferedImage imgtxt = texte.texteToImage();			
			image = superposerImages(image,imgtxt,texte.x,texte.y);
		}
		return image;
	}

	@Override
	public void keyPressed(int keycode) {
		switch(keycode){
			case GestionClavier.ToucheRole.ACTION : menu.confirmer(); break;
			case GestionClavier.ToucheRole.HAUT : menu.selectionnerElementEnHaut(); break; //z
			case GestionClavier.ToucheRole.GAUCHE : menu.selectionnerElementAGauche(); break; //q
			case GestionClavier.ToucheRole.BAS : menu.selectionnerElementEnBas(); break; //s
			case GestionClavier.ToucheRole.DROITE : menu.selectionnerElementADroite(); break; //d
			case GestionClavier.ToucheRole.RETOUR : menu.quitter(); break; //o
			default : break;
		}
	}
	
	public void changerMenu(Menu nouveauMenu){
		this.menu = nouveauMenu;
		nouveauMenu.lecteur = this;
		this.fenetre.futurLecteur = this;
		this.allume = false;
	}

	@Override
	public void keyReleased(Integer keycode) {
		// TODO Auto-generated method stub
		
	}
	
}
