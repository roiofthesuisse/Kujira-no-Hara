package map;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import comportementEvent.Avancer;
import comportementEvent.CommandeEvent;
import conditions.Condition;

public class Event implements Comparable<Event>{
	public Map map;
	public int x; //en pixels
	public int y; //en pixels
	//de combien de pixels avance l'event à chaque pas :
	public int vitesseActuelle = 4; //1:trèsLent 2:lent 4:normal 8:rapide 16:trèsrapide 32:trèstrèsRapide
	//toutes les combien de frames l'event change d'animation :
	public int frequenceActuelle = 4; //1:trèsAgité 2:agité 4:normal 8:mou 16:trèsMou
	public String nom;
	public int numero;
	/**
	 * 0 bas, 1 gauche, 2 droite, 3 haut.
	 */
	public int direction = 0;
	public int animation = 0;
	public BufferedImage imageActuelle = null;
	/**
	 * L'event est-il en train d'avancer en ce moment même ? (utile pour l'animation)
	 */
	public Boolean avance = false;
	public Boolean animeALArretActuel = false;
	public Boolean animeEnMouvementActuel = true;
	public ArrayList<CommandeEvent> deplacementActuel;
	Boolean repeterLeDeplacementActuel = true;
	protected Boolean ignorerLesMouvementsImpossiblesActuel = false;
	Boolean traversableActuel = false;
	public int largeurHitbox = 32;
	int hauteurHitbox = 32;
	/**
	 * Décale l'affichage vers le bas.
	 * En effet, décaler l'affichage dans les trois autres directions est possible en modifiant l'image.
	 */
	int offsetY = 0; 
	public ArrayList<PageDeComportement> pages;
	public PageDeComportement pageActive = null;
	public Boolean interrupteurLocalA = false;
	public Boolean interrupteurLocalB = false;
	public Boolean interrupteurLocalC = false;
	public Boolean interrupteurLocalD = false;
	/**
	 * Lorsque ce marqueur est à true, on considère l'event comme supprimé.
	 * Ce n'est qu'un simple marqueur : l'event n'est réellement supprimé qu'après la boucle for sur les events.
	 */
	public Boolean supprime = false;
	
	/**
	 * @param map
	 * @param x
	 * @param y
	 * @param nom de l'event
	 * @param nomImage
	 * @param pages
	 * @param largeurHitbox
	 */
	protected Event(Map map, Integer x, Integer y, String nom, ArrayList<PageDeComportement> pages, int largeurHitbox){
		this.map = map;
		this.x = x*32;
		this.y = y*32;
		this.nom = nom;
		this.pages = pages;
		this.largeurHitbox = largeurHitbox;
		initialiserLesPages();
	}
	
	protected void initialiserLesPages() {
		int numeroCondition = 0;
		int numeroPage = 0;
		try{
			for(PageDeComportement page : pages){
				page.event = this;
				if(page!=null){
					page.numero = numeroPage;
					//numérotation des conditions et on apprend aux conditions qui est leur page
					try{
						for(Condition cond : page.conditions){
							cond.numero = numeroCondition;
							cond.page = page;
							numeroCondition++;
						}
					}catch(NullPointerException e){
						//pas de conditions pour déclencher cette page
					}
					//on apprend aux commandes qui est leur page
					try{
						for(CommandeEvent comm : page.commandes){
							comm.page = page;
						}
					}catch(NullPointerException e){
						//pas de commandes dans cette page
					}
					numeroPage++;
				}
			}
		}catch(NullPointerException e){
			//pas de pages dans cet event
		}
	}

	public Boolean croisementDeHitbox(int xFutur, int yFutur, int tailleHitbox, int xAutre, int yAutre, int tailleHitboxAutre){
		int deltaX = (xFutur-xAutre);
		int deltaY = (yFutur-yAutre);
		int rayon = (tailleHitbox+tailleHitboxAutre);
		if(deltaX*deltaX+deltaY*deltaY > rayon*rayon/2){
			return false; //si les deux events sont trop éloignés l'un de l'autre, il n'y a pas de collision
		}else{ 
			int x1min = xFutur;
			int x1max = xFutur+tailleHitbox;
			int x2min = xAutre;
			int x2max = xAutre+tailleHitboxAutre;
			int y1min = yFutur;
			int y1max = yFutur+tailleHitbox;
			int y2min = yAutre;
			int y2max = yAutre+tailleHitboxAutre;
			/*
			if( //deux coins se chevauchent
			   ((x1min<=x2min && x2min<x1max && x1max<=x2max)
			  ||(x2min<=x1min && x1min<x2max && x2max<=x1max))
			&& ((y1min<=y2min && y2min<y1max && y1max<=y2max)
			  ||(y2min<=y1min && y1min<y2max && y2max<=y1max))
			){
				return true; 
			}
			if(tailleHitbox!=tailleHitboxAutre){
				//dernier cas : deux segments se chevauchent
				return(((x2min<x1min && x1min<x2max)||(x2min<x1max && x1max<x2max)) 
					&& ((y2min<y1min && y1min<y2max)||(y2min<y1max && y1max<y2max)))
					||(((x1min<x2min && x2min<x1max)||(x1min<x2max && x2max<x1max)) 
					&& ((y1min<y2min && y2min<y1max)||(y1min<y2max && y2max<y1max)));
			}else{
				//si les deux events ont la même taille ils ne peuvent que se croiser par coin
				return false;
			}
			*/
			return Hitbox.croisement(x1min, x1max, y1min, y1max, x2min, x2max, y2min, y2max, tailleHitbox, tailleHitboxAutre);
		}
	}
	
