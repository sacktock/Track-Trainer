package alexw.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexw.testlogin.R;

import java.util.ArrayList;
import java.util.Arrays;

import alexw.classes.Training;

/**
 * Created by alexw on 5/16/2017.
 */

public class TrainingAdapter {

    public static void setImageView(ImageView view, Completed_Training.Difficulty difficulty){
        //Sets a given image view to the corresponding icon given a difficulty
        if (difficulty == Completed_Training.Difficulty.Easy){
            view.setImageResource(R.drawable.ic_easy);
        }
        if (difficulty == Completed_Training.Difficulty.Good){
            view.setImageResource(R.drawable.ic_good);
        }
        if (difficulty == Completed_Training.Difficulty.Challenging){
            view.setImageResource(R.drawable.ic_challenging);
        }
        if (difficulty == Completed_Training.Difficulty.Hard){
            view.setImageResource(R.drawable.ic_hard);
        }
    }

    public static Training.TrainingType getTypeFromString(String type) {
        //Returns a TrainingType enumeration given a string
        if (type.equals("Long Run") ) {return Training.TrainingType.LongRun;}
        if (type.equals("Fartlek")) {return Training.TrainingType.Fartlek;}
        if (type.equals("Track")) {return Training.TrainingType.Track;}
        if (type.equals("Tempo Run") ) {return Training.TrainingType.TempoRun;}
        if (type.equals("Hill Reps")) {return Training.TrainingType.HillReps;}
        if (type.equals("Progression Run")) {return Training.TrainingType.ProgressionRun;}
        if (type.equals("Recovery Run")) {return Training.TrainingType.RecoveryRun;}
        return Training.TrainingType.None;
    }

    public static String getStringFromType(Training.TrainingType type){
        //Returns a string given a TrainingType enumeration
        if (type == Training.TrainingType.LongRun) {return "Long Run";}
        if (type == Training.TrainingType.Fartlek) {return "Fartlek";}
        if (type == Training.TrainingType.Track) {return type.toString();}
        if (type == Training.TrainingType.TempoRun) {return "Tempo Run";}
        if (type == Training.TrainingType.HillReps) {return "Hill Reps";}
        if (type == Training.TrainingType.ProgressionRun) {return "Progression Run";}
        if (type == Training.TrainingType.RecoveryRun) {return "Recovery Run";}
        return "None";
    }

    public static Completed_Training.Difficulty getDifficultyFromString(String string){
        //Returns a Difficulty enumeration given a string
        switch(string){
            case "Easy":
                return Completed_Training.Difficulty.Easy;
            case "Good":
                return Completed_Training.Difficulty.Good;
            case "Challenging":
                return Completed_Training.Difficulty.Challenging;
            case "Hard":
                return Completed_Training.Difficulty.Hard;
            default:
                return Completed_Training.Difficulty.Easy;
        }
    }

    private static Training_Set getTrainingSet(String string){
        //Returns a training set given a string description in the valid format
        int tempReps = 0;
        int tempDistance = 0;
        int tempTime = 0;
        int tempRest = 0;
        int lastChar = 0;

        char[] charArray = string.toCharArray();
        for (int i = 0; i < charArray.length; i = i +1) {
            if ((charArray[i] == 'x') && tempReps ==0){
               tempReps = Integer.parseInt((string.substring(lastChar, i)));
                lastChar = i +1;
            }
            if ((charArray[i] == 'm') && tempDistance == 0){
                tempDistance = Integer.parseInt(string.substring(lastChar, i));
                lastChar = i +8;
            }
            if (charArray[i] == ','){
                tempTime = (RaceTime.simpleStringToSeconds(string.substring(lastChar, i)));
                lastChar = i + 8;
                tempRest = (RaceTime.simpleStringToSeconds(string.substring(lastChar)));
                break;
            }
        }
        return new Training_Set(tempDistance, tempReps, tempTime, tempRest);
    }

    public static void getTrainingSetsFromString(String string, ArrayList<Object> objects){
        //Sets an array list of objects to instances of training sets
        //Given a string description of a training in the valid format
        int lastChar = 0;

        for (int i = 0; i < string.length(); i = i+1) {
           if (string.charAt(i) == '.'){
               objects.add(getTrainingSet(string.substring(lastChar, i)));
               lastChar = i +2;
           }
        }
   }

