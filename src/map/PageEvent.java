package map;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import commandes.Deplacement;
import commandes.Message;
import conditions.Condition;
import conditions.ConditionArriveeAuContact;
import conditions.ConditionParler;
import main.Commande;
import main.Main;
import mouvements.Mouvement;
import mouvements.SeRapprocher;
import utilitaire.graphismes.Graphismes;
import utilitaire.graphismes.ModeDeFusion;

/**
 * Un Event peut avoir plusieurs comportements. Chaque comportement est d�crit par une Page de comportements.
 * La Page est d�clench�e si certaines Conditions sont v�rifi�es, ses Commandes sont alors execut�es.
 */
public class PageEvent {
	private static final Logger LOG = LogManager.getLogger(PageEvent.class);
	
	/** Event auquel appartient la Page */
	public Event event;
	/** numero de la Page */
	public final int numero;
	
	/** Conditions de d�clenchement de la Page */
	public final ArrayList<Condition> conditions;
	
	/** liste de Commandes a executer dans l'ordre si les Conditions sont v�rifi�es */
	public final ArrayList<Commande> commandes;
	/**
	 * Le curseur indique quelle Commande executer.
	 * Il se d�place incr�mentalement, mais on peut lui faire faire des sauts.
	 */
	public int curseurCommandes = 0;
	
	//apparence
	public String nomImage;
	public BufferedImage image;
	public boolean apparenceEstUnTile;
	/** par d�faut, si image < 32px, l'Event est consid�r� comme plat (au sol) */
	public Boolean plat; //
	public int directionInitiale;
	public int animationInitiale;

	//param�tres
	public Vitesse vitesse;
	public Frequence frequence;
	
	/** 
	 * <p>figer les autres Events pendant la lecture de cette Page</p>
	 * <p>automatiquement mis a true si la Page contient une condition Parler</p>
	 */
	private boolean figerLesAutresEvents;
	private boolean figerLeHeros;
	public boolean animeALArret;
	public boolean animeEnMouvement;
	public Passabilite traversable;
	public boolean traversableParLeHeros;
	public boolean directionFixe;
	public boolean auDessusDeTout;
	public int opacite;
	public ModeDeFusion modeDeFusion;
	
	//mouvement
	public Deplacement deplacementNaturel = null;
	/** Lorsqu'on parle a cet Event, faut-il se rapprocher ? */
	private boolean seRapprocherDeSonInterlocuteur = false;
	
