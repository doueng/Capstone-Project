package com.example.douglas.vansbroappen;

/**
 * Created by douglas on 28/06/2016.
 */
public class Utility {

    public String convertTimeToString(double sec) {

        // hh:mm:ss.0 format

        String result = "";
        int hours = (int) (sec / 60) / 60;
        int min = (int) (sec - hours * 60 * 60) / 60;
        double secs = Math.round(((sec - hours * 60 * 60) - min * 60) * 10.0) / 10.0;

        if (hours > 0 && hours < 10) {
            result = "0" + hours + ":";
        } else if (hours >= 0) {
            result = hours + ":";
        }

        if (min < 10) {
            result = result + "0" + min + ":";
        } else {
            result = result + min + ":";
        }

        if (secs < 10) {
            result = result + "0" + secs;
        } else {
            result = result + secs;
        }

        return result;
    }
}
