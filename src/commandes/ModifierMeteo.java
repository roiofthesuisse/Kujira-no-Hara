package commandes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import map.meteo.Trochoide;
import map.meteo.Meteo;
import map.meteo.Neige;
import map.meteo.Pluie;
import map.meteo.TypeDeMeteo;

/**
 * Changer l'effet météorologique actuel de la Map.
 */
public class ModifierMeteo extends Commande implements CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(ModifierMeteo.class);
	
	private final TypeDeMeteo typeDeMeteo;
	private final HashMap<String, Object> parametres;
	
	/**
	 * Constructeur explicite
	 * @param nom de l'intempérie souhaitée
	 * @param parametres de l'intempérie souhaitée
	 */
	public ModifierMeteo(final String nom, final HashMap<String, Object> parametres) {
		this.typeDeMeteo = TypeDeMeteo.obtenirParNom(nom);
		LOG.info("Nouvelle météo : "+this.typeDeMeteo.nom);
		this.parametres = parametres;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ModifierMeteo(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("type") ? (String) parametres.get("type") : null,
			  parametres	
		);
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
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
				nouvelleMeteo = new Trochoide(intensite, dureeDeVie, vitesseX, vitesseY, vitesseRotation, rayonX, rayonY, nomImage);
			} catch (IOException e) {
				LOG.error("Impossible d'instancier la Météo personnalisée.", e);
				nouvelleMeteo = ancienneMeteo;
			}
			break;
		default:
			break;
		}
		
		if (Meteo.verifierSiIdentiques(nouvelleMeteo, ancienneMeteo)) {
			final String nouveauTypeMeteo = nouvelleMeteo != null ? nouvelleMeteo.getType().nom : "null";
			final String ancienTypeMeteo = ancienneMeteo != null ? ancienneMeteo.getType().nom : "null";
			LOG.warn("Cette météo est identique à l'ancienne, on ne fait rien. "+ancienTypeMeteo+"->"+nouveauTypeMeteo);
		} else {
			//la nouvelle météo proposée est différente de l'ancienne
			getPartieActuelle().meteo = nouvelleMeteo;
		}
		
		return curseurActuel + 1;
	}
}
