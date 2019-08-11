package alexw.classes;

import android.os.Parcelable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alexw on 7/12/2017.
 */

public class Active_Training extends Training{

    //Inherits from training class
    private int totalTime;
    private int totalDistance;
    //Additional private attributes for active training object

    public String getDateIssued(){return getDate();}
    public int getTotalTime() {
        return totalTime;
    }
    public int getTotalDistance() {
        return totalDistance;
    }

    private String makeDescription(Object[] sets){
        //Returns a description using an array of training sets
        String output = "";
        for (Object set1 : sets) {
            if (set1 instanceof Training_Set) {
                Training_Set set = (Training_Set) set1;
                output = output + set.getRepDesc() + ". ";
            }
        }
        return output;
    }

    public Active_Training(String ID, Account_Details accountDetails, String name, String dateIssued, Object[] sets,
                           String trainingType){
        //Constructor using an array of training sets
        super(false, "",name,dateIssued,ID, accountDetails, trainingType);
        this.description = makeDescription(sets);
        this.totalTime = makeTotalTime(sets);
        this.totalDistance = makeTotalDistance(sets);
    }

    private int makeTotalTime(Object[] sets){
        //Returns total time for the training using an array of training sets
        int output = 0;
        for (Object set1 : sets) {
            if (set1 instanceof Training_Set) {
                Training_Set set = (Training_Set) set1;
                output = output + set.getTotalTime();
            }
        }
        return output;
    }

    private int makeTotalDistance(Object[] sets){
        //Returns total distance for training using an array of training sets
        int output = 0;
        for (Object set1 : sets) {
            if (set1 instanceof Training_Set) {
                Training_Set set = (Training_Set) set1;
                output = output + set.getTotalDistance();
            }
        }
        return output;
    }

    public Active_Training(String ID, Account_Details accountDetails, String name, String dateIssued, String description,
                           String trainingType){
        //Constructor using description
        super(false, description,name,dateIssued, ID, accountDetails, trainingType);
        ArrayList<Object> list = new ArrayList<>();
        TrainingAdapter.getTrainingSetsFromString(description, list);
        makeTotalTime(list.toArray());
        makeTotalDistance(list.toArray());
    }

    public Completed_Training completeTraining(String userReview){
        //Method that creates a completed training object from its self
        return new Completed_Training("", accountDetails, name, StringManipulation.getTodayDate(), description, trainingType, userReview);

    }
}
