package menu;

import main.Fenetre;

public class QuitterJeu extends ComportementElementDeMenu{
	
	public QuitterJeu(){}
	
	@Override
	public void executer() {
		Fenetre fenetre = this.element.menu.lecteur.fenetre;
		fenetre.dispose();
	}

}
