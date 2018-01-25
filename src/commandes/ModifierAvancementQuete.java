package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import jeu.Partie;
import jeu.Quete.AvancementQuete;
import main.Commande;

/**
 * Modifier l'état d'avancement de la Quête.
 */
public class ModifierAvancementQuete extends Commande implements CommandeEvent {

	private AvancementQuete avancement;
	private int numeroQuete;
	
	/**
	 * Constructeur explicite
	 * @param numeroQuete identifiant de la Quête à faire évoluer
	 * @param avancement nouvel état de la Quête
	 */
	public ModifierAvancementQuete(final int numeroQuete, final AvancementQuete avancement) {
		this.numeroQuete = numeroQuete;
		this.avancement = avancement;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ModifierAvancementQuete(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero"),
			  AvancementQuete.getEtat((String) parametres.get("avancement")));
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final Partie partieActuelle = getPartieActuelle();
		partieActuelle.avancementDesQuetes[this.numeroQuete] = this.avancement;
		
		return curseurActuel+1;
	}

}
