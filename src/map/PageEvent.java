package map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.json.JSONException;
import org.json.JSONObject;

import commandes.Deplacement;
import conditions.Condition;
import conditions.ConditionParler;
import main.Commande;
import main.Fenetre;
import utilitaire.InterpreteurDeJson;

/**
 * Un Event peut avoir plusieurs comportements. Chaque comportement est décrit par une Page de comportements.
 * La Page est déclenchée si certaines Conditions sont vérifiées, ses Commandes sont alors executées.
 */
public class PageEvent {
	/** Event auquel appartient la Page */
	public Event event;
	/** numero de la Page */
	public final int numero;
	/** ce flag est automatiquement mis à true si contient une Page avec une condition Parler */
	private boolean sOuvreParParole = false;
	
	/** Conditions de déclenchement de la Page */
	public final ArrayList<Condition> conditions;
	
	/** liste de Commandes à executer dans l'ordre si les Conditions sont vérifiées */
	public final ArrayList<Commande> commandes;
	/**
	 * Le curseur indique quelle Commande executer.
	 * Il se déplace incrémentalement, mais on peut lui faire faire des sauts.
	 */
	public int curseurCommandes = 0;
	
	//apparence
	private String nomImage;
	public BufferedImage image;
	public boolean estPetit; //si < 32, considéré au sol
	public int directionInitiale;
	public int animationInitiale;
	
	//paramètres
	public boolean animeALArret;
	public boolean animeEnMouvement;
	public boolean traversable;
	public boolean directionFixe;
	public boolean auDessusDeTout;
	public int vitesse;
	public int frequence;
	
	//mouvement
	public Deplacement deplacementNaturel = null;
	
	/**
	 * Constructeur explicite
	 * @Warning constructeur à n'utiliser que pour le Héros
	 * @param numero de la Page
	 * @param conditions de déclenchement de la Page
	 * @param commandes à executer si la page est déclenchée
	 * @param nomImage nom du fichier image pour l'apparence
	 */
	public PageEvent(final int numero, final ArrayList<Condition> conditions, final ArrayList<Commande> commandes, final String nomImage) {
		this.numero = numero;
		
		//Conditions de déclenchement de la Page
		this.conditions = conditions;
		//Commandes Event de la Page
		this.commandes = commandes;
		
		//apparence de l'Event pour cette Page
		this.nomImage = nomImage;
		
		//propriétés de l'Event pour cette Page
		this.vitesse = Heros.VITESSE_HEROS_PAR_DEFAUT;
		this.frequence = Heros.FREQUENCE_HEROS_PAR_DEFAUT;
		this.animeALArret = Heros.ANIME_A_L_ARRET_HEROS_PAR_DEFAUT;
		this.animeEnMouvement = Heros.ANIME_EN_MOUVEMENT_HEROS_PAR_DEFAUT;
		this.traversable = Heros.TRAVERSABLE_HEROS_PAR_DEFAUT;
		this.directionFixe = Heros.DIRECTION_FIXE_HEROS_PAR_DEFAUT;
		this.auDessusDeTout = Heros.AU_DESSUS_DE_TOUT_HEROS_PAR_DEFAUT;
		
		
		//déplacement de l'Event pour cette Page
		this.deplacementNaturel = null;
		
		//ouverture de l'image d'apparence
		try {
			this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Characters\\"+nomImage));
		} catch (IOException e) {
			//l'image d'apparence n'existe pas
			this.image = null;
			e.printStackTrace();
		}
		this.estPetit = false;
		
		//on précise si c'est une Page qui s'ouvre en parlant à l'Event
		if (conditions!=null) {
			for (Condition cond : conditions) {
				if (cond.getClass().getName().equals( ConditionParler.class.getName() )) {
					this.sOuvreParParole = true;
				}
			}
		}
	}
	
