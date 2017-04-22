package commandes;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import main.Commande;
import map.LecteurMap;
import menu.Menu;
import menu.Texte;
import son.LecteurAudio;
import utilitaire.Maths;
import utilitaire.graphismes.Graphismes;

/**
 * Un Choix donne la possibilité au joueur de choisir jusqu'à quatre alternatives.
 * Le Choix s'affiche comme un Message, mais avec un curseur à déplacer.
 * Selon la sélection du joueur, un embranchement différent du code Event est utilisé.
 */
public class Choix extends Message {
	private static final Logger LOG = LogManager.getLogger(Choix.class);
	
	/** Numéro du Choix */
	public int numero;
	
	/** Différentes alternatives proposées par le Choix */
	public final ArrayList<ArrayList<String>> alternatives;

	private int positionCurseurAffichee = 0;
	public int positionCurseurChoisie = 0;
	public ArrayList<BufferedImage> imagesDesSelectionsPossibles = null;
	 
	/**
	 * Constructeur explicite
	 * @param numero du Choix
	 * @param texte affiché dans la boîte de dialogue
	 * @param alternatives offertes par le choix au joueur
	 */
	public Choix(final int numero, final ArrayList<String> texte, final ArrayList<ArrayList<String>> alternatives) {
		super(texte);
		this.numero = numero;
		this.alternatives = alternatives;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Choix(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero"),
				Texte.construireTexteMultilingue(parametres.get("texte")),
				recupererLesAlternativesDUnChoix((JSONArray) parametres.get("alternatives"))
		);
	}
	
	/**
	 * Fabrique l'image du Message à partir de l'image de la boîte de dialogue et du texte.
	 * Une image est fabriquée pour chaque alternative à sélectionner.
	 * @return image du Message
	 */
	@Override
	protected final BufferedImage produireImageDuMessage() {
		//initialisation (la première fois, on fabrique toutes les images)
		if (this.imagesDesSelectionsPossibles == null) {
			BufferedImage imageDesAlternatives = Graphismes.creerUneImageVideDeMemeTaille(Message.imageBoiteMessage);
			
			// Texte de base
			final Texte texteDeBase = new Texte(this.texte);
			
			// On ajoute les alternatives à l'image de base
			final ArrayList<Texte> alternativesTexte = new ArrayList<Texte>();
			final ArrayList<BufferedImage> imagesAlternatives = new ArrayList<BufferedImage>();
			
			final int hauteurLigne = Texte.Taille.MOYENNE.pixels + Texte.INTERLIGNE;
			final int hauteurTexte = this.calculerHauteurTexte();
			for (int i = 0; i < this.alternatives.size(); i++) {
				final ArrayList<String> alternativeString = alternatives.get(i);
				alternativesTexte.add( new Texte(alternativeString) );
				imagesAlternatives.add( alternativesTexte.get(i).getImage() );
				imageDesAlternatives = Graphismes.superposerImages(
						imageDesAlternatives, 
						imagesAlternatives.get(i), 
						MARGE_DU_TEXTE, 
						MARGE_DU_TEXTE + hauteurTexte + i*hauteurLigne
				);
			}
			
			// Différentes sélections possibles
			this.imagesDesSelectionsPossibles = new ArrayList<BufferedImage>();
			for (int i = 0; i < this.alternatives.size(); i++) {
				final BufferedImage surlignage = alternativesTexte.get(i).creerImageDeSelection();
				BufferedImage selectionPossible = Graphismes.clonerUneImage(imageBoiteMessage);				
				selectionPossible = Graphismes.superposerImages(
						selectionPossible, 
						surlignage, 
						MARGE_DU_TEXTE - Texte.CONTOUR, 
						MARGE_DU_TEXTE + hauteurTexte + i*hauteurLigne - Texte.CONTOUR
				);
				selectionPossible = Graphismes.superposerImages(
						selectionPossible, 
						texteDeBase.getImage(), 
						MARGE_DU_TEXTE, 
						MARGE_DU_TEXTE
				);
				selectionPossible = Graphismes.superposerImages(
						selectionPossible, 
						imageDesAlternatives, //toutes les alternatives sur la même image
						0, 
						0
				);
				this.imagesDesSelectionsPossibles.add(selectionPossible);
			}
		}
		return this.imagesDesSelectionsPossibles.get(this.positionCurseurAffichee);
	}
	
	@Override
	protected final boolean ilFautReactualiserLImageDuMessage(final LecteurMap lecteur) {
		final boolean leCurseurABouge = (positionCurseurAffichee != positionCurseurChoisie);
		final boolean lesImagesNOntJamaisEteGenerees = this.imagesDesSelectionsPossibles == null;
		this.positionCurseurAffichee = this.positionCurseurChoisie;
		
		return leCurseurABouge || lesImagesNOntJamaisEteGenerees || super.ilFautReactualiserLImageDuMessage(lecteur);
	}
	
	/**
	 * La Commande suivante dépend de l'alternative choisie par le joueur.
	 */
	@Override
	protected final int redirectionSelonLeChoix(final int curseurActuel, final ArrayList<Commande> commandes) {
		Commande commande;
		ChoixAlternative alternative;
		for (int i = 0; i < commandes.size(); i++) {
			commande = commandes.get(i);
			if (commande instanceof ChoixAlternative) {
				alternative = (ChoixAlternative) commande;
				if (alternative.numeroChoix == this.numero 
						&& alternative.numeroAlternative == this.positionCurseurAffichee
				) {
					//c'est l'alternative choisie par le joueur !
					LecteurAudio.playSe(Menu.BRUIT_CONFIRMER_SELECTION);
					return i+1;
				}
			}
		}
		//l'alternative sélectionnée de ce Choix n'a pas été trouvée
		LOG.error("L'alternative " + positionCurseurAffichee
				+ " du choix numéro " + numero + " n'a pas été trouvée !");
		return curseurActuel+1;
	}
	
	/** Le Joueur appuie sur la touche pendant le Message */
	@Override
	public final void haut() {
		this.positionCurseurChoisie = Maths.modulo(this.positionCurseurChoisie - 1, this.alternatives.size());
		LecteurAudio.playSe(Menu.BRUIT_DEPLACEMENT_CURSEUR);
	}
	
	/** Le Joueur appuie sur la touche pendant le Message */
	@Override
	public final void bas() {
		this.positionCurseurChoisie = Maths.modulo(this.positionCurseurChoisie + 1, this.alternatives.size());
		LecteurAudio.playSe(Menu.BRUIT_DEPLACEMENT_CURSEUR);
	}
	
	/**
	 * Traduit un JSONArray représentant les alternatives d'un Choix en une liste de Strings.
	 * La première ArrayList désigne les alternatives, la seconde les langues disponibles pour chaque alternative.
	 * @param alternativesJSON JSONArray représentant les alternatives
	 * @return liste des Strings
	 */
	public static ArrayList<ArrayList<String>> recupererLesAlternativesDUnChoix(final JSONArray alternativesJSON) {
		final ArrayList<ArrayList<String>> alternatives = new ArrayList<ArrayList<String>>();
		for (Object object : alternativesJSON) {
			final ArrayList<String> alternativeMultiLingue = Texte.construireTexteMultilingue(object);
			alternatives.add(alternativeMultiLingue);
		}
		return alternatives;
	}
	
}