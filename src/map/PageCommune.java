package map;

import java.io.File;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import conditions.Condition;
import main.Commande;
import utilitaire.InterpreteurDeJson;

/**
 * Pages de code commun a toutes les Maps.
 */
public class PageCommune extends PageEvent {
	private static final Logger LOG = LogManager.getLogger(PageCommune.class);
	private static final String DOSSIER_PAGES_COMMUNES = "./ressources/Data/PagesCommunes/";
	
	public boolean active;
	public final String nom;
	
	/**
	 * Constructeur explicite
	 * @param pageJSON objet JSON representant la Page
	 */
	public PageCommune(final JSONObject pageJSON) {
		super(-1, pageJSON, -1, null); //pas d'Event correspondant, pas de numero
		this.nom = pageJSON.getString("nom");
		this.active = false;
	}

	/**
	 * Activer la Page si les Conditions sont verifiees.
	 */
	public final void essayerDActiver() {
		if (this.conditions!=null && this.conditions.size()>0) {
			//la Page a des Conditions de declenchement, on les analyse
			boolean cettePageConvient = true;
			//si une Condition est fausse, la Page ne convient pas
			for (int j = 0; j<this.conditions.size() && cettePageConvient; j++) {
				final Condition cond = this.conditions.get(j);
				if (!cond.estVerifiee()) {
					//la Condition n'est pas verifiee
					cettePageConvient = false;
				}
			}
			//si toutes les Conditions sont verifiees, on active la Page
			if (cettePageConvient) {
				this.active = true;
			}
			
		} else {
			//aucune Condition necessaire pour cette Page, donc la Page est activee
			this.active = true;
		}
	}
	
	
	@Override
	public final void executer() {
		if (commandes!=null) {
			try {
				if (curseurCommandes >= commandes.size()) {
					curseurCommandes = 0;
				}
				boolean onAvanceDansLesCommandes = true;
				while (onAvanceDansLesCommandes) {
					final int ancienCurseur = curseurCommandes;
					final Commande commande = this.commandes.get(curseurCommandes);
					commande.page = this; //on apprend a la Commande depuis quelle Page elle est appelee
					curseurCommandes = commande.executer(curseurCommandes, commandes);
					if (curseurCommandes==ancienCurseur) { 
						//le curseur n'a pas change, c'est donc une commande qui prend du temps
						onAvanceDansLesCommandes = false;
					}
				}
			} catch (IndexOutOfBoundsException e) {
				//on a fini la page
				curseurCommandes = 0;
				this.active = false;
			}
		}
	}
	
	/**
	 * Recuperer les Pages Communes decrites dans un fichier JSON.
	 * @return Pages de code Event communes a toutes les Maps du jeu
	 */
	public static HashMap<Integer, PageCommune> recupererLesPagesCommunes() {
		final HashMap<Integer, PageCommune> pagesCommunes = new HashMap<>();
		final File dossierPagesCommunes = new File(DOSSIER_PAGES_COMMUNES);
		final File[] fichiersPagesCommunes = dossierPagesCommunes.listFiles();
		if (fichiersPagesCommunes == null) {
			LOG.error("Impossible d'ouvrir le dossier des pages communes !");
		} else {
			JSONObject jsonPageCommune;
			final int tailleExtension = ".json".length();
			String nomFichier, nomFichierSansExtension;
			int tailleNomFichier;
			for (File fichierPageCommune : fichiersPagesCommunes) {
				try {
					nomFichier = fichierPageCommune.getName();
					tailleNomFichier = nomFichier.length();
					nomFichierSansExtension = nomFichier.substring(0, tailleNomFichier-tailleExtension);
					
					jsonPageCommune = InterpreteurDeJson.ouvrirJson(nomFichierSansExtension, DOSSIER_PAGES_COMMUNES);
					final PageCommune pageCommune = new PageCommune(jsonPageCommune);
					final Integer numeroPageCommune = jsonPageCommune.getInt("numero");
					pagesCommunes.put(numeroPageCommune, pageCommune);
				} catch (Exception e) {
					LOG.error("Impossible d'ouvrir le fichier JSON de page commune : "+fichierPageCommune.getName(), e);
				}
			}
		}
		return pagesCommunes;
	}
	
}
