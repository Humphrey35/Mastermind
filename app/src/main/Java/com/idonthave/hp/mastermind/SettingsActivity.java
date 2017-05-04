package com.idonthave.hp.mastermind;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    public static final String PREFS_NAME = "GameSettings";
    private SeekBar seekBarNumberOfColors;
    private TextView textViewNumberOfColors;
    private SeekBar seekBarNumberOfSlots;
    private TextView textViewNumberOfSlots;
    private SeekBar seekBarNumberOfTries;
    private TextView textViewNumberOfTries;
    private Button start;
    private CheckBox duplicates;
    private CheckBox emptySlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_menu);
        initializeVariables();
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        final SharedPreferences.Editor editor = settings.edit();

        seekBarNumberOfColors.setProgress(setSeekbarColor(settings.getInt("numberOfColors", 6)));
        seekBarNumberOfSlots.setProgress(setSeekbarSlots(settings.getInt("numberOfSlots", 4)));
        seekBarNumberOfTries.setProgress(settings.getInt("numberOfTries",9));

        duplicates.setChecked(settings.getBoolean("allowDuplicates", true));
        emptySlots.setChecked(settings.getBoolean("allowEmpty", false));

        // Initialize the textview with '0'.

        textViewNumberOfColors.setText("Anzahl Farben: " + getSeekbarColor(seekBarNumberOfColors.getProgress()));

        seekBarNumberOfColors.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progress = getSeekbarColor(0);

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = getSeekbarColor(progresValue);
                textViewNumberOfColors.setText("Anzahl Farben: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                textViewNumberOfColors.setText("Anzahl Farben: " + progress);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewNumberOfColors.setText("Anzahl Farben: " + progress);
                editor.putInt("numberOfColors",progress);
                editor.commit();
            }
        });

        textViewNumberOfSlots.setText("Anzahl Löcher: " + getSeekbarSlots(seekBarNumberOfSlots.getProgress()));

        seekBarNumberOfSlots.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progress = getSeekbarSlots(0);

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = getSeekbarSlots(progresValue);
                textViewNumberOfSlots.setText("Anzahl Löcher: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                textViewNumberOfSlots.setText("Anzahl Löcher: " + progress);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewNumberOfSlots.setText("Anzahl Löcher: " + progress);
                editor.putInt("numberOfSlots",progress);
                editor.commit();
            }
        });

        textViewNumberOfTries.setText("Anzahl Versuche: " + (seekBarNumberOfTries.getProgress()+1));

        seekBarNumberOfTries.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progress = getSeekbarSlots(0);

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                textViewNumberOfTries.setText("Anzahl Versuche: " + (progress+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                textViewNumberOfTries.setText("Anzahl Versuche: " + (progress+1));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewNumberOfTries.setText("Anzahl Versuche: " + (progress+1));
                editor.putInt("numberOfTries",progress);
                editor.commit();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getSeekbarSlots(seekBarNumberOfSlots.getProgress())<=getSeekbarColor(seekBarNumberOfColors.getProgress()) || duplicates.isChecked()){
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        duplicates.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editor.putBoolean("allowDuplicates",((CheckBox) v).isChecked());
                editor.commit();
            }
        });

        emptySlots.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editor.putBoolean("allowEmpty",((CheckBox) v).isChecked());
                editor.commit();
            }
        });
    }

    // A private method to help us initialize our variables.
    private void initializeVariables() {
        seekBarNumberOfColors = (SeekBar) findViewById(R.id.seekBar);
        textViewNumberOfColors = (TextView) findViewById(R.id.textView);
        seekBarNumberOfSlots = (SeekBar) findViewById(R.id.seekBar2);
        textViewNumberOfSlots = (TextView) findViewById(R.id.textView2);
        seekBarNumberOfTries = (SeekBar) findViewById(R.id.seekBar3);
        textViewNumberOfTries = (TextView) findViewById(R.id.textView4);
        start = (Button) findViewById(R.id.start);
        duplicates = (CheckBox) findViewById(R.id.duplicates);
        emptySlots = (CheckBox) findViewById(R.id.emptySlots);
    }

    private int getSeekbarColor(int seek){
        int r = Integer.valueOf(6);
        switch (seek){
            case 0: r = 5;break;
            case 1: r = 6;break;
            case 2: r = 8;break;
        }
        return r;
    }

    private int getSeekbarSlots(int seek){
        int r = Integer.valueOf(4);
        switch (seek){
            case 0: r = 3;break;
            case 1: r = 4;break;
            case 2: r = 5;break;
            case 3: r = 6;break;
            case 4: r = 8;break;
        }
        return r;
    }

    private int setSeekbarColor(int seek){
        int r = Integer.valueOf(1);
        switch (seek){
            case 5: r = 0;break;
            case 6: r = 1;break;
            case 8: r = 2;break;
        }
        return r;
    }

    private int setSeekbarSlots(int seek){
        int r = Integer.valueOf(1);
        switch (seek){
            case 3: r = 0;break;
            case 4: r = 1;break;
            case 5: r = 2;break;
            case 6: r = 3;break;
            case 8: r = 4;break;
        }
        return r;
    }
}