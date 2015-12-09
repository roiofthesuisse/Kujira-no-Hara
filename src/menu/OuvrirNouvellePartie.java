package menu;

import main.Fenetre;

public class OuvrirNouvellePartie extends ComportementElementDeMenu{
	
	@Override
	public void executer() {
		Fenetre fenetre = this.element.menu.lecteur.fenetre;
		System.out.println("nouvelle partie");
		fenetre.partie = null;
		fenetre.ouvrirLaPartie();
	}
	
}
