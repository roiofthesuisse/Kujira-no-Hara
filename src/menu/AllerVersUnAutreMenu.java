package menu;

public class AllerVersUnAutreMenu extends ComportementElementDeMenu{
	public Menu nouveauMenu;
	
	public AllerVersUnAutreMenu(Menu nouveauMenu){
		this.nouveauMenu = nouveauMenu;
	}
	
	@Override
	public void executer() {
		this.element.menu.lecteur.changerMenu(nouveauMenu);
	}

}
