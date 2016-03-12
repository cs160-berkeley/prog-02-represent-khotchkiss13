package com.example.kylehotchkiss.represent;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by kylehotchkiss on 3/2/16.
 */
public class MyWearApplication extends Application {
    private repWatchData data;
    private ArrayList<Drawable> repImages;

    public repWatchData getRepData() {
        return data;
    }

    public void setWatchData(ArrayList<WatchRepresentative> reps) {
        data = new repWatchData(reps);
    }

    public void setImages(ArrayList<Drawable> images) {
        repImages = images;
    }

    public void addImage(Drawable image) {
        if (repImages == null) {
            repImages = new ArrayList<Drawable>();
        }
        repImages.add(image);
    }

    public ArrayList<Drawable> getRepImages() {
        return repImages;
    }
}
