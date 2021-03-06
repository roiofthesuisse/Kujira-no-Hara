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
import map.LecteurMap;
import menu.Texte;
import utilitaire.graphismes.Graphismes;

/**
 * Afficher un Message dans une bo�te de dialogue
 */
public class Message extends Commande implements CommandeEvent {
	// constantes
	private static final Logger LOG = LogManager.getLogger(Message.class);
	protected static final int MARGE_DU_TEXTE = 24;
	protected static final String NOM_IMAGE_BOITE_MESSAGE = "parchotexte.png";
	protected static final BufferedImage IMAGE_BOITE_MESSAGE_PLEINE = chargerImageDeFondDeLaBoiteMessage();
	protected static final BufferedImage IMAGE_BOITE_MESSAGE_VIDE = Graphismes
			.creerUneImageVideDeMemeTaille(IMAGE_BOITE_MESSAGE_PLEINE);
	protected static BufferedImage imageBoiteMessage = IMAGE_BOITE_MESSAGE_PLEINE;
	protected static final boolean MASQUER_BOITE_MESSAGE_PAR_DEFAUT = false;
	protected static final Position POSITION_BOITE_MESSAGE_PAR_DEFAUT = Position.BAS;

	public ArrayList<String> texte;
	public BufferedImage image;
	private boolean touchePresseePourQuitterLeMessage = false;
	private boolean premiereFrameDAffichageDuMessage = true;
	public static boolean masquerBoiteMessage = MASQUER_BOITE_MESSAGE_PAR_DEFAUT;
	public static Position positionBoiteMessage = POSITION_BOITE_MESSAGE_PAR_DEFAUT;

	/**
	 * Position de la bo�te de Messages � l'�cran.
	 */
	public enum Position {
		BAS("bas", 76, 320), MILIEU("milieu", 76, 160), HAUT("haut", 76, 0);

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
		 * R�cup�rer la Position de la bo�te de Messages par son nom.
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
	 * @param texte affich� dans la bo�te de dialogue
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
		final LecteurMap lecteur = this.page.event.map.lecteur;
		lecteur.normaliserApparenceDesInterlocuteursAvantMessage(this.page.event);
		// si le Message � afficher est diff�rent du Message affich�, on change !
		if (ilFautReactualiserLImageDuMessage(lecteur)) {
			lecteur.messagePrecedent = lecteur.messageActuel;
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
	 * Charge l'image de fond de la bo�te de dialogue.
	 * 
	 * @return image de fond de la bo�te de dialogue
	 */
	private static BufferedImage chargerImageDeFondDeLaBoiteMessage() {
		try {
			return Graphismes.ouvrirImage("Pictures", NOM_IMAGE_BOITE_MESSAGE);
		} catch (IOException e) {
			LOG.error("impossible d'ouvrir l'image " + NOM_IMAGE_BOITE_MESSAGE, e);
			return null;
		}
	}

	/**
	 * Fabrique l'image du Message � partir de l'image de la bo�te de dialogue et du
	 * texte. M�thode d�riv�e par la classe Choix.
	 * 
	 * @return image du Message
	 */
	protected BufferedImage produireImageDuMessage() {
		// Partir de la bo�te de dialogue
		BufferedImage imageMessage = Graphismes.clonerUneImage(Message.imageBoiteMessage);
		// Ajout du texte
		final Texte t = new Texte(this.texte);
		t.actualiserImage();
		imageMessage = Graphismes.superposerImages(imageMessage, t.getImage(), MARGE_DU_TEXTE, MARGE_DU_TEXTE);
		LOG.debug("Texte du message actualis�");
		return imageMessage;
	}

	/**
	 * Ce n'est pas un Choix, juste un Message Normal, donc la Commande suivante est
	 * juste apr�s. M�thode d�riv�e par la classe Choix.
	 * 
	 * @param curseurActuel curseur dans la lecture des Commandes
	 * @param commandes     en cours d'execution, dont fait notamment partie ce
	 *                      Message
	 * @return curseur incr�ment�
	 */
	protected int redirectionSelonLeChoix(final int curseurActuel, final List<Commande> commandes) {
		return curseurActuel + 1;
	}

	/**
	 * Calculer la hauteur d'un texte.
	 * 
	 * @return hauteur du texte � l'�cran (en pixels)
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
}
