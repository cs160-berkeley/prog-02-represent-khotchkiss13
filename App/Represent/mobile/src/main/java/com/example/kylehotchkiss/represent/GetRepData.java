package com.example.kylehotchkiss.represent;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.service.media.MediaBrowserService;
import android.util.Log;
import android.view.View;

import com.google.android.gms.wearable.Asset;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.tweetcomposer.TweetUploadService;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TweetUtils;

import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.tweetui.UserTimeline;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import io.fabric.sdk.android.Fabric;

/**
 * Created by kylehotchkiss on 3/1/16.
 */
public class GetRepData {
    ArrayList<Representative> representatives;
    ArrayList<WatchRepresentative> watchRepresentatives;
    ArrayList<Asset> repImages;


    public GetRepData() {

    }

    public Representative getRep(String rep_name) {
        for (int i = 0; i < representatives.size(); i++) {
            if (representatives.get(i).name.equals(rep_name)) {
                return representatives.get(i);
            }
        }
        return null;
    }

    public void setZipCode(String zip) {
        representatives = new ArrayList<Representative>();
        watchRepresentatives = new ArrayList<WatchRepresentative>();
        repImages = new ArrayList<Asset>();
        JSONArray reps = getReps(zip);
        initializeReps(reps);
        String[] location = getCounty(zip);
        VoteView vote = getVoteData(location[0], location[1]);
        for (int i = 0; i < watchRepresentatives.size(); i++) {
            watchRepresentatives.get(i).setVote(vote);
        }
    }

    public void setGPS(String lat, String lon) {
        representatives = new ArrayList<Representative>();
        watchRepresentatives = new ArrayList<WatchRepresentative>();
        repImages = new ArrayList<Asset>();
        JSONArray reps = getReps(lat, lon);
        initializeReps(reps);
        String[] location = getCounty(lat, lon);
        VoteView vote = getVoteData(location[0], location[1]);
        for (int i = 0; i < watchRepresentatives.size(); i++) {
            watchRepresentatives.get(i).setVote(vote);
        }
    }

