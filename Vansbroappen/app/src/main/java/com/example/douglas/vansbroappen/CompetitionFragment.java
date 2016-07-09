package com.example.douglas.vansbroappen;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.douglas.vansbroappen.data.StatsContract;
import com.example.douglas.vansbroappen.fetchData.LoaderUtility;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by douglas on 02/06/2016.
 */
public class CompetitionFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int URL_LOADER = 0;

    private Tracker tracker;
    private static final String LOG_TAG = CompetitionFragment.class.getSimpleName();

    @BindView(R.id.position)
    TextView position;

    @BindView(R.id.name)
    TextView name;

    @BindView(R.id.club)
    TextView club;

    @BindView(R.id.time)
    TextView time;

    @BindView(R.id.speed)
    TextView speedText;

    @BindView(R.id.competition)
    TextView competition;

    @BindView(R.id.length)
    TextView length;

    @BindView(R.id.num_starters)
    TextView numStarters;

    @BindView(R.id.num_finished)
    TextView numFinished;

    @BindView(R.id.top_three_speed)
    TextView topThreeSpeedText;

    @BindView(R.id.top_three_time)
    TextView topThreeTime;

    @BindView(R.id.difference_time)
    TextView differenceTime;

    @BindView(R.id.difference_speed)
    TextView differenceSpeed;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.competition_fragment, container, false);
        ButterKnife.bind(this, rootView);

        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        tracker = application.getDefaultTracker();

        Bundle bundle = getArguments();
        getLoaderManager().initLoader(URL_LOADER, bundle, this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selection = StatsContract.StatsColumns.NAME + " = ?" +
                " AND " + StatsContract.StatsColumns.COMPETITION + " = ?";
        String[] selectionArgs = {args.getString("name"),
                args.getString("selectedCompetition")};

        return new CursorLoader(
                getActivity(),
                StatsContract.Stats.buildDirUri(),
                LoaderUtility.Query.PROJECTION,
                selection,
                selectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        while (cursor.moveToNext()) {

            position.setText(cursor.getString(LoaderUtility.Query.POSITION));
            name.setText(cursor.getString(LoaderUtility.Query.NAME).toUpperCase());
            club.setText(cursor.getString(LoaderUtility.Query.CLUB));
            time.setText(cursor.getString(LoaderUtility.Query.TIME));

            String speed = (double) Math.round(cursor.getDouble
                    (LoaderUtility.Query.SPEED) * 10) / 10 + " " + getString(R.string.km_per_h);
            speedText.setText(speed);

            competition.setText(cursor.getString(LoaderUtility.Query.COMPETITION));

            String lengthStr = getString(R.string.length) + "\n"
                    + cursor.getString(LoaderUtility.Query.LENGTH) + getString(R.string.meter);
            length.setText(lengthStr);

            String starter = getString(R.string.start) + "\n" + cursor.getString(LoaderUtility.Query.NUM_STARTERS);
            numStarters.setText(starter);

            String finished = getString(R.string.finishers) + "\n" + cursor.getString(LoaderUtility.Query.NUM_FINISHED);
            numFinished.setText(finished);

            String topThreeSpeed = (double) Math.round(cursor.getDouble
                    (LoaderUtility.Query.TOP_THREE_AVERAGE_SPEED) * 10) / 10
                    + " " + getString(R.string.km_per_h);
            topThreeSpeedText.setText(topThreeSpeed);

            topThreeTime.setText(cursor.getString(LoaderUtility.Query.TOP_THREE_AVERAGE_TIME));
            differenceTime.setText(cursor.getString(LoaderUtility.Query.TIME_DIFFERENCE));

            String diffSpeed = cursor.getString(LoaderUtility.Query.SPEED_DIFFERENCE)
                    + " " + getString(R.string.km_per_h);
            differenceSpeed.setText(diffSpeed);

        }

        cursor.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "Setting screen name: " + LOG_TAG);
        tracker.setScreenName("Image~" + LOG_TAG);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
