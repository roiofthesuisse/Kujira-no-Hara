package preparateur;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import map.Event.Direction;
import map.Map;
import map.Passabilite;
import map.Tileset;
import map.Transition;
import utilitaire.InterpreteurDeJson;
import utilitaire.graphismes.Graphismes;

/**
 * Construire la carte du jeu
 */
public abstract class GenererLaCarteDuJeu {
	
	private static final int NOMBRE_DE_MAPS = 700;
	private static final int ID_PREMIER_LIEU = 5;
	private static final boolean[][][] CHIFFRES_A_DESSINER = new boolean[][][] {
		new boolean[][]{
			// 0
			new boolean[]{true, true, true},
			new boolean[]{true, false, true},
			new boolean[]{true, false, true},
			new boolean[]{true, false, true},
			new boolean[]{true, true, true},
		},
		new boolean[][]{
			// 1
			new boolean[]{false, false, true},
			new boolean[]{false, false, true},
			new boolean[]{false, false, true},
			new boolean[]{false, false, true},
			new boolean[]{false, false, true},
		},
		new boolean[][]{
			// 2
			new boolean[]{true, true, true},
			new boolean[]{false, false, true},
			new boolean[]{true, true, true},
			new boolean[]{true, false, false},
			new boolean[]{true, true, true},
		},
		new boolean[][]{
			// 3
			new boolean[]{true, true, true},
			new boolean[]{false, false, true},
			new boolean[]{false, true, true},
			new boolean[]{false, false, true},
			new boolean[]{true, true, true},
		},
		new boolean[][]{
			// 4
			new boolean[]{true, false, false},
			new boolean[]{true, false, false},
			new boolean[]{true, true, false},
			new boolean[]{true, true, true},
			new boolean[]{false, true, false},
		},
		new boolean[][]{
			// 5
			new boolean[]{true, true, true},
			new boolean[]{true, false, false},
			new boolean[]{true, true, true},
			new boolean[]{false, false, true},
			new boolean[]{true, true, true},
		},
		new boolean[][]{
			// 6
			new boolean[]{true, true, true},
			new boolean[]{true, false, false},
			new boolean[]{true, true, true},
			new boolean[]{true, false, true},
			new boolean[]{true, true, true},
		},
		new boolean[][]{
			// 7
			new boolean[]{true, true, true},
			new boolean[]{false, false, true},
			new boolean[]{false, false, true},
			new boolean[]{false, true, false},
			new boolean[]{false, true, false},
		},
		new boolean[][]{
			// 8
			new boolean[]{true, true, true},
			new boolean[]{true, false, true},
			new boolean[]{true, true, true},
			new boolean[]{true, false, true},
			new boolean[]{true, true, true},
		},
		new boolean[][]{
			// 9
			new boolean[]{true, true, true},
			new boolean[]{true, false, true},
			new boolean[]{true, true, true},
			new boolean[]{false, false, true},
			new boolean[]{true, true, true},
		}
	};
	private static final List<Integer> LIEUX_GROTTES = Arrays.asList( new Integer[]{
			6/*sabre de krakatram*/, 
			24/*decente aux algues*/, 
			25/*grottes ecailles*/,
			26/*grotte geysers dose*/,
			27/*grotte premier combat*/,
			31/*labyrinthe viking*/,
			32/*grotte statues dose*/,
			33/*grotte statues dose*/,
			34/*grotte geysers dose*/,
			38/*maison mathusalem*/,
			39/*oursin cinematique*/,
			40, 41/*maison viking*/, 
			46/*maitre cle*/,
			49/*grotte allumeur reverberes*/,
			51/*allumeur reverberes*/,
			61/*grotte allumeur reverberes*/,
			69/*taverne viking*/,
			72/*mare drakar*/,
			76/*cacarbre*/,
			79/*cacarbre oree foret*/,
			81/*maison colibri*/,
			88/*maison viking*/,
			95/*cacarbre*/,
			103/*palais flivouille*/,
			124, 123/*entrees des grottes*/,
			125/*grotte intestins voilier*/,
			137/*ermite*/,
			139/*grotte statues*/,
			140/*ogre*/,
			145/*ogre*/,
			149/*auberge moldo*/,
			153/*maitre de musique*/,
			162/*donjon pomme*/,
			166/*auberge sorcieres*/,
			169/*tunnel moldo*/,
			148/*cacarbre*/,
			172/*maison fillette*/,
			173/*geographe*/,
			186/*cacarbre spirale*/,
			190/*blocs*/,
			194/*tremplins dose*/, 
			202/*tunnel moldo*/,
			204/*eglise*/,
			237/*maison tabula*/,
			244/*echelle vers la montagne*/,
			248/*vieux con*/,
			253/*maison scientifique*/,
			254/*pozzo*/,
			256/*grotte musique*/,
			259/*archibald radis*/,
			267/*grotte spirale*/,
			268/*donjon feu*/, 
			269/*aeronaute*/,
			274/*dose*/,
			278/*cacarbre moutons*/,
			285, 286/*grotte etoilee*/,
			287/*grotte moldo*/,
			296/*maison viking jaune*/,
			305/*cacarbre*/,
			315/*arbre*/,
			316/*arbre aquatique*/,
			325/*donjon grenouille*/,
			320/*grotte pozzo*/,
			335/*malediction crapo*/,
			352/*tresor des 3 blocs*/,
			353/*astronomes*/,
			356/*cacarbre marais*/,
			360/*maison colibri*/,
			365/*grotte statues foret*/,
			366/*grotte statues foret*/,
			368/*flutini*/,
			369/*donjon sablier*/,
			372/*hibu*/,
			373/*lucky*/,
			404/*tunnel maitre musique*/,
			414/*grotte oursins glace*/,
			415/*grotte ballon geyser*/,
			416, 417, 418/*grotte braises*/,
			427/*macrodendron*/,
			445/*maison peintre*/,
			454/*ballon plaines aquatiques*/,
			456/*coffre fort*/,
			460/*partition*/,
			461/*grotte trou-poisson moldo*/,
			462/*cacarbre*/,
			465/*donjon pyramide*/,
			478/*maison mineurs*/,
			480/*jean-pierre coffe*/,
			510/*mercator*/,
			516/*grotte caillouteux*/,
			593/*donjon glacier*/,
			613/*maison chasseur*/
			} );
	
