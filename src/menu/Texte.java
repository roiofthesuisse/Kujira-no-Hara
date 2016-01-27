package menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import main.Lecteur;

/**
 * Un Texte peut être sélectionnable, avoir un comportement au survol et à la confirmation.
 */
public class Texte extends Selectionnable {
	//constantes
	private static final Color COULEUR_PAR_DEFAUT = new Color(50, 0, 150);
	private static final Color COULEUR_PAR_DEFAUT2 = new Color(150, 0, 50);
	public static final int MARGE_A_DROITE = 4; //pour ne pas que le côté droit du texte ne soit coupé
	public static final int TAILLE_MOYENNE = 18;
	public static final int TAILLE_GRANDE = 28;
	private static final int INTERLIGNE = 8;
	private static final int OPACITE_MAXIMALE = 100;
	
	public String contenu;
	public int taille;
	public int opacite;

	/**
	 * Constructeur implicite (opacité maximale, taille moyenne) pour les Menus
	 * @param contenu du Texte
	 * @param xDebut position x à l'écran du coin haut-gauche du Texte
	 * @param yDebut position y à l'écran du coin haut-gauche du Texte
	 * @param selectionnable est-il sélectionnable dans le cadre d'un Menu ?
	 * @param c1 c1 comportement au survol
	 * @param c2 c2 comportement à la confirmation
	 * @param menu auquel le Texte appartient
	 */
	public Texte(final String contenu, final int xDebut, final int yDebut, final boolean selectionnable, final ComportementElementDeMenu c1, final ComportementElementDeMenu c2, final Menu menu) {
		this(contenu, xDebut, yDebut, Texte.TAILLE_MOYENNE, selectionnable, Texte.OPACITE_MAXIMALE, c1, c2, menu);
	}
	
	/**
	 * Constructeur implicite (opacité maximale) pour les Menus
	 * @param contenu du Texte
	 * @param xDebut position x à l'écran du coin haut-gauche du Texte
	 * @param yDebut position y à l'écran du coin haut-gauche du Texte
	 * @param selectionnable est-il sélectionnable dans le cadre d'un Menu ?
	 * @param opacite transparence
	 * @param c1 comportement au survol
	 * @param c2 comportement à la confirmation
	 * @param menu auquel le Texte appartient
	 */
	public Texte(final String contenu, final int xDebut, final int yDebut, final int taille, final boolean selectionnable, final ComportementElementDeMenu c1, final ComportementElementDeMenu c2, final Menu menu) {
		this(contenu, xDebut, yDebut, taille, selectionnable, OPACITE_MAXIMALE, c1, c2, menu);
	}
	
	/**
	 * Constructeur explicite pour les Menus
	 * @param contenu du Texte
	 * @param xDebut position x à l'écran du coin haut-gauche du Texte
	 * @param yDebut position y à l'écran du coin haut-gauche du Texte
	 * @param taille des caractères
	 * @param selectionnable est-il sélectionnable dans le cadre d'un Menu ?
	 * @param opacite transparence
	 * @param c1 comportement au survol
	 * @param c2 comportement à la confirmation
	 * @param menu auquel le Texte appartient
	 */
	public Texte(final String contenu, final int xDebut, final int yDebut, final int taille, final boolean selectionnable, final int opacite, final ComportementElementDeMenu c1, final ComportementElementDeMenu c2, final Menu menu) {
		this.menu = menu;
		this.selectionnable = selectionnable;
		this.comportementSelection = c1;
		if (comportementSelection!=null) {
			comportementSelection.element = this;
		}
		this.comportementConfirmation = c2;
		if (comportementConfirmation!=null) {
			comportementConfirmation.element = this;
		}
		this.contenu = contenu;
		this.x = xDebut;
		this.y = yDebut;
		this.taille = taille;
		this.opacite = opacite;
		this.image = texteToImage();

		this.largeur = this.image.getWidth();
		this.hauteur = this.image.getHeight();
	}
	
	/**
	 * Constructeur explicite pour les Messages d'un Event
	 * @param contenu du Texte
	 */
	public Texte(final String contenu) {
		this.contenu = contenu;
		this.taille = Texte.TAILLE_MOYENNE;
		this.opacite = Texte.OPACITE_MAXIMALE;
	}
	
	/**
	 * Convertir un Texte en image
	 * @return le Texte sous forme d'image
	 */
	public final BufferedImage texteToImage() {
        final String[] texts = this.contenu.split("\\\\n");
        final int nombreLignes = texts.length;
        if (nombreLignes <= 0) {
        	return null;
        }
        
        BufferedImage img = new BufferedImage(1, 1, Lecteur.TYPE_DES_IMAGES);
        Graphics2D g2d = img.createGraphics();
        final Font font = new Font("Arial", Font.PLAIN, this.taille);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        final int width = fm.stringWidth(texts[0])+Texte.MARGE_A_DROITE;
        final int height = fm.getHeight();
        g2d.dispose();
        
        final int hauteurTotaleImage = (height + Texte.INTERLIGNE)*nombreLignes - Texte.INTERLIGNE; //on retire l'interligne inutile tout à la fin
        img = new BufferedImage(width, hauteurTotaleImage, Lecteur.TYPE_DES_IMAGES);
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
        g2d.setColor(Texte.COULEUR_PAR_DEFAUT);
        for (int i = 0; i<nombreLignes; i++) { //on écrit chaque ligne
        	int largeurALaquelleOnEcrit = 0;
        	final int hauteurALaquelleOnEcrit = i*(this.taille+Texte.INTERLIGNE) + fm.getAscent();
        	final String ligne = texts[i].trim();
        	final String[] boutsDeLigne = ligne.split("\\\\c", -1);
        	final int nombreDeBoutsDeLigne = boutsDeLigne.length;
        	for (int j = 0; j<nombreDeBoutsDeLigne; j++) {
        		if ( boutsDeLigne[j].startsWith("[02]") ) {
        			boutsDeLigne[j] = boutsDeLigne[j].replace("[02]", "");
	        		g2d.setColor(Texte.COULEUR_PAR_DEFAUT2); 
	        		largeurALaquelleOnEcrit += fm.stringWidth(boutsDeLigne[j-1]);
        		} else if ( boutsDeLigne[j].startsWith("[01]") ) {
        			boutsDeLigne[j] = boutsDeLigne[j].replace("[01]", "");
	        		g2d.setColor(Texte.COULEUR_PAR_DEFAUT); 
	        		largeurALaquelleOnEcrit += fm.stringWidth(boutsDeLigne[j-1]);
        		}
        		g2d.drawString(boutsDeLigne[j], largeurALaquelleOnEcrit, hauteurALaquelleOnEcrit);
        	}
        }
        g2d.dispose();
        
        return img;
	}

	@Override
	public final void executerLeComportementALArrivee() { //lorsque la sélection arrive sur ce texte
		if ( comportementSelection!=null ) {
			comportementSelection.executer();
		}
	}
	
}
