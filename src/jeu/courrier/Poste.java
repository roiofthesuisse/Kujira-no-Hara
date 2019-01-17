package jeu.courrier;

import java.io.IOException;
import java.io.InputStream;
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
	private static final String SERVEUR = "http://www.roiofthesuisse.byethost7.com/";
	private static final String RECEVOIR = "kujiranohara_recevoirCourrier.php";
	private static final String ENVOYER = "kujiranohara_envoyerCourrier.php";

	/**
	 * Envoyer une lettre à un personnage du jeu par la Poste.
	 * @param lettre à envoyer
	 * @return reponse http
	 */
	public static String envoyerDuCourrier(final LettreAEnvoyer lettre) {
		try {
			// Requete POST
			final URL url = new URL(SERVEUR+ENVOYER);
			final URLConnection con = url.openConnection();
			final HttpURLConnection http = (HttpURLConnection) con;
			http.setRequestMethod("POST"); // PUT is another valid option
			http.setDoOutput(true);
			
			// Arguments de la requete POST
			final HashMap<String, String> arguments = new HashMap<>();
			final Partie partie = Main.getPartieActuelle();
			arguments.put("joueurIp", JOUEUR[0]);
			arguments.put("joueurMac", JOUEUR[1]);
			arguments.put("joueurNom", partie.mots[Partie.NOM_DU_HEROS]);
			arguments.put("partieId", Integer.toString(partie.id));
			arguments.put("lettreId", Integer.toString(lettre.id));
			arguments.put("lettreDestinataire", lettre.personnage.nomPersonnage);
			arguments.put("lettreTexte", lettre.texte);
			final StringJoiner sj = new StringJoiner("&");
			for (HashMap.Entry<String, String> entry : arguments.entrySet()) {
			    sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" 
			         + URLEncoder.encode(entry.getValue(), "UTF-8"));
			}
			final byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
			final int length = out.length;
			
			// Envoyer la requete
			http.setFixedLengthStreamingMode(length);
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			http.connect();
			try (OutputStream os = http.getOutputStream()) {
			    os.write(out);
			}
			
			// Obtenir la reponse
			final InputStream is = http.getInputStream();
			final Scanner s = new Scanner(is);
			s.useDelimiter("\\A");
			final String reponse = s.next();
			s.close();
			is.close();
			http.disconnect();
			return reponse;
			
		} catch (IOException e) {
			LOG.error("Impossible de recevoir le courrier !", e);
			return null;
		}
	}
	
	/**
	 * Recevoir le courrier du serveur postal
	 * @return reponse http du serveur postal
	 */
	public static String recevoirLeCourrier() {
		try {
			// Requete POST
			final URL url = new URL(SERVEUR+RECEVOIR);
			final URLConnection con = url.openConnection();
			final HttpURLConnection http = (HttpURLConnection) con;
			http.setRequestMethod("POST"); // PUT is another valid option
			http.setDoOutput(true);
			
			// Arguments de la requete POST
			final HashMap<String, String> arguments = new HashMap<>();
			final Partie partie = Main.getPartieActuelle();
			arguments.put("ipJoueur", JOUEUR[0]);
			arguments.put("macJoueur", JOUEUR[1]);
			arguments.put("nomJoueur", partie.mots[Partie.NOM_DU_HEROS]);
			arguments.put("idPartie", Integer.toString(partie.id));
			arguments.put("idsLettres", idsDesLettresDontOnAttendUneReponse(partie));
			final StringJoiner sj = new StringJoiner("&");
			for (HashMap.Entry<String, String> entry : arguments.entrySet()) {
			    sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" 
			         + URLEncoder.encode(entry.getValue(), "UTF-8"));
			}
			final byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
			final int length = out.length;
			
			// Envoyer la requete
			http.setFixedLengthStreamingMode(length);
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			http.connect();
			try (OutputStream os = http.getOutputStream()) {
			    os.write(out);
			}
			
			// Obtenir la reponse
			final InputStream is = http.getInputStream();
			final Scanner s = new Scanner(is);
			s.useDelimiter("\\A");
			final String reponse = s.next();
			s.close();
			is.close();
			http.disconnect();
			return reponse;
			
		} catch (IOException e) {
			LOG.error("Impossible d'envoyer le courrier !", e);
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
