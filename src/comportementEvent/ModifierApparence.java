package comportementEvent;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import map.Event;

public class ModifierApparence extends CommandeEvent {
	int eventId;
	String nomNouvelleImage;
	
	public ModifierApparence(String nomNouvelleImage){
		this(nomNouvelleImage, -1); //c'est l'évènement qui donne l'ordre qui change d'apparence
	}
	
	public ModifierApparence(String nomNouvelleImage, int eventId){
		this.nomNouvelleImage = nomNouvelleImage;
		this.eventId = eventId;
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		ArrayList<Event> events = this.page.event.map.events;
		if(eventId == -1){
			eventId = this.page.event.numero; //c'est l'évènement qui donne l'ordre qui change d'apparence
		}
		for(Event e : events){
			if(e.numero == eventId){
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
