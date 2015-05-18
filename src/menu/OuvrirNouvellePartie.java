package menu;

public class OuvrirNouvellePartie extends ComportementElementDeMenu{
	
	public OuvrirNouvellePartie(){
		
	}
	
	@Override
	public void executer() {
		System.out.println("nouvelle partie");
		this.element.menu.lecteur.fenetre.partie = null;
		this.element.menu.lecteur.fenetre.ouvrirPartie();
	}
	
}
