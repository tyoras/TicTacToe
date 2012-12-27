/**
 * 
 */
package yoan.game.tictactoe.game.engines;

import yoan.game.Game;
import yoan.game.engines.GraphicEngine;
import yoan.game.tictactoe.game.engines.events.TttEngineEvent;
import yoan.game.util.errors.GameException;
import yoan.game.util.logs.Log;

/**
 * 
 * @author yoan
 */
public class TttGraphicEngine extends GraphicEngine<TttEngineEvent> {
	private int i = 0;

	public TttGraphicEngine(Game<TttEngineEvent> parent) {
		super(parent);
	}

	public void frame() throws GameException {
		this.i += 1;
		Log.debug(getType(), "frame " + this.i);
		processQueue();
	}

	protected void processEvent(TttEngineEvent event) {
	}

	@Override
	protected boolean checkInitArgs(String... initArgs){
		//pas d'argument d'initialisation
		return true;
	}
}