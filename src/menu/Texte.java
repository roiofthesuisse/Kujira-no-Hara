package menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import main.Commande;
import main.Main;
import utilitaire.InterpreteurDeJson;
import utilitaire.graphismes.Graphismes;

/**
 * Un Texte peut etre selectionnable, avoir un comportement au survol et a la confirmation.
 */
public class Texte extends ElementDeMenu {
	//constantes
	private static final Logger LOG = LogManager.getLogger(Texte.class);
	private static final Pattern PATTERN_MOT_DE_PASSE = Pattern.compile("\\\\m\\[[0-9]+\\]");
	private static final Pattern PATTERN_COULEUR = Pattern.compile("\\[[0-9]+\\].*");
	private static final JSONObject JSON_INFORMATIONS_POLICE = chargerInformationsPolice();
	private static final Color[] COULEURS_PAR_DEFAUT = chargerCouleurParDefaut();
	/** Pour ne pas que le cote droit du texte ne soit coupe */
	public static final int MARGE_A_DROITE = 4;
	/** A utiliser si le texte est vide */
	private static final BufferedImage PIXEL_VIDE = Graphismes.imageVide(1, 1);
	
	/**
	 * Taille de la police
	 */
	public enum Taille {
		MOYENNE("MOYENNE"), GRANDE("GRANDE");
		
		public final String nom;
		public final int pixels;
		
		/** 
		 * Constructeur explicite
		 * @param nom designant cette taille
		 */
		Taille(final String nom) {
			this.nom = nom;
			this.pixels = Texte.chargerTaille(nom.toLowerCase());
		}
		
		/**
		 * Obtenir un objet Taille a partir de son nom.
		 * @param nom designant une Taille
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
	
	public static final int INTERLIGNE = 8;
	private static final int OPACITE_MAXIMALE = 100;
	private static final String POLICE_PAR_DEFAUT = "arial";
	private static final String POLICE = chargerNomPolice();
	
	public ArrayList<String> contenu;
	public int taille;
	public int opacite;
	public Color couleurForcee = null;
	public Color couleurContour = null;
	/** Nombre maximal de caracteres d'une ligne */
	private int largeurMaximale;

	/**
	 * Constructeur implicite (opacite maximale, taille moyenne) pour les Menus
	 * @param contenu du Texte (dans plusieurs langues)
	 * @param xDebut position x a l'ecran du coin haut-gauche du Texte
	 * @param yDebut position y a l'ecran du coin haut-gauche du Texte
	 * @param selectionnable est-il selectionnable dans le cadre d'un Menu ?
	 * @param c1 c1 comportement au survol
	 * @param c2 c2 comportement a la confirmation
	 * @param id identifiant de l'ElementDeMenu
	 */
	public Texte(final ArrayList<String> contenu, final int xDebut, final int yDebut, final boolean selectionnable, 
			final ArrayList<Commande> c1, final ArrayList<Commande> c2, final int id) {
		this(contenu, xDebut, yDebut, Taille.MOYENNE, -1, selectionnable, Texte.OPACITE_MAXIMALE, c1, c2, id);
	}
	
	/**
	 * Constructeur implicite (opacite maximale) pour les Menus
	 * @param contenu du Texte (dans plusieurs langues)
	 * @param xDebut position x a l'ecran du coin haut-gauche du Texte
	 * @param yDebut position y a l'ecran du coin haut-gauche du Texte
	 * @param taille de la police
	 * @param largeurMaximale limite de caracteres d'une ligne
	 * @param selectionnable est-il selectionnable dans le cadre d'un Menu ?
	 * @param c1 comportement au survol
	 * @param c2 comportement a la confirmation
	 * @param id identifiant de l'ElementDeMenu
	 */
	public Texte(final ArrayList<String> contenu, final int xDebut, final int yDebut, final Taille taille, final int largeurMaximale, 
			final boolean selectionnable, final ArrayList<Commande> c1, final ArrayList<Commande> c2, final int id) {
		this(contenu, xDebut, yDebut, taille, largeurMaximale, selectionnable, OPACITE_MAXIMALE, c1, c2, id);
	}
	
	/**
	 * Constructeur explicite pour les Menus
	 * @param contenu du Texte (dans plusieurs langues)
	 * @param xDebut position x a l'ecran du coin haut-gauche du Texte
	 * @param yDebut position y a l'ecran du coin haut-gauche du Texte
	 * @param taille des caracteres
	 * @param largeurMaximale limite de caracteres d'une ligne
	 * @param selectionnable est-il selectionnable dans le cadre d'un Menu ?
	 * @param opacite transparence
	 * @param c1 comportement au survol
	 * @param c2 comportement a la confirmation
	 * @param id identifiant de l'ElementDeMenu
	 */
	public Texte(final ArrayList<String> contenu, final int xDebut, final int yDebut, final Taille taille, final int largeurMaximale, 
			final boolean selectionnable, final int opacite, final ArrayList<Commande> c1, final ArrayList<Commande> c2, final int id) {
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
		this(contenu, COULEURS_PAR_DEFAUT[0]);
	}
	
