package alexw.classes;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Chronometer;

import java.sql.Time;

/**
 * Created by alexw on 7/12/2017.
 */

public class RaceTime implements Parcelable {

    //Class used for formatting strings into race time format hh:mm:ss:hh
    private int hours;
    private int minutes;
    private int seconds;
    private int milliseconds;

    protected RaceTime(Parcel in) {
        //Constructor for instantiating object when it has been sent from another activity
        hours = in.readInt();
        minutes = in.readInt();
        seconds = in.readInt();
        milliseconds = in.readInt();
    }

    public static final Creator<RaceTime> CREATOR = new Creator<RaceTime>() {
        @Override
        public RaceTime createFromParcel(Parcel in) {
            return new RaceTime(in);
        }

        @Override
        public RaceTime[] newArray(int size) {
            return new RaceTime[size];
        }
    };

    public int getHours(){return hours;}
    public int getMinutes() {return minutes;}
    public int getSeconds() {return seconds;}
    public int getMilliseconds() {return milliseconds;}

    public RaceTime(int hours, int minutes, int seconds, int milliseconds){
        //Constructor
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.milliseconds = milliseconds;
    }

    public RaceTime(int seconds){
        //Constructor using seconds in integer format
        double minutes;
        int setSeconds;
        int setMinutes;
        setSeconds = seconds % 60;
        setMinutes = seconds % 3600;
        minutes = Math.floor(setMinutes / 60);
        this.seconds = setSeconds;
        this.minutes = (int)minutes;
        this.milliseconds = 0;
        this.hours = (int)Math.floor(seconds / 3600);
    }

    public RaceTime(double d){
        //Constructor using seconds in double format
        int seconds = (int)Math.floor(d);
        double minutes;
        int setSeconds;
        int setMinutes;
        setSeconds = seconds % 60;
        setMinutes = seconds % 3600;
        minutes = Math.floor(setMinutes / 60);
        this.seconds = setSeconds;
        this.minutes = (int)minutes;
        this.hours = (int)Math.floor(seconds / 3600);
        milliseconds = (int)Math.round((d- Math.floor(d)) * 100);
    }

    public RaceTime(String string){
        //Constructor using a string in race time format hh:mm:ss:hh
        stringToRaceTime(string);
    }

    public int getTimeInSeconds(){
        //Return amount of seconds in integer format
        return (getHours() * 3600) + (getMinutes()*60) + getSeconds();
    }

    public double getTimeInDouble(){
        //Return amount of seconds in double format
        return (getHours() * 3600) + (getMinutes() * 60) + getSeconds() + 0.01*getMilliseconds();
    }

    public String getTime(boolean includeSeconds, boolean includeMilliseconds){
        //Returns string in race time format hh:mm:ss:hh
        StringManipulation sm = new StringManipulation();
        MathFunc mf = new MathFunc();
        String string = "";

        if (MathFunc.isNotZero(hours)){
            string = string + StringManipulation.convertToString(hours) + ":";
        }

        if (MathFunc.isNotZero(minutes) || StringManipulation.isNoNothing(string)){
            if (StringManipulation.isNoNothing(string)) {
                string = string + StringManipulation.convertTo2DigitStringNumber(minutes);
            }else {
                string = string + StringManipulation.convertToString(minutes);
            }
            if (includeSeconds){
                string = string + ":";
            }
        }

        if (MathFunc.isNotZero(seconds)|| StringManipulation.isNoNothing(string)){
            if (includeSeconds){
                if (StringManipulation.isNoNothing(string)) {
                    string = string + StringManipulation.convertTo2DigitStringNumber(seconds);
                } else {
                    string = string + StringManipulation.convertToString(seconds);
                }
                if (includeMilliseconds){
                    string = string + ".";
                }
            }
        }

        if (MathFunc.isNotZero(milliseconds)|| StringManipulation.isNoNothing(string)){
            if (includeMilliseconds) {
                if (StringManipulation.isNoNothing(string)) {
                    string = string + StringManipulation.convertTo2DigitStringNumber(milliseconds);
                }else {
                    string = string + StringManipulation.convertToString(milliseconds);
                }
            }
        }

        return string;
    }

    public boolean isQuicker(RaceTime timeA){
        //Returns true if this instance of RaceTime is quicker than another
        if(hours < timeA.getHours()) {
            return true;
        }else if(hours > timeA.getHours()){
            return false;
        }

        if(minutes < timeA.getMinutes()){
            return true;
        }else if(minutes > timeA.getMinutes()){
            return false;
        }

        if(seconds < timeA.getSeconds()){
            return true;
        }else if(seconds > timeA.getSeconds()){
            return false;
        }

        if(milliseconds < timeA.getMilliseconds()){
            return true;
        }else if(milliseconds > timeA.getMilliseconds()){
            return false;
        }

        return false;
    }