	/**
	 * Constructeur g�n�rique
	 * La page de comportement est cr��e a partir du fichier JSON.
	 * @param numero de la Page
	 * @param pageJSON objet JSON d�crivant la page de comportements
	 * @param idEvent identifiant de l'Event
	 * @param map de l'Event
	 */
	public PageEvent(final int numero, final JSONObject pageJSON, final Integer idEvent, final Map map) {
		this.numero = numero;
		
		// Conditions de d�clenchement de la Page
		this.conditions = new ArrayList<Condition>();
		if (pageJSON.has("conditions")) {
			Condition.recupererLesConditions(this.conditions, pageJSON.getJSONArray("conditions"));
			if (this.conditions.size() > 0) {
				//on apprend aux Conditions qui est leur Page
				for (Condition condition : this.conditions) {
					condition.page = this;
				}
			} else {
				LOG.trace("Liste de conditions vide pour la page "+this.numero+" de l'event "+idEvent);
			}
		} else {
			LOG.trace("Pas de conditions pour la page "+this.numero+" de l'event "+idEvent);
		}
		
		// Commandes de la Page
		this.commandes = new ArrayList<Commande>();
		if (pageJSON.has("commandes")) {
			Commande.recupererLesCommandesEvent(this.commandes, pageJSON.getJSONArray("commandes"));
			if (this.commandes.size() > 0) {
				//on apprend aux Commandes qui est leur Page
				for (Commande commande : this.commandes) {
					commande.page = this;
				}

				// Si besoin, on se met bien en face de son interlocuteur
				final boolean laPageContientUnMessage = this.contientUneCommandeMessage();
				if (this.contientUneConditionParler() && laPageContientUnMessage) {
					final boolean leRapprochementEstDejaMis = this.seRapprocherDeSonInterlocuteur;
					if (!leRapprochementEstDejaMis) {
						final ArrayList<Mouvement> mouvements = new ArrayList<>();
						mouvements.add(new SeRapprocher(0, idEvent));
						final Commande deplacement = new Deplacement(0, mouvements, true, false, true);
						this.commandes.add(0, deplacement);
						this.seRapprocherDeSonInterlocuteur = true;
					}
				}
			} else {
				LOG.trace("Liste de commandes vide pour la page "+this.numero+" de l'event "+idEvent);
			}
		} else {
			LOG.trace("Pas de commandes pour la page "+this.numero+" de l'event "+idEvent);
		}
		
		// Apparence de l'Event lors de cette Page
		this.directionInitiale = pageJSON.has("directionInitiale") 
				? pageJSON.getInt("directionInitiale") 
				: Event.DIRECTION_PAR_DEFAUT;
		this.animationInitiale = pageJSON.has("animationInitiale") 
				? pageJSON.getInt("animationInitiale") 
				: 0;
		
		Integer tileDeLApparence = null;
		if (pageJSON.has("image")) {
			// l'Event a une apparence
			try {
				tileDeLApparence = pageJSON.getInt("image");
				// l'apparence est un Tile
				this.nomImage = "" + tileDeLApparence;
				this.image = map.tileset.carreaux[tileDeLApparence];
				this.apparenceEstUnTile = true;
			} catch (JSONException e) {
				// l'apparence est une Image
				this.nomImage = pageJSON.getString("image");
				try {
					//ouverture de l'image d'apparence
					this.image = Graphismes.ouvrirImage("Characters", nomImage);
				} catch (IOException e1) {
					//l'image d'apparence n'existe pas
					LOG.error("L'image d'apparence \""+nomImage+"\" de l'event "+idEvent+" n'existe pas !", e1);
					this.image = null;
				}
			}
		} else {
			// pas d'image
			this.image = null;
			LOG.trace("Pas d'image d'apparence pour la page "+this.numero+" de l'event "+idEvent);
		}
	
		// Propri�t�s de cette Page
		try {
			this.frequence = pageJSON.has("frequence") 
					? Frequence.parNom(pageJSON.getString("frequence"))
					: Event.FREQUENCE_PAR_DEFAUT;
		} catch (Exception e) {
			LOG.error("La fr�quence de l'�vent "+idEvent+" n'est pas une chaine de caract�res : "
					+pageJSON.toString());
			this.frequence = Event.FREQUENCE_PAR_DEFAUT;
		}
		if (this.frequence == null) {
			LOG.error("La fr�quence de l'event "+idEvent+" est nulle ! Cela va cr�er une erreur lors de l'animation !");
		}
		try {
			this.vitesse = pageJSON.has("vitesse")
					? Vitesse.parNom(pageJSON.getString("vitesse"))
					: Event.VITESSE_PAR_DEFAUT;
		} catch (Exception e) {
			LOG.error("La vitesse de l'event "+idEvent+" n'est pas une chaine de caract�res : "
					+pageJSON.toString());
			this.vitesse = Event.VITESSE_PAR_DEFAUT;
		}

		// Est-ce que cette Page fige les autres Events qu'elle ?
		this.figerLesAutresEvents = cettePageFigeLesAutresEvents(pageJSON);
		// Est-ce que cette page fige le Mouvement naturel du H�ros ?
		this.figerLeHeros = cettePageFigeLeHeros();

		this.animeALArret = pageJSON.has("animeALArret") 
				? pageJSON.getBoolean("animeALArret") 
				: Event.ANIME_A_L_ARRET_PAR_DEFAUT;
		this.animeEnMouvement = pageJSON.has("animeEnMouvement") 
				? pageJSON.getBoolean("animeEnMouvement") 
				: Event.ANIME_EN_MOUVEMENT_PAR_DEFAUT;

		// Traversabilit�
		this.traversableParLeHeros = Event.TRAVERSABLE_PAR_LE_HEROS_PAR_DEFAUT;
		if (pageJSON.has("traversable")) {
				// La passibilit� est explictement sp�cifi�e
			if (pageJSON.getBoolean("traversable")) {
				// La passibilit� sp�cifi�e est traversable
				this.traversable = Passabilite.PASSABLE;
			} else {
				// La passabilit� sp�cifi�e est solide
				// Mais elle d�pend aussi de l'apparence
				if (tileDeLApparence != null) {
					// Apparence de type "tile"
					// Comme l'Event n'est pas marqu� explicitement traversable, le tile impose sa passabilit� 
					this.traversable = map.tileset.passabiliteDeLaCase(tileDeLApparence);
				} else if (this.image == null) {
					// Pas d'apparence
					this.traversable = Passabilite.OBSTACLE; // obstacle pour la plupart des Events
					this.traversableParLeHeros = true; // mais pas le H�ros
				} else {
					// Apparence de type "character"
					this.traversable = Passabilite.OBSTACLE;
				} 
			}
		} else {
			// La passibilit� n'est pas explicitement sp�cifi�e
			if (tileDeLApparence != null) {
				// Apparence de type "tile"
				// Comme l'Event n'est pas marqu� explicitement traversable, le tile impose sa passabilit� 
				this.traversable = map.tileset.passabiliteDeLaCase(tileDeLApparence);
			} else if (this.image == null) {
				// Pas d'apparence
				this.traversable = Passabilite.OBSTACLE; // obstacle pour la plupart des Events
				this.traversableParLeHeros = true; // mais pas le H�ros
			} else {
				// Apparence de type "character"
				this.traversable = Passabilite.OBSTACLE;
			}
		}

		this.directionFixe = pageJSON.has("directionFixe")
				? pageJSON.getBoolean("directionFixe")
				: Event.DIRECTION_FIXE_PAR_DEFAUT;
		this.auDessusDeTout = pageJSON.has("auDessusDeTout")
				? pageJSON.getBoolean("auDessusDeTout")
				: Event.AU_DESSUS_DE_TOUT_PAR_DEFAUT;
		if (pageJSON.has("plat")) {
			this.plat = pageJSON.getBoolean("plat");
		} else if (this.image != null) {
			//si non pr�cis�, est d�termin� en fonction de la taille de l'image
			this.plat = (this.image.getHeight()/Event.NOMBRE_DE_VIGNETTES_PAR_IMAGE) <= Main.TAILLE_D_UN_CARREAU;
		} else {
			// pas d'image d'apparence
			this.plat = true;
		}
		this.modeDeFusion = pageJSON.has("modeDeFusion")
				? ModeDeFusion.parNom(pageJSON.get("modeDeFusion"))
				: ModeDeFusion.NORMAL;
		this.opacite = pageJSON.has("opacite")
				? pageJSON.getInt("opacite")
				: Graphismes.OPACITE_MAXIMALE;
		
		// Mouvement de l'Event lors de cette Page
		try {
			if (pageJSON.has("deplacement")) {
				this.deplacementNaturel = (Deplacement) Commande.recupererUneCommande(pageJSON.getJSONObject("deplacement"));
				this.deplacementNaturel.page = this; //on apprend au D�placement quelle est sa Page
				this.deplacementNaturel.naturel = true; //pour le distinguer des D�placements forc�s
			} else {
				//pas de d�placement pour cette Page
				this.deplacementNaturel = null;
			}
		} catch (Exception e) {
			LOG.warn("Erreur lors du chargement du d�placement naturel de la page "+this.numero+" de l'event "+idEvent, e);
		}
	}
	
