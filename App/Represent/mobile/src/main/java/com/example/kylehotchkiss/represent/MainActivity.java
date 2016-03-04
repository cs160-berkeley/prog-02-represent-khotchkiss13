package com.example.kylehotchkiss.represent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // Filler data
    private GetRepData data;
    public ArrayList<Representative> representatives;

    public void viewDetails(Representative representative) {
        Intent representatives = new Intent(this, DetailedActivity.class);
        representatives.putExtra("representative", representative);
        startActivity(representatives);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.representatives_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        data = ((MyApplication) this.getApplication()).getReps();
        representatives = data.representatives;
        RVAdapter adapter = new RVAdapter(representatives);
        mRecyclerView.setAdapter(adapter);
        if (((String) getIntent().getSerializableExtra("TOAST")).equals("true")) {
            Toast.makeText(getBaseContext(), "New Zip: " + ((String) getIntent().getSerializableExtra("ZIP")), Toast.LENGTH_SHORT).show();
        }
    }

    class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{
        List<Representative> representatives;

        public class PersonViewHolder extends RecyclerView.ViewHolder {
            CardView cv;
            TextView representativeName;
            TextView representativeParty;
            ImageView representativePhoto;
            TextView representativeEmail;
            TextView representativeWebsite;
            TextView representativeTweet;
            Button button;

            PersonViewHolder(View itemView) {
                super(itemView);
                cv = (CardView)itemView.findViewById(R.id.cv);
                representativeName = (TextView) itemView.findViewById(R.id.representative_name);
                representativeParty = (TextView) itemView.findViewById(R.id.representative_party);
                representativePhoto = (ImageView) itemView.findViewById(R.id.representative_photo);
                representativeEmail = (TextView) itemView.findViewById(R.id.representative_email);
                representativeWebsite = (TextView) itemView.findViewById(R.id.representative_website);
                representativeTweet = (TextView) itemView.findViewById(R.id.representative_tweet);
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
        public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
            final Representative rep = representatives.get(i);
            String tweet = "<b>Latest Tweet: </b> " + rep.tweet;
            personViewHolder.representativeName.setText(rep.name);
            personViewHolder.representativeParty.setText(rep.party);
            personViewHolder.representativePhoto.setImageResource(rep.photoId);
            personViewHolder.representativeEmail.setText(rep.email);
            personViewHolder.representativeWebsite.setText(rep.website);
            personViewHolder.representativeTweet.setText(Html.fromHtml(tweet));
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
}
