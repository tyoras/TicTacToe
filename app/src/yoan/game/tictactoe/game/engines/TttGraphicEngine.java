/**
 * 
 */
package yoan.game.tictactoe.game.engines;

import yoan.game.Game;
import yoan.game.engines.GraphicEngine;
import yoan.game.tictactoe.activities.GameView;
import yoan.game.tictactoe.game.constantes.State;
import yoan.game.tictactoe.game.engines.events.GraphicEvents;
import yoan.game.tictactoe.game.engines.events.TttEngineEvent;
import yoan.game.util.errors.GameErrors;
import yoan.game.util.errors.GameException;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * @author yoan
 */
public class TttGraphicEngine extends GraphicEngine<TttEngineEvent> {
	private GameView gameView;
    private TextView infoView;
    private Button buttonNext;
    
    /** Handler pour accéder à l'UI Thread */
    private Handler uiHandler = new Handler(Looper.getMainLooper(), new UiHandler()); 

	public TttGraphicEngine(Game<TttEngineEvent> parent) {
		super(parent);
	}

	@Override
	public void frame() throws GameException {
		processQueue();
	}

	/**
	 * Gestion des évenements de la queue
	 * @param event : l'événement à traiter
	 */
	@Override
	protected void processEvent(TttEngineEvent event) throws GameException {
		//récupération de l'évenement
		GraphicEvents code = identifierEvent(event);
		if (GraphicEvents.UNKNOWN == code) {
			GameErrors.throwFatalError(getType(), "Evenement non implémenté");
		}
		Message msg = new Message();
		msg.what = code.getValue();
		msg.obj = event.getData();
		uiHandler.sendMessage(msg);
	}
	
	/**
	 * Récupère le code de l'évenement s'il a réussi à l'identifier
	 * @param event inconnu
	 * @return l'évenement graphique identifié
	 * @throws GameException
	 */
	private GraphicEvents identifierEvent(TttEngineEvent event) throws GameException{
		GraphicEvents code = null;
		//récupération de l'évenement
		if (event.getCode() instanceof GraphicEvents){
			code = (GraphicEvents) event.getCode();
		}else{
			//si ce n'est pas un GraphicEvents, c'est un évenement inconnu
			GameErrors.throwFatalError(getType(), "Evenement inconnu");
		}
		return code;
	}
	
	/**
	 * Affichage du passage au tour suivant
	 * @param player : le joueur dont c'est le tour
	 * @param idString : le texte à afficher
	 */
	private void selectTurn(State player, int idString) {
        //on désactive le bouton
		buttonNext.setEnabled(false);
		//mise à jour du texte
        infoView.setText(idString);
        //la saisie est activée si c'est le tour du joueur humain
        if (player == State.PLAYER1) {
            gameView.setEnabled(true);
        } else if (player == State.PLAYER2) {
        	gameView.setEnabled(false);
        }
    }
	
	/**
	 * 
	 * @param player : le dernier joueur à avoir joué
     * @param col : l'index de la colonne complète, -1 sinon
     * @param row : l'index de la ligne complète, -1 sinon
     * @param diagonal : l'index de la diagonale complète, -1 sinon
	 */
	private void finish(State player, int col, int row, int diagonal) {
		gameView.setEnabled(false);
		gameView.setFinished(col, row, diagonal);
	}
	
	/**
	 * Affichage de fin de partie
	 * @param idString : le texte à afficher
	 */
	private void winState(int idString) {
		buttonNext.setEnabled(true);
		buttonNext.setText("Back");
		
		infoView.setText(idString);
	}
	
	/**
	 * Active ou désactive le bouton "Suivant"
	 * @param enable
	 */
	private void enableNextButton(Boolean enable){
		buttonNext.setEnabled(enable);
	}
	
	/**
	 * Demande à la vue de se redessiner
	 */
	private void refresh(){
		gameView.invalidate();
	}
	
	private void stopBlink(){
		gameView.stopBlink();
	}

	@Override
	protected boolean checkInitArgs(String... initArgs){
		//pas d'argument d'initialisation
		return true;
	}

	/**
	 * @param gameView the gameView to set
	 */
	public void setGameView(GameView gameView){
		this.gameView= gameView;
	}

	/**
	 * @param infoView the infoView to set
	 */
	public void setInfoView(TextView infoView){
		this.infoView= infoView;
	}

	/**
	 * @param buttonNext the buttonNext to set
	 */
	public void setButtonNext(Button buttonNext){
		this.buttonNext= buttonNext;
	}
	
	private class UiHandler implements Callback {
        public boolean handleMessage(Message msg) {
        	//récupération de l'évenement
    		GraphicEvents code = GraphicEvents.fromInt((Integer) msg.what);
    		Object[] data = (Object[]) msg.obj;
    		switch(code) {
    			case ENABLE_NEXT_BUTTON:
    				enableNextButton((Boolean) data[0]);
    				break;
    			case FINISH:
    				finish((State) data[0], (Integer) data[1], (Integer) data[2], (Integer) data[3]);
    				break;
    			case REFRESH:
    				refresh();
    				break;
    			case SELECT_TURN:
    				selectTurn((State) data[0], (Integer) data[1]);
    				break;
    			case STOP_BLINK:
    				stopBlink();
    				break;
    			case WIN_STATE:
    				winState((Integer) data[0]);
    				break;
    			default:
    				return false;
    		}
    		return true;
        }
    }
	
}