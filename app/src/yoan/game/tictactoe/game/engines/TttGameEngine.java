/**
 * 
 */
package yoan.game.tictactoe.game.engines;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import yoan.game.Game;
import yoan.game.engines.GameEngine;
import yoan.game.engines.ModuleType;
import yoan.game.tictactoe.R;
import yoan.game.tictactoe.game.constantes.ConstValues;
import yoan.game.tictactoe.game.constantes.State;
import yoan.game.tictactoe.game.context.TttContext;
import yoan.game.tictactoe.game.engines.events.GameEvents;
import yoan.game.tictactoe.game.engines.events.GraphicEvents;
import yoan.game.tictactoe.game.engines.events.TttEngineEvent;
import yoan.game.util.errors.GameErrors;
import yoan.game.util.errors.GameException;
import android.os.Handler;
import android.os.Looper;
import android.os.Handler.Callback;
import android.os.Message;

/**
 * Moteur gérant la logique du jeu
 * @author yoan
 */
public class TttGameEngine extends GameEngine<TttEngineEvent> {
	/** Indique que c'est le tour de l'IA */
	private static final int MSG_COMPUTER_TURN = 1;
	/** Délai d'envoi du message */
	private static final long COMPUTER_DELAY_MS = 500;
	
	/** Handler sur un thread gérant le tour de l'IA */
	private Handler handlerIA = new Handler(Looper.getMainLooper(), new HandlerIACallback());
	/** Random nécessaire à l'IA */
	private Random rand = new Random();
	    
	//arguments 
	private String firstPlayer = ConstValues.JOUEUR_HUMAIN_1;
	

	public TttGameEngine(Game<TttEngineEvent> parent) {
		super(parent);
	}
	
