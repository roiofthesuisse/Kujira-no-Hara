package preparateur;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import commandes.AppelerUnScript;
import conditions.ConditionScript;
import main.Commande;
import utilitaire.InterpreteurDeJson;

/**
 * Adapter les fichiers JSON du jeu au moteur Java sur des spécificités.
 */
public abstract class ListerLesScripts {
	
	/**
	 * Lancer le nettoyeur.
	 * @param args rien du tout
	 */
	public static void main(final String[] args) {
		// conditions script
		final ArrayList<String> scripts = listerLesConditionsScript();
		System.out.println("-----------------------------");
		System.out.println("-----------------------------");
		System.out.println("-----------------------------");
		for (String s : scripts) {
			System.out.println(s);
		}
	}
	
	private static ArrayList<String> listerLesConditionsScript() {
		ArrayList<String> scripts = new ArrayList<>();
		for (int numeroMap = 1; numeroMap <=572; numeroMap++) {
			try {
				final JSONObject jsonMap = InterpreteurDeJson.ouvrirJsonMap(numeroMap);
				final JSONArray jsonEvents = jsonMap.getJSONArray("events");
				for (Object oEvent : jsonEvents) {
					try {
						JSONObject jsonEvent = (JSONObject) oEvent;
						final JSONArray jsonPages = jsonEvent.getJSONArray("pages");
						for (Object oPage : jsonPages) {
							try {
								JSONObject jsonPage = (JSONObject) oPage;
								final JSONArray jsonCommandes = jsonPage.getJSONArray("commandes");
								for (Object oCommande : jsonCommandes) {
									try {
										JSONObject jsonCommande = (JSONObject) oCommande;
										final Commande commande = Commande.recupererUneCommande(jsonCommande);
										if (commande instanceof ConditionScript || commande instanceof AppelerUnScript) {
											scripts.add(commande.toString());
										}
									} catch (Exception e) {
										//LOG.error("Commande irrécupérable : "+oCommande);
									}
								}
							} catch (Exception e) {
								//e.printStackTrace();
							}
						}
					} catch (Exception e) {
						//LOG.error("Pas de pages : "+oEvent);
					}
				}
			} catch (Exception e) {
				//e.printStackTrace();
			}
			
		}
		return scripts;
	}
	
}
