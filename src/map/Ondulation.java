package map;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import main.Fenetre;
import utilitaire.graphismes.Graphismes;

public class Ondulation {
	public final int nombreDeVagues;
	public final int amplitude;
	public final int lenteur;
	
	Ondulation(final int nombreDeVagues, final int amplitude, final int lenteur) {
		this.nombreDeVagues = nombreDeVagues;
		this.amplitude = amplitude;
		this.lenteur = lenteur;
	}
	

    /**
     * Faie onduler l'écran car on est sous l'eau bloup bloup bloup.
     * @param ecran à déformer
     * @param frame temps
     * @return écran déformé
     */
	public static BufferedImage faireOndulerLEcran(BufferedImage ecran, int frame) {
		WritableRaster rasterEcran = ecran.getRaster();
		BufferedImage resultat = Graphismes.ecranVide();
		WritableRaster rasterResultat = resultat.getRaster();
		final double ratioSinus = 2*Math.PI/Fenetre.HAUTEUR_ECRAN;
		final int nombreDeVagues = 4;
		final int amplitudeDesVagues = 8;
		final int lenteurDesVagues = 4;
		for(int i=0; i<Fenetre.HAUTEUR_ECRAN ; i++){
			int[] pixels = new int[Fenetre.LARGEUR_ECRAN*4];
			int h = i + (int) (Math.round(amplitudeDesVagues * Math.sin(nombreDeVagues*i*ratioSinus + frame/lenteurDesVagues)));
			if (h >= Fenetre.HAUTEUR_ECRAN) {
				h = Fenetre.HAUTEUR_ECRAN-1;
			} else if(h < 0) {
				h = 0;
			}
			pixels = rasterEcran.getPixels(0, h, Fenetre.LARGEUR_ECRAN, 1, pixels);
			rasterResultat.setPixels(0, i, Fenetre.LARGEUR_ECRAN, 1, pixels); 
		}
		return resultat;
	}
}
