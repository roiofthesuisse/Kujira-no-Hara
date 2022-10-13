package commandes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import menu.Texte;

/**
 * Afficher la description de l'El�ment s�lectionn� dans le Menu.
 */
public class ModifierTexte extends Commande implements CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(ModifierTexte.class);

	private final ArrayList<String> nouveauTexte;
	private final Integer idElementDeMenu;

	/**
	 * Constructeur implicite
	 * 
	 * @param nouveauTexte a afficher comme
	 */
	public ModifierTexte(final ArrayList<String> nouveauTexte) {
		this.nouveauTexte = nouveauTexte;
		this.idElementDeMenu = null; // texte des descriptions par d�faut du Menu
	}

	/**
	 * Constructeur explicite
	 * 
	 * @param nouveauTexte    a afficher comme
	 * @param idElementDeMenu identifiant de l'ElementDeMenu dont il faut changer le
	 *                        texte
	 */
	private ModifierTexte(final ArrayList<String> nouveauTexte, final Integer idElementDeMenu) {
		this.nouveauTexte = nouveauTexte;
		this.idElementDeMenu = idElementDeMenu;
	}

	/**
	 * Constructeur g�n�rique
	 * 
	 * @param parametres liste de param�tres issus de JSON
	 */
	public ModifierTexte(final HashMap<String, Object> parametres) {
		this(Texte.construireTexteMultilingue(parametres.get("nouveauTexte")),
				parametres.containsKey("idElementDeMenu") ? (int) parametres.get("idElementDeMenu") : null);
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		try {
			final Texte elementAModifier;
			if (this.idElementDeMenu == null) {
				elementAModifier = this.element.menu.texteDescriptif;
			} else {
				elementAModifier = (Texte) this.element.menu.elements.get(this.idElementDeMenu);
			}
			elementAModifier.contenu = this.nouveauTexte;
			elementAModifier.actualiserImage();
		} catch (ClassCastException e) {
			LOG.error("L'�lement de menu " + this.idElementDeMenu + " n'est pas un texte.", e);
		}
		return curseurActuel + 1;
	}

}