    private void stringToRaceTime(String string){
        //Formats a string in race time format hh:mm:ss:hh to create an instance of RaceTime
        if (string.length() == 11){
            hours = Integer.parseInt(string.substring(0,2));
            minutes = Integer.parseInt(string.substring(3,5));
            seconds = Integer.parseInt(string.substring(6,8));
            milliseconds = Integer.parseInt(string.substring(9,11));
        }

        if (string.length() == 10){
            hours = Integer.parseInt(string.substring(0,1));
            minutes = Integer.parseInt(string.substring(2,4));
            seconds = Integer.parseInt(string.substring(5,7));
            milliseconds = Integer.parseInt(string.substring(8,10));
        }

        if (string.length() == 8){
            hours = 0;
            minutes = Integer.parseInt(string.substring(0,2));
            seconds = Integer.parseInt(string.substring(3,5));
            milliseconds = Integer.parseInt(string.substring(6,8));
        }

        if (string.length() == 7){
            hours = 0;
            minutes = Integer.parseInt(string.substring(0,1));
            seconds = Integer.parseInt(string.substring(2,4));
            milliseconds = Integer.parseInt(string.substring(5,7));
        }

        if (string.length() == 5){
            seconds = Integer.parseInt(string.substring(0,2));
            milliseconds = Integer.parseInt(string.substring(3,5));
        }

        if (string.length() == 4){
            seconds = Integer.parseInt(string.substring(0,1));
            milliseconds = Integer.parseInt(string.substring(2,4));
        }
    }

    public static boolean isValidTime(String string){
        //Validation to check a string is in race time format hh:mm:ss:hh

        if (string.length() == 11){
            if (MathFunc.isInteger(string.substring(0,2))){
                if (MathFunc.isInteger(string.substring(3,5))){
                    if (MathFunc.isInteger(string.substring(6,8))){
                        if (MathFunc.isInteger(string.substring(9,11))){
                            return true;
                        }
                    }
                }
            }
        }

        if (string.length() == 10){
            if (MathFunc.isInteger(string.substring(0,1))){
                if (MathFunc.isInteger(string.substring(2,4))){
                    if (MathFunc.isInteger(string.substring(5,7))){
                        if (MathFunc.isInteger(string.substring(8,10))){
                            return true;
                        }
                    }
                }
            }
        }

        if (string.length() == 8){
            if (MathFunc.isInteger(string.substring(0,2))){
                if (MathFunc.isInteger(string.substring(3,5))){
                    if (MathFunc.isInteger(string.substring(6,8))){
                        return true;
                    }
                }
            }
        }

        if (string.length() == 7){
            if (MathFunc.isInteger(string.substring(0,1))){
                if (MathFunc.isInteger(string.substring(2,4))){
                    if (MathFunc.isInteger(string.substring(5,7))){
                        return true;
                    }
                }
            }
        }

        if (string.length() == 5){
            if (MathFunc.isInteger(string.substring(0,2))){
                if (MathFunc.isInteger(string.substring(3,5))){
                    return true;
                }
            }
        }
        if (string.length() == 4){
            if (MathFunc.isInteger(string.substring(0,1))){
                if (MathFunc.isInteger(string.substring(2,4))){
                    return true;
                }
            }
        }
        return false;
    }

    public static int simpleStringToSeconds(String string){
        //Converts a simple race time format string hh:mm:ss into seconds
        int minutes = 0;
        int seconds = 0;
        if (string.length() ==5){
            minutes = Integer.parseInt(string.substring(0,2));
            seconds = Integer.parseInt(string.substring(3,5));
        }

        if (string.length() ==4){
            minutes = Integer.parseInt(string.substring(0,1));
            seconds = Integer.parseInt(string.substring(2,4));
        }

        if (string.length() == 2){
            seconds = Integer.parseInt(string.substring(0,2));
        }

        if (string.length() == 1){
            seconds = Integer.parseInt(string.substring(0,1));
        }

        return (minutes * 60) + seconds;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //Creates a parcel containing objects values to be sent to another activity
        dest.writeInt(hours);
        dest.writeInt(minutes);
        dest.writeInt(seconds);
        dest.writeInt(milliseconds);
    }
}
