package main;

/**
 * Adapter les fichiers JSON du jeu au moteur Java sur des spécificités.
 */
public abstract class Nettoyeur {
	/**
	 * Lancer le nettoyeur.
	 * @param args rien du tout
	 */
	public static void main(final String[] args) {
		// Vérifier si le nettoyage a déjà été fait
		if (leNettoyageADejaEteFait()) {
			return;
		}
		
		// Autotiles cousins
		calculerLesAutotilesCousins();
		
		// Noms des touches du clavier dans les messages
		reecrireLesTouchesDuClavier();
		
		// Egaliser les musiques
		egaliserLesMusiques();
		
	}

	private static boolean leNettoyageADejaEteFait() {
		// TODO Auto-generated method stub
		return false;
	}

	private static void calculerLesAutotilesCousins() {
		// TODO Auto-generated method stub
	}

	private static void reecrireLesTouchesDuClavier() {
		// TODO Auto-generated method stub
	}

	private static void egaliserLesMusiques() {
		//TODO calculer le volume moyen de chaque musique
		//TODO définir 1.0 comme volume par défaut pour la plus basse
		//TODO déduire les autres volumes par défaut par produit en croix
		//TODO recenser les occurences de chaque musique dans le jeu avec leur volume assigné
		//TODO pour le plus grand volumed'usage, utiliser le volume par défaut
		//TODO déduire les remplacements des autres volumes d'usage par produit en croix 
	}
}