	/**
	 * Point d'entree
	 * @param args inutilise
	 */
	public static void main(final String[] args) {
		// recuperation des lieux et jonctions
		final List<Lieu> lieux = recenserLesLieux();
		
		// affichage des lieux et jonctions
		for (Lieu lieu : lieux) {
			if (lieu.jonctions.size() > 0) { // on ignore les lieux sans jonctions
				System.out.println(lieu.toString());
				for (Jonction jonction : lieu.jonctions) {
					System.out.println("\t" + jonction.toString());
				}
			}
		}
		
		// positionner geographiquement tous les lieux
		final List<Lieu> lieuxPlaces = placerTousLesLieux(lieux);

		// dessiner
		dessiner(lieuxPlaces);
	}
	
	/**
	 * Positionner geographiquement tous les lieux
	 * @param lieux tous les lieux
	 * @return lieux qu'on a reussi a placer
	 */
	private static List<Lieu> placerTousLesLieux(final List<Lieu> lieux) {
		// on cherche le premier lieu
		final Lieu premierLieu = chercherUnLieuParId(ID_PREMIER_LIEU, lieux);
		
		final List<Lieu> lieuxAPlacer = new LinkedList<>();
		final ArrayList<Lieu> lieuxPlaces = new ArrayList<>();
		lieuxAPlacer.add(premierLieu);
		while (lieuxAPlacer.size() > 0) {
			final Lieu lieu = lieuxAPlacer.get(0);
			// on place naivement le nouveau lieu
			essayerDePlacer(lieu, lieuxPlaces);
			// y a-t-il des chevauchements entre les lieux places ?
			final boolean onAReussiAPlacer = !yATIlDesChevauchements(lieuxPlaces, lieu);
			if (onAReussiAPlacer) {
				// on a reussi a placer le lieu
				marquerLeLieuCommeEtantPlace(lieu, lieuxPlaces, lieuxAPlacer, lieux);
				
			} else {
				// on n'a pas reussi a placer le lieu

				// on eloigne les lieux les uns des autres 
				// pour faire de la place au nouveau				
				eloignerTousLesLieuxLesUnsDesAutres(lieuxPlaces, lieu);
				
				// on retente l'insertion...
				// on place naivement le nouveau lieu
				essayerDePlacer(lieu, lieuxPlaces);
				// y a-t-il des chevauchements entre les lieux places ?
				final boolean onAReussiAPlacer2 = !yATIlDesChevauchements(lieuxPlaces, lieu);
				if (onAReussiAPlacer2) {
					// on a enfin reussi a placer le lieu !
					marquerLeLieuCommeEtantPlace(lieu, lieuxPlaces, lieuxAPlacer, lieux);
					
					// on rapproche a nouveau les lieux
					// qu'on avait eloignes tout a l'heure
					rapprocherLesLieuxLesUnsDesAutres(lieuxPlaces);
					
				} else {
					// non mais franchement, lui c'est un cas desespere, oh !
					System.err.println("Impossible d'inserer le lieu : "+lieu.toString());
					
					// on le retire de la liste des lieux a placer
					lieuxAPlacer.remove(lieu);
				}
			}
			
		}
		return lieuxPlaces;
	}
	
