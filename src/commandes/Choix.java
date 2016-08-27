package commandes;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import main.Commande;
import menu.Menu;
import menu.Texte;
import son.LecteurAudio;
import utilitaire.Graphismes;
import utilitaire.InterpreteurDeJson;
import utilitaire.Maths;

/**
 * Un Choix donne la possibilité au joueur de choisir jusqu'à quatre alternatives.
 * Le Choix s'affiche comme un Message, mais avec un curseur à déplacer.
 * Selon la sélection du joueur, un embranchement différent du code Event est utilisé.
 */
public class Choix extends Message {
	/** Numéro du Choix */
	public int numero;
	
	/** Différentes alternatives proposées par le Choix */
	public final ArrayList<String> alternatives;

	private int positionCurseurAffichee = -1;
	public int positionCurseurChoisie = 0;
	public ArrayList<BufferedImage> imageDesSelectionsPossibles = null;
	 
	/**
	 * Constructeur explicite
	 * @param numero du Choix
	 * @param texte affiché dans la boîte de dialogue
	 * @param alternatives offertes par le choix au joueur
	 */
	public Choix(final int numero, final String texte, final ArrayList<String> alternatives) {
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
				(String) parametres.get("texte"),
				InterpreteurDeJson.recupererLesAlternativesDUnChoix((JSONArray) parametres.get("alternatives"))
		);
	}
	
	/**
	 * Fabrique l'image du Message à partir de l'image de la boîte de dialogue et du texte.
	 * Une image est fabriquée pour chaque alternative à sélectionner.
	 * @return image du Message
	 */
	@Override
	protected final BufferedImage produireImageDuMessage() {
		BufferedImage imageDesAlternatives = Graphismes.creerUneImageVideDeMemeTaille(Message.imageBoiteMessage);
		
		// Texte de base
		final Texte texteDeBase = new Texte(this.texte);
		
		// On ajoute les alternatives à l'image de base
		final ArrayList<Texte> alternativesTexte = new ArrayList<Texte>();
		final ArrayList<BufferedImage> imagesAlternatives = new ArrayList<BufferedImage>();
		
		final int hauteurLigne = Texte.TAILLE_MOYENNE + Texte.INTERLIGNE;
		final int hauteurTexte = this.calculerHauteurTexte();
		for (int i = 0; i < this.alternatives.size(); i++) {
			final String alternativeString = alternatives.get(i);
			alternativesTexte.add( new Texte(alternativeString) );
			imagesAlternatives.add( alternativesTexte.get(i).image );
			imageDesAlternatives = Graphismes.superposerImages(
					imageDesAlternatives, 
					imagesAlternatives.get(i), 
					MARGE_DU_TEXTE, 
					MARGE_DU_TEXTE + hauteurTexte + i*hauteurLigne
			);
		}
		
		// Différentes sélections possibles
		this.imageDesSelectionsPossibles = new ArrayList<BufferedImage>();
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
					texteDeBase.image, 
					MARGE_DU_TEXTE, 
					MARGE_DU_TEXTE
			);
			selectionPossible = Graphismes.superposerImages(
					selectionPossible, 
					imageDesAlternatives, //toutes les alternatives sur la même image
					0, 
					0
			);
			this.imageDesSelectionsPossibles.add(selectionPossible);
		}
		
		return this.imageDesSelectionsPossibles.get(this.positionCurseurAffichee);
	}
	
	/**
	 * Le curseur du Choix a-t-il bougé ?
	 * Si oui il faut remplacer l'image de Message affichée.
	 * @return 
	 */
	@Override
	protected final boolean siChoixLeCurseurATIlBouge() {
		final boolean reponse = (positionCurseurAffichee != positionCurseurChoisie) || this.imageDesSelectionsPossibles == null;
		positionCurseurAffichee = positionCurseurChoisie;
		return reponse;
	}
	
	/**
	 * La Commande suivante dépend de l'alternative choisie par le joueur.
	 */
	@Override
	protected final int redirectionSelonLeChoix(final int curseurActuel, final ArrayList<Commande> commandes) {
		for (int i = 0; i < commandes.size(); i++) {
			final Commande commande = commandes.get(i);
			if (commande instanceof ChoixAlternative) {
				final ChoixAlternative alternative = (ChoixAlternative) commande;
				if (alternative.numeroChoix == this.numero 
						&& alternative.numeroAlternative == positionCurseurAffichee
				) {
					//c'est l'alternative choisie par le joueur !
					LecteurAudio.playSe(Menu.BRUIT_CONFIRMER_SELECTION);
					return i+1;
				}
			}
		}
		//le début de Boucle n'a pas été trouvé
		System.err.println("L'alternative " + positionCurseurAffichee
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
	
}