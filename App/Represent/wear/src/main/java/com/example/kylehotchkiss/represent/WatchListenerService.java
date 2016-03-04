package com.example.kylehotchkiss.represent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.Service;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by kylehotchkiss on 2/28/16.
 */
public class WatchListenerService extends WearableListenerService {
    private static final String REPRESENTATIVES = "/REPS";

    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "Received a Message");
        if (messageEvent.getPath().equalsIgnoreCase( REPRESENTATIVES )) {
            Log.d("T", "Got some reps");
            ArrayList<WatchRepresentative> reps = deserialize(messageEvent.getData());
            Intent intent = new Intent(this, MainActivity.class );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ((MyWearApplication) this.getApplication()).setWatchData(reps);
            intent.putExtra("representatives", reps);
            startActivity(intent);
        } else {
            super.onMessageReceived( messageEvent );
        }
    }

    public static ArrayList<WatchRepresentative> deserialize(byte[] data) {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        try {
            ObjectInputStream is = new ObjectInputStream(in);
            return (ArrayList<WatchRepresentative>) is.readObject();
        }
        catch (IOException exception1) {
            return new ArrayList<WatchRepresentative>();
        }
        catch (ClassNotFoundException exception2) {
            return new ArrayList<WatchRepresentative>();
        }
    }
}
