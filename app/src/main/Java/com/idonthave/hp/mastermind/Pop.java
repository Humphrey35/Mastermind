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
public class Pop extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.text12);

        final Intent intent = getIntent();

        getWindow().setLayout(intent.getIntExtra("width", 10), intent.getIntExtra("height", 10));

        final int colors[] = intent.getIntArrayExtra("colorsArray");

//        TextView t = new TextView(this);
//        t.setText(String.valueOf(colors.length));
//        rl.addView(t);

        TextView slot[] = new TextView[colors.length];

        for (int i=0;i<slot.length;i++) {
            slot[i] = new TextView(this);
            rl.addView(slot[i]);
            //slot[i].setText(intent.getStringExtra("pin"));
            slot[i].setBackgroundResource(colors[i]);
            slot[i].setId((i * 1000)+1);
            slot[i].setMinimumWidth(1);
            slot[i].setWidth(intent.getIntExtra("slotWidth", 200));
            slot[i].setMinHeight(1);
            slot[i].setHeight(intent.getIntExtra("slotHeight", 200));
            slot[i].setIncludeFontPadding(false);
            slot[i].setGravity(Gravity.START);
            slot[i].setTag(i);
            slot[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, intent.getFloatExtra("textSize", 80.0f));
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) slot[i].getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
            if(i>0) {
                lp.addRule(RelativeLayout.RIGHT_OF,slot[i-1].getId());
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,0);
            } else {
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,1);
            }
            lp.setMargins(intent.getIntExtra("marginOfPopupSlot", 2)/2, intent.getIntExtra("marginOfPopupSlot", 2)/2,0,0);
            slot[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentColor = new Intent();
                    Integer color = (Integer) v.getTag();
                    intentColor.putExtra("ID", intent.getIntExtra("ID", 1));
                    intentColor.putExtra("COLOR", colors[color]);
                    intentColor.putExtra("COLORID", color);
                    setResult(200, intentColor);
                    finish();
                }
            });
        }
    }
}