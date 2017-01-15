package menu;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import map.Event.Direction;

/**
 * Une Liste est un tableau d'ElementsDeMenu à plusieurs lignes et colonnes.
 * 
 * @param <T> est un collectable du jeu susceptible d'être listé.
 */
public class Liste<T extends Listable> {
	private static final Logger LOG = LogManager.getLogger(Liste.class);
	private static final String METHODE_OBTENIR_LISTABLES = "obtenirTousLesListables";
	
	/** Position x de la Liste */
	private final int x;
	/** Position y de la Liste */
	private final int y;
	/** Nombre de colonnes du tableau bidimensionnel affiché */
	private final int nombreDeColonnes;
	/** Nombre de lignes visibles à l'écran à la fois */
	private final int nombreDeLignesVisibles;
	/** Combien de lignes dans le tableau en tout ? */
	private final int nombreDeLignesTotal;
	/** largeur (en pixels) maximale pour l'image d'un des ElementsDeMenu de la Liste */
	private int largeurMaximaleElement;
	/** hauteur (en pixels) maximale pour l'image d'un des ElementsDeMenu de la Liste */
	private int hauteurMaximaleElement;
	
	/** numéro de l'ElementDeMenu sélectionné dans la Liste */
	private int numeroElementSelectionne;
	/** première ligne visibles à l'écran */
	private int premiereLigneVisible = 0;
	/** ElementsDeMenu de la Liste */
	public ArrayList<ImageMenu> elements;
	/** ElementsDeMenu visibles de la Liste */
	public ImageMenu[][] elementsAffiches;
	
	/**
	 * Constructeur explicite
	 * @param x position x de la Liste dans le Menu
	 * @param y position y de la Liste dans le Menu
	 * @param nombreDeColonnes nombre de colonnes du tableau
	 * @param nombreDeLignesVisibles nombre de lignes visibles simultanément à l'écran
	 * @param provenance quel est la nature du Listable à afficher ?
	 * @param possedes n'affiche-t-on que les Listables possédés par le joueur ?
	 * @param avec liste exhaustive des numéros des Listables à afficher
	 * @param toutSauf liste exhaustive des numéros des Listables à ne pas afficher
	 */
	public Liste(final int x, final int y, final int nombreDeColonnes, final int nombreDeLignesVisibles,
			final Class<T> provenance, final boolean possedes, final ArrayList<Integer> avec, 
			final ArrayList<Integer> toutSauf) {
		this.x = x;
		this.y = y;
		this.nombreDeColonnes = nombreDeColonnes;
		this.nombreDeLignesVisibles = nombreDeLignesVisibles;
		
		this.elements = construireLesElements(provenance, possedes, avec, toutSauf);
		this.nombreDeLignesTotal = this.elements.size() / this.nombreDeColonnes + (this.elements.size() % this.nombreDeColonnes != 0 ? 1 : 0);
		
		// On remplit le tableau bidimensionnel avec le contenu de la liste
		this.elementsAffiches = new ImageMenu[this.nombreDeLignesVisibles][this.nombreDeColonnes];
		final int taille = elements.size();
		int iElement, jElement;
		ImageMenu element;
		for (int n = 0; n<taille; n++) {
			element = this.elements.get(n);
			element.liste = this;
			iElement = n / this.nombreDeColonnes;
			jElement = n % this.nombreDeColonnes;
			if (iElement < this.nombreDeLignesVisibles) {
				this.elementsAffiches[iElement][jElement] = element;
			} else {
				element.invisible = true;
			}
			
			if (element.image.getWidth() > this.largeurMaximaleElement) {
				this.largeurMaximaleElement = element.image.getWidth();
			}
			if (element.image.getHeight() > this.hauteurMaximaleElement) {
				this.hauteurMaximaleElement = element.image.getHeight();
			}
		}
		for (int i = 0; i<this.nombreDeLignesVisibles; i++) {
			for (int j = 0; j<this.nombreDeColonnes; j++) {
				if (i * this.nombreDeColonnes + j < taille) {
					element = this.elementsAffiches[i][j];
					element.x = this.x + (this.largeurMaximaleElement+Texte.MARGE_A_DROITE) * j;
					element.y = this.y + (this.hauteurMaximaleElement+Texte.INTERLIGNE) * (i - this.premiereLigneVisible);
				}
			}
		}
	}
	
