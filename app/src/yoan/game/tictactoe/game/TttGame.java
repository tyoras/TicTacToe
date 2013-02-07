/**
 * 
 */
package yoan.game.tictactoe.game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import yoan.game.Game;
import yoan.game.engines.ModuleType;
import yoan.game.tictactoe.game.engines.TttGameEngine;
import yoan.game.tictactoe.game.engines.TttGraphicEngine;
import yoan.game.tictactoe.game.engines.events.TttEngineEvent;
import yoan.game.util.errors.GameException;

/**
 * @author yoan
 *
 */
public class TttGame extends Game<TttEngineEvent> {
	
	/**
	 * Constructeur par défaut du jeu
	 */
	public TttGame(){
		super();
	}
	
	/**
	 * Initialisation des différents modules utilisés
	 * @param initArgs : arguments d'initialisation
	 */
	@Override
	public void init(String... initArgs) throws GameException {
		super.init(initArgs);
		
		//listage des modules utilisés
		modules.put(ModuleType.GAME, new TttGameEngine(this));
		modules.put(ModuleType.GRAPHIC, new TttGraphicEngine(this));
//		modules.put(ModuleType.SOUND, new TttSoundEngine(this));
		
		Map<ModuleType, String[]> args= new HashMap<ModuleType, String[]>();
		
		//le premier arg est celui du GameEngine
		args.put(ModuleType.GAME, Arrays.copyOfRange(initArgs, 0, 1));
		initEngines(args);
	}

	/**
	 * Arrête l'application
	 */
	@Override
	public void stop() {
		super.stop();
	}
	
	@Override
	protected boolean checkInitArgs(String... initArgs) {
		return initArgs != null && initArgs.length >= 1;
	}
}