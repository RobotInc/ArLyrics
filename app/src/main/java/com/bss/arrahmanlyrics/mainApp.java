package com.bss.arrahmanlyrics;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.bss.arrahmanlyrics.utils.MusicPlayer;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by mohan on 5/21/17.
 */

public class mainApp extends Application {
    private static Context mContext;
    private static MusicPlayer player;
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mContext = this;
        player = new MusicPlayer(mContext);
        
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public static Context getContext()
    {
        return mContext;
    }

    public static MusicPlayer getPlayer(){
        return player;
    }


}
