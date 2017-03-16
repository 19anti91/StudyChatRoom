package com.oop.projectgroup10.studychatroom;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChatRoomsTwoTabs extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_rooms_two_tabs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        CustomListAdapterChatRooms customListAdapter;
        final Activity act = this;
        final ListView chatRoomListView;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("currentChatRoom", "");
        editor.apply();
        new SendDataAsync(this, this).execute("getAllChatRooms", String.valueOf(pref.getInt("userid", 0)));
        chatRoomListView = (ListView) findViewById(R.id.chatRoomList);

        String[] chatRoomName = {};
        String[] chatRoomOwner = {};
        Integer[] chatRoomMembers = {};
        Integer[] chatRoomOwnerId = {};

        JSONArray chatRoomList;

        try {
            chatRoomList = new JSONArray(pref.getString("chatRoomList", ""));
            chatRoomName = new String[chatRoomList.length()];
            chatRoomOwner = new String[chatRoomList.length()];
            chatRoomMembers = new Integer[chatRoomList.length()];
            chatRoomOwnerId = new Integer[chatRoomList.length()];

            for (int i = 0; i < chatRoomList.length(); i++) {
                JSONObject room = (JSONObject) chatRoomList.get(i);
                chatRoomName[i] = room.getString("chatroomname");
                chatRoomOwner[i] = room.getString("chatroomownerusername");
                chatRoomOwnerId[i] = Integer.valueOf(room.getString("chatroomownerid"));
                chatRoomMembers[i] = Integer.valueOf(room.getString("chatroommembers"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        customListAdapter = new CustomListAdapterChatRooms(this, chatRoomName, chatRoomOwner, chatRoomMembers);

        chatRoomListView.setAdapter(customListAdapter);

        chatRoomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String roomName = chatRoomListView.getItemAtPosition(position).toString();
                Log.d("roomname", roomName);
                //TODO check if user is already on that room, if not do alert and ask
                new AlertDialog.Builder(act)
                        .setTitle("Join Chat Room?")
                        .setMessage("Do you want to join this Chat Room?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(R.drawable.ic_gear)
                        .show();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_rooms_two_tabs, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_chat_rooms_two_tabs, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /***********************************************/
    public class CustomListAdapterChatRooms extends ArrayAdapter<String> {

        private final Activity context;
        private final String[] chatRoomName;
        private final String[] chatRoomOwner;
        private final Integer[] chatRoomMembers;


        public CustomListAdapterChatRooms(Activity context, String[] chatRoomName, String[] chatRoomOwner, Integer[] chatRoomMembers) {
            super(context, R.layout.chat_room_list, chatRoomName);

            this.context = context;
            this.chatRoomName = chatRoomName;
            this.chatRoomOwner = chatRoomOwner;
            this.chatRoomMembers = chatRoomMembers;
        }

        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.chat_room_list, null, true);

            TextView roomName = (TextView) rowView.findViewById(R.id.chatRoomNameList);
            TextView roomOwner = (TextView) rowView.findViewById(R.id.chatRoomOwnerList);
            TextView roomMembers = (TextView) rowView.findViewById(R.id.chatRoomMembersList);

            roomName.setText(chatRoomName[position]);
            roomOwner.setText("Owner: " + chatRoomOwner[position]);
            roomMembers.setText("Members: " + String.valueOf(chatRoomMembers[position]));

            return rowView;

        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Chat Rooms";
                case 1:
                    return "Your Chat Rooms";

            }
            return null;
        }
    }
}