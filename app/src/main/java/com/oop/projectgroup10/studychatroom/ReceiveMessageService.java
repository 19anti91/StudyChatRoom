package com.oop.projectgroup10.studychatroom;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Anti1991 on 3/11/2017.
 */

public class ReceiveMessageService extends Service {

    int mStartMode;

    IBinder mBinder;

    boolean mAllowRebind;

    @Override
    public void onCreate() {

    }

    /**
     * The service is starting, due to a call to startService()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return mStartMode;
    }

    /**
     * A client is binding to the service with bindService()
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Called when all clients have unbound with unbindService()
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /**
     * Called when a client is binding to the service with bindService()
     */
    @Override
    public void onRebind(Intent intent) {

    }

    /**
     * Called when The service is no longer used and is being destroyed
     */
    @Override
    public void onDestroy() {

    }

    public void showMessages() {

    }
}
