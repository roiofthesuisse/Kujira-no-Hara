package conditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Main;
import map.Event;
import map.Hitbox;

/**
 * Condition basée sur l'interprétation d'un script ruby.
 */
public class ConditionScript extends Condition {
	//constantes
	private static final Logger LOG = LogManager.getLogger(ConditionScript.class);
	
	private static final String ESPACE = "(\\s)*";
	private static final String TRUE = "true";
	private static final String FALSE = "false";
	
	private static final String HEROS = "\\$game_player\\.";
	private static final String X = "x";
	private static final String Y = "y";
	private static final String COORD_EVENT_DEBUT = "\\$game_map\\.events\\[";
	private static final String COORD_EVENT_FIN = "\\]\\.";
	private static final String EVENT_ID = "@event_id";
	private static final String NOMBRE = "[0-9]+";
	private static final String COORD_EVENT_X = COORD_EVENT_DEBUT+NOMBRE+COORD_EVENT_FIN+X;
	private static final String COORD_EVENT_Y = COORD_EVENT_DEBUT+NOMBRE+COORD_EVENT_FIN+Y;
	private static final String COORD_HEROS_X = HEROS+X;
	private static final String COORD_HEROS_Y = HEROS+Y;
	private static final String RACINE_DEBUT = "Math\\.sqrt\\(";
	private static final String RACINE_FIN = "\\)\\.round";
	private static final String RACINAGE = RACINE_DEBUT+ESPACE+NOMBRE+ESPACE+RACINE_FIN;
	private static final String ABSOLU_DEBUT = "\\(";
	private static final String ABSOLU_FIN = "\\)\\.abs";
	private static final String ABSOLUTION = ABSOLU_DEBUT+ESPACE+NOMBRE+ESPACE+ABSOLU_FIN;
	private static final String VIE_EVENT_FIN = "\\]\\.life";
	private static final String VITALISATION = COORD_EVENT_DEBUT + NOMBRE + VIE_EVENT_FIN;
	private static final String CIBLAGE = "target_in_da_zone?("+NOMBRE+","+NOMBRE+")";
	private static final String CIBLAGE_PAR_HEROS = HEROS+CIBLAGE;
	private static final String CIBLAGE_PAR_EVENT = COORD_EVENT_DEBUT+NOMBRE+COORD_EVENT_FIN+CIBLAGE;
	
	private static final String ET = "&&";
	private static final String ETATION = NOMBRE+ESPACE+ET+ESPACE+NOMBRE;
	private static final String OU = "\\|\\|";
	private static final String OUATION = NOMBRE+ESPACE+OU+ESPACE+NOMBRE;
	private static final String NEGATION = "!"+ESPACE+NOMBRE;
	private static final String PARENTHESAGE = "\\("+ESPACE+NOMBRE+ESPACE+"\\)";
	
	private static final String EGAL = "==";
	private static final String EGALISATION = NOMBRE+ESPACE+EGAL+ESPACE+NOMBRE;
	private static final String INFEGAL = "<=";
	private static final String INFERIORATION_LARGE = NOMBRE+ESPACE+INFEGAL+ESPACE+NOMBRE;
	private static final String SUPEGAL = ">=";
	private static final String SUPERIORATION_LARGE = NOMBRE+ESPACE+SUPEGAL+ESPACE+NOMBRE;
	private static final String INFERIEUR = "<";
	private static final String INFERIORATION = NOMBRE+ESPACE+INFERIEUR+ESPACE+NOMBRE;
	private static final String SUPERIEUR = ">";
	private static final String SUPERIORATION = NOMBRE+ESPACE+SUPERIEUR+ESPACE+NOMBRE;
	private static final String DIFFERENT = "!=";
	private static final String DIFFERENTIATION = NOMBRE+ESPACE+DIFFERENT+ESPACE+NOMBRE;
	
	private static final String PLUS = "\\+";
	private static final String ADDITION = NOMBRE+ESPACE+PLUS+ESPACE+NOMBRE;
	private static final String MOINS = "-";
	private static final String SOUSTRACTION = NOMBRE+ESPACE+MOINS+ESPACE+NOMBRE;
	private static final String FOIS = "\\*";
	private static final String MULTIPLICATION = NOMBRE+ESPACE+FOIS+ESPACE+NOMBRE;
	private static final String SLASH = "/";
	private static final String DIVISION = NOMBRE+ESPACE+SLASH+ESPACE+NOMBRE;
	private static final String POURCENT = "%";
	private static final String MODULATION = NOMBRE+ESPACE+POURCENT+ESPACE+NOMBRE;
	private static final String EXPOSANT = "\\*\\*";
	private static final String EXPONENTIATION = NOMBRE+ESPACE+EXPOSANT+ESPACE+NOMBRE;
	
