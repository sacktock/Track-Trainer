package alexw.classes;

import android.content.Context;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by alexw on 10/22/2017.
 */

public class StatsUpdater {

    private Account_Details accountDetails;
    private DbHandler dbHandler;
    private Account_Stats accountStats;

    public StatsUpdater(Account_Details accountDetails, Context context){
        this.accountDetails = accountDetails;
        this.dbHandler = new DbHandler(context);
    }

    public void update(){
        accountStats = dbHandler.getAccountStats(accountDetails);
        //Set multiplier to 0
        double multiplier = 0;
        //Get trainings from the last 7 days
        ArrayList<Object> trainings = dbHandler.getPastWeekTrainings(accountDetails);

        int total = trainings.size();
        //Array will store the average of all the calculated stats for each training in the past week
        double stats[] = new double[4];

        for (int i = 0; i < total; i++) {
            if (trainings.get(i) instanceof Completed_Training){
                Completed_Training training = (Completed_Training) trainings.get(i);
                //Calculated stats are saved in global accountStats variable
                accountStats = dbHandler.getAccountStats(accountDetails);
                passTraining(training);
                //Add up the average for each statistic
                stats[0] = stats[0] + accountStats.getV4Speed()/total;
                stats[1] = stats[1] + accountStats.getLactateThreshold()/total;
                stats[2] = stats[2] + accountStats.getVO2Max()/total;
                stats[3] = stats[3] + accountStats.getRecoveryRate()/total;
                //Add to the multiplier based on how difficult the trainings have been
                if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Challenging.toString())
                        || Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Hard.toString())) {
                    multiplier = multiplier + 0.3;
                } else {
                    multiplier = multiplier + 0.1;
                }
            }
        }

        //Get number of trainings in the past month
        int pastMonthActivity = dbHandler.getPastMonthTrainings(accountDetails).size();

        //Check to see if training has been consistent the past month
        double check = ((pastMonthActivity*7)/30.42) *0.3;

        //If multiplier is larger then this week has been an anomaly
        //Reduce multiplier to the check value
        if (multiplier > check){
            multiplier = check;
        }

        if (trainings.size()>0){
            //Checks if trainings in the past week exist
            Account_Stats newStats = new Account_Stats(accountStats.getID(), multiplier,
                    stats[0],stats[1],stats[2],stats[3]);
            dbHandler.updateStats(newStats);
        } else {
            accountStats.setMultiplier(multiplier);
            dbHandler.updateStats(accountStats);
        }
    }

    private void passTraining(Completed_Training training){
        //Code to update account stats based on difficulty of training
        AthleteLactateModel model = dbHandler.getLactateModel(accountDetails);
        ArrayList<Object> trainingSets = new ArrayList<>();
        TrainingAdapter.getTrainingSetsFromString(training.getDescription(), trainingSets);

        double estimatedLactate = 0;

        //Switch statement estimates the lactate level of the athlete during the training
        //based on how difficult they found it and how difficult it should've been
        switch (TrainingAdapter.getTypeFromString(training.getTrainingType())){
            case Track:
                if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Easy.toString())){
                    estimatedLactate = 6;
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Good.toString())){
                    estimatedLactate = 8;
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Challenging.toString())){
                    estimatedLactate = 11;
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Hard.toString())){
                    estimatedLactate = 13;
                }
            case Fartlek:
                if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Easy.toString())){
                    estimatedLactate = 2.5;
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Good.toString())){
                    //Empty do nothing
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Challenging.toString())){
                    estimatedLactate = 4;
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Hard.toString())){
                    estimatedLactate = 5;
                }
            case LongRun:
                if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Easy.toString())){
                    estimatedLactate = 2.5;
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Good.toString())){
                    //Empty do nothing
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Challenging.toString())){
                    estimatedLactate = 4;
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Hard.toString())){
                    estimatedLactate = 5;
                }
            case TempoRun:
                if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Easy.toString())){
                    estimatedLactate = 3.5;
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Good.toString())){
                    estimatedLactate = 4.5;
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Challenging.toString())){
                    //Empty do nothing
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Hard.toString())){
                    estimatedLactate = 6;
                }
            case ProgressionRun:
                if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Easy.toString())){
                    estimatedLactate = 3.5;
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Good.toString())){
                    //Empty do nothing
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Challenging.toString())){
                    estimatedLactate = 4.5;
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Hard.toString())){
                    estimatedLactate = 5.5;
                }
            case HillReps:
                if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Easy.toString())){
                    estimatedLactate = 5;
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Good.toString())){
                    estimatedLactate = 7;
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Challenging.toString())){
                    estimatedLactate = 9;
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Hard.toString())){
                    estimatedLactate = 11;
                }
            case RecoveryRun:
                if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Easy.toString())){
                    //Empty do nothing
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Good.toString())){
                    //Empty do nothing
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Challenging.toString())){
                    estimatedLactate = 3;
                } else if (Objects.equals(training.getDifficulty(), Completed_Training.Difficulty.Hard.toString())){
                    estimatedLactate = 3.5;
                }
            default:
                //Empty do nothing
        }
        if (!(estimatedLactate == 0)){
            //If estimated lactate is 0 then the training does not change the account stats
            accountStats = model.optimiseAccountStats(trainingSets, accountStats, estimatedLactate);
        }
    }
}

