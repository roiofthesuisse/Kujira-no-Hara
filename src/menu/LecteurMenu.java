package menu;

import java.awt.image.BufferedImage;

import commandes.RevenirAuJeu;
import conditions.Condition;
import main.Fenetre;
import main.Lecteur;
import map.Event;
import map.LecteurMap;
import utilitaire.Graphismes;
import utilitaire.GestionClavier.ToucheRole;

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
	 * @return écran
	 */
	public final BufferedImage calculerAffichage() {
		BufferedImage ecran = ecranNoir();
		
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
	public final void keyPressed(ToucheRole touchePressee) {
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
				allerAuMenuParentOuRevenirAuJeu();
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
	 * Changer de Menu pour aller au Menu parent ou bien revenir au jeu.
	 */
	public final void allerAuMenuParentOuRevenirAuJeu() {
		if (this.menu.menuParent!=null) {
			//revenir au Menu parent
			new LecteurMenu(this.fenetre, this.menu.menuParent, this.lecteurMapMemorise).changerMenu();
		} else if (this.lecteurMapMemorise!=null) {
			//revenir au jeu
			new RevenirAuJeu(this).executer();
		} else {
			this.fenetre.dispose();
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
	public void keyReleased(ToucheRole toucheRelachee) {
		// TODO Auto-generated method stub
		
	}
	
}
