package mouvements;

import java.util.ArrayList;
import java.util.HashMap;

import commandes.Deplacement;
import map.Event;
import utilitaire.GestionClavier;

/**
 * Avancer selon les touches directionnelles du clavier.
 */
public class SuivreLesTouchesDirectionnelles extends Mouvement {
	private int deltaX;
	private int deltaY;
	
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
		
		boolean ilYADeplacement = false;
		if (GestionClavier.ToucheRole.HAUT.touche.enfoncee) {
			if (!GestionClavier.ToucheRole.BAS.touche.enfoncee) { //bas-haut impossible
				if (GestionClavier.ToucheRole.GAUCHE.touche.enfoncee) {
					if (!GestionClavier.ToucheRole.DROITE.touche.enfoncee) { //gauche-droite impossible
						//haut-gauche
						if (unPasVers(Event.Direction.HAUT, Event.Direction.GAUCHE, event).mouvementPossible()) {
							ilYADeplacement = true;
							this.deltaX = -event.pageActive.vitesse;
							this.deltaY = -event.pageActive.vitesse;
						} else {
							//le mouvement diagonale n'est pas possible, on essaye un des deux sous-mouvements
							if (unPasVers(Event.Direction.GAUCHE, event).mouvementPossible()) {
								ilYADeplacement = true;
								this.deltaX = -event.pageActive.vitesse;
							} else if (unPasVers(Event.Direction.HAUT, event).mouvementPossible()) {
								ilYADeplacement = true;
								this.deltaY = -event.pageActive.vitesse;
							}
						}
					}
				} else {
					if (GestionClavier.ToucheRole.DROITE.touche.enfoncee) {
						//haut-droite
						if (unPasVers(Event.Direction.HAUT, Event.Direction.DROITE, event).mouvementPossible()) {
							ilYADeplacement = true;
							this.deltaX = event.pageActive.vitesse;
							this.deltaY = -event.pageActive.vitesse;
						} else {
							//le mouvement diagonale n'est pas possible, on essaye un des deux sous-mouvements
							if (unPasVers(Event.Direction.DROITE, event).mouvementPossible()) {
								ilYADeplacement = true;
								this.deltaX = event.pageActive.vitesse;
							} else if (unPasVers(Event.Direction.HAUT, event).mouvementPossible()) {
								ilYADeplacement = true;
								this.deltaY = -event.pageActive.vitesse;
							}
						}
					} else {
						//haut seul
						if (unPasVers(Event.Direction.HAUT, event).mouvementPossible()) {
							ilYADeplacement = true;
							this.deltaY = -event.pageActive.vitesse;
						}
					}
				}
			}
		} else {
			if (GestionClavier.ToucheRole.BAS.touche.enfoncee) {
				if (GestionClavier.ToucheRole.GAUCHE.touche.enfoncee) {
					if (!GestionClavier.ToucheRole.DROITE.touche.enfoncee) { //gauche-droite impossible
						//bas-gauche
						if (unPasVers(Event.Direction.BAS, Event.Direction.GAUCHE, event).mouvementPossible()) {
							ilYADeplacement = true;
							this.deltaX = -event.pageActive.vitesse;
							this.deltaY = event.pageActive.vitesse;
						} else {
							//le mouvement diagonale n'est pas possible, on essaye un des deux sous-mouvements
							if (unPasVers(Event.Direction.GAUCHE, event).mouvementPossible()) {
								ilYADeplacement = true;
								this.deltaX = -event.pageActive.vitesse;
							} else if (unPasVers(Event.Direction.BAS, event).mouvementPossible()) {
								ilYADeplacement = true;
								this.deltaY = event.pageActive.vitesse;
							}
						}
					}
				} else {
					if (GestionClavier.ToucheRole.DROITE.touche.enfoncee) {
						//bas-droite
						if (unPasVers(Event.Direction.BAS, Event.Direction.DROITE, event).mouvementPossible()) {
							ilYADeplacement = true;
							this.deltaX = event.pageActive.vitesse;
							this.deltaY = event.pageActive.vitesse;
						} else {
							//le mouvement diagonale n'est pas possible, on essaye un des deux sous-mouvements
							if (unPasVers(Event.Direction.DROITE, event).mouvementPossible()) {
								ilYADeplacement = true;
								this.deltaX = event.pageActive.vitesse;
							} else if (unPasVers(Event.Direction.BAS, event).mouvementPossible()) {
								ilYADeplacement = true;
								this.deltaY = event.pageActive.vitesse;
							}
						}
					} else {
						//bas seul
						if (unPasVers(Event.Direction.BAS, event).mouvementPossible()) {
							ilYADeplacement = true;
							this.deltaY = event.pageActive.vitesse;
						}
					}
				}
			} else {
				if (GestionClavier.ToucheRole.GAUCHE.touche.enfoncee) {
					if (!GestionClavier.ToucheRole.DROITE.touche.enfoncee) { //gauche-droite impossible
						//gauche seule
						if (unPasVers(Event.Direction.GAUCHE, event).mouvementPossible()) {
							ilYADeplacement = true;
							this.deltaX = -event.pageActive.vitesse;
						}
					}
				} else {
					if (GestionClavier.ToucheRole.DROITE.touche.enfoncee) {
						//droite seule
						if (unPasVers(Event.Direction.DROITE, event).mouvementPossible()) {
							ilYADeplacement = true;
							this.deltaX = event.pageActive.vitesse;
						}
					}
				}
			}
		}
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
	 * @param event 
	 * @return un pas dans la direction demandée
	 */
	private Mouvement unPasVers(final int dir, final Event event) {
		if (event.pageActive == null) {
			event.activerUnePage();
		}
		final Mouvement pas = new Avancer(dir, event.pageActive.vitesse);
		pas.deplacement = new Deplacement(event.id, new ArrayList<Mouvement>(), true, false, false);
		pas.deplacement.page = event.pageActive; //on apprend au Déplacement quelle est sa Page
		return pas;
	}
	
	/**
	 * Créer un pas dans la direction diagonale voulue.
	 * Ce pas peut rester théorique pour permettre de calculer la possibilité (ou non) d'un Mouvement.
	 * @param dirVerti direction verticale du pas
	 * @param dirHori direction horizontale du pas
	 * @param event 
	 * @return un pas dans la direction demandée
	 */
	private Mouvement unPasVers(final int dirVerti, final int dirHori, final Event event) {
		if (event.pageActive == null) {
			event.activerUnePage();
		}
		final Mouvement pas = new PasEnDiagonale(dirVerti, dirHori, event.pageActive.vitesse);
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
		
		//même si Avancer est impossible (mur...), l'Event regarde dans la direction du Mouvement
		mettreEventDansLaDirectionDuMouvement();
		
		//l'Event n'attaque pas et ne bouge pas donc on remet sa première frame d'animation
		if (!event.avancaitALaFramePrecedente && !event.avance && !event.animeALArretActuel) {
			event.animation = 0;
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
			if ( GestionClavier.ToucheRole.GAUCHE.touche.enfoncee ) {
				event.direction = Event.Direction.GAUCHE;
			} else if ( GestionClavier.ToucheRole.DROITE.touche.enfoncee ) {
				event.direction = Event.Direction.DROITE;
			} else if ( GestionClavier.ToucheRole.BAS.touche.enfoncee ) {
				event.direction = Event.Direction.BAS;
			} else if ( GestionClavier.ToucheRole.HAUT.touche.enfoncee ) {
				event.direction = Event.Direction.HAUT;
			}
		}
		
		//aucune direction n'est imposée
		return -1;
	}

}
