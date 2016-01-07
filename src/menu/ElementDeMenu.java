package menu;

import java.awt.image.BufferedImage;

public class ElementDeMenu extends Selectionnable{
	
	public ElementDeMenu(BufferedImage image, int x, int y, Boolean selectionnable, Menu menu){
		this.menu = menu;
		this.image = image;
		this.x = x;
		this.y = y;
		this.selectionnable = selectionnable;
		this.selectionne = false;
	}
	
	public void executerLeComportementALArrivee(){
		this.selectionne = true;
	}
	
	public void comportementSiTouchePressee(Integer keycode){
		
	}
}
