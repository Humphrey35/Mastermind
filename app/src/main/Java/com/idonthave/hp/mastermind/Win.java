package com.idonthave.hp.mastermind;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/* Pop Class to Choose Color */
public class Win extends Activity {

    /* ToDo:
        colors needs to be fetched from the intent!!!!
     */
    final Integer colors[] = { R.drawable.blue , R.drawable.red, R.drawable.brown, R.drawable.green, R.drawable.yellow, R.drawable.orange };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.text12);

        final Intent intent = getIntent();

        getWindow().setLayout(intent.getIntExtra("width", 10), intent.getIntExtra("height", 10)+100);

        final String color = intent.getStringExtra("colors");

//        TextView t = new TextView(this);
//        t.setText(String.valueOf(colors.length));
//        rl.addView(t);

        TextView text = new TextView(this);
        rl.addView(text);

        text.setText(String.valueOf(intent.getLongExtra("estimatedTime", 100)/1000000));
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