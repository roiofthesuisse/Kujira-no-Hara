package comportementEvent;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main.GestionClavier;
import map.LecteurMap;
import menu.Parametre;
import menu.Texte;

public class Message extends CommandeEvent{
	public String texte;
	public BufferedImage image;
	public Boolean leRelachementDeToucheAEuLieu = false;
	
	public Message(String texte) {
		this.texte = texte;
	}
	
	public Message(ArrayList<Parametre> parametres){
		this( (String) trouverParametre("texte",parametres) );
	}

	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		LecteurMap lecteur = this.page.event.map.lecteur;
		lecteur.normaliserApparenceDuHerosAvantMessage();
		//si le message à afficher est différent du message affiché, on change !
		if( lecteur.messageActuel==null || !lecteur.messageActuel.equals(texte) ){
			lecteur.messageActuel = this;
			try {
				BufferedImage imageMessage = ImageIO.read(new File(".\\ressources\\Graphics\\Pictures\\parchotexte.png"));
				Texte t = new Texte(texte);
				imageMessage = lecteur.superposerImages(imageMessage, t.texteToImage(), 24, 24);
				this.image = imageMessage;
				//lecteur.stopEvent = true; //TODO à enlever, gestion via la condition parler
			} catch (IOException e) {
				System.out.println("impossible d'ouvrir l'image");
				e.printStackTrace();
			}
			
		}
		//si la touche action est relachée, la prochaine fois qu'elle sera pressé sera une nouvelle input
		if( !lecteur.fenetre.touchesPressees.contains(GestionClavier.ToucheRole.ACTION) ){
			leRelachementDeToucheAEuLieu = true;
		}
		//et cette nouvelle input servira à fermer le message
		if(leRelachementDeToucheAEuLieu && lecteur.fenetre.touchesPressees.contains(GestionClavier.ToucheRole.ACTION)){
			//on ferme le message
			lecteur.messageActuel = null;
			//lecteur.stopEvent = false; //TODO à enlever, gestion via la condition parler
			leRelachementDeToucheAEuLieu = false;
			return curseurActuel+1;
		}else{
			//on laisse le message ouvert
			return curseurActuel;
		}
	}

}
