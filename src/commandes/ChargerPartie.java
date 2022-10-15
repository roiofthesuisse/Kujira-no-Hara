package commandes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import jeu.Partie;
import main.Commande;
import main.Main;

/**
 * Charger une Partie dans le menu d�di�
 */
public class ChargerPartie extends Commande implements CommandeMenu {
	private static final Logger LOG = LogManager.getLogger(ChargerPartie.class);

	private final int numeroDeSauvegarde;

	/**
	 * Constructeur explicite
	 * 
	 * @param numeroDeSauvegarde Numero du fichier de sauvegarde a charger
	 */
	public ChargerPartie(final int numeroDeSauvegarde) {
		this.numeroDeSauvegarde = numeroDeSauvegarde;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public ChargerPartie(final HashMap<String, Object> parametres) {
		this((int) parametres.get("numeroSauvegarde"));
	}

	/**
	 * Retirer les ZZZZ inutiles a la fin du fichier.
	 * 
	 * @param partieDecryptee0 fichier avec des ZZZ
	 * @return fichier sans les ZZZ
	 */
	private static byte[] retirerLAppendice(final byte[] partieDecryptee0) {
		int tailleDuFichier = partieDecryptee0.length;
		while (partieDecryptee0[tailleDuFichier - 1] == 'Z') {
			tailleDuFichier--;
		}
		final byte[] partieDecryptee = new byte[tailleDuFichier];
		for (int i = 0; i < tailleDuFichier; i++) {
			partieDecryptee[i] = partieDecryptee0[i];
		}
		return partieDecryptee;
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandes) {
		LOG.info("chargement de la partie numero " + this.numeroDeSauvegarde);

		try {
			final Partie partie = chargerPartie(this.numeroDeSauvegarde);
			Main.setPartieActuelle(partie);
			Main.ouvrirLaPartie();
		} catch (Exception e) {
			LOG.error("Impossible de charger la partie numero " + this.numeroDeSauvegarde, e);
		}

		return curseurActuel + 1;
	}

	/**
	 * Charger une Partie a partir du Numero de la Sauvegarde.
	 * 
	 * @param numeroDeSauvegarde qui identifie le fichier de Sauvegarde
	 * @return Partie charg�e
	 * @throws JSONException le fichier de Sauvegarde n'a pas le bon format
	 * @throws Exception     le fichier de Nouvelle Partie n'a pas le bon format
	 */
	public static Partie chargerPartie(final int numeroDeSauvegarde) throws JSONException, Exception {
		final Path path = Paths.get(Sauvegarder.NOM_DOSSIER_SAUVEGARDES + Sauvegarder.PREFIXE_FICHIER_SAUVEGARDE
				+ numeroDeSauvegarde + ".txt");

		final byte[] bytesCryptes = Files.readAllBytes(path);

		final byte[] partieDecryptee = decrypter(bytesCryptes, construireCleDeCryptage());

		LOG.debug(partieDecryptee);
		final JSONObject jsonSauvegarde = new JSONObject(new String(partieDecryptee));
		final JSONObject jsonAvancement = (JSONObject) jsonSauvegarde.get("partie");
		final JSONObject jsonEtatMap = (JSONObject) jsonSauvegarde.get("etatMap");
		return new Partie(numeroDeSauvegarde, jsonEtatMap.getInt("numero"), jsonEtatMap.getInt("xHeros"),
				jsonEtatMap.getInt("yHeros"), jsonEtatMap.getInt("directionHeros"), jsonEtatMap, // infos sur le
																									// brouillard
				jsonAvancement.getInt("vie"), jsonAvancement.getInt("vieMax"), jsonAvancement.getInt("argent"),
				jsonAvancement.getInt("idArmeEquipee"), jsonAvancement.getInt("idGadgetEquipe"),
				jsonAvancement.getJSONArray("objetsPossedes"), // int[]
				jsonAvancement.getJSONArray("avancementQuetes"), // AvancementQuete[]
				jsonAvancement.getJSONArray("armesPossedees"), // boolean[]
				jsonAvancement.getJSONArray("gadgetsPossedes"), // boolean[]
				jsonAvancement.getJSONArray("interrupteurs"), jsonAvancement.getJSONArray("variables"),
				jsonAvancement.getJSONArray("interrupteursLocaux"), jsonAvancement.getJSONArray("mots"),
				jsonAvancement.has("chronometre") ? jsonAvancement.getJSONObject("chronometre") : null,
				jsonAvancement.getJSONArray("images"));
	}

	/**
	 * D�crypter un texte.
	 * 
	 * @param bytesCryptes fichier crypt�
	 * @param cle          de d�cryptage
	 * @return texte d�crypt�
	 */
	private static byte[] decrypter(final byte[] bytesCryptes, final SecretKeySpec cle) {
		try {
			LOG.debug(new String(bytesCryptes));

			// Hashage de la cl�
			final SecretKeySpec cleHashee = construireCleDeCryptage();

			// D�cryptage du texte avec la cl� hash�e
			final Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			final byte[] ivByte = new byte[cipher.getBlockSize()];
			final IvParameterSpec ivParamsSpec = new IvParameterSpec(ivByte);
			cipher.init(Cipher.DECRYPT_MODE, cleHashee, ivParamsSpec);
			final byte[] original = cipher.doFinal(bytesCryptes);

			LOG.debug(new String(original));

			// On retire les ZZZZ a la fin
			return retirerLAppendice(original);

		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
		}
		return null;
	}

}
