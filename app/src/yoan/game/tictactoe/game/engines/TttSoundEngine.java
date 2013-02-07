/**
 * 
 */
package yoan.game.tictactoe.game.engines;

import yoan.game.Game;
import yoan.game.engines.SoundEngine;
import yoan.game.tictactoe.game.engines.events.TttEngineEvent;
import yoan.game.util.errors.GameException;
import yoan.game.util.logs.Log;

/**
 * @author yoan
 */
public class TttSoundEngine extends SoundEngine<TttEngineEvent> {
	private int i = 0;

	public TttSoundEngine(Game<TttEngineEvent> parent) {
		super(parent);
	}

	public void frame() throws GameException {
		i++;
		Log.debug(getType(), "frame " + i);
		processQueue();
	}

	protected void processEvent(TttEngineEvent event) throws GameException {
		if (event != null) {
		}
	}

	@Override
	protected boolean checkInitArgs(String... initArgs){
		//pas d'argument d'initialisation
		return true;
	}
}