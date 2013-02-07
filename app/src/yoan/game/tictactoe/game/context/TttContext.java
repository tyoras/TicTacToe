/**
 * 
 */
package yoan.game.tictactoe.game.context;

import yoan.game.context.GameContext;
import yoan.game.tictactoe.game.constantes.State;

/**
 * Bean singleton décrivant le contexte du jeu
 * @author yoan
 */
public class TttContext implements GameContext {
	/** instance du singleton */
	private static TttContext instance= null;
	
	/** Contains one of {@link State#EMPTY}, {@link State#PLAYER1} or {@link State#PLAYER2}. */
	private final State[] data = new State[9];
	private State currentPlayer = State.UNKNOWN;
	private State winner = State.EMPTY;
	
	private int selectedCell = -1;
    private State selectedValue = State.EMPTY;

//    private int mWinCol = -1;
//    private int mWinRow = -1;
//    private int mWinDiag = -1;
	
	/** Constructeur privé */
	private TttContext(){ }
	
	/**
	 * Récupère le contexte et le créé s'il n'existe pas encore
	 * @return le contexte
	 */
	public final synchronized static TttContext getContext(){
        if(instance == null) 
            instance= new TttContext();
        return instance;
    }
	
	/**
	 * Destruction du contexte
	 */
	public final static void clear(){
		instance= null;
	}

	/**
	 * @return the currentPlayer
	 */
	public State getCurrentPlayer(){
		return currentPlayer;
	}

	/**
	 * @param currentPlayer the currentPlayer to set
	 */
	public void setCurrentPlayer(State currentPlayer){
		this.currentPlayer= currentPlayer;
	}

	/**
	 * @return the winner
	 */
	public State getWinner(){
		return winner;
	}

	/**
	 * @param winner the winner to set
	 */
	public void setWinner(State winner){
		this.winner= winner;
	}

	/**
	 * @return the data
	 */
	public State[] getData(){
		return data;
	}

	/**
	 * @return the selectedCell
	 */
	public int getSelectedCell(){
		return selectedCell;
	}

	/**
	 * @param selectedCell the selectedCell to set
	 */
	public void setSelectedCell(int selectedCell){
		this.selectedCell= selectedCell;
	}

	/**
	 * @return the selectedValue
	 */
	public State getSelectedValue(){
		return selectedValue;
	}

	/**
	 * @param selectedValue the selectedValue to set
	 */
	public void setSelectedValue(State selectedValue){
		this.selectedValue= selectedValue;
	}
}