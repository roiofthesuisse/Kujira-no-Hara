package jeu.courrier;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import conditions.Condition;
import conditions.ConditionEtatLettre;
import main.Commande;
import menu.Listable;

/**
 * Lettre a envoyer a un personnage du jeu via la poste.
 */
public class LettreAEnvoyer extends Lettre implements Listable {
	public EtatCourrier etat;
	
	@Override
	public final BufferedImage construireImagePourListe(final int largeur, final int hauteur) {
		// icone
		// TODO
		
		// Premi�re ligne (destinataire)
		// TODO

		// TODO
		return null;
	}

	@Override
	public final ArrayList<Condition> getConditions() {
		final ArrayList<Condition> conditions = new ArrayList<>();
		final Condition condition = new ConditionEtatLettre(this.id, EtatCourrier.PAS_ENVOYEE.nom);
		conditions.add(condition);
		return conditions;
	}

	@Override
	public final ArrayList<Commande> getComportementSelection() {
		// Au survol, rien
		return null;
	}

	@Override
	public final ArrayList<Commande> getComportementConfirmation() {
		// Ouvrir le menu d'�dition de lettre
		// TODO
		return null;
	}

}
