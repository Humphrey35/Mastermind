package com.idonthave.hp.mastermind;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by rrRRrr on 17.05.2017.
 */

public class DuellActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "GameSettings";
    public static int numberOfColors;
    public static int numberOfSlots;
    public static int numberOfTries;
    public static boolean allowEmpty;
    public static boolean allowDuplicates;
    private TextView[] btn;
    private int AllColors[];
    private int colors[];
    private Button start;
    private int hidden[];
    private DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.duell_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Farbcode festlegen");
        }

        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        mydb = new DBHelper(this);

        numberOfColors = settings.getInt("numberOfColors", 6);
        numberOfSlots = settings.getInt("numberOfSlots", 4);
        numberOfTries = settings.getInt("numberOfTries", 9) + 2;
        allowDuplicates = settings.getBoolean("allowDuplicates", true);
        allowEmpty = settings.getBoolean("allowEmpty", false);
        if (allowEmpty) {
            numberOfColors++;
        }
        hidden = new int[numberOfSlots];

        colors = new int[numberOfColors];

        AllColors = new int[]{ settings.getInt("Color0", R.drawable.blue), settings.getInt("Color1", R.drawable.brown), settings.getInt("Color2", R.drawable.grass), settings.getInt("Color3", R.drawable.green), settings.getInt("Color4", R.drawable.orange), settings.getInt("Color5", R.drawable.pink), settings.getInt("Color6", R.drawable.purple), settings.getInt("Color7", R.drawable.red), settings.getInt("Color8", R.drawable.sky), settings.getInt("Color9", R.drawable.yellow) };

        for (int i=0;i<numberOfColors;i++){
            if(i==numberOfColors-1&&allowEmpty){
                colors[i] = R.drawable.active_circle;
            } else {
                colors[i] = AllColors[i];
            }
        }

        for (int i=0;i<hidden.length;i++){
            if (allowEmpty){
                hidden[i] = numberOfColors-1;
            } else {
                hidden[i] = 99;
            }
        }

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rlduell);
        // init Display Size and calculate sizes
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x;

        final int marginOfSlot = Math.round((width/numberOfSlots)*0.1f); // 10% Margin of the Size of one Slot as Margin in between
        final int widthOfSlot = (width/numberOfSlots)-marginOfSlot;
        final int marginOfPopupSlot = Math.round((width/(numberOfColors+1))*0.1f);
        final int widthOfPopup = (width/(numberOfColors+1))*numberOfColors;
        final int widthOfPopupSlot = (width/(numberOfColors+1))-marginOfPopupSlot;

        btn = new TextView[numberOfSlots];

        for (int i=1;i<=numberOfSlots;i++) {
            btn[i-1] = new TextView(this);
            rl.addView(btn[i-1]);
            btn[i-1].setBackgroundResource(R.drawable.active_circle);
            btn[i-1].setId(1+i+90);
            btn[i-1].setMinimumWidth(1);
            btn[i-1].setWidth(widthOfSlot);
            btn[i-1].setMinHeight(1);
            btn[i-1].setHeight(widthOfSlot);
            btn[i-1].setIncludeFontPadding(false);
            btn[i-1].setGravity(Gravity.CENTER_VERTICAL);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) btn[i-1].getLayoutParams();
            lp.setMargins(marginOfSlot, 250, 0, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            if (i>1) {
                lp.addRule(RelativeLayout.RIGHT_OF, btn[i - 2].getId());
            } else {
                lp.addRule(RelativeLayout.RIGHT_OF, R.id.textView5);
            }
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            lp.addRule(RelativeLayout.BELOW, R.id.seekBar);
            btn[i-1].setLayoutParams(lp);
            btn[i-1].setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent popup = new Intent(DuellActivity.this, Pop.class);
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

        start = (Button) findViewById(R.id.button);

        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean completeRow = true;
                if (!allowEmpty) {
                    for (int j = 0; j < btn.length; j++) {
                        if (hidden[j] == 99){
                            completeRow = false;
                        }
                    }
                }
                boolean noDuplicates = true;
                if (!allowDuplicates){
                    for (int i=0;i<btn.length;i++){
                        for (int j=i+1;j<btn.length;j++){
                            if (hidden[i] == hidden[j]){
                                noDuplicates = false;
                            }
                        }
                    }
                }
                if (completeRow && noDuplicates){
                    Intent intent = new Intent(DuellActivity.this, MainActivity.class);
                    intent.putExtra("continueGame",false);
                    intent.putExtra("againstPlayer",true);
                    mydb.deleteAllHidden();
                    for (int i=0;i<numberOfSlots;i++){
                        mydb.insertHidden(String.valueOf(hidden[i]));
                    }
                    mydb.close();
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Farbcode (noch) nicht zulässig",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
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
                Integer id = data.getIntExtra("ID", 1);
                TextView t = (TextView) findViewById(id);
                if (allowDuplicates){
                    t.setBackgroundResource(data.getIntExtra("COLOR", R.drawable.circle));
                    hidden[id-92] = data.getIntExtra("COLORID",0);
                } else {
                    Boolean alreadyUsed = false;
                    for (int i=0;i<numberOfSlots;i++){
                        if (hidden[i] == data.getIntExtra("COLORID",0)){
                            alreadyUsed = true;
                        }
                    }
                    if (!alreadyUsed){
                        t.setBackgroundResource(data.getIntExtra("COLOR", R.drawable.circle));
                        hidden[id-92] = data.getIntExtra("COLORID",0);
                    } else {
                        Toast.makeText(getApplicationContext(), "Duplikate nicht zulässig",
                                Toast.LENGTH_LONG).show();
                    }
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
