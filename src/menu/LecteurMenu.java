package menu;

import java.awt.Color;
import java.awt.image.BufferedImage;

import main.Commande;
import main.Fenetre;
import main.Lecteur;
import map.Event;
import map.LecteurMap;
import utilitaire.GestionClavier.ToucheRole;
import utilitaire.graphismes.Graphismes;

/**
 * Le Lecteur de Menu a pour rôle de produire l'image à afficher à l'écran s'il s'agit d'un Menu.
 */
public class LecteurMenu extends Lecteur {
	public Menu menu;
	public LecteurMap lecteurMapMemorise;
	
	/**
	 * Constructeur explicite
	 * @param fenetre dans laquelle on doit afficher le Menu
	 * @param menu que va lire ce Lecteur
	 * @param lecteurMapMemorise Lecteur de la Map sur laquelle on se trouvait avant d'entrer dans le Menu
	 * @param selectionInitiale identifiant de l'ElementDeMenu sélectionné au début
	 */
	public LecteurMenu(final Fenetre fenetre, final Menu menu, final LecteurMap lecteurMapMemorise, 
			final Integer selectionInitiale) {
		this.menu = menu;
		menu.lecteur = this; //on prévient le Lecteur qu'il a un Menu
		
		final ElementDeMenu elementSelectionneInitialement = this.menu.elements.get(selectionInitiale);
		if (elementSelectionneInitialement.selectionnable) {
			this.menu.elementSelectionne = elementSelectionneInitialement;
			elementSelectionneInitialement.selectionne = true;
		} else {
			LOG.error("Impossible de faire de l'élement de menu "+selectionInitiale+" la sélection initiale.");
		}
		
		this.fenetre = fenetre;
		this.lecteurMapMemorise = lecteurMapMemorise;
		this.allume = true; //TODO est-ce utile ?
	}
	
	/**
	 * Constituer l'image de l'écran, avec tous les éléments du Menu
	 * @param frame de l'écran calculé
	 * @return écran
	 */
	public final BufferedImage calculerAffichage(final int frame) {
		BufferedImage ecran = Graphismes.ecranColore(Color.BLACK);
		
		//lecture des CommandesMenu
		final ElementDeMenu elementConfirme = this.menu.elementSelectionne;
		if (elementConfirme != null && elementConfirme.selectionnable && elementConfirme.selectionne) {
			// on execute uniquement les Elements visibles
			if (elementConfirme.ilFautAfficherCetElement()) {
				if (elementConfirme.executionDesCommandesDeConfirmation
						&& elementConfirme.comportementConfirmation != null && elementConfirme.comportementConfirmation.size()>0) {
					// Commandes de confirmation
					elementConfirme.executerLesCommandesDeConfirmation();
				} else if (elementConfirme.executionDesCommandesDeSurvol
						&& elementConfirme.comportementSurvol != null && elementConfirme.comportementSurvol.size()>0) {
					// Commandes de survol
					elementConfirme.executerLesCommandesDeSurvol();
				}
			}
		}
		
		//image de fond
		if (this.menu.fond != null) {
			ecran = Graphismes.superposerImages(ecran, this.menu.fond, 0, 0);
		}

		//affichage de la sélection
		final ElementDeMenu selectionnable = menu.elementSelectionne;
		if (selectionnable!=null && selectionnable.selectionnable && selectionnable.selectionne) {
			final BufferedImage selection = selectionnable.creerImageDeSelection();
			ecran = Graphismes.superposerImages(ecran, selection, selectionnable.x-ImageMenu.CONTOUR, selectionnable.y-ImageMenu.CONTOUR);
		}
		
		//affichage des éléments de menu
		for (ImageMenu element : menu.images) {
			if (element.ilFautAfficherCetElement()) {
				ecran = Graphismes.superposerImages(ecran, 
						element.image, 
						element.x + element.largeur/2 - element.image.getWidth()/2, 
						element.y + element.hauteur/2 - element.image.getHeight()/2
				);
			}
		}
		
		//afficher les listes
		ImageMenu elementDeListe;
		for (@SuppressWarnings("rawtypes") Liste liste : menu.listes) {
			for (int i = 0; i<liste.elementsAffiches.length; i++) {
				for (int j = 0; j<liste.elementsAffiches[i].length; j++) {
					elementDeListe = liste.elementsAffiches[i][j];
					if (elementDeListe != null) {
					ecran = Graphismes.superposerImages(ecran, elementDeListe.image, elementDeListe.x, elementDeListe.y);
					}
				}
			}
			
		}

		//affichage des textes
		BufferedImage imgtxt;
		for (Texte texte : menu.textes) {
			imgtxt = texte.image;			
			ecran = Graphismes.superposerImages(ecran, imgtxt, texte.x, texte.y);
		}
		
		//afficher le Texte descriptif
		if (this.menu.texteDescriptif != null && !this.menu.texteDescriptif.contenu.equals("")) {
			ecran = Graphismes.superposerImages(ecran, this.menu.texteDescriptif.image, this.menu.texteDescriptif.x, this.menu.texteDescriptif.y);
		}
		
		return ecran;
	}

