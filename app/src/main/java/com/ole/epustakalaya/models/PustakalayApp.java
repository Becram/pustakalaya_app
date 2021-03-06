package com.ole.epustakalaya.models;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.ole.epustakalaya.R;

import java.util.HashMap;

/**
 * Created by mjt-ole on 10/30/14.
 */
public class PustakalayApp extends Application {
    // The following line should be changed to include the correct property id.
    private static final String PROPERTY_ID = "UA-33061197-3";

    public static int GENERAL_TRACKER = 0;



    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public PustakalayApp() {
        super();


    }

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(
                    R.xml.global_tracker)
                    : analytics.newTracker(R.xml.ecommerce_tracker);
            t.enableAdvertisingIdCollection(true);
            mTrackers.put(trackerId, t);
        }

        Log.e("pustapp","we reached here baby");
        return mTrackers.get(trackerId);
    }
}
