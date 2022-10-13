package commandes;

import java.util.HashMap;
import java.util.List;

import jeu.Partie;
import jeu.Quete;
import jeu.Quete.AvancementQuete;
import main.Commande;
import main.Main;
import map.LecteurMap;
import map.NotificationQuete;

/**
 * Modifier l'�tat d'avancement de la Qu�te.
 */
public class ModifierAvancementQuete extends Commande implements CommandeEvent {

	private AvancementQuete avancement;
	private int numeroQuete;

	/**
	 * Constructeur explicite
	 * 
	 * @param numeroQuete identifiant de la Qu�te a faire �voluer
	 * @param avancement  nouvel �tat de la Qu�te
	 */
	public ModifierAvancementQuete(final int numeroQuete, final AvancementQuete avancement) {
		this.numeroQuete = numeroQuete;
		this.avancement = avancement;
	}

	/**
	 * Constructeur g�n�rique
	 * 
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ModifierAvancementQuete(final HashMap<String, Object> parametres) {
		this((int) parametres.get("numero"), AvancementQuete.getEtat((String) parametres.get("avancement")));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		final Partie partieActuelle = getPartieActuelle();

		// est-ce une nouveaute ?
		boolean queteApprise = !AvancementQuete.CONNUE.equals(partieActuelle.avancementDesQuetes[this.numeroQuete])
				&& AvancementQuete.CONNUE.equals(this.avancement);
		boolean queteTerminee = !AvancementQuete.FAITE.equals(partieActuelle.avancementDesQuetes[this.numeroQuete])
				&& AvancementQuete.FAITE.equals(this.avancement);

		// mettre a jour l'avancement
		partieActuelle.avancementDesQuetes[this.numeroQuete] = this.avancement;

		// afficher une notification a l'ecran du joueur
		if (queteApprise || queteTerminee) {
			if (Main.lecteur instanceof LecteurMap) {
				Quete quete = Quete.quetesDuJeu[this.numeroQuete];
				NotificationQuete notification = new NotificationQuete(quete.nom, quete.getNomIcone(),
						quete.getIcone());
				((LecteurMap) Main.lecteur).notificationsQuetes.add(notification);
			}
		}

		return curseurActuel + 1;
	}

}
