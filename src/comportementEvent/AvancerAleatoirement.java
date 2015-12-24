package comportementEvent;

import java.util.ArrayList;

import utilitaire.Parametre;

public class AvancerAleatoirement extends Avancer{	
	
	/**
	 * Constructeur spécifique
	 */
	public AvancerAleatoirement(){
		super(rand.nextInt(4), 1);
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AvancerAleatoirement(ArrayList<Parametre> parametres){
		this();
	}
	
	@Override
	public void reinitialiser(){
		super.reinitialiser();
		int nouvelleDirection = rand.nextInt(4);
		if( (direction==0 && nouvelleDirection!=3) 
				|| (direction==1 && nouvelleDirection!=2) 
				|| (direction==2 && nouvelleDirection!=1) 
				|| (direction==3 && nouvelleDirection!=0) 
		){
			direction = nouvelleDirection;
		}
		
	}
}
