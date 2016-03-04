package com.example.kylehotchkiss.represent;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by kylehotchkiss on 3/1/16.
 */
public class MyApplication extends Application {
    private GetRepData data;

    public GetRepData getReps() {
        return data;
    }

    public void setRepDataRandomZip() {
        Random r = new Random();
        int zipcode = r.nextInt((99999 - 501) + 1) + 501;
        data = new GetRepData();
        data.setZipCode(Integer.toString(zipcode));
        // Set watch
        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        startService(sendIntent);

        Intent representatives = new Intent(this, MainActivity.class);
        representatives.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        representatives.putExtra("TOAST", "true");
        representatives.putExtra("ZIP", Integer.toString(zipcode));
        startActivity(representatives);
    }

    public void setRepDataZip(String zipcode) {
        data = new GetRepData();
        data.setZipCode(zipcode);
        // Set watch
        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        startService(sendIntent);

        Intent representatives = new Intent(this, MainActivity.class);
        representatives.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        representatives.putExtra("TOAST", "false");
        startActivity(representatives);
    }

    public void setRepDataGPS() {
        data = new GetRepData();
        data.setZipCode("94704"); //Change this later
        // Set watch
        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        startService(sendIntent);

        Intent representatives = new Intent(this, MainActivity.class);
        representatives.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        representatives.putExtra("TOAST", "false");
        startActivity(representatives);
    }
}
