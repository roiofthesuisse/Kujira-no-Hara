package menu;

import java.awt.Color;
import java.awt.image.BufferedImage;

import main.Lecteur;

public abstract class Selectionnable {
	public Menu menu;
	public static int contour = 16;
	public Boolean selectionnable = true;
	public Boolean selectionne = false;
	public ComportementElementDeMenu comportementSelection;
	public ComportementElementDeMenu comportementConfirmation;
	public BufferedImage image;
	public int x;
	public int y;
	
	public abstract void comportementALArrivee();
	
	public BufferedImage creerImageDeSelection(){
		int largeur = image.getWidth()+2*ElementDeMenu.contour;
		int hauteur = image.getHeight()+2*ElementDeMenu.contour;
		BufferedImage selection = new BufferedImage(largeur,hauteur,Lecteur.imageType);
		for(int i=0; i<largeur; i++){
			for(int j=0; j<hauteur; j++){
				int r,g,b,a, r1,r2,g1,g2,b1,b2,a1,a2;
				r=0;g=0;b=0;a=0;
				r1=255;g1=255;b1=120;a1=175; //couleur au centre de la sélection
				r2=255;g2=150;b2=0;a2=0; //couleur à l'extérieur de la sélection
				double rate=0.0, hypotenuse=0.0;
				//calcul du taux "rate" d'éloignement avec le centre de la sélection
				if(i>=ElementDeMenu.contour && i<=largeur-ElementDeMenu.contour){
					//centre centre
					if(j>=ElementDeMenu.contour && j<=hauteur-ElementDeMenu.contour){
						rate=1.0;
					}
					//centre haut
					if(j<ElementDeMenu.contour){
						rate=(double) (j) / (double) (ElementDeMenu.contour);
					}
					//centre bas
					if(j>hauteur-ElementDeMenu.contour){
						rate=(double) (hauteur-j) / (double) (ElementDeMenu.contour);
					}
				}else{
					if(i<ElementDeMenu.contour){
						//gauche centre
						if(j>=ElementDeMenu.contour && j<=hauteur-ElementDeMenu.contour){
							rate=(double) (i) / (double) (ElementDeMenu.contour);
						}
						//gauche haut
						if(j<ElementDeMenu.contour){
							hypotenuse=Math.sqrt( Math.pow(i-ElementDeMenu.contour,2) + Math.pow(j-ElementDeMenu.contour,2) );
						}
						//gauche bas
						if(j>hauteur-ElementDeMenu.contour){
							hypotenuse=Math.sqrt( Math.pow(i-ElementDeMenu.contour,2) + Math.pow(j-(hauteur-ElementDeMenu.contour),2) );
						}
					}else{
						if(i>largeur-ElementDeMenu.contour){
							//droite centre
							if(j>=ElementDeMenu.contour && j<=hauteur-ElementDeMenu.contour){
								rate=(double) (largeur-i) / (double) (ElementDeMenu.contour);
							}
							//droite haut
							if(j<ElementDeMenu.contour){
								hypotenuse=Math.sqrt( Math.pow(i-(largeur-ElementDeMenu.contour),2) + Math.pow(j-ElementDeMenu.contour,2) );
							}
							//droite bas
							if(j>hauteur-ElementDeMenu.contour){
								hypotenuse=Math.sqrt( Math.pow(i-(largeur-ElementDeMenu.contour),2) + Math.pow(j-(hauteur-ElementDeMenu.contour),2) );
							}
						}
					}
				}
				if(hypotenuse!=0){
					if(hypotenuse>ElementDeMenu.contour){
						rate=0;
					}else{
						rate=1.0-hypotenuse/(double)contour;
					}
				}
				//calcul de la couleur en fonction du taux "rate" d'éloignement du centre de la sélection
				r=(int) (r1*rate+r2*(1-rate));g=(int) (g1*rate+g2*(1-rate));b=(int) (b1*rate+b2*(1-rate));a=(int) (a1*rate+a2*(1-rate));
				Color couleur = new Color(r,g,b,a);
				selection.setRGB(i, j, couleur.getRGB());
			}
		}
		return selection;
	}
	
	public void confirmer(){
		if(this.comportementConfirmation != null){
			this.comportementConfirmation.executer();
		}
	}
}
