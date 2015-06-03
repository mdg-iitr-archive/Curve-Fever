package com.dev.anshul.curve_fever;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class GameOverActivity extends Activity {
    TextView winnerNameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        winnerNameTV =(TextView) findViewById(R.id.tv_winner_name);
        winnerNameTV.setText("The Winner is: " + getIntent().getStringExtra("winner"));

    }
}
