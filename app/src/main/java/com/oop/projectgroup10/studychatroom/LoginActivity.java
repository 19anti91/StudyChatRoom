package com.oop.projectgroup10.studychatroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    public void register(View v) {
        Intent goToRegister = new Intent(v.getContext(), RegisterActivity.class);
        v.getContext().startActivity(goToRegister);
    }

    public void submitLogin(View v) {

        new SubmitLoginAndSignup(v.getContext()).execute("login", getUsername(), getPassword());
    }

    public String getUsername() {
        EditText username = (EditText) findViewById(R.id.usernameL);
        return username.getText().toString();
    }

    public String getPassword() {
        EditText password = (EditText) findViewById(R.id.passwordL);
        return password.getText().toString();
    }
}
