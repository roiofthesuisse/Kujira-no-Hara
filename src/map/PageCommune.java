package map;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import conditions.Condition;
import main.Commande;
import utilitaire.InterpreteurDeJson;

/**
 * Pages de code commun � toutes les Maps.
 */
public class PageCommune extends PageEvent {
	private static final Logger LOG = LogManager.getLogger(PageCommune.class);
	public boolean active;
	public final String nom;
	
	/**
	 * Constructeur explicite
	 * @param pageJSON objet JSON repr�sentant la Page
	 */
	public PageCommune(final JSONObject pageJSON) {
		super(-1, pageJSON, -1, null); //pas d'Event correspondant, pas de num�ro
		this.nom = pageJSON.getString("nom");
		this.active = false;
	}

	/**
	 * Activer la Page si les Conditions sont v�rifi�es.
	 */
	public final void essayerDActiver() {
		if (this.conditions!=null && this.conditions.size()>0) {
			//la Page a des Conditions de d�clenchement, on les analyse
			boolean cettePageConvient = true;
			//si une Condition est fausse, la Page ne convient pas
			for (int j = 0; j<this.conditions.size() && cettePageConvient; j++) {
				final Condition cond = this.conditions.get(j);
				if (!cond.estVerifiee()) {
					//la Condition n'est pas v�rifi�e
					cettePageConvient = false;
				}
			}
			//si toutes les Conditions sont v�rifi�es, on active la Page
			if (cettePageConvient) {
				this.active = true;
			}
			
		} else {
			//aucune Condition n�cessaire pour cette Page, donc la Page est activ�e
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
					commande.page = this; //on apprend � la Commande depuis quelle Page elle est appel�e
					curseurCommandes = commande.executer(curseurCommandes, commandes);
					if (curseurCommandes==ancienCurseur) { 
						//le curseur n'a pas chang�, c'est donc une commande qui prend du temps
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
	 * R�cup�rer les Pages Communes d�crites dans un fichier JSON.
	 * @return Pages de code Event communes � toutes les Maps du jeu
	 */
	public static HashMap<Integer, PageCommune> recupererLesPagesCommunes() {
		final HashMap<Integer, PageCommune> pagesCommunes = new HashMap<>();
		final JSONObject jsonObjets;
		try {
			jsonObjets = InterpreteurDeJson.ouvrirJson("pagesCommunes", "./ressources/Data/");
		} catch (Exception e) {
			LOG.error("Impossible d'ouvrir le fichier JSON des pages communes !", e);
			return pagesCommunes;
		}
		
		final JSONArray jsonPagesCommunes = jsonObjets.getJSONArray("pages");
		for (Object o : jsonPagesCommunes) {
			final JSONObject jsonPageCommune = (JSONObject) o;
			final PageCommune pageCommune = new PageCommune(jsonPageCommune);
			final Integer numeroPageCommune = jsonPageCommune.getInt("numero");
			pagesCommunes.put(numeroPageCommune, pageCommune);
		}
		return pagesCommunes;
	}
	
}
