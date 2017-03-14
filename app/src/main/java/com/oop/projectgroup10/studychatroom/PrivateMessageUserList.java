package com.oop.projectgroup10.studychatroom;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class PrivateMessageUserList extends AppCompatActivity {

    public CustomListAdapter customAdapter;
    public Activity act = this;
    private ListView mainListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_message_user_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Select User");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("currentPrivUser", "");
        editor.apply();
        new SendDataAsync(getApplicationContext(), this).execute("getAllUsers", String.valueOf(pref.getInt("userid", 0)));
        mainListView = (ListView) findViewById(R.id.userList);


        Integer[] userIcon = {};
        String[] userlist = {};

        JSONArray userList;

        try {
            userList = new JSONArray(pref.getString("userList", ""));
            userlist = new String[userList.length()];
            userIcon = new Integer[userList.length()];
            for (int i = 0; i < userList.length(); i++) {
                JSONObject user = (JSONObject) userList.get(i);
                userlist[i] = user.getString("username");
                userIcon[i] = Integer.valueOf(user.getString("usericon"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        customAdapter = new CustomListAdapter(this, userlist, userIcon);
//final Integer[] icon = userIcon;
        mainListView.setAdapter(customAdapter);

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String username = mainListView.getItemAtPosition(position).toString();
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("currentPrivUser", username);
                //              editor.putInt("currentPrivUserIcon",icon[position]);
                editor.apply();

                Intent goToPrivMsgRoom = new Intent(act, PrivateMessage.class);
                startActivity(goToPrivMsgRoom);
            }
        });
    }

    //Beginning of class custom list adapter
    public class CustomListAdapter extends ArrayAdapter<String> {

        private final Activity context;
        private final String[] itemname;
        private final Integer[] imgid;

        public CustomListAdapter(Activity context, String[] itemname, Integer[] imgid) {
            super(context, R.layout.user_list, itemname);

            this.context = context;
            this.itemname = itemname;
            this.imgid = imgid;
        }

        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.user_list, null, true);

            TextView txtTitle = (TextView) rowView.findViewById(R.id.userListText);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.userListIcon);


            txtTitle.setText(itemname[position]);
            switch (imgid[position]) {
                case 0:
                    imageView.setImageResource(R.drawable.ic_femalelight);
                    break;
                case 1:
                    imageView.setImageResource(R.drawable.ic_femaledark);
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.ic_femaledarker);
                    break;
                case 3:
                    imageView.setImageResource(R.drawable.ic_maleredhair);
                    break;
                case 4:
                    imageView.setImageResource(R.drawable.ic_malelight);
                    break;
                case 5:
                    imageView.setImageResource(R.drawable.ic_maledarker);
                    break;

            }


            return rowView;

        }
    }
}