    JSONArray getReps(String lat, String lon) {
        InputStream in = null;
        String result = null;
        JSONArray reps = null;
        try {
            URL request = new URL("http://congress.api.sunlightfoundation.com/legislators/locate?latitude=" + lat + "&longitude=" + lon + "&apikey=3895ef0506c44cf1ad0168a57210f303");
            HttpURLConnection urlConnection = (HttpURLConnection) request.openConnection();
            in = urlConnection.getInputStream();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                line = line + "\n";
                sb.append(line);
            }
            result = sb.toString();
            reps = new JSONObject(result).getJSONArray("results");

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null)
                    in.close();
            }
            catch (Exception squish){
                // oops
            }
        }
        return reps;
    }
    String[] getCounty(String lat, String lon) {
        InputStream in = null;
        String result = null;
        String county = null;
        String state = null;
        JSONArray googleapi = null;
        try {
            URL request = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lon + "&ES&key=AIzaSyArqTk5x0kbDfmmoGFDg0aJLloiO2WJ4vM");
            HttpURLConnection urlConnection = (HttpURLConnection) request.openConnection();
            in = urlConnection.getInputStream();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                line = line + "\n";
                sb.append(line);
            }
            result = sb.toString();
            googleapi = new JSONObject(result).getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
            for (int i = 0; i < googleapi.length(); i++) {
                String possible_result = googleapi.getJSONObject(i).getString("short_name");
                JSONArray types = googleapi.getJSONObject(i).getJSONArray("types");
                for (int j = 0; j < types.length(); j++) {
                    if (types.get(j).equals("administrative_area_level_2")) {
                        county = possible_result.replace(" County", "");
                    } else if (types.get(j).equals("administrative_area_level_1")) {
                        state = possible_result;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null)
                    in.close();
            }
            catch (Exception squish){
                // oops
            }
        }
        String[] array = {county, state};
        return array;
    }


    private VoteView getVoteData(String county, String state) {
        InputStream in = null;
        String result = null;
        String obama_data = null;
        String romney_data = null;
        JSONArray countyVoteData = null;
        VoteView vote = null;
        try {
            URL request = new URL("https://raw.githubusercontent.com/cs160-sp16/voting-data/master/election-county-2012.json");
            HttpsURLConnection urlConnection = (HttpsURLConnection) request.openConnection();
            in = urlConnection.getInputStream();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                line = line + "\n";
                sb.append(line);
            }
            result = sb.toString();
            countyVoteData = new JSONArray(result);
            for (int i = 0; i < countyVoteData.length(); i++) {
                String county_state = countyVoteData.getJSONObject(i).getString("state-postal");
                String county_name = countyVoteData.getJSONObject(i).getString("county-name");
                if (county_name.equals(county) && state.equals(county_state)) {
                    obama_data = Double.toString(countyVoteData.getJSONObject(i).getDouble("obama-percentage"));
                    romney_data = Double.toString(countyVoteData.getJSONObject(i).getDouble("romney-percentage"));
                }
            }
            vote = new VoteView(state, county, obama_data, romney_data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null)
                    in.close();
            }
            catch (Exception squish){
                // oops
            }
        }
        return vote;
    }

    private String[] getCounty(String zip) {
        InputStream in = null;
        String result = null;
        String county = null;
        String state = null;
        JSONArray googleapi = null;
        try {
            URL request = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + zip + "&ES&key=AIzaSyArqTk5x0kbDfmmoGFDg0aJLloiO2WJ4vM");
            HttpURLConnection urlConnection = (HttpURLConnection) request.openConnection();
            in = urlConnection.getInputStream();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                line = line + "\n";
                sb.append(line);
            }
            result = sb.toString();
            googleapi = new JSONObject(result).getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
            for (int i = 0; i < googleapi.length(); i++) {
                String possible_result = googleapi.getJSONObject(i).getString("short_name");
                JSONArray types = googleapi.getJSONObject(i).getJSONArray("types");
                for (int j = 0; j < types.length(); j++) {
                    if (types.get(j).equals("administrative_area_level_2")) {
                        county = possible_result.replace(" County", "");
                    } else if (types.get(j).equals("administrative_area_level_1")) {
                        state = possible_result;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null)
                    in.close();
            }
            catch (Exception squish){
                // oops
            }
        }
        String[] array = {county, state};
        return array;
    }

    private void initializeReps(JSONArray reps) {
        for (int i=0; i < reps.length(); i++)
        {
            try {
                JSONObject rep = reps.getJSONObject(i);
                // Pulling items from the array
                String id = rep.getString("bioguide_id");
                String name = rep.getString("first_name") + " " + rep.getString("last_name");
                String email = rep.getString("oc_email");
                String party = rep.getString("party");
                String website = rep.getString("website");
                String twitterID = rep.getString("twitter_id");
                Boolean isRep = rep.getString("chamber").equals("house");
                String title;
                if (isRep) {
                    title = "Representative, District " + Integer.toString(rep.getInt("district"));
                } else {
                    title = "Senator";
                }
                String state = rep.getString("state_name");
                String end = rep.getString("term_end");
                ArrayList<Bill> bills = getBills(id);
                ArrayList<String> committees = getCommittees(id);
                Drawable image = LoadImageFromWebOperations("https://theunitedstates.io/images/congress/450x550/" + id + ".jpg");
                Bitmap image_bitmap = drawableToBitmap(image);
                Asset image_asset = createAssetFromBitmap(image_bitmap);
                repImages.add(image_asset);
                representatives.add(new Representative(name, party, title, state, image, email, website, twitterID, end, committees, bills));
                watchRepresentatives.add(new WatchRepresentative(name, party, isRep));
            } catch (JSONException e) {
                // Oops
            }
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    private static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    private JSONArray getReps(String zip) {
        InputStream in = null;
        String result = null;
        JSONArray reps = null;
        try {
            URL request = new URL("http://congress.api.sunlightfoundation.com/legislators/locate?zip="+ zip + "&apikey=3895ef0506c44cf1ad0168a57210f303");
            HttpURLConnection urlConnection = (HttpURLConnection) request.openConnection();
            in = urlConnection.getInputStream();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                line = line + "\n";
                sb.append(line);
            }
            result = sb.toString();
            reps = new JSONObject(result).getJSONArray("results");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null)
                    in.close();
            }
            catch (Exception squish){
                // oops
            }
        }
        return reps;
    }

    private ArrayList<Bill> getBills(String id) {
        ArrayList<Bill> billsList = new ArrayList<Bill>();
        InputStream in = null;
        String result = null;
        JSONArray billsAPI = null;
        try {
            URL request = new URL("http://congress.api.sunlightfoundation.com/bills?sponsor_id" + id + "&apikey=3895ef0506c44cf1ad0168a57210f303");
            HttpURLConnection urlConnection = (HttpURLConnection) request.openConnection();
            in = urlConnection.getInputStream();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                line = line + "\n";
                sb.append(line);
            }
            result = sb.toString();
            billsAPI = new JSONObject(result).getJSONArray("results");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null)
                    in.close();
            }
            catch (Exception squish){
                // oops
            }
        }
        for (int i=0; i < billsAPI.length(); i++) {
            try {
                JSONObject bill = billsAPI.getJSONObject(i);
                if (!bill.getString("short_title").equals("null")) {
                    String title = bill.getString("short_title");
                    String introduced = bill.getString("introduced_on");
                    billsList.add(new Bill(title, introduced));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
        return billsList;
    }

    private ArrayList<String> getCommittees(String id) {
        ArrayList<String> committeesList = new ArrayList<String>();
        InputStream in = null;
        String result = null;
        JSONArray committeesAPI = null;
        try {
            URL request = new URL("http://congress.api.sunlightfoundation.com/committees?member_ids=" + id + "&apikey=3895ef0506c44cf1ad0168a57210f303");
            HttpURLConnection urlConnection = (HttpURLConnection) request.openConnection();
            in = urlConnection.getInputStream();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                line = line + "\n";
                sb.append(line);
            }
            result = sb.toString();
            committeesAPI = new JSONObject(result).getJSONArray("results");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null)
                    in.close();
            }
            catch (Exception squish){
                // oops
            }
        }

        for (int i=0; i < committeesAPI.length(); i++) {
            try {
                JSONObject committee = committeesAPI.getJSONObject(i);
                committeesList.add(committee.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
        return committeesList;
    }
}

class Representative implements Serializable {
    String name;
    String party;
    String title;
    String state;
    Drawable image;
    String email;
    String website;
    String twitter_id;
    Tweet tweet;
    String end_date;
    ArrayList<String> commitees;
    ArrayList<Bill> bills;


    Representative(String name, String party, String title, String state, Drawable photo, String email, String website, String id, String end_date, ArrayList<String> commitees, ArrayList<Bill> bills) {
        this.name = name;
        if (party.equals("D")) {
            party = "Democrat";
        } else if (party.equals("R")) {
            party = "Republican";
        } else {
            party = "Independent";
        }
        this.title = title;
        this.state = state;
        this.party = party;
        this.image = photo;
        this.email = email;
        this.website = website;
        this.twitter_id = id;
        this.end_date = end_date;
        this.commitees = commitees;
        this.bills = bills;
    }

    public void setTweet(Tweet tweetText) {
        this.tweet = tweetText;
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