	private String script;
	private final boolean modeTest;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param script à interpréter
	 * @param modeTest est-on en train de tester la classe ?
	 */
	public ConditionScript(final int numero, final String script, final boolean modeTest) {
		this.numero = numero;
		this.script = script;
		this.modeTest = modeTest;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionScript(final HashMap<String, Object> parametres) {
		this( parametres.get("numero") != null ? (int) parametres.get("numero") : -1,
			(String) parametres.get("script"),
			false //cas réel
		);
	}
	
	/**
	 * Constructeur de test
	 */
	public ConditionScript() {
		this.numero = 0;
		this.script = null;
		this.modeTest = true;
	}
	
	@Override
	public final boolean estVerifiee() {
		String s = this.script;
		
		boolean fini = false;
		while (!fini) {
			System.out.println(s);
			s = traiter(s);
			try {
				//fini
				Integer.parseInt(s);
				fini = true;
			} catch (NumberFormatException e) {
				//pas fini
			}
		}

		return !"0".equals(s);
		
		//$game_player.target_in_da_zone?(@event_id, 6)
		
		//$game_map.events[@event_id].target_in_da_zone?(0, 0)

		//$game_map.events[2].lolilol("geyser character", nil, nil)
		
		//$game_map.events[22].x==18
		
		//$game_player.x < $game_map.events[@event_id].x
		
		//Math.sqrt(($game_player.x-$game_map.events[@event_id].x)**2 + ($game_player.y-$game_map.events[@event_id].y)**2).round <= 3
		
		//($game_variables[45] != $game_variables[2]) || ($game_variables[46] != $game_variables[3])
		
		//$game_map.events[@event_id].araignee_brulee?()
		
		//$game_map.events[@event_id].life <= 0
		
		//($game_map.events[3].x-$game_player.x).abs + ($game_map.events[3].y-$game_player.y).abs <= 1
		
		//$game_player.prout("Jiyounasu AttaqueEpee character", nil, 2)

		//$game_map.events[@event_id].lolilol(nil, nil, 1) || $game_map.events[@event_id].lolilol(nil, nil, 3)
		
		//($game_player.target_in_da_zone?(@event_id, 2) && $game_player.prout("Jiyounasu AttaqueEpee character", nil, 2)) || ($game_player.target_in_da_zone?(@event_id, 1) && $game_player.prout("Jiyounasu AttaqueTorche character", nil, nil)) || ($game_player.target_in_da_zone?(@event_id, 2) && $game_player.prout("Jiyounasu AttaqueEpee character", nil, 3))
		
		//Math.hypot($game_player.x-25, $game_player.y-25).round >= 10
		
		//Input.trigger?(Input::C)
		//ConditionScript : Input.trigger?(Input::X)
		
		// ! ($game_map.events[@event_id].event_arround?("Anémone HP[1] RESET", 50) || $game_map.events[@event_id].event_arround?("Crevette HP[3] RESET", 50))
	}
	
	/**
	 * Interpréter un script ruby.
	 * @param expression (en ruby)
	 * @return une chaine de caractère qui est un nombre lorsque l'interprétation est terminée.
	 */
	private String traiter(final String expression) {
		if (expression.contains(TRUE)) {
			return expression.replace(TRUE, "1");
		}
		if (expression.contains(FALSE)) {
			return expression.replace(FALSE, "0");
		}

		// Trim
		if (expression.startsWith(" ") || expression.endsWith(" ")) {
			System.out.println("trim");
			return expression.trim();
		}
		
		Pattern p;
		Matcher m;
		
		//-----------//
		// Fonctions //
		//-----------//
		
		// Event id
		p = Pattern.compile(EVENT_ID);
		m = p.matcher(expression);
		if (m.find()) {
			System.out.println(EVENT_ID);
			return expression.replaceFirst(EVENT_ID, "" + eventId());
		}
		
		// Coordonnée x event
		p = Pattern.compile(COORD_EVENT_X);
		m = p.matcher(expression);
		if (m.find()) {
			System.out.println("coordonnée x de l'event");
			final Integer nombre = extraireLeNombre(m.group(0));
			return expression.replaceFirst(COORD_EVENT_X, ""+coordonneeXEvent(nombre));
		}
		
		// Coordonnée y event
		p = Pattern.compile(COORD_EVENT_Y);
		m = p.matcher(expression);
		if (m.find()) {
			System.out.println("coordonnée y de l'event");
			final Integer nombre = extraireLeNombre(m.group(0));
			return expression.replaceFirst(COORD_EVENT_Y, ""+coordonneeYEvent(nombre));
		}
		
		// Coordonnée x héros
		p = Pattern.compile(COORD_HEROS_X);
		m = p.matcher(expression);
		if (m.find()) {
			System.out.println("coordonnée x du héros");
			return expression.replaceFirst(COORD_HEROS_X, ""+coordonneeXHeros());
		}
		
		// Coordonnée y héros
		p = Pattern.compile(COORD_HEROS_Y);
		m = p.matcher(expression);
		if (m.find()) {
			System.out.println("coordonnée y du héros");
			return expression.replaceFirst(COORD_HEROS_Y, ""+coordonneeYHeros());
		}
		
		// Vie d'un event
		p = Pattern.compile(VITALISATION);
		m = p.matcher(expression);
		if (m.find()) {
			System.out.println("vie de l'event");
			final Integer nombre = extraireLeNombre(m.group(0));
			return expression.replaceFirst(VITALISATION, ""+vieEvent(nombre));
		}
		
		// Ciblage par le héros
		p = Pattern.compile(CIBLAGE_PAR_HEROS);
		m = p.matcher(expression);
		if (m.find()) {
			System.out.println("ciblage par le heros");
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0));
			return expression.replaceFirst(CIBLAGE_PAR_HEROS, ""+ciblage(0, nombres.get(0), nombres.get(1)));
		}
		
