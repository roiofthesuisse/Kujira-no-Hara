package conditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import commandes.CommandeEvent;
import main.Main;
import map.Event;
import map.Hitbox;

/**
 * Condition basée sur l'interprétation d'un script ruby.
 */
public class ConditionScript extends Condition implements CommandeEvent {
	//constantes
	private static final Logger LOG = LogManager.getLogger(ConditionScript.class);
	
	private static final String ESPACE = "(\\s)*";
	private static final String TRUE = "true";
	private static final String FALSE = "false";
	
	private static final String HEROS = "\\$game_player\\.";
	private static final String X = "x";
	private static final String Y = "y";
	private static final String EVENT_DEBUT = "\\$game_map\\.events\\[";
	private static final String EVENT_FIN = "\\]\\.";
	private static final String EVENT_ID = "@event_id";
	private static final String RELATIF = "-?[0-9]+";
	private static final String POSITIF = "[0-9]+";
	private static final String POSITIF_OU_NIL = "([0-9]+|nil)";
	private static final String CHAINE_OU_NIL = "(\".*\"|nil)";
	private static final String COORD_EVENT_X = EVENT_DEBUT+POSITIF+EVENT_FIN+X;
	private static final String COORD_EVENT_Y = EVENT_DEBUT+POSITIF+EVENT_FIN+Y;
	private static final String COORD_HEROS_X = HEROS+X;
	private static final String COORD_HEROS_Y = HEROS+Y;
	private static final String VARIABLE_DEBUT = "\\$game_variables\\[";
	private static final String VARIABLE_FIN = "\\]";
	private static final String VARIABILISATION = VARIABLE_DEBUT+POSITIF+VARIABLE_FIN;
	private static final String RACINE_DEBUT = "Math\\.sqrt\\(";
	private static final String RACINE_FIN = "\\)\\.round";
	private static final String RACINAGE = RACINE_DEBUT+ESPACE+POSITIF+ESPACE+RACINE_FIN;
	private static final String HYPOTHENUSE_DEBUT = "Math\\.hypot\\(";
	private static final String HYPOTHENUSE_MILIEU = ",";
	private static final String HYPOTHENUSE_FIN = "\\)\\.round";
	private static final String HYPOTHENUSAGE = HYPOTHENUSE_DEBUT+ESPACE+RELATIF+ESPACE+HYPOTHENUSE_MILIEU+ESPACE+RELATIF+ESPACE+HYPOTHENUSE_FIN;
	private static final String ABSOLU_DEBUT = "\\(";
	private static final String ABSOLU_FIN = "\\)\\.abs";
	private static final String ABSOLUTION = ABSOLU_DEBUT+ESPACE+RELATIF+ESPACE+ABSOLU_FIN;
	private static final String VIE_EVENT_FIN = "\\]\\.life";
	private static final String VITALISATION = EVENT_DEBUT + POSITIF + VIE_EVENT_FIN;
	private static final String CIBLAGE = "target_in_da_zone?\\("+POSITIF+","+ESPACE+POSITIF+"\\)";
	private static final String CIBLAGE_PAR_HEROS = HEROS+CIBLAGE;
	private static final String CIBLAGE_PAR_EVENT = EVENT_DEBUT+POSITIF+EVENT_FIN+CIBLAGE;
	private static final String APPARENCE = "lolilol\\(";
	private static final String APPARENCIATION = EVENT_DEBUT+POSITIF+EVENT_FIN+APPARENCE+CHAINE_OU_NIL+","+ESPACE+POSITIF_OU_NIL+","+ESPACE+POSITIF_OU_NIL+"\\)";
	
	private static final String ET = "&&";
	private static final String ETATION = RELATIF+ESPACE+ET+ESPACE+RELATIF;
	private static final String OU = "\\|\\|";
	private static final String OUATION = RELATIF+ESPACE+OU+ESPACE+RELATIF;
	private static final String NEGATION = "!"+ESPACE+RELATIF;
	private static final String PARENTHESAGE = "\\("+ESPACE+RELATIF+ESPACE+"\\)";
	
