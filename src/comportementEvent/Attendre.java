package comportementEvent;

import java.util.ArrayList;

public class Attendre extends CommandeEvent {
	public int nombreDeFrames; //nombre de frames qu'il faut attendre
	public int ceQuiAEteFait; //nombre de frames qu'on a déjà attendu
	
	public Attendre(int nombreDeFrames){
		this.ceQuiAEteFait = 0;
		this.nombreDeFrames = nombreDeFrames;
	}
	
	public void reinitialiser(){
		ceQuiAEteFait = 0;
	}
	
	@Override
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes) {
		if(ceQuiAEteFait>=nombreDeFrames){
			reinitialiser();
			return curseurActuel+1;
		}else{
			ceQuiAEteFait++;
			return curseurActuel;
		}
	}

}
