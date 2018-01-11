package mouvements;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import commandes.Deplacement;
import map.Event;
import map.Heros;
import utilitaire.GestionClavier;

/**
 * Avancer selon les touches directionnelles du clavier.
 */
public class SuivreLesTouchesDirectionnelles extends Mouvement {
	protected static final Logger LOG = LogManager.getLogger(SuivreLesTouchesDirectionnelles.class);
	
	private int deltaX;
	private int deltaY;
	
	/** Si l'Event marche vers un coin, on le décale légèrement pour qu'il puisse passer */
	private boolean onPeutContournerUnCoin;
	/** Décalage de l'Event pour l'aider à franchir un coin */
	private int realignementX, realignementY;
	/** Inertie : le Héros avance moins vite lors de la première frame d'appui */
	private boolean toucheEnfonceeALaFramePrecedente;
	
	/**
	 * Constructeur vide
	 */
	public SuivreLesTouchesDirectionnelles() {
		//rien
	}

	/**
	 * Constructeur paramétrique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public SuivreLesTouchesDirectionnelles(final HashMap<String, Object> parametres) {
		this();
	}
	
	@Override
	protected final void reinitialiserSpecifique() {
		this.deltaX = 0;
		this.deltaY = 0;
	}

	@Override
	public final boolean mouvementPossible() {
		final Event event = this.deplacement.getEventADeplacer();
		if (event.pageActive == null) {
			event.activerUnePage();
		}
		
		// Inertie : si on vient d'appuyer sur la touche, le Héros va moins vite
		final int vitesse;
		if (event instanceof Heros && !this.toucheEnfonceeALaFramePrecedente) {
			vitesse = Math.max(1, event.pageActive.vitesse/2);
		} else {
			vitesse = event.pageActive.vitesse;
		}
		
		boolean ilYADeplacement = false;
		boolean toucheEnfonceeACetteFrame = false;
		if (GestionClavier.ToucheRole.HAUT.enfoncee()) {
			toucheEnfonceeACetteFrame = true;
			if (!GestionClavier.ToucheRole.BAS.enfoncee()) { //bas-haut impossible
				if (GestionClavier.ToucheRole.GAUCHE.enfoncee()) {
					if (!GestionClavier.ToucheRole.DROITE.enfoncee()) { //gauche-droite impossible
						//haut-gauche
						if (unPasVers(Event.Direction.HAUT, Event.Direction.GAUCHE, event, vitesse).mouvementPossible()) {
							ilYADeplacement = true;
							this.deltaX = -vitesse;
							this.deltaY = -vitesse;
						} else {
							//le mouvement diagonale n'est pas possible, on essaye un des deux sous-mouvements
							if (unPasVers(Event.Direction.GAUCHE, event, vitesse).mouvementPossible()) {
								ilYADeplacement = true;
								this.deltaX = -vitesse;
							} else if (unPasVers(Event.Direction.HAUT, event, vitesse).mouvementPossible()) {
								ilYADeplacement = true;
								this.deltaY = -vitesse;
							}
						}
					}
				} else {
					if (GestionClavier.ToucheRole.DROITE.enfoncee()) {
						//haut-droite
						if (unPasVers(Event.Direction.HAUT, Event.Direction.DROITE, event, vitesse).mouvementPossible()) {
							ilYADeplacement = true;
							this.deltaX = vitesse;
							this.deltaY = -vitesse;
						} else {
							//le mouvement diagonale n'est pas possible, on essaye un des deux sous-mouvements
							if (unPasVers(Event.Direction.DROITE, event, vitesse).mouvementPossible()) {
								ilYADeplacement = true;
								this.deltaX = vitesse;
							} else if (unPasVers(Event.Direction.HAUT, event, vitesse).mouvementPossible()) {
								ilYADeplacement = true;
								this.deltaY = -vitesse;
							}
						}
					} else {
						//haut seul
						Avancer unPas = unPasVers(Event.Direction.HAUT, event, vitesse);
						if (unPas.mouvementPossible()) {
							ilYADeplacement = true;
							this.deltaY = -vitesse;
							
						} else if (unPas.onPeutContournerUnCoin) {
							//contournement d'un coin
							this.onPeutContournerUnCoin = true;
							this.realignementX = unPas.realignementX;
							this.realignementY = unPas.realignementY;
						}
					}
				}
			}
		} else {
			if (GestionClavier.ToucheRole.BAS.enfoncee()) {
				toucheEnfonceeACetteFrame = true;
				if (GestionClavier.ToucheRole.GAUCHE.enfoncee()) {
					if (!GestionClavier.ToucheRole.DROITE.enfoncee()) { //gauche-droite impossible
						//bas-gauche
						if (unPasVers(Event.Direction.BAS, Event.Direction.GAUCHE, event, vitesse).mouvementPossible()) {
							ilYADeplacement = true;
							this.deltaX = -vitesse;
							this.deltaY = vitesse;
						} else {
							//le mouvement diagonale n'est pas possible, on essaye un des deux sous-mouvements
							if (unPasVers(Event.Direction.GAUCHE, event, vitesse).mouvementPossible()) {
								ilYADeplacement = true;
								this.deltaX = -vitesse;
							} else if (unPasVers(Event.Direction.BAS, event, vitesse).mouvementPossible()) {
								ilYADeplacement = true;
								this.deltaY = vitesse;
							}
						}
					}
				} else {
					if (GestionClavier.ToucheRole.DROITE.enfoncee()) {
						//bas-droite
						if (unPasVers(Event.Direction.BAS, Event.Direction.DROITE, event, vitesse).mouvementPossible()) {
							ilYADeplacement = true;
							this.deltaX = vitesse;
							this.deltaY = vitesse;
						} else {
							//le mouvement diagonale n'est pas possible, on essaye un des deux sous-mouvements
							if (unPasVers(Event.Direction.DROITE, event, vitesse).mouvementPossible()) {
								ilYADeplacement = true;
								this.deltaX = vitesse;
							} else if (unPasVers(Event.Direction.BAS, event, vitesse).mouvementPossible()) {
								ilYADeplacement = true;
								this.deltaY = vitesse;
							}
						}
					} else {
						//bas seul
						Avancer unPas = unPasVers(Event.Direction.BAS, event, vitesse);
						if (unPas.mouvementPossible()) {
							ilYADeplacement = true;
							this.deltaY = vitesse;
							
						} else if (unPas.onPeutContournerUnCoin) {
							//contournement d'un coin
							this.onPeutContournerUnCoin = true;
							this.realignementX = unPas.realignementX;
							this.realignementY = unPas.realignementY;
						}
					}
				}
			} else {
				if (GestionClavier.ToucheRole.GAUCHE.enfoncee()) {
					toucheEnfonceeACetteFrame = true;
					if (!GestionClavier.ToucheRole.DROITE.enfoncee()) { //gauche-droite impossible
						//gauche seule
						Avancer unPas = unPasVers(Event.Direction.GAUCHE, event, vitesse);
						if (unPas.mouvementPossible()) {
							ilYADeplacement = true;
							this.deltaX = -vitesse;
							
						} else if (unPas.onPeutContournerUnCoin) {
							//contournement d'un coin
							this.onPeutContournerUnCoin = true;
							this.realignementX = unPas.realignementX;
							this.realignementY = unPas.realignementY;
						}
					}
				} else {
					if (GestionClavier.ToucheRole.DROITE.enfoncee()) {
						toucheEnfonceeACetteFrame = true;
						//droite seule
						Avancer unPas = unPasVers(Event.Direction.DROITE, event, vitesse);
						if (unPas.mouvementPossible()) {
							ilYADeplacement = true;
							this.deltaX = vitesse;
							
						} else if (unPas.onPeutContournerUnCoin) {
							//contournement d'un coin
							this.onPeutContournerUnCoin = true;
							this.realignementX = unPas.realignementX;
							this.realignementY = unPas.realignementY;
						}
					} else if (this.toucheEnfonceeALaFramePrecedente && event instanceof Heros) {
						// Inertie : même si on ne presse plus les touches, le Héros avance encore un peu
						final int vitesseInertie = Math.max(1, event.pageActive.vitesse/2);
						Avancer unPas = unPasVers(event.direction, event, vitesseInertie);
						if (unPas.mouvementPossible()) {
							ilYADeplacement = true;
							switch(event.direction){
								case Event.Direction.BAS :
									this.deltaY = vitesseInertie;
									break;
								case Event.Direction.HAUT :
									this.deltaY = -vitesseInertie;
									break;
								case Event.Direction.DROITE :
									this.deltaX = vitesseInertie;
									break;
								case Event.Direction.GAUCHE :
									this.deltaX = -vitesseInertie;
									break;
							}
						} else if (unPas.onPeutContournerUnCoin) {
							LOG.error("Contournement d'un obstacle suite à un mouvement inertiel.");
							//contournement d'un coin
							this.onPeutContournerUnCoin = true;
							this.realignementX = unPas.realignementX;
							this.realignementY = unPas.realignementY;
						}
					}
				}
			}
		}
		
		this.toucheEnfonceeALaFramePrecedente = toucheEnfonceeACetteFrame;
		
		return ilYADeplacement;
	}

	@Override
	protected final void calculDuMouvement(final Event event) {
		event.y += this.deltaY;
		event.x += this.deltaX;
	}
	
	/**
	 * Créer un pas dans la direction voulue.
	 * Ce pas peut rester théorique pour permettre de calculer la possibilité (ou non) d'un Mouvement.
	 * @param dir direction du pas
	 * @param event qui doit avancer
	 * @param vitesseForcee du pas, éventuellement différente de la vitesse de l'Event
	 * @return un pas dans la direction demandée
	 */
	private Avancer unPasVers(final int dir, final Event event, final int vitesseForcee) {
		final Avancer pas = new Avancer(dir, vitesseForcee);
		pas.deplacement = new Deplacement(event.id, new ArrayList<Mouvement>(), true, false, false);
		pas.deplacement.page = event.pageActive; //on apprend au Déplacement quelle est sa Page
		return pas;
	}
	
