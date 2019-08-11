package alexw.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alexw on 5/16/2017.
 */

public class Training extends run_Activity implements Parcelable{

    //Parent class of Active_Training and Completed_Training
    protected String trainingType;
    //Additional protected attributes of a the training class

    protected Training(boolean completed, String description, String name, String date, String ID, Account_Details accountDetails, String trainingType) {
        //Constructor
        super(completed, description, name, date, ID, accountDetails);
        this.trainingType = trainingType;
    }

    public String getTrainingType(){return  trainingType;}

    public enum TrainingType{
        None, LongRun, TempoRun, Track, Fartlek, RecoveryRun,
        HillReps, ProgressionRun
        //Enumeration
    }

    protected Training(Parcel in) {
        super(in);
        trainingType = in.readString();
        //Constructor for instantiating object when it has been sent from another activity
    }

    public static final Creator<Training> CREATOR = new Creator<Training>() {
        @Override
        public Training createFromParcel(Parcel in) {
            return new Training(in);
        }

        @Override
        public Training[] newArray(int size) {
            return new Training[size];
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
        dest.writeString(trainingType);
    }
}

