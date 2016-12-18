package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;

/**
 * Une Boucle répète indéfiniment les Commandes qu'elle contient.
 */
public class BoucleFin extends Commande implements CommandeEvent, CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(BoucleFin.class);
	
	public int numero; //le numéro de Boucle est le même que le numéro de fin de Boucle qui correspond

	/**
	 * Constructeur explicite
	 * @param numero identifiant de la Boucle
	 */
	public BoucleFin(final int numero) {
		this.numero = numero;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public BoucleFin(final HashMap<String, Object> parametres) {
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
		for (int i = 0; i < commandes.size(); i++) {
			final Commande commande = commandes.get(i);
			if (commande instanceof Boucle) {
				final Boucle debutDeBoucle = (Boucle) commande;
				if (debutDeBoucle.numero == this.numero) {
					//le début de Boucle a été trouvé
					return i;
				}
			}
		}
		//le début de Boucle n'a pas été trouvé
		LOG.error("Le début de boucle numéro "+numero+" n'a pas été trouvé !");
		return curseurActuel+1;
	}
	
	/**
	 * Les Commandes de Menu sont instantannées et donc n'utilisent pas de curseur.
	 */
	public void executer() {
		//rien
	}
}
