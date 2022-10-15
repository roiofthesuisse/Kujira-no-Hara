package commandes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jeu.Partie;
import main.Commande;
import main.Main;
import map.LecteurMap;
import utilitaire.Maths;

/**
 * Appeler un script ruby.
 */
public class AppelerUnScript extends Commande implements CommandeEvent, CommandeMenu {
	// constantes
	private static final Logger LOG = LogManager.getLogger(AppelerUnScript.class);
	private static final String ESPACE = "(\\s)*";
	private static final String EVENT_ID = "@event_id";
	private static final String ATTENDRE_CET_EVENT = "wait_for_event(@event_id)";
	private static final String ATTENDRE_HEROS = "wait_for_event(0)";
	private static final String AFFICHER_UN_FACESET = "\\$game_temp.faceset = \"[0-9A-Za-z ]+\"";
	private static final String EFFACER_LE_FACESET = "$game_temp.faceset = nil";
	private static final String POSITIF = ESPACE+"[0-9]+"+ESPACE;
	private static final String RELATIF = ESPACE + "-?[0-9]+" + ESPACE;
	private static final String DECIMAL = ESPACE + "-?[0-9]+(\\.[0-9]+)?" + ESPACE;
	private static final String ATTENDRE_UN_EVENT = "wait_for_event\\(" + POSITIF + "\\)";
	private static final String INVOQUER = "invoquer\\(" +POSITIF + "," + POSITIF + "," +POSITIF + "," + POSITIF + "\\)";
	private static final String COORD_X_EVENT = "\\$game_map\\.events\\[" + POSITIF + "\\]\\.x";
	private static final String COORD_Y_EVENT = "\\$game_map\\.events\\[" + POSITIF + "\\]\\.y";
	private static final String VARIABLE = "\\$game_variables\\[" + POSITIF + "\\]";
	private static final String TRANSFORMER = "\\$game_map\\.events\\[" + POSITIF + "\\]\\.transform\\(" + POSITIF + ","
			+ POSITIF + "\\)";
	private static final String MODIFIER_OFFSET_X = "\\$game_map\\.events\\[" + POSITIF + "\\]\\.offset_x\\(" + DECIMAL + "\\)";
	private static final String MODIFIER_OFFSET_Y = "\\$game_map\\.events\\[" + POSITIF + "\\]\\.offset_y\\(" + DECIMAL + "\\)";

	// calculs
	private static final String ADDITION_DECIMAL = DECIMAL + "\\+" + DECIMAL;
	private static final String SOUSTRACTION_DECIMAL = DECIMAL + "-" + DECIMAL;
	private static final String MULTIPLICATION_DECIMAL = DECIMAL + "\\*" + DECIMAL;
	private static final String DIVISION_DECIMAL = DECIMAL + "/" + DECIMAL;
	private static final String MODULATION_DECIMAL = DECIMAL + "%" + DECIMAL;
	private static final String EXPONENTIATION = RELATIF + "\\*\\*" + RELATIF;
	private static final String ADDITION_RELATIF = RELATIF + "\\+" + RELATIF;
	private static final String SOUSTRACTION_RELATIF = RELATIF + "-" + RELATIF;
	private static final String MULTIPLICATION_RELATIF = RELATIF + "\\*" + RELATIF;
	private static final String DIVISION_RELATIF = RELATIF + "/" + RELATIF;
	private static final String MODULATION_RELATIF = RELATIF + "%" + RELATIF;
	private static final String SINUS = "Math\\.sin\\(" + DECIMAL + "\\)";
	private static final String COSINUS = "Math\\.cos\\(" + DECIMAL + "\\)";
	private static final String PI = "Math::PI";
	private static final String FRAME_ACTUELLE = "Graphics.frame_count";
	private static final String TEMPS_ACTUEL = "Time.now";
	private static final String CONVERSION_DECIMAL = ".to_f";
	private static final String PARENTHESES = "\\(" + DECIMAL + "\\)";
	
