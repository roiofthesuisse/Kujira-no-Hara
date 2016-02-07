package conditions;

import commandes.CommandeEvent;
import map.Event;
import map.Heros;
import map.PageEvent;

/**
 * Est-ce que le Héros est en contact avec l'Event ?
 * Le contact a deux sens :
 * - si l'Event est traversable, le contact signifie que le Héros est majoritairement superposé à lui ;
 * - si l'Event n'est pas traversable, le contact signifie que le Héros et l'Event se touchent par un côté de la Hitbox.
 */
public class ConditionContact extends Condition  implements CommandeEvent {
	private PageEvent page;
	
	@Override
	public final boolean estVerifiee() {
		//TODO delete this method
		return estVerifieeInterne();
	}
	
	/**
	 * Méthode utilisée en interne, et dont estVerifiee() est l'ambassadrice publique.
	 * @return true si le Héros est au contact de cet Event, sinon false
	 */
	private boolean estVerifieeInterne() {
		try {
			final int pageActive = ((CommandeEvent) this).getPage().event.pageActive.numero;
			final int cettePage = ((CommandeEvent) this).getPage().numero;
			//il faut d'abord que la page ne soit pas ouverte
			if (pageActive==cettePage) {
				return false;
			}
		} catch (NullPointerException e) {
			//pas de page active
		}
		
		final Event event = ((CommandeEvent) this).getPage().event;
		final Heros heros = event.map.heros;
		final int xmin1 = heros.x;
		final int xmax1 = heros.x+heros.largeurHitbox;
		final int ymin1 = heros.y;
		final int ymax1 = heros.y+heros.hauteurHitbox;
		final int xmin2 = event.x;
		final int xmax2 = event.x+event.largeurHitbox;
		final int ymin2 = event.y;
		final int ymax2 = event.y+event.hauteurHitbox;
		//deux interprétations très différentes selon la traversabilité de l'event
		if (event.traversableActuel) {
			//genre une dalle au sol
			return ((xmin2<=xmin1&&xmax1<=xmax2) //vraiment dedans
				|| (xmin1<=xmin2&&xmin2<xmax1&&xmax1<=xmax2 && 2*(xmax1-xmin2)>=heros.largeurHitbox) //à cheval mais beaucoup (héros à gauche)
				|| (xmin2<=xmin1&&xmin1<xmax2&&xmax2<=xmax1 && 2*(xmax2-xmin1)>=heros.largeurHitbox))//à cheval mais beaucoup (héros à droite)
					&&
				((ymin2<=ymin1&&ymax1<=ymax2) //vraiment dedans
				|| (ymin1<=ymin2&&ymin2<ymax1&&ymax1<=ymax2 && 2*(ymax1-ymin2)>=heros.hauteurHitbox) //à cheval mais beaucoup (héros en haut)
				|| (ymin2<=ymin1&&ymin1<ymax2&&ymax2<=ymax1 && 2*(ymax2-ymin1)>=heros.hauteurHitbox)); //à cheval mais beaucoup (héros en bas)	
		} else {
			//genre une brique
			return (xmin1==xmax2 || xmin2==xmax1) && (
					(ymin2<=ymin1&&ymax1<=ymax2) //vraiment 
				|| (ymin2<=ymin1&&ymin1<ymax2&&ymax2<=ymax1 && 2*(ymax2-ymin1)>=heros.hauteurHitbox)
				|| (ymin1<=ymin2&&ymin2<ymax1&&ymax1<=ymax2 && 2*(ymax1-ymin2)>=heros.hauteurHitbox)
				)
					||
				(ymin1==ymax2 || ymin2==ymax1) && (
					(xmin2<=xmin1&&xmax1<=xmax2) //vraiment 
				|| (xmin2<=xmin1&&xmin1<xmax2&&xmax2<=xmax1 && 2*(xmax2-xmin1)>=heros.largeurHitbox)
				|| (xmin1<=xmin2&&xmin2<xmax1&&xmax1<=xmax2 && 2*(xmax1-xmin2)>=heros.largeurHitbox)
				);	
		}
	}
	
	/**
	 * C'est une Condition qui implique une proximité avec le Héros.
	 * @return true 
	 */
	public final boolean estLieeAuHeros() {
		return true;
	}

	@Override
	public final PageEvent getPage() {
		return this.page;
	}

	@Override
	public final void setPage(final PageEvent page) {
		this.page = page;
	}
	
}
