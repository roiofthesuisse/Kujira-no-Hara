package jeu.courrier;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import conditions.Condition;
import main.Commande;
import menu.Listable;

/**
 * Lettre recue par un personnage du jeu via la poste.
 */
public class LettreRecue extends Lettre implements Listable {

	/**
	 * Constructeur explicite
	 * @param idLettre identifiant de la lettre envoyee
	 * @param nomPersonnage exp�diteur
	 * @param texte contenu de la reponse
	 */
	public LettreRecue(final int idLettre, final String nomPersonnage, final String texte) {
		 this.id = idLettre;
		 this.personnage = Adresse.parNom(nomPersonnage);
		 this.texte = texte;
	}
	
	@Override
	public final BufferedImage construireImagePourListe(final int largeur, final int hauteur) {
		// icone
		// TODO
		
		// expediteur
		// TODO
		
		// TODO
		return null;
	}

	final @Override
	public ArrayList<Condition> getConditions() {
		// Toujours afficher les lettres recues
		return null;
	}

	final @Override
	public ArrayList<Commande> getComportementSelection() {
		// Au survol, afficher le contenu de la lettre
		//TODO
		return null;
	}

	final @Override
	public ArrayList<Commande> getComportementConfirmation() {
		// Pas de confirmation
		return null;
	}

}
