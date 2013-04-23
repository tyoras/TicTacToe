package yoan.game.tictactoe.activities;

import static yoan.game.tictactoe.game.constantes.ConstValues.JOUEUR_HUMAIN_1;
import static yoan.game.tictactoe.game.constantes.ConstValues.JOUEUR_ORDI;
import yoan.game.tictactoe.R;
import yoan.game.tictactoe.game.context.TttContext;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Acitvité de lancement du jeu
 * @author yoan
 */
public class MainActivity extends Activity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mise en place de l'affichage de l'écran de démarrage
        setContentView(R.layout.main);
        
        //si l'action est QUIT
	    if(Navigation.ACTION_QUIT.getAction().equals(getIntent().getAction())){
	    	//on quitte l'activité
    		finish();
	    } else {
            // le clic sur le premier bouton fait commencer le joueur humain
            findViewById(R.id.start_player).setOnClickListener(new OnClickListener() {
            	@Override
            	public void onClick(View v) {
                    startGame(JOUEUR_HUMAIN_1);
                }
            });
            // le clic sur le 2ème bouton fait commencer le joueur ordi
            findViewById(R.id.start_comp).setOnClickListener(new OnClickListener() {
               @Override
            	public void onClick(View v) {
                    startGame(JOUEUR_ORDI);
                }
            });
	    }
        
      //TODO [Ttt] ajouter un bouton pour reprendre une partie en cours
    }

    /**
     * Démarre le jeu avec le joueur sélectionné
     * @param firstPlayer : le joueur qui commence
     */
    private void startGame(String firstPlayer) {
        //on démarre l'activité gérant le jeu
    	Intent i = new Intent(this, GameActivity.class);
        i.putExtra(GameActivity.EXTRA_START_PLAYER, firstPlayer);
        //on vide le contexte
        TttContext.clear();
        startActivity(i);
    }
}