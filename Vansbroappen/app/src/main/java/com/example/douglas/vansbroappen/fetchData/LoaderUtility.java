package com.example.douglas.vansbroappen.fetchData;

import com.example.douglas.vansbroappen.data.StatsContract;

/**
 * Created by douglas on 05/06/2016.
 */
public class LoaderUtility {

    public interface Query {
        String[] PROJECTION = {
                StatsContract.Stats._ID,
                StatsContract.Stats.POSITION,
                StatsContract.Stats.NAME,
                StatsContract.Stats.CLUB,
                StatsContract.Stats.START_NUMBER,
                StatsContract.Stats.TIME,
                StatsContract.Stats.SPEED,
                StatsContract.Stats.COMPETITION,
                StatsContract.Stats.DATE,
                StatsContract.Stats.LENGTH,
                StatsContract.Stats.NUM_STARTERS,
                StatsContract.Stats.NUM_FINISHED,
                StatsContract.Stats.TOP_THREE_AVERAGE_SPEED,
                StatsContract.Stats.SPEED_DIFFERENCE,
                StatsContract.Stats.TOP_THREE_AVERAGE_TIME,
                StatsContract.Stats.TIME_DIFFERENCE
        };

        int _ID = 0;
        int POSITION = 1;
        int NAME = 2;
        int CLUB = 3;
        int START_NUMBER = 4;
        int TIME = 5;
        int SPEED = 6;
        int COMPETITION = 7;
        int DATE = 8;
        int LENGTH = 9;
        int NUM_STARTERS = 10;
        int NUM_FINISHED = 11;
        int TOP_THREE_AVERAGE_SPEED = 12;
        int SPEED_DIFFERENCE = 13;
        int TOP_THREE_AVERAGE_TIME = 14;
        int TIME_DIFFERENCE = 15;
    }
}
