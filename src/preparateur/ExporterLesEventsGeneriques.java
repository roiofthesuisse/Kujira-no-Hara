package preparateur;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import utilitaire.InterpreteurDeJson;

/**
 * Certains evements sont sur plusieurs maps, copies a partir d'un clone.
 * On va placer les modeles des clones dans un meme dossier.
 */
public abstract class ExporterLesEventsGeneriques {
	
	private static final int NOMBRE_DE_MAPS = 631;
	
	/**
	 * Lancer le nettoyeur.
	 * @param args rien du tout
	 */
	public static void main(final String[] args) {
		// Exporter les Events communs
		exporterLesEventsGeneriques();
	}

	/**
	 * Certains events sont des clones d'un modèle.
	 * Isoler ces modèles et les ranger dans le dossier des modèles pour clones.
	 */
	private static void exporterLesEventsGeneriques() {
		System.out.println("Recherche des events génériques :");
		ArrayList<ArrayList<Integer>> localisationDesGeneriques = new ArrayList<>();
		for (int numeroMap = 1; numeroMap <=NOMBRE_DE_MAPS; numeroMap++) {
			try {
				final JSONObject jsonMap = InterpreteurDeJson.ouvrirJsonMap(numeroMap);
				final JSONArray jsonEvents = jsonMap.getJSONArray("events");
				for (Object oEvent : jsonEvents) {
					try {
						JSONObject jsonEvent = (JSONObject) oEvent;
						String nomEvent = jsonEvent.getString("nom");
						Pattern p = Pattern.compile("Clone\\[[0-9]+\\]\\[[0-9]+\\]");
						Matcher m = p.matcher(nomEvent);
						if (m.find()) {
							// L'event est un clone
							Pattern p2 = Pattern.compile("[0-9]+");
							Matcher m2 = p2.matcher(nomEvent);
							final ArrayList<Integer> nombres = new ArrayList<>();
							while (m2.find()) {
								nombres.add(Integer.parseInt(m2.group()));
							}
							boolean dejaDansLaListe = false;
							estCeQueLeModeleEstDejaDansLaListe: 
							for (ArrayList<Integer> l : localisationDesGeneriques) {
								if (l.get(0).equals(nombres.get(0)) 
										&& l.get(1).equals(nombres.get(1))) {
									dejaDansLaListe = true;
									break estCeQueLeModeleEstDejaDansLaListe;
								}
							}
							if(!dejaDansLaListe){
								System.out.println(nombres.get(0)+"/"+nombres
										.get(1));
								localisationDesGeneriques.add(nombres);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("---------------------------");
		for (int numeroMap = 1; numeroMap <=NOMBRE_DE_MAPS; numeroMap++) {
			try {
				final JSONObject jsonMap = InterpreteurDeJson.ouvrirJsonMap(numeroMap);
				final JSONArray jsonEvents = jsonMap.getJSONArray("events");
				for (Object oEvent : jsonEvents) {
					try {
						JSONObject jsonEvent = (JSONObject) oEvent;
						int idEvent = jsonEvent.getInt("id");
						String jsonModele = null;
						String nomModele = "";
						rechercheDuModele: for (ArrayList<Integer> l : localisationDesGeneriques) {
							if (l.get(0) == numeroMap && l.get(1) == idEvent) {
								// On a trouvé le modèle
								jsonModele = jsonEvent.toString(4);
								
								nomModele = "Clone["+numeroMap+"]["+idEvent+"]";
								break rechercheDuModele;
							}
						}
						if (jsonModele!=null) {
							String nomFichier = "./ressources/Data/GenericEvents/Exportation/"+nomModele+".json";
							PrintWriter out = new PrintWriter(nomFichier);
							out.println(jsonModele);
							out.close();
							System.out.println(nomFichier);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
