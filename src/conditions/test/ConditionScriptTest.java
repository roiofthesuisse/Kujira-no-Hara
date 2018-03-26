package conditions.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import conditions.ConditionScript;

/**
 * Tester la classe ConditionScript
 */
public class ConditionScriptTest {
	//constantes
	private static final Logger LOG = LogManager.getLogger(ConditionScriptTest.class);
	
	/**
	 * Tester si la condition fonctionne correctement.
	 */
	@Test
	public void test001() {
		final ArrayList<String> liste = new ArrayList<String>();
		final ArrayList<Boolean> attendu = new ArrayList<Boolean>();
		liste.add(""); attendu.add(false);
		liste.add("true"); attendu.add(true);
		liste.add("false"); attendu.add(false);
		liste.add("    true     "); attendu.add(true);
		liste.add("!(true)"); attendu.add(false);
		liste.add("!(false)"); attendu.add(true);
		liste.add("!(!(false))"); attendu.add(false);
		liste.add("!(!(false) && !(true)) || false"); attendu.add(true);
		liste.add("!   (true)  "); attendu.add(false);
		liste.add("!(   false   )"); attendu.add(true);
		liste.add("false == false"); attendu.add(true);
		liste.add("true == false"); attendu.add(false);
		liste.add("$lala.urg[3,5]**2 == 18"); attendu.add(false);
		liste.add("2 < 18"); attendu.add(true);
		liste.add("18 < 2"); attendu.add(false);
		liste.add("18 <= 18"); attendu.add(true);
		liste.add("18 <= 2"); attendu.add(false);
		liste.add("2 >= 2"); attendu.add(true);
		liste.add("2 >= 18"); attendu.add(false);
		liste.add("2 == 2"); attendu.add(true);
		liste.add("2 == 18"); attendu.add(false);
		liste.add("2 != 18"); attendu.add(true);
		liste.add("2 != 2"); attendu.add(false);
		liste.add("2 + 2 == 4"); attendu.add(true);
		liste.add("2 + 2 == 5"); attendu.add(false);
		liste.add("$game_map.events[22].x==15"); attendu.add(true);
		liste.add("$game_map.events[22].x==1000"); attendu.add(false);
		liste.add("$game_map.events[22].y==20"); attendu.add(true);
		liste.add("$game_map.events[22].y==1000"); attendu.add(false);
		liste.add("$game_player.x==5"); attendu.add(true);
		liste.add("$game_player.x==1000"); attendu.add(false);
		liste.add("$game_player.y==6"); attendu.add(true);
		liste.add("$game_player.y==1000"); attendu.add(false);
		liste.add("true && true"); attendu.add(true);
		liste.add("true && false"); attendu.add(false);
		liste.add("false && true"); attendu.add(false);
		liste.add("false && false"); attendu.add(false);
		liste.add("true || true"); attendu.add(true);
		liste.add("true || false"); attendu.add(true);
		liste.add("false || true"); attendu.add(true);
		liste.add("false || false"); attendu.add(false);
		liste.add("2 + 4 * 3**2 == 38"); attendu.add(true);
		liste.add("2 + 4 * 3**2 == 38 && 5 == 4"); attendu.add(false);
		liste.add("1<2 && 2<3 || 2<0 && 4<75"); attendu.add(true);
		liste.add("$game_map.events[18].life == 3"); attendu.add(true);
		liste.add("$game_map.events[18].life == 4"); attendu.add(false);
		liste.add("$game_variables[14] == 10"); attendu.add(true);
		liste.add("$game_variables[50] == 11 || $game_variables[18] == 0"); attendu.add(false);
		liste.add("($game_map.events[22].x-$game_player.x).abs + ($game_map.events[22].y-$game_player.y).abs == 24"); attendu.add(true);
		liste.add("Math.hypot($game_player.x-2, $game_player.y-2).round == 5"); attendu.add(true);
		liste.add("Math.sqrt(65).round == 8"); attendu.add(true);
		
		for (int i = 0; i<liste.size(); i++) {
			final String s = liste.get(i);
			final Boolean resultatAttendu = attendu.get(i);
			LOG.info("attendu : " + (resultatAttendu ? "1" : "0"));
			final ConditionScript condition = new ConditionScript(0, s, true);
			assertEquals(condition.estVerifiee(), resultatAttendu);
			LOG.info("--------------------------------------------------------");
		}
	}
}
