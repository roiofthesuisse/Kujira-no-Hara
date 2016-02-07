package conditions;

import commandes.CommandeEvent;
import commandes.CommandeMenu;
import jeu.Objet;
import main.Fenetre;
import map.PageEvent;
import menu.ElementDeMenu;

/**
 * Le joueur possède-t-il l'Objet ?
 */
public class ConditionObjetPossede extends Condition implements CommandeEvent, CommandeMenu {
	private PageEvent page;
	private ElementDeMenu element;
	
	private int numeroObjet;
	
	/**
	 * Constructeur explicite
	 * @param objet identifiant de l'Objet : numéro ou nom
	 */
	public ConditionObjetPossede(final Object objet) {
		try {
			this.numeroObjet = (Integer) objet;
		} catch (Exception e) {
			this.numeroObjet = Objet.objetsDuJeuHash.get((String) objet).numero;
		}
	}
		
	
	@Override
	public final boolean estVerifiee() {
		return Fenetre.getPartieActuelle().objetsPossedes[this.numeroObjet] >= 1;
	}

	@Override
	public final boolean estLieeAuHeros() {
		return false;
	}
	
	@Override
	public final ElementDeMenu getElement() {
		return this.element;
	}

	@Override
	public final void setElement(final ElementDeMenu element) {
		this.element = element;
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