	/**
	 * Créer un pas dans la direction diagonale voulue.
	 * Ce pas peut rester théorique pour permettre de calculer la possibilité (ou non) d'un Mouvement.
	 * @param dirVerti direction verticale du pas
	 * @param dirHori direction horizontale du pas
	 * @param event qui doit avancer
	 * @param vitesseForcee du pas, éventuellement différente de la vitesse de l'Event
	 * @return un pas dans la direction demandée
	 */
	private Mouvement unPasVers(final int dirVerti, final int dirHori, final Event event, final int vitesseForcee) {
		final Mouvement pas = new PasEnDiagonale(dirVerti, dirHori, vitesseForcee);
		pas.deplacement = new Deplacement(event.id, new ArrayList<Mouvement>(), true, false, false);
		pas.deplacement.page = event.pageActive; //on apprend au Déplacement quelle est sa Page
		return pas;
	}

	@Override
	protected final void terminerLeMouvementSpecifique(final Event event) {
		//rien
	}

	@Override
	protected final void ignorerLeMouvementSpecifique(final Event event) {
		event.avance = false;
		
		// Même si Avancer est impossible (mur...), l'Event regarde dans la direction du Mouvement
		mettreEventDansLaDirectionDuMouvement();
		
		if (this.onPeutContournerUnCoin) {
			// Contournement d'un coin
			Avancer.contournerUnCoin(event, this.realignementX, this.realignementY);
			this.onPeutContournerUnCoin = false;
			this.realignementX = 0;
			this.realignementY = 0;
			
		} else {
			// L'Event n'attaque pas et ne bouge pas donc on remet sa première frame d'animation
			if (!event.avancaitALaFramePrecedente && !event.avance && !event.animeALArretActuel) {
				event.animation = 0;
			}
		}
	}

	@Override
	public final String toString() {
		return "Avancer selon les touches du clavier";
	}

	@Override
	public final int getDirectionImposee() {
		final Event event = this.deplacement.getEventADeplacer();
		if (!event.map.lecteur.stopEvent //pas de gel des Events
				//&& event.animationAttaque <= 0 //pas en attaque
				&& (event.deplacementForce == null || event.deplacementForce.mouvements.size() <= 0) //pas de Déplacement forcé
		) {
			if (GestionClavier.ToucheRole.GAUCHE.enfoncee()) {
				event.direction = Event.Direction.GAUCHE;
			} else if (GestionClavier.ToucheRole.DROITE.enfoncee()) {
				event.direction = Event.Direction.DROITE;
			} else if (GestionClavier.ToucheRole.BAS.enfoncee()) {
				event.direction = Event.Direction.BAS;
			} else if (GestionClavier.ToucheRole.HAUT.enfoncee()) {
				event.direction = Event.Direction.HAUT;
			}
		}
		
		//aucune direction n'est imposée
		return -1;
	}

}
