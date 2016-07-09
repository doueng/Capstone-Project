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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.douglas.vansbroappen.data.StatsContract;
import com.example.douglas.vansbroappen.fetchData.LoaderUtility;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by douglas on 18/06/2016.
 */
public class OverviewFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener {

    private static final int URL_LOADER = 0;
    private LineChart chart;

    private ArrayList<String> xAxis = new ArrayList<>();
    private ArrayList<Integer> positions = new ArrayList<>();
    private ArrayList<Float> speedDifference = new ArrayList<>();

    private Tracker tracker;
    private static final String LOG_TAG = OverviewFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRetainInstance(true);

        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        tracker = application.getDefaultTracker();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.overview_fragment, container, false);
        ButterKnife.bind(this, rootView);

        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
        String[] dropDownOptions = {getString(R.string.result), getString(R.string.km_per_h)};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, dropDownOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        chart = (LineChart) rootView.findViewById(R.id.chart);
        Bundle bundle = getArguments();
        getLoaderManager().initLoader(URL_LOADER, bundle, this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = StatsContract.StatsColumns.NAME + " = ?";
        String[] selectionArgs = {args.getString("name")};

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
            String competition = cursor.getString(LoaderUtility.Query.COMPETITION);
            String[] parts = competition.split(" ");
            String year = parts[1];
            xAxis.add(year);

            int position = Integer.parseInt(cursor.getString(LoaderUtility.Query.POSITION));
            positions.add(position);

            float speedDiff = (float) cursor.getDouble(LoaderUtility.Query.SPEED_DIFFERENCE);
            speedDiff *= -1;
            speedDifference.add(speedDiff);
        }

        cursor.close();

        setChartData(positions, "int");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    protected void setChartData(ArrayList chartData, String chart) {
        LineData data = new LineData(xAxis, getDataSet(chartData, chart));

        this.chart.setData(data);
        this.chart.setDescription("");
        this.chart.getXAxis().setTextSize(14f);
        this.chart.getAxisLeft().setTextSize(14f);
        this.chart.getAxisRight().setTextSize(14f);
        this.chart.setExtraTopOffset(15f);
        this.chart.setDescriptionTextSize(12f);

        if (chart.equals("positionChart")) {
            YAxisValueFormatter yAxisValueFormatter = new YAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, YAxis yAxis) {
                    return "" + ((int) value);
                }
            };
            this.chart.getAxisLeft().setInverted(true);
            this.chart.getAxisLeft().setValueFormatter(yAxisValueFormatter);
            this.chart.getAxisRight().setValueFormatter(yAxisValueFormatter);

        } else if (chart.equals("km/hChart")) {
            YAxisValueFormatter yAxisValueFormatter = new YAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, YAxis yAxis) {
                    return "" + value;
                }
            };
            this.chart.setDescription(getString(R.string.kmh_chart_description));
            this.chart.getAxisLeft().setValueFormatter(yAxisValueFormatter);
            this.chart.getAxisRight().setValueFormatter(yAxisValueFormatter);
        }
        this.chart.invalidate();
    }


    protected LineDataSet getDataSet(ArrayList chartData, String chart) {

        ArrayList<Entry> entries = new ArrayList<>();
        String name = "";

        for (int i = 0; i < chartData.size(); i++) {
            Entry entry = null;
            if (chart.equals("positionChart")) {
                int chartValue = (Integer) chartData.get(i);
                entry = new Entry(chartValue, i);
            } else if (chart.equals("km/hChart")) {
                Float chartValue = (Float) chartData.get(i);
                entry = new Entry(chartValue, i);
            }
            entries.add(entry);
        }

        LineDataSet dataSet = new LineDataSet(entries, name);

        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(true);
        dataSet.setColor(ColorTemplate.VORDIPLOM_COLORS[3]);
        dataSet.setValueTextSize(16f);

        if (chart.equals("positionChart")) {
            dataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return "" + ((int) value);
                }
            });

        } else if (chart.equals("km/hChart")) {
            dataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return "" + value;
                }
            });
        }

        return dataSet;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int chart, long l) {

        switch (chart) {
            case 0:
                setChartData(positions, "positionChart");
                break;
            case 1:
                setChartData(speedDifference, "km/hChart");
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "Setting screen name: " + LOG_TAG);
        tracker.setScreenName("Image~" + LOG_TAG);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
