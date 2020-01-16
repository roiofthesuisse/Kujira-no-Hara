package preparateur;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import commandes.AppelerUnScript;
import conditions.ConditionScript;
import main.Commande;
import utilitaire.InterpreteurDeJson;

/**
 * Lister les scripts ruby qui ne sont pas encore interpretes
 */
public abstract class ListerLesScripts {
	
	/**
	 * Lister les scripts ruby qui ne sont pas encore interpretes
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
		System.out.println("-----------------------------");
		System.out.println("-----------------------------");
		System.out.println("-----------------------------");

		System.out.println("Nombre de scripts non interprétés : "+scripts.size());
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
										if (commande instanceof ConditionScript) {
											ConditionScript condition = (ConditionScript) commande;
											condition.estVerifiee();
											if(!condition.interpretationImplementee) {
												// ce format de condition script est inconnu
												scripts.add(commande.toString());
											}
										} else if( commande instanceof AppelerUnScript) {
											AppelerUnScript appel = (AppelerUnScript) commande;
											appel.executer(0, null);
											if(!appel.interpretationImplementee) {
												// ce format de script est inconnu
												scripts.add(commande.toString());
											}
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
