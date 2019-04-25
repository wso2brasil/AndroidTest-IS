package com.example.sampleis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.sampleis.service.RestService;

public class SuccessLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_login);

        TextView tx = findViewById(R.id.reponseinfo);

        RestService rs = new RestService();
        String userInfo = rs.getUserInfo();

        tx.setText(userInfo);
    }
}
