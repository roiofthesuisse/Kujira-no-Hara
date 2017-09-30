package map.meteo;

import java.awt.image.BufferedImage;
import java.io.IOException;

import main.Fenetre;
import utilitaire.Maths;
import utilitaire.graphismes.Graphismes;

/**
 * Intempérie personnalisable dont les particules ont une trajectoire trochoïdale.
 * <ul><li>t varie de 0 à dureeDeVie</li>
 * <li>x = x0 + vitesseX * t + rayonX * sin(t * vitesseRotation)</li>
 * <li>y = y0 + vitesseY * t + rayonY * cos(t * vitesseRotation)</li></ul>
 * Si rayonX et rayonY sont nuls, le mouvement est rectiligne.
 */
public class Trochoide extends Meteo {
	//constantes
	private static final int RATIO_INTENSITE_NOMBRE_DE_FLOCONS = 10;
	
	private final double vitesseX, vitesseY, vitesseRotation, rayonX, rayonY;
	private final BufferedImage imageParticule;
	
	/**
	 * Constructeur explicite
	 * @param intensite proportionnelle au nombre de particules à l'écran
	 * @param dureeDeVie (en nombre de frames) d'une particule
	 * @param vitesseX, vitesseY (en pixels par frame) de la direction générale des particules
	 * @param vitesseRotation (pulsation en radians par frame) de la particule autour de son axe
	 * @param rayonX, rayonY (en pixels) de la rotation de la particule autour de son axe
	 * @param nomImage nom du fichier image de la particule
	 * @throws IOException image de la particule introuvable
	 */
	public Trochoide(final int intensite, final int dureeDeVie, final double vitesseX, final double vitesseY, 
			final double vitesseRotation, final int rayonX, final int rayonY, final String nomImage) throws IOException {
		dureeDeVieParticule = dureeDeVie;
		ratioIntensiteNombreDeParticules = RATIO_INTENSITE_NOMBRE_DE_FLOCONS;
		this.intensite = intensite;
		this.nombreDeParticulesNecessaires = intensite*ratioIntensiteNombreDeParticules;
		this.vitesseX = vitesseX;
		this.vitesseY = vitesseY;
		this.vitesseRotation = vitesseRotation;
		this.rayonX = rayonX;
		this.rayonY = rayonY;
		
		this.imageParticule = Graphismes.ouvrirImage("Meteo", nomImage);

	}
	
	@Override
	public final TypeDeMeteo getType() {
		return TypeDeMeteo.TROCHOIDE;
	}

	@Override
	public final BufferedImage calculerImage(final int numeroFrame) {
		ajouterDesParticulesSiNecessaire(numeroFrame);
		
		BufferedImage imageMeteo = new BufferedImage(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN, Graphismes.TYPE_DES_IMAGES);
		for (int i = 0; i<particules.size(); i++) {
			final Particule flocon = particules.get(i);
			imageMeteo = Graphismes.superposerImages(
				imageMeteo, 
				imageParticule, 
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
	protected final int calculerXParticule(final Particule particule) {
		final int t = particule.resteAVivre;
		final int avanceeX = (int) (vitesseX * t);
		final int trochoideX = (int) (Math.sin(t*vitesseRotation) * rayonX);
		return particule.x0 + avanceeX + trochoideX;
	}
	
	@Override
	protected final int calculerYParticule(final Particule particule) {
		final int t = particule.resteAVivre;
		final int avanceeY = (int) (vitesseY * t);
		final int trochoideY = (int) (Math.cos(t*vitesseRotation) * rayonY);
		return particule.y0 + avanceeY + trochoideY;
	}

	@Override
	protected final void ajouterUneParticule() {
		//offset pour centrer les particules sur l'écran
		final int offsetX = (int) (vitesseX * dureeDeVieParticule)/2;
		final int offsetY = (int) (vitesseY * dureeDeVieParticule)/2;
		
		final Particule nouvelleParticule;
		final int x0 = Maths.generateurAleatoire.nextInt(Fenetre.LARGEUR_ECRAN) - offsetX;
		final int y0 = Maths.generateurAleatoire.nextInt(Fenetre.HAUTEUR_ECRAN) - offsetY;
		if (this.bassinDeParticules.size() > 0) {
			// On peut recycler une particule issue du bassin
			nouvelleParticule = this.rehabiliterParticule();
			nouvelleParticule.reinitialiser(x0, y0, dureeDeVieParticule, 0);
		} else {
			// Le bassin est vide, il faut créer une nouvelle particule
			nouvelleParticule = new Particule(x0, y0, dureeDeVieParticule, 0);
		}
		particules.add(nouvelleParticule);
	}

}