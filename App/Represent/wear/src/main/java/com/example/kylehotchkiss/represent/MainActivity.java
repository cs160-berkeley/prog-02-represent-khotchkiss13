package com.example.kylehotchkiss.represent;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends WearableActivity {


    private repWatchData data;
    private static ArrayList<WatchRepresentative> reps;
    private SensorManager mSensorManager;
    private ShakeDetector mSensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data = ((MyWearApplication) this.getApplication()).getRepData();
        reps = data.representatives;
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setAdapter(new RepPagerAdapter(this, getFragmentManager()));
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeDetector();

        mSensorListener.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            public void onShake(){
                Toast.makeText(getBaseContext(), "Lets shake things up!", Toast.LENGTH_SHORT).show();
                sendToPhone("shake", getBaseContext());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    private class RepPagerAdapter extends FragmentGridPagerAdapter {
        private final Context mContext;
        private List mRows;
        private final Page[][] PAGES;

        public RepPagerAdapter(Context ctx, FragmentManager fm) {
            super(fm);
            mContext = ctx;
            PAGES = new Page[3][3];
        }

        int[] BG_IMAGES = new int[] { R.drawable.lee2, R.drawable.boxer2, R.drawable.feinstein2 };


        private class Page {
            // static resources
            int titleRes;
            int textRes;
            int iconRes;
        }

        @Override
        public int getRowCount() {
            return PAGES.length;
        }

        @Override
        public int getColumnCount(int rowNum) {
            return rowNum == 0 ? PAGES[rowNum].length : 2;
        }

        @Override
        public Fragment getFragment(int row, int col) {
            Page page = PAGES[row][col];
            if (col == 0){
                String title = reps.get(row).name;
                String text = reps.get(row).party;
                if (title.equals("Barbara Lee")) {
                    BG_IMAGES[0] = R.drawable.lee2;
                } else if (title.equals("Jeff Denham")) {
                    BG_IMAGES[0] = R.drawable.denham2;
                }
                CardFragment frag = CardFragment.create(title, text);
                return frag;
            } else if (col == 1) {
                OpenOnPhoneFragment frag = new OpenOnPhoneFragment();
                Bundle arg = new Bundle();
                arg.putString("representative", reps.get(row).name);
                frag.setArguments(arg);
                return frag;
            } else {
                return new VoteFragment();
            }
        }

        @Override
        public Drawable getBackgroundForRow(int row) {
            return mContext.getResources().getDrawable(
                    (BG_IMAGES[row % BG_IMAGES.length]), null);
        }
    }

    public static void sendToPhone(String data, Context context) {
        Intent sendIntent = new Intent(context, WatchToPhoneService.class);
        sendIntent.putExtra("DATA", data);
        context.startService(sendIntent);
    }

    public static class RepCardFragment extends CardFragment {
        @Override
        public View onCreateContentView(LayoutInflater inflater, ViewGroup container,
                                        Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.watch_card_content, container, false);
            ((View) view.getParent()).setClickable(true);
            view.setClickable(true);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendToPhone(((TextView) v.findViewById(R.id.title)).getText().toString(), view.getContext());
                }
            });
            return view;
        }
    }

    public static class OpenOnPhoneFragment extends Fragment {
        String representative;

        @Override
        public void setArguments(Bundle bundle) {
            representative = bundle.getString("representative");
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            final View view = inflater.inflate(R.layout.open_view, container, false);
            Button button = (Button) view.findViewById(R.id.button);
            button.setClickable(true);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendToPhone(representative, view.getContext());
                }
            });
            return view;
        }
    }

    public static class VoteFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            final View view = inflater.inflate(R.layout.vote_view, container, false);
            VoteView vote = reps.get(0).vote;
            String district = vote.district + ", " + vote.state;
            TextView district_name = (TextView) view.findViewById(R.id.district);
            TextView obama = (TextView) view.findViewById(R.id.obama_perc);
            TextView romney = (TextView) view.findViewById(R.id.romney_perc);
            district_name.setText(district);
            obama.setText(vote.obama_percent);
            romney.setText(vote.romney_percent);
            return view;
        }
    }
}

