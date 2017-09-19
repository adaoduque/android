 package com.salaoideal.adusilne.changetextviewcolorfont;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

 public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar                 =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView textView = findViewById(R.id.edtColor);
        textView.setTextColor(Color.RED);
        textView.setTextColor(Color.RED);

        RadioGroup rgColor = findViewById(R.id.rgSelectColor);
        rgColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbVermelho:
                        Log.d("DUQUE-LOG", "TEXTO VERMELHO");
                        textView.setTextColor(Color.RED);
                        break;
                    case R.id.rbAzul:
                        Log.d("DUQUE-LOG", "TEXTO AZUL");
                        textView.setTextColor(Color.BLUE);
                        break;
                    case R.id.rbVerde:
                        Log.d("DUQUE-LOG", "TEXTO VERDE");
                        textView.setTextColor(Color.GREEN);
                        break;
                }
            }
        });

    }
}
