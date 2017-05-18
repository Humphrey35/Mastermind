package com.idonthave.hp.mastermind;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Play extends AppCompatActivity {

    public static final String PREFS_NAME = "GameSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Spiel Auswahl");
        }

        Button continueButton = (Button) findViewById(R.id.button_continue);
        Button singlePlayer = (Button) findViewById(R.id.button_singlePlayer);
        Button versus = (Button) findViewById(R.id.button_versus);

        DBHelper mydb = new DBHelper(this);
        int exist = mydb.numberOfRowsSavedgame();

        if (exist > 0){
            continueButton.setVisibility(View.VISIBLE);
        } else {
            continueButton.setVisibility(View.GONE);
        }

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Play.this, MainActivity.class);
                intent.putExtra("continueGame",true);
                startActivity(intent);
            }
        });
        singlePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                if (settings.getInt("numberOfSlots",4)<=settings.getInt("numberOfColors",6) || settings.getBoolean("allowDuplicates",false) || (settings.getInt("numberOfSlots",4)<=(settings.getInt("numberOfColors",6)+1)&&settings.getBoolean("allowEmpty",false))){
                    Intent intent = new Intent(Play.this, MainActivity.class);
                    intent.putExtra("continueGame",false);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Mit diesen Einstellungen nicht Spielbar!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        versus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                if (settings.getInt("numberOfSlots",4)<=settings.getInt("numberOfColors",6) || settings.getBoolean("allowDuplicates",false) || (settings.getInt("numberOfSlots",4)<=(settings.getInt("numberOfColors",6)+1)&&settings.getBoolean("allowEmpty",false))){
                    Intent intent = new Intent(Play.this, DuellActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Mit diesen Einstellungen nicht Spielbar!"+String.valueOf((settings.getInt("numberOfSlots",4)<=(settings.getInt("numberOfColors",6)+1)&&settings.getBoolean("allowEmpty",false))),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}