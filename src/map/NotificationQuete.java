package map;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import menu.Texte;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Notification affichee a l'ecran pour prevenir le joueur qu'une nouvelle quete
 * est disponible. Indique egalement les quetes resolues.
 */
public class NotificationQuete {
	private static final Logger LOG = LogManager.getLogger(NotificationQuete.class);

	/** duree (en frames) pendant laquelle la notification est affichee */
	private static final int DUREE_AFFICHAGE = 120;
	/** duree (en frames) du fondu en apparition et en disparition */
	private static final int DUREE_FONDU = 20;

	// Message d'annonce de la nouvelle quete
	private static final String[] ANNONCE_QUETE_PRINCIPALE = new String[] { "Quête principale : ", "Main quest: " };
	private static final String[] ANNONCE_QUETE_SECONDAIRE = new String[] { "Quête secondaire : ", "Side quest: " };
	private static final String[] ANNONCE_MYSTERE = new String[] { "Mystère : ", "Mystery: " };
	private static final String[] ANNONCE_MYSTERE_RESOLU = new String[] { "Mystère résolu : ", "Mystery solved: " };
	private static final String[] ANNONCE_QUETE_TERMINEE = new String[] { "Quête terminée : ", "Completed quest: " };
	private static final String[] ANNONCE_TYPE_INCONNU = new String[] { "[Type de quete inconnu]",
			"[Unknown quest type]" };

	// Nom de l'icone pour chaque type de quete
	private static final String NOM_ICONE_QUETE_PRINCIPALE = "quete principale a faire icon";
	private static final String NOM_ICONE_QUETE_SECONDAIRE = "quete a faire icon";
	private static final String NOM_ICONE_MYSTERE = "achievement icon";
	private static final String NOM_ICONE_MYSTERE_RESOLU = "achievement obtenu icon";
	private static final String NOM_ICONE_QUETE_TERMINEE = "quete faite icon";

	private final List<String> nomQuete;
	private int temps = 0;
	private final String nomIcone;
	private final BufferedImage icone;

	// TODO ne recalculer l'image que si les notifications ont change

	public NotificationQuete(List<String> nomQuete, String nomIcone, BufferedImage icone) {
		this.nomQuete = nomQuete;
		this.nomIcone = nomIcone;
		this.icone = icone;
	}

	public static BufferedImage dessinerLesNotificationsDeQuetes(BufferedImage ecran,
			List<NotificationQuete> notificationsQuetes) {

		// s'il n'y a pas de notification, on ne fait rien
		if (notificationsQuetes.isEmpty()) {
			return ecran;
		}

		BufferedImage resultat = ecran;
		int i = 0;
		for (NotificationQuete notification : notificationsQuetes) {
			// fondu en apparition et en disparition
			int opacite;
			int temps = notification.temps;
			if (temps < DUREE_FONDU) {
				opacite = Graphismes.OPACITE_MAXIMALE * temps / DUREE_FONDU;
			} else if (temps >= (DUREE_AFFICHAGE - DUREE_FONDU) && temps < DUREE_AFFICHAGE) {
				opacite = Graphismes.OPACITE_MAXIMALE * (DUREE_AFFICHAGE - temps) / DUREE_FONDU;
			} else if (temps >= DUREE_AFFICHAGE) {
				opacite = 0;
			} else {
				opacite = Graphismes.OPACITE_MAXIMALE;
			}

			// calculer l'image du texte
			String[] annonce;
			switch (notification.nomIcone) {
			case NOM_ICONE_QUETE_PRINCIPALE:
				annonce = ANNONCE_QUETE_PRINCIPALE;
				break;
			case NOM_ICONE_QUETE_SECONDAIRE:
				annonce = ANNONCE_QUETE_SECONDAIRE;
				break;
			case NOM_ICONE_MYSTERE:
				annonce = ANNONCE_MYSTERE;
				break;
			case NOM_ICONE_MYSTERE_RESOLU:
				annonce = ANNONCE_MYSTERE_RESOLU;
				break;
			case NOM_ICONE_QUETE_TERMINEE:
				annonce = ANNONCE_QUETE_TERMINEE;
				break;
			default:
				LOG.warn("Type de quete inconnu : " + notification.nomIcone);
				annonce = ANNONCE_TYPE_INCONNU;
				break;
			}
			List<String> nomQuete = notification.nomQuete;
			List<String> message = new ArrayList<>();
			message.add(annonce[0] + nomQuete.get(0));
			message.add(annonce[1] + nomQuete.get(1));
			final Texte texte = new Texte(message, Color.white, Color.black, Texte.Taille.MOYENNE);
			BufferedImage imageTexte = texte.getImage();

			// dessiner cadre
			int hauteurCadre = 32;
			int largeurCadre = imageTexte.getWidth() + 4;
			Color couleur = new Color(0, 0, 0, opacite);
			resultat = Graphismes.dessinerRectangle(resultat, 0, 128 + 32 * i, largeurCadre, hauteurCadre, couleur);

			// dessiner icone
			resultat = Graphismes.superposerImages(resultat, notification.icone, 0, 128 + 4 + 32 * i, false,
					Graphismes.PAS_D_HOMOTHETIE, Graphismes.PAS_D_HOMOTHETIE, opacite, ModeDeFusion.NORMAL,
					Graphismes.PAS_DE_ROTATION);

			// dessiner texte
			resultat = Graphismes.superposerImages(resultat, imageTexte, 32, 128 + 32 * i, false,
					Graphismes.PAS_D_HOMOTHETIE, Graphismes.PAS_D_HOMOTHETIE, opacite, ModeDeFusion.NORMAL,
					Graphismes.PAS_DE_ROTATION);
			i++;
		}
		return resultat;
	}

}
