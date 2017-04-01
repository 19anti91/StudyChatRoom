package com.oop.projectgroup10.studychatroom;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

//This class allows users to register to our application
public class RegisterActivity extends AppCompatActivity {


    Boolean passwordOK;

    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //To check that both passwords are ok
        final EditText passwordConfirm = (EditText) findViewById(R.id.confirmpassword);
        final EditText password = (EditText) findViewById(R.id.passwordR);

        TextWatcher passwordValidation = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!getPassword().equals(getPasswordConfirm())) {
                    passwordOK = false;

                    passwordConfirm.setTextColor(Color.parseColor("#FE0417"));
                    password.setTextColor(Color.parseColor("#FE0417"));

                } else {
                    passwordOK = true;
                    passwordConfirm.setTextColor(Color.parseColor("#28B463"));
                    password.setTextColor(Color.parseColor("#28B463"));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        passwordConfirm.addTextChangedListener(passwordValidation);

        SimpleImageArrayAdapter adapter = new SimpleImageArrayAdapter(this, new Integer[]{
                R.drawable.ic_femalelight_small,
                R.drawable.ic_femaledark_small,
                R.drawable.ic_femaledarker_small,
                R.drawable.ic_maleredhair_small,
                R.drawable.ic_malelight_small,
                R.drawable.ic_maledarker_small});

        Spinner userIconSpinner = (Spinner) findViewById(R.id.userIconSpinner);
        userIconSpinner.setAdapter(adapter);
        getAvatar();
    }

    //TODO fix GUI
    public void submitRegistration(View v) {

        boolean isValid = isValidEmail(getEmail());
        if (!isValid) {
            Toast.makeText(this, "Invalid email address. Please check again.", Toast.LENGTH_LONG).show();
        } else
        if (getFname().isEmpty() || getLname().isEmpty() || getUsername().isEmpty() || getEmail().isEmpty() || getUserType().isEmpty() || !passwordOK || !getPassword().equals(getPasswordConfirm())) {
            Toast.makeText(this, "Please note that all the fields are required. Passwords must match", Toast.LENGTH_LONG).show();
        } else {
            new SubmitLoginAndSignup(v.getContext(), this).execute("register", getUsername(), getPassword(), getFname(), getLname(), getEmail(), getUserType(), getAvatar());
        }

    }

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

    public String getAvatar() {
        Spinner userIcon = (Spinner) findViewById(R.id.userIconSpinner);


        return String.valueOf(userIcon.getSelectedItemPosition());
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


    //Class to create spinner with images

    public class SimpleImageArrayAdapter extends ArrayAdapter<Integer> {
        private Integer[] images;

        public SimpleImageArrayAdapter(Context context, Integer[] images) {
            super(context, android.R.layout.simple_spinner_item, images);
            this.images = images;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getImageForPosition(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getImageForPosition(position);
        }

        private View getImageForPosition(int position) {
            ImageView imageView = new ImageView(getContext());
            imageView.setBackgroundResource(images[position]);
            imageView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return imageView;
        }
    }

}
