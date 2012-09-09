package yoan.game.tictactoe;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class AccueilActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_accueil);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.layout_accueil, menu);
        return true;
    }
}
