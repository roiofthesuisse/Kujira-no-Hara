package menu;

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