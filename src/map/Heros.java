package map;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import commandes.Deplacement;
import main.Fenetre;
import mouvements.Avancer;
import mouvements.Mouvement;
import utilitaire.GestionClavier;
import utilitaire.InterpreteurDeJson;

/**
 * Event particulier qui est déplacé par le joueur à l'aide du clavier
 */
public class Heros extends Event {
	public static final Event MODELE = creerModele();
	
	/**
	 * L'animation d'attaque vaut 0 si le héros n'attaque pas.
	 * Au début d'une attaque, elle est mise au maximum (longueur de l'animation de l'attaque).
	 * A chaque frame, elle est affichée puis décrémentée.
	 */
	public int animationAttaque = 0;

	/**
	 * Constructeur explicite
	 * @param x position x du Héros sur la Map
	 * @param y position y du Héros sur la Map
	 * @param directionEnDebutDeMap directiondu Héros au début de la Map
	 * @throws FileNotFoundException 
	 */
	public Heros(final int x, final int y, final int directionEnDebutDeMap) throws FileNotFoundException {
		super(x, y, MODELE.nom, MODELE.id, MODELE.pages, MODELE.largeurHitbox, MODELE.hauteurHitbox);
		this.direction = directionEnDebutDeMap;
	}
	
	/**
	 * Le Héros est créé à partir d'un modèle.
	 * Ce modèle est un Event générique.
	 * @return Event modèle qui sert à la création du Héros
	 */
	private static final Event creerModele() {
		JSONObject jsonEventGenerique = null;
		try {
			jsonEventGenerique = InterpreteurDeJson.ouvrirJsonEventGenerique("Heros");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		int largeur = jsonEventGenerique.has("largeur") ? (int) jsonEventGenerique.get("largeur") : Event.LARGEUR_HITBOX_PAR_DEFAUT;
		int hauteur = jsonEventGenerique.has("hauteur") ? (int) jsonEventGenerique.get("hauteur") : Event.LARGEUR_HITBOX_PAR_DEFAUT;
		final JSONArray jsonPages = jsonEventGenerique.getJSONArray("pages");
		final ArrayList<PageEvent> pages = creerListeDesPagesViaJson(jsonPages, 0);
		
		final Event modele = new Event (0, 0, "heros", 0, pages, largeur, hauteur);
		return modele;
	}
	
	@Override
	public final void deplacer() {
		if (animationAttaque > 0) {
			//pas de déplacement si animation d'attaque
			this.animation = Fenetre.getPartieActuelle().getArmeEquipee().framesDAnimation[animationAttaque-1];
			
			animationAttaque--;
		} else if (this.deplacementForce!=null && this.deplacementForce.mouvements.size()>0) {
			//il y a un déplacement forcé
			this.deplacementForce.executerLePremierMouvement();
		} else {
			//déplacement selon les touches et les obstacles rencontrés
			boolean ilYADeplacement = false;
			if ( GestionClavier.ToucheRole.HAUT.pressee && !GestionClavier.ToucheRole.BAS.pressee ) {
				if ( unPasVers(Event.Direction.HAUT).mouvementPossible() ) {
					ilYADeplacement = true;
					this.y -= pageActive.vitesse;
				}
			}
			if ( GestionClavier.ToucheRole.BAS.pressee && !GestionClavier.ToucheRole.HAUT.pressee ) {
				if ( unPasVers(Event.Direction.BAS).mouvementPossible() ) {
					ilYADeplacement = true;
					this.y += pageActive.vitesse;
				}
			}
			if ( GestionClavier.ToucheRole.GAUCHE.pressee && !GestionClavier.ToucheRole.DROITE.pressee ) {
				if ( unPasVers(Event.Direction.GAUCHE).mouvementPossible() ) {
					ilYADeplacement = true;
					this.x -= pageActive.vitesse;
				}
			}
			if ( GestionClavier.ToucheRole.DROITE.pressee && !GestionClavier.ToucheRole.GAUCHE.pressee ) {
				if ( unPasVers(Event.Direction.DROITE).mouvementPossible() ) {
					ilYADeplacement = true;
					this.x += pageActive.vitesse;
				}
			}
			if (ilYADeplacement) {
				this.avance = true;
				//on profite du déplacement pour remettre le Héros dans la bonne direction
				this.mettreDansLaBonneDirection();
			} else {
				this.avance = false;
				//le Héros n'attaque pas et ne bouge pas donc on remet sa première frame d'animation
				this.animation = 0;
			}
		}
	}
	
	/**
	 * Créer un pas dans la direction voulue.
	 * @param dir direction du pas
	 * @return un pas dans la direction demandée
	 */
	private Mouvement unPasVers(final int dir) {
		if(this.pageActive == null){
			this.activerUnePage();
		}
		final Mouvement pas = new Avancer(dir, pageActive.vitesse);
		pas.deplacement = new Deplacement(0, new ArrayList<Mouvement>(), true, false, false);
		pas.deplacement.page = this.pageActive; //on apprend au Déplacement quelle est sa Page
		return pas;
	}
	
	/**
	 * Tourner le Héros dans la bonne Direction selon l'entrée clavier.
	 * Il faut qu'à ce moment le Héros soit libre de ses Mouvements.
	 */
	public final void mettreDansLaBonneDirection() {
		final Heros heros = map.heros;
		if (!this.map.lecteur.stopEvent //pas de gel des Events
				&& heros.animationAttaque <= 0 //pas en attaque
				&& (this.deplacementForce == null || this.deplacementForce.mouvements.size() <= 0) //pas de Déplacement forcé
		) {
			if ( GestionClavier.ToucheRole.GAUCHE.pressee ) {
				heros.direction = Event.Direction.GAUCHE;
			} else if ( GestionClavier.ToucheRole.DROITE.pressee ) {
				heros.direction = Event.Direction.DROITE;
			} else if ( GestionClavier.ToucheRole.BAS.pressee ) {
				heros.direction = Event.Direction.BAS;
			} else if ( GestionClavier.ToucheRole.HAUT.pressee ) {
				heros.direction = Event.Direction.HAUT;
			}
		}
	}
	
}
