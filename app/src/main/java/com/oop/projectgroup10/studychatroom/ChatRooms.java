package com.oop.projectgroup10.studychatroom;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatRooms extends AppCompatActivity {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    public static ViewGroup view;
    public static LinearLayout layout;
    public static Handler UIHandler;
    static boolean isActive = false;

    static {
        UIHandler = new Handler(Looper.getMainLooper());
    }

    public ListView usersFound;
    public Activity act = this;
    private ReceiveMessageService mBoundService;
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

    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static void runOnUI(Runnable runnable) {
        UIHandler.post(runnable);
    }

    //TODO emojis
/*
    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
        msgFromMe.setText(getEmojiByUnicode(0x1F60A));
        http://apps.timwhitlock.info/emoji/tables/unicode
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_rooms);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(pref.getString("currentChatRoom", ""));


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem settingsItem = menu.findItem(R.id.action_settings);
        settingsItem.setVisible(false);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_rooms, menu);
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
        View rootView;

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


        public static void populateReceivedMsg(String msg, String from, Activity activity, int ico) {


            View view = LayoutInflater.from(activity).inflate(R.layout.msg_from_them, null);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
            String toGroup = pref.getString("userGroup", "");

            if (!msg.isEmpty() && from.equals(toGroup)) {

                layout.addView(view);
                TextView msgFromMe = (TextView) view.findViewById(R.id.msgFromThemTxt);
                TextView userName = (TextView) view.findViewById(R.id.msgFromGroup);
                userName.setId(generateViewId());
                userName.setText(pref.getString("userFrom", ""));
                ImageView icon = getIcon(ico, R.id.msgFromThemIcon, view);

                msgFromMe.setId(generateViewId());
                msgFromMe.setText(msg);
                layout.invalidate();
/*
                final ScrollView scroll = (ScrollView) lay.findViewById(R.id.scrollPriv);
                scroll.post(new Runnable() {
                                @Override
                                public void run() {
                                    scroll.fullScroll(View.FOCUS_DOWN);
                                }
                            }

                );*/

            }

        }

        public static ImageView getIcon(int icon, int id, View view) {
            ImageView imageView = (ImageView) view.findViewById(id);
            switch (icon) {
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
            return imageView;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final SharedPreferences.Editor edit = pref.edit();

            // = inflater.inflate(R.layout.fragment_chat_rooms, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            int section = getArguments().getInt(ARG_SECTION_NUMBER);
            ListView listView;
            CustomListAdapter adapter;
            //Members
            if (section == 1) {
                rootView = inflater.inflate(R.layout.activity_private_message_user_list, container, false);
                new SendDataAsync(getActivity(), getActivity()).execute("getAllUsersFromChatRoom", String.valueOf(pref.getInt("userid", 0)), pref.getString("currentChatRoom", ""));
                listView = (ListView) rootView.findViewById(R.id.userList);


                Integer[] userIcon = {};
                String[] userlist = {};

                JSONArray userList;
                try {
                    userList = new JSONArray(pref.getString("usersFrom"+pref.getString("currentChatRoom",""), ""));
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

                adapter = new CustomListAdapter(getActivity(), userlist, userIcon);
                final Integer[] icon = userIcon;
                listView.setAdapter(adapter);
                /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String username = listView.getItemAtPosition(position).toString();
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("currentPrivUser", username);
                        editor.putInt("currentPrivUserIcon", icon[position]);

                        editor.apply();

                        Intent goToPrivMsgRoom = new Intent(getActivity(), PrivateMessage.class);
                        startActivity(goToPrivMsgRoom);
                    }
                });*/


            } else if (section == 2) {
                rootView = inflater.inflate(R.layout.activity_private_message, container, false);

                layout = (LinearLayout) rootView.findViewById(R.id.privMsgLayout);
                view = (ViewGroup) rootView.findViewById(R.id.privMsgLayout);

                new MessageAsync(getActivity(), getActivity(), view, layout).execute("getGroupMsg", String.valueOf(pref.getInt("userid", 0)), pref.getString("currentChatRoom", ""));
                ImageView send = (ImageView) rootView.findViewById(R.id.sendMsgBtn);
                send.setClickable(true);
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendMessage(v);
                    }
                });
                //TODO check why dif pictures
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUI(new Runnable() {
                            @Override
                            public void run() {

                                if (pref.getInt("hasMessage", 0) == 1) {
                                    Log.d("MESSAGE", pref.getString("message", ""));
                                    populateReceivedMsg(pref.getString("message", ""), pref.getString("userGroup", ""), getActivity(), Integer.valueOf(pref.getString("groupUserIcon", "")));

                                    edit.putInt("hasMessage", 0);
                                    edit.apply();

                                }


                            }
                        });

                    }
                }, 0, 500);



            } else {
                rootView = inflater.inflate(R.layout.manage_room, container, false);

                final TextView roomName = (TextView) rootView.findViewById(R.id.roomNameMan);
                Switch makeRoomPriv = (Switch) rootView.findViewById(R.id.makePrivMan);
                Switch passProt = (Switch) rootView.findViewById(R.id.passwordProtectedSwitch);
                TextView changePass = (TextView) rootView.findViewById(R.id.changePasswordMan);
                TextView inviteUser = (TextView) rootView.findViewById(R.id.inviteUsersMan);
                TextView banUser = (TextView) rootView.findViewById(R.id.banUsersMan);
                TextView deleteRoom = (TextView) rootView.findViewById(R.id.deleteRoomMan);
                TextView leaveRoom = (TextView) rootView.findViewById(R.id.leaveRoomMan);


                roomName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText name = new EditText(getActivity());
                        name.setText(pref.getString("currentChatRoom",""));

                        new AlertDialog.Builder(getActivity())
                                .setTitle("Change Room Name")
                                .setMessage("Please choose the new name")
                                .setView(name)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        new SendDataAsync(getActivity(),getActivity()).execute("updateChatRoomName",pref.getString("userid",""), pref.getString("currentChatRoom",""),name.getText().toString());
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });

                makeRoomPriv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    }
                });

                passProt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    }
                });

                changePass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                inviteUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                banUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                deleteRoom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                leaveRoom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });





                //if regular user only show leave room
                //if admin or room owner show all except leave room