	private static final String EGAL = "==";
	private static final String EGALISATION = RELATIF+ESPACE+EGAL+ESPACE+RELATIF;
	private static final String INFEGAL = "<=";
	private static final String INFERIORATION_LARGE = RELATIF+ESPACE+INFEGAL+ESPACE+RELATIF;
	private static final String SUPEGAL = ">=";
	private static final String SUPERIORATION_LARGE = RELATIF+ESPACE+SUPEGAL+ESPACE+RELATIF;
	private static final String INFERIEUR = "<";
	private static final String INFERIORATION = RELATIF+ESPACE+INFERIEUR+ESPACE+RELATIF;
	private static final String SUPERIEUR = ">";
	private static final String SUPERIORATION = RELATIF+ESPACE+SUPERIEUR+ESPACE+RELATIF;
	private static final String DIFFERENT = "!=";
	private static final String DIFFERENTIATION = RELATIF+ESPACE+DIFFERENT+ESPACE+RELATIF;
	
	private static final String PLUS = "\\+";
	private static final String ADDITION = RELATIF+ESPACE+PLUS+ESPACE+RELATIF;
	private static final String MOINS = "-";
	private static final String SOUSTRACTION = RELATIF+ESPACE+MOINS+ESPACE+RELATIF;
	private static final String FOIS = "\\*";
	private static final String MULTIPLICATION = RELATIF+ESPACE+FOIS+ESPACE+RELATIF;
	private static final String SLASH = "/";
	private static final String DIVISION = RELATIF+ESPACE+SLASH+ESPACE+RELATIF;
	private static final String POURCENT = "%";
	private static final String MODULATION = RELATIF+ESPACE+POURCENT+ESPACE+RELATIF;
	private static final String EXPOSANT = "\\*\\*";
	private static final String EXPONENTIATION = RELATIF+ESPACE+EXPOSANT+ESPACE+RELATIF;
	
	private String script;
	private final boolean modeTest;
	public boolean interpretationImplementee = true;
	
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
	

		//$game_map.events[2].lolilol("geyser character", nil, nil)

		//$game_map.events[@event_id].araignee_brulee?()
		
		//$game_player.prout("Jiyounasu AttaqueEpee character", nil, 2)

		//$game_map.events[@event_id].lolilol(nil, nil, 1) || $game_map.events[@event_id].lolilol(nil, nil, 3)
		
		//($game_player.target_in_da_zone?(@event_id, 2) && $game_player.prout("Jiyounasu AttaqueEpee character", nil, 2)) || ($game_player.target_in_da_zone?(@event_id, 1) && $game_player.prout("Jiyounasu AttaqueTorche character", nil, nil)) || ($game_player.target_in_da_zone?(@event_id, 2) && $game_player.prout("Jiyounasu AttaqueEpee character", nil, 3))
		
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
		
