package commandes;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import main.Commande;
import menu.Texte;
import utilitaire.Graphismes;
import utilitaire.InterpreteurDeJson;

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
	public ArrayList<BufferedImage> imagesAlternatives = new ArrayList<BufferedImage>();
	 
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
		//TODO factoriser partiellement avec la méthode mère
		BufferedImage imageMessage = new BufferedImage(
				IMAGE_BOITE_MESSAGE.getWidth(), 
				IMAGE_BOITE_MESSAGE.getWidth(), 
				IMAGE_BOITE_MESSAGE.getType()
		);
		
		// Ajout de l'image de boîte de dialogue
		imageMessage = Graphismes.superposerImages(imageMessage, IMAGE_BOITE_MESSAGE, 0, 0);
		
		// Ajout du texte
		final Texte t = new Texte(texte);
		imageMessage = Graphismes.superposerImages(IMAGE_BOITE_MESSAGE, t.texteToImage(), MARGE_DU_TEXTE, MARGE_DU_TEXTE);
		
		//TODO fabriquer l'image de base avec le texte et les différentes alternatives, sans sélection
		//...
		//TODO fabriquer les images avec les différentes sélections possibles
		//...
		
		return imageMessage;
	}
	/**
	 * Le curseur du Choix a-t-il bougé ?
	 * Si oui il faut remplacer l'image de Message affichée.
	 * @return 
	 */
	@Override
	protected final boolean siChoixLeCurseurATIlBouge() {
		final boolean reponse = positionCurseurAffichee != positionCurseurChoisie;
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
					return i+1;
				}
			}
		}
		//le début de Boucle n'a pas été trouvé
		System.err.println("L'alternative " + positionCurseurAffichee
				+ " du choix numéro " + numero + " n'a pas été trouvée !");
		return curseurActuel+1;
	}
}
