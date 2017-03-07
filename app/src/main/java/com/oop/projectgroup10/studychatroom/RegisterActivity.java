package com.oop.projectgroup10.studychatroom;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {


    Boolean passwordOK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //To match that both passwords are ok
        EditText passwordConfirm = (EditText) findViewById(R.id.confirmpassword);
        final TextView passMatch = (TextView) findViewById(R.id.passMatch);
        TextWatcher passwordValidation = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!getPassword().equals(getPasswordConfirm())) {
                    passwordOK = false;
                    passMatch.setTextColor(Color.parseColor("#FE0417"));
                    passMatch.setText("Does not Match");
                } else {
                    passwordOK = true;
                    passMatch.setTextColor(Color.parseColor("#28B463"));
                    passMatch.setText("Match");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        passwordConfirm.addTextChangedListener(passwordValidation);
    }

    public void submitRegistration(View v) {

        if (getFname().isEmpty() || getLname().isEmpty() || getUsername().isEmpty() || getEmail().isEmpty() || getUserType().isEmpty() || !passwordOK) {
            Toast.makeText(this, "Please note that all the fields are required. Passwords must match", Toast.LENGTH_LONG).show();
        } else {
            new SubmitLoginAndSignup(v.getContext(), this).execute("register", getUsername(), getPassword(), getFname(), getLname(), getEmail(), getUserType());
        }

    }
//TODO REGEX to check email

    //getters
    public String getFname() {
        EditText fname = (EditText) findViewById(R.id.fname);

        return fname.getText().toString();
    }

    public String getLname() {
        EditText lname = (EditText) findViewById(R.id.lname);

        return lname.getText().toString();
    }

    public String getEmail() {
        EditText email = (EditText) findViewById(R.id.emailaddress);
        return email.getText().toString();
    }

    public String getUserType() {
        Spinner userType = (Spinner) findViewById(R.id.usertype);
        String type = userType.getSelectedItem().toString();

        switch (type) {
            case "Administrator":
                type = "1";
                break;
            case "Teacher":
                type = "2";
                break;
            case "Student":
                type = "3";
                break;

        }
        return type;
    }

    public String getPassword() {
        EditText password = (EditText) findViewById(R.id.passwordR);
        return password.getText().toString();
    }


    public String getUsername() {
        EditText username = (EditText) findViewById(R.id.usernameR);
        return username.getText().toString();
    }

    public String getPasswordConfirm() {
        EditText passwordConfirm = (EditText) findViewById(R.id.confirmpassword);

        return passwordConfirm.getText().toString();
    }
}
