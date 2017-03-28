package com.oop.projectgroup10.studychatroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class DashBoard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        new SendDataAsync(getApplicationContext(), this).execute("getAllUsers", String.valueOf(pref.getInt("userid", 0)));
        new SendDataAsync(getApplicationContext(), this).execute("getAllChatRooms", String.valueOf(pref.getInt("userid", 0)));

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("currentPrivUser", "");
        editor.apply();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
            System.exit(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dash_board, menu);

        //getinfo from preferences
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        String userName = pref.getString("username", "");
        String userType = pref.getString("type", "");

        TextView userNameD = (TextView) findViewById(R.id.usernameDash);
        TextView userTypeD = (TextView) findViewById(R.id.usertypeDash);
        userNameD.setText(userName);
        userTypeD.setText(userType);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_settings) {
            Intent goToSettings = new Intent(this, MemberSettings.class);
            startActivity(goToSettings);

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        if (id == R.id.createRoom) {
            if(pref.getString("type","").equals("Student")){
                Toast.makeText(this, "Create Room is only available for Teachers and Administrators", Toast.LENGTH_LONG).show();
            }else{
                Intent goToCreateRoom = new Intent(this, CreateRoom.class);
                startActivity(goToCreateRoom);
            }

        } else if (id == R.id.settings) {
            Intent goToSettings = new Intent(this, MemberSettings.class);
            startActivity(goToSettings);
        } else if (id == R.id.logout) {
            clearPref();
        } else if (id == R.id.chatRoom) {
            Intent goToChatRoomList = new Intent(this, ChatRoomsTwoTabs.class);
            startActivity(goToChatRoomList);


        } else if (id == R.id.privateMessageRoom) {

            new SendDataAsync(getApplicationContext(), this).execute("getAllUsers", String.valueOf(pref.getInt("userid", 0)));
            Intent goToPrivMsgRoom = new Intent(this, PrivateMessageUserList.class);
            startActivity(goToPrivMsgRoom);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Clear preferences. Can be used to log out
    public void clearPref() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = pref.edit();
        edit.clear();
        edit.commit();
        this.finish();
    }


}

