package map.meteo;

import java.awt.image.BufferedImage;
import java.io.IOException;

import main.Fenetre;
import utilitaire.Maths;
import utilitaire.graphismes.Graphismes;

/**
 * Il pleut sur la Map.
 */
public class Pluie extends Meteo {
	//constantes
	private static final int DUREE_DE_VIE_GOUTTE = 30;
	private static final int RATIO_INTENSITE_NOMBRE_DE_GOUTTES = 30;
	private static final int RATIO_ENTRE_LES_DEUX_TAILLES_DE_GOUTTES = 2;
	private static final int VITESSE_X = -8;
	private static final int VITESSE_Y = 16;
	private static final BufferedImage IMAGE_GOUTTE1 = chargerImageGoutte1();
	private static final BufferedImage IMAGE_GOUTTE2 = chargerImageGoutte2();
	private static final int HAUTEUR_GOUTTE1 = IMAGE_GOUTTE1.getHeight();
	private static final int LARGEUR_GOUTTE1 = IMAGE_GOUTTE1.getWidth();
	private static final BufferedImage IMAGE_ECLABOUSSURE1 = chargerImageEclaboussure1();
	private static final int DEMI_LARGEUR_ECLABOUSSURE1 = IMAGE_ECLABOUSSURE1.getWidth()/2;
	private static final int DEMI_HAUTEUR_ECLABOUSSURE1 = IMAGE_ECLABOUSSURE1.getHeight()/2;
	private static final int FRAME_DE_FIN_DE_L_ECLABOUSSURE1 = -2;
	private static final BufferedImage IMAGE_ECLABOUSSURE2 = chargerImageEclaboussure2();
	private static final int DEMI_LARGEUR_ECLABOUSSURE2 = IMAGE_ECLABOUSSURE2.getWidth()/2;
	private static final int DEMI_HAUTEUR_ECLABOUSSURE2 = IMAGE_ECLABOUSSURE2.getHeight()/2;
	private static final int FRAME_DE_FIN_DE_L_ECLABOUSSURE2 = -4;
	
	/**
	 * Pluie
	 * @param intensite de l'intemp�rie
	 */
	public Pluie(final int intensite) {
		dureeDeVieParticule = DUREE_DE_VIE_GOUTTE;
		ratioIntensiteNombreDeParticules = RATIO_INTENSITE_NOMBRE_DE_GOUTTES;
		this.intensite = intensite;
		this.nombreDeParticulesNecessaires = intensite*ratioIntensiteNombreDeParticules;
	}
	
