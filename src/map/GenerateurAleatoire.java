package map;

import java.util.Random;

public class GenerateurAleatoire extends Random{
	private static final long serialVersionUID = 1L;
	
	public int nombreEntre(int a, int b){
		return this.nextInt(b-a)+a;
	}

}
