package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;
import map.PageEvent;
import son.LecteurAudio;

/**
 * Jouer un effet sonore.
 */
public class JouerEffetSonore implements CommandeEvent {
	private PageEvent page;
	
	private final String nomFichierSonore;
	
	/**
	 * @param nomFichierSonore nom du fichier de l'effet sonore à jouer
	 */
	public JouerEffetSonore(final String nomFichierSonore) {
		this.nomFichierSonore = nomFichierSonore;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public JouerEffetSonore(final HashMap<String, Object> parametres) {
		this( (String) parametres.get("nomFichierSonore") );
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<? extends Commande> commandes) {
		LecteurAudio.playSe(nomFichierSonore);
		return curseurActuel+1;
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
