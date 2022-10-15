package menu;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import main.Commande;
import main.Lecteur;
import main.Main;
import map.Event;
import map.LecteurMap;
import utilitaire.GestionClavier.ToucheRole;
import utilitaire.graphismes.Graphismes;
import utilitaire.son.LecteurAudio;

/**
 * Le Lecteur de Menu a pour role de produire l'image a afficher a l'ecran s'il s'agit d'un Menu.
 */
public class LecteurMenu extends Lecteur {
	public Menu menu;
	public LecteurMap lecteurMapMemorise;
	
	/**
	 * Constructeur explicite
	 * @param menu que va lire ce Lecteur
	 * @param lecteurMapMemorise Lecteur de la Map sur laquelle on se trouvait avant d'entrer dans le Menu
	 * @param selectionInitiale identifiant de l'ElementDeMenu s�lectionn� au d�but impos� par ce changement de Menu
	 */
	public LecteurMenu(final Menu menu, final LecteurMap lecteurMapMemorise, 
			final Integer selectionInitiale) {
		this.menu = menu;
		menu.lecteur = this; //on pr�vient le Lecteur qu'il a un Menu
		
		// Le changement de Menu impose peut-etre sa propre s�lection initiale
		if (selectionInitiale != null) {
			final ElementDeMenu elementSelectionneInitialement = this.menu.elements.get(selectionInitiale);
			if (elementSelectionneInitialement.selectionnable) {
				this.menu.elementSelectionne = elementSelectionneInitialement;
				elementSelectionneInitialement.selectionne = true;
			} else {
				LOG.error("Impossible de faire de l'�lement de menu "+selectionInitiale+" la s�lection initiale.");
			}
		}
		
		this.lecteurMapMemorise = lecteurMapMemorise;
		this.allume = true; //TODO est-ce utile ?
	}
	
	/**
	 * Constituer l'image de l'ecran, avec tous les �l�ments du Menu
	 * @param frame de l'ecran calcul�
	 * @return ecran
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

		//affichage de la s�lection
		final ElementDeMenu selectionnable = this.menu.elementSelectionne;
		if (selectionnable!=null && selectionnable.selectionnable && selectionnable.selectionne) {
			final BufferedImage selection = selectionnable.creerImageDeSelection(null, null);
			ecran = Graphismes.superposerImages(ecran, selection, selectionnable.x-ImageMenu.CONTOUR, selectionnable.y-ImageMenu.CONTOUR);
		}
		
		//affichage des �l�ments de menu
		for (ImageMenu element : this.menu.images) {
			if (element.ilFautAfficherCetElement()) {
				ecran = Graphismes.superposerImages(ecran, 
						element.getImage(), 
						element.x + element.largeur/2 - element.getImage().getWidth()/2, 
						element.y + element.hauteur/2 - element.getImage().getHeight()/2
				);
			}
		}
		
		//afficher les listes
		ImageMenu elementDeListe;
		for (@SuppressWarnings("rawtypes") Liste liste : this.menu.listes) {
			for (int i = 0; i<liste.elementsAffiches.length; i++) {
				for (int j = 0; j<liste.elementsAffiches[i].length; j++) {
					elementDeListe = liste.elementsAffiches[i][j];
					if (elementDeListe != null) {
					ecran = Graphismes.superposerImages(ecran, elementDeListe.getImage(), elementDeListe.x, elementDeListe.y);
					}
				}
			}
			
		}
		
		//affichage de la carte
		if (this.menu.carte != null) {
			final Carte carte = this.menu.carte;			
			ecran = Graphismes.superposerImages(ecran, carte.getImage(), carte.x, carte.y);
		}

		//affichage des textes
		BufferedImage imgtxt;
		for (Texte texte : this.menu.textes) {
			imgtxt = texte.getImage();			
			ecran = Graphismes.superposerImages(ecran, imgtxt, texte.x, texte.y);
		}
		
		//afficher le Texte descriptif
		if (this.menu.texteDescriptif != null 
				&& !CollectionUtils.isEmpty(this.menu.texteDescriptif.contenu) 
				&& !StringUtils.isEmpty(this.menu.texteDescriptif.contenu.get(0))) {
			ecran = Graphismes.superposerImages(ecran, this.menu.texteDescriptif.getImage(), this.menu.texteDescriptif.x, this.menu.texteDescriptif.y);
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
	 * @warning cette Methode ne doit etre appel�e que par le nouveau Lecteur !
	 */
	public final void changerMenu() {
		Main.futurLecteur = this;
		Main.lecteur.allume = false;
	}
	
	/**
	 * Changer de Menu pour aller au Menu suivant.
	 * @param selectionInitiale identifiant de l'ElementDeMenu s�lectionn� au d�but
	 */
	public final void allerAuMenuSuivant(final int selectionInitiale) {
		if (this.menu.menuSuivant!=null) {
			new LecteurMenu(this.menu.menuSuivant, this.lecteurMapMemorise, selectionInitiale).changerMenu();
		}
	}
	
	/**
	 * Changer de Menu pour aller au Menu pr�c�dent.
	 * @param selectionInitiale identifiant de l'ElementDeMenu s�lectionn� au d�but
	 */
	public final void allerAuMenuPrecedent(final int selectionInitiale) {
		if (this.menu.menuPrecedent!=null) {
			new LecteurMenu(this.menu.menuPrecedent, this.lecteurMapMemorise, selectionInitiale).changerMenu();
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
	 * Commandes a executer lorsqu'on annule le Menu.
	 */
	private void executerLeComportementDAnnulation() {
		int i = 0;
		for (Commande commande : this.menu.comportementAnnulation) {
			i = commande.executer(i, this.menu.comportementAnnulation);
		}
	}

	@Override
	public final void lireMusique() {
		if (((LecteurMenu) this).menu==null) {
			LOG.error("Le menu est null pour le lecteur");
			return;
		}
		final Menu menu = ((LecteurMenu) this).menu;
		if (menu.nomBGM != null && !menu.nomBGM.isEmpty()) {
			LecteurAudio.playBgm(menu.nomBGM, menu.volumeBGM, 0);
		}
	}
	
}
