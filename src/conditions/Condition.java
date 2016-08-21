package conditions;

import java.util.ArrayList;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import main.Commande;

/**
 * Une Condition peut servir à définir le moment de déclenchement d'une Page, ou faire partie du code Event.
 */
public abstract class Condition extends Commande {
	public int numero = -1; //le numéro de condition est le même que le numéro de fin de condition qui correspond

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
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		//une Condition doit avoir un numéro pour être exécutée comme Commande Event
		if (this.numero == -1) {
			System.err.println("La condition "+this.getClass().getName()+" n'a pas de numéro !");
		}
		
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
					if (this instanceof CommandeEvent) {
						System.out.println("L'évènement n°"+this.page.event.numero+" n'a pas trouvé sa fin de condition "+this.numero+" :");
					}
					if (this instanceof CommandeMenu) {
						System.out.println("L'élément de menu n°"+this.element.id+" n'a pas trouvé sa fin de condition "+this.numero+" :");
					}
					e.printStackTrace();
				} catch (Exception e) {
					//pas une condition
				}
			}
			return nouveauCurseur+1;
		}
	}
	
	/** 
	 * Est-ce que la Condition demande un mouvement particulier du Héros ?
	 * Contact, ArrivéeAuContact, Parler... 
	 * @return false si la Condition est à considérer pour l'apparence d'un Event, false sinon
	 */
	public abstract boolean estLieeAuHeros();
	
	/**
	 * Les Commandes de Menu sont instantannées et donc n'utilisent pas de curseur.
	 * Cette méthode, exigée par CommandeMenu, est la même pour toutes les Conditions.
	 */
	public void executer() {
		//rien
	}
}