	/**
	 * Essayer de placer le lieu
	 * @param lieu a placer
	 * @param lieuxPlaces lieux deja places
	 */
	private static void essayerDePlacer(final Lieu lieu, final List<Lieu> lieuxPlaces) {
		if (lieuxPlaces.size() == 0) {
			// aucun lieu n'a encore ete place
			// celui-ci sera le point de depart
			lieu.x = 0;
			lieu.y = 0;
			return;
		}
		
		// quel lieu deja place mene a celui-ci ?
		// par quelle jonction ?
		Lieu lieuProvenance = null;
		Jonction jonctionProvenance = null;
		onChercheQuelLieuPlaceMeneACeluiCi: for (Lieu lieuPlace : lieuxPlaces) {
			for (Jonction jonction : lieuPlace.jonctions) {
				if (jonction.idLieuDestination == lieu.id) {
					lieuProvenance = lieuPlace;
					jonctionProvenance = jonction;
					break onChercheQuelLieuPlaceMeneACeluiCi;
				}
			}
		}
	
		// dans quelle direction se fait la jonction ?
		final int directionDeLaJonction = Transition.calculerDirectionDefilement(
				jonctionProvenance.xOrigine, jonctionProvenance.yOrigine, 
				jonctionProvenance.xDestination, jonctionProvenance.yDestination, 
				lieuProvenance.largeur, lieuProvenance.hauteur, 
				lieu.largeur, lieu.hauteur);
		
		// on calcule la position du nouveau lieu a inserer
		jonctionProvenance.direction = directionDeLaJonction;
		switch (directionDeLaJonction) {
		case Direction.BAS:
			lieu.x = lieuProvenance.x + jonctionProvenance.xOrigine - jonctionProvenance.xDestination;
			lieu.y = lieuProvenance.y + lieuProvenance.hauteur;
			break;
		case Direction.GAUCHE:
			lieu.x = lieuProvenance.x - lieu.largeur;
			lieu.y = lieuProvenance.y + jonctionProvenance.yOrigine - jonctionProvenance.yDestination;
			break;
		case Direction.DROITE: 
			lieu.x = lieuProvenance.x + lieuProvenance.largeur;
			lieu.y = lieuProvenance.y + jonctionProvenance.yOrigine - jonctionProvenance.yDestination;
			break;
		case Direction.HAUT:
		default:
			lieu.x = lieuProvenance.x + jonctionProvenance.xOrigine - jonctionProvenance.xDestination;
			lieu.y = lieuProvenance.y - lieu.hauteur;
			break;
		}
	}
	
