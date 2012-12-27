package yoan.game.tictactoe;

import static yoan.game.tictactoe.game.constantes.ConstValues.JOUEUR_HUMAIN_1;
import static yoan.game.tictactoe.game.constantes.ConstValues.JOUEUR_ORDI;
import yoan.game.tictactoe.game.TttGame;
import yoan.game.util.errors.GameException;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.start_player).setOnClickListener(
                new OnClickListener() {
            public void onClick(View v) {
                startGame(JOUEUR_HUMAIN_1);
            }
        });

        findViewById(R.id.start_comp).setOnClickListener(
                new OnClickListener() {
            public void onClick(View v) {
                startGame(JOUEUR_ORDI);
            }
        });
    }

    private void startGame(String firstPlayer) {
//        Intent i = new Intent(this, GameActivity.class);
//        i.putExtra(GameActivity.EXTRA_START_PLAYER, startWithHuman ? State.PLAYER1.getValue() : State.PLAYER2.getValue());
//        startActivity(i);
		TttGame game = new TttGame();
		try {
    		game.init(/*firstPlayer*/"50", "100");
    		game.setRunning(true);
    		game.run();
		} catch(GameException gex) {
			if (gex.isBloquant()) {
				Toast.makeText(MainActivity.this, String.format(getString(R.string.fatalError), gex.getMessage()), Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(MainActivity.this, String.format(getString(R.string.error), gex.getMessage()), Toast.LENGTH_SHORT).show();
			}
		}
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.layout_accueil, menu);
//        return true;
//    }
}
