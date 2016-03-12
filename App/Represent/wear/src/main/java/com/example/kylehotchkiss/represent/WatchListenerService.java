package com.example.kylehotchkiss.represent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.Service;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by kylehotchkiss on 2/28/16.
 */
public class WatchListenerService extends WearableListenerService {
    private static final String REPRESENTATIVES = "/REPS";
    private ArrayList<Drawable> images;

    GoogleApiClient mApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
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

    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "Received a Message");
        if (messageEvent.getPath().equalsIgnoreCase( REPRESENTATIVES )) {
            Log.d("T", "Got some reps");
            ArrayList<WatchRepresentative> reps = deserialize(messageEvent.getData());
            ((MyWearApplication) this.getApplication()).setWatchData(reps);
            startWearActivity();
        } else {
            super.onMessageReceived( messageEvent );
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        if (images == null) {
            images = new ArrayList<Drawable>();
        }
        for (DataEvent event : dataEvents) {
            Log.d("T", "onDataChanged: Got image");
            if (event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().equals("/image")) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                Asset profileAsset = dataMapItem.getDataMap().getAsset("profileImage");
                Bitmap bitmap = loadBitmapFromAsset(profileAsset);
                // Do something with the bitmap
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                Log.d("T", "adding image");
                images.add(drawable);
            }
        }
        startWearActivity();
    }

    public void startWearActivity() {
        if (!(images == null) && !(((MyWearApplication) this.getApplication()).getRepData() == null) && !(((MyWearApplication) this.getApplication()).getRepData().representatives == null)) {
            if (((MyWearApplication) this.getApplication()).getRepData().representatives.size() == images.size()) {
                ((MyWearApplication) getApplication()).setImages(images);
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                images = null;
            }
        }
    }

    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result =
                mApiClient.blockingConnect(100000, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mApiClient, asset).await().getInputStream();
        mApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w("T", "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
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
