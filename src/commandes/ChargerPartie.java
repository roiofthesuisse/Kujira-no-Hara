package commandes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
	
	@Override
	public final void executer() {
		final Fenetre fenetre = this.element.menu.lecteur.fenetre;
		LOG.info("chargement de la partie numero "+this.numeroDeSauvegarde);
		
		try {
			final byte[] bytesCryptes = Files.readAllBytes(Paths.get("./saves/save"+this.numeroDeSauvegarde+".txt"));
			final String partieDecryptee = decrypter(bytesCryptes, construireCleDeCryptage());
			final JSONObject jsonSauvegarde = new JSONObject(partieDecryptee);
			final JSONObject jsonEtatMap = (JSONObject) jsonSauvegarde.get("etatMap");
			final JSONObject jsonAvancement = (JSONObject) jsonSauvegarde.get("partie");
			Partie partie = new Partie(
					jsonEtatMap.getInt("numero"),
					jsonEtatMap.getInt("xHeros"),
					jsonEtatMap.getInt("yHeros"),
					jsonEtatMap.getInt("directionHeros"),
					jsonAvancement.getInt("vie"),
					jsonAvancement.getInt("vieMax"),
					jsonAvancement.getInt("idArmeEquipee"),
					jsonAvancement.getInt("idGadgetEquipe"),
					jsonAvancement.getJSONArray("objetsPossedes"), // int[]
					jsonAvancement.getJSONArray("avancementDesQuetes"), // AvancementQuete[]
					jsonAvancement.getJSONArray("armesPossedees"), // boolean[] 
					jsonAvancement.getJSONArray("gadgetsPossedes") // boolean[] 
			);
			fenetre.setPartieActuelle(partie);
			fenetre.ouvrirLaPartie();
		} catch (IOException e) {
			LOG.error("Impossible d'ouvrir le fichier de partie numero "+this.numeroDeSauvegarde, e);
		}
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		this.executer();
		return curseurActuel+1;
	}
	
	/**
	 * Décrypter un texte.
	 * @param bytesCryptes fichier crypté
	 * @param cle de décryptage
	 * @return texte décrypté
	 */
	private String decrypter(final byte[] bytesCryptes, final SecretKeySpec cle) {
		try {
			final Cipher aesCipher = Cipher.getInstance("AES");
			aesCipher.init(Cipher.DECRYPT_MODE, cle);
	        final byte[] texteDecrypte = aesCipher.doFinal(bytesCryptes);
	        return new String(texteDecrypte, StandardCharsets.UTF_8);
		} catch (NoSuchAlgorithmException e) {
			LOG.error("Algorithme de décryptage inconnu !", e);
		} catch (NoSuchPaddingException e) {
			LOG.error(e);
		} catch (InvalidKeyException e) {
			LOG.error("Mauvaise clé de décryptage !", e);
		} catch (IllegalBlockSizeException e) {
			LOG.error("Bloc de taille incorrecte !", e);
		} catch (BadPaddingException e) {
			LOG.error(e);
		}
		return null;
	}

}
