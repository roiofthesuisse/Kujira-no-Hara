package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import main.Fenetre;
import map.LecteurMap;

/**
 * Faire défiler la Map dans une direction donnée, pour la décentrer du Héros.
 */
public class FaireDefilerLaMap extends Commande implements CommandeEvent {
	//constantes
	public static final int VITESSE_DEFILEMENT_PAR_DEFAUT = 4;
	
	private final int nombreDePixels;
	private final int vitesse;
	private final int direction;
	
	private boolean initialisationFaite = false;
	private int dejaFait;
	
	/**
	 * Constructeur explicite
	 * @param nombreDeCarreaux du défilement
	 * @param vitesse du défilement
	 * @param direction du défilement
	 */
	public FaireDefilerLaMap(final int nombreDeCarreaux, final int vitesse, final int direction) {
		this.nombreDePixels = Fenetre.TAILLE_D_UN_CARREAU*nombreDeCarreaux;
		this.vitesse = vitesse;
		this.direction = direction;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public FaireDefilerLaMap(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("nombreDeCarreaux"),
			parametres.containsKey("vitesse") ? (int) parametres.get("vitesse") : VITESSE_DEFILEMENT_PAR_DEFAUT,
			(int) parametres.get("direction")
		);
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		//réinitialisation de la Commande au cas où elle est jouée plusieurs fois
		if (!this.initialisationFaite) {
			this.dejaFait = 0;
			this.initialisationFaite = true;
		}
		
		//on déplace la caméra
		final LecteurMap lecteurMap = (LecteurMap) Fenetre.getFenetre().lecteur;
		switch(direction) {
		case 0: 
			lecteurMap.defilementY += this.vitesse;
			break;
		case 1:
			lecteurMap.defilementX -= this.vitesse;
			break;
		case 2:
			lecteurMap.defilementX += this.vitesse;
			break;
		default:
			lecteurMap.defilementY -= this.vitesse;
			break;
		}
		this.dejaFait += this.vitesse;
		
		if (this.dejaFait >= this.nombreDePixels) {
			//fini
			this.initialisationFaite = false;
			return curseurActuel+1;
		} else {
			//pas fini
			return curseurActuel;
		}
	}

}
