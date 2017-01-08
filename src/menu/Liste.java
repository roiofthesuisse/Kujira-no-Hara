package menu;

import java.util.ArrayList;

import conditions.Condition;
import main.Commande;

/**
 * Une Liste est un tableau d'ElementsDeMenu à plusieurs lignes et colonnes.
 */
public class Liste extends ElementDeMenu {

	protected Liste(final int id, final boolean selectionnable, final int x, final int y, 
			final ArrayList<Commande> comportementSelection, final ArrayList<Commande> comportementConfirmation,
			final ArrayList<Condition> conditions) {
		super(id, selectionnable, x, y, comportementSelection, comportementConfirmation, conditions);
		// TODO Auto-generated constructor stub
	}

}
