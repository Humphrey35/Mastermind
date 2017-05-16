package com.idonthave.hp.mastermind;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
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
    private TextView[] btn;

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

        btn = new TextView[9];
        createColorPicker(getSeekbarColor(seekBarNumberOfColors.getProgress()));
        resetColors();

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
                hideAndShow(progress);
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

    private void createColorPicker(int numberOfColors){

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rlsettings);
        // init Display Size and calculate sizes
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x;

        final int marginOfSlot = Math.round((width/(8))*0.1f); // 10% Margin of the Size of one Slot as Margin in between
        final int widthOfSlot = (width/(8))-marginOfSlot;

        for (int i=1;i<9;i++) {
            btn[i] = new TextView(this);
            rl.addView(btn[i]);
            btn[i].setBackgroundResource(R.drawable.circle);
            btn[i].setId(i+1+90);
            btn[i].setMinimumWidth(1);
            btn[i].setWidth(widthOfSlot);
            btn[i].setMinHeight(1);
            btn[i].setHeight(widthOfSlot);
            btn[i].setIncludeFontPadding(false);
            btn[i].setGravity(Gravity.CENTER_VERTICAL);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) btn[i].getLayoutParams();
            lp.setMargins(marginOfSlot, 70, 0, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            if (i>1) {
                lp.addRule(RelativeLayout.RIGHT_OF, btn[i - 1].getId());
            } else {
                lp.addRule(RelativeLayout.RIGHT_OF, R.id.textView5);
            }
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            lp.addRule(RelativeLayout.BELOW, R.id.seekBar);
            btn[i].setLayoutParams(lp);
        }
    }

    private void resetColors(){
        btn[1].setBackgroundResource(R.drawable.blue);
        btn[2].setBackgroundResource(R.drawable.brown);
        btn[3].setBackgroundResource(R.drawable.grass);
        btn[4].setBackgroundResource(R.drawable.green);
        btn[5].setBackgroundResource(R.drawable.orange);
        btn[6].setBackgroundResource(R.drawable.pink);
        btn[7].setBackgroundResource(R.drawable.purple);
        btn[8].setBackgroundResource(R.drawable.red);
    }

    private void hideAndShow(int number){
        if (number == 5){
            btn[6].setVisibility(View.GONE);
            btn[7].setVisibility(View.GONE);
            btn[8].setVisibility(View.GONE);
            resetColors();
        } else if (number == 6) {
            btn[6].setVisibility(View.VISIBLE);
            btn[7].setVisibility(View.GONE);
            btn[8].setVisibility(View.GONE);
            resetColors();
        } else {
            btn[6].setVisibility(View.VISIBLE);
            btn[7].setVisibility(View.VISIBLE);
            btn[8].setVisibility(View.VISIBLE);
            resetColors();
        }
    }
}