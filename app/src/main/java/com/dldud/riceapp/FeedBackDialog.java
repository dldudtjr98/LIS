package com.dldud.riceapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import java.net.MalformedURLException;

public class FeedBackDialog extends AppCompatActivity {
    Button back, send;
    EditText editText;
    final String url = "http://52.78.18.156/public/fbupload.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_feed_back_dialog);

        back = findViewById(R.id.fbBackBtn);
        send = findViewById(R.id.fbSendBtn);
        editText = findViewById(R.id.fbEdit);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    PHPRequestFeedBack php = new PHPRequestFeedBack(url);
                    String result = php.PhPtest(editText.getText().toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
    }
}
