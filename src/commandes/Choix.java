package commandes;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import main.Commande;
import map.LecteurMap;
import menu.Menu;
import menu.Texte;
import utilitaire.Maths;
import utilitaire.graphismes.Graphismes;
import utilitaire.son.LecteurAudio;

/**
 * Un Choix donne la possibilite au joueur de choisir jusqu'a� quatre
 * alternatives. Le Choix s'affiche comme un Message, mais avec un curseur a
 * deplacer. Selon la selection du joueur, un embranchement different du code
 * Event est utilise.
 */
public class Choix extends Message {
	private static final Logger LOG = LogManager.getLogger(Choix.class);

	/** Numero du Choix */
	public int numero;

	/** Differentes alternatives proposees par le Choix */
	public final ArrayList<ArrayList<String>> alternatives;

	private int positionCurseurAffichee = 0;
	public int positionCurseurChoisie = 0;
	public ArrayList<BufferedImage> imagesDesSelectionsPossibles = null;

	/**
	 * Constructeur explicite
	 * 
	 * @param numero       du Choix
	 * @param texte        affiche dans la boîte de dialogue
	 * @param alternatives offertes par le choix au joueur
	 */
	public Choix(final int numero, final ArrayList<String> texte, final ArrayList<ArrayList<String>> alternatives) {
		super(texte);
		this.numero = numero;
		this.alternatives = alternatives;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public Choix(final HashMap<String, Object> parametres) {
		this((int) parametres.get("numero"), Texte.construireTexteMultilingue(parametres.get("texte")),
				recupererLesAlternativesDUnChoix((JSONArray) parametres.get("alternatives")));
	}

	/**
	 * Fabrique l'image du Message à partir de l'image de la boîte de dialogue et
	 * du texte. Une image est fabriquee pour chaque alternative à selectionner.
	 * 
	 * @return image du Message
	 */
	@Override
	protected final BufferedImage produireImageDuMessage() {
		// initialisation (la premiere fois, on fabrique toutes les images)
		if (this.imagesDesSelectionsPossibles == null) {
			BufferedImage imageDesAlternatives = Graphismes.creerUneImageVideDeMemeTaille(Message.imageBoiteMessage());

			// Texte de base
			if (this.texte == null || this.texte.size() == 0 || this.texte.get(0) == null
					|| "".equals(this.texte.get(0))) {
				if (this.page != null && this.page.event != null
						&& this.page.event.map.lecteur.messagePrecedent != null) {
					this.texte = this.page.event.map.lecteur.messagePrecedent.texte;
				}
			}
			final Texte texteQuestion = new Texte(this.texte);
			BufferedImage imageQuestion = texteQuestion.getImage();
			final int nombreDeLignesQuestion = Math
					.round((float) imageQuestion.getHeight() / (float) (texteQuestion.taille + Texte.INTERLIGNE));
			final int nombreDeLignesQuOnNePeutPasAfficher = Math
					.max(this.alternatives.size() + nombreDeLignesQuestion - 4, 0);
			final int debutDecoupage = nombreDeLignesQuOnNePeutPasAfficher * (texteQuestion.taille + Texte.INTERLIGNE);
			final int hauteurDecoupage = imageQuestion.getHeight() - debutDecoupage;
			imageQuestion = imageQuestion.getSubimage(0, debutDecoupage, imageQuestion.getWidth(), hauteurDecoupage);

			// On ajoute les alternatives a l'image de base
			final ArrayList<Texte> alternativesTexte = new ArrayList<Texte>();
			final ArrayList<BufferedImage> imagesAlternatives = new ArrayList<BufferedImage>();

			final int hauteurLigne = Texte.Taille.MOYENNE.pixels + Texte.INTERLIGNE;
			for (int i = 0; i < this.alternatives.size(); i++) {
				final ArrayList<String> alternativeString = alternatives.get(i);
				alternativesTexte.add(new Texte(alternativeString));
				imagesAlternatives.add(alternativesTexte.get(i).getImage());
				imageDesAlternatives = Graphismes.superposerImages(imageDesAlternatives, imagesAlternatives.get(i),
						MARGE_X_TEXTE, MARGE_Y_TEXTE + imageQuestion.getHeight() + i * hauteurLigne);
			}

			// Differentes selections possibles
			this.imagesDesSelectionsPossibles = new ArrayList<BufferedImage>();
			for (int i = 0; i < this.alternatives.size(); i++) {
				final int[][] couleursDeSelectionAdaptees = alternativesTexte.get(i)
						.trouverCouleursDeSelectionAdaptees();
				final BufferedImage surlignage = alternativesTexte.get(i)
						.creerImageDeSelection(couleursDeSelectionAdaptees[0], couleursDeSelectionAdaptees[1]);
				BufferedImage selectionPossible = Graphismes.clonerUneImage(Message.imageBoiteMessage());
				selectionPossible = Graphismes.superposerImages(selectionPossible, surlignage,
						MARGE_X_TEXTE - Texte.CONTOUR,
						MARGE_Y_TEXTE + imageQuestion.getHeight() + i * hauteurLigne - Texte.CONTOUR);
				selectionPossible = Graphismes.superposerImages(selectionPossible, imageQuestion, MARGE_X_TEXTE,
						MARGE_Y_TEXTE);
				selectionPossible = Graphismes.superposerImages(selectionPossible, imageDesAlternatives, // toutes les
																											// alternatives
																											// sur la
																											// même
																											// image
						0, 0);
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
	 * La Commande suivante depend de l'alternative choisie par le joueur.
	 */
	@Override
	protected final int redirectionSelonLeChoix(final int curseurActuel, final List<Commande> commandes) {
		Commande commande;
		ChoixAlternative alternative;
		for (int i = 0; i < commandes.size(); i++) {
			commande = commandes.get(i);
			if (commande instanceof ChoixAlternative) {
				alternative = (ChoixAlternative) commande;
				if (alternative.numeroChoix == this.numero
						&& alternative.numeroAlternative == this.positionCurseurAffichee) {
					// c'est l'alternative choisie par le joueur !
					LecteurAudio.playSe(Menu.BRUIT_CONFIRMER_SELECTION);
					return i + 1;
				}
			}
		}
		// l'alternative selectionnee de ce Choix n'a pas ete trouvee
		LOG.error("L'alternative " + positionCurseurAffichee + " du choix numero " + numero + " n'a pas ete trouvee !");
		return curseurActuel + 1;
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
	 * Traduit un JSONArray representant les alternatives d'un Choix en une liste de
	 * Strings. La premiere ArrayList designe les alternatives, la seconde les
	 * langues disponibles pour chaque alternative.
	 * 
	 * @param alternativesJSON JSONArray representant les alternatives
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