		// Ciblage par un event
		p = Pattern.compile(CIBLAGE_PAR_EVENT);
		m = p.matcher(expression);
		if (m.find()) {
			System.out.println("ciblage par un event");
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0));
			return expression.replaceFirst(CIBLAGE_PAR_HEROS, ""+ciblage(nombres.get(0), nombres.get(1), nombres.get(2)));
		}
		
		// Racine
		p = Pattern.compile(RACINAGE);
		m = p.matcher(expression);
		if (m.find()) {
			final int nombre = extraireLeNombre(m.group(0));
			return expression.replaceFirst(RACINAGE, ""+nombre);
		}
		
		// Valeur absolue
		p = Pattern.compile(ABSOLUTION);
		m = p.matcher(expression);
		if (m.find()) {
			final int nombre = extraireLeNombre(m.group(0));
			return expression.replaceFirst(ABSOLUTION, ""+nombre);
		}
		
		
		//--------------------//
		// Opérations unaires //
		//--------------------//
		
		// Négation
		p = Pattern.compile(NEGATION);
		m = p.matcher(expression);
		if (m.find()) {
			final int nombre = extraireLeNombre(m.group(0));
			return expression.replaceFirst(NEGATION, nombre==0 ? "1" : "0");
		}
		
		// Parenthèses
		p = Pattern.compile(PARENTHESAGE);
		m = p.matcher(expression);
		if (m.find()) {
			final int nombre = extraireLeNombre(m.group(0));
			return expression.replaceFirst(PARENTHESAGE, ""+nombre);
		}

		
		//--------------------------//
		// Opérations arithmétiques //
		//--------------------------//				
		
		// Puissance
		p = Pattern.compile(EXPONENTIATION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0));
			return expression.replaceFirst(EXPONENTIATION, ""+((int) Math.pow(nombres.get(0), nombres.get(1))));
		}
		
		// Multiplication
		p = Pattern.compile(MULTIPLICATION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0));
			return expression.replaceFirst(MULTIPLICATION, ""+(nombres.get(0)*nombres.get(1)));
		}
		
		// Division
		p = Pattern.compile(DIVISION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0));
			return expression.replaceFirst(DIVISION, ""+(nombres.get(0)/nombres.get(1)));
		}
		
		// Modulo
		p = Pattern.compile(MODULATION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0));
			return expression.replaceFirst(MODULATION, ""+(nombres.get(0)%nombres.get(1)));
		}
		
		// Addition
		p = Pattern.compile(ADDITION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0));
			return expression.replaceFirst(ADDITION, ""+(nombres.get(0)+nombres.get(1)));
		}
		
		// Soustraction
		p = Pattern.compile(SOUSTRACTION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0));
			return expression.replaceFirst(SOUSTRACTION, ""+(nombres.get(0)-nombres.get(1)));
		}
		
		
		//---------------------//
		// Relations d'égalité //
		//---------------------//
		
		// Egalité
		p = Pattern.compile(EGALISATION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0));
			return expression.replaceFirst(EGALISATION, nombres.get(0)==nombres.get(1) ? "1" : "0");
		}
		
		// Inférieur ou égal
		p = Pattern.compile(INFERIORATION_LARGE);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0));
			return expression.replaceFirst(INFERIORATION_LARGE, nombres.get(0)<=nombres.get(1) ? "1" : "0");
		}
		
		// Supérieur ou égal
		p = Pattern.compile(SUPERIORATION_LARGE);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0));
			return expression.replaceFirst(SUPERIORATION_LARGE, nombres.get(0)>=nombres.get(1) ? "1" : "0");
		}
		
		// Inférieur
		p = Pattern.compile(INFERIORATION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0));
			return expression.replaceFirst(INFERIORATION, nombres.get(0)<nombres.get(1) ? "1" : "0");
		}
		
		// Supérieur
		p = Pattern.compile(SUPERIORATION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0));
			return expression.replaceFirst(SUPERIORATION, nombres.get(0)>nombres.get(1) ? "1" : "0");
		}
		
		// Différent
		p = Pattern.compile(DIFFERENTIATION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0));
			return expression.replaceFirst(DIFFERENTIATION, nombres.get(0)!=nombres.get(1) ? "1" : "0");
		}
		
		//--------------------------------//
		// Opérations booléennes binaires //
		//--------------------------------//
		
		// Et
		p = Pattern.compile(ETATION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0));
			final String remplacement;
			if (nombres.get(0) == 0 || nombres.get(1) == 0) {
				remplacement = "0";
			} else {
				remplacement = "1";
			}
			return expression.replaceFirst(ETATION, remplacement);
		}
		
		// Ou
		p = Pattern.compile(OUATION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0));
			final String remplacement;
			if (nombres.get(0) == 0 && nombres.get(1) == 0) {
				remplacement = "0";
			} else {
				remplacement = "1";
			}
			return expression.replaceFirst(OUATION, remplacement);
		}
		
		
		LOG.error("Script impossible à interpréter : "+expression);
		return "0";
	}
	
	/**
	 * Remplacer la dernière occurence.
	 * @param expression dans laquelle on cherche une occurrence
	 * @param aReplacer occurrence à remplacer
	 * @param remplacement mot à mettre à la place
	 * @return expression modifiée
	 */
	public static String replaceLast(final String expression, final String aReplacer, final String remplacement) {
        return expression.replaceFirst("(?s)(.*)" + aReplacer, "$1" + remplacement);
    }
	
	/**
	 * Trouver le nombre situé dans une chaine de caractères.
	 * @param nombreBrut chaine de caractères contenant un nombre
	 * @return nombre contenu
	 */
	private static int extraireLeNombre(final String nombreBrut) {
		final Pattern p = Pattern.compile(NOMBRE);
		final Matcher m = p.matcher(nombreBrut);
		m.find();
		final String nombreExtrait = m.group(0);
		return (int) Integer.parseInt(nombreExtrait);
	}
	
	/**
	 * Trouver les nombres situés dans une chaine de caractères.
	 * @param brut chaine de caractères contenant des nombres
	 * @return nombres contenus
	 */
	private static ArrayList<Integer> extraireLesNombres(final String brut) {
		final Pattern p = Pattern.compile(NOMBRE);
		final Matcher m = p.matcher(brut);
		final ArrayList<Integer> nombres = new ArrayList<>();
		while (m.find()) {
			nombres.add(Integer.parseInt(m.group()));
		}
		return nombres;
	}

	@Override
	public final boolean estLieeAuHeros() {
		return false;
	}
	
	@Override
	public final String toString() {
		return "ConditionScript : "+script;
	}
	
	private int eventId() {
		if (this.modeTest) {
			return 18;
		} else {
			return this.page.event.id;
		}
	}
	
	private int coordonneeXEvent(final int idEvent) {
		if (this.modeTest) {
			return 15;
		} else {
			return this.page.event.map.eventsHash.get(idEvent).x / Main.TAILLE_D_UN_CARREAU;
		}
	}
	
	private int coordonneeYEvent(final int idEvent) {
		if (this.modeTest) {
			return 20;
		} else {
			return this.page.event.map.eventsHash.get(idEvent).y / Main.TAILLE_D_UN_CARREAU;
		}
	}
	
	private int coordonneeXHeros() {
		if (this.modeTest) {
			return 5;
		} else {
			return this.page.event.map.heros.x / Main.TAILLE_D_UN_CARREAU;
		}
	}
	
	private int coordonneeYHeros() {
		if (this.modeTest) {
			return 6;
		} else {
			return this.page.event.map.heros.y / Main.TAILLE_D_UN_CARREAU;
		}
	}
	
	private int vieEvent(final int idEvent) {
		if (this.modeTest) {
			return 3;
		} else {
			return this.page.event.map.eventsHash.get(idEvent).vies;
		}
	}
	
	private boolean ciblage(Integer idAttaquant, Integer idCible, Integer typeDeZone) {
		final Event attaquant = this.page.event.map.eventsHash.get(idAttaquant);
		final Event cible = this.page.event.map.eventsHash.get(idCible);
		return Hitbox.ZONES_D_ATTAQUE.get(typeDeZone).estDansZoneDAttaque(cible, attaquant);
	}

}
