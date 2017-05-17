package com.idonthave.hp.mastermind;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Highscore extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        ListView highscore = (ListView) findViewById(R.id.highscore_view);

        DBHelper db = new DBHelper(this);
        String[] highscorearray = db.getTopHighscores();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, highscorearray);
        highscore.setAdapter(adapter);

    }
}