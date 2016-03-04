package com.example.kylehotchkiss.represent;

import android.app.Service;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kylehotchkiss on 3/2/16.
 */
public class repWatchData {
    ArrayList<WatchRepresentative> representatives;

    public repWatchData(ArrayList<WatchRepresentative> reps) {
        representatives = reps;
    }
}

class WatchRepresentative implements Serializable {
    String name;
    String party;
    int photoId;
    VoteView vote;

    WatchRepresentative(String name, String party, int photoId, VoteView phoneVote) {
        this.name = name;
        this.party = party;
        this.photoId = photoId;
        this.vote = phoneVote;
    }
}

class VoteView implements Serializable {
    String state;
    String district;
    String obama_percent;
    String romney_percent;

    VoteView(String st, String dis, String obama, String romney) {
        state = st;
        district = dis;
        obama_percent = obama;
        romney_percent = romney;
    }
}

