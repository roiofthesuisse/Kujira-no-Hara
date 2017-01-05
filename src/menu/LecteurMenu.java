package menu;

import java.awt.image.BufferedImage;

import conditions.Condition;
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
	 */
	public LecteurMenu(final Fenetre fenetre, final Menu menu, final LecteurMap lecteurMapMemorise) {
		this.menu = menu;
		menu.lecteur = this; //on prévient le Lecteur qu'il a un Menu
		
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
		BufferedImage ecran = Graphismes.ecranNoir();
		
		//lecture des CommandesMenu
		final ElementDeMenu elementConfirme = this.menu.elementSelectionne;
		if (elementConfirme != null && elementConfirme.selectionnable && elementConfirme.selectionne) {
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
		
		//image de fond
		if (this.menu.fond != null) {
			ecran = Graphismes.superposerImages(ecran, this.menu.fond, 0, 0);
		}

		//affichage de la sélection
		final ElementDeMenu selectionnable = menu.elementSelectionne;
		if (selectionnable!=null && selectionnable.selectionnable && selectionnable.selectionne) {
			final BufferedImage selection = selectionnable.creerImageDeSelection();
			ecran = Graphismes.superposerImages(ecran, selection, selectionnable.x-Image.CONTOUR, selectionnable.y-Image.CONTOUR);
		}
		
		//affichage des éléments de menu
		for (Image element : menu.images) {
			if (ilFautAfficherLElement(element)) {
				ecran = Graphismes.superposerImages(ecran, element.image, element.x, element.y);
			}
		}

		//affichage des textes
		for (Texte texte : menu.textes) {
			final BufferedImage imgtxt = texte.image;			
			ecran = Graphismes.superposerImages(ecran, imgtxt, texte.x, texte.y);
		}
		
		//afficher le Texte descriptif
		if (this.menu.texteDescriptif != null && !this.menu.texteDescriptif.contenu.equals("")) {
			ecran = Graphismes.superposerImages(ecran, this.menu.texteDescriptif.image, this.menu.texteDescriptif.x, this.menu.texteDescriptif.y);
		}
		
		//afficher les listes
		//TODO
		
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
			case PAGE_MENU_SUIVANTE : 
				allerAuMenuSuivant();
				break;
			case PAGE_MENU_PRECEDENTE : 
				allerAuMenuPrecedent();
				break;
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
	 */
	public final void allerAuMenuSuivant() {
		if (this.menu.menuSuivant!=null) {
			new LecteurMenu(this.fenetre, this.menu.menuSuivant, this.lecteurMapMemorise).changerMenu();
		}
	}
	
	/**
	 * Changer de Menu pour aller au Menu précédent.
	 */
	public final void allerAuMenuPrecedent() {
		if (this.menu.menuPrecedent!=null) {
			new LecteurMenu(this.fenetre, this.menu.menuPrecedent, this.lecteurMapMemorise).changerMenu();
		}
	}
	
	/**
	 * Faut-il afficher l'Element ? Ses Conditions sont-elles toutes vérifiées ?
	 * @param element à examiner
	 * @return true s'il faut afficher l'Element, false sinon
	 */
	private boolean ilFautAfficherLElement(final Image element) {
		if (element.conditions==null || element.conditions.size()<=0) {
			//pas de contrainte particulière sur l'affichage
			return true;
		}
		
		//on essaye toutes les Conditions
		for (Condition condition : element.conditions) {
			if (!condition.estVerifiee()) {
				return false;
			}
		}
		return true;
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
