package com.example.sampleis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sampleis.service.RestService;

import org.json.JSONObject;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final RestService rs = new RestService();
        setContentView(R.layout.activity_register);

        Button btn = findViewById(R.id.createUserBtn);
        final EditText username = findViewById(R.id.username);
        final EditText password = findViewById(R.id.password);
        final EditText givenname = findViewById(R.id.givenname);
        final EditText lastname = findViewById(R.id.lastname);
        final EditText mobile = findViewById(R.id.mobile);
        final EditText email = findViewById(R.id.email);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonUser = rs.createJsonUser(username.getText().toString(),
                        password.getText().toString(),
                        givenname.getText().toString(),
                        email.getText().toString(),
                        lastname.getText().toString(),
                        mobile.getText().toString());

                boolean created = rs.createNewUser(jsonUser);

                if (created) {
                    Toast.makeText(Register.this, "O usuário foi criado com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Register.this, "O usuário não pode ser criado. Verifique os logs.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
