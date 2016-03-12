package com.example.kylehotchkiss.represent;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailedActivity extends AppCompatActivity {
    Representative rep;
    private GetRepData data;
    TextView name;
    TextView party;
    TextView end_date;
    ImageView image;
    LinearLayout committees;
    LinearLayout bills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        data = ((MyApplication) this.getApplication()).getReps();
        rep = data.getRep((String) getIntent().getSerializableExtra("representative"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (TextView) findViewById(R.id.name);
        party = (TextView) findViewById(R.id.party);
        end_date = (TextView) findViewById(R.id.term_end);
        image = (ImageView) findViewById(R.id.imageView);
        committees = (LinearLayout) findViewById(R.id.committee_list);
        bills = (LinearLayout) findViewById(R.id.bills_list);
        image.setImageDrawable(rep.image);
        name.setText(rep.name);
        party.setText(rep.party);
        end_date.setText("Term ends on " + rep.end_date);
        ListAdapter committeesAdapter = new ArrayAdapter<String>(this, R.layout.committee_row, rep.commitees);
        ArrayList<HashMap<String, String>> billsList = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < rep.bills.size(); i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            Bill bill = rep.bills.get(i);
            map.put("name", bill.name);
            map.put("date_introduced", "Introduced on " + bill.date_introduced);
            billsList.add(map);
        }
        ListAdapter billsAdapter = new BillAdapter(this, billsList);
        addItemsToView(committeesAdapter, committees);
        addItemsToView(billsAdapter, bills);
    }

    public static void addItemsToView(Adapter adapter, LinearLayout view) {
        final int item_count = adapter.getCount();
        for (int i = 0; i < item_count; i++) {
            View item = adapter.getView(i, null, null);
            view.addView(item);
        }
    }

    public class BillAdapter extends BaseAdapter {

        private Activity activity;
        private ArrayList<HashMap<String, String>> data;
        private LayoutInflater inflater=null;

        public BillAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
            activity = a;
            data=d;
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return data.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View vi=convertView;
            if(convertView==null)
                vi = inflater.inflate(R.layout.bill_row, null);

            TextView name = (TextView)vi.findViewById(R.id.bill_name);
            TextView introduced = (TextView)vi.findViewById(R.id.bill_date);

            HashMap<String, String> bill;
            bill = data.get(position);

            // Setting all values in listview
            name.setText(bill.get("name"));
            introduced.setText(bill.get("date_introduced"));
            return vi;
        }
    }
}

