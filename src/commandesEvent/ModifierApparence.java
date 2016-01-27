package commandesEvent;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import map.Event;

/**
 * Changer l'apparence d'un Event
 */
public class ModifierApparence extends CommandeEvent {
	int eventId;
	String nomNouvelleImage;
	
	/**
	 * Constructeur implicite (cet Event)
	 * @param nomNouvelleImage nom de l'image de la nouvelle apparence
	 */
	public ModifierApparence(final String nomNouvelleImage) {
		this(nomNouvelleImage, -1); //c'est l'évènement qui donne l'ordre qui change d'apparence
	}
	
	/**
	 * Constructeur explicite
	 * @param nomNouvelleImage nom de l'image de la nouvelle apparence
	 * @param eventId numéro de l'Event à modifier
	 */
	public ModifierApparence(final String nomNouvelleImage, final int eventId) {
		this.nomNouvelleImage = nomNouvelleImage;
		this.eventId = eventId;
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<CommandeEvent> commandes) {
		ArrayList<Event> events = this.page.event.map.events;
		if (eventId == -1) {
			eventId = this.page.event.numero; //c'est l'évènement qui donne l'ordre qui change d'apparence
		}
		for (Event e : events) {
			if (e.numero == eventId) {
				try {
					BufferedImage image = ImageIO.read(new File(".\\ressources\\Graphics\\Characters\\"+nomNouvelleImage));
					e.imageActuelle = image;
				} catch (IOException err) {
					System.out.println("Erreur lors de l'ouverture de l'image durant modification d'apparence d'event :");
					err.printStackTrace();
				}
			}
		}
		return curseurActuel+1;
	}

}
