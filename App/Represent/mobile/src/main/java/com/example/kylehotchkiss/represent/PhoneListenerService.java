package com.example.kylehotchkiss.represent;

/**
 * Created by kylehotchkiss on 3/1/16.
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class PhoneListenerService extends WearableListenerService {

    //   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        GetRepData data = ((MyApplication) this.getApplication()).getReps();
        ArrayList<Representative> representatives = data.representatives;
        for (int i = 0; i < representatives.size(); i++) {
            Representative rep = representatives.get(i);
            if (messageEvent.getPath().equalsIgnoreCase("/" + rep.name)) {
                Intent intent = new Intent(this, DetailedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("representative", rep.name);
                startActivity(intent);
            }
        }
        if (messageEvent.getPath().equalsIgnoreCase("/shake")) {
            // shake
            ((MyApplication) this.getApplication()).setRepDataRandomZip();
        }
        super.onMessageReceived( messageEvent );
    }
}

