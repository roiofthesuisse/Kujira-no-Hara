package menu;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import map.Event.Direction;

/**
 * Une Liste est un tableau d'ElementsDeMenu a plusieurs lignes et colonnes.
 * 
 * @param <T> est un collectable du jeu susceptible d'�tre list�.
 */
public class Liste<T extends Listable> {
	private static final Logger LOG = LogManager.getLogger(Liste.class);
	private static final String METHODE_OBTENIR_LISTABLES = "obtenirTousLesListables";
	
	/** Listables qui seront mentionn�s par la Liste */
	Map<Integer, Listable> tousLesListables;
	ArrayList<Integer> numerosDesListables;
	
	/** Position x de la Liste */
	private final int x;
	/** Position y de la Liste */
	private final int y;
	/** Nombre de colonnes du tableau bidimensionnel affich� */
	private final int nombreDeColonnes;
	/** Nombre de lignes visibles a l'ecran a la fois */
	private final int nombreDeLignesVisibles;
	/** Combien de lignes dans le tableau en tout ? */
	private final int nombreDeLignesTotal;
	/** largeur (en pixels) maximale pour l'image d'un des ElementsDeMenu de la Liste */
	public int largeurMaximaleElement;
	/** hauteur (en pixels) maximale pour l'image d'un des ElementsDeMenu de la Liste */
	public int hauteurMaximaleElement;
	
	/** Espacement horizontal entre les �l�ments de la liste */
	final int margeADroite;
	/** Espacement vertical entre les �l�ments de la liste */
	final int interligne;
	
	/** num�ro de l'ElementDeMenu s�lectionn� dans la Liste */
	private int numeroElementSelectionne;
	/** premi�re ligne visibles a l'ecran */
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
	 * @param nombreDeLignesVisibles nombre de lignes visibles simultan�ment a l'ecran
	 * @param margeADroite espacement horizontal entre les �l�ments de la liste
	 * @param largeurMinimaleElements largeur minimale pour un �l�ment
	 * @param hauteurMinimaleElements hauteur minimale pour un �l�ment
	 * @param interligne espacement horizontal entre les �l�ments de la liste
	 * @param provenance quel est la nature du Listable a afficher ?
	 * @param possedes n'affiche-t-on que les Listables poss�d�s par le joueur ?
	 * @param avec liste exhaustive des num�ros des Listables a afficher
	 * @param toutSauf liste exhaustive des num�ros des Listables a ne pas afficher
	 */
	public Liste(final int x, final int y, final int nombreDeColonnes, final int nombreDeLignesVisibles,
			final int largeurMinimaleElements, final int hauteurMinimaleElements, final int margeADroite, final int interligne,
			final Class<T> provenance, final boolean possedes, final ArrayList<Integer> avec, 
			final ArrayList<Integer> toutSauf) {
		this.x = x;
		this.y = y;
		this.nombreDeColonnes = nombreDeColonnes;
		this.nombreDeLignesVisibles = nombreDeLignesVisibles;
		this.largeurMaximaleElement = largeurMinimaleElements; //pour l'instant fix� au minimum, mais sera augment� �ventuellement apres
		this.hauteurMaximaleElement = hauteurMinimaleElements; //pour l'instant fix� au minimum, mais sera augment� �ventuellement apres
		this.margeADroite = margeADroite;
		this.interligne = interligne;
		
		// Recenser les Listables concern�s par cette Liste
		recenserLesListablesAConsiderer(provenance, possedes, avec, toutSauf);
		this.elements = genererLesImagesDesElements(largeurMinimaleElements, hauteurMinimaleElements);
		
		// Remplir le tableau bidimensionnel des �l�ments a afficher
		this.nombreDeLignesTotal = this.elements.size() / this.nombreDeColonnes + (this.elements.size() % this.nombreDeColonnes != 0 ? 1 : 0);
		determinerLesElementsAAfficher();
	}
	
