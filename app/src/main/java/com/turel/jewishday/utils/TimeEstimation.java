package com.turel.jewishday.utils;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import static org.joda.time.LocalTime.now;

/**
 * Created by Haim.Turkel on 2/8/2016.
 */
public class TimeEstimation {

    public static final LocalTime SEC_TO_MIDNIGHT = new LocalTime(23, 59, 59, 0);

    private LocalTime todaysAlarm = SEC_TO_MIDNIGHT;
    private String todaysDescription;
    private LocalTime tommarowAlarm = SEC_TO_MIDNIGHT;
    private String tommarowDescription;


    public TimeEstimation() {
    }

    public void setTodaysAlarm(LocalTime todaysAlarm) {
        this.todaysAlarm = todaysAlarm;
    }

    public void setTommarowAlarm(LocalTime tommarowAlarm) {
        this.tommarowAlarm = tommarowAlarm;
    }

    public LocalTime getTodaysAlarm() {
        return todaysAlarm;
    }

    public LocalTime getTommarowAlarm() {
        return tommarowAlarm;
    }

    public DateTime getNextAlarm(){
        if (todaysAlarm.isAfter(now()) && todaysAlarm!=SEC_TO_MIDNIGHT) {
            return todaysAlarm.toDateTimeToday();
        }
        else if (tommarowAlarm!=SEC_TO_MIDNIGHT) {
            return tommarowAlarm.toDateTimeToday().plusDays(1);
        }
        else
            return null;
    }

    public String getNextAlarmDescription(){
        if (todaysAlarm.isAfter(now()) && todaysAlarm!=SEC_TO_MIDNIGHT) {
            return todaysDescription;
        }
        else if (tommarowAlarm!=SEC_TO_MIDNIGHT) {
            return tommarowDescription;
        }
        else
            return "";
    }

    public String getTodaysDescription() {
        return todaysDescription;
    }

    public void setTodaysDescription(String todaysDescription) {
        this.todaysDescription = todaysDescription;
    }

    public String getTommarowDescription() {
        return tommarowDescription;
    }

    public void setTommarowDescription(String tommarowDescription) {
        this.tommarowDescription = tommarowDescription;
    }
}
