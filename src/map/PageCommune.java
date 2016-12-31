package map;

import org.json.JSONObject;

import conditions.Condition;
import main.Commande;

/**
 * Pages de code commun à toutes les Maps.
 */
public class PageCommune extends PageEvent {
	public boolean active;
	
	/**
	 * Constructeur explicite
	 * @param pageJSON objet JSON représentant la Page
	 */
	public PageCommune(final JSONObject pageJSON) {
		super(-1, pageJSON, -1); //pas d'Event correspondant, pas de numéro
		this.active = false;
	}

	/**
	 * Activer la Page si les Conditions sont vérifiées.
	 */
	public final void essayerDActiver() {
		if (this.conditions!=null && this.conditions.size()>0) {
			//la Page a des Conditions de déclenchement, on les analyse
			boolean cettePageConvient = true;
			//si une Condition est fausse, la Page ne convient pas
			for (int j = 0; j<this.conditions.size() && cettePageConvient; j++) {
				final Condition cond = this.conditions.get(j);
				if (!cond.estVerifiee()) {
					//la Condition n'est pas vérifiée
					cettePageConvient = false;
				}
			}
			//si toutes les Conditions sont vérifiées, on active la Page
			if (cettePageConvient) {
				this.active = true;
			}
			
		} else {
			//aucune Condition nécessaire pour cette Page, donc la Page est activée
			this.active = true;
		}
	}
	
	
	@Override
	public final void executer() {
		if (commandes!=null) {
			try {
				if (curseurCommandes >= commandes.size()) {
					curseurCommandes = 0;
				}
				boolean onAvanceDansLesCommandes = true;
				while (onAvanceDansLesCommandes) {
					final int ancienCurseur = curseurCommandes;
					final Commande commande = this.commandes.get(curseurCommandes);
					commande.page = this; //on apprend à la Commande depuis quelle Page elle est appelée
					curseurCommandes = commande.executer(curseurCommandes, commandes);
					if (curseurCommandes==ancienCurseur) { 
						//le curseur n'a pas changé, c'est donc une commande qui prend du temps
						onAvanceDansLesCommandes = false;
					}
				}
			} catch (IndexOutOfBoundsException e) {
				//on a fini la page
				curseurCommandes = 0;
				this.active = false;
			}
		}
	}
}
