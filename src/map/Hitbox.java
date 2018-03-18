package map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import jeu.Partie;
import main.Fenetre;
import main.Main;
import utilitaire.InterpreteurDeJson;
import utilitaire.graphismes.Graphismes;

/**
 * Une hitbox peut être assignée à une arme afin de calculer sa portée et son étendue.
 */
public final class Hitbox {
	private static final Logger LOG = LogManager.getLogger(Hitbox.class);
	public static final HashMap<Integer, Hitbox> ZONES_D_ATTAQUE = chargerLesZonesDAttaqueDuJeu();
	
	public final int portee;
	public final int etendue;
	
	/**
	 * Constructeur explicite
	 * @param portee : profondeur de la zone d'attaque
	 * @param etendue : largeur de la zone d'attaque
	 */
	private Hitbox(final int portee, final int etendue) {
		this.portee = portee;
		this.etendue = etendue;
	}
	
	/**
	 * Cet Event cible est-il dans la zone d'attaque del'Event attaquant ?
	 * @param cible un Event à atteindre
	 * @param attaquant un Event qui cherche à atteindre la cible
	 * @return true si l'Event est dans la zone d'attaque, false sinon
	 */
	public boolean estDansZoneDAttaque(final Event cible, final Event attaquant) {
		final Partie partieActuelle = Main.getPartieActuelle();
		final boolean estCeQueLeHerosAUneArme = (partieActuelle.nombreDArmesPossedees > 0) && partieActuelle.getArmeEquipee()!=null;
		if (estCeQueLeHerosAUneArme) {
			//on calcule les bords de la zone d'attaque en fonction de l'orientation du héros
			final int[] coord = this.calculerCoordonneesAbsolues(attaquant);
			final int xminHitbox = coord[0];
			final int xmaxHitbox = coord[1];
			final int yminHitbox = coord[2];
			final int ymaxHitbox = coord[3];
			final int largeurHitbox = xmaxHitbox - xminHitbox;
			final int hauteurHitbox = ymaxHitbox - yminHitbox;
			
			final int xminEvent = cible.x;
			final int xmaxEvent = cible.x + cible.largeurHitbox;
			final int yminEvent = cible.y;
			final int ymaxEvent = cible.y + cible.hauteurHitbox;
			//calcul du croisement entre la bodybox de l'event et la hitbox de l'arme
			return lesDeuxRectanglesSeChevauchent(xminHitbox, xmaxHitbox, yminHitbox, ymaxHitbox, xminEvent, xmaxEvent, yminEvent, ymaxEvent, largeurHitbox, hauteurHitbox, cible.largeurHitbox, cible.hauteurHitbox);
		}
		return false;
	}
	
	/**
	 * Calcule les coordonnées x et y minimales et maximales du rectangle de la Hitbox.
	 * @param attaquant l'Event qui attaque
	 * @return xmin, xmax, ymin, ymax
	 */
	public int[] calculerCoordonneesAbsolues(final Event attaquant) {
		final int[] coordonneesAbsolues = new int[4];
		final int xminHitbox;
		final int xmaxHitbox;
		final int yminHitbox;
		final int ymaxHitbox;
		switch(attaquant.direction) {
			case Event.Direction.BAS :
				xminHitbox = (attaquant.x+attaquant.largeurHitbox/2) - this.etendue/2;
				xmaxHitbox = (attaquant.x+attaquant.largeurHitbox/2) + this.etendue/2;
				yminHitbox = attaquant.y+attaquant.hauteurHitbox;
				ymaxHitbox = attaquant.y+attaquant.hauteurHitbox + this.portee;
				break;
			case Event.Direction.GAUCHE :
				xminHitbox = attaquant.x - this.portee;
				xmaxHitbox = attaquant.x;
				yminHitbox = (attaquant.y+attaquant.hauteurHitbox/2) - this.etendue/2;
				ymaxHitbox = (attaquant.y+attaquant.hauteurHitbox/2) + this.etendue/2;
				break;
			case Event.Direction.DROITE :
				xminHitbox = attaquant.x+attaquant.largeurHitbox;
				xmaxHitbox = attaquant.x+attaquant.largeurHitbox + this.portee;
				yminHitbox = (attaquant.y+attaquant.hauteurHitbox/2) - this.etendue/2;
				ymaxHitbox = (attaquant.y+attaquant.hauteurHitbox/2) + this.etendue/2;
				break;
			default : //HAUT
				xminHitbox = (attaquant.x+attaquant.largeurHitbox/2) - this.etendue/2;
				xmaxHitbox = (attaquant.x+attaquant.largeurHitbox/2) + this.etendue/2;
				yminHitbox = attaquant.y - this.portee;
				ymaxHitbox = attaquant.y;
				break;
		}
		coordonneesAbsolues[0] = xminHitbox;
		coordonneesAbsolues[1] = xmaxHitbox;
		coordonneesAbsolues[2] = yminHitbox;
		coordonneesAbsolues[3] = ymaxHitbox;
		return coordonneesAbsolues;
	}

