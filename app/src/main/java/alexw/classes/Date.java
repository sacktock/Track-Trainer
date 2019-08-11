package alexw.classes;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by alexw on 9/13/2017.
 */

public class Date {

    //Class used for formatting strings into date format dd/MM/yyyy
    private int year;
    private int month;
    private int day;

    public int getDay() {
        return day;
    }
    public int getMonth() {
        return month;
    }
    public int getYear() {
        return year;
    }

    public Date(String string){
        //Constructor to instantiate date from string if string is in valid dd/MM/yyyy format
        try {
            new SimpleDateFormat("dd/MM/yyyy").parse(string);
            day = Integer.parseInt(string.substring(0,2));
            month = Integer.parseInt(string.substring(3,5));
            year = Integer.parseInt(string.substring(6));
        } catch (ParseException e) {
            e.printStackTrace();
            day = 1;
            month = 1;
            year = 1930;
        }
    }

    public boolean moreRecentDate(Date date){
        //Returns true if this instance of a rate is more recent than another
        if (year > date.getYear()){
            return true;
        } else if (year < date.getYear()){
            return false;
        }
        if (month > date.getMonth()){
            return true;
        } else if (month < date.getMonth()){
            return false;
        }
        return day >= date.getDay();
    }

    public String makeDate(){
        //Returns a string that describes the values of the date object in the format dd/MM/yyyy
        String string = "";
        if (day < 10) {
            string = "0" + Integer.toString(day);
        } else {
            string = Integer.toString(day);
        }

        string = string + "/";

        if (month < 10) {
            string = string + "0" + Integer.toString(month);
        } else {
            string =  string + Integer.toString(month);
        }

        string = string + "/" + Integer.toString(year);

        return string;
    }

    public Date(int day, int month, int year){
        //Constructor
        this.day = day;
        this.month = month;
        this.year = year;
    }
}
