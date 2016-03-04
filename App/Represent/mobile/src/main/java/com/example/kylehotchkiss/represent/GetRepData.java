package com.example.kylehotchkiss.represent;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kylehotchkiss on 3/1/16.
 */
public class GetRepData {
    ArrayList<Representative> representatives;
    ArrayList<WatchRepresentative> watchRepresentatives;

    public GetRepData() {

    }

    public void setZipCode(String zipCode) {
        representatives = new ArrayList<Representative>();
        watchRepresentatives = new ArrayList<WatchRepresentative>();
        VoteView vote;
        String[] commities = {"Human Services", "State Programs", "Veterans' Affairs"};
        Bill bill1 = new Bill("Education Reform Bill", "February 2nd, 2016");
        Bill bill2 = new Bill("California is Awesome Bill", "February 3rd, 2016");
        Bill[] bills = {bill1, bill2};
        if (zipCode.equals("94704")) {
            representatives.add(new Representative("Barbara Lee", "Democrat", R.drawable.lee, R.drawable.lee2, "lee@gmail.com", "lee.com", "I Like Turtles!", "January 03, 2017", commities, bills));
            vote = new VoteView("CA", "District 13", "55%", "45%");
        } else {
            representatives.add(new Representative("Jeff Denham", "Republican", R.drawable.denham, R.drawable.denham2, "denham@gmail.com", "denham.com", "I Like Turtles!", "January 03, 2017", commities, bills));
            vote = new VoteView("CA", "District 10", "67%", "33%");
        }
        representatives.add(new Representative("Barbara Boxer", "Democrat", R.drawable.boxer, R.drawable.boxer2, "boxer@gmail.com", "boxer.com", "I Like Turtles too!", "January 04, 2018", commities, bills));
        representatives.add(new Representative("Diane Feinstein", "Democrat", R.drawable.feinstein, R.drawable.feinstein2, "feinstein@gmail.com", "feinstein.com", "I Like Turtles three!", "January 05, 2019", commities, bills));
        for (int i = 0; i < representatives.size(); i++) {
            Representative rep = representatives.get(i);
            watchRepresentatives.add(new WatchRepresentative(rep.name, rep.party, rep.photoId, vote));
        }
    }
}

class Representative implements Serializable {
    String name;
    String party;
    int photoId;
    int photoId2;
    String email;
    String website;
    String tweet;
    String end_date;
    String[] commitees;
    Bill[] bills;


    Representative(String name, String party, int photoId, int photoId2, String email, String website, String tweet, String end_date, String[] commitees, Bill[] bills) {
        this.name = name;
        this.party = party;
        this.photoId = photoId;
        this.photoId2 = photoId2;
        this.email = email;
        this.website = website;
        this.tweet = tweet;
        this.end_date = end_date;
        this.commitees = commitees;
        this.bills = bills;
    }
}

class Bill implements Serializable {
    String name;
    String date_introduced;

    Bill(String name, String date) {
        this.name = name;
        this.date_introduced = date;
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