	public Boolean deplacementPossible(int sens){
		Boolean reponse = true;
		int xAInspecter = this.x; //pour le décor
		int yAInspecter = this.y;
		int xAInspecter2 = this.x; //pour le décor, deuxième case à vérifier si entre deux cases
		int yAInspecter2 = this.y;
		int xAInspecter3 = this.x; //pour les events
		int yAInspecter3 = this.y;
		switch(sens){
			case 0 : yAInspecter+=largeurHitbox;   yAInspecter2+=largeurHitbox;   xAInspecter2+=32; yAInspecter3+=vitesseActuelle; break;
			case 1 : xAInspecter-=vitesseActuelle; xAInspecter2-=vitesseActuelle; yAInspecter2+=32; xAInspecter3-=vitesseActuelle; break;
			case 2 : xAInspecter+=largeurHitbox;   xAInspecter2+=largeurHitbox;   yAInspecter2+=32; xAInspecter3+=vitesseActuelle; break;
			case 3 : yAInspecter-=vitesseActuelle; yAInspecter2-=vitesseActuelle; xAInspecter2+=32; yAInspecter3-=vitesseActuelle; break;
			default : break;
		}
		try{
			//si rencontre avec un élément de décor non passable -> false
				//TODO etendre aux layers 1 et 2 : si false return false, sinon continuer à chercher dans la layer suivante.
				//Si RAS, renvoyer true.
			ArrayList<int[][]> layers = new ArrayList<int[][]>();
			layers.add(map.layer0);
			layers.add(map.layer1);
			for(int[][] layer : layers){
				try{
					int carrelageAInspecter = layer[xAInspecter/32][yAInspecter/32];
					int carrelageAInspecter2 = layer[xAInspecter2/32][yAInspecter2/32];
					if(carrelageAInspecter!=-1 && !map.tileset.passabilite[carrelageAInspecter]){
						reponse = false;
						return false;
					}
					//second carrelage à inspecter si l'event est à cheval sur deux cases
					if(carrelageAInspecter2!=-1 && (sens==0||sens==3) && ((x+largeurHitbox-1)/32!=(x/32)) && !map.tileset.passabilite[carrelageAInspecter2]){
						reponse = false;
						return false;
					}
					if(carrelageAInspecter2!=-1 && (sens==1||sens==2) && ((y+largeurHitbox-1)/32!=(y/32)) && !map.tileset.passabilite[carrelageAInspecter2]){
						reponse = false;
						return false;
					}
				}catch(NumberFormatException e){
					System.out.println("NumberFormatException dans Event.deplacementPossible(), cela signifie que le CSV contient un non chiffre.");
				}
			}
			//si rencontre avec un autre évènement non traversable -> false
			for(Event autreEvent : this.map.events){
				if(numero != autreEvent.numero){
					if( croisementDeHitbox(xAInspecter3, yAInspecter3, largeurHitbox, autreEvent.x, autreEvent.y, autreEvent.largeurHitbox) ){
						return false;
					}
				}
			}
		}catch(Exception e){
			//on sort de la map !
			e.printStackTrace();
			reponse = true;
		}
		return reponse;
	}

	/**
	 * déplacement naturel, inscrit dans la liste des déplacements de la page de l'event
	 */
	public void deplacer() {
		if(pageActive==null){
			activerUnePage(); //on essayer d'activer une page, mais il se peut que la bonne réponse soit aucune page
		}
		if(pageActive!=null){
			try{
				CommandeEvent mouvementActuel = deplacementActuel.get(0);
				String typeMouvement = mouvementActuel.getClass().getName();
				switch(typeMouvement){
					case "comportementEvent.Avancer" : deplacer((Avancer)mouvementActuel); break;
					case "comportementEvent.AvancerAleatoirement" : deplacer((Avancer)mouvementActuel); break;
					default : break;
				}
			}catch(NullPointerException e){
				//pas de déplacement pour cet event
			}
		}
	}
	