	/**
	 * Constructeur implicite pour les Messages d'un Event
	 * @param contenu du Texte (dans plusieurs langues)
	 * @param couleurForcee pour avoir un texte d'une autre couleur que celle par defaut
	 */
	public Texte(final ArrayList<String> contenu, final Color couleurForcee) {
		this(contenu, couleurForcee, null, Taille.MOYENNE);
	}
	
	/**
	 * Constructeur explicite pour les Messages d'un Event
	 * @param contenu du Texte (dans plusieurs langues)
	 * @param couleurForcee pour avoir un texte d'une autre couleur que celle par defaut
	 * @param taille autre que la taille par defaut
	 * @param couleurContour une autre couleur enrobe les lettres
	 */
	public Texte(final ArrayList<String> contenu, final Color couleurForcee, final Color couleurContour, final Taille taille) {
		super(0, false, 0, 0, null, null, null); //on se fout de la gueule de la classe mere
		
		this.couleurForcee = couleurForcee;
		this.contenu = contenu;
		this.taille = taille.pixels;
		this.opacite = Texte.OPACITE_MAXIMALE;
		this.couleurContour = couleurContour;
	}

	/**
	 * Convertir un Texte en image
	 * @return le Texte sous forme d'image
	 */
	public final BufferedImage texteToImage() {
		final int langue = Main.langue;
		String texteAAfficher = this.contenu.get(langue < this.contenu.size() ? langue : 0);
		
		if (texteAAfficher == null || "".equals(texteAAfficher)) {
			// le texte est vide, donc l'image aussi
			return PIXEL_VIDE;
		}
		
		//mot de passe
		final Matcher matcher = PATTERN_MOT_DE_PASSE.matcher(texteAAfficher);
		while (matcher.find()) {
			final int numeroMot = Integer.parseInt( texteAAfficher.substring(matcher.start()+3, matcher.end()-1) );
			final String mot = Main.getPartieActuelle().mots[numeroMot];
			texteAAfficher = texteAAfficher.replace("\\m["+numeroMot+"]", mot != null ? mot : "");
		}
		
		//decoupage en lignes
        String[] texts = texteAAfficher.split("\\\\n");
        int nombreLignes = texts.length;
        if (texts.length <= 0) {
        	return null;
        }
        
        //largeur maximale des lignes
        if (this.largeurMaximale > 0) {
        	final ArrayList<String> textesListe = new ArrayList<String>();
        	for (String ligne : texts) {
        		textesListe.add(ligne);
        	}
        	
        	int nombreDeLignes = textesListe.size();
	        for (int i = 0; i<nombreDeLignes; i++) {
	        	final String ligne = textesListe.get(i);
	        	if (ligne.length() > this.largeurMaximale) {
	        		final int coupure = couper(ligne);
	        		textesListe.set(i, ligne.substring(0, coupure));
	        		textesListe.add(i+1, ligne.substring(coupure+1));
	        		nombreDeLignes++;
	        		LOG.debug("La ligne \""+ligne+"\" a ete scindee : "+textesListe.get(i)+"|"+textesListe.get(i+1));
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
        final int hauteurTotaleImage = (hauteurDesLignes + Texte.INTERLIGNE)*nombreLignes - Texte.INTERLIGNE; //on retire l'interligne inutile tout a la fin
        img = new BufferedImage(largeurDesLignes, hauteurTotaleImage, Graphismes.TYPE_DES_IMAGES);
        
        //reglage de la police
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
        
        //si aucune couleur forcee, couleur par defaut
        if (this.couleurForcee == null) {
        	g2d.setColor(Texte.COULEURS_PAR_DEFAUT[0]);
        } else {
        	g2d.setColor(this.couleurForcee);
        }
        
        //on ecrit chaque ligne
        for (int i = 0; i<nombreLignes; i++) { 
        	int largeurALaquelleOnEcrit = 0;
        	final int hauteurALaquelleOnEcrit = i*(this.taille+Texte.INTERLIGNE) + fm.getAscent();
        	final String ligne = texts[i].trim();
        	final String[] boutsDeLigne = ligne.split("\\\\c", -1);
        	final int nombreDeBoutsDeLigne = boutsDeLigne.length;
        	for (int j = 0; j<nombreDeBoutsDeLigne; j++) {      		
        		//couleur du texte ?
        		if (boutsDeLigne[j].matches("^\\[[0-9]+\\].*")) {
        			final int numeroCouleur = Integer.parseInt(
        					boutsDeLigne[j]
        					.split("\\[", -1)[1]
        					.split("\\]", -1)[0]
        			);
        			g2d.setColor(Texte.COULEURS_PAR_DEFAUT[numeroCouleur]);
        			boutsDeLigne[j] = boutsDeLigne[j].substring(boutsDeLigne[j].indexOf(']')+1);
	        		largeurALaquelleOnEcrit += fm.stringWidth(boutsDeLigne[j-1]);
        		}
        		
        		//dessiner le contour
        		if (this.couleurContour != null) {
        			final Color memoriserCouleur = g2d.getColor();
        			g2d.setColor(this.couleurContour);
        			g2d.drawString(boutsDeLigne[j], largeurALaquelleOnEcrit-1, hauteurALaquelleOnEcrit-1);
        			g2d.drawString(boutsDeLigne[j], largeurALaquelleOnEcrit-1, hauteurALaquelleOnEcrit+1);
        			g2d.drawString(boutsDeLigne[j], largeurALaquelleOnEcrit+1, hauteurALaquelleOnEcrit-1);
        			g2d.drawString(boutsDeLigne[j], largeurALaquelleOnEcrit+1, hauteurALaquelleOnEcrit+1);
        			g2d.setColor(memoriserCouleur);
        		}
        		
        		//dessiner les lettres
        		g2d.drawString(boutsDeLigne[j], largeurALaquelleOnEcrit, hauteurALaquelleOnEcrit);
        	}
        }
	    
        LOG.trace("Texte transforme en image : "+texteAAfficher);
        return img;
	}
	
	/**
	 * Couper une ligne trop longue.
	 * @param ligne a couper
	 * @return lieu de coupure
	 */
	private int couper(final String ligne) {
		int i = this.largeurMaximale;
		boolean onATrouveLeLieuDeCoupure = false;
		while (!onATrouveLeLieuDeCoupure && i>0) {
			if (ligne.charAt(i) == ' ') {
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
	 * Separer les langues dans une liste.
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
	
	/**
	 * Charger le fichier de propritetes de la police d'ecriture.
	 * @return objet JSON
	 */
	private static JSONObject chargerInformationsPolice() {
		try {
			return InterpreteurDeJson.ouvrirJson("police", "./ressources/Data/");
		} catch (Exception e) {
			LOG.error("Impossible de lire le fichier des informations sur la police d'ecriture.", e);
			return null;
		}
	}
	
	/**
	 * Recuperer le nom de la police du jeu.
	 * @return nom de la police
	 */
	private static String chargerNomPolice() {
		final String police = JSON_INFORMATIONS_POLICE.getString("nomPolice");
		if (verifierSiLaPoliceEstInstallee(police)) {
			// La police est disponible
			return police;
			
		} else {
			// La police n'existe pas sur l'ordinateur de l'utilisateur
			// On essaye de l'installer
			try {
				final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("./ressources/Graphics/Fonts/"+police+".ttf")));
				LOG.info("La police "+police+" a de etre installee.");
				return police;
			} catch (IOException|FontFormatException e) {
				LOG.error("Impossible de charger la police personnalisee "+police, e);
				LOG.info("La police par defaut "+POLICE_PAR_DEFAUT+" sera utilisee a la place.");
			}
		}
		return POLICE_PAR_DEFAUT;
	}
	
	/**
	 * Recuperer les couleurs de la police du jeu.
	 * @return couleurs de la police
	 */
	private static Color[] chargerCouleurParDefaut() {
		final JSONArray jsonCouleurs = JSON_INFORMATIONS_POLICE.getJSONArray("couleurs");
		final int taille = jsonCouleurs.length();
		final Color[] couleurs = new Color[taille];
		for (int i = 0; i<taille; i++) {
			final JSONArray jsonCouleur = jsonCouleurs.getJSONArray(i);
			couleurs[i] = new Color(jsonCouleur.getInt(0), jsonCouleur.getInt(1), jsonCouleur.getInt(2));
		}
		return couleurs;
	}
	
	/**
	 * Recuperer la taille de la police dans le fichier de proprietes.
	 * @param taille libelle de la taille
	 * @return nombre de pixels de hauteur
	 */
	private static int chargerTaille(final String taille) {
		return JSON_INFORMATIONS_POLICE.getJSONObject("tailles").getInt(taille);
	}
	
	/**
	 * Verifier si la police du jeu est installee sur l'ordinateur de l'utilisateur.
	 * @param nomPolice police du jeu
	 * @return disponible ou pas
	 */
	private static boolean verifierSiLaPoliceEstInstallee(final String nomPolice) {
		GraphicsEnvironment g = null;
		g = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final String[] fonts = g.getAvailableFontFamilyNames();
		for (int i = 0; i<fonts.length; i++) {
			if (fonts[i].equals(nomPolice)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Trouver des couleurs de selection adaptees au Texte.
	 * @return tableau conteneant deux couleurs RGBA
	 */
	public int[][] trouverCouleursDeSelectionAdaptees() {
		final int[][] couleursDeSelectionAdaptees = new int[2][4];
		
		final int langue = Main.langue;
		final String texteAAfficher = this.contenu.get(langue < this.contenu.size() ? langue : 0);
		
		if (texteAAfficher.contains("\\c[07]")) {
			// selection argentee pour les textes blancs
			couleursDeSelectionAdaptees[0] = new int[]{200, 255, 255, 100};
			couleursDeSelectionAdaptees[1] = new int[]{100, 150, 200, 0};
		} else {
			couleursDeSelectionAdaptees[0] = null;
			couleursDeSelectionAdaptees[1] = null;
		}
		
		return couleursDeSelectionAdaptees;
	}

}