	public final String script;
	private ArrayList<Commande> commandes;
	private int curseur;
	public boolean interpretationImplementee;

	/**
	 * Constructeur explicite
	 * 
	 * @param script a executer
	 */
	public AppelerUnScript(final String script) {
		this.script = script;
		this.commandes = null;
		this.curseur = 0;
	}

	/**
	 * Constructeur generique
	 * 
	 * @param parametres liste de parametres issus de JSON
	 */
	public AppelerUnScript(final HashMap<String, Object> parametres) {
		this((String) parametres.get("script"));
	}

	@Override
	public final int executer(final int curseurActuel, final List<Commande> commandesEvent) {
		// Initialisation
		if (this.commandes == null) {
			// On parse le script en liste de Commandes
			this.commandes = new ArrayList<>();
			// Convertir toutes les expressions du script en Commandes
			String[] expressions = this.script.split("/n");
			for (String expression : expressions) {
				Commande commande = traiter(expression);
				if (commande != null) {
					commande.page = this.page; // on rend la Commande au courant de qui est sa Page
					this.commandes.add(commande);
				}
			}
			this.curseur = 0;
		}

		// Executer les Commandes equivalentes au script ruby
		if (this.commandes != null && this.commandes.size() > 0) {
			// On deja parse le script en liste de Commandes
			this.interpretationImplementee = true;

			// On continue d'executer les Commandes de la liste
			// Il y a encore des Commandes a lire
			boolean laCommandeEstInstantannee = true;
			while (laCommandeEstInstantannee && this.curseur < this.commandes.size()) {
				int curseurAvant = this.curseur;
				this.curseur = this.commandes.get(curseur).executer(this.curseur, this.commandes);
				int curseurApres = this.curseur;
				// Si la Commande prend du temps, on la continuera plus tard
				laCommandeEstInstantannee = (curseurAvant != curseurApres);
			}

			// Est-ce qu'on a fini de lire toutes les Commandes du script ?
			if (this.curseur < this.commandes.size()) {
				// Il en reste
				return curseurActuel;
			} else {
				// Il n'y a plus de Commandes a lire
				this.curseur = 0;
				// On efface les commandes parsees
				// Elles seront reparsees la prochaine fois
				this.commandes = null;
				return curseurActuel + 1;
			}
		} else {
			// Impossible de parser le script !
			this.interpretationImplementee = false;
			LOG.error("L'appel de ce script n'est pas encore implemente ! " + this.script);
			// On efface les commandes parsees
			// Elles seront reparsees la prochaine fois
			this.commandes = null;
			return curseurActuel + 1;
		}
	}

	/**
	 * interpreter un script ruby.
	 * 
	 * @param expression (en ruby)
	 * @return une chaine de caractere qui est un nombre lorsque l'interpretation
	 *         est terminee.
	 */
	private Commande traiter(String expression) {

		LOG.trace("Traitement de l'expression : " + expression);

		Pattern p;
		Matcher m;

		// -----------//
		// Commandes //
		// -----------//
		// Attendre cet Event / le Heros
		Integer idEventAAttendre = null;
		if (ATTENDRE_CET_EVENT.equals(this.script)) {
			// Attendre la fin des deplacements de cet Events
			idEventAAttendre = this.page.event.id;
			LOG.trace("Script ruby reconnu : ATTENDRE_CET_EVENT");
		} else if (ATTENDRE_HEROS.equals(script)) {
			// Attendre la fin du Deplacement du Heros
			idEventAAttendre = this.page.event.map.heros.id;
			LOG.trace("Script ruby reconnu : ATTENDRE_LE_HEROS");
		}
		if (idEventAAttendre != null) {
			// Attendre l'Event
			final AttendreLaFinDesDeplacements attendreEvent = new AttendreLaFinDesDeplacements(idEventAAttendre);
			attendreEvent.page = this.page; // On dit a la commande qui est son Event
			return attendreEvent;
		}

		// Attendre un Event
		p = Pattern.compile(ATTENDRE_UN_EVENT);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : ATTENDRE_UN_EVENT");
			int idEvent = extraireLeNombre(POSITIF, m.group(0));
			return new AttendreLaFinDesDeplacements(idEvent);
		}

