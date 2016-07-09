package com.example.douglas.vansbroappen;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.douglas.vansbroappen.fetchData.AsyncResponse;
import com.example.douglas.vansbroappen.fetchData.FetchJsonData;
import com.example.douglas.vansbroappen.fetchData.LoaderUtility;
import com.example.douglas.vansbroappen.fetchData.StatsAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by douglas on 03/06/2016.
 */
public class SearchFragment extends Fragment implements AsyncResponse {

    private StatsAdapter statsAdapter;
    private ListView listView;
    private AdView adView;
    private TextView overview;
    private Button addToWidget;
    private String name;

    private Tracker tracker;
    private static final String LOG_TAG = SearchFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            name = savedInstanceState.getString("name");
        }

        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        tracker = application.getDefaultTracker();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_fragment, container, false);

        adView = (AdView)rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        EditText editText = (EditText) rootView.findViewById(R.id.edit_text_main);
        editText.setText("johan hansson");

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String name = formatInputName(v.getText().toString());
                    populateListView(name);
                    handled = true;
                }
                return handled;
            }
        });

        addToWidget = (Button) rootView.findViewById(R.id.add_to_widget);
        addToWidget.setVisibility(View.INVISIBLE);

        overview = (TextView) rootView.findViewById(R.id.overview);
        overview.setVisibility(View.INVISIBLE);


        return rootView;
    }


    public String formatInputName(String name) {
        // get input name from user and convert it to "smith, john" format to match the database

        name = name.toLowerCase();
        if (name.contains(" ")) {
            String[] parts = name.split(" ");
            if (parts.length == 2) {
                name = parts[1] + ", " + parts[0];
            } else if (parts.length == 3) {
                name = parts[1] + " " + parts[2] + ", " + parts[0];
            }
        }
        return name;
    }

    public void populateListView(String name) {

        this.name = name;

        listView = (ListView) getActivity().findViewById(R.id.list);
        ContextName contextName = new ContextName(getContext(), name);
        FetchJsonData fetchJsonData = new FetchJsonData();
        fetchJsonData.delegate = this;
        fetchJsonData.execute(contextName);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) statsAdapter.getItem(position);
                String competition = cursor.getString(LoaderUtility.Query.COMPETITION);
                String name = cursor.getString(LoaderUtility.Query.NAME);
                Bundle bundle = new Bundle();
                bundle.putString("selectedCompetition", competition);
                bundle.putString("name", name);
                CompetitionFragment fragment = new CompetitionFragment();
                fragment.setArguments(bundle);

                cursor.close();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public void processFinish(Cursor cursor) {

        if (cursor != null) {
            this.statsAdapter = new StatsAdapter(getContext(), cursor, 0);
            this.listView.setAdapter(this.statsAdapter);
            final String name = cursor.getString(LoaderUtility.Query.NAME);


            addToWidget.setVisibility(View.VISIBLE);
            addToWidget.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = getContext();
                    Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                            .putExtra("name", name);
                    context.sendBroadcast(intent);
                }
            });

            overview.setVisibility(View.VISIBLE);
            overview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("name", name);

                    OverviewFragment fragment = new OverviewFragment();
                    fragment.setArguments(bundle);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });

        } else {
            this.listView.setAdapter(null);
            overview.setVisibility(View.INVISIBLE);
            addToWidget.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), getString(R.string.no_hits), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", this.name);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adView != null) {
            adView.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }

        Log.i(LOG_TAG, "Setting screen name: " + LOG_TAG);
        tracker.setScreenName("Image~" + LOG_TAG);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        if (this.name != null) {
            populateListView(this.name);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adView != null) {
            adView.destroy();
        }
    }
}
