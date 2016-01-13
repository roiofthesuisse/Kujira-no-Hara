package conditions;

import java.util.ArrayList;
import java.util.HashMap;

import comportementEvent.CommandeEvent;

/**
 * Une Condition peut servir à définir le moment de déclenchement d'une Page, ou faire partie du code Event.
 */
public abstract class Condition extends CommandeEvent {
	public int numero; //le numéro de condition est le même que le numéro de fin de condition qui correspond
	
	/**
	 * La Condition est elle vérifiée là maintenant ?
	 * @return true si vérifiée, false si non vérifiée
	 */
	public abstract boolean estVerifiee();
	
	/**
	 * Une Condition est une Commande Event, elle peut être executée pour faire des sauts de curseur.
	 * Son execution est instantanée.
	 * @param curseurActuel position du curseur avant l'execution
	 * @param commandes liste des Commandes de la Page
	 * @return nouvelle position du curseur
	 */
	public final int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
		if ( estVerifiee() ) {
			return curseurActuel+1;
		} else {
			int nouveauCurseur = curseurActuel;
			boolean onATrouveLaFinDeSi = false;
			while (!onATrouveLaFinDeSi) {
				nouveauCurseur++;
				try {
					//la fin de si a le même numero que la condition
					if ( ((Condition) commandes.get(nouveauCurseur)).numero == numero ) {
						onATrouveLaFinDeSi = true;
					}
				} catch (IndexOutOfBoundsException e) {
					System.out.println("L'évènement n°"+this.page.event.numero+" n'a pas trouvé sa fin de condition "+this.numero+" :");
					e.printStackTrace();
				} catch (Exception e) {
					//pas une condition
				}
			}
			return nouveauCurseur+1;
		}
	}
}
