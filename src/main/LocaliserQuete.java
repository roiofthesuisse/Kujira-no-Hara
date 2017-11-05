package main;

import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import jeu.Quete;

/**
 * Lala
 */
public class LocaliserQuete {
	static String adresse = "./ressources/Graphics/Pictures/Cartes/";
	static int margeX = 0;
	static int margeY = 0;
	static int numeroQuete = 0;
	static int numeroCarte = 0;
	static BufferedImage icone;
	static JLabel label;
	/**
	 * Lala
	 */
	public static void main(final String[] args) throws IOException {
		final BufferedImage image = ImageIO.read(new File(adresse + 0 + ".png"));
		icone = ImageIO.read(new File("./ressources/Graphics/Icons/quete a faire icon.png"));
		
		final ImageIcon imageIcon = new ImageIcon(adresse + 0 + ".png");
		final JFrame frame = new JFrame();
		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(final KeyEvent ke) {
				numeroCarte = ke.getKeyCode() - 96;
				if (numeroCarte>=0 && numeroCarte<=9) {
					final String nomImage = adresse + numeroCarte + ".png";
					//System.out.println(nomImage);
					final ImageIcon imageIcon = new ImageIcon(nomImage);
					label.setIcon(imageIcon);
					label.repaint();
				}
				System.out.println();
			}

			@Override
			public void keyReleased(KeyEvent ke) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyTyped(KeyEvent ke) {
				// TODO Auto-generated method stub
			}
		});
		
		frame.addMouseListener(new MouseAdapter() {
					public void mousePressed(final MouseEvent me) {
						final int x = me.getX() - margeX - icone.getWidth()/2;
						final int y = me.getY() - margeY - icone.getHeight()/2;
						System.out.println("\t\t\"numeroCarte\": "+numeroCarte+",");
						System.out.println("\t\t\"xCarte\": "+x+",");
						System.out.println("\t\t\"yCarte\": "+y+",");
						System.out.println("\t\t\"bonus\": false");
						System.out.println("\t},");
						numeroQuete++;
						
						final Quete quete = Quete.quetesDuJeu[numeroQuete];
						System.out.println("\t{");
						System.out.println("\t\t\"id\": "+quete.id+",");
						System.out.println("\t\t\"nom\": [\""+quete.nom.get(0)+"\", \""+quete.nom.get(1)+"\"],");
						System.out.println("\t\t\"description\": [\""+quete.description.get(0)+"\", \""+quete.description.get(1)+"]\",");
					} 
				}
		);
		frame.setSize(image.getWidth(), image.getHeight());
		label = new JLabel(imageIcon);
		frame.add(label);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		final Insets marges = frame.getInsets();
		margeX = marges.left;
		margeY = marges.top;
		
		
		final int nombreDeQuetes = Quete.chargerLesQuetesDuJeu();
		Quete quete = Quete.quetesDuJeu[0];
		System.out.println("\t{");
		System.out.println("\t\t\"id\": 0,");
		System.out.println("\t\t\"nom\": [\""+quete.nom.get(0)+"\", \""+quete.nom.get(1)+"\"],");
		System.out.println("\t\t\"description\": [\""+quete.description.get(0)+"\", \""+quete.description.get(1)+"\"],");
	}
}
