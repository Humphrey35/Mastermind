package com.idonthave.hp.mastermind;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/* Pop Class to Choose Color */
public class Win extends Activity {

    private DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mydb = new DBHelper(this);

        setContentView(R.layout.popup);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.text12);

        final Intent intent = getIntent();

        getWindow().setLayout(intent.getIntExtra("width", 10), intent.getIntExtra("height", 10)+500);

//        TextView t = new TextView(this);
//        t.setText(String.valueOf(colors.length));
//        rl.addView(t);

        TextView text = new TextView(this);
        rl.addView(text);

        if(intent.getStringExtra("WL").equals("W")) {
            if(intent.getBooleanExtra("allowHighscore", false)){
                if(!mydb.insertHighscore(String.format(Locale.GERMANY,"%012.0f",intent.getDoubleExtra("highscore", 0)))){
                    Toast.makeText(getApplicationContext(), "Error saving to Database",
                            Toast.LENGTH_LONG).show();
                }
            }
            text.setText(" Sie haben gewonnen!\r\nIhr Highscore war " + String.format(Locale.GERMANY,"%012.0f",intent.getDoubleExtra("highscore", 0)));
        } else {
            text.setText("Sie haben verloren!\r\n");
        }


        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentColor = new Intent();
//                Integer color = (Integer) v.getTag();
//                    intentColor.putExtra("ID", intent.getIntExtra("ID", 1));
//                intentColor.putExtra("COLOR", colors[color]);
//                intentColor.putExtra("COLORID", color);
                setResult(200, intentColor);
                finish();
            }
        });
    }
}