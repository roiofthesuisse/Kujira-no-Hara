package menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import main.Commande;
import main.Fenetre;
import utilitaire.graphismes.Graphismes;

/**
 * Un Texte peut être sélectionnable, avoir un comportement au survol et à la confirmation.
 */
public class Texte extends ElementDeMenu {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Texte.class);
	private static final Color COULEUR_PAR_DEFAUT = new Color(50, 0, 150);
	private static final Color COULEUR_PAR_DEFAUT2 = new Color(150, 0, 50);
	public static final int MARGE_A_DROITE = 4; //pour ne pas que le côté droit du texte ne soit coupé
	
	/**
	 * Taille de la police
	 */
	public enum Taille {
		MOYENNE("MOYENNE", 18), GRANDE("GRANDE", 28);
		
		public final String nom;
		public final int pixels;
		
		/** 
		 * Constructeur explicite
		 * @param nom désignant cette taille
		 * @param pixels hauteur de la police en pixels
		 */
		Taille(final String nom, final int pixels) {
			this.nom = nom;
			this.pixels = pixels;
		}
		
		/**
		 * Obtenir un objet Taille à partir de son nom.
		 * @param nom désignant une Taille
		 * @return Taille correspondante
		 */
		public static Taille getTailleParNom(final String nom) {
			for (Taille taille : Taille.values()) {
				if (taille.nom.equals(nom)) {
					return taille;
				}
			}
			return Taille.MOYENNE;
		}
	}
	
	public static final int TAILLE_MOYENNE = 18;
	public static final int TAILLE_GRANDE = 28;
	
	
	public static final int INTERLIGNE = 8;
	private static final int OPACITE_MAXIMALE = 100;
	private static final String POLICE = "arial"; //"roiofthesuisse";
	
	public ArrayList<String> contenu;
	public int taille;
	public int opacite;
	public Color couleurForcee = null;
	/** Nombre maximal de caractères d'une ligne */
	private int largeurMaximale;

	/**
	 * Constructeur implicite (opacité maximale, taille moyenne) pour les Menus
	 * @param contenu du Texte (dans plusieurs langues)
	 * @param xDebut position x à l'écran du coin haut-gauche du Texte
	 * @param yDebut position y à l'écran du coin haut-gauche du Texte
	 * @param selectionnable est-il sélectionnable dans le cadre d'un Menu ?
	 * @param c1 c1 comportement au survol
	 * @param c2 c2 comportement à la confirmation
	 * @param id identifiant de l'ElementDeMenu
	 */
	public Texte(final ArrayList<String> contenu, final int xDebut, final int yDebut, final boolean selectionnable, final ArrayList<Commande> c1, final ArrayList<Commande> c2, final int id) {
		this(contenu, xDebut, yDebut, Taille.MOYENNE, -1, selectionnable, Texte.OPACITE_MAXIMALE, c1, c2, id);
	}
	
	/**
	 * Constructeur implicite (opacité maximale) pour les Menus
	 * @param contenu du Texte (dans plusieurs langues)
	 * @param xDebut position x à l'écran du coin haut-gauche du Texte
	 * @param yDebut position y à l'écran du coin haut-gauche du Texte
	 * @param taille de la police
	 * @param largeurMaximale limite de caractères d'une ligne
	 * @param selectionnable est-il sélectionnable dans le cadre d'un Menu ?
	 * @param c1 comportement au survol
	 * @param c2 comportement à la confirmation
	 * @param id identifiant de l'ElementDeMenu
	 */
	public Texte(final ArrayList<String> contenu, final int xDebut, final int yDebut, final Taille taille, final int largeurMaximale, final boolean selectionnable, final ArrayList<Commande> c1, final ArrayList<Commande> c2, final int id) {
		this(contenu, xDebut, yDebut, taille, largeurMaximale, selectionnable, OPACITE_MAXIMALE, c1, c2, id);
	}
	
	/**
	 * Constructeur explicite pour les Menus
	 * @param contenu du Texte (dans plusieurs langues)
	 * @param xDebut position x à l'écran du coin haut-gauche du Texte
	 * @param yDebut position y à l'écran du coin haut-gauche du Texte
	 * @param taille des caractères
	 * @param largeurMaximale limite de caractères d'une ligne
	 * @param selectionnable est-il sélectionnable dans le cadre d'un Menu ?
	 * @param opacite transparence
	 * @param c1 comportement au survol
	 * @param c2 comportement à la confirmation
	 * @param id identifiant de l'ElementDeMenu
	 */
	public Texte(final ArrayList<String> contenu, final int xDebut, final int yDebut, final Taille taille, final int largeurMaximale, final boolean selectionnable, final int opacite, final ArrayList<Commande> c1, final ArrayList<Commande> c2, final int id) {
		super(id, selectionnable, xDebut, yDebut, c1, c2, null);
		
		if (comportementSurvol!=null && comportementSurvol.size()>0) {
			for (Commande commande : comportementSurvol) {
				commande.element = this;
			}
		}
		if (comportementConfirmation!=null && comportementConfirmation.size()>0) {
			for (Commande commande : comportementConfirmation) {
				commande.element = this;
			}
		}
		this.contenu = contenu;
		this.x = xDebut;
		this.y = yDebut;
		this.taille = taille.pixels;
		this.largeurMaximale = largeurMaximale;
		this.opacite = opacite;
	}
	
	/**
	 * Constructeur implicite pour les Messages d'un Event
	 * @param contenu du Texte (dans plusieurs langues)
	 */
	public Texte(final ArrayList<String> contenu) {
		this(contenu, COULEUR_PAR_DEFAUT);
	}
	
	/**
	 * Constructeur explicite pour les Messages d'un Event
	 * @param contenu du Texte (dans plusieurs langues)
	 * @param couleurForcee pour avoir un texte d'une autre couleur que celle par défaut
	 */
	public Texte(final ArrayList<String> contenu, final Color couleurForcee) {
		super(0, false, 0, 0, null, null, null); //on se fout de la gueule de la classe mère
		
		this.couleurForcee = couleurForcee;
		this.contenu = contenu;
		this.taille = Texte.TAILLE_MOYENNE;
		this.opacite = Texte.OPACITE_MAXIMALE;
	}
	
	/**
	 * Convertir un Texte en image
	 * @return le Texte sous forme d'image
	 */
	public final BufferedImage texteToImage() {
		final int langue = Fenetre.langue;
		String texteAAfficher = this.contenu.get(langue < this.contenu.size() ? langue : 0);
		
		//mot de passe
		final Matcher matcher = Pattern.compile("\\\\m\\[[0-9]+\\]").matcher(texteAAfficher);
		while (matcher.find()) {
			final int numeroMot = Integer.parseInt( texteAAfficher.substring(matcher.start()+3, matcher.end()-1) );
			final String mot = Fenetre.getPartieActuelle().mots[numeroMot];
			texteAAfficher = texteAAfficher.replace("\\m["+numeroMot+"]", mot != null ? mot : "");
		}
		
		//découpage en lignes
        String[] texts = texteAAfficher.split("\\\\n");
        int nombreLignes = texts.length;
        if (texts.length <= 0) {
        	return null;
        }
        
        //largeur maximale des lignes
        if (this.largeurMaximale > 0) {
        	ArrayList<String> textesListe = new ArrayList<String>();
        	for (String ligne : texts) {
        		textesListe.add(ligne);
        	}
        	
        	int nombreDeLignes = textesListe.size();
	        for (int i = 0; i<nombreDeLignes; i++) {
	        	String ligne = textesListe.get(i);
	        	if (ligne.length() > this.largeurMaximale) {
	        		int coupure = couper(ligne);
	        		textesListe.set(i, ligne.substring(0, coupure));
	        		textesListe.add(i+1, ligne.substring(coupure+1));
	        		nombreDeLignes++;
	        		LOG.debug("La ligne \""+ligne+"\" a été scindée : "+textesListe.get(i)+"|"+textesListe.get(i+1));
	        	}
	        }
	        
	        texts = textesListe.toArray(texts);
	        nombreLignes = texts.length;
        }
        
        //calcul de la taille du Texte
        BufferedImage img = new BufferedImage(1, 1, Graphismes.TYPE_DES_IMAGES);
        Graphics2D g2d = img.createGraphics();
        final Font font = new Font(POLICE, Font.PLAIN, this.taille);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int largeurDesLignes = 0;
        int largeurDeCetteLigne;
        for (int i = 0; i<texts.length; i++) {
        	largeurDeCetteLigne = fm.stringWidth(texts[i])+Texte.MARGE_A_DROITE;
        	if (largeurDeCetteLigne > largeurDesLignes) {
        		largeurDesLignes = largeurDeCetteLigne;
        	}
        }
        final int hauteurDesLignes = fm.getHeight();
        g2d.dispose();
        final int hauteurTotaleImage = (hauteurDesLignes + Texte.INTERLIGNE)*nombreLignes - Texte.INTERLIGNE; //on retire l'interligne inutile tout à la fin
        img = new BufferedImage(largeurDesLignes, hauteurTotaleImage, Graphismes.TYPE_DES_IMAGES);
        
        //réglage de la police
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
        
        //si aucune couleur forcée, couleur par défaut
        if (this.couleurForcee == null) {
        	g2d.setColor(Texte.COULEUR_PAR_DEFAUT);
        } else {
        	g2d.setColor(this.couleurForcee);
        }
        
        //on écrit chaque ligne
        for (int i = 0; i<nombreLignes; i++) { 
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
	    
        LOG.trace("Texte transformé en image : "+texteAAfficher);
        return img;
	}
	
	/**
	 * Couper une ligne trop longue.
	 * @param ligne à couper
	 * @return lieu de coupure
	 */
	private int couper(final String ligne) {
		int i = this.largeurMaximale;
		boolean onATrouveLeLieuDeCoupure = false;
		while (!onATrouveLeLieuDeCoupure && i>0) {
			if(ligne.charAt(i) == ' '){
				onATrouveLeLieuDeCoupure = true;
				return i;
			}
			i--;
		}
		return this.largeurMaximale;
	}
	
	@Override
	public final BufferedImage getImage() {
		if (this.image == null) {
			this.image = this.texteToImage();
			this.largeur = this.image.getWidth();
			this.hauteur = this.image.getHeight();
		}
		return this.image;
	}

	/**
	 * Provoquer un recalcul de l'image la prochaine fois qu'elle est utile.
	 */
	public void actualiserImage() {
		this.image = null;
	}
	
	/**
	 * Séparer les langues dans une liste.
	 * @param o soit le texte dans une langue unique au format String, soit le texte multilingue ArrayList<String>
	 * @return liste du texte dans chaque langue
	 */
	public static ArrayList<String> construireTexteMultilingue(final Object o) {
		final ArrayList<String> resultat = new ArrayList<String>();
		try {
			// Texte monolingue
			final String texteUnique = (String) o;
			resultat.add(texteUnique);
		} catch (Exception e) {
			// Texte multilingue
			final JSONArray jsonTexteMulti = (JSONArray) o;
			for (Object texteDansUneLangue : jsonTexteMulti) {
				resultat.add((String) texteDansUneLangue);
			}
		}
		return resultat;
	}
	
}
