package com.example.kylehotchkiss.represent;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;


public class ZipcodeActivity extends AppCompatActivity {
    private EditText mZipCodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zipcode);
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
            SharedPreferences preferences = getSharedPreferences(this.toString(), Context.MODE_PRIVATE);
            preferences.edit().putString("zip_code", zipcode).apply();
            preferences.edit().putBoolean("use_zip", true).apply();
            // Initialize data
            ((MyApplication) this.getApplication()).setRepDataZip(zipcode);
        }
    }

    private void setLocation() {
        // Do stuff to get user's current location
        SharedPreferences preferences = getSharedPreferences(this.toString(), Context.MODE_PRIVATE);
        preferences.edit().putString("location", "current_location").apply();
        preferences.edit().putBoolean("use_zip", false).apply();
        // Initialize data
        ((MyApplication) this.getApplication()).setRepDataGPS();
    }

    private boolean isZipValid(String zipcode) {
        //TODO: Replace this with your own logic
        return !zipcode.contains("[a-zA-Z]+") && zipcode.length() > 4;
    }
}

