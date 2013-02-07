/**
 * 
 */
package yoan.game.tictactoe.game.context;

import android.app.Application;

/**
 * Classe gérant le cycle de vie des sessions du contexte global de l'application
 * @author yoan
 */
public class GlobalContext extends Application {
	/** Le contexte d'identification utilisateur **/
	private TttContext gameContext;
	
	@Override
	public void onCreate(){
		super.onCreate();
		this.gameContext= TttContext.getContext();
	}

	@Override
	public void onTerminate(){
		super.onTerminate();
		gameContext = null;
	}

	/**
	 * @return the identificationSession
	 */
	public TttContext getTttContext() {
		return gameContext;
	}
}
