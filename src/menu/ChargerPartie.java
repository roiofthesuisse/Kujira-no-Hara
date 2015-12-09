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
		System.out.println("chargement de la partie numero "+numeroDeSauvegarde);
		fenetre.partie = Partie.chargerPartie(this.numeroDeSauvegarde);
		fenetre.ouvrirLaPartie();
	}

}
