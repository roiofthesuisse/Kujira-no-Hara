package map.positionInitiale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import map.Transition;

/**
 * Position initiale du Heros sur la Map.
 */
public abstract class PositionInitiale {
	protected static final Logger LOG = LogManager.getLogger(PositionInitiale.class);
	
	/**
	 * Calculer xHeros, yHeros, directionHeros sur la nouvelle Map
	 * @param largeurMap (en carreaux)
	 * @param hauteurMap (en carreaux)
	 * @param transition type de Transition qui introduit la nouvelle Map
	 * @return tableau de 3 entiers
	 */
	public abstract int[] calculer(int largeurMap, int hauteurMap, Transition transition);

}