	/**
	 * Recenser les Listables qui figureront dans la Liste.
	 * @param provenance classe d'o� proviennent les Listables
	 * @param possedes consid�rer seulement les Listables poss�d�s ou non
	 * @param avec identifiants des collectables a inclure
	 * @param toutSauf identifiants des collectables a ne pas inclure
	 */
	@SuppressWarnings("unchecked")
	private void recenserLesListablesAConsiderer(final Class<T> provenance, final Boolean possedes, 
			final ArrayList<Integer> avec, final ArrayList<Integer> toutSauf) {

		try {
			this.tousLesListables = (Map<Integer, Listable>) 
					provenance.getDeclaredMethod(METHODE_OBTENIR_LISTABLES, Boolean.class).invoke(null, possedes);
			
			// Recensement des num�ros des Listables a consid�rer
			if (avec != null && avec.size()>0) {
				// liste blanche
				this.numerosDesListables = avec;
			} else {
				// liste noire
				this.numerosDesListables = new ArrayList<Integer>();
				this.numerosDesListables.addAll(tousLesListables.keySet());
				if (toutSauf != null && toutSauf.size()>0) {
					for (Integer numeroARetirer : toutSauf) {
						this.numerosDesListables.remove(numeroARetirer);
					}
				}
			}
		} catch (NoSuchMethodException e) {
			LOG.error("Impossible de trouve la Methode '"+METHODE_OBTENIR_LISTABLES+"' pour obtenir chaque "+provenance.getName(), e);
		} catch (SecurityException e) {
			LOG.error("Probl�me de s�curit� ! Tous aux abris !", e);
		} catch (IllegalAccessException e) {
			LOG.error("Acc�s incorrect a la Methode d'un Listable.", e);
		} catch (IllegalArgumentException e) {
			LOG.error("Arguments incorrects pour la Methode d'un Listable.", e);
		} catch (InvocationTargetException e) {
			LOG.error("Impossible de joindre ce type de Listable.", e);
		}
	}
	
	/**
	 * G�n�rer l'ElementDeMenu de chaque Listable de la Liste.
	 * @param largeurMinimaleElement largeur minimale des �l�ments de la liste
	 * @param hauteurMinimaleElement hauteur minimale des �l�ments de la liste
	 * @return ElementsDeMenu de la Liste
	 */
	public ArrayList<ImageMenu> genererLesImagesDesElements(final int largeurMinimaleElement, final int hauteurMinimaleElement) {
		final ArrayList<ImageMenu> elements = new ArrayList<ImageMenu>();
		
		// Cr�er un ElementDeMenu pour chaque num�ro
		Listable listable;
		BufferedImage image;
		ImageMenu element;
		for (Integer numero : this.numerosDesListables) {
			listable = tousLesListables.get(numero);
			image = listable.construireImagePourListe(largeurMinimaleElement, hauteurMinimaleElement);
			element = new ImageMenu(
					image, //apparence
					0, 0, //coordonn�es (en pixel) temporaires
					-1, -1, //largeur/hauteur forc�es
					null, //conditions
					true, //s�lectionnable
					listable.getComportementSelection(), //comportement au survol
					listable.getComportementConfirmation(), //comportement a la confirmation
					-1 //id temporaire de l'ElementDeMenu
			);
			elements.add(element);
			element.liste = this;
		}
		
		return elements;
	}
	
