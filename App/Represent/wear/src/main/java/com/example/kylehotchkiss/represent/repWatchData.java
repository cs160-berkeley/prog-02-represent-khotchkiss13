package com.example.kylehotchkiss.represent;

import android.app.Service;
import android.graphics.drawable.Drawable;

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
    Boolean isRep;
    VoteView vote;

    WatchRepresentative(String name, String party, Boolean isRep) {
        this.name = name;
        if (party.equals("D")) {
            party = "Democrat";
        } else if (party.equals("R")) {
            party = "Republican";
        } else {
            party = "Independent";
        }
        this.party = party;
        this.vote = null;
        this.isRep = isRep;
    }

    public void setVote(VoteView voteview) {
        this.vote = voteview;
    }
}

class VoteView implements Serializable {
    String state;
    String county;
    String obama_percent;
    String romney_percent;

    VoteView(String st, String dis, String obama, String romney) {
        state = st;
        county = dis;
        obama_percent = obama;
        romney_percent = romney;
    }

    public String toString() {
        return "2012 Vote View: County: " + this.county + ", State: " + this.state + ", Obama: " + this.obama_percent + " , Romney: " + this.romney_percent;
    }
}