	@Override
	public final void keyPressed(final ToucheRole touchePressee) {
		switch(touchePressee) {
			case ACTION : 
				menu.confirmer(); 
				break;
			case HAUT : 
				menu.selectionnerElementDansLaDirection(Event.Direction.HAUT); 
				break;
			case BAS : 
				menu.selectionnerElementDansLaDirection(Event.Direction.BAS);
				break;
			case GAUCHE : 
				menu.selectionnerElementDansLaDirection(Event.Direction.GAUCHE);
				break;
			case DROITE : 
				menu.selectionnerElementDansLaDirection(Event.Direction.DROITE);
				break;
			/*case PAGE_MENU_SUIVANTE : 
				allerAuMenuSuivant();
				break;
			case PAGE_MENU_PRECEDENTE : 
				allerAuMenuPrecedent();
				break;*/
			case MENU : 
				executerLeComportementDAnnulation();
				break;
			case CAPTURE_D_ECRAN : 
				this.faireUneCaptureDEcran(); 
				break;
			default : 
				// touche inconnue
				break;
		}
	}
	
	/**
	 * Ouvrir un autre Menu.
	 * @warning cette méthode ne doit être appelée que par le nouveau Lecteur !
	 */
	public final void changerMenu() {
		Fenetre.getFenetre().futurLecteur = this;
		Fenetre.getFenetre().lecteur.allume = false;
	}
	
	/**
	 * Changer de Menu pour aller au Menu suivant.
	 * @param selectionInitiale identifiant de l'ElementDeMenu sélectionné au début
	 */
	public final void allerAuMenuSuivant(final int selectionInitiale) {
		if (this.menu.menuSuivant!=null) {
			new LecteurMenu(this.fenetre, this.menu.menuSuivant, this.lecteurMapMemorise, selectionInitiale).changerMenu();
		}
	}
	
	/**
	 * Changer de Menu pour aller au Menu précédent.
	 * @param selectionInitiale identifiant de l'ElementDeMenu sélectionné au début
	 */
	public final void allerAuMenuPrecedent(final int selectionInitiale) {
		if (this.menu.menuPrecedent!=null) {
			new LecteurMenu(this.fenetre, this.menu.menuPrecedent, this.lecteurMapMemorise, selectionInitiale).changerMenu();
		}
	}

	@Override
	public void keyReleased(final ToucheRole toucheRelachee) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected final String typeDeLecteur() {
		return "LecteurMenu";
	}
	
	/**
	 * Commandes à executer lorsqu'on annule le Menu.
	 */
	private void executerLeComportementDAnnulation() {
		int i = 0;
		for (Commande commande : this.menu.comportementAnnulation) {
			i = commande.executer(i, this.menu.comportementAnnulation);
		}
	}
	
}
