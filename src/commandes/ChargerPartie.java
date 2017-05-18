package commandes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import jeu.Partie;
import main.Commande;
import main.Fenetre;

/**
 * Charger une Partie dans le menu dédié
 */
public class ChargerPartie extends Commande implements CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(ChargerPartie.class);
	
	private final int numeroDeSauvegarde;
	
	/**
	 * Constructeur explicite
	 * @param numeroDeSauvegarde numéro du fichier de sauvegarde à charger
	 */
	public ChargerPartie(final int numeroDeSauvegarde) {
		this.numeroDeSauvegarde = numeroDeSauvegarde;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ChargerPartie(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numeroSauvegarde") );
	}

	/**
	 * Retirer les ZZZZ inutiles à la fin du fichier.
	 * @param partieDecryptee0 fichier avec des ZZZ
	 * @return fichier sans les ZZZ
	 */
	private static byte[] retirerLAppendice(final byte[] partieDecryptee0) {
		int tailleDuFichier = partieDecryptee0.length;
		while (partieDecryptee0[tailleDuFichier-1] == 'Z') {
			tailleDuFichier--;
		}
		final byte[] partieDecryptee = new byte[tailleDuFichier];
		for (int i = 0; i<tailleDuFichier; i++) {
			partieDecryptee[i] = partieDecryptee0[i];
		}
		return partieDecryptee;
	}

	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		final Fenetre fenetre = this.element.menu.lecteur.fenetre;
		LOG.info("chargement de la partie numero "+this.numeroDeSauvegarde);
		
		try {
			final Partie partie = chargerPartie(this.numeroDeSauvegarde);
			fenetre.setPartieActuelle(partie);
			fenetre.ouvrirLaPartie();
		} catch (IOException e) {
			LOG.error("Impossible d'ouvrir le fichier de partie numero "+this.numeroDeSauvegarde, e);
		}
		
		return curseurActuel+1;
	}
	
	public static Partie chargerPartie(final int numeroDeSauvegarde) throws IOException {
		final Path path = Paths.get(Sauvegarder.NOM_DOSSIER_SAUVEGARDES + Sauvegarder.PREFIXE_FICHIER_SAUVEGARDE + numeroDeSauvegarde + ".txt");
	
		final byte[] bytesCryptes = Files.readAllBytes(path);
		
		final byte[] partieDecryptee = decrypter(bytesCryptes, construireCleDeCryptage());			
		
		LOG.debug(partieDecryptee);
		final JSONObject jsonSauvegarde = new JSONObject(new String(partieDecryptee));
		final JSONObject jsonAvancement = (JSONObject) jsonSauvegarde.get("partie");
		final JSONObject jsonEtatMap = (JSONObject) jsonSauvegarde.get("etatMap");
		return new Partie(
				numeroDeSauvegarde,
				jsonEtatMap.getInt("numero"),
				jsonEtatMap.getInt("xHeros"),
				jsonEtatMap.getInt("yHeros"),
				jsonEtatMap.getInt("directionHeros"),
				jsonEtatMap, //infos sur le brouillard
				jsonAvancement.getInt("vie"),
				jsonAvancement.getInt("vieMax"),
				jsonAvancement.getInt("argent"),
				jsonAvancement.getInt("idArmeEquipee"),
				jsonAvancement.getInt("idGadgetEquipe"),
				jsonAvancement.getJSONArray("objetsPossedes"), // int[]
				jsonAvancement.getJSONArray("avancementQuetes"), // AvancementQuete[]
				jsonAvancement.getJSONArray("armesPossedees"), // boolean[] 
				jsonAvancement.getJSONArray("gadgetsPossedes"), // boolean[] 
				jsonAvancement.getJSONArray("interrupteurs"),
				jsonAvancement.getJSONArray("variables"),
				jsonAvancement.getJSONArray("interrupteursLocaux"),
				jsonAvancement.getJSONArray("images")
		);
	}
	
	/**
	 * Décrypter un texte.
	 * @param bytesCryptes fichier crypté
	 * @param cle de décryptage
	 * @return texte décrypté
	 */
	private static byte[] decrypter(final byte[] bytesCryptes, final SecretKeySpec cle) {
		try {
			LOG.debug(new String(bytesCryptes));
			
			// Hashage de la clé
			final SecretKeySpec cleHashee = construireCleDeCryptage();
			
			// Décryptage du texte avec la clé hashée
			final Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            final byte[] ivByte = new byte[cipher.getBlockSize()];
            final IvParameterSpec ivParamsSpec = new IvParameterSpec(ivByte);
            cipher.init(Cipher.DECRYPT_MODE, cleHashee, ivParamsSpec);
            final byte[] original = cipher.doFinal(bytesCryptes);
			
			LOG.debug(new String(original));
			
			// On retire les ZZZZ à la fin
			return retirerLAppendice(original);
			
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
		}
		return null;
	}

}
