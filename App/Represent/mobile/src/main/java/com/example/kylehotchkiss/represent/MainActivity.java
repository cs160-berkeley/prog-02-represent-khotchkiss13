package com.example.kylehotchkiss.represent;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.UserUtils;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;
import com.twitter.sdk.android.tweetui.UserTimeline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar progressBar;
    StatusesService statusesService;


    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "KoRPiv4DNFVKqgNiP6b1VRilg";
    private static final String TWITTER_SECRET = "5YH0PwkP9b86nk3tH4olNl5ZwQHNV3trSipCvRFti9buV0GAT8";

    // Filler data
    private GetRepData data;
    public ArrayList<Representative> representatives;

    public void viewDetails(Representative representative) {
        Intent representatives = new Intent(this, DetailedActivity.class);
        representatives.putExtra("representative", representative.name);
        startActivity(representatives);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(MainActivity.this, new Twitter(authConfig));
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        statusesService = twitterApiClient.getStatusesService();
        mRecyclerView = (RecyclerView) findViewById(R.id.representatives_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        RVAdapter adapter = new RVAdapter(new ArrayList<Representative>());
        mRecyclerView.setAdapter(adapter);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        String locationType = (String) getIntent().getSerializableExtra("LOCATION_TYPE");
        if (locationType.equals("ZIP")) {
            String zip = (String) getIntent().getSerializableExtra("ZIP");
            new RetrieveRepData().execute(zip);
        } else {
            String[] location = (String[]) getIntent().getSerializableExtra("LOCATION");
            new RetrieveGPSRepData().execute(location);
        }
        if (((String) getIntent().getSerializableExtra("TOAST")).equals("true")) {
            String[] location = (String[]) getIntent().getSerializableExtra("LOCATION");
            Toast.makeText(getBaseContext(), "New location Lat: " + location[0] + ", Long: " + location[1], Toast.LENGTH_SHORT).show();
        }
    }

    class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{
        List<Representative> representatives;

        public class PersonViewHolder extends RecyclerView.ViewHolder {
            CardView cv;
            TextView representativeName;
            TextView representativeParty;
            ImageView representativePhoto;
            TextView representativeTitle;
            TextView representativeState;
            Button representativeEmail;
            Button representativeWebsite;
            LinearLayout representativeTweet;
            Button button;

            PersonViewHolder(View itemView) {
                super(itemView);
                cv = (CardView)itemView.findViewById(R.id.cv);
                representativeName = (TextView) itemView.findViewById(R.id.representative_name);
                representativeParty = (TextView) itemView.findViewById(R.id.representative_party);
                representativePhoto = (ImageView) itemView.findViewById(R.id.representative_photo);
                representativeTitle = (TextView) itemView.findViewById(R.id.rep_title);
                representativeState = (TextView) itemView.findViewById(R.id.rep_state);
                representativeEmail = (Button) itemView.findViewById(R.id.email);
                representativeWebsite = (Button) itemView.findViewById(R.id.link);
                representativeTweet = (LinearLayout) itemView.findViewById(R.id.representative_tweet);
                button = (Button) itemView.findViewById(R.id.button);
            }
        }

        RVAdapter(List<Representative> persons){
            this.representatives = persons;
        }

        @Override
        public int getItemCount() {
            return representatives.size();
        }

        @Override
        public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.representative_card, viewGroup, false);
            PersonViewHolder pvh = new PersonViewHolder(v);
            return pvh;
        }

        @Override
        public void onBindViewHolder(final PersonViewHolder personViewHolder, int i) {
            final Representative rep = representatives.get(i);
            personViewHolder.representativeName.setText(rep.name);
            personViewHolder.representativeParty.setText(rep.party);
            personViewHolder.representativeTitle.setText(rep.title);
            personViewHolder.representativeState.setText(rep.state);
            personViewHolder.representativePhoto.setImageDrawable(rep.image);
            personViewHolder.representativeEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + rep.email));
                    startActivity(intent);
                }
            });
            personViewHolder.representativeWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(rep.website); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
                @Override
                public void success(Result<AppSession> appSessionResult) {
                    AppSession session = appSessionResult.data;
                    final TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                    twitterApiClient.getStatusesService().userTimeline(null, rep.twitter_id, 1, null, null, false, false, false, true, new Callback<List<Tweet>>() {
                        @Override
                        public void success(Result<List<Tweet>> listResult) {
                            if (!listResult.data.isEmpty()) {
                                rep.setTweet(listResult.data.get(0));
                                TweetUtils.loadTweet(rep.tweet.id, new Callback<Tweet>() {
                                    @Override
                                    public void success(Result<Tweet> result) {
                                        personViewHolder.representativeTweet.addView(new CompactTweetView(MainActivity.this, result.data));
                                    }

                                    @Override
                                    public void failure(TwitterException exception) {
                                        // Toast.makeText(...).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void failure(TwitterException e) {
                            e.printStackTrace();
                        }
                    });
                }

                @Override
                public void failure(TwitterException e) {
                    e.printStackTrace();
                }
            });
            personViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewDetails(rep);
                }
            });
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }
    }

    public class RetrieveRepData extends AsyncTask<String, Void, String> {
        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        protected String doInBackground(String... location) {
            ((MyApplication) getApplication()).setRepDataZip(location[0]);
            data = ((MyApplication) getApplication()).getReps();
            representatives = data.representatives;
            return "Success";
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
            startService(sendIntent);

            RVAdapter adapter = new RVAdapter(representatives);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    public class RetrieveGPSRepData extends AsyncTask<String[], Void, String> {
        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        protected String doInBackground(String[]... location) {
            ((MyApplication) getApplication()).setRepDataGPS(location[0][0], location[0][1]);
            data = ((MyApplication) getApplication()).getReps();
            representatives = data.representatives;
            return "Success";
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
            startService(sendIntent);

            RVAdapter adapter = new RVAdapter(representatives);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}
