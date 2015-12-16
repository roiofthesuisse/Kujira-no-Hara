package utilitaire;

public class Math {
	public static int modulo(int a, int b){
		if(b<=0){
			return -1;
		}
		while(a<0){
			a += b;
		}
		while(a>=b){
			a -= b;
		}
		return a;
	}
}