	/**
	 * Faire une capture d'écran avec deux rectangles
	 * @param x1min coordonnée x minimale du rectangle rouge
	 * @param x1max coordonnée x maximale du rectangle rouge
	 * @param y1min coordonnée y minimale du rectangle rouge
	 * @param y1max coordonnée y maximale du rectangle rouge
	 * @param x2min coordonnée x minimale du rectangle bleu
	 * @param x2max coordonnée x maximale du rectangle bleu
	 * @param y2min coordonnée y minimale du rectangle bleu
	 * @param y2max coordonnée y maximale du rectangle bleu
	 */
	public static void printCroisement(final int x1min, final int x1max, final int y1min, final int y1max, final int x2min, final int x2max, final int y2min, final int y2max) {
		//on part d'une image blanche
		final BufferedImage img = new BufferedImage(Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN, Graphismes.TYPE_DES_IMAGES);
		final Graphics2D graphics = img.createGraphics();
		graphics.setPaint(Color.white);
		graphics.fillRect(0, 0, Fenetre.LARGEUR_ECRAN, Fenetre.HAUTEUR_ECRAN);
		
		//on dessine le rouge
		graphics.setPaint(Color.red);
		graphics.fillRect(x1min, y1min, x1max-x1min, y1max-y1min);

		//on dessine le bleu
		graphics.setPaint(Color.blue);
		graphics.fillRect(x2min, y2min, x2max-x2min, y2max-y2min);
		
		//on enregistre
		Graphismes.sauvegarderImage(img, "croisements");
		
		//on n'utilisera plus jamais cette image
		graphics.dispose();
	}

	/**
	 * Calcule si deux rectangles sont proches. 
	 * S'ils ne sont pas proches, ils ne peuvent pas se croiser.
	 * Utile pour éliminer des cas lors des calculs de collisions.
	 * @param x1 coordonnée x minimale du rectangle 1
	 * @param y1 coordonnée y minimale du rectangle 1
	 * @param largeur1 largeur du rectangle 1
	 * @param hauteur1 hauteur du rectangle 1
	 * @param x2 coordonnée x minimale du rectangle 2
	 * @param y2 coordonnée y minimale du rectangle 2
	 * @param largeur2 largeur du rectangle 2
	 * @param hauteur2 hauteur du rectangle 2
	 * @return true si les rectancles ont une chance de se croiser, false s'ils sont trop éloignés pour que ce soit possible.
	 */
	private static boolean lesRectanglesSontProches(final int x1, final int y1, final int largeur1, final int hauteur1, final int x2, final int y2, final int largeur2, final int hauteur2) {
		final int deltaX = x2 - x1;
		final int deltaY = y2 - y1;
		final int distance = deltaX*deltaX + deltaY*deltaY;
		
		final int diagonale1 = largeur1*largeur1 + hauteur1*hauteur1;
		final int diagonale2 = largeur2*largeur2 + hauteur2*hauteur2;
		if (distance < diagonale1 || distance < diagonale2) {
			return true;
		}		
		return false;
	}
	