		// Effacer le faceset
		if (EFFACER_LE_FACESET.equals(this.script)) {
			return new EffacerLeFaceset();
		}
		// Afficher un faceset
		p = Pattern.compile(AFFICHER_UN_FACESET);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : AFFICHER_UN_FACESET");
			String nomFaceset = extraireLeTexte(m.group(0));
			return new AfficherUnFaceset(nomFaceset);
		}

		// Invoquer un Event
		p = Pattern.compile(INVOQUER);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : ATTENDRE_UN_EVENT");
			ArrayList<Integer> nombres = extraireLesNombres(POSITIF, m.group(0));
			return new InvoquerUnEvent(nombres.get(0), nombres.get(1), nombres.get(2), nombres.get(3));
		}

		// Transformer un Event
		p = Pattern.compile(TRANSFORMER);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : TRANSFORMER");
			ArrayList<Integer> nombres = extraireLesNombres(POSITIF, m.group(0));
			return new TransformerUnEvent(nombres.get(0), nombres.get(1), nombres.get(2));
		}

		// Offset X
		p = Pattern.compile(MODIFIER_OFFSET_X);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : MODIFIER_OFFSET_X");
			ArrayList<Integer> nombres = extraireLesNombres(RELATIF, m.group(0));
			return new ModifierOffsetX(nombres.get(0), nombres.get(1));
		}

		// Offset Y
		p = Pattern.compile(MODIFIER_OFFSET_Y);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : MODIFIER_OFFSET_Y");
			ArrayList<Integer> nombres = extraireLesNombres(RELATIF, m.group(0));
			return new ModifierOffsetY(nombres.get(0), nombres.get(1));
		}

		// Autre
		// TODO restent a parser :
		// $game_map.events[@event_id].x == $game_variables[181] &&
		// $game_map.events[@event_id].y == $game_variables[182]

		// invoquer($game_map.events[1].x, $game_map.events[1].y, 21, 54)

		// $game_map.events[@event_id].transform(511, 6)

		// event = invoquer(15, 13, 457, 7)\nevent.jump($game_player.x-event.x,
		// $game_player.y-event.y)

		// $game_map.events[@event_id].life -= 1

		// $game_temp.animations.push([23, true, $game_map.events[@event_id].x,
		// $game_map.events[@event_id].y])

		// x = y = 0\ncase $game_player.direction\nwhen 2; y =
		// $game_map.events[@event_id].y - $game_player.y - 1\nwhen 4; x =
		// $game_map.events[@event_id].x - $game_player.x + 1\nwhen 6; x =
		// $game_map.events[@event_id].x - $game_player.x - 1\nwhen 8; y =
		// $game_map.events[@event_id].y - $game_player.y +
		// 1\nend\n$game_player.jump(x,y)

		// ------------- //
		// Remplacements //
		// ------------- //

		// Trim
		if (expression.startsWith(" ") || expression.endsWith(" ")) {
			LOG.trace("trim");
			return traiter(expression.trim());
		}

		// Coordonnee x et y du Heros
		if (expression.contains("$game_player.y") || expression.contains("$game_player.x")) {
			LOG.trace("Script ruby reconnu : Coordonnee x et y du Heros");
			Integer xHeros = this.page.event.map.heros.x;
			Integer yHeros = this.page.event.map.heros.y;
			return traiter(expression.replace("$game_player.y", yHeros.toString()).replace("$game_player.x",
					xHeros.toString()));
		}

		// Id event
		if (expression.contains(EVENT_ID)) {
			LOG.trace("Script ruby reconnu : EVENT_ID");
			String eventId = Integer.toString(this.page.event.id);
			return traiter(expression.replace(EVENT_ID, eventId));
		}

		// Frame actuelle
		if (expression.contains(FRAME_ACTUELLE)) {
			LOG.trace("Script ruby reconnu : FRAME_ACTUELLE");
			String frameActuelle = Integer.toString(this.page.event.map.lecteur.frameActuelle);
			return traiter(expression.replace(FRAME_ACTUELLE, frameActuelle));
		}

		// Temps actuel
		if (expression.contains(TEMPS_ACTUEL)) {
			LOG.trace("Script ruby reconnu : TEMPS_ACTUEL");
			String frameActuelle = Double.toString(this.page.event.map.lecteur.frameActuelle / 20.0);
			return traiter(expression.replace(TEMPS_ACTUEL, frameActuelle));
		}

		// Pi
		if (expression.contains(PI)) {
			LOG.trace("Script ruby reconnu : PI");
			return traiter(expression.replace(PI, "3.14159"));
		}

		// Conversion en decimal
		if (expression.contains(CONVERSION_DECIMAL)) {
			LOG.trace("Script ruby reconnu : CONVERSION_DECIMAL");
			return traiter(expression.replace(CONVERSION_DECIMAL, ""));
		}


		// Coordonnee x d'un Event
		p = Pattern.compile(COORD_X_EVENT);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : COORD_X_EVENT");
			String aRemplacer = m.group(0);
			int idEvent = extraireLeNombre(POSITIF, aRemplacer);
			int valeurVariable = getXEvent(idEvent);
			return traiter(expression.replace(aRemplacer, Integer.toString(valeurVariable)));
		}

		// Coordonnee y d'un Event
		p = Pattern.compile(COORD_Y_EVENT);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : COORD_Y_EVENT");
			String aRemplacer = m.group(0);
			int idEvent = extraireLeNombre(POSITIF, aRemplacer);
			int valeurVariable = getYEvent(idEvent);
			return traiter(expression.replace(aRemplacer, Integer.toString(valeurVariable)));
		}

		// Variable du jeu
		p = Pattern.compile(VARIABLE);
		m = p.matcher(expression);
		if(m.find()) {
			LOG.trace("Script ruby reconnu : VARIABLE");
			String aRemplacer = m.group(0);
			int numeroVariable = extraireLeNombre(POSITIF, aRemplacer);
			int valeurVariable = getValeurVariable(numeroVariable);
			return traiter(expression.replace(aRemplacer, Integer.toString(valeurVariable)));
		}

		// Sinus
		p = Pattern.compile(SINUS);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : SINUS");
			String aRemplacer = m.group(0);
			double nombre = extraireLeNombreDecimal(aRemplacer);
			return traiter(expression.replace(aRemplacer, String.valueOf(Maths.round(Math.sin(nombre), 3))));
		}

		// Cosinus
		p = Pattern.compile(COSINUS);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : COSINUS");
			String aRemplacer = m.group(0);
			double nombre = extraireLeNombreDecimal(aRemplacer);
			return traiter(expression.replace(aRemplacer, String.valueOf(Maths.round(Math.cos(nombre), 3))));
		}

		// Multiplication
		p = Pattern.compile(MULTIPLICATION_DECIMAL);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : MULTIPLICATION_DECIMAL");
			String aRemplacer = m.group(0);
			ArrayList<Double> nombres = extraireLesNombresDecimaux(aRemplacer);
			double valeurVariable = nombres.get(0) * nombres.get(1);
			return traiter(expression.replace(aRemplacer, Double.toString(Maths.round(valeurVariable, 3))));
		}
		p = Pattern.compile(MULTIPLICATION_RELATIF);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : MULTIPLICATION_RELATIF");
			String aRemplacer = m.group(0);
			ArrayList<Integer> nombres = extraireLesNombres(RELATIF, aRemplacer);
			int valeurVariable = nombres.get(0) * nombres.get(1);
			return traiter(expression.replace(aRemplacer, Integer.toString(valeurVariable)));
		}

		// Division
		p = Pattern.compile(DIVISION_DECIMAL);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : DIVISION_DECIMAL");
			String aRemplacer = m.group(0);
			ArrayList<Double> nombres = extraireLesNombresDecimaux(aRemplacer);
			double valeurVariable = nombres.get(0) / nombres.get(1);
			return traiter(expression.replace(aRemplacer, Double.toString(Maths.round(valeurVariable, 3))));
		}
		p = Pattern.compile(DIVISION_RELATIF);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : DIVISION_RELATIF");
			String aRemplacer = m.group(0);
			ArrayList<Integer> nombres = extraireLesNombres(RELATIF, aRemplacer);
			int valeurVariable = nombres.get(0) / nombres.get(1);
			return traiter(expression.replace(aRemplacer, Integer.toString(valeurVariable)));
		}

		// Exponentiation
		p = Pattern.compile(EXPONENTIATION);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : EXPONENTIATION");
			String aRemplacer = m.group(0);
			ArrayList<Integer> nombres = extraireLesNombres(RELATIF, aRemplacer);
			int valeurVariable = (int) Math.round(Math.pow(nombres.get(0), nombres.get(1)));
			return traiter(expression.replace(aRemplacer, Integer.toString(valeurVariable)));
		}

		// Modulo
		p = Pattern.compile(MODULATION_DECIMAL);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : MODULATION_DECIMAL");
			String aRemplacer = m.group(0);
			ArrayList<Double> nombres = extraireLesNombresDecimaux(aRemplacer);
			double valeurVariable = nombres.get(0) % nombres.get(1);
			return traiter(expression.replace(aRemplacer, Double.toString(Maths.round(valeurVariable, 3))));
		}
		p = Pattern.compile(MODULATION_RELATIF);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : MODULATION_RELATIF");
			String aRemplacer = m.group(0);
			ArrayList<Integer> nombres = extraireLesNombres(RELATIF, aRemplacer);
			int valeurVariable = nombres.get(0) % nombres.get(1);
			return traiter(expression.replace(aRemplacer, Integer.toString(valeurVariable)));
		}

		// Addition
		p = Pattern.compile(ADDITION_DECIMAL);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : ADDITION_DECIMAL");
			String aRemplacer = m.group(0);
			ArrayList<Double> nombres = extraireLesNombresDecimaux(aRemplacer);
			double valeurVariable = nombres.get(0) + nombres.get(1);
			return traiter(expression.replace(aRemplacer, Double.toString(Maths.round(valeurVariable, 3))));
		}
		p = Pattern.compile(ADDITION_RELATIF);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : ADDITION_RELATIF");
			String aRemplacer = m.group(0);
			ArrayList<Integer> nombres = extraireLesNombres(RELATIF, aRemplacer);
			int valeurVariable = nombres.get(0) + nombres.get(1);
			return traiter(expression.replace(aRemplacer, Integer.toString(valeurVariable)));
		}

		// Soustraction
		p = Pattern.compile(SOUSTRACTION_DECIMAL);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : SOUSTRACTION_DECIMAL");
			String aRemplacer = m.group(0);
			ArrayList<Double> nombres = extraireLesNombresDecimaux(aRemplacer);
			double valeurVariable = nombres.get(0) - nombres.get(1);
			return traiter(expression.replace(aRemplacer, Double.toString(Maths.round(valeurVariable, 3))));
		}
		p = Pattern.compile(SOUSTRACTION_RELATIF);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : SOUSTRACTION_RELATIF");
			String aRemplacer = m.group(0);
			ArrayList<Integer> nombres = extraireLesNombres(RELATIF, aRemplacer);
			int valeurVariable = nombres.get(0) - nombres.get(1);
			return traiter(expression.replace(aRemplacer, Integer.toString(valeurVariable)));
		}

		// Retirer les parentheses
		// Doit etre le dernier remplacement effectue, car peut endommager une fonction
		p = Pattern.compile(PARENTHESES);
		m = p.matcher(expression);
		if (m.find()) {
			LOG.trace("Script ruby reconnu : PARENTHESES");
			String aRemplacer = m.group(0);
			double nombre = extraireLeNombreDecimal(aRemplacer);
			return traiter(expression.replace(aRemplacer, String.valueOf(nombre)));
		}
		
		// Pas encore implemente
		LOG.error("Script impossible a interpreter : " + expression);
		this.interpretationImplementee = false;
		return null;
	}

	/**
	 * Trouver le nombre situe dans une chaine de caracteres.
	 * 
	 * @param nombreBrut chaine de caracteres contenant un nombre
	 * @return nombre contenu
	 */
	private static int extraireLeNombre(final String positifOuRelatif, final String nombreBrut) {
		final Pattern p = Pattern.compile(positifOuRelatif);
		final Matcher m = p.matcher(nombreBrut);
		m.find();
		final String nombreExtrait = m.group(0).trim();
		return Integer.parseInt(nombreExtrait);
	}

	/**
	 * Trouver le nombre situe dans une chaine de caracteres.
	 * 
	 * @param nombreBrut chaine de caracteres contenant un nombre
	 * @return nombre contenu
	 */
	private static double extraireLeNombreDecimal(final String nombreBrut) {
		final Pattern p = Pattern.compile(DECIMAL);
		final Matcher m = p.matcher(nombreBrut);
		m.find();
		final String nombreExtrait = m.group(0).trim();
		return Double.parseDouble(nombreExtrait);
	}

	/**
	 * Trouver les nombres situes dans une chaine de caracteres.
	 * 
	 * @param brut             chaine de caracteres contenant des nombres
	 * @param relatifOuPositif les nombres attendus sont-ils positifs ou relatifs ?
	 * @return nombres contenus
	 */
	private static ArrayList<Integer> extraireLesNombres(final String positifOuRelatif, final String brut) {
		final Pattern p = Pattern.compile(positifOuRelatif);
		final Matcher m = p.matcher(brut);
		final ArrayList<Integer> nombres = new ArrayList<>();
		while (m.find()) {
			final String nombre = m.group().trim();
			if ("nil".equals(nombre)) {
				nombres.add(null);
			} else {
				nombres.add(Integer.parseInt(nombre));
			}
		}
		return nombres;
	}
	
	private static ArrayList<Double> extraireLesNombresDecimaux(final String brut) {
		final Pattern p = Pattern.compile(DECIMAL);
		final Matcher m = p.matcher(brut);
		final ArrayList<Double> nombres = new ArrayList<>();
		while (m.find()) {
			final String nombre = m.group().trim();
			if ("nil".equals(nombre)) {
				nombres.add(null);
			} else {
				nombres.add(Double.parseDouble(nombre));
			}
		}
		return nombres;
	}

	/**
	 * Etraire le texte entre guillemets
	 * 
	 * @param brut chaine de caracteres contenant des guillemets
	 * @return texte situe entre les guillemets
	 */
	private static String extraireLeTexte(final String brut) {
		return brut.split("\"")[1];
	}

	/**
	 * Obtenir la valeur d'une Variable du Jeu.
	 * Renvoyer une fausse valeur si le Jeu n'est pas instanci�.
	 * @param numeroVariable
	 * @return
	 */
	private static int getValeurVariable(int numeroVariable) {
		Partie partie = getPartieActuelle();
		if(partie != null) {
			return partie.variables[numeroVariable];
		}
		return 7;
	}

	private int getXEvent(int idEvent) {
		Partie partie = getPartieActuelle();
		if (partie != null) {
			return ((LecteurMap) Main.lecteur).map.events.get(idEvent).x;
		}
		return 14;
	}

	private int getYEvent(int idEvent) {
		Partie partie = getPartieActuelle();
		if (partie != null) {
			return ((LecteurMap) Main.lecteur).map.events.get(idEvent).y;
		}
		return 17;
	}

	@Override
	public final String toString() {
		return "AppelerUnScript : " + script;
	}

}
