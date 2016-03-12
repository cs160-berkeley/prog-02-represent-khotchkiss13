package com.example.kylehotchkiss.represent;

import android.app.Application;
import android.content.Intent;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by kylehotchkiss on 3/1/16.
 */
public class MyApplication extends Application {
    private GetRepData data;

    public GetRepData getReps() {
        return data;
    }

    public void setRepDataRandomZip() {
        double top = 49.3457868;
        double left = -124.7844079;
        double right = -66.9513812;
        double bottom =  24.7433195;
        double latitude = 0;
        double longitude = 0;
        Random r = new Random();
        Boolean valid = false;
        while (!valid) {
            latitude = bottom + (top - bottom) * r.nextDouble();
            longitude = left + (right - left) * r.nextDouble();
            InputStream in = null;
            String result = null;
            JSONArray googleapi = null;
            try {
                URL request = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + Double.toString(latitude) + "," + Double.toString(longitude) + "&ES&key=AIzaSyArqTk5x0kbDfmmoGFDg0aJLloiO2WJ4vM");
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
                    String possible_result = googleapi.getJSONObject(i).getString("long_name");
                    JSONArray types = googleapi.getJSONObject(i).getJSONArray("types");
                    for (int j = 0; j < types.length(); j++) {
                        if (types.get(j).equals("country")) {
                            valid = possible_result.equals("United States");
                            break;
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
        }
        Intent representatives = new Intent(this, MainActivity.class);
        representatives.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        representatives.putExtra("TOAST", "true");
        representatives.putExtra("LOCATION_TYPE", "LOCATION");
        String[] location = {Double.toString(latitude), Double.toString(longitude)};
        representatives.putExtra("LOCATION", location);
        startActivity(representatives);
    }

    public void setRepDataZip(String zipcode) {
        data = new GetRepData();
        data.setZipCode(zipcode);
        // Set watch
    }

    public void setRepDataGPS(String lat, String lon) {
        data = new GetRepData();
        data.setGPS(lat, lon);
    }
}