	/**
	 * Générer les ElementsDeMenu qui figureront dans la Liste.
	 * @param provenance nature des collectables à lister
	 * @param possedes considérer seulement les Listables possédés ou non
	 * @param avec identifiants des collectables à inclure
	 * @param toutSauf identifiants des collectables à ne pas inclure
	 * @return ElementsDeMenu de la Liste
	 */
	private ArrayList<ImageMenu> construireLesElements(final Class<T> provenance, final Boolean possedes, 
			final ArrayList<Integer> avec, final ArrayList<Integer> toutSauf) {
		
		final ArrayList<ImageMenu> elements = new ArrayList<ImageMenu>();
		try {

			@SuppressWarnings("unchecked")
			final Map<Integer, Listable> tousLesListables = (Map<Integer, Listable>) 
					provenance.getDeclaredMethod(METHODE_OBTENIR_LISTABLES, Boolean.class).invoke(null, possedes);
			
			
			// Recensement des numéros des Listables à considérer
			final ArrayList<Integer> numerosDesListables;
			if (avec != null && avec.size()>0) {
				// liste blanche
				numerosDesListables = avec;
			} else {
				// liste noire
				numerosDesListables = new ArrayList<Integer>();
				numerosDesListables.addAll(tousLesListables.keySet());
				if (toutSauf != null && toutSauf.size()>0) {
					for (Integer numeroARetirer : toutSauf) {
						numerosDesListables.remove(numeroARetirer);
					}
				}
			}

			// Créer un ElementDeMenu pour chaque numéro
			Listable listable;
			BufferedImage image;
			ImageMenu element;
			for (Integer numero : numerosDesListables) {
				listable = tousLesListables.get(numero);
				image = listable.construireImagePourListe();
				element = new ImageMenu(
						image, //apparence
						0, 0, //coordonnées (en pixel) temporaires
						-1, -1, //largeur/hauteur forcées
						null, //conditions
						true, //sélectionnable
						listable.getComportementSelection(), //comportement au survol
						listable.getComportementConfirmation(), //comportement à la confirmation
						-1 //id temporaire de l'ElementDeMenu
				);
				elements.add(element);
			}
		
		} catch (NoSuchMethodException e) {
			LOG.error("Impossible de trouve la méthode '"+METHODE_OBTENIR_LISTABLES+"' pour obtenir chaque "+provenance.getName(), e);
		} catch (SecurityException e) {
			LOG.error("Problème de sécurité ! Tous aux abris !", e);
		} catch (IllegalAccessException e) {
			LOG.error("Accès incorrect à la méthode d'un Listable.", e);
		} catch (IllegalArgumentException e) {
			LOG.error("Arguments incorrects pour la méthode d'un Listable.", e);
		} catch (InvocationTargetException e) {
			LOG.error("Impossible de joindre ce type de Listable.", e);
		}
		
		return elements;
	}

	/**
	 * Chercher un autre ElementDeMenu à sélectionner dans la Liste.
	 * @param direction dans laquelle chercher
	 * @return ElementDeMenu à sélectionner, ou null si bord de Liste
	 */
	public ElementDeMenu selectionnerUnAutreElementDansLaListe(final int direction) {
		// Déplacement du curseur dans le tableau
		switch (direction) {
			case Direction.GAUCHE :
				if (this.numeroElementSelectionne % this.nombreDeColonnes == 0) {
					// on sort de la Liste
					return null;
				} else {
					this.numeroElementSelectionne--;
				}
				break;
			case Direction.HAUT :
				if (this.numeroElementSelectionne - this.nombreDeColonnes < 0) {
					// on sort de la Liste
					return null;
				} else {
					this.numeroElementSelectionne -= this.nombreDeColonnes;
				}
				break;
			case Direction.DROITE :
				if ((this.numeroElementSelectionne+1) % this.nombreDeColonnes == 0) {
					// on sort de la Liste
					return null;
				} else {
					this.numeroElementSelectionne++;
				}
				break;
			case Direction.BAS :
				if (this.numeroElementSelectionne / this.nombreDeColonnes == this.nombreDeLignesTotal - 1) {
					// on sort de la Liste
					return null;
				} else {
					this.numeroElementSelectionne += this.nombreDeColonnes;
				}
				break;
		}
		
		// On va au dernier Element lorsque la dernière ligne est lacunaire
		final int taille = this.elements.size();
		if (this.numeroElementSelectionne >= taille) {
			this.numeroElementSelectionne = taille - 1;
		}
		final ElementDeMenu nouvelElementSelectionne = this.elements.get(this.numeroElementSelectionne);

		LOG.debug("nouvel élement sélectionné dans la liste : "+ this.numeroElementSelectionne);
		
		// Eventuellement masquer/afficher certains ElementsDeMenu en fonction du nombre de lignes/colonnes à afficher
		boolean decalageDuTableau = false;
		if (this.numeroElementSelectionne < this.premiereLigneVisible * this.nombreDeColonnes ) {
			this.premiereLigneVisible--;
			decalageDuTableau = true;
		} else if (this.numeroElementSelectionne >= (this.premiereLigneVisible+this.nombreDeLignesVisibles) * this.nombreDeColonnes) {
			this.premiereLigneVisible++;
			decalageDuTableau = true;
		}
		if (decalageDuTableau) {
			int i, j;
			ImageMenu element;
			this.elementsAffiches = new ImageMenu[this.nombreDeLignesVisibles][this.nombreDeColonnes];
			for (int n = 0; n < taille; n++) {
				element = this.elements.get(n);
				i = n / this.nombreDeColonnes;
				j = n % this.nombreDeColonnes;
				if (i>=this.premiereLigneVisible && i<this.premiereLigneVisible+this.nombreDeLignesVisibles) {
					element.invisible = false;
					element.x = this.x + (this.largeurMaximaleElement+Texte.MARGE_A_DROITE) * j;
					element.y = this.y + (this.hauteurMaximaleElement+Texte.INTERLIGNE) * (i - this.premiereLigneVisible);
					this.elementsAffiches[i - this.premiereLigneVisible][j] = element;
				} else {
					element.invisible = true;
				}
			}
		}
		
		return nouvelElementSelectionne;
	}

}
