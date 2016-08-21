package map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main.Fenetre;
import main.Lecteur;
import utilitaire.Graphismes;
import utilitaire.Maths;

/**
 * Il pleut sur la Map.
 */
public class MeteoPluie extends Meteo {
	private static final int DUREE_DE_VIE_GOUTTE = 30;
	private static final int VITESSE_X = -8;
	private static final int VITESSE_Y = 16;
	private ArrayList<int[]> gouttes = new ArrayList<int[]>();
	private static final BufferedImage IMAGE_GOUTTE = chargerImageGoutte();
	private static final int LARGEUR_GOUTTE = IMAGE_GOUTTE.getWidth();
	private static final int HAUTEUR_GOUTTE = IMAGE_GOUTTE.getHeight();
	private static final BufferedImage IMAGE_ECLABOUSSURE1 = chargerImageEclaboussure1();
	private static final int DEMI_LARGEUR_ECLABOUSSURE1 = IMAGE_ECLABOUSSURE1.getWidth()/2;
	private static final int DEMI_HAUTEUR_ECLABOUSSURE1 = IMAGE_ECLABOUSSURE1.getHeight()/2;
	private static final BufferedImage IMAGE_ECLABOUSSURE2 = chargerImageEclaboussure2();
	private static final int DEMI_LARGEUR_ECLABOUSSURE2 = IMAGE_ECLABOUSSURE2.getWidth()/2;
	private static final int DEMI_HAUTEUR_ECLABOUSSURE2 = IMAGE_ECLABOUSSURE2.getHeight()/2;
	
	public static final int RATIO_FORCE_NOMBRE_DE_GOUTTES = 10;
	
	final int nombreDeGouttesNecessaires;
	
	/**
	 * Pluie
	 * @param intensite de l'intempérie
	 */
	public MeteoPluie(final int intensite) {
		this.intensite = intensite;
		this.nombreDeGouttesNecessaires = intensite*RATIO_FORCE_NOMBRE_DE_GOUTTES;
	}
	
	@Override
	public final BufferedImage calculerImage(final int numeroFrame) {
		ajouterDesGouttesSiNecessaire(numeroFrame);
		
		BufferedImage imageMeteo = new BufferedImage(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN, Lecteur.TYPE_DES_IMAGES);
		for (int i = 0; i<gouttes.size(); i++) {
			final int[] goutte = gouttes.get(i);
			imageMeteo = Graphismes.superposerImages(
				imageMeteo, 
				IMAGE_GOUTTE, 
				goutte[0] - goutte[2]*VITESSE_X, 
				goutte[1] - goutte[2]*VITESSE_Y - HAUTEUR_GOUTTE,
				Graphismes.OPACITE_MAXIMALE
			);
			goutte[2]--;
			
			//la goutte atteint le sol
			if (goutte[2] == 0) {
				//éclaboussure au sol
				imageMeteo = Graphismes.superposerImages(
					imageMeteo, 
					IMAGE_ECLABOUSSURE1, 
					goutte[0] - DEMI_LARGEUR_ECLABOUSSURE1, 
					goutte[1] - DEMI_HAUTEUR_ECLABOUSSURE1,
					Graphismes.OPACITE_MAXIMALE
				);
			} else if (goutte[2] < 0) {
				//on retire la goutte de la liste
				gouttes.remove(0);
				i--;
				
				//éclaboussure au sol
				imageMeteo = Graphismes.superposerImages(
					imageMeteo, 
					IMAGE_ECLABOUSSURE2, 
					goutte[0] - DEMI_LARGEUR_ECLABOUSSURE2, 
					goutte[1] - DEMI_HAUTEUR_ECLABOUSSURE2,
					Graphismes.OPACITE_MAXIMALE
				);
			}
		}
		return imageMeteo;
	}

	/**
	 * Ajouter des gouttes à la pluie en fonction de l'intensité voulue pour l'intempérie.
	 * @param numeroFrame numéro de la frame actuelle du LecteurMap
	 */
	private void ajouterDesGouttesSiNecessaire(final int numeroFrame) {
		if (gouttes.size() < nombreDeGouttesNecessaires) {
			//il faut rajouter des gouttes
			if (DUREE_DE_VIE_GOUTTE <= nombreDeGouttesNecessaires
			&& numeroFrame % (nombreDeGouttesNecessaires/DUREE_DE_VIE_GOUTTE) == 0) {
				//ajouter les gouttes au fur et à mesure, pour éviter qu'elles arrivent toutes en groupe
				ajouterUneGoutte();
				if (Maths.generateurAleatoire.nextBoolean()) {
					ajouterUneGoutte();
				}
			} else {
				//ajouter plusieurs gouttes à la fois car elles disparaissent plus vite qu'elles apparaissent
				for (int i = 0; i<=nombreDeGouttesNecessaires/DUREE_DE_VIE_GOUTTE; i++) {
					ajouterUneGoutte();
				}
			}
		}
	}

	/**
	 * Ajouter une goutte à la pluie.
	 */
	private void ajouterUneGoutte() {
		this.gouttes.add(new int[]{
				Maths.generateurAleatoire.nextInt(Fenetre.LARGEUR_ECRAN) - LARGEUR_GOUTTE, 
				Maths.generateurAleatoire.nextInt(Fenetre.HAUTEUR_ECRAN) + HAUTEUR_GOUTTE, 
				DUREE_DE_VIE_GOUTTE
		});
	}

	/**
	 * Charger l'image de la goutte de pluie.
	 * @return image de la goutte de pluie
	 */
	private static BufferedImage chargerImageGoutte() {
		try {
			return ImageIO.read(new File(".\\ressources\\Graphics\\Meteo\\pluie.png"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Charger l'image de l'éclaboussure.
	 * @return image de l'éclaboussure
	 */
	private static BufferedImage chargerImageEclaboussure1() {
		try {
			return ImageIO.read(new File(".\\ressources\\Graphics\\Meteo\\eclaboussure1.png"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Charger l'image de l'éclaboussure.
	 * @return image de l'éclaboussure
	 */
	private static BufferedImage chargerImageEclaboussure2() {
		try {
			return ImageIO.read(new File(".\\ressources\\Graphics\\Meteo\\eclaboussure2.png"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public final TypeDeMeteo getType() {
		return Meteo.TypeDeMeteo.PLUIE;
	}

}
