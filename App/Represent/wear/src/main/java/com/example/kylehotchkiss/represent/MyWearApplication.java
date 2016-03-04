package com.example.kylehotchkiss.represent;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by kylehotchkiss on 3/2/16.
 */
public class MyWearApplication extends Application {
    private repWatchData data;

    public repWatchData getRepData() {
        return data;
    }

    public void setWatchData(ArrayList<WatchRepresentative> reps) {
        data = new repWatchData(reps);
    }
}
