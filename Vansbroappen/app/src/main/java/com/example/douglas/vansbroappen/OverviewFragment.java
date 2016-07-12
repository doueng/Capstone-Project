package com.example.douglas.vansbroappen;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
        implements AdapterView.OnItemSelectedListener {

    private static final int URL_LOADER = 0;
    private LineChart chart;

    private ArrayList<String> xAxis;
    private ArrayList<Integer> positions;
    private ArrayList<Float> speedDifferences;

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

        xAxis = new ArrayList<>();
        positions = new ArrayList<>();
        speedDifferences = new ArrayList<>();

        ArrayList<String> competitions = bundle.getStringArrayList("competitions");
        ArrayList<String> positions = bundle.getStringArrayList("positions");
        ArrayList<String> speedDifferences = bundle.getStringArrayList("speedDifferences");

        if (competitions != null && positions != null && speedDifferences != null) {
            for (String competition : competitions) {
                String[] parts = competition.split(" ");
                String year = parts[1];
                xAxis.add(year);
            }

            for (String position : positions) {
                this.positions.add(Integer.parseInt(position));
            }

            for (String speedDifference : speedDifferences) {
                this.speedDifferences.add(Float.parseFloat(speedDifference) * -1);
            }
        }
        setChartData(positions, "int");



        return rootView;
    }

    protected void setChartData(ArrayList chartData, String chartType) {
        LineData data = new LineData(xAxis, getDataSet(chartData, chartType));

        chart.clear();

        chart.setData(data);

        chart.setDescription("");
        chart.getXAxis().setTextSize(14f);
        chart.getAxisLeft().setTextSize(14f);
        chart.getAxisRight().setTextSize(14f);
        chart.setExtraTopOffset(15f);
        chart.setDescriptionTextSize(12f);

        if (chartType.equals("positionChart")) {
            YAxisValueFormatter yAxisValueFormatter = new YAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, YAxis yAxis) {
                    return "" + ((int) value);
                }
            };
            chart.getAxisLeft().setInverted(true);
            chart.getAxisLeft().setValueFormatter(yAxisValueFormatter);
            chart.getAxisRight().setValueFormatter(yAxisValueFormatter);

        } else if (chartType.equals("km/hChart")) {
            YAxisValueFormatter yAxisValueFormatter = new YAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, YAxis yAxis) {
                    return "" + value;
                }
            };
            chart.setDescription(getString(R.string.kmh_chart_description));
            chart.getAxisLeft().setValueFormatter(yAxisValueFormatter);
            chart.getAxisRight().setValueFormatter(yAxisValueFormatter);
        }

        chart.invalidate();
    }

    protected LineDataSet getDataSet(ArrayList chartData, String chartType) {

        ArrayList<Entry> entries = new ArrayList<>();
        String name = "";

        for (int i = 0; i < chartData.size(); i++) {
            Entry entry = null;
            if (chartType.equals("positionChart")) {
                int chartValue = (Integer) chartData.get(i);
                entry = new Entry(chartValue, i);
            } else if (chartType.equals("km/hChart")) {
                Float chartValue = (Float) chartData.get(i);
                entry = new Entry(chartValue, i);
            }
            if (entry != null) {
                entries.add(entry);
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, name);

        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(true);
        dataSet.setColor(ColorTemplate.VORDIPLOM_COLORS[3]);
        dataSet.setValueTextSize(16f);

        if (chartType.equals("positionChart")) {
            dataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return "" + ((int) value);
                }
            });

        } else if (chartType.equals("km/hChart")) {
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
                setChartData(speedDifferences, "km/hChart");
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
