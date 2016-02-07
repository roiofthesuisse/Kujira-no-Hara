package jeu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * Le joueur doit réussir des Quêtes durant le jeu.
 */
public class Quete {
	//constantes
	public static final String NOM_ICONE_QUETE_PAS_FAITE_PAR_DEFAUT = "quete a faire icon.png";
	public static final String NOM_ICONE_QUETE_FAITE_PAR_DEFAUT = "quete faite icon.png";
	public static final HashMap<String, BufferedImage> ICONES_MEMORISEES = new HashMap<String, BufferedImage>();
	
	public Integer numero; //Integer car clé d'une HashMap
	public String nom;
	public String description;
	public BufferedImage iconeQuetePasFaite;
	public BufferedImage iconeQueteFaite;
	public int xCarte;
	public int yCarte;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Quête
	 * @param nom de la Quête
	 * @param description de la Quête
	 * @param nomIconeQuetePasFaite nom de l'icône affichée lorsque la Quête n'est pas encore faite
	 * @param nomIconeQueteFaite nom de l'icône affichée lorsque la Quête a été  faite
	 * @param xCarte position x sur la carte des Quêtes
	 * @param yCarte position y sur la carte des Quêtes
	 */
	private Quete(final int numero, final String nom, final String description, final String nomIconeQuetePasFaite, final String nomIconeQueteFaite, final int xCarte, final int yCarte) {
		this.numero = numero;
		this.nom = nom;
		this.description = description;
		this.xCarte = xCarte;
		this.yCarte = yCarte;
		
		if (ICONES_MEMORISEES.containsKey(nomIconeQuetePasFaite)) {
			this.iconeQuetePasFaite = ICONES_MEMORISEES.get(nomIconeQuetePasFaite);
		} else {
			try {
				this.iconeQuetePasFaite = ImageIO.read(new File(".\\ressources\\Graphics\\Icons\\"+nomIconeQuetePasFaite));
				ICONES_MEMORISEES.put(nomIconeQuetePasFaite, this.iconeQuetePasFaite);
			} catch (IOException e) {
				//l'image d'apparence n'existe pas
				this.iconeQuetePasFaite = null;
				ICONES_MEMORISEES.put(nomIconeQuetePasFaite, null);
				System.err.println("Impossible de trouver l'icône de Quete : " + nomIconeQuetePasFaite);
			}
		}
		if (ICONES_MEMORISEES.containsKey(nomIconeQueteFaite)) {
			this.iconeQueteFaite = ICONES_MEMORISEES.get(nomIconeQueteFaite);
		} else {
			try {
				this.iconeQueteFaite = ImageIO.read(new File(".\\ressources\\Graphics\\Icons\\"+nomIconeQueteFaite));
				ICONES_MEMORISEES.put(nomIconeQueteFaite, this.iconeQueteFaite);
			} catch (IOException e) {
				//l'image d'apparence n'existe pas
				this.iconeQueteFaite = null;
				ICONES_MEMORISEES.put(nomIconeQueteFaite, null);
				System.err.println("Impossible de trouver l'icône de Quete : " + nomIconeQueteFaite);
			}
		}
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Quete(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero"), 
			(String) parametres.get("nom"),
			(String) parametres.get("description"),
			(String) (parametres.containsKey("nomIconeQuetePasFaite") ? parametres.get("nomIconeQuetePasFaite") : NOM_ICONE_QUETE_PAS_FAITE_PAR_DEFAUT),
			(String) (parametres.containsKey("nomIconeQueteFaite") ? parametres.get("nomIconeQueteFaite") : NOM_ICONE_QUETE_FAITE_PAR_DEFAUT),
			(int) parametres.get("xCarte"),
			(int) parametres.get("yCarte")
		);
	}

}
