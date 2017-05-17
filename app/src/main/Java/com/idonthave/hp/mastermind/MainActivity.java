package com.idonthave.hp.mastermind;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends Activity {

    // Global Vars
    public static final String PREFS_NAME = "GameSettings";
    public static int numberOfColors;
    public static int numberOfSlots;
    public static int numberOfTries;
    public static boolean allowEmpty;
    public static boolean allowDuplicates;
    public static boolean allowHighscore;
    public static boolean againstPlayer;
    public static boolean continueGame;
    public static double highscore = 0;
    public static int colors[];
    public static String pin;
    public Integer guess = 1;
    TextView[][] btn;
    Integer[][] guesses;
    Integer[] hiddenAnswer;
    public static Integer answerID = 0;
    public static long startTime;
    public static String colorString;
    public static int[] AllColors;

    private DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mydb = new DBHelper(this);

        final Intent intent = getIntent();
        continueGame = intent.getBooleanExtra("continueGame", true);
        againstPlayer = intent.getBooleanExtra("againstPlayer",false);

        setContentView(R.layout.activity_main);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
        ScrollView sc = (ScrollView) findViewById(R.id.sc);
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

        //rl.setBackgroundColor(Color.WHITE);
        sc.setBackgroundColor(Color.WHITE);
        ll.setBackgroundColor(Color.WHITE);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        AllColors = new int[]{ settings.getInt("Color0",R.drawable.blue), settings.getInt("Color1",R.drawable.brown), settings.getInt("Color2",R.drawable.grass), settings.getInt("Color3",R.drawable.green), settings.getInt("Color4",R.drawable.orange), settings.getInt("Color5",R.drawable.pink), settings.getInt("Color6",R.drawable.purple), settings.getInt("Color7",R.drawable.red), settings.getInt("Color8",R.drawable.sky), settings.getInt("Color9",R.drawable.yellow) };

        for (int h=0;h<AllColors.length;h++){
            if (h==0){
                colorString = String.valueOf(AllColors[h]);
            } else {
                colorString += ","+String.valueOf(AllColors[h]);
            }
        }

        numberOfColors = settings.getInt("numberOfColors", 6);
        numberOfSlots = settings.getInt("numberOfSlots", 4);
        numberOfTries = settings.getInt("numberOfTries", 9)+2;
        allowDuplicates = settings.getBoolean("allowDuplicates", true);
        allowEmpty = settings.getBoolean("allowEmpty", false);
        if (allowEmpty){
            numberOfColors++;
        }
        allowHighscore = true;
        highscore = 0;

        if (continueGame){
            String[] savedConfig = mydb.getSavedgame();
            numberOfTries = Integer.valueOf(savedConfig[0]);
            numberOfSlots = Integer.valueOf(savedConfig[1]);
            numberOfColors = Integer.valueOf(savedConfig[2]);
            colorString = savedConfig[3];
            allowDuplicates = Boolean.valueOf(savedConfig[4]);
            allowEmpty = Boolean.valueOf(savedConfig[5]);
            allowHighscore = Boolean.valueOf(savedConfig[6]);
            String[] tmp = colorString.split(",");
            for (int i=0;i<10;i++){
                AllColors[i] = Integer.valueOf(tmp[i]);
            }
        } else {
            mydb.deleteAllSavedgame();
            mydb.deleteAllSavedmove();
            mydb.insertSavedgame(String.valueOf(numberOfTries), String.valueOf(numberOfColors),String.valueOf(numberOfSlots),colorString,String.valueOf(allowDuplicates),String.valueOf(allowEmpty),String.valueOf(allowHighscore),String.valueOf(highscore));
            mydb.close();
        }

        colors = new int[numberOfColors];

        for (int i=0;i<numberOfColors;i++){
            if(i==numberOfColors-1&&allowEmpty){
                colors[i] = R.drawable.active_circle;
            } else {
                colors[i] = AllColors[i];
            }
        }

        pin = settings.getString("pin", "●");

        /* Init of the Arrays
            btn: Board
            guesses: all guesses. needed for save and continue
            hiddenAnswer: Combination
         */
        btn = new TextView[numberOfTries][numberOfSlots];
        guesses = new Integer[numberOfTries][numberOfSlots];
        hiddenAnswer = new Integer[numberOfSlots];
        for (int i=0;i<guesses.length;i++){
            for (int j=0;j<guesses[i].length;j++){
                if (allowEmpty){
                    guesses[i][j] = numberOfColors-1;
                } else {
                    guesses[i][j] = 99;
                }
            }
        }


        // get an System Time that cannot(!) be changed (though changing the System Clock)
        startTime = System.nanoTime();

        // Random Combination (with dupes check)
        if(!againstPlayer) {
            if (!continueGame) {
                createRandomAnswer();
                mydb.deleteAllHidden();
                for (int i = 0; i < hiddenAnswer.length; i++) {
                    mydb.insertHidden(String.valueOf(hiddenAnswer[i]));
                }
                mydb.close();
            } else {
                ArrayList<String> h = mydb.getHidden();
                for (int i=0;i<h.size();i++) {
                    hiddenAnswer[i] = Integer.valueOf(h.get(i));
                }
                mydb.close();
            }
        } else {
            ArrayList<String> h = mydb.getHidden();
            for (int i=0;i<h.size();i++) {
                hiddenAnswer[i] = Integer.valueOf(h.get(i));
            }
            mydb.close();
        }

        // init Display Size and calculate sizes
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x;

        final int marginOfSlot = Math.round((width/(numberOfSlots+2))*0.1f); // 10% Margin of the Size of one Slot as Margin in between
        final int widthOfSlot = (width/(numberOfSlots+2))-marginOfSlot;
        final int marginOfPopupSlot = Math.round((width/(numberOfColors+1))*0.1f);
        final int widthOfPopup = (width/(numberOfColors+1))*numberOfColors;
        final int widthOfPopupSlot = (width/(numberOfColors+1))-marginOfPopupSlot;

        final int numberOfSmallPinsInRow;
        if (numberOfSlots > 4) {
            numberOfSmallPinsInRow = 3;
        } else {
            numberOfSmallPinsInRow = 2;
        }

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
            ToDo: Cleanup TextSize relevant Stuff
         */
        for (int i=0;i<btn.length;i++){
            for (int j=0;j<btn[i].length;j++){
                btn[i][j] = new TextView(this);
                rl.addView(btn[i][j]);
//                btn[i][j].setText(pin);
                if(i==0) btn[i][j].setBackgroundResource(R.drawable.grey);
                else btn[i][j].setBackgroundResource(R.drawable.circle);
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
                    btn[i][j].setBackgroundResource(R.drawable.active_circle);


                    btn[i][j].setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Intent popup = new Intent(MainActivity.this, Pop.class);
                            popup.putExtra("width", widthOfPopup);
                            popup.putExtra("height", widthOfPopupSlot);
                            popup.putExtra("slotWidth", widthOfPopupSlot);
                            popup.putExtra("slotHeight", widthOfPopupSlot);
                            popup.putExtra("textSize", textSizeOfPopupSlot);
                            popup.putExtra("marginOfPopupSlot", marginOfPopupSlot);
                            popup.putExtra("colorsArray", colors);
                            popup.putExtra("pin", pin);
                            popup.putExtra("ID", v.getId());
                            popup.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivityForResult(popup, 200);
                        }
                    });
                }
            }
            /* This part is only for Layout Consistency */
            if (i==0){
                for (int k=0;k<(numberOfSlots/2);k++){
                    answerID++;
                    for(int l=0;l<(numberOfSlots/2);l++){
                        TextView answer = new TextView(this);
                        rl.addView(answer);
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
        final ImageButton myFab = (ImageButton) findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean completeRow = true;
                if (!allowEmpty) {
                    for (int j = 0; j < btn[guess].length; j++) {
                        if (guesses[guess][j] == 99){
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
                    boolean[] knownAnswer = new boolean[numberOfSlots];
                    for (int i=0;i<numberOfSlots;i++){
                        knownPins[i] = false;
                        knownAnswer[i] = false;
                    }
                    for (int i=0;i<btn[guess].length;i++) {
                        if (guesses[guess][i].equals(hiddenAnswer[i])) {
                            knownPins[i] = true;
                            knownAnswer[i] = true;
                            black++;
                            highscore = highscore + Math.pow(2,37-guess);
                            if(allowDuplicates){
                                double addToHighscore = (Math.pow(2,37-guess)/4);
                                if(addToHighscore > 1){
                                    highscore = highscore + addToHighscore;
                                } else {
                                    highscore = highscore + 1;
                                }

                            }
                            if(allowEmpty){
                                double addToHighscore = (Math.pow(2,37-guess)/8);
                                if(addToHighscore > 1){
                                    highscore = highscore + addToHighscore;
                                } else {
                                    highscore = highscore + 1;
                                }

                            }
                        }
                    }
                    for (int i=0;i<btn[guess].length;i++) {
                        for (int j = 0; j < btn[guess].length; j++) {
                            if (guesses[guess][i].equals(hiddenAnswer[j]) && i != j && !knownPins[j] && !knownAnswer[i]) {
                                knownPins[j] = true;
                                knownAnswer[i] = true;
                                white++;
                                highscore = highscore + Math.pow(2,36-guess);
                                if(allowDuplicates){
                                    double addToHighscore = (Math.pow(2,36-guess)/4);
                                    if(addToHighscore > 1){
                                        highscore = highscore + addToHighscore;
                                    } else {
                                        highscore = highscore + 1;
                                    }
                                }
                                if(allowEmpty){
                                    double addToHighscore = (Math.pow(2,36-guess)/8);
                                    if(addToHighscore > 1){
                                        highscore = highscore + addToHighscore;
                                    } else {
                                        highscore = highscore + 1;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    if(black == numberOfSlots){
                        long estimatedTime = System.nanoTime() - startTime;
                        highscore = highscore + (Math.pow(2,37-guess)*(numberOfSlots/2));
                        if (highscore > 999999999999.0){
                            highscore = 999999999999.0;
                        }
                        Intent win = new Intent(MainActivity.this, Win.class);
                        win.putExtra("width", widthOfPopup);
                        win.putExtra("height", widthOfPopupSlot);
                        win.putExtra("slotWidth", widthOfPopupSlot);
                        win.putExtra("slotHeight", widthOfPopupSlot);
                        win.putExtra("textSize", textSizeOfPopupSlot);
                        win.putExtra("marginOfPopupSlot", marginOfPopupSlot);
                        win.putExtra("allowHighscore", allowHighscore);
                        win.putExtra("pin", pin);
                        win.putExtra("WL","W");
                        win.putExtra("estimatedTime", estimatedTime);
                        win.putExtra("highscore",highscore);
                        //popup.putExtra("ID", v.getId());
                        for (int j=0;j<btn[0].length;j++){
                            btn[0][j].setBackgroundResource(colors[hiddenAnswer[j]]);
                        }
                        win.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(win, 300);
                    }
                    for (int k=0;k<(numberOfSlots/2);k++){
                        answerID++;
                        for(int l=0;l<numberOfSmallPinsInRow;l++){
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
                    if(guess>=btn.length){
                        long estimatedTime = System.nanoTime() - startTime;
                        Intent win = new Intent(MainActivity.this, Win.class);
                        win.putExtra("width", widthOfPopup);
                        win.putExtra("height", widthOfPopupSlot);
                        win.putExtra("slotWidth", widthOfPopupSlot);
                        win.putExtra("slotHeight", widthOfPopupSlot);
                        win.putExtra("textSize", textSizeOfPopupSlot);
                        win.putExtra("marginOfPopupSlot", marginOfPopupSlot);
                        win.putExtra("pin", pin);
                        win.putExtra("estimatedTime", estimatedTime);
                        win.putExtra("WL","L");
                        win.putExtra("allowHighscore", allowHighscore);
                        //popup.putExtra("ID", v.getId());
                        for (int j=0;j<btn[0].length;j++){
                            btn[0][j].setBackgroundResource(colors[hiddenAnswer[j]]);
                        }
                        win.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(win, 300);
                    } else {
                        if (allowEmpty) {
                            for (int j = 0; j < btn[guess - 1].length; j++) {
                                if (guesses[guess - 1][j] == numberOfColors - 1) {
                                    btn[guess - 1][j].setBackgroundResource(R.drawable.circle);
                                }
                            }
                        }
                        for (int j = 0; j < btn[guess].length; j++) {
                            btn[guess][j].setBackgroundResource(R.drawable.active_circle);
                            btn[guess][j].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent popup = new Intent(MainActivity.this, Pop.class);
                                    popup.putExtra("width", widthOfPopup);
                                    popup.putExtra("height", widthOfPopupSlot);
                                    popup.putExtra("slotWidth", widthOfPopupSlot);
                                    popup.putExtra("slotHeight", widthOfPopupSlot);
                                    popup.putExtra("textSize", textSizeOfPopupSlot);
                                    popup.putExtra("marginOfPopupSlot", marginOfPopupSlot);
                                    popup.putExtra("colorsArray", colors);
                                    popup.putExtra("pin", pin);
                                    popup.putExtra("ID", v.getId());
                                    popup.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivityForResult(popup, 200);
                                }
                            });
                        }
                        // Savegame & Savemode
                        if (!continueGame){
                            for (int g=0;g<numberOfSlots;g++){
                                mydb.insertSavedmove(String.valueOf(guesses[guess-1][g]),String.valueOf(guess-1),String.valueOf(g));
                            }
                            mydb.close();
                        }
                    }
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (Integer aHiddenAnswer : hiddenAnswer) {
                        sb.append(aHiddenAnswer);
                    }
                    Toast.makeText(getApplicationContext(), sb.toString(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        /*
            undo Button.
            Set all colors of this move back to black, set all colors of the next move to black and delete the answer pins

            Create AnswerPin Layout
            ID Convention: answerID*10+btn[guess].length+1+l+9000

            add clickListener to the next Row
         */
        ImageButton myFab2 = (ImageButton) findViewById(R.id.undo);
        myFab2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (guess > 1) {
                    allowHighscore = false;
                    mydb.deleteAllSavedgame();
                    mydb.insertSavedgame(String.valueOf(numberOfTries), String.valueOf(numberOfColors),String.valueOf(numberOfSlots),colorString,String.valueOf(allowDuplicates),String.valueOf(allowEmpty),String.valueOf(allowHighscore),String.valueOf(highscore));
                    mydb.close();
                    RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
                    for (int j = 0; j < btn[guess].length; j++) {
                        btn[guess][j].setBackgroundResource(R.drawable.circle);
                    }
                    for (int j = 0; j < btn[guess].length; j++) {
                        btn[guess][j].setClickable(false);
                    }
                    guess--;
                    for (int j = 0; j < btn[guess].length; j++) {
                        btn[guess][j].setBackgroundResource(R.drawable.circle);
                        guesses[guess][j] = 99;
                    }
                    for (int j = 0; j < btn[guess].length; j++) {
                        btn[guess][j].setBackgroundResource(R.drawable.active_circle);
                        btn[guess][j].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent popup = new Intent(MainActivity.this, Pop.class);
                                popup.putExtra("width", widthOfPopup);
                                popup.putExtra("height", widthOfPopupSlot);
                                popup.putExtra("slotWidth", widthOfPopupSlot);
                                popup.putExtra("slotHeight", widthOfPopupSlot);
                                popup.putExtra("textSize", textSizeOfPopupSlot);
                                popup.putExtra("marginOfPopupSlot", marginOfPopupSlot);
                                popup.putExtra("colorsArray", colors);
                                popup.putExtra("pin", pin);
                                popup.putExtra("ID", v.getId());
                                popup.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivityForResult(popup, 200);
                            }
                        });
                    }
                    for (int k = 0; k < (numberOfSlots / 2); k++) {
                        for (int l = 0; l < (numberOfSlots / 2); l++) {
                            rl.removeView(findViewById(answerID * 10 + btn[guess].length + 1 + l + 9000));
                        }
                        answerID--;
                    }
                }
            }
        });
        if (continueGame){
            ArrayList<String> moves = mydb.getSavedmove();
            mydb.close();
            for (int i=0;i<moves.size();i=i+3){
                btn[Integer.valueOf(moves.get(i+1))][Integer.valueOf(moves.get(i+2))].setBackgroundResource(AllColors[Integer.valueOf(moves.get(i))]);
                guesses[Integer.valueOf(moves.get(i+1))][Integer.valueOf(moves.get(i+2))] = Integer.valueOf(moves.get(i));
                if (i+4<moves.size()){
                    if (!moves.get(i+1).equals(moves.get(i+4))){
                        myFab.performClick();
                    }
                }
            }
            myFab.performClick();
            continueGame = false;
        }
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
    Fill hiddenAnswer with random ints between 0 and numberOfColors
     */
    protected void createRandomAnswer(){
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
    }

    /*
        Get information of the Popup and set color.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 200) {
            if (resultCode == 200) {
                // fetch the message String
                Integer id = data.getIntExtra("ID", 1);
                TextView t = (TextView) findViewById(id);
                t.setBackgroundResource(data.getIntExtra("COLOR", R.drawable.circle));
                guesses[guess][(id % 10) - 1] = data.getIntExtra("COLORID", 0);
            }
        }
        if (requestCode == 300) {
            if (resultCode == 200) {
                recreate();
            }
        }
    }
}
