package conditions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import jeu.courrier.EtatCourrier;
import main.Main;

/**
 * Où en est la lettre ?
 */
public class ConditionEtatLettre extends Condition implements CommandeEvent, CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(ConditionEtatLettre.class);
	
	final int idLettre;
	final String nomEtatCourrier;
	
	/**
	 * Constructeur explicite
	 * @param idLettre identifiant de la lettre
	 * @param nomEtatCourrier etat attendu
	 */
	public ConditionEtatLettre(final int idLettre, final String nomEtatCourrier) {
		this.idLettre = idLettre;
		this.nomEtatCourrier = nomEtatCourrier;
	}
	
	@Override
	public final boolean estVerifiee() {
		final EtatCourrier etatAttendu = EtatCourrier.parNom(this.nomEtatCourrier);
		if (etatAttendu == null) {
			LOG.error("Etat de courrier inconnu : "+this.nomEtatCourrier);
			return false;
		}
		
		return etatAttendu.equals(Main.getPartieActuelle().lettresAEnvoyer.get(this.idLettre).etat);
	}

	@Override
	public final boolean estLieeAuHeros() {
		return false;
	}

}
