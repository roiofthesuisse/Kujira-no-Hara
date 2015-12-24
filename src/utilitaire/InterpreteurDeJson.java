package utilitaire;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.json.JSONObject;

public class InterpreteurDeJson {
	private static JSONObject ouvrirJson(String nomFichier, String adresse) throws FileNotFoundException{
		String nomFichierJson = adresse+nomFichier+".json";
		Scanner scanner = new Scanner(new File(nomFichierJson));
		String contenuFichierJson = scanner.useDelimiter("\\Z").next();
		scanner.close();
		return new JSONObject(contenuFichierJson);
	}
	
	public static JSONObject ouvrirJsonMap(int numero) throws FileNotFoundException{
		return ouvrirJson(""+numero,".\\ressources\\Data\\Maps\\");
	}

	public static JSONObject ouvrirJsonEventGenerique(String nomEvent) throws FileNotFoundException {
		return ouvrirJson(nomEvent,".\\ressources\\Data\\GenericEvents\\");
	}
	
	
}
