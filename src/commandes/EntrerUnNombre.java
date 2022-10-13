package commandes;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.Commande;
import map.LecteurMap;
import menu.Menu;
import menu.Texte;
import utilitaire.Maths;
import utilitaire.graphismes.Graphismes;
import utilitaire.son.LecteurAudio;

/**
 * La saisie de nombre donne la possibilit� au joueur d'�crire un nombre demand�
 * par le jeu. Le nombre entr� est m�moris� dans une Variable. S'affiche comme
 * un Message, mais avec un curseur a d�placer.
 */
public class EntrerUnNombre extends Message {
	/** base d�cimale */
	private static final int NOMBRE_DE_CHIFFRES = 10;

	/** Num�ro de la Variable qui m�morise le code */
	public int numeroDeVariable;
	/** Tableau des chiffres rentr�s par le joueur */
	private int[] chiffresRentres;
	private Texte[] chiffresRentresTexte;

	public int positionCurseur = 0;
	public boolean reactualiserLImage = true;

	private final BufferedImage surlignage;
	private BufferedImage imageDuMessage;
	private final int largeurChiffre;

	/**
	 * Constructeur explicite
	 * 
	 * @param texte            affich� dans la bo�te de dialogue
	 * @param numeroDeVariable num�ro de la Variable qui m�morise le nombre
	 * @param tailleDuNombre   longueur (en chiffres) du nombre entr�
	 */
	public EntrerUnNombre(final ArrayList<String> texte, final int numeroDeVariable, final int tailleDuNombre) {
		super(texte);
		this.numeroDeVariable = numeroDeVariable;
		this.chiffresRentres = new int[tailleDuNombre];
		this.chiffresRentresTexte = new Texte[tailleDuNombre];
		final ArrayList<String> contenuTexte = new ArrayList<String>();
		contenuTexte.add("0");
		for (int i = 0; i < tailleDuNombre; i++) {
			chiffresRentresTexte[i] = new Texte(contenuTexte);
		}
		this.surlignage = chiffresRentresTexte[0].creerImageDeSelection(null, null);
		this.largeurChiffre = chiffresRentresTexte[0].getImage().getWidth();
	}

	/**
	 * Constructeur g�n�rique
	 * 
	 * @param parametres liste de param�tres issus de JSON
	 */
	public EntrerUnNombre(final HashMap<String, Object> parametres) {
		this(Texte.construireTexteMultilingue(parametres.get("texte")), (int) parametres.get("numeroDeVariable"),
				(int) parametres.get("tailleDuNombre"));
	}

	/**
	 * Fabrique l'image du Message a partir de l'image de la bo�te de dialogue et du
	 * texte. Une image est fabriqu�e pour chaque alternative a s�lectionner.
	 * 
	 * @return image du Message
	 */
	@Override
	protected final BufferedImage produireImageDuMessage() {
		this.reactualiserLImage = false;

		// Texte de base
		final Texte texteDeBase = new Texte(this.texte);
		final int hauteurTexte = calculerHauteurTexte();

		// Superposition
		imageDuMessage = Graphismes.clonerUneImage(Message.imageBoiteMessage());

		imageDuMessage = Graphismes.superposerImages(imageDuMessage, surlignage,
				MARGE_X_TEXTE - Texte.CONTOUR + 2 * positionCurseur * largeurChiffre,
				MARGE_Y_TEXTE + hauteurTexte - Texte.CONTOUR);
		imageDuMessage = Graphismes.superposerImages(imageDuMessage, texteDeBase.getImage(), MARGE_X_TEXTE,
				MARGE_Y_TEXTE);
		for (int i = 0; i < chiffresRentres.length; i++) {
			imageDuMessage = Graphismes.superposerImages(imageDuMessage, chiffresRentresTexte[i].getImage(),
					MARGE_X_TEXTE + 2 * i * largeurChiffre, MARGE_Y_TEXTE + hauteurTexte);
		}
		return imageDuMessage;
	}

	@Override
	protected final boolean ilFautReactualiserLImageDuMessage(final LecteurMap lecteur) {
		return this.reactualiserLImage || super.ilFautReactualiserLImageDuMessage(lecteur);
	}

	/** Le Joueur appuie sur la touche pendant le Message */
	@Override
	public final void haut() {
		this.chiffresRentres[positionCurseur] = Maths.modulo(this.chiffresRentres[positionCurseur] + 1,
				NOMBRE_DE_CHIFFRES);
		final ArrayList<String> contenuTexte = new ArrayList<String>();
		contenuTexte.add("" + chiffresRentres[positionCurseur]);
		this.chiffresRentresTexte[positionCurseur] = new Texte(contenuTexte);
		this.reactualiserLImage = true;
		LecteurAudio.playSe(Menu.BRUIT_DEPLACEMENT_CURSEUR);
	}

	/** Le Joueur appuie sur la touche pendant le Message */
	@Override
	public final void bas() {
		this.chiffresRentres[positionCurseur] = Maths.modulo(this.chiffresRentres[positionCurseur] - 1,
				NOMBRE_DE_CHIFFRES);
		final ArrayList<String> contenuTexte = new ArrayList<String>();
		contenuTexte.add("" + chiffresRentres[positionCurseur]);
		this.chiffresRentresTexte[positionCurseur] = new Texte(contenuTexte);
		this.reactualiserLImage = true;
		LecteurAudio.playSe(Menu.BRUIT_DEPLACEMENT_CURSEUR);
	}

	/** Le Joueur appuie sur la touche pendant le Message */
	@Override
	public final void gauche() {
		this.positionCurseur = Maths.modulo(positionCurseur - 1, this.chiffresRentres.length);
		this.reactualiserLImage = true;
		LecteurAudio.playSe(Menu.BRUIT_DEPLACEMENT_CURSEUR);
	}

	/** Le Joueur appuie sur la touche pendant le Message */
	@Override
	public final void droite() {
		this.positionCurseur = Maths.modulo(positionCurseur + 1, this.chiffresRentres.length);
		this.reactualiserLImage = true;
		LecteurAudio.playSe(Menu.BRUIT_DEPLACEMENT_CURSEUR);
	}

	@Override
	protected final int redirectionSelonLeChoix(final int curseurActuel, final List<Commande> commandes) {
		int nombre = 0;
		for (int i = this.chiffresRentres.length - 1; i >= 0; i--) {
			nombre *= NOMBRE_DE_CHIFFRES;
			nombre += this.chiffresRentres[i];
		}
		// on modifie la valeur de la variable
		getPartieActuelle().variables[this.numeroDeVariable] = nombre;
		return curseurActuel + 1;
	}

}