	/**
	 * Detecter les chevauchements entre les lieux places
	 * @param lieuxPlaces lieux deja places
	 * @param lieu recemment place
	 * @return true s'il y a un chevauchement
	 */
	private static boolean yATIlDesChevauchements(final List<Lieu> lieuxPlaces, final Lieu lieu) {
		return lieuxPlaces.size() > 200;
		// TODO
		/*
		for (Lieu lieu2 : lieuxPlaces) {
			if (Hitbox.lesDeuxRectanglesSeChevauchent(lieu.x, lieu.x+lieu.largeur, 
					lieu.y, lieu.y+lieu.hauteur, 
					lieu2.x, lieu2.x+lieu2.largeur, 
					lieu2.y, lieu2.y+lieu2.hauteur, 
					lieu.largeur, lieu.hauteur, 
					lieu2.largeur, lieu2.hauteur)) {
				return true;
			}
		}
		return false;*/
	}
	
	/**
	 * Eloigner les lieux les uns des autres 
	 * pour faire de la place a un nouveau.
	 * @param lieuxPlaces lieux deja places
	 * @param lieu a inserer
	 */
	private static void eloignerTousLesLieuxLesUnsDesAutres(final List<Lieu> lieuxPlaces, final Lieu lieu) {
		//TODO
	}
	
	/**
	 * Rapprocher a nouveau les lieux precedemment eloignes 
	 * pour faire place a un nouveau.
	 * @param lieuxPlaces lieux deja places
	 */
	private static void rapprocherLesLieuxLesUnsDesAutres(final List<Lieu> lieuxPlaces) {
		//TODO
	}
	
	/**
	 * Retirer le lieu de la liste des lieux a placer.
	 * Ajouter le lieu dans la liste des lieux deja places.
	 * Ajouter les lieux voisins de celui-ci dans la liste des lieux a placer.
	 * @param lieu dont le placement a ete reussi
	 * @param lieuxPlaces lieux deja places
	 * @param lieuxAPlacer lieux a placer
	 * @param lieux tous les lieux
	 */
	private static void marquerLeLieuCommeEtantPlace(final Lieu lieu, final List<Lieu> lieuxPlaces, final List<Lieu> lieuxAPlacer, final List<Lieu> lieux) {
		// on marque le lieu comme etant place
		lieuxPlaces.add(lieu);
		lieuxAPlacer.remove(lieu);
		
		// on ajoute tous les lieux voisins a la liste des lieux a placer
		for (Jonction jonction : lieu.jonctions) {
			final Lieu lieuAJoindre = chercherUnLieuParId(jonction.idLieuDestination, lieux);
			if (lieuAJoindre != null
					&& !lieuxAPlacer.contains(lieuAJoindre) // ce lieu est deja dans la liste de traitement en attente
					&& !lieuxPlaces.contains(lieuAJoindre) // ce lieu a deja ete place
					&& !LIEUX_GROTTES.contains(lieuAJoindre.id) // on masque les grottes
			) {
				lieuxAPlacer.add(lieuAJoindre);
			}
		}
	}

