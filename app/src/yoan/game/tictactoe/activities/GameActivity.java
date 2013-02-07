package yoan.game.tictactoe.activities;

import yoan.game.engines.ModuleType;
import yoan.game.tictactoe.R;
import yoan.game.tictactoe.activities.GameView.ICellListener;
import yoan.game.tictactoe.game.TttGame;
import yoan.game.tictactoe.game.constantes.State;
import yoan.game.tictactoe.game.context.TttContext;
import yoan.game.tictactoe.game.engines.TttGraphicEngine;
import yoan.game.tictactoe.game.engines.events.GameEvents;
import yoan.game.tictactoe.game.engines.events.TttEngineEvent;
import yoan.game.util.errors.GameException;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class GameActivity extends Activity {

    /** Start player. Must be 1 or 2. Default is 1. */
    public static final String EXTRA_START_PLAYER = "yoan.game.tictactoe.GameActivity.EXTRA_START_PLAYER";

    private TttGame game;
    private GameView mGameView;
    private TextView mInfoView;
    private Button mButtonNext;
    
  //TODO [Ttt] ajouter un bouton pour quitter en fin de partie
  //TODO [Ttt] ajouter un menu pour retourner à l'accueil ou quitter

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.lib_game);

        mGameView = (GameView) findViewById(R.id.game_view);
        mInfoView = (TextView) findViewById(R.id.info_turn);
        mButtonNext = (Button) findViewById(R.id.next_turn);

        mGameView.setFocusable(true);
        mGameView.setFocusableInTouchMode(true);
        mGameView.setCellListener(new CellListener());

        mButtonNext.setOnClickListener(new NextButtonListener());
        
        game = new TttGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String firstPlayer = getIntent().getStringExtra(EXTRA_START_PLAYER);
        try {
    		game.init(firstPlayer);
    		game.setRunning(true);
    		
    		//on récupère le graphic engine
    		TttGraphicEngine graphicEngine = (TttGraphicEngine) game.getModules().get(ModuleType.GRAPHIC);
    		//et on lui passe les views
    		graphicEngine.setGameView(mGameView);
    		graphicEngine.setInfoView(mInfoView);
    		graphicEngine.setButtonNext(mButtonNext);
    		
    		//TODO [Ttt] commenter la gestion des erreurs
    		//FIXME [Ttt] trop d'appels au GC
    		new Thread(new Runnable() {
    			private final Handler mHandler = new Handler(Looper.getMainLooper(), new MsgHandler());
    			
    			class MsgHandler implements Callback {
    		        public boolean handleMessage(Message msg) {
    		        	lauchError((GameException) msg.obj);
    		        	return true;
    		        }
    		    }
    			
    		    public void run() {
    		    	try {
    		    		game.run();
    		    	} catch(GameException gex) {
    		    		Message msg = new Message();
    		    		msg.obj = gex;
    		    		mHandler.sendMessage(msg);
    				}
    		    }
    		  }).start();
    		
		} catch(GameException gex) {
			lauchError(gex);
		}
        //récupération du 1er joueur
        State startPlayer = State.fromString(getIntent().getStringExtra(EXTRA_START_PLAYER));
        //envoi d'une requête au game engine
		game.getModules().get(ModuleType.GAME).pushEvent(new TttEngineEvent(GameEvents.BEGIN_TURN, startPlayer));
    }

    private class CellListener implements ICellListener {
        @Override
    	public void onCellSelected() {
        	//envoi d'une requête au game engine
    		game.getModules().get(ModuleType.GAME).pushEvent(new TttEngineEvent(GameEvents.CELL_SELECTED));
        }
    }

    private class NextButtonListener implements OnClickListener {
    	@Override
        public void onClick(View v) {
        	//envoi d'une requête au game engine
    		game.getModules().get(ModuleType.GAME).pushEvent(new TttEngineEvent(GameEvents.NEXT_BUTTON_PRESSED));
    		if (State.WIN == TttContext.getContext().getCurrentPlayer()) {
    			GameActivity.this.finish();
    		}
        }
    }
    
    private void lauchError(GameException gex) {
    	if (gex.isBloquant()) {
			Toast.makeText(GameActivity.this, String.format(getString(R.string.fatalError), gex.getMessage()), Toast.LENGTH_SHORT).show();
			finish();
		} else {
			Toast.makeText(GameActivity.this, String.format(getString(R.string.error), gex.getMessage()), Toast.LENGTH_SHORT).show();
		}
    }
}