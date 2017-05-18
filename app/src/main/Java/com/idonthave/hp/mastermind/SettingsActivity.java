package com.idonthave.hp.mastermind;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "GameSettings";
    private SeekBar seekBarNumberOfColors;
    private TextView textViewNumberOfColors;
    private SeekBar seekBarNumberOfSlots;
    private TextView textViewNumberOfSlots;
    private SeekBar seekBarNumberOfTries;
    private TextView textViewNumberOfTries;
    private SeekBar BoardColor;
    private TextView textBoardColor;
    private CheckBox duplicates;
    private CheckBox emptySlots;
    private CheckBox ovals;
    private TextView[] btn;
    private int AllColors[];
    private int colors[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }


        initializeVariables();
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        final SharedPreferences.Editor editor = settings.edit();

        seekBarNumberOfColors.setProgress(setSeekbarColor(settings.getInt("numberOfColors", 6)));
        seekBarNumberOfSlots.setProgress(setSeekbarSlots(settings.getInt("numberOfSlots", 4)));
        seekBarNumberOfTries.setProgress(settings.getInt("numberOfTries",9));
        BoardColor.setProgress(settings.getInt("boardColor",0));

        duplicates.setChecked(settings.getBoolean("allowDuplicates", true));
        emptySlots.setChecked(settings.getBoolean("allowEmpty", false));

        AllColors = new int[]{ settings.getInt("Color0", R.drawable.blue), settings.getInt("Color1", R.drawable.brown), settings.getInt("Color2", R.drawable.grass), settings.getInt("Color3", R.drawable.green), settings.getInt("Color4", R.drawable.orange), settings.getInt("Color5", R.drawable.pink), settings.getInt("Color6", R.drawable.purple), settings.getInt("Color7", R.drawable.red), settings.getInt("Color8", R.drawable.sky), settings.getInt("Color9", R.drawable.yellow) };

        colors = getUnusedColors(getSeekbarColor(seekBarNumberOfColors.getProgress()));

        btn = new TextView[9];
        createColorPicker(getSeekbarColor(seekBarNumberOfColors.getProgress()));
        hideAndShow(getSeekbarColor(seekBarNumberOfColors.getProgress()));
        colors = getUnusedColors(getSeekbarColor(seekBarNumberOfColors.getProgress()));

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
                colors = getUnusedColors(progress);
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

        textBoardColor.setText("Board Farbe: " + getBoardColor(BoardColor.getProgress()));

        BoardColor.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progress = getSeekbarSlots(0);

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                textBoardColor.setText("Board Farbe: " + getBoardColor(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                textBoardColor.setText("Board Farbe: " + getBoardColor(progress));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textBoardColor.setText("Board Farbe: " + getBoardColor(progress));
                editor.putInt("boardColor",progress);
                editor.commit();
            }
        });

        duplicates.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editor.putBoolean("allowDuplicates",((CheckBox) v).isChecked());
                editor.commit();
            }
        });

        ovals.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editor.putBoolean("ovalPins",((CheckBox) v).isChecked());
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
        duplicates = (CheckBox) findViewById(R.id.duplicates);
        emptySlots = (CheckBox) findViewById(R.id.emptySlots);
        ovals = (CheckBox) findViewById(R.id.checkBox5);
        BoardColor = (SeekBar) findViewById(R.id.seekBar4);
        textBoardColor = (TextView) findViewById(R.id.textView3);
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

    private String getBoardColor(int seek){
        String r = "White";
        switch (seek){
            case 0: r = "White";break;
            case 1: r = "Brown";break;
            case 2: r = "Orange";break;
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
        final int marginOfPopupSlot = Math.round((width/(numberOfColors+1))*0.1f);
        final int widthOfPopup = (width/(numberOfColors+1))*numberOfColors;
        final int widthOfPopupSlot = (width/(numberOfColors+1))-marginOfPopupSlot;

        for (int i=1;i<9;i++) {
            btn[i] = new TextView(this);
            rl.addView(btn[i]);
            btn[i].setBackgroundResource(AllColors[i-1]);
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
            btn[i].setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent popup = new Intent(SettingsActivity.this, Pop.class);
                    popup.putExtra("width", widthOfPopup);
                    popup.putExtra("height", widthOfPopupSlot);
                    popup.putExtra("slotWidth", widthOfPopupSlot);
                    popup.putExtra("slotHeight", widthOfPopupSlot);
                    popup.putExtra("marginOfPopupSlot", marginOfPopupSlot);
                    popup.putExtra("colorsArray", colors);
                    popup.putExtra("ID", v.getId());
                    popup.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivityForResult(popup, 200);
                }
            });
        }
    }

    /* Hide and Show Colorpicker Options */
    private void hideAndShow(int number){
        if (number == 5){
            btn[6].setVisibility(View.GONE);
            btn[7].setVisibility(View.GONE);
            btn[8].setVisibility(View.GONE);

        } else if (number == 6) {
            btn[6].setVisibility(View.VISIBLE);
            btn[7].setVisibility(View.GONE);
            btn[8].setVisibility(View.GONE);
        } else {
            btn[6].setVisibility(View.VISIBLE);
            btn[7].setVisibility(View.VISIBLE);
            btn[8].setVisibility(View.VISIBLE);
        }
    }

    private int[] getUnusedColors(int number){
        int[] c = new int[10-number];
        for (int i=9;i>=number;i--){
            c[9-i] = AllColors[i];
        }
        return c;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        // check if the request code is same as what is passed  here it is 2
        if(requestCode==200)
        {
            if(resultCode == 200){
                // fetch the message String
                final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                final SharedPreferences.Editor editor = settings.edit();
                Integer id = data.getIntExtra("ID", 1);
                TextView t = (TextView) findViewById(id);
                int tmp = AllColors[id-92];
                int tnp = AllColors[9-data.getIntExtra("COLORID",0)];
                t.setBackgroundResource(data.getIntExtra("COLOR", R.drawable.circle));
                AllColors[id-92] = tnp;
                AllColors[9-data.getIntExtra("COLORID",0)] = tmp;
                colors = getUnusedColors(getSeekbarColor(seekBarNumberOfColors.getProgress()));
                for (int i=0;i<10;i++){
                    editor.putInt("Color"+String.valueOf(i),AllColors[i]);
                    editor.apply();
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}