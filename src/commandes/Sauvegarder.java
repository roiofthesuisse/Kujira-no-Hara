package commandes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import jeu.Partie;
import jeu.Quete.AvancementQuete;
import main.Commande;
import main.Fenetre;
import map.Map;

/**
 * Sauvegarder la Partie ainsi que l'état de la Map actuelle dans un fichier externe crypté.
 */
public class Sauvegarder extends Commande implements CommandeMenu, CommandeEvent {
	private static final Logger LOG = LogManager.getLogger(Sauvegarder.class);
	/** Le cryptage impose des textes de taille multiple de 16 bytes */
	private static final int MULTIPLE_DE_SEIZE = 16;
	
	private int numeroSauvegarde;
	
	/**
	 * Constructeur explicite
	 * @param numeroSauvegarde numéro du fichier de Sauvegarde
	 */
	private Sauvegarder(final int numeroSauvegarde) {
		this.numeroSauvegarde = numeroSauvegarde;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Sauvegarder(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numeroSauvegarde") );
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final String filename = "./saves/save"+this.numeroSauvegarde+".txt";
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
		final JSONObject jsonPartie = new JSONObject();
		final Partie partie = Fenetre.getPartieActuelle();
		jsonPartie.put("vie", partie.vie);
		jsonPartie.put("vieMax", partie.vieMax);
		jsonPartie.put("argent", partie.argent);
		
		//armes
		jsonPartie.put("idArmeEquipee", partie.idArmeEquipee);
		final JSONArray armesPossedees = new JSONArray();
		for (int i = 0; i<partie.armesPossedees.length; i++) {
			if (partie.armesPossedees[i]) {
				armesPossedees.put(i);
			}
		}
		jsonPartie.put("armesPossedees", armesPossedees);
		
		//gadgets
		jsonPartie.put("idGadgetEquipe", partie.idGadgetEquipe);
		final JSONArray gadgetsPossedes = new JSONArray();
		for (int i = 0; i<partie.gadgetsPossedes.length; i++) {
			if (partie.gadgetsPossedes[i]) {
				gadgetsPossedes.put(i);
			}
		}
		jsonPartie.put("gadgetsPossedes", gadgetsPossedes);
		
		//interrupteurs
		final JSONArray interrupteurs = new JSONArray();
		for (int i = 0; i<partie.interrupteurs.length; i++) {
			if (partie.interrupteurs[i]) {
				interrupteurs.put(i);
			}
		}
		jsonPartie.put("interrupteurs", interrupteurs);
		
		//variables
		final JSONArray variables = new JSONArray();
		for (int i = 0; i<partie.variables.length; i++) {
			if (partie.variables[i] != 0) {
				final JSONObject variable = new JSONObject();
				variable.put("numero", i);
				variable.put("valeur", partie.variables[i]);
				variables.put(variable);
			}
		}
		jsonPartie.put("variables", variables);
		
		//interrupteurs locaux
		final JSONArray interrupteursLocaux = new JSONArray();
		for (int i = 0; i<partie.interrupteursLocaux.size(); i++) {
			final String code = partie.interrupteursLocaux.get(i); // code de la forme mXXXeXXXiXXX (map, event, interrupteur)
			interrupteursLocaux.put(code);
		}
		jsonPartie.put("interrupteursLocaux", interrupteursLocaux);
		
		//objets
		final JSONArray objetsPossedes = new JSONArray();
		for (int i = 0; i<partie.objetsPossedes.length; i++) {
			if (partie.objetsPossedes[i] != 0) {
				final JSONObject objet = new JSONObject();
				objet.put("numero", i);
				objet.put("quantite", partie.objetsPossedes[i]);
				objetsPossedes.put(objet);
			}
		}
		jsonPartie.put("objetsPossedes", objetsPossedes);
		
		//quêtes
		final JSONArray avancementQuetes = new JSONArray();
		for (int i = 0; i<partie.avancementDesQuetes.length; i++) {
			if (partie.avancementDesQuetes[i]!= null 
					&& !AvancementQuete.INCONNUE.equals(partie.avancementDesQuetes[i])) {
				final JSONObject quete = new JSONObject();
				quete.put("numero", i);
				quete.put("avancement", partie.avancementDesQuetes[i].nom);
				avancementQuetes.put(quete);
			}
		}
		jsonPartie.put("avancementQuetes", avancementQuetes);
		
		
		jsonSauvegarde.put("partie", jsonPartie);
		
		
		// Etat de la Map
		final JSONObject jsonEtatMap = new JSONObject();
		final Map map;
		if (this.element != null) {
			map = this.element.menu.lecteur.lecteurMapMemorise.map;
		} else {
			map = this.page.event.map;
		}
		jsonEtatMap.put("numero", map.numero);
		jsonEtatMap.put("xHeros", map.heros.x);
		jsonEtatMap.put("yHeros", map.heros.y);
		jsonEtatMap.put("directionHeros", map.heros.direction);
		//TODO pour tous les events : x, y, direction, apparence, vitesse, frequence, proprietesActuelles, pageActive, curseur, mouvements
		//TODO images (état de déplacement, ce qui est fait)
		//TODO brouillard
		jsonSauvegarde.put("etatMap", jsonEtatMap);
		
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
