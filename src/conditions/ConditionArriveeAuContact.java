package conditions;

/**
 * Est-ce que le Héros vient d'entrer en contact avec l'Event ?
 * Le contact a deux sens :
 * - si l'Event est traversable, le contact signifie que le Héros est majoritairement superposé à lui ;
 * - si l'Event n'est pas traversable, le contact signifie que le Héros et l'Event se touchent par un côté de la Hitbox.
 */
public class ConditionArriveeAuContact extends Condition {

	@Override
	public final boolean estVerifiee() {
		//l'Event est au contact du Héros maintenant
		final ConditionContact conditionContactMaintenant = new ConditionContact();
		conditionContactMaintenant.page = this.page;
		conditionContactMaintenant.numero = this.numero;
		final boolean reponse = conditionContactMaintenant.estVerifiee();
		
		//mais l'Event n'était pas encore au contact du Héros à la frame d'avant
		if (!this.page.event.estAuContactDuHeros) {
			this.page.event.estAuContactDuHeros = reponse;
			if (reponse) {
				System.out.println("ConditionArriveeAuContact"); //TODO retirer
			}
			return reponse;
		}
		
		//ici l'Event était déjà au contact du Héros, donc ce n'est pas une arrivée
		this.page.event.estAuContactDuHeros = reponse;
		return false;
	}

}
