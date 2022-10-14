package commandes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import main.Commande;
import map.Map;

/**
 * Sauvegarder la Partie ainsi que l'�tat de la Map actuelle dans un fichier
 * externe crypt�.
 */
public class Sauvegarder extends Commande implements CommandeMenu, CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(Sauvegarder.class);
	public static final String NOM_DOSSIER_SAUVEGARDES = "./saves/";
	public static final String PREFIXE_FICHIER_SAUVEGARDE = "save";
	/** Le cryptage impose des textes de taille multiple de 16 bytes */
	private static final int MULTIPLE_DE_SEIZE = 16;

	private Integer numeroSauvegarde;

	/**
	 * Constructeur explicite
	 * 
	 * @param numeroSauvegarde num�ro du fichier de Sauvegarde
	 */
	private Sauvegarder(final Integer numeroSauvegarde) {
		this.numeroSauvegarde = numeroSauvegarde;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public Sauvegarder(final HashMap<String, Object> parametres) {
		this(parametres.containsKey("numeroSauvegarde") ? (int) parametres.get("numeroSauvegarde") : null);
	}

	/**
	 * Tout ce qui est suceptible de figurer dans une sauvegarde.
	 */
	public interface Sauvegardable {
		/**
		 * G�n�rer un JSON de l'�tat actuel de cet objet java pour la Sauvegarde.
		 */
		public JSONObject sauvegarderEnJson();
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		// La Partie par d�faut est la Partie actuelle
		if (this.numeroSauvegarde == null) {
			this.numeroSauvegarde = getPartieActuelle().id;
		}

		final String nomFichierSauvegarde = PREFIXE_FICHIER_SAUVEGARDE + this.numeroSauvegarde + ".txt";
		final String filename = NOM_DOSSIER_SAUVEGARDES + nomFichierSauvegarde;

		// On cr�e le dossier des sauvegardes s'il n'existe pas
		File dossierSauvegardes = new File(NOM_DOSSIER_SAUVEGARDES);
		if (!dossierSauvegardes.exists()) {
			dossierSauvegardes.mkdir();
		}

		try {
			// G�n�rer le fichier de sauvegarde
			final JSONObject jsonSauvegarde = genererSauvegarde();
			final String jsonStringSauvegarde = jsonSauvegarde.toString();

			// Crypter
			final byte[] jsonStringSauvegardeCryptee = crypter(jsonStringSauvegarde);

			// Enregistrer le fichier
			final FileOutputStream fos = new FileOutputStream(filename);
			fos.write(jsonStringSauvegardeCryptee);
			fos.close();

			LOG.info("Partie sauvegard�e dans le fichier " + filename);
			LOG.debug(jsonStringSauvegarde);
		} catch (IOException e) {
			LOG.error("Impossible de sauvegarder la partie dans le fichier " + filename, e);
		}

		return curseurActuel + 1;
	}

	/**
	 * Produire un fichier JSON repr�sentant la Partie et l'�tat actuel de la Map.
	 * 
	 * @return sauvegarde au format JSON
	 */
	private JSONObject genererSauvegarde() {
		final JSONObject jsonSauvegarde = new JSONObject();

		// Partie
		final JSONObject jsonPartie = getPartieActuelle().sauvegarderEnJson();
		jsonSauvegarde.put("partie", jsonPartie);

		// Etat de la Map
		final Map map;
		if (this.element != null) {
			map = this.element.menu.lecteur.lecteurMapMemorise.map;
		} else {
			map = this.page.event.map;
		}
		final JSONObject jsonEtatMap = map.sauvegarderEnJson();
		jsonSauvegarde.put("etatMap", jsonEtatMap);
		// TODO pour tous les events : x, y, direction, apparence, vitesse, frequence,
		// proprietesActuelles, pageActive, curseur, mouvements

		return jsonSauvegarde;
	}

	/**
	 * Crypter la sauvegarde.
	 * 
	 * @param jsonStringSauvegarde texte de la Sauvegarde non crypt�
	 * @return texte de la Sauvegarde crypt�
	 */
	private byte[] crypter(final String jsonStringSauvegarde) {
		try {
			// Hashage de la cl�
			final SecretKeySpec cleHashee = construireCleDeCryptage();

			// Cryptage du texte avec la cl� hash�e
			byte[] text = jsonStringSauvegarde.getBytes("UTF8");
			// On ajoute des ZZZZ a la fin pour avoir une taille multiple de 16
			text = ajouterAppendice(text);

			final Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			final byte[] iv = new byte[cipher.getBlockSize()];
			final IvParameterSpec ivParam = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, cleHashee, ivParam);
			final byte[] encrypted = cipher.doFinal(text);

			LOG.debug(new String(encrypted));
			return encrypted;

		} catch (Exception e) {
			LOG.error("Impossible de crypter le texte.", e);
			return null;
		}
	}

	/**
	 * Ajouter des ZZZZ inutiles a la fin du fichier pour avoir une taille multiple
	 * de 16.
	 * 
	 * @param text0 texte en entree
	 * @return texte rallong� avec des ZZZ
	 */
	private byte[] ajouterAppendice(final byte[] text0) {
		LOG.debug("text0 : " + text0.length);
		int arrondi = text0.length;
		while (arrondi % MULTIPLE_DE_SEIZE != 0) {
			arrondi++;
		}
		LOG.debug("arrondi : " + arrondi);
		final byte[] text = new byte[arrondi];
		for (int i = 0; i < text0.length; i++) {
			text[i] = text0[i];
		}
		for (int i = text0.length; i < arrondi; i++) {
			text[i] = 'Z';
		}
		return text;
	}

}
