package alexw.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alexw on 8/1/2017.
 */

public class PersonalBest implements Parcelable {

    private String performance;
    private String distance;
    private String date;
    private String prediction;
    private String ID;
    private Account_Details accountDetails;
    //Private attributes of the personal best entity

    protected PersonalBest(Parcel in) {
        //Constructor for instantiating object when it has been sent from another activity
        performance = in.readString();
        distance = in.readString();
        date = in.readString();
        prediction = in.readString();
        ID = in.readString();
        accountDetails = in.readParcelable(Account_Details.class.getClassLoader());
    }

    public static final Creator<PersonalBest> CREATOR = new Creator<PersonalBest>() {
        @Override
        public PersonalBest createFromParcel(Parcel in) {
            return new PersonalBest(in);
        }

        @Override
        public PersonalBest[] newArray(int size) {
            return new PersonalBest[size];
        }
    };

    public Account_Details getAccountDetails() {
        return accountDetails;
    }
    public String getPerformance() {
        return performance;
    }
    public String getDate() {
        return date;
    }
    public String getPrediction() {
        return prediction;
    }
    public String getDistance() {
        return distance;
    }
    public String getID(){return ID;}
    public void setPrediction(String prediction){this.prediction = prediction;}
    public void setPerformance(String performance){
        this.performance = performance;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getRichDate(){
        return StringManipulation.dateToRichString(date, true);
    }

    public PersonalBest(String ID, Account_Details accountDetails, String distance, String performance, String date,  String prediction ){
        //Constructor
        this.date = date;
        this.ID = ID;
        this.distance = distance;
        this.performance = performance;
        if (prediction == null){
            this.prediction = "";
        }else {
            this.prediction = prediction;
        }
        this.accountDetails = accountDetails;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //Creates a parcel containing objects values to be sent to another activity
        dest.writeString(performance);
        dest.writeString(distance);
        dest.writeString(date);
        dest.writeString(prediction);
        dest.writeString(ID);
        dest.writeParcelable(accountDetails, flags);
    }
}
