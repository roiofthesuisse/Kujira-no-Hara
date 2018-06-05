package map;

import java.io.FileNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import map.positionInitiale.PositionInitiale;
import map.positionInitiale.PositionInitialeParAdjacence;

/** 
 * Map adjacente à une Map, permettant une téléportation automatique
 */
public class Adjacence {
	private static final Logger LOG = LogManager.getLogger(Adjacence.class);
	
	final int numeroMap;
	final Integer direction;
	final int decalage;
	final Transition transition;
	
	/**
	 * Constructeur explicite
	 * @param numeroMap numéro de la Map adjacente
	 * @param direction dans laquelle on va pour se rendre sur cette Map
	 * @param decalage perpendiculaire des cases
	 * @param transition pour introduire la Map suivante
	 */
	Adjacence(final int numeroMap, final Integer direction, final int decalage, final Transition transition) {
		this.numeroMap = numeroMap;
		this.direction = direction;
		this.decalage = decalage;
		this.transition = transition;
	}
	
	/**
	 * Y aller à partir de la Map actuelle.
	 * @param heros qui quitte la Map
	 */
	public void allerALaMapAdjacente(final Heros heros) {
		LOG.info("On sort pour aller à la map adjacente.");
		final LecteurMap nouveauLecteur = new LecteurMap();
		nouveauLecteur.transition = this.transition;
		// final int xHerosMapPrecedente, final int yHerosMapPrecedente
		final PositionInitiale positionInitiale = new PositionInitialeParAdjacence(this.decalage, 
				this.direction, heros.x, heros.y);
		try {
			final Map nouvelleMap = new Map(
					this.numeroMap, 
					nouveauLecteur, 
					heros, 
					null, //pas de Brouillard forcé
					positionInitiale
			);			
			nouveauLecteur.devenirLeNouveauLecteurMap(nouvelleMap);
		} catch (FileNotFoundException e) {
			LOG.error("Impossible d'ouvrir la Map adjacente ! Fichier introuvable : ", e);
		} catch (Exception e) {
			LOG.error("Impossible d'ouvrir la Map adjacente !", e);
		}
	}
}

