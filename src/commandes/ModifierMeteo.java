package commandes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import map.meteo.Meteo;
import map.meteo.Neige;
import map.meteo.Pluie;
import map.meteo.Trochoide;
import map.meteo.TypeDeMeteo;

/**
 * Changer l'effet m�t�orologique actuel de la Map.
 */
public class ModifierMeteo extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(ModifierMeteo.class);

	private final TypeDeMeteo typeDeMeteo;
	private final HashMap<String, Object> parametres;

	/**
	 * Constructeur explicite
	 * 
	 * @param nom        de l'intemp�rie souhait�e
	 * @param parametres de l'intemp�rie souhait�e
	 */
	public ModifierMeteo(final String nom, final HashMap<String, Object> parametres) {
		this.typeDeMeteo = TypeDeMeteo.obtenirParNom(nom);
		LOG.info("Nouvelle m�t�o : " + this.typeDeMeteo.nom);
		this.parametres = parametres;
	}

	/**
	 * Constructeur g�n�rique
	 * 
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ModifierMeteo(final HashMap<String, Object> parametres) {
		this(parametres.containsKey("type") ? (String) parametres.get("type") : null, parametres);
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		final Meteo ancienneMeteo = getPartieActuelle().meteo;
		Meteo nouvelleMeteo = null;

		final int intensite = parametres.containsKey("intensite") ? (int) parametres.get("intensite") : 0;

		switch (this.typeDeMeteo) {
		case PLUIE:
			nouvelleMeteo = new Pluie(intensite);
			break;
		case NEIGE:
			nouvelleMeteo = new Neige(intensite);
			break;
		case TROCHOIDE:
			final int dureeDeVie = (int) parametres.get("dureeDeVie");
			final double vitesseX = (double) parametres.get("vitesseX");
			final double vitesseY = (double) parametres.get("vitesseY");
			final double vitesseRotation = (double) parametres.get("vitesseRotation");
			final int rayonX = (int) parametres.get("rayonX");
			final int rayonY = (int) parametres.get("rayonY");
			final String nomImage = (String) parametres.get("image");
			try {
				nouvelleMeteo = new Trochoide(intensite, dureeDeVie, vitesseX, vitesseY, vitesseRotation, rayonX,
						rayonY, nomImage);
			} catch (IOException e) {
				LOG.error("Impossible d'instancier la M�t�o personnalis�e.", e);
				nouvelleMeteo = ancienneMeteo;
			}
			break;
		default:
			break;
		}

		if (Meteo.verifierSiIdentiques(nouvelleMeteo, ancienneMeteo)) {
			final String nouveauTypeMeteo = nouvelleMeteo != null ? nouvelleMeteo.getType().nom : "null";
			final String ancienTypeMeteo = ancienneMeteo != null ? ancienneMeteo.getType().nom : "null";
			LOG.warn("Cette m�t�o est identique a l'ancienne, on ne fait rien. " + ancienTypeMeteo + "->"
					+ nouveauTypeMeteo);
		} else {
			// la nouvelle m�t�o propos�e est diff�rente de l'ancienne
			getPartieActuelle().meteo = nouvelleMeteo;
		}

		return curseurActuel + 1;
	}
}
