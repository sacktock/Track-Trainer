package alexw.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alexw on 5/16/2017.
 */

public class run_Activity implements Parcelable {

    //Parents class of: Training, Completed_Training, Active_Training and Race
    protected boolean completed;
    protected String description;
    protected String name;
    protected String date;
    protected String ID;
    protected Account_Details accountDetails;
    //Protected attributes that are shared by child classes


    protected run_Activity(Parcel in) {
        //Constructor for instantiating object when it has been sent from another activity
        completed = in.readByte() != 0;
        description = in.readString();
        name = in.readString();
        date = in.readString();
        ID = in.readString();
        accountDetails = in.readParcelable(Account_Details.class.getClassLoader());
    }

    public static final Creator<run_Activity> CREATOR = new Creator<run_Activity>() {
        @Override
        public run_Activity createFromParcel(Parcel in) {
            return new run_Activity(in);
        }

        @Override
        public run_Activity[] newArray(int size) {
            return new run_Activity[size];
        }
    };

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName(){return name;}
    public String getDate(){return date;}
    public String getDescription(){return description;}
    public String getID() {return ID;}
    public boolean isCompleted() {return completed;}
    public Account_Details getAccountDetails() {return accountDetails;}

    protected run_Activity(boolean completed, String description, String name, String date, String ID, Account_Details accountDetails){
        //Constructor
        this.completed = completed;
        this.ID = ID;
        this.description = description;
        this.name = name;
        this.date = date;
        this.accountDetails = accountDetails;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //Creates a parcel containing objects values to be sent to another activity
        dest.writeByte((byte) (completed ? 1 : 0));
        dest.writeString(description);
        dest.writeString(name);
        dest.writeString(date);
        dest.writeString(ID);
        dest.writeParcelable(accountDetails, flags);
    }
}
