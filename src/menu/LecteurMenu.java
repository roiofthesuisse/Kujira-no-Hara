package menu;

import java.awt.image.BufferedImage;

import main.Fenetre;
import main.Lecteur;

public class LecteurMenu extends Lecteur{
	public Menu menu;
	
	public LecteurMenu(Fenetre fenetre){
		this.fenetre = fenetre;
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
			case 32 : menu.confirmer(); break; //espace
			case 10 : menu.confirmer(); break; //entrée
			case 90 : menu.haut(); break; //z
			case 81 : menu.gauche(); break; //q
			case 83 : menu.bas(); break; //s
			case 68 : menu.droite(); break; //d
			/*case 38 : menu.haut(); break;
			case 37 : menu.gauche(); break;
			case 40 : menu.bas(); break;
			case 39 : menu.droite(); break;*/
			case 79 : menu.quitter(); break; //o
			case 75 : menu.confirmer(); break; //k
			default : break;
		}
	}
	
	public void changerMenu(Menu nouveauMenu){
		this.menu = nouveauMenu;
		nouveauMenu.lecteur = this;
		Fenetre.futurLecteur = this;
		this.allume = false;
	}

	@Override
	public void keyReleased(Integer keycode) {
		// TODO Auto-generated method stub
		
	}
	
}
