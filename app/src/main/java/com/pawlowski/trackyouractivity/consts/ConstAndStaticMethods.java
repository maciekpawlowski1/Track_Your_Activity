package com.pawlowski.trackyouractivity.consts;

import android.util.Patterns;

import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.models.TrainingModel;

import java.util.regex.Pattern;

public class ConstAndStaticMethods {
    public static final int REQUEST_LOCATION_PERMISSION = 631;
    public static final int REQUEST_LOCATION_PERMISSION2 = 632;

    public static final String INTENT_FILTER_STOP_TEXT = "stopTrackingService";

    public static final String NOTIFICATION_CHANNEL_ID = "TrackYourActivityChannel";

    public static final String SERVICE_NAME = "TrackingService";

    public static final int NOTIFICATION_ID = 1;
    public static final int UPDATE_CHECK_DELTA_TIME_IN_SECONDS = 300;

    public static long SPEED_TIME_CHECKING_DELTA_IN_SECONDS = 15;

    public static String SHARED_PREFERENCES_NAME = "TrackYourActivity";

    public static int TRACKING_INTERVAL = 7000;
    public static int TRACKING_FAST_INTERVAL = 2000;

    public static long GPX_UPDATE_TIME_DELTA_IN_SECONDS = 10;



    public static double getKcalForGivenSecondsHavingMet(double weight, double met, long seconds)
    {
        return (((met * weight * 3.5) / 200.) / 60.) * seconds;
    }


    public static double getCyclingMet(double speedKmh)
    {
        double speedMph = speedKmh / 1.61;
        if(speedMph <= 9.4)
        {
            return 3.5;
        }
        else if(speedMph > 9.4 && speedMph <= 10)
        {
            return 5.8;
        }
        else if(speedMph > 10 && speedMph <= 11.9)
        {
            return 6.8;
        }
        else if(speedMph > 11.9 && speedMph <= 13.9)
        {
            return 8.0;
        }
        else if(speedMph > 13.9 && speedMph <= 15.9)
        {
            return 10;
        }
        else if(speedMph > 15.9 && speedMph <= 19)
        {
            return 12.0;
        }
        else if(speedMph > 19)
        {
            return 15.8;
        }
        else
            return 0.;
    }

    public static double getRollerSkatingMet(double speedKmh)
    {
        double speedMph = speedKmh / 1.61;
        if(speedMph <= 9.0)
        {
            return 7.0;
        }
        else if(speedMph > 9.0 && speedMph <= 11.0)
        {
            return 7.5;
        }
        else if(speedMph > 11.0 && speedMph <= 13.0)
        {
            return 9.8;
        }
        else if(speedMph > 13.0 && speedMph <= 13.6)
        {
            return 12.3;
        }
        else if(speedMph > 13.6)
        {
            return 14.0;
        }
        else
            return 0.;
    }

    public static double getWalkingMet(double speedKmh)
    {
        double speedMph = speedKmh / 1.61;
        if(speedMph <= 2.0)
        {
            return 2.0;
        }
        else if(speedMph > 2.0 && speedMph <= 2.5)
        {
            return 2.8;
        }
        else if(speedMph > 2.5 && speedMph <= 2.8)
        {
            return 3.0;
        }
        else if(speedMph > 2.8 && speedMph <= 3.2)
        {
            return 3.5;
        }
        else if(speedMph > 3.5 && speedMph <= 4.0)
        {
            return 4.3;
        }
        else if(speedMph > 4.0 && speedMph <= 4.5)
        {
            return 5.0;
        }
        else if(speedMph > 4.5 && speedMph <= 5.0)
        {
            return 7.0;
        }
        else if(speedMph > 5.0)
        {
            return 8.3;
        }
        else
            return 0.;
    }

