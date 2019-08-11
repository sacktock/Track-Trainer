package alexw.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alexw on 7/12/2017.
 */

public class Completed_Training extends Training implements Parcelable {

    protected String difficulty;
    //Additional private attribute of training entity

    //Easy is default value
    public enum Difficulty{
        Easy, Good, Challenging, Hard
        //Enumeration
    }

    public String getDifficulty(){
        if (completed) {
            return difficulty;
        }else {
            return Difficulty.Easy.toString();
        }
    }

    public Completed_Training(String ID, Account_Details accountDetails, String name, String dateCompleted, String description,
                               String trainingType, String difficulty ){
        //Constructor using description
        super(true, description, name, dateCompleted, ID, accountDetails, trainingType);
        this.difficulty = difficulty;
    }

    public Completed_Training(String ID, Account_Details accountDetails, String name, String dateCompleted, Object[] sets,
                              String trainingType, String difficulty){
        //Constructor using an array of training sets
        super(true, "", name, dateCompleted, ID, accountDetails, (trainingType));
        this.completed = true;
        for (int i = 0; i < sets.length ; i= i +1) {
            if (sets[i] instanceof Training_Set){
                Training_Set set = Training_Set.class.cast(sets[i]);
                this.description = getDescription() + set.getRepDesc() + ". ";
            }
        }
        this.difficulty = difficulty;
    }

    public String getRichDateCompleted(){
        if (completed){
            StringManipulation s = new StringManipulation();
            return s.dateToRichString(date, true);
        } else {
            return "To Be Completed";
        }}

    public String getDateCompleted(){
        if (completed){
            return date;
        } else {
            return "To Be Completed";
        }}

    protected Completed_Training(Parcel in) {
        //Constructor for created completed training object from another activity
        super(in);
        difficulty = in.readString();
    }

    public static final Creator<Completed_Training> CREATOR = new Creator<Completed_Training>() {
        @Override
        public Completed_Training createFromParcel(Parcel in) {
            return new Completed_Training(in);
        }

        @Override
        public Completed_Training[] newArray(int size) {
            return new Completed_Training[size];
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
        dest.writeString(difficulty);
    }
}
