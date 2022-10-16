package commandes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import map.Brouillard;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Modifier l'image de Panorama pour en avoir une autre que celle associ�e par
 * defaut au Tileset. On peut �galement changer le degr� de parallaxe.
 */
public class ModifierBrouillard extends Commande implements CommandeEvent {
	protected static final Logger LOG = LogManager.getLogger(ModifierBrouillard.class);

	private final String nomImage;
	private final Integer opacite;
	private final String nomModeDeFusion;
	private final Integer defilementX, defilementY;
	private final Integer zoom;
	private final int[] ton;

	/**
	 * Constructeur explicite
	 * 
	 * @param nomImage        nom de l'image de panorama
	 * @param opacite         transparence de 0 a 255
	 * @param nomModeDeFusion normal, addition, soustraction...
	 * @param defilementX     vitesse du d�filement horizontal
	 * @param defilementY     vitesse du d�filement vertical
	 * @param zoom            homoth�tie
	 * @param ton             du brouillard (gris, rouge, vert, bleu)
	 */
	public ModifierBrouillard(final String nomImage, final Integer opacite, final String nomModeDeFusion,
			final Integer defilementX, final Integer defilementY, final Integer zoom, final int[] ton) {
		this.nomImage = nomImage;
		this.opacite = opacite;
		this.nomModeDeFusion = nomModeDeFusion;
		this.defilementX = defilementX;
		this.defilementY = defilementY;
		this.zoom = zoom;
		this.ton = ton;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public ModifierBrouillard(final HashMap<String, Object> parametres) {
		// "null" signifie aucun changement par rapport au Brouillard actuel
		this(parametres.containsKey("nomImage") ? (String) parametres.get("nomImage") : null,
				parametres.containsKey("opacite") ? (int) parametres.get("opacite") : null,
				parametres.containsKey("modeDeFusion") ? (String) parametres.get("modeDeFusion") : null,
				parametres.containsKey("defilementX") ? (int) parametres.get("defilementX") : null,
				parametres.containsKey("defilementY") ? (int) parametres.get("defilementY") : null,
				parametres.containsKey("zoom") ? (int) parametres.get("zoom") : null,
				parametres.containsKey("rouge")
						? new int[] { (int) parametres.get("gris"), (int) parametres.get("rouge"),
								(int) parametres.get("vert"), (int) parametres.get("bleu") }
						: null);
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		final Brouillard brouillardActuel = this.page.event.map.brouillard;

		// Y a-t-il deja un Brouillard actuel ?
		if (brouillardActuel == null) {
			// Il n'y a pas encore de Brouillard
			// On cree un Brouillard tout neuf
			// On utilise des valeurs par defaut si manquantes
			this.page.event.map.brouillard = new Brouillard(this.nomImage,
					this.opacite == null ? Graphismes.OPACITE_MAXIMALE : this.opacite,
					this.nomModeDeFusion == null ? ModeDeFusion.NORMAL : ModeDeFusion.parNom(this.nomModeDeFusion),
					this.defilementX == null ? 0 : this.defilementX, this.defilementY == null ? 0 : this.defilementY,
					this.zoom == null ? Graphismes.PAS_D_HOMOTHETIE : this.zoom,
					this.ton == null ? Graphismes.TON_PAR_DEFAUT : this.ton);

		} else {
			// Il y a deja un Brouillard
			// On modifie le Brouillard actuel
			if ((this.nomImage == null || brouillardActuel.nomImage.equals(this.nomImage))
					&& (this.opacite == null || brouillardActuel.opacite == this.opacite)
					&& (this.nomModeDeFusion == null || brouillardActuel.mode.nom.equals(this.nomModeDeFusion))
					&& (this.defilementX == null || brouillardActuel.defilementX == this.defilementX)
					&& (this.defilementY == null || brouillardActuel.defilementY == this.defilementY)
					&& (this.zoom == null || brouillardActuel.zoom == this.zoom)
					&& (this.ton == null || Graphismes.memeTon(brouillardActuel.ton, this.ton))) {
				// Aucun changement
				LOG.warn("Cette modification du brouillard ne change rien !");
			} else {
				// different d'avant !
				// Si manquantes, on r�utilise les valeurs du Brouillard actuel
				this.page.event.map.brouillard = new Brouillard(
						this.nomImage == null ? brouillardActuel.nomImage : this.nomImage,
						this.opacite == null ? brouillardActuel.opacite : this.opacite,
						this.nomModeDeFusion == null ? brouillardActuel.mode
								: ModeDeFusion.parNom(this.nomModeDeFusion),
						this.defilementX == null ? brouillardActuel.defilementX : this.defilementX,
						this.defilementY == null ? brouillardActuel.defilementY : this.defilementY,
						this.zoom == null ? brouillardActuel.zoom : this.zoom,
						this.ton == null ? brouillardActuel.ton : this.ton);
			}
		}
		return curseurActuel + 1;
	}
}
