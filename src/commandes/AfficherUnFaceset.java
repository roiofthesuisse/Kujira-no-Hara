package commandes;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Commande;
import utilitaire.graphismes.Graphismes;

public class AfficherUnFaceset extends Commande implements CommandeEvent {
	
	private static final Logger LOG = LogManager.getLogger(AfficherUnFaceset.class);

	private final String nomFaceset;

	public AfficherUnFaceset(String nomFaceset) {
		this.nomFaceset = nomFaceset;
	}

	@Override
	public int executer(int curseurActuel, List<Commande> commandes) {
		try {
			Message.faceset = this.nomFaceset == null ? null
					: Graphismes.ouvrirImage("Pictures/Facesets", this.nomFaceset);
		} catch (IOException e) {
			LOG.error("Impossible de charger le faceset : " + this.nomFaceset, e);
			Message.faceset = null;
		}
		return curseurActuel+1;
	}

}