	/**
	 * Recenser les lieux et jonctions entre lieux
	 * @return lieux
	 */
	private static List<Lieu> recenserLesLieux() {
		final List<Lieu> lieux = new ArrayList<>();
		for (int numeroMap = 1; numeroMap <= NOMBRE_DE_MAPS; numeroMap++) {
			try {
				final JSONObject jsonMap = InterpreteurDeJson.ouvrirJsonMap(numeroMap);
				
				// on recense un nouveau lieu
				final Lieu lieu = new Lieu();
				lieu.id = numeroMap;
				lieu.nom = jsonMap.getString("nom");
				lieu.largeur = jsonMap.getInt("largeur");
				lieu.hauteur = jsonMap.getInt("hauteur");
				lieu.jonctions = new ArrayList<>();
				
				final JSONArray jsonEvents = jsonMap.getJSONArray("events");
				for (Object oEvent : jsonEvents) {
					try {
						final JSONObject jsonEvent = (JSONObject) oEvent;
						if (jsonEvent.has("pages")) {
							final JSONArray jsonPages = jsonEvent.getJSONArray("pages");
							// pages de l'event
							for (Object oPage : jsonPages) {
								final JSONObject jsonPage = (JSONObject) oPage;
								
								// cette teleportation est-elle faite par contact ?
								final JSONArray jsonConditions = jsonPage.getJSONArray("conditions");
								boolean contact = false;
								estCeQueLaConditionEstUnContact: for (Object oCondition : jsonConditions) {
									final JSONObject jsonCondition = (JSONObject) oCondition;
									final String nomCondition = jsonCondition.getString("nom");
									if ("ArriveeAuContact".equals(nomCondition)
											|| "Contact".equals(nomCondition)) {
										contact = true;
										break estCeQueLaConditionEstUnContact;
									}
								}
								if (contact) { // on ignore les teleportations en processus parallele
									
									final JSONArray jsonCommandes = jsonPage.getJSONArray("commandes");
									for (Object oCommande : jsonCommandes) {
										final JSONObject jsonCommande = (JSONObject) oCommande;
										if ("ChangerDeMap".equals(jsonCommande.getString("nom"))
												&& !jsonCommande.getBoolean("variable")) {
											
											// on recense une jonction entre ce lieu et un autre
											final Jonction jonction = new Jonction();
											jonction.idLieuOrigine = lieu.id;
											jonction.xOrigine = jsonEvent.getInt("x");
											jonction.yOrigine = jsonEvent.getInt("y");
											jonction.idLieuDestination = jsonCommande.getInt("numeroNouvelleMap");
											jonction.xDestination = jsonCommande.getInt("xDebutHeros");
											jonction.yDestination = jsonCommande.getInt("yDebutHeros");
											
											lieu.jonctions.add(jonction);
										}
										
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				lieux.add(lieu);
				System.out.println("Recensement du lieu "+numeroMap);
				
			} catch (Exception e) {
				System.err.println("Erreur avec la map "+numeroMap);
				e.printStackTrace();
			}
		}
		return lieux;
	}
	
	/**
	 * Dessiner la carte des lieux
	 * @param lieux places
	 */
	private static void dessiner(final List<Lieu> lieux) {
		// on calcule le cadre
		final int[] minMax = calculerCadre(lieux);
		final int largeurImage = minMax[2] - minMax[0] +1;
		final int hauteurImage = minMax[3] - minMax[1] +1;
		System.out.println("largeur image : "+largeurImage);
		System.out.println("hauteur image : "+hauteurImage);
		
		// on dessine
		final BufferedImage img = new BufferedImage(largeurImage, hauteurImage, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g2d = img.createGraphics();
		// fond image
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, largeurImage, hauteurImage);
		
		final HashMap<String, Tileset> tilesets = new HashMap<>();
		// chaque lieu
		for (Lieu lieu : lieux) {
			final int xLieuSurLImage = lieu.x - minMax[0];
			final int yLieuSurLImage = lieu.y - minMax[1];
			
			// passabilite
			try {
				// on recupere le tileset
				final JSONObject jsonMap = InterpreteurDeJson.ouvrirJsonMap(lieu.id);
				final String nomTileset = jsonMap.getString("tileset");
				final Tileset tileset;
				if (tilesets.containsKey(nomTileset)) {
					tileset = tilesets.get(nomTileset);
				} else {
					tileset = new Tileset(nomTileset);
					tilesets.put(nomTileset, tileset);
				}
				// on recupere la couche 0 de la map
				final int[][] layer0 = Map.recupererCouche(jsonMap, 0, lieu.largeur, lieu.hauteur);
				for (int x = 0; x < lieu.largeur; x++) {
					for (int y = 0; y < lieu.hauteur; y++) {
						boolean bordure = x == 0 || y == 0 || x == lieu.largeur-1 || y == lieu.hauteur-1;
						try {
							final int tile = layer0[x][y];
							// on dessine la passabilite
							final boolean passable = Passabilite.PASSABLE.equals(tileset.passabiliteDeLaCase(tile));
							final int terrain = tileset.terrainDeLaCase(tile);
							final int rgb;
							if (passable) {
								switch (terrain) {
								case 0: //sol
									rgb = Color.WHITE.getRGB();
									break;
								case 5: //eau
									rgb = Color.CYAN.getRGB();
									break;
								case 6: //trou
									rgb = bordure ? Color.BLUE.getRGB() : Color.BLACK.getRGB();
									break;
								default: //autre
									rgb = Color.GREEN.getRGB();
									break;
								}
							} else {
								rgb = Color.GRAY.getRGB();
							}
							img.setRGB(xLieuSurLImage+x, yLieuSurLImage+y, rgb);
						} catch (ArrayIndexOutOfBoundsException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// id lieu
			int idLieu = lieu.id;
			final List<Integer> chiffres = new ArrayList<>();
			while (idLieu >0) {
				chiffres.add(0, idLieu % 10);
				idLieu /= 10;
			}
			for (int i = 0; i<chiffres.size(); i++) {
				final int chiffre = chiffres.get(i);
				dessinerChiffre(chiffre, img, i, xLieuSurLImage, yLieuSurLImage);
			}
			
			// contour lieu
//			g2d.setColor(Color.BLUE);
//			g2d.drawRect(lieu.x - minMax[0], lieu.y - minMax[1], lieu.largeur, lieu.hauteur);
		}

		g2d.dispose();
		// on sauvegarde l'image
		Graphismes.sauvegarderImage(img, "plan kujira");
	}
	
	/**
	 * Dessiner un chiffre
	 * @param chiffre a dessiner
	 * @param img image
	 * @param position unite ? dizaine ? centaine ?
	 * @param x position ou on dessine le chiffre sur l'image
	 * @param y position ou on dessine le chiffre sur l'image
	 */
	public static void dessinerChiffre(final int chiffre, BufferedImage img, final int position, final int x, final int y) {
		final boolean[][] chiffreDessin = CHIFFRES_A_DESSINER[chiffre];
		for (int j = 0; j<chiffreDessin.length; j++) {
			for (int i = 0; i<chiffreDessin[j].length; i++) {
				if (chiffreDessin[j][i]) {
					img.setRGB(1+x+i+position*(chiffreDessin[j].length+1), 1+y+j, Color.RED.getRGB());
				}
			}
		}
	}
	
	/**
	 * Calculer l'etendue du monde
	 * @param lieux places
	 * @return minX, minY, maxX, maxY
	 */
	private static int[] calculerCadre(final List<Lieu> lieux) {
		int minX = 0;
		int minY = 0;
		int maxX = 0;
		int maxY = 0;
		for (Lieu lieu : lieux) {
			if (lieu.x < minX) {
				minX = lieu.x;
			}
			if (lieu.y < minY) {
				minY = lieu.y;
			}
			if (lieu.x + lieu.largeur > maxX) {
				maxX = lieu.x + lieu.largeur;
			}
			if (lieu.y + lieu.hauteur > maxY) {
				maxY = lieu.y + lieu.hauteur;
			}
		}
		return new int[]{minX, minY, maxX, maxY};
	}
	
	/**
	 * Represente une map du jeu
	 */
	protected static class Lieu {
		public int id;
		public String nom;
		public int largeur, hauteur;
		public List<Jonction> jonctions;
		/** placement geographique */
		public int x, y;
		
		/**
		 * Comparaison
		 * @param lieu autre
		 * @return true si c'est le m�me
		 */
		public final boolean equals(final Lieu lieu) {
			return lieu.id == this.id;
		}

		/**
		 * Ecrire un petit blabla
		 * @return infos
		 */
		public String toString() {
			return this.id+" - "+this.nom+" ("+this.largeur+";"+this.hauteur+")";
		}
	}
	
	/**
	 * Chercher un lieu dans une liste par son id
	 * @param id cherche
	 * @param lieux parcourus
	 * @return lieu
	 */
	private static Lieu chercherUnLieuParId(final int id, final List<Lieu> lieux) {
		for (Lieu lieu : lieux) {
			if (lieu.id == id) {
				return lieu;
			}
		}
	return null;
	}
	
	/**
	 * Represente une connection physique 
	 * entre une map du jeu et une autre map du jeu
	 */
	private static class Jonction  {
		public int idLieuOrigine;
		public int xOrigine, yOrigine;
		public int idLieuDestination;
		public int xDestination, yDestination;
		public int direction;
		
		/**
		 * Ecrire un petit blabla
		 * @return infos
		 */
		public String toString() {
			return this.idLieuOrigine+" ("+this.xOrigine+";"+this.yOrigine+") -> "
					+ this.idLieuDestination+" ("+this.xDestination+";"+this.yDestination+")";
		}
	}

}
