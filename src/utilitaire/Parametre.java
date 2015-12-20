package utilitaire;

/**
 * Un paramètre est envoyé à un constructeur appelé par l'introspection.
 * Cette pratique est employée lors de l'importation d'un élément du jeu via un fichier JSON,
 * le nombre et la nature des paramètres pouvant varier selon le constructeur.
 * Un objet du jeu peut donc avoir un constructeur aux arguments explicitement spécifiés, 
 * et à côté un constructeur qui prend en argument une liste de Paramètres.
 * TODO migrer vers une Map de paramètres, pour accéder directement aux paramètres sans faire de rechercher sur la liste (coûteux).
 */
public class Parametre{
	public String nom;
	public Object valeur;
	public Class<?> type;
	
	public Parametre(String nom, Object valeur){
		this.nom = nom;
		this.valeur = valeur;
		this.type = devinerLeType(valeur);
	}
	
	@SuppressWarnings("unused")
	private Class<?> devinerLeType(Object val){
		try{
			Boolean bool = (Boolean) val;
			return Boolean.class;
		}catch(Exception e1){
			try{
				Integer integer = (Integer) val;
				return Integer.class;
			}catch(Exception e2){
				String string = (String) val;
				return String.class;
			}
		}
	}
}