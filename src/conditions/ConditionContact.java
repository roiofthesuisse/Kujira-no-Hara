package conditions;

import map.Event;
import map.Heros;

/**
 * Est-ce que le Héros est en contact avec l'Event ?
 * Le contact a deux sens :
 * - si l'Event est traversable, le contact signifie que le Héros est majoritairement superposé à lui ;
 * - si l'Event n'est pas traversable, le contact signifie que le Héros et l'Event se touchent par un côté de la Hitbox.
 */
public class ConditionContact extends Condition {
	
	@Override
	public final boolean estVerifiee() {
		try {
			int pageActive = this.page.event.pageActive.numero;
			int cettePage = this.page.numero;
			//il faut d'abord que la page ne soit pas ouverte
			if (pageActive==cettePage) {
				return false;
			}
		} catch (NullPointerException e) {
			//pas de page active
		}
		
		Event event = page.event;
		Heros heros = event.map.heros;
		int xmin1 = heros.x;
		int xmax1 = heros.x+heros.largeurHitbox;
		int ymin1 = heros.y;
		int ymax1 = heros.y+heros.hauteurHitbox;
		int xmin2 = event.x;
		int xmax2 = event.x+event.largeurHitbox;
		int ymin2 = event.y;
		int ymax2 = event.y+event.hauteurHitbox;
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
}
