package commandes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;

import main.Commande;
import map.LecteurMap;
import menu.Texte;
import utilitaire.graphismes.Graphismes;

/**
 * Afficher un Message dans une boîte de dialogue
 */
public class Message extends Commande implements CommandeEvent {
	//constantes
	protected static final int MARGE_DU_TEXTE = 24;
	protected static final String NOM_IMAGE_BOITE_MESSAGE = ".\\ressources\\Graphics\\Pictures\\parchotexte.png";
	protected static final BufferedImage IMAGE_BOITE_MESSAGE_PLEINE = chargerImageDeFondDeLaBoiteMessage();
	protected static final BufferedImage IMAGE_BOITE_MESSAGE_VIDE = Graphismes.creerUneImageVideDeMemeTaille(IMAGE_BOITE_MESSAGE_PLEINE);
	protected static BufferedImage imageBoiteMessage = IMAGE_BOITE_MESSAGE_PLEINE;
	
	public String texte;
	public BufferedImage image;
	private boolean touchePresseePourQuitterLeMessage = false;
	private boolean premiereFrameDAffichageDuMessage = true;
	
	/**
	 * Constructeur explicite
	 * @param texte affiché dans la boîte de dialogue
	 */
	public Message(final String texte) {
		this.texte = texte;
		this.touchePresseePourQuitterLeMessage = false;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Message(final HashMap<String, Object> parametres) {
		this( (String) parametres.get("texte") );
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final LecteurMap lecteur = this.page.event.map.lecteur;
		lecteur.normaliserApparenceDesInterlocuteursAvantMessage(this.page.event);
		//si le Message à afficher est différent du Message affiché, on change !
		if ( lecteur.messageActuel==null 
				|| !lecteur.messageActuel.texte.equals(this.texte) 
				|| siChoixLeCurseurATIlBouge()
				|| this.premiereFrameDAffichageDuMessage
		) {
			lecteur.messageActuel = this;
			this.image = produireImageDuMessage();
		}
		
		//fermer le message
		if ( this.touchePresseePourQuitterLeMessage ) {
			//on ferme le message
			lecteur.messageActuel = null;
			this.touchePresseePourQuitterLeMessage = false;
			this.premiereFrameDAffichageDuMessage = true;
			return redirectionSelonLeChoix(curseurActuel, commandes);
		} else {
			//on laisse le message ouvert
			this.premiereFrameDAffichageDuMessage = false;
			return curseurActuel;
		}
	}

	/**
	 * Charge l'image de fond de la boîte de dialogue.
	 * @return image de fond de la boîte de dialogue
	 */
	private static BufferedImage chargerImageDeFondDeLaBoiteMessage() {
		try {
			final BufferedImage imageMessage = ImageIO.read(new File(NOM_IMAGE_BOITE_MESSAGE));
			return imageMessage;
		} catch (IOException e) {
			System.out.println("impossible d'ouvrir l'image");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Fabrique l'image du Message à partir de l'image de la boîte de dialogue et du texte.
	 * Méthode dérivée par la classe Choix.
	 * @return image du Message
	 */
	protected BufferedImage produireImageDuMessage() {
		// Partir de la boîte de dialogue
		BufferedImage imageMessage = Graphismes.clonerUneImage(Message.imageBoiteMessage);
		
		// Ajout du texte
		final Texte t = new Texte(texte);
		imageMessage = Graphismes.superposerImages(imageMessage, t.image, MARGE_DU_TEXTE, MARGE_DU_TEXTE);
		return imageMessage;
	}
	
	/**
	 * Ce n'est pas un Choix, juste un Message Normal, donc le curseur n'a pas changé.
	 * On n'a jamais besoin de remplacer l'image du Message.
	 * Méthode dérivée par la classe Choix.
	 * @return false
	 */
	protected boolean siChoixLeCurseurATIlBouge() {
		return false;
	}
	
	/**
	 * Ce n'est pas un Choix, juste un Message Normal, donc la Commande suivante est juste après.
	 * Méthode dérivée par la classe Choix.
	 * @param curseurActuel
	 * @return curseur incrémenté
	 */
	protected int redirectionSelonLeChoix(final int curseurActuel, final ArrayList<Commande> commandes) {
		return curseurActuel+1;
	}
	
	protected final int calculerHauteurTexte() {
		final int nombreDeLignesDuTexte;
		if (this.texte == null || this.texte.equals("")) {
			nombreDeLignesDuTexte = 0; 
		} else {
			nombreDeLignesDuTexte = 1 + StringUtils.countMatches(this.texte, "\n");
		}
		final int hauteurLigne = Texte.TAILLE_MOYENNE + Texte.INTERLIGNE;
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
