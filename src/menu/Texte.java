package menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import main.Lecteur;

public class Texte extends Selectionnable{
	public String contenu;
	public int taille;
	private static int tailleMoyenne = 18;
	public static int interligne = 8;
	public int opacite;
	public static Color couleurParDefaut = new Color(50,0,150);
	public static Color couleurParDefaut2 = new Color(150,0,50);
	public static int margeDroite = 4; //pour ne pas que le côté droit du texte ne soit coupé

	/**
	 * @param contenu
	 * @param xDebut
	 * @param yDebut
	 * @param selectionnable
	 * @param c1
	 * @param c2
	 * @param menu
	 */
	public Texte(String contenu, int xDebut, int yDebut, Boolean selectionnable, ComportementElementDeMenu c1, ComportementElementDeMenu c2, Menu menu){
		this(contenu, xDebut, yDebut, tailleMoyenne, selectionnable, 100, c1, c2, menu);
	}
	
	/**
	 * @param contenu
	 * @param xDebut
	 * @param yDebut
	 * @param selectionnable
	 * @param opacite
	 * @param c1
	 * @param c2
	 * @param menu
	 */
	public Texte(String contenu, int xDebut, int yDebut, Boolean selectionnable, int opacite, ComportementElementDeMenu c1, ComportementElementDeMenu c2, Menu menu){
		this(contenu, xDebut, yDebut, tailleMoyenne, selectionnable, opacite, c1, c2, menu);
	}
	
	/**
	 * @param contenu
	 * @param xDebut
	 * @param yDebut
	 * @param taille
	 * @param selectionnable
	 * @param opacite
	 * @param c1
	 * @param c2
	 * @param menu
	 */
	public Texte(String contenu, int xDebut, int yDebut, int taille, Boolean selectionnable, int opacite, ComportementElementDeMenu c1, ComportementElementDeMenu c2, Menu menu){
		this.menu = menu;
		this.selectionnable = selectionnable;
		this.comportementSelection = c1;
		if(comportementSelection!=null) comportementSelection.element = this;
		this.comportementConfirmation = c2;
		if(comportementConfirmation!=null) comportementConfirmation.element = this;
		this.contenu = contenu;
		this.x = xDebut;
		this.y = yDebut;
		this.taille = taille;
		this.opacite = opacite;
		this.image = texteToImage();
	}
	
	/**
	 * à ne pas utiliser pour les menus !
	 * pour les messages d'events seulement !!!
	 */
	public Texte(String contenu){
		this.contenu = contenu;
	}
	
	public BufferedImage texteToImage(){
        String[] texts = this.contenu.split("\\\\n");
        int nombreLignes = texts.length;

        BufferedImage img = new BufferedImage(1, 1, Lecteur.imageType);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font("Arial", Font.PLAIN, tailleMoyenne);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(texts[0])+margeDroite;
        int height = fm.getHeight();
        g2d.dispose();
        
        int hauteurTotaleImage = (height+interligne)*nombreLignes-interligne;
        img = new BufferedImage(width, hauteurTotaleImage, Lecteur.imageType);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(couleurParDefaut);
        for(int i=0; i<nombreLignes; i++){ //on écrit chaque ligne
        	int largeurALaquelleOnEcrit = 0;
        	int hauteurALaquelleOnEcrit = i*(tailleMoyenne+interligne)+fm.getAscent();
        	String ligne = texts[i].trim();
        	String[] boutsDeLigne = ligne.split("\\\\c",-1);
        	int nombreDeBoutsDeLigne = boutsDeLigne.length;
        	for(int j=0; j<nombreDeBoutsDeLigne; j++){
        		if( boutsDeLigne[j].startsWith("[02]") ){
        			boutsDeLigne[j] = boutsDeLigne[j].replace("[02]", "");
	        		g2d.setColor(couleurParDefaut2); 
	        		largeurALaquelleOnEcrit+=fm.stringWidth(boutsDeLigne[j-1]);
        		}else if( boutsDeLigne[j].startsWith("[01]") ){
        			boutsDeLigne[j] = boutsDeLigne[j].replace("[01]", "");
	        		g2d.setColor(couleurParDefaut); 
	        		largeurALaquelleOnEcrit+=fm.stringWidth(boutsDeLigne[j-1]);
        		}
        		g2d.drawString(boutsDeLigne[j], largeurALaquelleOnEcrit, hauteurALaquelleOnEcrit);
        	}
        }
        g2d.dispose();
        
        return img;
	}

	@Override
	public void comportementALArrivee() { //lorsque la sélection arrive sur ce texte
		if( comportementSelection!=null ){
			comportementSelection.executer();
		}
	}
	
}
