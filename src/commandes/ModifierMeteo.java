package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import main.Fenetre;
import map.meteo.Aigrettes;
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
	private int intensite;
	
	/**
	 * Constructeur explicite
	 * @param nom de l'intempérie souhaitée
	 * @param intensite de l'intempérie souhaitée
	 */
	public ModifierMeteo(final String nom, final int intensite) {
		this.typeDeMeteo = TypeDeMeteo.obtenirParNom(nom);
		LOG.info("Nouvelle météo : "+this.typeDeMeteo.nom);
		this.intensite = intensite;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ModifierMeteo(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("type") ? (String) parametres.get("type") : null,
			  parametres.containsKey("intensite") ? (int) parametres.get("intensite") : 0	
		);
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		Meteo nouvelleMeteo = null;
		
		switch (this.typeDeMeteo) {
		case PLUIE:
			nouvelleMeteo = new Pluie(this.intensite);
			break;
		case NEIGE:
			nouvelleMeteo = new Neige(this.intensite);
			break;
		case AIGRETTES:
			nouvelleMeteo = new Aigrettes(this.intensite);
			break;
		default:
			break;
		}
		
		if (Meteo.verifierSiIdentiques(nouvelleMeteo, Fenetre.getPartieActuelle().meteo)) {
			LOG.warn("Cette météo est identique à l'ancienne, on ne fait rien."+ nouvelleMeteo.getType()+" "+Fenetre.getPartieActuelle().meteo.getType());
		} else {
			//la nouvelle météo proposée est différente de l'ancienne
			Fenetre.getPartieActuelle().meteo = nouvelleMeteo;
		}
		
		return curseurActuel + 1;
	}
}