	/**
	 * Constructeur générique
	 * La page de comportement est créée à partir du fichier JSON.
	 * @param numero de la Page
	 * @param pageJSON objet JSON décrivant la page de comportements
	 * @param idEvent identifiant de l'Event
	 */
	public PageEvent(final int numero, final JSONObject pageJSON, final Integer idEvent) {
		this.numero = numero;
		
		//conditions de déclenchement de la page
		final ArrayList<Condition> conditions = new ArrayList<Condition>();
		try {
			InterpreteurDeJson.recupererLesConditions(conditions, pageJSON.getJSONArray("conditions"));
		} catch (JSONException e2) {
			//pas de Conditions de déclenchement pour cette Page
		}
		this.conditions = conditions;
		
		//commandes de la page
		final ArrayList<Commande> commandes = new ArrayList<Commande>();
		try {
			InterpreteurDeJson.recupererLesCommandesEvent(commandes, pageJSON.getJSONArray("commandes"), this);
		} catch (JSONException e2) {
			//pas de Commandes Event pour cette Page
		}
		this.commandes = commandes;
		
		
		//apparence de l'event lors de cette page
		try {
			this.nomImage = (String) pageJSON.get("image");
		} catch (JSONException e) {
			this.nomImage = "";
		}
		try {
			this.directionInitiale = (int) pageJSON.get("directionInitiale");
		} catch (JSONException e) {
			this.directionInitiale = Event.Direction.BAS;
		}
		try {
			this.animationInitiale = (int) pageJSON.get("animationInitiale");
		} catch (JSONException e) {
			this.animationInitiale = 0;
		}
		
		
		//propriétés de l'event lors de cette page
		try {
			this.animeALArret = (boolean) pageJSON.get("animeALArret");
		} catch (JSONException e) {
			this.animeALArret = Event.ANIME_A_L_ARRET_PAR_DEFAUT;
		}
		try {
			this.animeEnMouvement = (boolean) pageJSON.get("animeEnMouvement");
		} catch (JSONException e) {
			this.animeEnMouvement = Event.ANIME_EN_MOUVEMENT_PAR_DEFAUT;
		}
		try {
			this.traversable = (boolean) pageJSON.get("traversable");
		} catch (JSONException e) {
			this.traversable = Event.TRAVERSABLE_PAR_DEFAUT;
		}
		try {
			this.directionFixe = (boolean) pageJSON.get("directionFixe");
		} catch (JSONException e) {
			this.directionFixe = Event.DIRECTION_FIXE_PAR_DEFAUT;
		}
		try {
			this.auDessusDeTout = (boolean) pageJSON.get("auDessusDeTout");
		} catch (JSONException e) {
			this.auDessusDeTout = Event.AU_DESSUS_DE_TOUT_PAR_DEFAUT;
		}
		
		try {
			this.vitesse = (int) pageJSON.get("vitesse");
		} catch (JSONException e) {
			this.vitesse = Event.VITESSE_PAR_DEFAUT;
		}
		try {
			this.frequence = (int) pageJSON.get("frequence");
		} catch (JSONException e) {
			this.frequence = Event.FREQUENCE_PAR_DEFAUT;
		}
		
		//mouvement de l'event lors de cette page
		try {
			this.deplacementNaturel = (Deplacement) InterpreteurDeJson.recupererUneCommande(pageJSON.getJSONObject("deplacement"));
			this.deplacementNaturel.page = this; //on apprend au Déplacement quelle est sa Page
			this.deplacementNaturel.naturel = true; //pour le distinguer des Déplacements forcés
		} catch (Exception e) {
			//pas de déplacement pour cette Page
			this.deplacementNaturel = null;
		}
		
		//ouverture de l'image d'apparence
		try {
			this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Characters\\"+nomImage));
			this.estPetit = (this.image.getHeight()/Event.NOMBRE_DE_VIGNETTES_PAR_IMAGE) <= Fenetre.TAILLE_D_UN_CARREAU;
		} catch (IOException e) {
			//l'image d'apparence n'existe pas
			this.estPetit = true;
			//e.printStackTrace();
		}
		//on précise si c'est une Page qui s'ouvre en parlant à l'Event
		if (conditions!=null) {
			for (Condition cond : conditions) { 
				if (cond instanceof ConditionParler) {
					this.sOuvreParParole = true;
					return; //s'arrêter dès que true trouvé
				}
			}
		}
	}

	/**
	 * Executer la Page de comportement.
	 * C'est-à-dire que les conditions de déclenchement ont été réunies.
	 * On va donc lire les commandes une par une avec un curseur.
	 */
	public final void executer() {
		//si la page est une page "Parler", elle active le stopEvent qui fige tous les events
		if (sOuvreParParole) {
			this.event.map.lecteur.stopEvent = true;
			this.event.map.lecteur.eventQuiALanceStopEvent = this.event;
		}
		//lecture des commandes event
		if (commandes!=null) {
			try {
				if (curseurCommandes >= commandes.size()) {
					curseurCommandes = 0;
					if (sOuvreParParole) {
						this.event.map.lecteur.stopEvent = false; //on désactive le stopEvent si fin de la page
					}
				}
				boolean onAvanceDansLesCommandes = true;
				while (onAvanceDansLesCommandes) {
					final int ancienCurseur = curseurCommandes;
					final Commande commande = this.commandes.get(curseurCommandes);
					commande.page = this; //on apprend à la Commande depuis quelle Page elle est appelée
					curseurCommandes = commande.executer(curseurCommandes, commandes);
					if (curseurCommandes==ancienCurseur) { 
						//le curseur n'a pas changé, c'est donc une commande qui prend du temps
						onAvanceDansLesCommandes = false;
					}
				}
			} catch (IndexOutOfBoundsException e) {
				//on a fini la page
				curseurCommandes = 0;
				if (sOuvreParParole) {
					this.event.map.lecteur.stopEvent = false; //on désactive le stopEvent si fin de la page
				}
				this.event.pageActive = null; 
			}
		}
	}
	
}
