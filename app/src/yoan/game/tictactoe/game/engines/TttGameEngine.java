/**
 * 
 */
package yoan.game.tictactoe.game.engines;

import org.apache.commons.lang3.StringUtils;

import yoan.game.Game;
import yoan.game.engines.GameEngine;
import yoan.game.engines.ModuleType;
import yoan.game.tictactoe.game.engines.events.TttEngineEvent;
import yoan.game.util.errors.GameException;
import yoan.game.util.logs.Log;

/**
 * @author yoan
 *
 */
public class TttGameEngine extends GameEngine<TttEngineEvent> {
	private int i = 0;
	
	//arguments 
	private int indexSon = -1;
	private int indexFin = -1;
	private boolean humanFirst = true;

	public TttGameEngine(Game<TttEngineEvent> parent) {
		super(parent);
	}
	
	@Override
	public void init(String... initArgs) throws GameException {
		super.init(initArgs);
		//valorisation des paramètres du moteur
		indexSon = Integer.parseInt(initArgs[0]);
		indexFin= Integer.parseInt(initArgs[1]);
	}

	public void frame() throws GameException {
		i++;
		Log.debug(getType(), "frame " + i);
		if (i == indexSon) {
			Log.debug(getType(), "envoi d'un evenement au module sonore");
			game.getModules().get(ModuleType.SOUND).pushEvent(new TttEngineEvent("bip"));
		} else if (i == indexFin) {
			game.stop();
		}
		processQueue();
	}

	protected void processEvent(TttEngineEvent event) {
	}

	@Override
	protected boolean checkInitArgs(String... initArgs){
		boolean argsOk = false;
		if (initArgs != null && initArgs.length == 2) {
			String arg0 = initArgs[0];
			String arg1 = initArgs[1];
			argsOk = StringUtils.isNotBlank(arg0) && StringUtils.isNotBlank(arg1);
		}
		return argsOk;
	}

	/**
	 * @return the humanFirst
	 */
	public boolean isHumanFirst(){
		return humanFirst;
	}

	/**
	 * @param humanFirst the humanFirst to set
	 */
	public void setHumanFirst(boolean humanFirst){
		this.humanFirst= humanFirst;
	}
}