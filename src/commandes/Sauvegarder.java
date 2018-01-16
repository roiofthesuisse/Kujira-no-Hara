package commandes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import main.Commande;
import main.Fenetre;
import map.Map;

/**
 * Sauvegarder la Partie ainsi que l'état de la Map actuelle dans un fichier externe crypté.
 */
public class Sauvegarder extends Commande implements CommandeMenu, CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(Sauvegarder.class);
	public final static String NOM_DOSSIER_SAUVEGARDES = "./saves/";
	public final static String PREFIXE_FICHIER_SAUVEGARDE = "save";
	/** Le cryptage impose des textes de taille multiple de 16 bytes */
	private static final int MULTIPLE_DE_SEIZE = 16;
	
	private Integer numeroSauvegarde;
	
	/**
	 * Constructeur explicite
	 * @param numeroSauvegarde numéro du fichier de Sauvegarde
	 */
	private Sauvegarder(final Integer numeroSauvegarde) {
		this.numeroSauvegarde = numeroSauvegarde;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Sauvegarder(final HashMap<String, Object> parametres) {
		this( parametres.containsKey("numeroSauvegarde") ? (int) parametres.get("numeroSauvegarde") : null );
	}
	
	/**
	 * Tout ce qui est suceptible de figurer dans une sauvegarde.
	 */
	public interface Sauvegardable {
		/**
		 * Générer un JSON de l'état actuel de cet objet java pour la Sauvegarde.
		 */
		public JSONObject sauvegarderEnJson();
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		// La Partie par défaut est la Partie actuelle
		if (this.numeroSauvegarde == null) {
			this.numeroSauvegarde = Fenetre.getPartieActuelle().id;
		}
		
		final String nomFichierSauvegarde = PREFIXE_FICHIER_SAUVEGARDE + this.numeroSauvegarde + ".txt";
		final String filename = NOM_DOSSIER_SAUVEGARDES + nomFichierSauvegarde;
		
		// On crée le dossier des sauvegardes s'il n'existe pas
		File dossierSauvegardes = new File(NOM_DOSSIER_SAUVEGARDES);
		if (!dossierSauvegardes.exists()) {
			dossierSauvegardes.mkdir();
	    }
		
		try {
				// Générer le fichier de sauvegarde
				final JSONObject jsonSauvegarde = genererSauvegarde();
				final String jsonStringSauvegarde = jsonSauvegarde.toString();
				
				// Crypter
				final byte[] jsonStringSauvegardeCryptee = crypter(jsonStringSauvegarde);
				
				// Enregistrer le fichier
				final FileOutputStream fos = new FileOutputStream(filename);
				fos.write(jsonStringSauvegardeCryptee);
				fos.close();
				
				LOG.info("Partie sauvegardée dans le fichier "+filename);
				LOG.debug(jsonStringSauvegarde);
		} catch (IOException e) {
			LOG.error("Impossible de sauvegarder la partie dans le fichier "+filename, e);
		}
		
		return curseurActuel+1;
	}

	/**
	 * Produire un fichier JSON représentant la Partie et l'état actuel de la Map.
	 * @return sauvegarde au format JSON
	 */
	private JSONObject genererSauvegarde() {
		final JSONObject jsonSauvegarde = new JSONObject();
		
		// Partie
		final JSONObject jsonPartie = Fenetre.getPartieActuelle().sauvegarderEnJson();
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
		//TODO pour tous les events : x, y, direction, apparence, vitesse, frequence, proprietesActuelles, pageActive, curseur, mouvements
		
		return jsonSauvegarde;
	}
	
	/**
	 * Crypter la sauvegarde.
	 * @param jsonStringSauvegarde texte de la Sauvegarde non crypté
	 * @return texte de la Sauvegarde crypté
	 */
	private byte[] crypter(final String jsonStringSauvegarde) {
		try {
			// Hashage de la clé
			final SecretKeySpec cleHashee = construireCleDeCryptage();
	
			// Cryptage du texte avec la clé hashée
	        byte[] text = jsonStringSauvegarde.getBytes("UTF8");
	        // On ajoute des ZZZZ à la fin pour avoir une taille multiple de 16
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
	 * Ajouter des ZZZZ inutiles à la fin du fichier pour avoir une taille multiple de 16.
	 * @param text0 texte en entrée
	 * @return texte rallongé avec des ZZZ
	 */
	private byte[] ajouterAppendice(final byte[] text0) {
		LOG.debug("text0 : "+text0.length);
        int arrondi = text0.length;
        while (arrondi%MULTIPLE_DE_SEIZE != 0) {
        	arrondi++;
        }
        LOG.debug("arrondi : "+arrondi);
        final byte[] text = new byte[arrondi];
        for (int i = 0; i<text0.length; i++) {
        	text[i] = text0[i];
        }
        for (int i = text0.length; i<arrondi; i++) {
        	text[i] = 'Z';
        }
        return text;
	}
	
}