    public static double getRunningMet(double speedKmh)
    {
        double speedMph = speedKmh / 1.61;
        if(speedMph <= 4)
        {
            return 5.0;
        }
        else if(speedMph > 4 && speedMph <= 5)
        {
            return 6.0;
        }
        else if(speedMph > 5 && speedMph <= 5.2)
        {
            return 8.3;
        }
        else if(speedMph > 5.2 && speedMph <= 6)
        {
            return 9.0;
        }
        else if(speedMph > 6 && speedMph <= 6.7)
        {
            return 9.8;
        }
        else if(speedMph > 6.7 && speedMph <= 7)
        {
            return 10.5;
        }
        else if(speedMph > 7 && speedMph <= 7.5)
        {
            return 11;
        }
        else if(speedMph > 7.5 && speedMph <= 8)
        {
            return 11.5;
        }
        else if(speedMph > 8 && speedMph <= 8.6)
        {
            return 11.8;
        }
        else if(speedMph > 8.6 && speedMph <= 9)
        {
            return 12.3;
        }
        else if(speedMph > 9 && speedMph <= 10)
        {
            return 12.8;
        }
        else if(speedMph > 10 && speedMph <= 11)
        {
            return 14.5;
        }
        else if(speedMph > 11 && speedMph <= 12)
        {
            return 16.0;
        }
        else if(speedMph > 12 && speedMph <= 13)
        {
            return 19.0;
        }
        else if(speedMph > 13 && speedMph <= 14)
        {
            return 19.8;
        }
        else if(speedMph > 14)
        {
            return 23.0;
        }
        else
        {
            return 0.0;
        }

    }




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

    public static String cutDouble(double value)
    {
        value*= 10;
        value = Math.floor(value);
        value/= 10.0;
        return value+"";
    }

    public static boolean isNameCorrect(String name)
    {
        return name.length() > 2;
    }

    public static boolean isDateCorrect(String date)
    {
        final String DATE_PATTERN = "^(3[01]|[12][0-9]|0[1-9])-(1[0-2]|0[1-9])-[0-9]{4}$";//"/^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$/";
        Pattern pattern = Pattern.compile(DATE_PATTERN);
        return pattern.matcher(date).matches();
    }



    public static boolean isMailCorrect(String mail)
    {
        return Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }



    public static boolean isPasswordCorrect(String password)
    {
        if(password.length() > 5)
        {
            boolean smallLetter = false;
            boolean bigLetter = false;
            boolean decimal = false;
            for(int i=0;i<password.length();i++)
            {
                char z = password.charAt(i);
                if (z >= 'a' && z <= 'z')
                {
                    smallLetter = true;
                }
                else if (z >= 'A' && z <= 'Z')
                {
                    bigLetter = true;
                }
                else if (z >= '0' && z <= '9')
                {
                    decimal = true;
                }
            }
            return smallLetter && bigLetter && decimal;
        }
        else
        {
            return false;
        }
    }



    public static int getImageResourceOfTrainingType(int trainingType, boolean isWhite)
    {
        if(isWhite)
        {
            if(trainingType == TrainingModel.TrainingType.RUNNING.ordinal())
            {
                return R.drawable.run_icon_white;
            }
            else if(trainingType == TrainingModel.TrainingType.CYCLING.ordinal())
            {
                return R.drawable.bike_icon_white;
            }
            else if(trainingType == TrainingModel.TrainingType.ROLLER_SKATING.ordinal())
            {
                return R.drawable.roller_skating_icon_white;
            }
            else if(trainingType == TrainingModel.TrainingType.NORDIC_WALKING.ordinal())
            {
                return R.drawable.nordic_walking_icon_white;
            }
        }
        else
        {
            if(trainingType == TrainingModel.TrainingType.RUNNING.ordinal())
            {
                return R.drawable.run_icon2;
            }
            else if(trainingType == TrainingModel.TrainingType.CYCLING.ordinal())
            {
                return R.drawable.bike_icon_dark;
            }
            else if(trainingType == TrainingModel.TrainingType.ROLLER_SKATING.ordinal())
            {
                return R.drawable.roller_skating_icon_dark;
            }
            else if(trainingType == TrainingModel.TrainingType.NORDIC_WALKING.ordinal())
            {
                return R.drawable.nordic_walking_icon_dark;
            }
        }

        return -1;
    }

}