	/**
	 * Remplir le tableau bidimensionnel des �l�ments a afficher a partir du contenu de la Liste.
	 */
	public final void determinerLesElementsAAfficher() {
		this.elementsAffiches = new ImageMenu[this.nombreDeLignesVisibles][this.nombreDeColonnes];
		final int taille = elements.size();
		int iElement, jElement;
		for (int n = 0; n<taille; n++) {
			final ImageMenu element = this.elements.get(n);
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
					final ImageMenu element = this.elementsAffiches[i][j];
					element.x = this.x + (this.largeurMaximaleElement+this.margeADroite) * j;
					element.y = this.y + (this.hauteurMaximaleElement+this.interligne) * (i - this.premiereLigneVisible);
				}
			}
		}
	}

	/**
	 * Chercher un autre ElementDeMenu a s�lectionner dans la Liste.
	 * @param direction dans laquelle chercher
	 * @return ElementDeMenu a s�lectionner, ou null si bord de Liste
	 */
	public ElementDeMenu selectionnerUnAutreElementDansLaListe(final int direction) {
		// D�placement du curseur dans le tableau
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
		
		// On va au dernier Element lorsque la derni�re ligne est lacunaire
		final int taille = this.elements.size();
		if (this.numeroElementSelectionne >= taille) {
			this.numeroElementSelectionne = taille - 1;
		}
		final ElementDeMenu nouvelElementSelectionne = this.elements.get(this.numeroElementSelectionne);

		LOG.debug("nouvel �lement s�lectionn� dans la liste : "+ this.numeroElementSelectionne);
		
		// Eventuellement masquer/afficher certains ElementsDeMenu en fonction du nombre de lignes/colonnes a afficher
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
					element.x = this.x + (this.largeurMaximaleElement+this.margeADroite) * j;
					element.y = this.y + (this.hauteurMaximaleElement+this.interligne) * (i - this.premiereLigneVisible);
					this.elementsAffiches[i - this.premiereLigneVisible][j] = element;
				} else {
					element.invisible = true;
				}
			}
		}
		
		return nouvelElementSelectionne;
	}
	
	/**
	 * R�cup�rer une Liste d'ElementsDeMenu.
	 * @param jsonElement objet JSON repr�sentant la Liste
	 * @param x position x (en pixels) de la Liste dans le Menu
	 * @param y position y (en pixels) de la Liste dans le Menu
	 * @return Liste d'ElementsDeMenu.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Liste recupererElementDeMenuListe(final JSONObject jsonElement, final int x, final int y) {
		final String natureDuListable = Listable.PREFIXE_NOM_CLASSE + jsonElement.getString("provenance");
		try {
			final Class<? extends Listable> provenance = (Class<? extends Listable>) Class.forName(natureDuListable);
			
			final int nombreDeColonnes = jsonElement.getInt("nombreDeColonnes");
			final int nombreDeLignesVisibles = jsonElement.getInt("nombreDeLignesVisibles");
			final int largeurMinimaleElements = jsonElement.has("largeurMinimaleElements") ? jsonElement.getInt("largeurMinimaleElements") : 0;
			final int hauteurMinimaleElements = jsonElement.has("hauteurMinimaleElements") ? jsonElement.getInt("hauteurMinimaleElements") : 0;
			final int margeADroite = jsonElement.has("margeADroite") ? jsonElement.getInt("margeADroite") : Texte.MARGE_A_DROITE;
			final int interligne = jsonElement.has("interligne") ? jsonElement.getInt("interligne") : Texte.INTERLIGNE;
			
			final boolean possedes = jsonElement.getBoolean("possedes");
			
			final JSONArray jsonTousSauf; 
			ArrayList<Integer> tousSauf = new ArrayList<Integer>();
			try {
				jsonTousSauf = jsonElement.getJSONArray("tousSauf");
				for (Object o : jsonTousSauf) {
					tousSauf.add((Integer) o);
				}
			} catch (JSONException e) {
				tousSauf = null;
			}
			
			final JSONArray jsonAvec;
			ArrayList<Integer> avec = new ArrayList<Integer>();
			try {
				jsonAvec = jsonElement.getJSONArray("avec");
				for (Object o : jsonAvec) {
					avec.add((Integer) o);
				}
				
			} catch (JSONException e) {
				avec = null;
			}

			Liste liste = null;
			try {
				liste = new Liste(x, y, nombreDeColonnes, nombreDeLignesVisibles, largeurMinimaleElements, hauteurMinimaleElements, margeADroite, interligne,
						provenance, possedes, avec, tousSauf);
			} catch (Exception e) {
				LOG.error("Impossible de cr�er la liste d'�l�ments pour le menu !", e);
			}
			return liste;
		} catch (ClassNotFoundException e) {
			LOG.error(natureDuListable+" n'est pas un Listable connu.", e);
			return null;
		}
	}

}
