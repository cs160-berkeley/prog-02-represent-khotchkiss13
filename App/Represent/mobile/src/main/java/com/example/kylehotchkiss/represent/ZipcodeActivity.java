package com.example.kylehotchkiss.represent;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ZipcodeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private EditText mZipCodeView;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    String mLatitudeText;
    String mLongitudeText;
    ProgressBar progressBar;
    ScrollView locationForm;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getBaseContext();
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
        setContentView(R.layout.activity_zipcode);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        locationForm = (ScrollView) findViewById(R.id.login_form);
        mZipCodeView = (EditText) findViewById(R.id.zipcode);
        mZipCodeView.setSelected(false);
        Button mSetZipButton = (Button) findViewById(R.id.set_zip);
        mSetZipButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setZip();
            }
        });
        final Button mUseLocationButton = (Button) findViewById(R.id.use_location);
        mSetZipButton.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    setZip();
                    handled = true;
                }
                return handled;
            }
        });
        mUseLocationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocation();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }
    @Override
    public void onConnected(Bundle connectionHint) {
    }

    private void setZip() {
        // Reset errors.
        mZipCodeView.setError(null);

        // Store values at the time of the login attempt.
        String zipcode = mZipCodeView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid zipcode.
        if (TextUtils.isEmpty(zipcode)) {
            mZipCodeView.setError(getString(R.string.error_field_required));
            focusView = mZipCodeView;
            cancel = true;
        } else if (!isZipValid(zipcode)) {
            mZipCodeView.setError(getString(R.string.error_invalid_zip));
            focusView = mZipCodeView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            Intent representatives = new Intent(getApplicationContext(), MainActivity.class);
            representatives.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            representatives.putExtra("TOAST", "false");
            representatives.putExtra("LOCATION_TYPE", "ZIP");
            representatives.putExtra("ZIP", zipcode);
            startActivity(representatives);
        }
    }

    private void setLocation() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else {
            getLocation();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();

                } else {
                    Toast.makeText(getBaseContext(), "Please input zip code instead", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void getLocation() {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } catch (SecurityException e) {
            return;
        }
        if (mLastLocation != null) {
            mLatitudeText = String.valueOf(mLastLocation.getLatitude());
            mLongitudeText = String.valueOf(mLastLocation.getLongitude());
        }
        Intent representatives = new Intent(getApplicationContext(), MainActivity.class);
        representatives.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        representatives.putExtra("TOAST", "false");
        representatives.putExtra("LOCATION_TYPE", "LOCATION");
        String[] location = {mLatitudeText, mLongitudeText};
        representatives.putExtra("LOCATION", location);
        startActivity(representatives);
    }

    private boolean isZipValid(String zipcode) {
        //TODO: Replace this with your own logic
        return !zipcode.contains("[a-zA-Z]+") && zipcode.length() > 4;
    }
}