		// Variable
		p = Pattern.compile(VARIABILISATION);
		m = p.matcher(expression);
		if (m.find()) {
			System.out.println("variable");
			final Integer nombre = extraireLeNombre(m.group(0));
			return expression.replaceFirst(VARIABILISATION, ""+variable(nombre));
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
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0), POSITIF);
			return expression.replaceFirst(CIBLAGE_PAR_HEROS, ""+ciblage(0, nombres.get(0), nombres.get(1)));
		}
		
		// Ciblage par un event
		p = Pattern.compile(CIBLAGE_PAR_EVENT);
		m = p.matcher(expression);
		if (m.find()) {
			System.out.println("ciblage par un event");
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0), POSITIF);
			return expression.replaceFirst(CIBLAGE_PAR_EVENT, ""+ciblage(nombres.get(0), nombres.get(1), nombres.get(2)));
		}
		
		// Racine
		p = Pattern.compile(RACINAGE);
		m = p.matcher(expression);
		if (m.find()) {
			final int nombre = extraireLeNombre(m.group(0));
			final int racine = ((int) Math.sqrt(nombre));
			return expression.replaceFirst(RACINAGE, ""+racine);
		}
		
		// Hypothenuse
		p = Pattern.compile(HYPOTHENUSAGE);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(), RELATIF);
			final int hypothenuse = ((int) Math.sqrt(nombres.get(0)*nombres.get(0)+nombres.get(1)*nombres.get(1)));
			return expression.replaceFirst(HYPOTHENUSAGE, ""+hypothenuse);
		}
		
		// Valeur absolue
		p = Pattern.compile(ABSOLUTION);
		m = p.matcher(expression);
		if (m.find()) {
			final int nombre = extraireLeNombre(m.group(0));
			return expression.replaceFirst(ABSOLUTION, ""+nombre);
		}
		
		// Apparence, direction, animation d'un Event
		p = Pattern.compile(APPARENCIATION);
		m = p.matcher(expression);
		if (m.find()) {
			System.out.println("apparence, direction, animation de l'event");
			final String chaine = extraireLaChaineOuNil(m.group(0));
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0), POSITIF_OU_NIL);
			return expression.replaceFirst(APPARENCIATION, ""+apparenceDeLEvent(nombres.get(0), chaine, nombres.get(nombres.size()-1), nombres.get(nombres.size()-2)));
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
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0), RELATIF);
			return expression.replaceFirst(EXPONENTIATION, ""+((int) Math.pow(nombres.get(0), nombres.get(1))));
		}
		
		// Multiplication
		p = Pattern.compile(MULTIPLICATION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0), RELATIF);
			return expression.replaceFirst(MULTIPLICATION, ""+(nombres.get(0)*nombres.get(1)));
		}
		
		// Division
		p = Pattern.compile(DIVISION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0), RELATIF);
			return expression.replaceFirst(DIVISION, ""+(nombres.get(0)/nombres.get(1)));
		}
		
		// Modulo
		p = Pattern.compile(MODULATION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0), RELATIF);
			return expression.replaceFirst(MODULATION, ""+(nombres.get(0)%nombres.get(1)));
		}
		
		// Addition
		p = Pattern.compile(ADDITION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0), RELATIF);
			return expression.replaceFirst(ADDITION, ""+(nombres.get(0)+nombres.get(1)));
		}
		
		// Moins moins
		p = Pattern.compile("-"+ESPACE+"-");
		m = p.matcher(expression);
		if (m.find()) {
			return expression.replaceFirst("-"+ESPACE+"-", "");
		}
		
		// Soustraction
		p = Pattern.compile(SOUSTRACTION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0), POSITIF);
			return expression.replaceFirst(SOUSTRACTION, ""+(nombres.get(0)-nombres.get(1)));
		}
		
		
		//---------------------//
		// Relations d'égalité //
		//---------------------//
		
		// Egalité
		p = Pattern.compile(EGALISATION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0), RELATIF);
			return expression.replaceFirst(EGALISATION, nombres.get(0)==nombres.get(1) ? "1" : "0");
		}
		
		// Inférieur ou égal
		p = Pattern.compile(INFERIORATION_LARGE);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0), RELATIF);
			return expression.replaceFirst(INFERIORATION_LARGE, nombres.get(0)<=nombres.get(1) ? "1" : "0");
		}
		
		// Supérieur ou égal
		p = Pattern.compile(SUPERIORATION_LARGE);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0), RELATIF);
			return expression.replaceFirst(SUPERIORATION_LARGE, nombres.get(0)>=nombres.get(1) ? "1" : "0");
		}
		
		// Inférieur
		p = Pattern.compile(INFERIORATION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0), RELATIF);
			return expression.replaceFirst(INFERIORATION, nombres.get(0)<nombres.get(1) ? "1" : "0");
		}
		
		// Supérieur
		p = Pattern.compile(SUPERIORATION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0), RELATIF);
			return expression.replaceFirst(SUPERIORATION, nombres.get(0)>nombres.get(1) ? "1" : "0");
		}
		
		// Différent
		p = Pattern.compile(DIFFERENTIATION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0), RELATIF);
			return expression.replaceFirst(DIFFERENTIATION, nombres.get(0)!=nombres.get(1) ? "1" : "0");
		}
		
		//--------------------------------//
		// Opérations booléennes binaires //
		//--------------------------------//
		
		// Et
		p = Pattern.compile(ETATION);
		m = p.matcher(expression);
		if (m.find()) {
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0), RELATIF);
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
			final ArrayList<Integer> nombres = extraireLesNombres(m.group(0), RELATIF);
			final String remplacement;
			if (nombres.get(0) == 0 && nombres.get(1) == 0) {
				remplacement = "0";
			} else {
				remplacement = "1";
			}
			return expression.replaceFirst(OUATION, remplacement);
		}
		
		
		LOG.error("Script impossible à interpréter : "+expression);
		this.interpretationImplementee = false;
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
		final Pattern p = Pattern.compile(RELATIF);
		final Matcher m = p.matcher(nombreBrut);
		m.find();
		final String nombreExtrait = m.group(0);
		return (int) Integer.parseInt(nombreExtrait);
	}
	
	/**
	 * Trouver les nombres situés dans une chaine de caractères.
	 * @param brut chaine de caractères contenant des nombres
	 * @param relatifOuPositif les nombres attendus sont-ils positifs ou relatifs ?
	 * @return nombres contenus
	 */
	private static ArrayList<Integer> extraireLesNombres(final String brut, final String relatifOuPositif) {
		final Pattern p = Pattern.compile(relatifOuPositif);
		final Matcher m = p.matcher(brut);
		final ArrayList<Integer> nombres = new ArrayList<>();
		while (m.find()) {
			final String nombre = m.group();
			if ("nil".equals(nombre)) {
				nombres.add(null);
			} else {
				nombres.add(Integer.parseInt(nombre));
			}
		}
		return nombres;
	}

	/**
	 * Trouver la chaine entre guillemets (ou nil) situee dans une chaine de caractères.
	 * @param chaineBrute chaine de caractères contenant la chaine entre guillemets
	 * @return chaine entre guillemets contenue
	 */
	private static String extraireLaChaineOuNil(final String chaineBrute) {
		final Pattern p = Pattern.compile(CHAINE_OU_NIL);
		final Matcher m = p.matcher(chaineBrute);
		m.find();
		String chaineExtraite = m.group(0);
		if ("nil".equals(chaineExtraite)) {
			chaineExtraite = null;
		}
		return chaineExtraite;
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
	
	private int variable(final int idVariable) {
		if (this.modeTest) {
			return 10;
		} else {
			return Main.getPartieActuelle().variables[idVariable];
		}
	}
	
	private boolean ciblage(Integer idAttaquant, Integer idCible, Integer typeDeZone) {
		final Event attaquant = this.page.event.map.eventsHash.get(idAttaquant);
		final Event cible = this.page.event.map.eventsHash.get(idCible);
		return Hitbox.ZONES_D_ATTAQUE.get(typeDeZone).estDansZoneDAttaque(cible, attaquant);
	}
	
	private boolean apparenceDeLEvent(Integer idCible, String apparence, Integer direction, Integer animation) {
		final Event cible = this.page.event.map.eventsHash.get(idCible);
		if (apparence != null) {
			if (!cible.pageDApparence.nomImage.equals(apparence.replace("\"", ""))) {
				return false;
			}
		}
		if (direction != null) {
			if (cible.direction != direction.intValue()) {
				return false;
			}
		}
		if (animation != null) {
			if (cible.animation != animation.intValue()) {
				return false;
			}
		}
		return true;
	}

}
