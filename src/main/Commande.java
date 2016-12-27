package main;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import map.PageEvent;
import menu.ElementDeMenu;

/**
 * Une Commande modifie l'état du jeu.
 * Elle peut être lancée par une Page d'Event, ou par un Elément de Menu.
 */
public abstract class Commande {
	//clé de cryptage
	private static final Logger LOG = LogManager.getLogger(Commande.class);
	private static final String CLE_CRYPTAGE_SAUVEGARDE = "t0p_k3k";
	private static final int NOMBRE_OCTETS_HASH = 16;
	
	/** [CommandeEvent] Eventuelle Page d'Event qui a appelé cette Commande */
	public PageEvent page;
	/** [CommandeMenu] Element de Menu qui a appelé cette Commande de Menu */
	public ElementDeMenu element;
	
	/**
	 * Execute la Commande totalement ou partiellement.
	 * Le curseur peut être inchangé (attendre n frames...) ;
	 * le curseur peut être incrémenté (assignement de variable...) ;
	 * le curseur peut faire un grand saut (boucles, conditions...).
	 * @param curseurActuel position du curseur avant que la Commande soit executée
	 * @param commandes liste des Commandes de la Page de comportement en train d'être lue
	 * @return nouvelle position du curseur après l'execution totale ou partielle de la Commande
	 */
	public abstract int executer(int curseurActuel, ArrayList<Commande> commandes);
	
	/**
	 * Construire la clé de cryptage.
	 * @return clé de cryptage
	 */
	protected final SecretKeySpec construireCleDeCryptage() {
		try {
			// Hashage de la clé
			byte[] cle = CLE_CRYPTAGE_SAUVEGARDE.getBytes("UTF-8");
			final MessageDigest sha = MessageDigest.getInstance("SHA-1");
			cle = sha.digest(cle);
			
			cle = Arrays.copyOf(cle, NOMBRE_OCTETS_HASH); //seulement les 128 premiers bits
			return new SecretKeySpec(cle, "AES");
			
		} catch (UnsupportedEncodingException e) {
			LOG.error(e);
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e);
		}
		return null;
	}
}