	/**
	 * déplacement naturel ou forcé
	 */
	public void deplacer(Avancer mouvementActuel){
		try{
			int sens = ((Avancer) mouvementActuel).getDirection();
			if( deplacementPossible(sens) ){
				this.avance = true;
				//déplacement :
				switch(sens){
					case 0 : this.y+=vitesseActuelle; break;
					case 1 : this.x-=vitesseActuelle; break;
					case 2 : this.x+=vitesseActuelle; break;
					case 3 : this.y-=vitesseActuelle; break;
					default : break;
				}
				((Avancer) mouvementActuel).ceQuiAEteFait += vitesseActuelle;
				//*ancien emplacement de l'animation*
				//quelle sera la commande suivante ?
				if( ((Avancer) mouvementActuel).ceQuiAEteFait >= ((Avancer) mouvementActuel).nombreDeCarreaux*32){
					if(repeterLeDeplacementActuel){
						//on le réinitialise et on le met en bout de file
						((Avancer) mouvementActuel).reinitialiser();
						deplacementActuel.add(mouvementActuel);
					}
					//on passe au mouvement suivant
					deplacementActuel.remove(0);
				}
			}else{
				this.avance = false;
				if(ignorerLesMouvementsImpossiblesActuel){
					if(repeterLeDeplacementActuel){
						//on le réinitialise et on le met en bout de file
						((Avancer) mouvementActuel).reinitialiser();
						deplacementActuel.add(mouvementActuel);
					}
					//on passe au mouvement suivant
					deplacementActuel.remove(0);
				}
			}
			
		} catch (NullPointerException e1) {
			//pas de mouvement pour cet évènement
		} catch(Exception e) {
			System.out.println("Erreur lors du mouvement de l'évènement :");
			e.printStackTrace();
		}
	}

	@Override
	/**
	 * permet de dire si un event est devant ou derrière un autre en terme d'affichage
	 */
	public int compareTo(Event e) {
		if(y > e.y) return 1;
		if(y < e.y) return -1;
		return 0;
	}
	
	public void setIgnorerLesMouvementsImpossibles(Boolean b){
		if(this.pageActive == null) this.activerUnePage();
		try{
			ignorerLesMouvementsImpossiblesActuel = b;
		}catch(NullPointerException e){
			//pas de page pour cet event
		}
	}

	public void activerUnePage() {
		PageDeComportement pageQuOnChoisitEnRemplacement = null;
		try{
			Boolean onATrouveLaPageActive = false;
			for(int i=pages.size()-1; i>=0 && !onATrouveLaPageActive; i--){
				PageDeComportement page = pages.get(i);
				Boolean cettePageConvient = true;
				try{
					for(Condition cond : page.conditions){
						if(!cond.estVerifiee()){
							cettePageConvient = false;
						}
						if(!cettePageConvient) break;
					}
				}catch(NullPointerException e){
					//pas de conditions pour cette page
				}
				if(cettePageConvient){
					onATrouveLaPageActive = true;
					pageQuOnChoisitEnRemplacement = page;
				}
			}
		}catch(NullPointerException e){
			//pas de pages pour cet event
		}
		//on ne change la page que si c'est une page différente
		if(this.pageActive==null || (this.pageActive.numero!=pageQuOnChoisitEnRemplacement.numero) ){
			//changement de page !
			this.pageActive = pageQuOnChoisitEnRemplacement;
			//System.out.println("     l'evènement "+numero+" ("+nom+") change de page : page "+pageActive.numero);
			//maintenant on donne à l'évent toutes les propriétés de la page
			if(this.pageActive != null){
				this.imageActuelle = pageActive.image;
				this.deplacementActuel = pageActive.deplacement;
				this.vitesseActuelle = pageActive.vitesse;
				this.frequenceActuelle = pageActive.frequence;
				this.animeALArretActuel = pageActive.animeALArret;
				this.animeEnMouvementActuel = pageActive.animeEnMouvement;
				this.repeterLeDeplacementActuel = pageActive.repeterLeDeplacement;
				this.ignorerLesMouvementsImpossiblesActuel = pageActive.ignorerLesMouvementsImpossibles;
				this.traversableActuel = pageActive.traversable;
			}
		}
	}
	
}