package alexw.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Time;

/**
 * Created by alexw on 7/12/2017.
 */

public class Race extends run_Activity implements Parcelable{

    private String performance;
    private String distance;
    //Private attributes for a race entity

    public String getDistance(){return distance;}
    public String getPerformance() {return performance;}

    public String getRichDateCompleted(){
        StringManipulation sm = new StringManipulation();
        return sm.dateToRichString(date, true);
    }

    protected Race(Parcel in) {
        //Constructor for instantiating object when it has been sent from another activity
        super(in);
        distance = in.readString();
        performance = in.readString();
    }

    public static final Creator<Race> CREATOR = new Creator<Race>() {
        @Override
        public Race createFromParcel(Parcel in) {
            return new Race(in);
        }

        @Override
        public Race[] newArray(int size) {
            return new Race[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //Creates a parcel containing objects values to be sent to another activity
        super.writeToParcel(dest, flags);
        dest.writeString(distance);
        dest.writeString(performance);
    }

    public Race(String ID, Account_Details accountDetails, String name, String dateCompleted,
                String description, String performance, String distance){
        //Constructor
        super(true,"",name,dateCompleted, ID, accountDetails);

        this.description = distance + " Performance: " + performance;
        this.distance = distance;
        this.performance = performance;
    }

    public PersonalBest createPersonalBestObject(){
        //Method for creating a personal best object from its self
        return new PersonalBest(ID, accountDetails, distance, performance, date, "");
    }
}
