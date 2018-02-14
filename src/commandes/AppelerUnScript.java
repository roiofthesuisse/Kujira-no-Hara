package commandes;

import java.util.ArrayList;
import java.util.HashMap;

import main.Commande;

/**
 * Appeler un script ruby.
 */
public class AppelerUnScript extends Commande implements CommandeEvent, CommandeMenu {
	
	private final String script;
	
	/**
	 * Constructeur explicite
	 * @param script à exécuter
	 */
	public AppelerUnScript(final String script) {
		this.script = script;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public AppelerUnScript(final HashMap<String, Object> parametres) {
		this((String) parametres.get("script"));
	}
	
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		// TODO Auto-generated method stub
		
		//wait_for_event(@event_id)
		
		//wait_for_event(0)
		
		//$game_map.events[@event_id].x == $game_variables[181] && $game_map.events[@event_id].y == $game_variables[182] 
		
		//invoquer($game_map.events[1].x, $game_map.events[1].y, 21, 54)
		
		//$game_map.events[@event_id].transform(511, 6)
		
		//event = invoquer(15, 13, 457, 7)\nevent.jump($game_player.x-event.x, $game_player.y-event.y)
		
		//$game_map.events[@event_id].life -= 1
		
		//$game_temp.animations.push([23, true, $game_map.events[@event_id].x, $game_map.events[@event_id].y])
		
		//x = y = 0\ncase $game_player.direction\nwhen 2; y = $game_map.events[@event_id].y - $game_player.y - 1\nwhen 4; x = $game_map.events[@event_id].x - $game_player.x + 1\nwhen 6; x = $game_map.events[@event_id].x - $game_player.x - 1\nwhen 8; y = $game_map.events[@event_id].y - $game_player.y + 1\nend\n$game_player.jump(x, y)
		
		return curseurActuel+1;
	}
	
	@Override
	public final String toString() {
		return "AppelerUnScript : "+script;
	}

}
