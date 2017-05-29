package map.meteo;

import java.awt.image.BufferedImage;
import java.io.IOException;

import main.Fenetre;
import utilitaire.Maths;
import utilitaire.graphismes.Graphismes;

/**
 * Il neige sur la Map.
 */
public class Aigrettes extends Meteo {
	//constantes
	private static final int DUREE_DE_VIE_FLOCON = 400;
	private static final int RATIO_INTENSITE_NOMBRE_DE_FLOCONS = 10;
	private static final BufferedImage IMAGE_FLOCON = chargerImageParticule();
	private static final double VITESSE_X_FLOCON = 0.2;
	private static final double VITESSE_Y_FLOCON = -0.5;
	private static final double VITESSE_ROTATION = 0.05;
	private static final int RAYON_TROCHOIDE_X = 48;
	private static final int RAYON_TROCHOIDE_Y = 32;
	
	/**
	 * Pluie
	 * @param intensite de l'intempérie
	 */
	public Aigrettes(final int intensite) {
		dureeDeVieParticule = DUREE_DE_VIE_FLOCON;
		ratioIntensiteNombreDeParticules = RATIO_INTENSITE_NOMBRE_DE_FLOCONS;
		this.intensite = intensite;
		this.nombreDeParticulesNecessaires = intensite*ratioIntensiteNombreDeParticules;
	}
	
	@Override
	public final TypeDeMeteo getType() {
		return TypeDeMeteo.AIGRETTES;
	}

	@Override
	public final BufferedImage calculerImage(final int numeroFrame) {
		ajouterDesParticulesSiNecessaire(numeroFrame);
		
		BufferedImage imageMeteo = new BufferedImage(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN, Graphismes.TYPE_DES_IMAGES);
		for (int i = 0; i<particules.size(); i++) {
			final Particule flocon = particules.get(i);
			imageMeteo = Graphismes.superposerImages(
				imageMeteo, 
				IMAGE_FLOCON, 
				calculerXParticule(flocon), 
				calculerYParticule(flocon),
				Graphismes.OPACITE_MAXIMALE
			);
			flocon.resteAVivre--;
			if (flocon.resteAVivre < 0) {
				//on retire le flocon de la liste
				particules.remove(0);
				i--;
			}
		}
		return imageMeteo;
	}

	@Override
	protected final int calculerXParticule(final Particule particule) {
		final int t = particule.resteAVivre;
		final int avanceeX = (int) (VITESSE_X_FLOCON * t);
		final int trochoideX = (int) (Math.sin(t*VITESSE_ROTATION) * RAYON_TROCHOIDE_X);
		return particule.x0 + avanceeX + trochoideX;
	}
	
	@Override
	protected final int calculerYParticule(final Particule particule) {
		final int t = particule.resteAVivre;
		final int avanceeY = (int) (VITESSE_Y_FLOCON * t);
		final int trochoideY = (int) (Math.cos(t*VITESSE_ROTATION) * RAYON_TROCHOIDE_Y);
		return particule.y0 + avanceeY + trochoideY;
	}

	/**
	 * Charger l'image d'un flocon.
	 * @return image d'un flocon.
	 */
	private static BufferedImage chargerImageParticule() {
		try {
			return Graphismes.ouvrirImage("Meteo", "flocon.png");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected final void ajouterUneParticule() {
		//offset pour centrer les particules sur l'écran
		final int offsetX = (int) (VITESSE_X_FLOCON * DUREE_DE_VIE_FLOCON)/2;
		final int offsetY = (int) (VITESSE_Y_FLOCON * DUREE_DE_VIE_FLOCON)/2;
		particules.add(new Particule(
				Maths.generateurAleatoire.nextInt(Fenetre.LARGEUR_ECRAN) - offsetX,
				Maths.generateurAleatoire.nextInt(Fenetre.HAUTEUR_ECRAN) - offsetY, 
				dureeDeVieParticule,
				0 //un seul type
		));
	}

}