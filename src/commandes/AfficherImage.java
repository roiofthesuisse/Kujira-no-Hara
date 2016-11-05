package commandes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;
import map.Picture;
import utilitaire.Graphismes;

/**
 * Afficher une image par dessus l'écran.
 */
public class AfficherImage extends Commande implements CommandeEvent {
	//constantes
	private static final int PAS_D_HOMOTHETIE = 100;
	
	private Picture picture;
	
	/**
	 * Constructeur explicite
	 * @param nomImage nom de l'image à afficher
	 * @param numero de l'image à afficher
	 * @param centre son origine est-elle son centre ? sinon, son origine est son coin haut-gauche
	 * @param x coordonnée d'affichage à l'écran (en pixels)
	 * @param y coordonnée d'affichage à l'écran (en pixels)
	 * @param zoomX étirement horizontal (en pourcents)
	 * @param zoomY étirement vertical (en pourcents)
	 * @param opacite de l'image (sur 255)
	 */
	public AfficherImage(final String nomImage, final int numero, final boolean centre, final int x, final int y, final int zoomX, final int zoomY, final int opacite) {
		try {
			this.picture = new Picture(nomImage, numero, centre, x, y, zoomX, zoomY, opacite);
		} catch (IOException e) {
			this.picture = null;
			System.err.println("Impossible d'ouvrir l'image "+nomImage);
			e.printStackTrace();
		}
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AfficherImage(final HashMap<String, Object> parametres) {
		this( (String) parametres.get("nomImage"),
				(int) parametres.get("numero"),
				parametres.containsKey("centre") ? (boolean) parametres.get("centre") : false,
				parametres.containsKey("x") ? (int) parametres.get("x") : 0,
				parametres.containsKey("y") ? (int) parametres.get("y") : 0,
				parametres.containsKey("zoomX") ? (int) parametres.get("zoomX") : PAS_D_HOMOTHETIE,
				parametres.containsKey("zoomY") ? (int) parametres.get("zoomY") : PAS_D_HOMOTHETIE,
				parametres.containsKey("opacite") ? (int) parametres.get("opacite") : Graphismes.OPACITE_MAXIMALE
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		Fenetre.getPartieActuelle().images.put(picture.numero, picture);
		return curseurActuel+1;
	}

}
