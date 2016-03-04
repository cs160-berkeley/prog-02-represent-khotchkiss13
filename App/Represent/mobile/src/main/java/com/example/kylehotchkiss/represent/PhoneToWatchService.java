package com.example.kylehotchkiss.represent;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by kylehotchkiss on 2/28/16.
 */
public class PhoneToWatchService extends Service {
    private GoogleApiClient mApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("T", "About to send Message");
        //initialize the googleAPIClient for message passing
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.d("T", "About to send Message");
        GetRepData data = ((MyApplication) this.getApplication()).getReps();
        final ArrayList<WatchRepresentative> reps = data.watchRepresentatives;
        Log.d("T", "About to send Message");
        new Thread(new Runnable() {
            @Override
            public void run() {
                mApiClient.connect();
                Log.d("T", "About to send Message");
                sendMessage("/REPS", reps);
            }
        }).start();

        return START_STICKY;
    }

    @Override //remember, all services need to implement an IBiner
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMessage(final String path, final ArrayList<WatchRepresentative> reps) {
        //one way to send message: start a new thread and call .await()
        //see watchtophoneservice for another way to send a message
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mApiClient).await();
                for (Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, serialize(reps)).await();
                }
            }
        }).start();
    }

    public static byte[] serialize(ArrayList<WatchRepresentative> obj)  {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(obj);
            return out.toByteArray();
        }
        catch (IOException exception) {
            return new byte[0];
        }
    }
}
