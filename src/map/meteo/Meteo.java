package map.meteo;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import utilitaire.Maths;

/**
 * Effet météorologique sur la Map.
 * La Météo est constituée de particules animées à l'écran.
 */
public abstract class Meteo {
	protected static int dureeDeVieParticule;
	protected static int ratioIntensiteNombreDeParticules;
	
	/**
	 * Obtenir le type de cette Météo.
	 * @return élément de l'énumération TypeDeMeteo
	 */
	public abstract TypeDeMeteo getType();
	public int intensite;
	protected int nombreDeParticulesNecessaires;
	protected ArrayList<Particule> particules = new ArrayList<Particule>();
	
	/**
	 * Fabriquer l'image représentant l'effet Météo.
	 * @param numeroFrame numéro de la frame actuelle du LecteurMap
	 * @return image de l'effet Météo à superposer à l'écran
	 */
	public abstract BufferedImage calculerImage(int numeroFrame);

	/**
	 * Ajouter une goutte à la pluie.
	 */
	protected abstract void ajouterUneGoutte();
	
	/**
	 * Ajouter des gouttes à la pluie en fonction de l'intensité voulue pour l'intempérie.
	 * @param numeroFrame numéro de la frame actuelle du LecteurMap
	 */
	protected final void ajouterDesParticulesSiNecessaire(final int numeroFrame) {
		if (particules.size() < nombreDeParticulesNecessaires) {
			//il faut rajouter des gouttes
			if (dureeDeVieParticule <= nombreDeParticulesNecessaires
			&& numeroFrame % (nombreDeParticulesNecessaires/dureeDeVieParticule) == 0) {
				//ajouter les gouttes au fur et à mesure, pour éviter qu'elles arrivent toutes en groupe
				ajouterUneGoutte();
				if (Maths.generateurAleatoire.nextInt(2) == 1) {
					ajouterUneGoutte();
				}
			} else {
				//ajouter plusieurs gouttes à la fois car elles disparaissent plus vite qu'elles apparaissent
				for (int i = 0; i<=nombreDeParticulesNecessaires/dureeDeVieParticule; i++) {
					ajouterUneGoutte();
				}
			}
		}
	}
	
	/**
	 * Calculer la position horizontae de la particule au cours du temps.
	 * @param particule et ses caractéristiques
	 * @return position horizontale de la particule
	 */
	protected abstract int calculerXParticule(final Particule particule);
	
	/**
	 * Calculer la position verticale de la particule au cours du temps.
	 * @param particule et ses caractéristiques
	 * @return position horizontale de la particule
	 */
	protected abstract int calculerYParticule(final Particule particule);
	
	/**
	 * Vérifier si deux Météos sont identiques.
	 * @param m1 une météo
	 * @param m2 une autre météo
	 * @return si elles sont équivalentes
	 */
	public static boolean verifierSiIdentiques(final Meteo m1, final Meteo m2) {
		if (m1 == null && m2 == null) {
			return true;
		}
		if (m1 == null && m2 != null) {
			return false;
		}
		if (m1 != null && m2 == null) {
			return false;
		}
		if (!m1.getType().equals(m2.getType())) {
			return false;
		}
		if (m1.intensite != m2.intensite) {
			return false;
		}
		return true;
	}
}