	/**
	 * Est-ce que l'activation de cette page fige les autres Events ?
	 * @param pageJSON objet JSON d�crivant la page de comportements
	 * @return true si la Page fige les autres Events
	 */
	private boolean cettePageFigeLesAutresEvents(JSONObject pageJSON) {
		if (contientUneConditionParler()) {
			return true;
		} else if (pageJSON.has("figerLesAutresEvents")) {
			return pageJSON.getBoolean("figerLesAutresEvents");
		} else {
			return false;
		}
	}
	
	/**
	 * La PageEvent contient-elle une ConditionParler ?
	 * Auquel cas, cette Page fige les autres Events de la Map pendant son execution.
	 * @return pr�sence d'une ConditionParler
	 */
	private boolean contientUneConditionParler() {
		if (this.conditions != null) {
			for (Condition cond : this.conditions) {
				if (cond instanceof ConditionParler) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * L'activation de cette Page fige-t-elle le Heros dans son mouvement naturel ?
	 * @return true si contient une condition ArriveeAuContact
	 */
	private boolean cettePageFigeLeHeros() {
		if (this.conditions != null) {
			for (Condition cond : this.conditions) {
				if (cond instanceof ConditionArriveeAuContact) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * La PageEvent contient-elle une Commande Message ?
	 * @return pr�sence d'une Commande Message
	 */
	private boolean contientUneCommandeMessage() {
		for (Commande commande : this.commandes) {
			if (commande instanceof Message) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Executer la Page de comportement.
	 * C'est-�-dire que les conditions de d�clenchement ont �t� r�unies.
	 * On va donc lire les commandes une par une avec un curseur.
	 */
	public void executer() {
		//figer les autres Events ?
		//si la page est par exemple une page "Parler", elle active le stopEvent qui fige tous les Events
		if (this.figerLesAutresEvents) {
			this.event.map.lecteur.stopEvent = true;
			this.event.map.lecteur.eventQuiALanceStopEvent = this.event;
		}
		//si la page est une page ArriveeAuContact, elle bloque les Mouvements naturels du h�ros
		if (this.figerLeHeros) {
			this.event.map.lecteur.stopHeros = true;
			this.event.map.lecteur.eventQuiALanceStopEvent = this.event;
		}
		
		//lecture des Commandes event
		if (commandes != null) {
			boolean onAvanceDansLesCommandes = true;
			//on n'enchaine durant la m�me frame que les commandes instantan�es
			//si une commande longue est rencontr�e, on reporte la lecture de la Page a la frame suivante
			parcoursDesCommandes:
			while (onAvanceDansLesCommandes) {
				if (curseurCommandes < commandes.size()) {
					//il y a encore des commandes dans la liste
					final int ancienCurseur = curseurCommandes;
					final Commande commande = this.commandes.get(curseurCommandes);
					commande.page = this; //on apprend a la Commande depuis quelle Page elle est appel�e
					try {
						curseurCommandes = commande.executer(curseurCommandes, commandes);
					} catch (Exception e1) {
						LOG.error(
								(this.event != null ? "Event "+this.event.id + ", " : "")
								+ "page " + this.numero
								+ ", commande "+curseurCommandes
								+ " ("+commande.getClass().getSimpleName()
								+ ") a �chou� :", e1
						);
						curseurCommandes++;
						throw e1;
					}
					if (curseurCommandes == ancienCurseur) {
						//le curseur n'a pas chang�, c'est donc une commande qui prend du temps
						//la lecture de cette Page sera continu�e a la frame suivante
						onAvanceDansLesCommandes = false;
						// une Commande qui prend du temps fait oublier le faceset
						Message.oublierLeFaceset(commande, this.event);
					}
				} else {
					//on a fini la page
					refermerLaPage();
					break parcoursDesCommandes;
				}
			}
		}
	}
	
	/**
	 * D�sactiver la Page.
	 * Remettre le curseur des commandes a z�ro.
	 * Lib�rer les autres Events s'ils ont �t� fig�s par cette Page.
	 */
	private void refermerLaPage() {
		//a la prochaine activation de la Page, on recommencera en haut du code event
		curseurCommandes = 0;
		
		// Lib�ration des events fig�s
		//si la Page figeait les autres Events, elle doit maintenant les liberer
		if (this.figerLesAutresEvents) {
			this.event.map.lecteur.stopEvent = false; //on d�sactive le stopEvent si fin de la page
			this.event.map.lecteur.messagePrecedent = null; // plus besoin d'afficher la question d'un Choix
		}
		//si la Page figeait le Heros, elle doit maintenant le liberer
		if (this.figerLeHeros) {
			this.event.map.lecteur.stopHeros = false; //on d�sactive le stopHeros si fin de la page
		}
		
		//desactivation de la Page, ainsi une nouvelle Page sera activee a la prochaine frame
		this.event.pageActive = null;
	}
	
}