   public static int getTotalDistanceFromString(String string){
       //Returns the total distance of a training from a string description in the valid format
       ArrayList<Object> list = new ArrayList<>();
       getTrainingSetsFromString(string, list);
       int distance =0;
       for (int i = 0; i < list.size() ; i++) {
           if (list.get(i) instanceof Training_Set){
               Training_Set set = (Training_Set) list.get(i);
               distance = distance + set.getTotalDistance();
           }
       }
       return distance;
   }

   public static int getTotalTimeFromString(String string){
       //Returns the total time of a training from a string description in the valid format
       ArrayList<Object> list = new ArrayList<>();
       getTrainingSetsFromString(string, list);
       int time = 0;
       for (int i = 0; i < list.size() ; i++) {
           if (list.get(i) instanceof Training_Set){
               Training_Set set = (Training_Set) list.get(i);
               time = time + set.getTotalTime();
           }
       }
       return time;
   }

   public static Date getOldestTraining(ArrayList<Object> list){
       //Returns the date of the oldest training in an array list of instances of Completed_Trainings
       Completed_Training training;
       Date date = new Date(StringManipulation.getTodayDate());
       for (int i = 0; i < list.size() ; i++) {
           if (list.get(i) instanceof Completed_Training){
               training = (Completed_Training) list.get(i);
               if (date.moreRecentDate(new Date(training.getDate()))){
                   date = new Date(training.getDate());
               }
           }
       }
       return date;
   }

    public static void sortRunActivity(ArrayList<Object> list){
        //Merge sorts instances of run_activities based on the date they were completed
        //Newest first, oldest last
        class Mergesort {
            private run_Activity[] activities;
            private run_Activity[] helper;

            private int number;

            public void sort(run_Activity[] values) {
                this.activities = values;
                number = values.length;
                this.helper = new run_Activity[number];
                mergesort(0, number - 1);
            }

            private void mergesort(int low, int high) {
                // check if low is smaller than high, if not then the array is sorted
                if (low < high) {
                    // Get the index of the element which is in the middle
                    int middle = low + (high - low) / 2;
                    // Sort the left side of the array
                    mergesort(low, middle);
                    // Sort the right side of the array
                    mergesort(middle + 1, high);
                    // Combine them both
                    merge(low, middle, high);
                }
            }

            private void merge(int low, int middle, int high) {
                // Copy both parts into the helper array
                for (int i = low; i <= high; i++) {
                    helper[i] = activities[i];
                }

                int i = low;
                int j = middle + 1;
                int k = low;
                // Copy the smallest values from either the left or the right side back
                // to the original array
                while (i <= middle && j <= high) {
                    if ((new Date(helper[i].getDate())).moreRecentDate(new Date(helper[j].getDate()))) {
                        activities[k] = helper[i];
                        i++;
                    } else {
                        activities[k] = helper[j];
                        j++;
                    }
                    k++;
                }
                // Copy the rest of the left side of the array into the target array
                while (i <= middle) {
                    activities[k] = helper[i];
                    k++;
                    i++;
                }
                // Since we are sorting in-place any leftover elements from the right side
                // are already at the right position.
            }
        }

        run_Activity[] run = new run_Activity[list.size()];

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof  run_Activity){
                run[i] = (run_Activity) list.get(i);
            }
        }

        Mergesort mergesort = new Mergesort();
        mergesort.sort(run);

        list.clear();
        list.addAll(Arrays.asList(run));
    }

    public static String getDisplayDescription(ArrayList<Object> list){
        //Returns the description of a training to be displayed form an
        //Array list of instances of training sets
        String string = "";

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof Training_Set){
                Training_Set set = (Training_Set) list.get(i);
                if (set.getReps() ==1){
                    //If only 1 rep in a training set
                    //pace is used instead of the standard description
                    string = string + set.getDistance() + " Pace: " + set.getPace();
                } else {
                    string = string + set.getRepDesc();
                }
                string = string + ". ";
            }
        }
        return string;
    }

    public static String getTrainingSetDescription(ArrayList<Object> list){
        //Returns the actual description of a training
        //From an array list of instances of training sets
        String string = "";

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof Training_Set){
                Training_Set set = (Training_Set) list.get(i);
                    string = string + set.getRepDesc();
                string = string + ". ";
            }
        }
        return string;
    }
}
