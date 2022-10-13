package map.meteo;

import java.awt.image.BufferedImage;
import java.io.IOException;

import main.Fenetre;
import utilitaire.Maths;
import utilitaire.graphismes.Graphismes;

/**
 * Il neige sur la Map.
 */
public class Neige extends Meteo {
	//constantes
	private static final int DURREE_DE_VIE_FLOCON = 300;
	private static final int RATIO_INTENSITE_NOMBRE_DE_FLOCONS = 60;
	private static final int RATIO_ENTRE_LES_DEUX_TAILLES_DE_FLOCONS = 2;
	private static final BufferedImage IMAGE_FLOCON1 = chargerImageFlocon1();
	private static final int VITESSE_Y_FLOCON1 = 2;
	private static final double RESSERRAGE_DE_LA_SINUSOIDE_FLOCON1 = 0.05;
	private static final double AMPLITUDE_DE_LA_SINUSOIDE_FLOCON1 = 16.0;
	private static final BufferedImage IMAGE_FLOCON2 = chargerImageFlocon2();
	private static final int VITESSE_Y_FLOCON2 = 3;
	private static final double RESSERRAGE_DE_LA_SINUSOIDE_FLOCON2 = 0.01;
	private static final double AMPLITUDE_DE_LA_SINUSOIDE_FLOCON2 = 64.0;
	
	
	
	/**
	 * Pluie
	 * @param intensite de l'intemp�rie
	 */
	public Neige(final int intensite) {
		dureeDeVieParticule = DURREE_DE_VIE_FLOCON;
		ratioIntensiteNombreDeParticules = RATIO_INTENSITE_NOMBRE_DE_FLOCONS;
		this.intensite = intensite;
		this.nombreDeParticulesNecessaires = intensite*ratioIntensiteNombreDeParticules;
	}
	
	@Override
	public final TypeDeMeteo getType() {
		return TypeDeMeteo.NEIGE;
	}

	@Override
	public final BufferedImage calculerImage(final int numeroFrame) {
		ajouterDesParticulesSiNecessaire(numeroFrame);
		
		BufferedImage imageMeteo = new BufferedImage(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN, Graphismes.TYPE_DES_IMAGES);
		for (int i = 0; i<particules.size(); i++) {
			final Particule flocon = particules.get(i);
			imageMeteo = Graphismes.superposerImages(
				imageMeteo, 
				flocon.type == 0 ? IMAGE_FLOCON1 : IMAGE_FLOCON2, 
				calculerXParticule(flocon), 
				calculerYParticule(flocon),
				Graphismes.OPACITE_MAXIMALE
			);
			flocon.resteAVivre--;
			if (flocon.resteAVivre < 0) {
				//on retire le flocon de la liste
				congedierParticule(particules.get(0));
				i--;
			}
		}
		return imageMeteo;
	}
	
	@Override
	protected final int calculerYParticule(final Particule particule) {
		final int vitesse = particule.type == 0 ? VITESSE_Y_FLOCON1 : VITESSE_Y_FLOCON2;
		
		final int avanceeY = particule.resteAVivre*vitesse;
		return particule.y0 - avanceeY;
	}

	@Override
	protected final int calculerXParticule(final Particule particule) {
		final int t = particule.resteAVivre;
		final int vitesse = particule.type == 0 ? VITESSE_Y_FLOCON1 : VITESSE_Y_FLOCON2;
		final double resserrage = particule.type == 0 ? RESSERRAGE_DE_LA_SINUSOIDE_FLOCON1 : RESSERRAGE_DE_LA_SINUSOIDE_FLOCON2;
		final double amplitude = particule.type == 0 ? AMPLITUDE_DE_LA_SINUSOIDE_FLOCON1 : AMPLITUDE_DE_LA_SINUSOIDE_FLOCON2;
		
		final int deltaX = (int) (Math.sin(resserrage * t * vitesse) * amplitude);
		return particule.x0 - deltaX;
	}

	/**
	 * Charger l'image d'un flocon.
	 * @return image d'un flocon.
	 */
	private static BufferedImage chargerImageFlocon1() {
		try {
			return Graphismes.ouvrirImage("Meteo", "flocon.png");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Charger l'image d'un flocon.
	 * @return image d'un flocon.
	 */
	private static BufferedImage chargerImageFlocon2() {
		try {
			return Graphismes.ouvrirImage("Meteo", "flocon2.png");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected final void ajouterUneParticule() {
		final int tailleFlocon = Maths.generateurAleatoire.nextInt(RATIO_ENTRE_LES_DEUX_TAILLES_DE_FLOCONS);
		final int vitesse = tailleFlocon == 0 ? VITESSE_Y_FLOCON1 : VITESSE_Y_FLOCON2;
		final int offsetFlocons = vitesse*dureeDeVieParticule; //pour combler le manque de flocons en bas de l'ecran
		
		final Particule nouvelleParticule;
		final int x0 = Maths.generateurAleatoire.nextInt(Fenetre.LARGEUR_ECRAN);
		final int y0 = Maths.generateurAleatoire.nextInt(Fenetre.HAUTEUR_ECRAN + offsetFlocons);
		if (this.bassinDeParticules.size() > 0) {
			// On peut recycler une particule issue du bassin
			nouvelleParticule = this.rehabiliterParticule();
			nouvelleParticule.reinitialiser(x0, y0, dureeDeVieParticule, tailleFlocon);
		} else {
			// Le bassin est vide, il faut cr�er une nouvelle particule
			nouvelleParticule = new Particule(x0, y0, dureeDeVieParticule, tailleFlocon);
		}
		particules.add(nouvelleParticule);
	}

}