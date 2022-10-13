package commandes;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import main.Main;
import map.Event;
import map.LecteurMap;
import menu.Texte;
import utilitaire.graphismes.Graphismes;

/**
 * Afficher un Message dans une boite de dialogue
 */
public class Message extends Commande implements CommandeEvent {
	// constantes
	private static final Logger LOG = LogManager.getLogger(Message.class);
	protected static final int MARGE_X_TEXTE = 96;
	protected static final int MARGE_X_TEXTE_FACESET = 54;
	protected static final int MARGE_Y_TEXTE = 24;
	protected static final int MARGE_Y_FACESET = 32;
	protected static final int INTELIGNE = 24;
	protected static final int LARGEUR_FACESET = 96;
	protected static final int ECART_FACESET = 12;
	protected static final String NOM_IMAGE_BOITE_MESSAGE = "parchotexte.png";
	protected static final String NOM_IMAGE_BOITE_MESSAGE_FACESET = "parchotexte faceset.png";
	protected static final BufferedImage IMAGE_BOITE_MESSAGE_PLEINE = chargerImageDeFondDeLaBoiteMessage(false);
	protected static final BufferedImage IMAGE_BOITE_MESSAGE_PLEINE_FACESET = chargerImageDeFondDeLaBoiteMessage(true);
	protected static final BufferedImage IMAGE_BOITE_MESSAGE_VIDE = Graphismes
			.creerUneImageVideDeMemeTaille(IMAGE_BOITE_MESSAGE_PLEINE);
	// protected static BufferedImage imageBoiteMessage =
	// IMAGE_BOITE_MESSAGE_PLEINE;
	protected static final boolean MASQUER_BOITE_MESSAGE_PAR_DEFAUT = false;
	protected static final Position POSITION_BOITE_MESSAGE_PAR_DEFAUT = Position.BAS;

	public ArrayList<String> texte;
	public BufferedImage image;
	private boolean touchePresseePourQuitterLeMessage = false;
	private boolean premiereFrameDAffichageDuMessage = true;
	public static boolean masquerBoiteMessage = MASQUER_BOITE_MESSAGE_PAR_DEFAUT;
	public static Position positionBoiteMessage = POSITION_BOITE_MESSAGE_PAR_DEFAUT;
	public static BufferedImage faceset;

	/**
	 * Position de la boite de Messages a l'ecran.
	 */
	public enum Position {
		BAS("bas", 0, 320), MILIEU("milieu", 0, 160), HAUT("haut", 0, 0);

		final String nom;
		public final int xAffichage;
		public final int yAffichage;

		/**
		 * Constructeur explicite
		 * 
		 * @param nom        de la position
		 * @param xAffichage (en pixels)
		 * @param yAffichage (en pixels)
		 */
		Position(final String nom, final int xAffichage, final int yAffichage) {
			this.nom = nom;
			this.xAffichage = xAffichage;
			this.yAffichage = yAffichage;
		}

		/**
		 * R�cup�rer la Position de la boite de Messages par son nom.
		 * 
		 * @param nom de la position
		 * @return position ainsi nomm�e
		 */
		public static Position parNom(final String nom) {
			for (Position position : Position.values()) {
				if (position.nom.equals(nom)) {
					return position;
				}
			}
			return Position.BAS;
		}
	}

	/**
	 * Constructeur explicite
	 * 
	 * @param texte affich� dans la boite de dialogue
	 */
	public Message(final ArrayList<String> texte) {
		this.texte = texte;
		this.touchePresseePourQuitterLeMessage = false;
	}

