package comportementEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class Attendre extends CommandeEvent {
	private int nombreDeFrames; //nombre de frames qu'il faut attendre
	private int ceQuiAEteFait; //nombre de frames qu'on a déjà attendu
	
	/**
	 * Constructeur spécifique
	 */
	public Attendre(int nombreDeFrames){
		this.ceQuiAEteFait = 0;
		this.nombreDeFrames = nombreDeFrames;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public Attendre(HashMap<String, Object> parametres){
		this( (Integer) parametres.get("nombreDeFrames") );
	}
	
	public void reinitialiser(){
		this.ceQuiAEteFait = 0;
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		if(this.ceQuiAEteFait>=this.nombreDeFrames){
			reinitialiser();
			return curseurActuel+1;
		}else{
			this.ceQuiAEteFait++;
			return curseurActuel;
		}
	}

}
