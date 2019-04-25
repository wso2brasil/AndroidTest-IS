package com.example.sampleis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sampleis.service.RestService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RestService loginService = new RestService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btLogin = findViewById(R.id.button_signin);
        Button btSignUp = findViewById(R.id.button_register);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText loginEdt = findViewById(R.id.login_edt);
                EditText passEdt = findViewById(R.id.password_edt);

                String login = loginEdt.getText().toString();
                String pass = passEdt.getText().toString();

                if (pass.isEmpty() || login.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Login and Pass cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    Map<String, Object> map = loginService.loginOAuthUser(login, pass);
                    Map<String, List<String>> headers = (Map<String, List<String>>) map.get("headers");
                    Integer statusCode = (Integer) map.get("statusCode");
                    JSONObject response = (JSONObject) map.get("response");

                    if (statusCode != null && statusCode.equals(200)) {
                        Toast.makeText(MainActivity.this, "Login efetuado com sucesso", Toast.LENGTH_SHORT).show();
                        RestService.TOKEN = response.getString( "access_token");
                        Intent it = new Intent(MainActivity.this, SuccessLogin.class);
                        startActivity(it);
                    } else if (statusCode == null || statusCode.equals(401) || statusCode.equals(403)) {
                        Toast.makeText(MainActivity.this, "Usuário não possui acesso", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Houve um erro", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, Register.class);
                startActivity(it);
            }
        });
    }
}
