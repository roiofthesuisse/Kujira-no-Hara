package map;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.imageio.ImageIO;

public class Tileset {
	public String nom;
	public BufferedImage image;
	public boolean[] passabilite;
	int[] altitude; //0:sol 1:objetSurSol 2:objetSurObjetSurSol 3:heros 4:objetSurHeros 5:ObjetSurObjetSurHeros
	
	public Tileset(String nomTileset) {
		this.nom = nomTileset;
		try {
			this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Tilesets\\"+nomTileset));
			int tailleTileset = (image.getHeight()/32) * 8;
			
			this.passabilite = new boolean[tailleTileset];
			//par défaut les passabilités sont vraie pour la case 0, faux pour les autres
			passabilite[0] = true;
			//passabilités obtenues par le fichier csv du tileset
			BufferedReader buff;
			try {
				buff = new BufferedReader(new FileReader(".\\ressources\\Data\\Tilesets\\"+nomTileset+"\\passabilite.csv"));
				String ligne;
				if((ligne = buff.readLine()) != null) {
					String[] listePassabilite = ligne.split(";",-1); //le -1 de split signifie qu'il gère les cases vides
					for(int i=0; i<listePassabilite.length; i++){
						passabilite[i] = listePassabilite[i].equals("0");
					}
				}
				buff.close();
			} catch (Exception e) {
				System.out.println("Erreur lors de l'ouverture de la couche 0 de la map :");
				e.printStackTrace();
			}
			
			//TODO extraire les altitudes d'un fichier csv
			this.altitude = new int[tailleTileset];
			for(int i=0; i<tailleTileset; i++){
				altitude[i] = 0;
			}
		} catch (Exception e) {
			System.out.println("Erreur lors du chargement du tileset :");
			e.printStackTrace();
		}
	}
}
