package alexw.classes;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by alexw on 7/12/2017.
 */

public class Account_Details implements Parcelable{

    private String accountID;
    private String firstName;
    private String lastName;
    private String eMail;
    private String birthDate;
    private String gender;
    private String raceDistance;
    private String password;
    private String username;
    //Private attributes of the account details entity

    protected Account_Details(Parcel in) {
        //Constructor for instantiating object when it has been sent from another activity
        accountID = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        eMail = in.readString();
        birthDate = in.readString();
        gender = in.readString();
        raceDistance = in.readString();
        password = in.readString();
        username = in.readString();
    }

    public void setPassword(String string){
        password = string;
    }
    public void setAccountID(String num){
        accountID = num;
    }

    public static final Creator<Account_Details> CREATOR = new Creator<Account_Details>() {
        @Override
        public Account_Details createFromParcel(Parcel in) {
            return new Account_Details(in);
        }

        @Override
        public Account_Details[] newArray(int size) {
            return new Account_Details[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //Creates a parcel containing objects values to be sent to another activity
        dest.writeString(accountID);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(eMail);
        dest.writeString(birthDate);
        dest.writeString(gender);
        dest.writeString(raceDistance);
        dest.writeString(password);
        dest.writeString(username);
    }

    public String getAccountID() {return accountID;}
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {return lastName;}
    public String getEMail() {return eMail;}
    public String getBirthDate() {return birthDate;}
    public String getGender() {
        if (Objects.equals(gender, "None")){
            return "";
        }
        return gender;
    }
    public String getRaceDistance() {return raceDistance;}
    public String getPassword() {return password;}
    public String getUsername() {return username;}

    public Account_Details(String accountID, String firstName, String lastName, String eMail, String birthDate,
                           String gender,  String username, String password, String raceDistance){
        //Constructor
       this.accountID = accountID;
       this.firstName = firstName;
       this.lastName = lastName;
       this.eMail = eMail;
       this.birthDate = birthDate;
       this.gender = gender;
       this.raceDistance = raceDistance;
        this.password = password;
        this.username = username;
    }

    public Account_Details(){
        //Empty constructor
    }
}
