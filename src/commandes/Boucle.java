package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import conditions.ConditionTouche;
import main.Commande;
import main.Lecteur;

/**
 * Une Boucle répète indéfiniment les Commandes qu'elle contient.
 */
public class Boucle extends Commande implements CommandeEvent, CommandeMenu {
	protected static final Logger LOG = LogManager.getLogger(Boucle.class);
	/** Au bout de cette fraction de la frame, on considère que le calcul est trop long */
	private static final int TIMEOUT_FRACTION_DE_FRAME = 4;
	
	public int numero; //le numéro de Boucle est le même que le numéro de fin de Boucle qui correspond
	public long debutBoucle = -1;
	private Boolean laBoucleAttendUneActionDuJoueur = null;

	/**
	 * Constructeur explicite
	 * @param numero identifiant de la Boucle
	 */
	public Boucle(final int numero) {
		this.numero = numero;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Boucle(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero") );
	}
	
	/**
	 * Une Boucle est une Commande Event, elle peut être executée pour faire des sauts de curseur.
	 * Son execution est instantanée.
	 * @param curseurActuel position du curseur avant l'execution
	 * @param commandes liste des Commandes de la Page
	 * @return nouvelle position du curseur
	 */
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		if (this.debutBoucle == -1) {
			this.debutBoucle = System.currentTimeMillis();
		}
		
		// cas où la boucle a duré trop longtemps
		if (!laBoucleAttendUneActionDuJoueur(commandes)
				&& System.currentTimeMillis() - this.debutBoucle >= Lecteur.DUREE_FRAME/TIMEOUT_FRACTION_DE_FRAME) {
			LOG.warn("Boucle interrompue car a duré trop longtemps.");
			this.debutBoucle = -1;
			return curseurActuel;
		}
		
		return curseurActuel+1;
	}
	
	/**
	 * Les Commandes de Menu sont instantannées et donc n'utilisent pas de curseur.
	 */
	public void executer() {
		//rien
	}
	
	/**
	 * La Boucle dépend-elle d'une action du Joueur ?
	 * Si oui, elle ne doit pas échouer par Timeout car ce n'est pas un calcul.
	 * @param commandes liste des Commandes de la Page
	 * @return true si le Joueur doit répondre, false sinon
	 */
	private boolean laBoucleAttendUneActionDuJoueur(final ArrayList<Commande> commandes) {
		if (this.laBoucleAttendUneActionDuJoueur == null) {
			this.laBoucleAttendUneActionDuJoueur = false;
			//rechercher si la Boucle attend une action de la part du Joueur
			boolean onATrouveLeDebutDeBoucle = false;
			boolean onATrouveLaFinDeBoucle = false;
			for (Commande commande : commandes) {
				//on cherche le début de Boucle
				if (commande instanceof Boucle && ((Boucle) commande).numero == this.numero) {
					onATrouveLeDebutDeBoucle = true;
				}
				//on cherche la fin de Boucle
				if (commande instanceof BoucleFin && ((BoucleFin) commande).numero == this.numero) {
					onATrouveLaFinDeBoucle = true;
				}
				//les Conditions de réponse du Joueur sont situées entre les deux
				if (commande instanceof ConditionTouche && onATrouveLeDebutDeBoucle && !onATrouveLaFinDeBoucle) {
					this.laBoucleAttendUneActionDuJoueur = true;
					break;
				}
			}
		}
		return this.laBoucleAttendUneActionDuJoueur;
	}
}