	@Override
	public void init(String... initArgs) throws GameException {
		super.init(initArgs);
		
		//valorisation des paramètres du moteur
		firstPlayer = initArgs[0];
	}

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
		GameEvents code = identifierEvent(event);
		Object[] data = event.getData();
		switch(code) {
			case NEXT_BUTTON_PRESSED:
				onButtonNextpressed();
				break;
			case CELL_SELECTED:
				onCellSelected();
				break;
			case BEGIN_TURN:
				beginTurn((State) data[0]);
				break;
			default:
				GameErrors.throwFatalError(getType(), "Evenement non implémenté");
				break;
		}
	}
	
	/**
	 * Récupère le code de l'évenement s'il a réussi à l'identifier
	 * @param event inconnu
	 * @return l'évenement graphique identifié
	 * @throws GameException
	 */
	private GameEvents identifierEvent(TttEngineEvent event) throws GameException{
		GameEvents code = null;
		//récupération de l'évenement
		if (event.getCode() instanceof GameEvents){
			code = (GameEvents) event.getCode();
		}else{
			//si ce n'est pas un GameEvents, c'est un évenement inconnu
			GameErrors.throwFatalError(getType(), "Evenement inconnu");
		}
		return code;
	}

	@Override
	protected boolean checkInitArgs(String... initArgs){
		boolean argsOk = false;
		if (initArgs != null && initArgs.length == 1) {
			String arg0 = initArgs[0];
			argsOk = StringUtils.isNotBlank(arg0);
		}
		return argsOk;
	}

	/**
	 * Débute un nouveau tour
	 * @param player : le joueur de ce tour
	 * @return le joueur de ce tour
	 */
	private State selectTurn(State player) {
		//le joueur en cours est celui défini
		TttContext.getContext().setCurrentPlayer(player);
		TttContext.getContext().setSelectedCell(-1);
		//on détermine le texte à afficher
		int idText = -1;
        if (player == State.PLAYER1) {
        	idText = R.string.player1_turn;

        } else if (player == State.PLAYER2) {
        	idText = R.string.player2_turn;
        }
        
        //envoi d'une requête au graphic engine
		game.getModules().get(ModuleType.GRAPHIC).pushEvent(new TttEngineEvent(GraphicEvents.SELECT_TURN, player, idText));
        return player;
    }
	
	/**
	 * Récupère l'identifiant de l'autre joueur de la partie
	 * @param player 
	 * @return autre joueur
	 */
	private State getOtherPlayer(State player) {
        return player == State.PLAYER1 ? State.PLAYER2 : State.PLAYER1;
    }
	
	/**
	 * Finit le tour
	 */
	private void finishTurn() {
		State player = TttContext.getContext().getCurrentPlayer();
        if (!checkGameFinished(player)) {
        	player = selectTurn(getOtherPlayer(player));
            if (player == State.PLAYER2) {
            	handlerIA.sendEmptyMessageDelayed(MSG_COMPUTER_TURN, COMPUTER_DELAY_MS);
            }
        }
    }
	
	/**
	 * Vérifie si le jeu est fini
	 * @param player : le joueur qui vient de jouer
	 * @return true si jeu fini
	 */
    public boolean checkGameFinished(State player) {
    	State[] data = TttContext.getContext().getData();
    	boolean full = true;

        int col = -1;
        int row = -1;
        int diag = -1;

        // check rows
        for (int j = 0, k = 0; j < 3; j++, k += 3) {
            if (data[k] != State.EMPTY && data[k] == data[k+1] && data[k] == data[k+2]) {
                row = j;
            }
            if (full && (data[k] == State.EMPTY ||
                         data[k+1] == State.EMPTY ||
                         data[k+2] == State.EMPTY)) {
                full = false;
            }
        }

        // check columns
        for (int i = 0; i < 3; i++) {
            if (data[i] != State.EMPTY && data[i] == data[i+3] && data[i] == data[i+6]) {
                col = i;
            }
        }

        // check diagonals
        if (data[0] != State.EMPTY && data[0] == data[1+3] && data[0] == data[2+6]) {
            diag = 0;
        } else  if (data[2] != State.EMPTY && data[2] == data[1+3] && data[2] == data[0+6]) {
            diag = 1;
        }

        if (col != -1 || row != -1 || diag != -1) {
            setFinished(player, col, row, diag);
            return true;
        }

        // if we get here, there's no winner but the board is full.
        if (full) {
            setFinished(State.EMPTY, -1, -1, -1);
            return true;
        }
        return false;
    }
    
    /**
     * Fini la partie
     * @param player : le dernier joueur à avoir joué
     * @param col : l'index de la colonne complète, -1 sinon
     * @param row : l'index de la ligne complète, -1 sinon
     * @param diagonal : l'index de la diagonale complète, -1 sinon
     */
    private void setFinished(State player, int col, int row, int diagonal) {
        //l'état courant indique que la partie est finie
    	TttContext.getContext().setCurrentPlayer(State.WIN);
    	TttContext.getContext().setSelectedCell(-1);
    	TttContext.getContext().setWinner(player);
    	//envoi d'une requête au graphic engine
      	game.getModules().get(ModuleType.GRAPHIC).pushEvent(new TttEngineEvent(GraphicEvents.FINISH, player, col, row, diagonal));
    	setWinState(player);
       
    }
    
    private void setWinState(State player) {
    	//on détermine le texte à afficher en fonction du résultat
        int idText = -1;
        if (player == State.EMPTY) {
        	idText = R.string.tie;
        } else if (player == State.PLAYER1) {
        	idText = R.string.player1_win;
        } else {
        	idText = R.string.player2_win;
        }
        //envoi d'une requête au graphic engine
      	game.getModules().get(ModuleType.GRAPHIC).pushEvent(new TttEngineEvent(GraphicEvents.WIN, idText));
    }
    
    
    private void onButtonNextpressed() {
    	State currentPlayer = TttContext.getContext().getCurrentPlayer();
        if (currentPlayer == State.WIN) {
            game.stop();
        } else if (currentPlayer == State.PLAYER1) {
            int cell = getSelection();
            if (cell >= 0) {
            	//envoi d'une requête au graphic engine
              	game.getModules().get(ModuleType.GRAPHIC).pushEvent(new TttEngineEvent(GraphicEvents.STOP_BLINK));
                setCell(cell, currentPlayer);
                finishTurn();
            }
        }
    }
    
    private void onCellSelected() {
    	State currentPlayer = TttContext.getContext().getCurrentPlayer();
    	if (currentPlayer == State.PLAYER1) {
            int cell = getSelection();
            //on active le bouton "Suivant si une case est sélectionnée
            Boolean enableNextButton = cell >= 0;
            //envoi d'une requête au graphic engine
          	game.getModules().get(ModuleType.GRAPHIC).pushEvent(new TttEngineEvent(GraphicEvents.ENABLE_NEXT_BUTTON, enableNextButton));
        }
    }
    
    private void beginTurn(State startPlayer) {
    	State currentPlayer = TttContext.getContext().getCurrentPlayer();
        if (currentPlayer == State.UNKNOWN) {
        	TttContext.getContext().setCurrentPlayer(startPlayer);
        	currentPlayer = startPlayer;
            if (!checkGameFinished(currentPlayer)) {
                selectTurn(currentPlayer);
            }
        }
        if (currentPlayer == State.PLAYER2) {
        	handlerIA.sendEmptyMessageDelayed(MSG_COMPUTER_TURN, COMPUTER_DELAY_MS);
        }
        if (currentPlayer == State.WIN) {
        	State winner = TttContext.getContext().getWinner();
            setWinState(winner);
        }
    }

	public int getSelection() {
		State currentPlayer = TttContext.getContext().getCurrentPlayer();
		State selectedValue = TttContext.getContext().getSelectedValue();
        if (selectedValue == currentPlayer) {
            return TttContext.getContext().getSelectedCell();
        }
        return -1;
    }
	
	/**
	 * Attribue une valeur à une case et rafraîchit la vue
	 * @param cellIndex : la case à valoriser
	 * @param value : la nouvelle valeur de la case
	 */
	public void setCell(int cellIndex, State value) {
    	State[] data = TttContext.getContext().getData();
    	data[cellIndex] = value;
    	//On rafraichit la vue
      	game.getModules().get(ModuleType.GRAPHIC).pushEvent(new TttEngineEvent(GraphicEvents.REFRESH));
    }
	
	/**
	 * @return the firstPlayer
	 */
	public String getFirstPlayer(){
		return firstPlayer;
	}

	/**
	 * @param firstPlayer the firstPlayer to set
	 */
	public void setFirstPlayer(String firstPlayer){
		this.firstPlayer= firstPlayer;
	}
	
	
	/**
	 * Callback sur le handler du thread de l'IA
	 * Permet de gérer des messages dans un thread à part
	 */
	private class HandlerIACallback implements Callback {
        /**
         * gestion d'un message en entrée
         * @param msg : le message d'entrée
         */
		public boolean handleMessage(Message msg) {
			//si c'est le tour de l'IA, on cherche une cas à jouer
            if (msg.what == MSG_COMPUTER_TURN) {
                //récupération de l'état de la grille dans le contexte
            	State[] data = TttContext.getContext().getData();
                int used = 0;
                //tant que le random n'a pas généré l'index de chaque case
                while (used != 0x1F) {
                	//génération d'un chiffre entre 0 et 8
                    int index = rand.nextInt(9);
                    //si cet index n'a pas déjà été généré
                    if (((used >> index) & 1) == 0) {
                    	//on change le bit correspondant à cet index
                        used |= 1 << index;
                        //et si la case correspondant n'est pas jouée
                        if (data[index] == State.EMPTY) {
                        	State currentPlayer = TttContext.getContext().getCurrentPlayer();
                        	//on fait jouer cette case par l'IA
                        	setCell(index, currentPlayer);
                            break;
                        }
                    }
                }
                //on finit le tour
                finishTurn();
                return true;
            }
            return false;
        }
    }
	
}