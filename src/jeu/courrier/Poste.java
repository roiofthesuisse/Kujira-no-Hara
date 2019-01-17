package jeu.courrier;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;
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
	 * Envoyer une lettre à un personnage du jeu par la Poste.
	 * @param lettre à envoyer
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
		arguments.put("lettreId", "1");
		arguments.put("lettreDestinataire", Adresse.MATHUSALEM.nomPersonnage);
		arguments.put("lettreTexte", "Bonjour mon vieux !\\nJ'espère que tout va bien pour toi.\\nAmitié,\\nRobert");
		
		final String reponse = requetePost(SERVEUR+ENVOYER, arguments);
		LOG.info(reponse);
	}
	
	/**
	 * Recevoir le courrier du serveur postal
	 * @return reponse http du serveur postal
	 */
	public static String recevoirLeCourrier() {
		// Arguments
		final HashMap<String, String> arguments = new HashMap<>();
		final Partie partie = Main.getPartieActuelle();
		arguments.put("ipJoueur", JOUEUR[0]);
		arguments.put("macJoueur", JOUEUR[1]);
		arguments.put("nomJoueur", partie.mots[Partie.NOM_DU_HEROS]);
		arguments.put("idPartie", Integer.toString(partie.id));
		arguments.put("idsLettres", idsDesLettresDontOnAttendUneReponse(partie));

		return requetePost(SERVEUR+RECEVOIR, arguments);
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
			/*
			final byte[] out = urlParameters.getBytes(StandardCharsets.UTF_8);
			final int length = out.length;
			
			// Envoyer la requete POST
			http.setFixedLengthStreamingMode(length);
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			http.connect();
			try (OutputStream os = http.getOutputStream()) {
			    os.write(out);
			    os.flush();
				os.close();
			}
			*/
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			//print result
			String reponse = response.toString();
			System.out.println(reponse);
			return reponse;
			/*
			final int responseCode = http.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);
			
			// Obtenir la reponse
			final InputStream is = http.getInputStream();
			final Scanner s = new Scanner(is);
			s.useDelimiter("\\A");
			final String reponse = s.next();
			s.close();
			is.close();
			http.disconnect();
			return reponse;
			*/
		} catch (IOException e) {
			LOG.error("Impossible d'envoyer la requete POST !", e);
			return null;
		}
	}
	
	/**
	 * Obtenir la liste des lettres envoyées mais qui sont toujours sans réponse
	 * @param partie actuelle
	 * @return identifiants des lettres sans réponse
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