	/**
	 * Calcule si deux rectangles se chevauchent.
	 * @param x1min coordonnée x minimale du rectangle 1
	 * @param x1max coordonnée x maximale du rectangle 1
	 * @param y1min coordonnée y minimale du rectangle 1
	 * @param y1max coordonnée y maximale du rectangle 1
	 * @param x2min coordonnée x minimale du rectangle 2
	 * @param x2max coordonnée x maximale du rectangle 2
	 * @param y2min coordonnée y minimale du rectangle 2
	 * @param y2max coordonnée y maximale du rectangle 2
	 * @param largHitbox largeur du rectangle 1 (non recalculé pour les performances)
	 * @param hautHitbox hauteur du rectangle 1 (non recalculé pour les performances)
	 * @param largHitboxAutre largeur du rectangle 2 (non recalculé pour les performances)
	 * @param hautHitboxAutre hauteur du rectangle 2 (non recalculé pour les performances)
	 * @return true si les rectangles se chevauchent, false sinon
	 */
	public static final boolean lesDeuxRectanglesSeChevauchent(final int x1min, final int x1max, final int y1min, final int y1max, final int x2min, final int x2max, final int y2min, final int y2max, final int largHitbox, final int hautHitbox, final int largHitboxAutre, final int hautHitboxAutre) {
		//si les deux rectangles sont très éloignés, il ne peut pas y avoir collision
		if (!lesRectanglesSontProches(x1min, y1min, largHitbox, hautHitbox, x2min, y2min, largHitboxAutre, hautHitboxAutre)) {
			return false;
		}
		
		//premier cas : deux coins se chevauchent
		final boolean deuxCoinsSeChevauchent = ((x1min<=x2min && x2min<x1max && x1max<=x2max)
											  ||(x2min<=x1min && x1min<x2max && x2max<=x1max))
										    && ((y1min<=y2min && y2min<y1max && y1max<=y2max)
										      ||(y2min<=y1min && y1min<y2max && y2max<=y1max));
		if (deuxCoinsSeChevauchent) {
			return true; 
		}
		
		if (largHitbox==largHitboxAutre && hautHitbox==hautHitboxAutre) {
			//si deux events ont la même taille, ils ne peuvent se chevaucher que par le coin
			//(pour être plus exact : le cas où deux events de même taille se chevauchent par l'arête est un cas particulier de la formule du chevauchement par coin)
			return false;
		}
		
		if (largHitbox!=largHitboxAutre) { //si deux events n'ont pas la même largeur, ils peuvent se chevaucher par arête horizontale
			//deuxième cas : deux cotés de chevauchent
			final boolean deuxCotesSeChevauchent = ((x1min<=x2min && x2max<=x1max)&&((y2min<=y1min && y1min<y2max && y2max<=y1max)||(y1min<=y2min && y2min<y1max && y1max<=y2max)))
												|| ((x2min<=x1min && x1max<=x2max)&&((y1min<=y2min && y2min<y1max && y1max<=y2max)||(y2min<=y1min && y1min<y2max && y2max<=y1max)));
			if (deuxCotesSeChevauchent) {
				return true;
			}
		}
		if (hautHitbox!=hautHitboxAutre) { //si deux events n'ont pas la même hauteur, ils peuvent se chevaucher par arête verticale
			//deuxième cas : deux cotés de chevauchent
			final boolean deuxCotesSeChevauchent = ((y1min<=y2min && y2max<=y1max)&&((x2min<=x1min && x1min<x2max && x2max<=x1max)||(x1min<=x2min && x2min<x1max && x1max<=x2max)))
												|| ((y2min<=y1min && y1max<=y2max)&&((x1min<=x2min && x2min<x1max && x1max<=x2max)||(x2min<=x1min && x1min<x2max && x2max<=x1max)));
			if (deuxCotesSeChevauchent) {
				return true;
			}
		}
		
		//troisième cas : une hitbox incluse dans l'autre (pfff faut vraiment le faire exprès lol)
		final boolean unInclusDansLAutre = ((x1min<=x2min && x2max<=x1max)&&(y1min<=y2min && y2max<=y1max))
										|| ((x2min<=x1min && x1max<=x2max)&&(y2min<=y1min && y1max<=y2max));
		if (unInclusDansLAutre) {
			return true;
		}
		
		// quatrième cas : superposition en croix
		final boolean superpositionEnCroix = ((x2min<=x1min && x1max<=x2max)&&(y1min<=y2min && y2max<=y1max))
										  || ((x1min<=x2min && x2max<=x1max)&&(y2min<=y1min && y1max<=y2max));
		
		return superpositionEnCroix;
	}
	
	/**
	 * Charger les Zones d'attaque du jeu à partir du fichier JSON.
	 * @return zones d'attaque du jeu
	 */
	private static HashMap<Integer, Hitbox> chargerLesZonesDAttaqueDuJeu() {
		final JSONArray jsonZones;
		try {
			jsonZones = InterpreteurDeJson.ouvrirJsonZonesDAttaque();
		} catch (Exception e) {
			//problème lors de l'ouverture du fichier JSON
			LOG.error("Impossible de charger les zones d'attaque du jeu.", e);
			return null;
		}
		
		final HashMap<Integer, Hitbox> zones = new HashMap<>();
		for (Object objectArme : jsonZones) {
			final JSONObject jsonZone = (JSONObject) objectArme;
			final Integer id = jsonZone.getInt("id");
			final Hitbox zone = new Hitbox(jsonZone.getInt("portee"), jsonZone.getInt("etendue"));
			zones.put(id, zone);
		}
		return zones;
	}
	
}
