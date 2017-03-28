package com.idonthave.hp.mastermind;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

public class MainActivity extends Activity {

    // Global Vars
    public static final String PREFS_NAME = "GameSettings";
    public static Integer numberOfColors;
    public static Integer numberOfSlots;
    public static Integer numberOfTries;
    public static boolean allowEmpty;
    public static boolean allowDuplicates;
    public static boolean againstPlayer;
    public static String colors[];
    public static String pin;
    public Integer guess = 1;
    TextView[][] btn;
    Integer[][] guesses;
    Integer[] hiddenAnswer;
    public static Integer answerID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);

        // Settings ToDo: Cleanup. Color no longer needed.
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        numberOfColors = settings.getInt("numberOfColors", 6);
        numberOfSlots = settings.getInt("numberOfSlots", 4);
        numberOfTries = settings.getInt("numberOfTries", 12);
        againstPlayer = settings.getBoolean("againstPlayer", false);
        allowDuplicates = settings.getBoolean("allowDuplicates", false);
        final String c = settings.getString("colors", "#ff0000@#0000ff@#ffff00@#ffa500@#008000@#a52a2a");
        allowEmpty = settings.getBoolean("allowEmpty", false);
        colors = c.split("@");
        pin = settings.getString("pin", "●");

        /* Init of the Arrays
            btn: Board
            guesses: all guesses. needed for save and continue
            hiddenAnswer: Combination
         */
        btn = new TextView[numberOfTries][numberOfSlots];
        guesses = new Integer[numberOfTries][numberOfSlots];
        hiddenAnswer = new Integer[numberOfSlots];

        // Random Combination (with dupes check): ToDo: Duell Modus
        if(!againstPlayer) {
            Random rand = new Random();
            for (int i = 0; i < numberOfSlots; i++) {
                int n = rand.nextInt(numberOfColors);
                if(allowDuplicates) {
                    hiddenAnswer[i] = n;
                } else {
                    boolean dupes = false;
                    for (int j = 0;j<i;j++){
                        if (hiddenAnswer[j].equals(n)){
                            dupes = true;
                            i--;
                            break;
                        }
                    }
                    if (!dupes){
                        hiddenAnswer[i] = n;
                    }
                }
            }
        } else {
            // ToDo
        }

        // init Display Size and calculate sizes
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x;

        final int marginOfSlot = Math.round((width/(numberOfSlots+1))*0.1f); // 10% Margin of the Size of one Slot as Margin in between
        final int widthOfSlot = (width/(numberOfSlots+1))-marginOfSlot;
        final int marginOfPopupSlot = Math.round((width/(numberOfColors+1))*0.1f);
        final int widthOfPopup = (width/(numberOfColors+1))*numberOfColors;
        final int widthOfPopupSlot = (width/(numberOfColors+1))-marginOfPopupSlot;

        // ToDo: Lets keep it for now. can be delete for final.
        final float textSizeOfPopupSlot = correctWidth(pin, Math.round(widthOfPopupSlot/1.2f)); // no Longer needed; Scales TextSize
        final float textSizeOfSlot = correctWidth(pin, Math.round(widthOfSlot/1.2f)); // no Longer needed; Scales TextSize
        final float textSizeAnswer = correctWidth("○", Math.round((widthOfSlot/numberOfSlots)/1.2f)); // no Longer needed; Scales TextSize

        /* Build the Board!
            First row reserved for Answer.
            clickListener only for a single Row.
            BoardSlots id convention: i*10+j+1 ==   1 2 3 4
                                                    11 12 13 14
                                                    etc
            ToDo: Show Answer when done.
            ToDo: Cleanup TextSize relevant Stuff
         */
        for (int i=0;i<btn.length;i++){
            for (int j=0;j<btn[i].length;j++){
                btn[i][j] = new TextView(this);
                rl.addView(btn[i][j]);
//                btn[i][j].setText(pin);
                if(i==0) btn[i][j].setBackgroundResource(R.drawable.grey);
                else btn[i][j].setBackgroundResource(R.drawable.circle);
                btn[i][j].setTextColor(Color.parseColor("#000000"));
                btn[i][j].setId(i*10+j+1);
                btn[i][j].setMinimumWidth(1);
                btn[i][j].setWidth(widthOfSlot);
                btn[i][j].setMinHeight(1);
                btn[i][j].setHeight(widthOfSlot);
                btn[i][j].setIncludeFontPadding(false);
                btn[i][j].setGravity(Gravity.CENTER_VERTICAL);
                btn[i][j].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeOfSlot);
                LayoutParams lp = (LayoutParams) btn[i][j].getLayoutParams();
                lp.setMargins(marginOfSlot, marginOfSlot,0,0);
                if(j==0){
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,1);
                } else {
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,0);
                    lp.addRule(RelativeLayout.RIGHT_OF,btn[i][j-1].getId());
                }
                if(i==0){
                    lp.addRule(RelativeLayout.ALIGN_PARENT_TOP,1);
                } else {
                    lp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
                    lp.addRule(RelativeLayout.BELOW,btn[i-1][j].getId());
                }
                btn[i][j].setLayoutParams(lp);
                if (i==guess){
                    btn[i][j].setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Intent popup = new Intent(MainActivity.this, Pop.class);
                            popup.putExtra("width", widthOfPopup);
                            popup.putExtra("height", widthOfPopupSlot);
                            popup.putExtra("slotWidth", widthOfPopupSlot);
                            popup.putExtra("slotHeight", widthOfPopupSlot);
                            popup.putExtra("textSize", textSizeOfPopupSlot);
                            popup.putExtra("colors", c);
                            popup.putExtra("pin", pin);
                            popup.putExtra("ID", v.getId());
                            startActivityForResult(popup, 200);
                        }
                    });
                }
            }
            /* toDo: Make invisible. This is only for Layout Consistency */
            if (i==0){
                for (int k=0;k<(numberOfSlots/2);k++){
                    answerID++;
                    for(int l=0;l<(numberOfSlots/2);l++){
                        TextView answer = new TextView(this);
                        rl.addView(answer);
                        answer.setBackgroundResource(R.drawable.circle);
                        answer.setTextColor(Color.parseColor("#000000"));
                        answer.setId(answerID*10+btn[i].length+1+l+9000);
                        answer.setMinimumWidth(1);
                        answer.setWidth(widthOfSlot/(numberOfSlots/2));
                        answer.setMinHeight(1);
                        answer.setHeight(widthOfSlot/(numberOfSlots/2));
                        answer.setIncludeFontPadding(false);
                        answer.setGravity(Gravity.CENTER_VERTICAL);
                        answer.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeAnswer);
                        LayoutParams lp = (LayoutParams) answer.getLayoutParams();
                        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,0);
                        lp.setMargins(marginOfSlot/2, marginOfSlot/2,0,0);
                        if (l==0) {
                            lp.addRule(RelativeLayout.RIGHT_OF, btn[i][btn[i].length - 1].getId());
                        } else {
                            lp.addRule(RelativeLayout.RIGHT_OF, answerID*10+btn[i].length+1+(l-1)+9000);
                        }
                        if(k==0){
                            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP,1);
                        } else {
                            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
                            lp.addRule(RelativeLayout.BELOW,(answerID-1)*10+btn[i].length+1+l+9000);
                        }
                        answer.setLayoutParams(lp);
                    }
                }
            }
        }

        /*
            Check Guess Button.
            Check for black pins and flag known
            Check for white pins and flag known [only one answer pin per guess]

            Create AnswerPin Layout
            ID Convention: answerID*10+btn[guess].length+1+l+9000

            add clickListener to the next Row
         */
        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean completeRow = true;
                if (!allowEmpty) {
                    for (int j = 0; j < btn[guess].length; j++) {
                        if (btn[guess][j].getCurrentTextColor() == Color.parseColor("#000000")) {
                            completeRow = false;
                        }
                    }
                }
                if (completeRow){
                    for (int j=0;j<btn[guess].length;j++){
                        btn[guess][j].setClickable(false);
                    }
                    int black = 0;
                    int white = 0;
                    boolean[] knownPins = new boolean[numberOfSlots];
                    for (int i=0;i<numberOfSlots;i++){
                        knownPins[i] = false;
                    }
                    for (int i=0;i<btn[guess].length;i++) {
                        if (guesses[guess][i].equals(hiddenAnswer[i])) {
                            knownPins[i] = true;
                            black++;
                        }
                    }
                    for (int i=0;i<btn[guess].length;i++) {
                        for (int j = 0; j < btn[guess].length; j++) {
                            if (guesses[guess][i].equals(hiddenAnswer[j]) && i != j && !knownPins[j]) {
                                knownPins[j] = true;
                                white++;
                                break;
                            }
                        }
                    }
                    for (int k=0;k<(numberOfSlots/2);k++){
                        answerID++;
                        for(int l=0;l<(numberOfSlots/2);l++){
                            TextView answer = new TextView(getApplicationContext());
                            RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
                            rl.addView(answer);
                            if (black>0) {
                                answer.setBackgroundResource(R.drawable.circle);
                                black--;
                            } else if (white>0) {
                                answer.setBackgroundResource(R.drawable.white);
                                white--;
                            }
                            answer.setTextColor(Color.parseColor("#000000"));
                            answer.setId(answerID*10+btn[guess].length+1+l+9000);
                            answer.setMinimumWidth(1);
                            answer.setWidth(widthOfSlot/(numberOfSlots/2));
                            answer.setMinHeight(1);
                            answer.setHeight(widthOfSlot/(numberOfSlots/2));
                            answer.setIncludeFontPadding(false);
                            answer.setGravity(Gravity.CENTER_VERTICAL);
                            answer.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeAnswer);
                            LayoutParams lp = (LayoutParams) answer.getLayoutParams();
                            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,0);
                            lp.setMargins(marginOfSlot/2, marginOfSlot/2,0,0);
                            if (l==0) {
                                lp.addRule(RelativeLayout.RIGHT_OF, btn[guess][btn[guess].length - 1].getId());
                            } else {
                                lp.addRule(RelativeLayout.RIGHT_OF, answerID*10+btn[guess].length+1+(l-1)+9000);
                            }
                            if(k==0){
                                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
                                lp.addRule(RelativeLayout.BELOW,btn[guess-1][0].getId());
                            } else {
                                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
                                lp.addRule(RelativeLayout.BELOW,(answerID-1)*10+btn[guess].length+1+l+9000);
                            }
                            answer.setLayoutParams(lp);
                        }
                    }
                    guess++;
                    for (int j=0;j<btn[guess].length;j++){
                        btn[guess][j].setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                Intent popup = new Intent(MainActivity.this, Pop.class);
                                popup.putExtra("width", widthOfPopup);
                                popup.putExtra("height", widthOfPopupSlot);
                                popup.putExtra("slotWidth", widthOfPopupSlot);
                                popup.putExtra("slotHeight", widthOfPopupSlot);
                                popup.putExtra("textSize", textSizeOfPopupSlot);
                                popup.putExtra("colors", c);
                                popup.putExtra("pin", pin);
                                popup.putExtra("ID", v.getId());
                                startActivityForResult(popup, 200);
                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), String.valueOf(hiddenAnswer[0])+ " " + String.valueOf(hiddenAnswer[1])+ " " + String.valueOf(hiddenAnswer[2])+ " " + String.valueOf(hiddenAnswer[3]),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /*
        Scale TextSize. No Longer Needed?
     */
    public float correctWidth(String text, int desiredWidth)
    {
        Paint paint = new Paint();
        Rect bounds = new Rect();
        TextView textView = new TextView(this);

        paint.setTypeface(textView.getTypeface());
        float textSize = 500.0f;
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), bounds);

        while (bounds.width() > desiredWidth)
        {
            textSize--;
            paint.setTextSize(textSize);
            paint.getTextBounds(text, 0, text.length(), bounds);
        }
        //textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        return textSize;

    }

    /*
        Get information of the Popup and set color.
     */
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
                t.setBackgroundResource(data.getIntExtra("COLOR", R.drawable.circle));
                t.setTextColor(Color.parseColor(colors[data.getIntExtra("COLORID", 0)]));
                guesses[guess][(id%10)-1] = data.getIntExtra("COLORID", 0);
            }
        }
    }




}
