package com.oop.projectgroup10.studychatroom;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateRoom extends AppCompatActivity {

    boolean passwordOK = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Room");
        actionBar.setDisplayHomeAsUpEnabled(true);


        //To check that both passwords are ok
        EditText passwordConfirm = (EditText) findViewById(R.id.roomPassConfCr);
        final TextView passMatch = (TextView) findViewById(R.id.passMatchCr);
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

        final EditText pass = (EditText) findViewById(R.id.roomPassCr);
        final EditText passConf = (EditText) findViewById(R.id.roomPassConfCr);
        pass.setEnabled(false);
        passConf.setEnabled(false);

        CheckBox isPassPr = (CheckBox) findViewById(R.id.passProtect);

        isPassPr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pass.setEnabled(true);
                    passConf.setEnabled(true);
                } else {
                    pass.setEnabled(false);
                    passConf.setEnabled(false);
                }
            }
        });
    }


    public void createRoom(View v) {

        if (!getRoomName().isEmpty() && !getPassword().isEmpty() && passwordOK) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            new SendDataAsync(this.getApplicationContext(), this).execute("createRoom", String.valueOf(pref.getInt("userid", 0)), getRoomName(), getPassword(), isPrivate());
        } else {
            Toast.makeText(this, "Please check the room name and password", Toast.LENGTH_LONG).show();
        }


    }

    public String isPrivate() {
        CheckBox isPriv = (CheckBox) findViewById(R.id.isPrivChk);
        return String.valueOf(isPriv.isChecked());
    }

    public String getRoomName() {
        EditText roomName = (EditText) findViewById(R.id.roomName);
        return roomName.getText().toString();
    }

    public String getPassword() {
        EditText password = (EditText) findViewById(R.id.roomPassCr);
        return password.getText().toString();

    }

    public String getPasswordConfirm() {
        EditText passwordConfirm = (EditText) findViewById(R.id.roomPassConfCr);

        return passwordConfirm.getText().toString();
    }

    public void goBack(View v) {
        finish();
    }

    public String getIsPassProt() {
        CheckBox isPassProt = (CheckBox) findViewById(R.id.passProtect);
        return String.valueOf(isPassProt.isChecked());
    }
}
