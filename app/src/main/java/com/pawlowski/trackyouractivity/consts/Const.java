package com.pawlowski.trackyouractivity.consts;

public class Const {
    public static final int REQUEST_LOCATION_PERMISSION = 631;
    public static final int REQUEST_LOCATION_PERMISSION2 = 632;

    public static final String INTENT_FILTER_STOP_TEXT = "stopTrackingService";

    public static final String NOTIFICATION_CHANNEL_ID = "TrackYourActivityChannel";

    public static final String SERVICE_NAME = "TrackingService";

    public static final int NOTIFICATION_ID = 1;

    public static long SPEED_TIME_CHECKING_DELTA_IN_SECONDS = 15;

    public static String SHARED_PREFERENCES_NAME = "TrackYourActivity";

    public static int TRACKING_INTERVAL = 7000;
    public static int TRACKING_FAST_INTERVAL = 2500;

    public static long GPX_UPDATE_TIME_DELTA_IN_SECONDS = 10;


    public static String convertSecondsToTimeTest(long seconds)
    {
        long secondsT = (seconds - (seconds/60)*60);
        long minutesT = ((seconds/60) - (seconds/3600)*3600);
        long hoursT = (seconds/3600);
        return (hoursT<10?"0"+hoursT:hoursT+"")+":"+(minutesT<10?"0"+minutesT:minutesT+"")+":"+(secondsT<10?"0"+secondsT:secondsT+"");
    }

    public static double distanceMetersToKilometers(double distance)
    {
        distance /= 1000.0;
        distance*= 10;
        distance = Math.floor(distance);
        distance/= 10.0;
        return distance;
    }

}
