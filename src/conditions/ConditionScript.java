package conditions;

import java.util.HashMap;


public class ConditionScript extends Condition {

	private final String script;
	
	/**
	 * Constructeur explicite
	 * @param numero de la Condition
	 * @param script à interpréter
	 */
	public ConditionScript(final int numero, final String script) {
		this.numero = numero;
		this.script = script;
	}
	
	/**
	 * Constructeur générique
	 * @param parametres liste de paramètres issus de JSON
	 */
	public ConditionScript(final HashMap<String, Object> parametres) {
		this( parametres.get("numero") != null ? (int) parametres.get("numero") : -1,
			(String) parametres.get("script")
		);
	}
	
	@Override
	public final boolean estVerifiee() {
		// TODO Auto-generated method stub
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
		
		return false;
	}

	@Override
	public final boolean estLieeAuHeros() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public final String toString() {
		return "ConditionScript : "+script;
	}

}
