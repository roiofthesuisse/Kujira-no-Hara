package mouvements;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import commandes.Deplacement;
import map.Event;
import map.Heros;
import utilitaire.GestionClavier;
import utilitaire.GestionClavier.ToucheRole;

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
		final int vitesse = vitesse(event);
		
		// Le Héros traverse tout si la touche de triche est pressée
		if (event instanceof Heros && ToucheRole.TRICHE.enfoncee()) {
			this.deltaX += GestionClavier.ToucheRole.DROITE.enfoncee() ? vitesse : 0;
			this.deltaX += GestionClavier.ToucheRole.GAUCHE.enfoncee() ? -vitesse : 0;
			this.deltaY += GestionClavier.ToucheRole.BAS.enfoncee() ? vitesse : 0;
			this.deltaY += GestionClavier.ToucheRole.HAUT.enfoncee() ? -vitesse : 0;
			return true;
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
						final Avancer unPas = unPasVers(Event.Direction.HAUT, event, vitesse);
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
						final Avancer unPas = unPasVers(Event.Direction.BAS, event, vitesse);
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
						final Avancer unPas = unPasVers(Event.Direction.GAUCHE, event, vitesse);
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
						final Avancer unPas = unPasVers(Event.Direction.DROITE, event, vitesse);
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
						final int vitesseInertie = Math.max(1, event.pageActive.vitesse.valeur/2);
						final Avancer unPas = unPasVers(event.direction, event, vitesseInertie);
						if (unPas.mouvementPossible()) {
							ilYADeplacement = true;
							switch(event.direction) {
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

	/**
	 * Vitesse avec laquelle l'Event avance a chaque frame
	 * @param event qui doit avancer
	 * @return avancee a chaque frame
	 */
	private int vitesse(final Event event) {
		if (event instanceof Heros && !this.toucheEnfonceeALaFramePrecedente) {
			// inertie : si on vient d'appuyer sur la touche, le Héros va moins vite
			return Math.max(1, event.pageActive.vitesse.valeur/2);
		} else {
			// vitesse maximale de l'Event
			return event.pageActive.vitesse.valeur;
		}
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
		// Même si Avancer est impossible (mur...), l'Event regarde dans la direction du Mouvement
		mettreEventDansLaDirectionDuMouvement();
		
		if (this.onPeutContournerUnCoin) {
			// Contournement d'un coin
			Avancer.contournerUnCoin(event, this.realignementX, this.realignementY);
			this.onPeutContournerUnCoin = false;
			this.realignementX = 0;
			this.realignementY = 0;
			
		} else {
			
			// Le pas complet n'a pas pu etre fait
			// mais on avance jusqu'a l'obstacle (pas incomplet)
			avancerJusquALObstacle(event);

			// On arrete l'animation de marche
			event.avance = false;
			
			// L'Event n'attaque pas et ne bouge pas donc on remet sa première frame d'animation
			if (!event.avancaitALaFramePrecedente && !event.avance && !event.animeALArretActuel) {
				event.animation = 0;
			}
		}
	}

	/**
	 * Le pas complet n'etait pas possible.
	 * Mais on avance quand meme jusqu'a l'obstacle (c'est un pas incomplet).
	 * @param event a deplacer
	 */
	private void avancerJusquALObstacle(final Event event) {
		int distanceALObstacle;
		if (GestionClavier.ToucheRole.BAS.enfoncee()) {
			distanceALObstacle = calculerLaDistanceALOBstacle(event, Event.Direction.BAS);
			if (distanceALObstacle != 0) {
				event.y += distanceALObstacle;
				LOG.debug("distance a l'obstacle en bas : " + distanceALObstacle);
			}
		}
		if (GestionClavier.ToucheRole.GAUCHE.enfoncee()) {
			distanceALObstacle = calculerLaDistanceALOBstacle(event, Event.Direction.GAUCHE);
			if (distanceALObstacle != 0) {
				event.x -= distanceALObstacle;
				LOG.debug("distance a l'obstacle a gauche : " + distanceALObstacle);
			}
		}
		if (GestionClavier.ToucheRole.DROITE.enfoncee()) {
			distanceALObstacle = calculerLaDistanceALOBstacle(event, Event.Direction.DROITE);
			if (distanceALObstacle != 0) {
				event.x += distanceALObstacle;
				LOG.debug("distance a l'obstacle a droite : " + distanceALObstacle);
			}
		}
		if (GestionClavier.ToucheRole.HAUT.enfoncee()) {
			distanceALObstacle = calculerLaDistanceALOBstacle(event, Event.Direction.HAUT);
			if (distanceALObstacle != 0) {
				event.y -= distanceALObstacle;
				LOG.debug("distance a l'obstacle en haut : " + distanceALObstacle);
			}
		}
	}
	
	/**
	 * Quelle est la distance qui separe l'Event de l'obstacle en face de lui ?
	 * @param event a faire avancer jusqu'a l'obstacle
	 * @param direction dans laquelle l'event veut avancer
	 * @return distance entre l'event et l'obstacle
	 */
	private int calculerLaDistanceALOBstacle(final Event event, final int direction) {
		// Recherche par dichotomie
		int distanceALObstacle = vitesse(event);
		int nouvelleDistanceALObstacle;
		int borneSup = distanceALObstacle;
		int borneInf = 0;
		while (distanceALObstacle > 0) {
			nouvelleDistanceALObstacle = (borneInf + borneSup)/2;
			if (nouvelleDistanceALObstacle == distanceALObstacle) {
				break;
			}
			distanceALObstacle = nouvelleDistanceALObstacle;
			if (unPasVers(direction, event, distanceALObstacle).mouvementPossible()) {
				borneInf = distanceALObstacle;
			} else {
				borneSup = distanceALObstacle;
			}
		}
		return distanceALObstacle;
	}

	@Override
	public final String toString() {
		return "Avancer selon les touches du clavier";
	}

	@Override
	public final int getDirectionImposee() {
		final Event event = this.deplacement.getEventADeplacer();
		if (!event.map.lecteur.stopEvent //pas de gel des Events
			&& !(event.map.lecteur.stopHeros && event instanceof Heros) //pas de gel du Héros
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
