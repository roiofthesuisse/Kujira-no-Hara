package menu;

import main.Fenetre;
import main.Partie;

public class ChargerPartie extends ComportementElementDeMenu {
	private final int numeroDeSauvegarde;
	
	public ChargerPartie(int numeroDeSauvegarde){
		this.numeroDeSauvegarde = numeroDeSauvegarde;
	}
	
	@Override
	public void executer() {
		Fenetre fenetre = this.element.menu.lecteur.fenetre;
		System.out.println("nouvelle partie");
		fenetre.partie = Partie.chargerPartie(this.numeroDeSauvegarde);
		fenetre.ouvrirLaPartie();
	}

}