	@Override
	public final BufferedImage calculerImage(final int numeroFrame) {
		ajouterDesParticulesSiNecessaire(numeroFrame);
		
		BufferedImage imageMeteo = new BufferedImage(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN, Graphismes.TYPE_DES_IMAGES);
		for (int i = 0; i<particules.size(); i++) {
			
			//on affiche la goutte
			final Particule goutte = particules.get(i);
			if (goutte.resteAVivre >= 0) {
				final BufferedImage imageGoutte = goutte.type == 0 ? IMAGE_GOUTTE1 : IMAGE_GOUTTE2;
				
				imageMeteo = Graphismes.superposerImages(
					imageMeteo, 
					imageGoutte, 
					calculerXParticule(goutte), 
					calculerYParticule(goutte) - HAUTEUR_GOUTTE1, //pour que la flaque soit au meme endroit que la goutte
					Graphismes.OPACITE_MAXIMALE
				);
			}
				
			goutte.resteAVivre--;
			
			//la goutte atteint le sol, on affiche une �claboussure
			if (goutte.type == 0) { //seules les grosses gouttes font une �claboussure
				if (goutte.resteAVivre <= 0 
						&& goutte.resteAVivre > FRAME_DE_FIN_DE_L_ECLABOUSSURE1) {
					//�claboussure au sol
					imageMeteo = Graphismes.superposerImages(
						imageMeteo, 
						IMAGE_ECLABOUSSURE1, 
						goutte.x0 - DEMI_LARGEUR_ECLABOUSSURE1, 
						goutte.y0 - DEMI_HAUTEUR_ECLABOUSSURE1,
						Graphismes.OPACITE_MAXIMALE
					);
				} else if (goutte.resteAVivre <= FRAME_DE_FIN_DE_L_ECLABOUSSURE1 
						&& goutte.resteAVivre > FRAME_DE_FIN_DE_L_ECLABOUSSURE2) {
					
					//�claboussure au sol
					imageMeteo = Graphismes.superposerImages(
						imageMeteo, 
						IMAGE_ECLABOUSSURE2, 
						goutte.x0 - DEMI_LARGEUR_ECLABOUSSURE2, 
						goutte.y0 - DEMI_HAUTEUR_ECLABOUSSURE2,
						Graphismes.OPACITE_MAXIMALE
					);
				}
			}
			
			//mort de la goutte
			if ( (goutte.type == 0 && goutte.resteAVivre <= FRAME_DE_FIN_DE_L_ECLABOUSSURE2)
					|| (goutte.type != 0 && goutte.resteAVivre < 0) ) {
				//on retire la goutte de la liste
				congedierParticule(particules.get(0));
				i--;
			}
			
		}
		return imageMeteo;
	}

	@Override
	protected final void ajouterUneParticule() {
		final int tailleGoutte = Maths.generateurAleatoire.nextInt(RATIO_ENTRE_LES_DEUX_TAILLES_DE_GOUTTES);
		final int offsetX = LARGEUR_GOUTTE1;
		final int offsetY = HAUTEUR_GOUTTE1;
		
		final Particule nouvelleParticule;
		final int x0 = Maths.generateurAleatoire.nextInt(Fenetre.LARGEUR_ECRAN) - offsetX;
		final int y0 = Maths.generateurAleatoire.nextInt(Fenetre.HAUTEUR_ECRAN) + offsetY;
		if (this.bassinDeParticules.size() > 0) {
			// On peut recycler une particule issue du bassin
			nouvelleParticule = this.rehabiliterParticule();
			nouvelleParticule.reinitialiser(x0, y0, dureeDeVieParticule, tailleGoutte);
		} else {
			// Le bassin est vide, il faut creer une nouvelle particule
			nouvelleParticule = new Particule(x0, y0, dureeDeVieParticule, tailleGoutte);
		}
		particules.add(nouvelleParticule);
	}

	/**
	 * Charger l'image de la goutte de pluie.
	 * @return image de la goutte de pluie
	 */
	private static BufferedImage chargerImageGoutte1() {
		try {
			return Graphismes.ouvrirImage("Meteo", "pluie.png");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Charger l'image de la goutte de pluie.
	 * @return image de la goutte de pluie
	 */
	private static BufferedImage chargerImageGoutte2() {
		try {
			return Graphismes.ouvrirImage("Meteo", "pluie2.png");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Charger l'image de l'�claboussure.
	 * @return image de l'�claboussure
	 */
	private static BufferedImage chargerImageEclaboussure1() {
		try {
			return Graphismes.ouvrirImage("Meteo", "eclaboussure1.png");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Charger l'image de l'�claboussure.
	 * @return image de l'�claboussure
	 */
	private static BufferedImage chargerImageEclaboussure2() {
		try {
			return Graphismes.ouvrirImage("Meteo", "eclaboussure2.png");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public final TypeDeMeteo getType() {
		return TypeDeMeteo.PLUIE;
	}

	@Override
	protected final int calculerXParticule(final Particule particule) {
		return particule.x0 - particule.resteAVivre*VITESSE_X;
	}

	@Override
	protected final int calculerYParticule(final Particule particule) {
		return particule.y0 - particule.resteAVivre*VITESSE_Y;
	}

}
