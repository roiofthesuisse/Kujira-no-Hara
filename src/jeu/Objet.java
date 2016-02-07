package jeu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import commandesMenu.CommandeMenu;

/**
 * Le joueur collecte des Objets.
 */
public class Objet {
	public final Integer numero; //Integer car clé d'une HashMap
	public final String nom;
	public BufferedImage icone;
	public final String description;
	public final ArrayList<CommandeMenu> effet;
	
	/**
	 * Constructeur explicite
	 * @param numero dans le Menu
	 * @param nom de l'Objet
	 * @param nomIcone nom de l'icône de l'Objet affichée dans le Menu
	 * @param description de l'Objet
	 * @param effet de l'Objet lorsqu'on le consomme
	 */
	private Objet(final int numero, final String nom, final String nomIcone, final String description, final ArrayList<CommandeMenu> effet) {
		this.numero = numero;
		this.nom = nom;
		//ouverture de l'image d'apparence
		try {
			this.icone = ImageIO.read(new File(".\\ressources\\Graphics\\Icons\\"+nomIcone));
		} catch (IOException e) {
			//l'image d'apparence n'existe pas
			//e.printStackTrace();
		}
		
		this.description = description;
		this.effet = effet;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	@SuppressWarnings("unchecked")
	public Objet(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero"), 
			(String) parametres.get("nom"),
			(String) parametres.get("nomIcone"),
			(String) parametres.get("description"),
			(ArrayList<CommandeMenu>) parametres.get("effet")
		);
	}

}
