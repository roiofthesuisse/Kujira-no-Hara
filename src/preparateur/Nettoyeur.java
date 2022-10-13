package preparateur;

/**
 * Adapter les fichiers JSON du jeu au moteur Java sur des specificites.
 */
public abstract class Nettoyeur {
	
	/**
	 * Lancer le nettoyeur.
	 * @param args rien du tout
	 */
	public static void main(final String[] args) {
		// Verifier si le nettoyage a deja ete fait
		if (leNettoyageADejaEteFait()) {
			return;
		}

		// Noms des touches du clavier dans les messages
		//reecrireLesTouchesDuClavier();
		
		// Egaliser les musiques
		//egaliserLesMusiques();

	}

	private static boolean leNettoyageADejaEteFait() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Les dialogues mentionnent les touches du clavier, mais en Java elles sont differentes !
	 */
	private static void reecrireLesTouchesDuClavier() {
		// TODO Auto-generated method stub
	}

	private static void egaliserLesMusiques() {
		//TODO calculer le volume moyen de chaque musique
		//TODO definir 1.0 comme volume par defaut pour la plus basse
		//TODO deduire les autres volumes par defaut par produit en croix
		//TODO recenser les occurences de chaque musique dans le jeu avec leur volume assigne
		//TODO pour le plus grand volume d'usage, utiliser le volume par defaut
		//TODO deduire les remplacements des autres volumes d'usage par produit en croix 
	}
	
}
