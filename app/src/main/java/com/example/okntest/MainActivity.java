package com.example.okntest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public EditText f;
    static int freq=8;
    String p="8";
    public Button but1;
    public void init(){
        try{
            but1= (Button)findViewById(R.id.but1);
            but1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    f= (EditText)findViewById(R.id.text1) ;
                    if(f.getText().toString()!="") {
                        p = f.getText().toString();
                    }
                    freq= Integer.parseInt(p);
                    Intent toy = new Intent(MainActivity.this,GameView.class);
                    startActivity(toy);
                }
            });
        }catch(Error e){
            System.out.println(e);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

}