//delete room, ban people, change room name, create, change password, remove password, invite, make private, make public
         //regular user leave room
                //add warnings
            }


            return rootView;
        }

        //end
        public void sendMessage(View v) {

            View view = LayoutInflater.from(getActivity()).inflate(R.layout.msg_from_me, null);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String toGroup = pref.getString("currentChatRoom", "");

            if (!getMessage().isEmpty()) {
                new MessageAsync(getActivity(), getActivity(), view, layout).execute("groupMsg", String.valueOf(pref.getInt("userid", 0)), toGroup, getMessage());
                layout.addView(view);
                TextView msgFromMe = (TextView) rootView.findViewById(R.id.msgFromMeTxt);
                msgFromMe.setId(generateViewId());
                msgFromMe.setText(getMessage());
                ImageView icon = getIcon(pref.getInt("usericon", 0), R.id.messageFromMeIcon, view);
                layout.invalidate();
                EditText msgToSend = (EditText) rootView.findViewById(R.id.msgToSend);
                msgToSend.setText("");

                final ScrollView scroll = (ScrollView) rootView.findViewById(R.id.scrollPriv);
                scroll.post(new Runnable() {
                                @Override
                                public void run() {
                                    scroll.fullScroll(View.FOCUS_DOWN);
                                }
                            }

                );

            }
        }

        public void recieveMessage(View v) {
            new MessageAsync(getActivity(), getActivity(), view, layout).execute();
        }

        public String getMessage() {
            String msg;
            EditText msgToSend = (EditText) rootView.findViewById(R.id.msgToSend);
            msg = msgToSend.getText().toString();

            return msg;
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
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Members";
                case 1:
                    return "Chat Room";
                case 2:
                    return "Manage Chat Room";
            }
            return null;
        }
    }
}
