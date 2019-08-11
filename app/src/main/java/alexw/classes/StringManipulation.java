package alexw.classes;

import android.widget.DatePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

/**
 * Created by alexw on 5/5/2017.
 */


public class StringManipulation {

    public static String convertToString(int x) {
        return Integer.toString(x);
    }

    public static String convertTo2DigitStringNumber(int x) {
        String string;
        string = convertToString(x);
        //If integer is only 1 digit then '0' is added in-front of the string
        if (string.length() == 1) {
            string = "0" + string;
        }

        return string;
    }

    public static boolean isNoNothing(String string) {
        return !string.equals("");
    }

    public static boolean isGapYear(int year) {
        //Checks if a given year is a leap year
        String string = Integer.toString(year).substring(2);
        return Integer.parseInt(string) % 4 == 0;
    }

    public static boolean validEmail(String Email) {
        return Email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches();

    }

    public static boolean validPassword(String password) {
        return password.length() >= 8;
    }

    public static boolean equals(String str1, String str2) {
        return str1.equals(str2);
    }

    public static boolean isThisDateValid(String dateToValidate, String dateFormat) {
        //Checks if a string is a valid date
        if (dateToValidate == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        try {
            //If not valid, it will throw ParseException
            Date date = sdf.parse(dateToValidate);
            return date.compareTo(getDateFromString(getTodayDate())) <= 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static String makeDate(int day, int month, int year) {
        //Creates a string date in the format dd/MM/yyyy
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
            string = string + Integer.toString(month);
        }

        string = string + "/" + Integer.toString(year);

        return string;
    }

    public static String dateToRichString(String dateIN, boolean yearTrue) {
        //Returns a rich date string form a string in the format dd/MM/yyyy
        String dateFromat = "dd/MM/yyyy";
        String day;
        String month;
        String year;
        String RichOutput = "";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
        sdf.setLenient(false);
        try {
            //if not valid, it will throw ParseException
            Date date = sdf.parse(dateIN);
        } catch (ParseException e) {
            return dateIN;
        }

        day = dateIN.substring(0, 2);
        month = dateIN.substring(3, 5);
        year = dateIN.substring(6, 10);

        if (day.equals("01")) {
            RichOutput = RichOutput + "1st";
        }
        if (day.equals("02")) {
            RichOutput = RichOutput + "2nd";
        }
        if (day.equals("03")) {
            RichOutput = RichOutput + "3rd";
        }
        if (day.equals("21")) {
            RichOutput = RichOutput + "21st";
        }
        if (day.equals("22")) {
            RichOutput = RichOutput + "22nd";
        }
        if (day.equals("23")) {
            RichOutput = RichOutput + "23rd";
        }
        if (day.equals("31")) {
            RichOutput = RichOutput + "31st";
        }
        if (doesNotEqual(day, "01")) {
            if (doesNotEqual(day, "02")) {
                if (doesNotEqual(day, "03")) {
                    if (doesNotEqual(day, "21")) {
                        if (doesNotEqual(day, "22")) {
                            if (doesNotEqual(day, "23")) {
                                if (doesNotEqual(day, "31")) {
                                    if ((Integer.parseInt(day) >= 10)) {
                                        RichOutput = RichOutput + day + "th";
                                    } else {
                                        RichOutput = RichOutput + day.substring(1) + "th";
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        RichOutput = RichOutput + " Of";

        if (month.equals("01")) {
            RichOutput = RichOutput + " January";
        }
        if (month.equals("02")) {
            RichOutput = RichOutput + " February";
        }
        if (month.equals("03")) {
            RichOutput = RichOutput + " March";
        }
        if (month.equals("04")) {
            RichOutput = RichOutput + " April";
        }
        if (month.equals("05")) {
            RichOutput = RichOutput + " May";
        }
        if (month.equals("06")) {
            RichOutput = RichOutput + " June";
        }
        if (month.equals("07")) {
            RichOutput = RichOutput + " July";
        }
        if (month.equals("08")) {
            RichOutput = RichOutput + " August";
        }
        if (month.equals("09")) {
            RichOutput = RichOutput + " September";
        }
        if (month.equals("10")) {
            RichOutput = RichOutput + " October";
        }
        if (month.equals("11")) {
            RichOutput = RichOutput + " November";
        }
        if (month.equals("12")) {
            RichOutput = RichOutput + " December";
        }
        if (yearTrue) {
            RichOutput = RichOutput + " " + year;
        }
        return RichOutput;
    }

    public static boolean doesNotEqual(String str1, String str2) {
        return !str1.equals(str2);
    }

    public static int getPosition(String[] array, String value) {
        //Simple linear search
        //Returns index position of a string object within an array
        //Returns -1 if not in the array
        if (value == null) {
            return -1;
        }

        for (int i = 0; i < array.length; i = i + 1) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    public static String getTodayDate() {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date today = new Date();
        return format.format(today);
    }

    public static String getLastWeekDate() {
        //Return the string date of the day 7 days ago
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date newDate = calendar.getTime();

        return format.format(newDate);
    }

    public static Date getDateFromString(String string) {
        try {
            //If not valid, it will throw ParseException
            return new SimpleDateFormat("dd/MM/yyyy").parse(string);
        } catch (ParseException e) {
            Date date = new Date();
            return date;
        }
    }

    public static boolean isWithinAWeek(String string) {
        //Checks if a given string date is no more than 7 days ago
        Date today = getDateFromString(getTodayDate());
        Date date = getDateFromString(string);
        long days = today.getTime() - date.getTime();
        return (TimeUnit.DAYS.convert(days, TimeUnit.MILLISECONDS) <= 6);
    }

    public static long dateDifference(String string) {
        //Returns how many days ago a given string date was from the present day
        Date today = getDateFromString(getTodayDate());
        Date date = getDateFromString(string);
        long days = today.getTime() - date.getTime();
        long output = (TimeUnit.DAYS.convert(days, TimeUnit.MILLISECONDS));
        if (output < 1) {
            output = 1;
        }
        return output;
    }

    public static boolean isWithinAYear(String string) {
        //Returns true if a given string date is no longer than a year ago
        Date today = getDateFromString(getTodayDate());
        Date date = getDateFromString(string);
        long days = today.getTime() - date.getTime();
        return (TimeUnit.DAYS.convert(days, TimeUnit.MILLISECONDS) <= 364);
    }

    public static boolean isWithinAMonth(String string) {
        //Returns true if a given string date is no longer than a year ago
        Date today = getDateFromString(getTodayDate());
        Date date = getDateFromString(string);
        long days = today.getTime() - date.getTime();
        return (TimeUnit.DAYS.convert(days, TimeUnit.MILLISECONDS) <= 30);
    }

    public static int getDistanceInt(String distance) {
        //Converts a distance in string format from "distance"+'m'
        //to an integer distance
        String string = distance.substring(0, distance.length() - 1);
        int i;
        try {
            i = Integer.parseInt(string);
        } catch (Exception e) {
            return 0;
        }
        return i;
    }

    private static boolean queryFilter(String query, String text, int j) {
        //Function checks if a given word (query) is a subset of the word (text) from the start of the word
        //Example: "hel" is a subset of "hello" starting from the beginning so true will be returned
        //"llo" is also a subset of "hello" but doesn't start from the beginning so will not be accepted
        //Must be in order and start from the beginning to be accepted
        return (query.charAt(j) == text.charAt(j)) && query.length() == j + 1 || query.charAt(j) == text.charAt(j) && queryFilter(query, text, j + 1);
        //Recursive function
    }

    public static boolean typingEquals(String query, String text) {
        return queryFilter(query, text, 0);
        //Used to check if a word being typed is a subset of a given word
    }
}
