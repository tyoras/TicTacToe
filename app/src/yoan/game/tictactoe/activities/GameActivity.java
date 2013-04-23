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
import android.content.Intent;
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

/**
 * Activité gerant le jeu
 * @author yoan
 */
public class GameActivity extends Activity {

    /** Start player. Must be 1 or 2. Default is 1. */
    public static final String EXTRA_START_PLAYER = "yoan.game.tictactoe.GameActivity.EXTRA_START_PLAYER";

    /** gère les modules du jeu */
    private TttGame game;
    /** gère l'affichage du jeu */
    private GameView gameView;
    /** zone de texte du l'écran de jeu */
    private TextView infoText;
    /** bouton "Next" */
    private Button buttonNext;
    /** bouton "Quit" */
    private Button buttonQuit;
    
  //TODO [Ttt] ajouter un menu pour retourner à l'accueil ou quitter

    /** Appelé à la première création de l'activité */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.lib_game);
        //récupération des éléments de l'activité
        gameView = (GameView) findViewById(R.id.game_view);
        infoText = (TextView) findViewById(R.id.info_turn);
        buttonNext = (Button) findViewById(R.id.next_turn);
        buttonQuit = (Button) findViewById(R.id.b_quit); 

        //config et mise en place des listener sur les éléments
        gameView.setFocusable(true);
        gameView.setFocusableInTouchMode(true);
        gameView.setCellListener(new CellListener());

        buttonNext.setOnClickListener(new NextButtonListener());
        
        buttonQuit.setVisibility(View.INVISIBLE);
        buttonQuit.setOnClickListener(new QuitButtonListener());
        
        game = new TttGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //on détermine le 1er joueur à partir du bouton clické sur l'activité précedente
        String firstPlayer = getIntent().getStringExtra(EXTRA_START_PLAYER);
        try {
        	//initialisation du jeu
    		game.init(firstPlayer);
    		game.setRunning(true);
    		
    		//on récupère le graphic engine
    		TttGraphicEngine graphicEngine = (TttGraphicEngine) game.getModules().get(ModuleType.GRAPHIC);
    		//et on lui passe les views
    		graphicEngine.setGameView(gameView);
    		graphicEngine.setInfoView(infoText);
    		graphicEngine.setButtonNext(buttonNext);
    		graphicEngine.setButtonQuit(buttonQuit);
    		
    		//FIXME [Ttt] récréation de nouveau thread à chaque onResume()
    		//on lance la gestion du jeu dans un thread
    		Thread gameThread = new Thread(new Runnable() {
    			/** handler pour communiquer avec le thread principal (UI thread) */
    			private final Handler handler = new Handler(Looper.getMainLooper(), new MsgHandler());
    			/** Callback permettant de transmettre une GameException au thread principal */ 
    			class MsgHandler implements Callback {
    		        public boolean handleMessage(Message msg) {
    		        	lauchError((GameException) msg.obj);
    		        	return true;
    		        }
    		    }
    			
    		    public void run() {
    		    	// Moves the current Thread into the background
    		        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
    		    	try {
    		    		game.run();
    		    	} catch(GameException gex) {
    		    		//en cas d'erreur du jeu
    		    		Message msg = new Message();
    		    		msg.obj = gex;
    		    		//on envoie l'exception à l'UI thread via le callback
    		    		handler.sendMessage(msg);
    				}
    		    }
    		  });
    		gameThread.setName("TtT Game thread");
    		gameThread.start();
		} catch(GameException gex) {
			//interprétation de l'erreur du jeu
			lauchError(gex);
		}
        //récupération du 1er joueur
        State startPlayer = State.getPlayerState(getIntent().getStringExtra(EXTRA_START_PLAYER));
        //envoi d'une requête au game engine
		game.getModules().get(ModuleType.GAME).pushEvent(new TttEngineEvent(GameEvents.BEGIN_TURN, startPlayer));
    }
    
    
    protected void quitter() {
    	game.stop();
    	//on quitte l'activité
		setResult(Navigation.ACTION_QUIT.getResultCode());
		//on revient à l'accueil pour quitter
		Intent quitter= new Intent(GameActivity.this, MainActivity.class);
		quitter.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		quitter.setAction(Navigation.ACTION_QUIT.getAction());
		finish();
		startActivity(quitter);
    }

    /** Listener du click sur une des cases */
    private class CellListener implements ICellListener {
        @Override
    	public void onCellSelected() {
        	//envoi de l'évenement au game engine
    		game.getModules().get(ModuleType.GAME).pushEvent(new TttEngineEvent(GameEvents.CELL_SELECTED));
        }
    }

    /** Listener du click sur le bouton "Next" */
    private class NextButtonListener implements OnClickListener {
    	@Override
        public void onClick(View v) {
        	//envoi de l'évenement au game engine
    		game.getModules().get(ModuleType.GAME).pushEvent(new TttEngineEvent(GameEvents.NEXT_BUTTON_PRESSED));
    		//si le jeu est fini
    		if (State.WIN == TttContext.getContext().getCurrentPlayer()) {
    			game.stop();
    			//on finit l'activité
    			GameActivity.this.finish();
    		}
        }
    }
    
    /** Listener du click sur le bouton "Quit" */
    private class QuitButtonListener implements OnClickListener {
    	@Override
        public void onClick(View v) {
        	//on arrête le jeu
    		quitter();
        }
    }
    
    /**
     * Génére un message d'erreur à partir d'une GameException
     * @param gex : l'exception 
     */
    private void lauchError(GameException gex) {
    	if (gex.isBloquant()) {
			Toast.makeText(GameActivity.this, String.format(getString(R.string.fatalError), gex.getMessage()), Toast.LENGTH_SHORT).show();
			//si message bloquant on finit l'activité après avoir afficher le message
			finish();
		} else {
			Toast.makeText(GameActivity.this, String.format(getString(R.string.error), gex.getMessage()), Toast.LENGTH_SHORT).show();
		}
    }

	@Override
	protected void onDestroy(){
		game.stop();
		super.onDestroy();
	}

	@Override
	protected void onPause(){
		// TODO Auto-generated method stub
		super.onPause();
	}
}