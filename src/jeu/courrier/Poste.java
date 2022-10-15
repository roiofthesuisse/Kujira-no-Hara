package jeu.courrier;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jeu.Partie;
import main.Main;

/**
 * Pour envoyer ou recevoir du courrier.
 */
public abstract class Poste {
	private static final Logger LOG = LogManager.getLogger(Poste.class);
	protected static final String[] JOUEUR = calculerIdentifiantJoueur();
	private static final String SERVEUR = "http://www.roiofthesuisse.fr/kujiranohara/";
	private static final String RECEVOIR = "kujiranohara_recevoirCourrier.php";
	private static final String ENVOYER = "kujiranohara_envoyerCourrier.php";

	/**
	 * Envoyer une lettre a un personnage du jeu par la Poste.
	 * @param lettre a envoyer
	 * @return reponse http
	 */
	public static String envoyerDuCourrier(final LettreAEnvoyer lettre) {
		// Arguments
		final HashMap<String, String> arguments = new HashMap<>();
		final Partie partie = Main.getPartieActuelle();
		arguments.put("joueurIp", JOUEUR[0]);
		arguments.put("joueurMac", JOUEUR[1]);
		arguments.put("joueurNom", partie.mots[Partie.NOM_DU_HEROS]);
		arguments.put("partieId", Integer.toString(partie.id));
		arguments.put("lettreId", Integer.toString(lettre.id));
		arguments.put("lettreDestinataire", lettre.personnage.nomPersonnage);
		arguments.put("lettreTexte", lettre.texte);
		
		return requetePost(SERVEUR+ENVOYER, arguments);
	}
	
	
	public static void main(final String[] args) {
		// Arguments
		final HashMap<String, String> arguments = new HashMap<>();
		arguments.put("joueurIp", "0.0.0.0");
		arguments.put("joueurMac", "FF:FF:FF:FF:FF:FF:FF:FF");
		arguments.put("joueurNom", "Robert");
		arguments.put("partieId", "1");
		arguments.put("partieAvancement", "42");
		arguments.put("lettreIds", "1,17,18");
		
		final String responseHttp = requetePost(SERVEUR+RECEVOIR, arguments);
		if (responseHttp.contains("DEBUT_COURRIER")) {
			final int debut = responseHttp.indexOf("DEBUT_COURRIER")+"DEBUT_COURRIER".length();
			final int fin = responseHttp.lastIndexOf("FIN_COURRIER");
			final String courrier = responseHttp.substring(debut, fin);
			LOG.info("Le courrier a ete re�u : \""+courrier+"\"");
		} else {
			LOG.info("Le courrier n'a pas ete re�u.");
		}
	}
	
	/**
	 * Recevoir le courrier du serveur postal
	 * @return reponse http du serveur postal
	 */
	public static String recevoirLeCourrier() {
		// Arguments
		final HashMap<String, String> arguments = new HashMap<>();
		final Partie partie = Main.getPartieActuelle();
		arguments.put("joueurIp", JOUEUR[0]);
		arguments.put("joueurMac", JOUEUR[1]);
		arguments.put("joueurNom", partie.mots[Partie.NOM_DU_HEROS]);
		arguments.put("partieId", Integer.toString(partie.id));
		arguments.put("partieAvancement", Integer.toString(partie.avancement()));
		arguments.put("lettreIds", idsDesLettresDontOnAttendUneReponse(partie));

		final String responseHttp = requetePost(SERVEUR+RECEVOIR, arguments);
		if (responseHttp.contains("|")) {
			final int debut = responseHttp.indexOf("|");
			final int fin = responseHttp.lastIndexOf("|") + 1;
			final String courrier = responseHttp.substring(debut, fin);
			LOG.info("Le courrier a ete re�u : \""+courrier+"\"");
			return courrier;
			
		} else {
			LOG.info("Le courrier n'a pas ete re�u.");
			return null;
		}
	}
	
	/**
	 * Effectuer une requete POST vers le serveur postal.
	 * @param urlString url du serveur et de sa page PHP
	 * @param arguments de la requete POST
	 * @return reponse HTTP
	 */
	private static String requetePost(final String urlString, final HashMap<String, String> arguments) {
		try {
			// Requete POST
			final URL url = new URL(urlString);
			final URLConnection con = url.openConnection();
			final HttpURLConnection http = (HttpURLConnection) con;
			http.setRequestMethod("POST"); // PUT is another valid option
			http.setDoOutput(true);
			
			// Joindre les arguments de la requete POST
			final StringJoiner sj = new StringJoiner("&");
			for (HashMap.Entry<String, String> entry : arguments.entrySet()) {
			    sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" 
			         + URLEncoder.encode(entry.getValue(), "UTF-8"));
			}
			final String urlParameters = sj.toString();

			// Ecrire les parametres dans la requete POST
			con.setDoOutput(true);
			final DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			
			// Recuperer la reponse HTTP
			final BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			final StringBuffer sbuf = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				sbuf.append(inputLine);
			}
			in.close();
			
			//print result
			LOG.info("La requete POST a ete envoyee et sa r�ponse a ete r�cup�r�e.");
			return sbuf.toString();

		} catch (IOException e) {
			LOG.error("Impossible d'envoyer la requete POST !", e);
			return null;
		}
	}
	
	/**
	 * Obtenir la liste des lettres envoyees mais qui sont toujours sans r�ponse
	 * @param partie actuelle
	 * @return identifiants des lettres sans r�ponse
	 */
	private static String idsDesLettresDontOnAttendUneReponse(final Partie partie) {
		return partie.lettresAEnvoyer.stream()
		.filter(lettre->EtatCourrier.ENVOYEE_PAS_REPONDUE.equals(lettre.etat))
		.map(lettre->lettre.id).map(Object::toString)
		.collect(Collectors.joining(","));
	}
	
	/**
	 * Calculer l'identifiant du joueur
	 * @return adresse mac du joueur
	 */
	private static String[] calculerIdentifiantJoueur() {
		final String[] identifiant = new String[2];
		try {
			// Adresse ip
			final InetAddress ip = InetAddress.getLocalHost();
			identifiant[0] = ip.toString();

			// Adresse mac
			final NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			final byte[] mac = network.getHardwareAddress();
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
			}
			identifiant[1] = sb.toString();

		} catch (UnknownHostException | SocketException e) {
			LOG.error("Impossible de calculer l'identifiant du joueur !", e);
			identifiant[0] = "255.255.255.255";
			identifiant[1] = "FF:FF:FF:FF:FF:FF";
		}
		LOG.info(identifiant[0]);
		LOG.info(identifiant[1]);
		return identifiant;
	}

}