	/**
	 * Constructeur g�n�rique
	 * 
	 * @param parametres liste de param�tres issus de JSON
	 */
	public Message(final HashMap<String, Object> parametres) {
		this(Texte.construireTexteMultilingue(parametres.get("texte")));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {

		// TODO si la Commande suivante est le Choix/EntrerUnNombre dont ce Message est
		// la question, et qu'il y a la place dans la Commande suivante pour afficher la
		// question, passer tout de suite à la COmmande suivante

		final LecteurMap lecteur = this.page.event.map.lecteur;
		lecteur.normaliserApparenceDesInterlocuteursAvantMessage(this.page.event);
		// si le Message a afficher est diff�rent du Message affiche, on change !
		if (ilFautReactualiserLImageDuMessage(lecteur)) {
			if (lecteur.messageActuel != null) {
				lecteur.messagePrecedent = lecteur.messageActuel;
			}
			lecteur.messageActuel = this;
			this.image = produireImageDuMessage();
		}

		// fermer le message
		if (this.touchePresseePourQuitterLeMessage) {
			// on ferme le message
			lecteur.messagePrecedent = this;
			lecteur.messageActuel = null;
			this.touchePresseePourQuitterLeMessage = false;
			this.premiereFrameDAffichageDuMessage = true;
			return redirectionSelonLeChoix(curseurActuel, commandes);
		} else {
			// on laisse le message ouvert
			this.premiereFrameDAffichageDuMessage = false;
			return curseurActuel;
		}
	}

	/**
	 * Faut-il r�actualiser l'image du Message ?
	 * 
	 * @param lecteur de map
	 * @return true s'il faut construire une nouvelle image de Message, false sinon
	 */
	protected boolean ilFautReactualiserLImageDuMessage(final LecteurMap lecteur) {
		// le dialogue vient de commencer
		if (lecteur.messageActuel == null) {
			return true;
		}

		// le texte a chang� mais pas encore l'image
		final int langue = Main.langue;
		final ArrayList<String> texteActuel = lecteur.messageActuel.texte;
		final String messageActuel = texteActuel.get(langue < texteActuel.size() ? langue : 0);
		final String messageDesire = this.texte.get(langue < this.texte.size() ? langue : 0);
		if ((messageActuel == null && messageDesire != null)
				|| (messageActuel != null && !messageActuel.equals(messageDesire))) {
			LOG.debug(messageActuel + " =/= " + messageDesire);
			return true;
		}

		// TODO utile ?
		if (this.premiereFrameDAffichageDuMessage) {
			return true;
		}

		return false;
	}

	/**
	 * Charge l'image de fond de la boite de dialogue.
	 * 
	 * @return image de fond de la boite de dialogue
	 */
	private static BufferedImage chargerImageDeFondDeLaBoiteMessage(final boolean yATIlUnfaceset) {
		String nomImageBoiteMessage = yATIlUnfaceset ? NOM_IMAGE_BOITE_MESSAGE_FACESET : NOM_IMAGE_BOITE_MESSAGE;
		try {
			return Graphismes.ouvrirImage("Pictures", nomImageBoiteMessage);
		} catch (IOException e) {
			LOG.error("impossible d'ouvrir l'image " + nomImageBoiteMessage, e);
			return null;
		}
	}

	/**
	 * Fabrique l'image du Message a partir de l'image de la boite de dialogue et du
	 * texte. Methode derivee par la classe Choix.
	 * 
	 * @return image du Message
	 */
	protected BufferedImage produireImageDuMessage() {
		// Partir de la boite de dialogue
		BufferedImage imageMessage = Graphismes.clonerUneImage(imageBoiteMessage());
		// Ajout du texte
		final Texte t = new Texte(this.texte);
		t.actualiserImage();
		if (faceset != null) {
			imageMessage = Graphismes.superposerImages(imageMessage, faceset, MARGE_X_TEXTE_FACESET,
					MARGE_Y_FACESET);
			imageMessage = Graphismes.superposerImages(imageMessage, t.getImage(),
					MARGE_X_TEXTE_FACESET + LARGEUR_FACESET + ECART_FACESET,
					MARGE_Y_TEXTE);
		} else {
			imageMessage = Graphismes.superposerImages(imageMessage, t.getImage(), MARGE_X_TEXTE, MARGE_Y_TEXTE);
		}
		LOG.debug("Texte du message actualise");
		return imageMessage;
	}

	/**
	 * Position x ou il faut dessiner le texte, a l'interieur de l'image du Message
	 * 
	 * @return marge x
	 */
	protected final int xTexte() {
		return (faceset == null) ? MARGE_X_TEXTE : MARGE_X_TEXTE_FACESET + LARGEUR_FACESET + ECART_FACESET;
	}

	/**
	 * Ce n'est pas un Choix, juste un Message Normal, donc la Commande suivante est
	 * juste apres. Methode derivee par la classe Choix.
	 * 
	 * @param curseurActuel curseur dans la lecture des Commandes
	 * @param commandes     en cours d'execution, dont fait notamment partie ce
	 *                      Message
	 * @return curseur incremente
	 */
	protected int redirectionSelonLeChoix(final int curseurActuel, final List<Commande> commandes) {
		return curseurActuel + 1;
	}

	/**
	 * Calculer la hauteur d'un texte.
	 * 
	 * @return hauteur du texte a l'ecran (en pixels)
	 */
	protected final int calculerHauteurTexte() {
		final int nombreDeLignesDuTexte;
		if (this.texte == null || this.texte.size() == 0) {
			// texte null
			nombreDeLignesDuTexte = 0;
		} else {
			final int langue = Main.langue;
			final String texteDansUneLangue = this.texte.get(langue < this.texte.size() ? langue : 0);
			if ("".equals(texteDansUneLangue)) {
				// texte vide
				nombreDeLignesDuTexte = 0;
			} else {
				// plusieurs lignes
				nombreDeLignesDuTexte = 1 + StringUtils.countMatches(texteDansUneLangue, "\n");
			}
		}
		final int hauteurLigne = Texte.Taille.MOYENNE.pixels + Texte.INTERLIGNE;
		final int hauteurTexte = nombreDeLignesDuTexte * hauteurLigne;
		return hauteurTexte;
	}

	/** Le Joueur appuie sur la touche pendant le Message */
	public void haut() {
		// rien
	}

	/** Le Joueur appuie sur la touche pendant le Message */
	public void bas() {
		// rien
	}

	/** Le Joueur appuie sur la touche pendant le Message */
	public void gauche() {
		// rien
	}

	/** Le Joueur appuie sur la touche pendant le Message */
	public void droite() {
		// rien
	}

	/** Le Joueur appuie sur la touche pendant le Message */
	public void action() {
		this.touchePresseePourQuitterLeMessage = true;
	}

	/**
	 * Obtenir l'image de la boite du message. Elle peut etre vide.
	 * 
	 * @return fond de la boite de texte
	 */
	public static BufferedImage imageBoiteMessage() {
		if (masquerBoiteMessage) {
			return IMAGE_BOITE_MESSAGE_VIDE;
		}
		return (faceset == null) ? IMAGE_BOITE_MESSAGE_PLEINE : IMAGE_BOITE_MESSAGE_PLEINE_FACESET;
	}

	/**
	 * Une Commande qui prend du temps a s'executer fait oublier le faceset
	 * 
	 * @param commande qui prend du temps
	 * @param event    qui fige les autres (par un Message)
	 */
	public static void oublierLeFaceset(Commande commande, Event event) {
		if (!(commande instanceof Message || commande instanceof AppelerUnScript)
				&& event.map.lecteur.eventQuiALanceStopEvent == event) {
			LOG.debug("L'Event " + event.nom + " a efface le faceset avec la Commande "
					+ commande.getClass().getSimpleName());
			Message.faceset = null;
		}
	}

}
