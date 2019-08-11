package com.example.alexw.testlogin;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.Objects;

import alexw.classes.Active_Training;
import alexw.classes.AddTrainingSet;
import alexw.classes.Completed_Training;
import alexw.classes.PersonalBest;
import alexw.classes.Race;
import alexw.classes.StringManipulation;
import alexw.classes.Training;
import alexw.classes.TrainingAdapter;

/**
 * Created by alexw on 9/9/2017.
 */

public class EditActivity extends AddActivity {

    Object myObject;
    String tabHostTag;
    String ObjectID;
    String personalBestPrediction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myObject = getIntent().getExtras().getParcelable("Object");
        super.button.setText("Done");
        setUpObjectDetails();
        setUpDialogs();
        setUpEditButton();
    }

    private void setUpEditButton() {
        super.button.setOnClickListener(null);
        super.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFilled()) {
                    final Dialog dialog = savingDialog();
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Intent intent = new Intent(EditActivity.this, MainPage.class);
                            intent.putExtra("AccountDetails", accountDetails);
                            startActivity(intent);
                        }
                    });
                    dialog.show();
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            switch (tabHost.getCurrentTabTag()) {
                                case "Training":
                                    dbHandler.updateTraining(new Completed_Training(ObjectID, accountDetails, trainingName.getText().toString(),
                                            trainingDate.getText().toString(), trainingSets.toArray(), TrainingAdapter.getStringFromType(trainingType),
                                            difficultyChooser.getDifficulty().toString()));
                                    statsUpdater.update();
                                    break;
                                case "Race":
                                    dbHandler.updateRace(new Race(ObjectID, accountDetails, raceName.getText().toString(), raceDate.getText().toString(),
                                            "", raceTime.getText().toString(), raceSpinnerDistance.getSelectedItem().toString()));
                                    break;
                                case "Personal Best":
                                    dbHandler.updatePersonalBest(new PersonalBest(ObjectID, accountDetails, personalBestDistance.getText().toString(),
                                            personalBestTime.getText().toString(), personalBestDate.getText().toString(), personalBestPrediction));
                                    break;
                            }
                            dialog.dismiss();
                        }
                    };
                    thread.start();
                } else {
                    sayToast("Entry Fields Not Filled");
                }
            }
        });
    }

    private void setUpObjectDetails() {
        if (myObject instanceof Completed_Training) {
            super.getSupportActionBar().setTitle("Edit Training");
            Completed_Training training = (Completed_Training) myObject;
            tabHostTag = "Training";
            this.tabHost.setCurrentTabByTag(tabHostTag);

            //Setting details
            this.trainingSpinnerType.setSelection(StringManipulation.getPosition
                    (getResources().getStringArray(R.array.activity_types), training.getTrainingType()));
            this.trainingName.setText(training.getName());
            this.trainingDate.setText(training.getDateCompleted());
            trainingSets.clear();
            TrainingAdapter.getTrainingSetsFromString(training.getDescription(), this.trainingSets);
            this.difficultyChooser.setDifficulty(TrainingAdapter.getDifficultyFromString(training.getDifficulty()));
            trainingSets.add(new AddTrainingSet());
            trainingAdapter.notifyDataSetChanged();
            ObjectID = training.getID();
        } else if (myObject instanceof Race) {
            super.getSupportActionBar().setTitle("Edit Race");
            Race race = (Race) myObject;
            tabHostTag = "Race";
            this.tabHost.setCurrentTabByTag(tabHostTag);

            //Setting details
            this.raceSpinnerDistance.setSelection(StringManipulation.getPosition
                    (getResources().getStringArray(R.array.distance_array), race.getDistance()));

            this.raceName.setText(race.getName());
            this.raceDate.setText(race.getDate());
            this.raceTime.setText(race.getPerformance());
            ObjectID = race.getID();
        } else if (myObject instanceof PersonalBest) {
            super.getSupportActionBar().setTitle("Edit Personal Best");
            PersonalBest personalBest = (PersonalBest) myObject;
            tabHostTag = "Personal Best";
            this.tabHost.setCurrentTabByTag(tabHostTag);

            //Setting details
            this.personalBestTime.setText(personalBest.getPerformance());
            this.personalBestDistance.setText(personalBest.getDistance());
            this.personalBestDate.setText(personalBest.getDate());
            this.personalBestPrediction = personalBest.getPrediction();
            ObjectID = personalBest.getID();
        }
    }

    @Override
    protected void tabHostChange() {
        if (!(tabHost.getCurrentTabTag() == tabHostTag)) {
            tabHost.setCurrentTabByTag(tabHostTag);
        }
    }

    @Override
    protected Dialog discardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Save Changes?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        button.callOnClick();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(EditActivity.this, MainPage.class);
                        intent.putExtra("AccountDetails", accountDetails);
                        startActivity(intent);
                    }
                });
        return builder.create();
    }

    @Override
    protected boolean entriesChanged() {
        //Check if entries have been changed
        if (myObject instanceof Completed_Training) {
            Completed_Training training = (Completed_Training) myObject;
            if (Objects.equals(trainingName.getText().toString(), training.getName())) {
                if (Objects.equals(trainingDate.getText().toString(), training.getDate())) {
                    if (Objects.equals(TrainingAdapter.getTrainingSetDescription(trainingSets), training.getDescription())) {
                        if (trainingType == TrainingAdapter.getTypeFromString(training.getTrainingType())) {
                            if (difficultyChooser.getDifficulty() == TrainingAdapter.getDifficultyFromString(training.getDifficulty())) {
                                return false;
                            }
                        }
                    }
                }
            }
        } else if (myObject instanceof Race) {
            Race race = (Race) myObject;
            if (Objects.equals(raceName.getText().toString(), race.getName())) {
                if (Objects.equals(raceDate.getText().toString(), race.getDate())) {
                    if (Objects.equals(raceTime.getText().toString(), race.getPerformance())) {
                        if (raceSpinnerDistance.getSelectedItemPosition() ==
                                StringManipulation.getPosition(getResources().getStringArray(R.array.distance_array), race.getDistance())) {
                            return false;
                        }
                    }
                }
            }
        } else if (myObject instanceof PersonalBest) {
            PersonalBest personalBest = (PersonalBest) myObject;
            if (Objects.equals(personalBestDate.getText().toString(), personalBest.getDate())) {
                if (Objects.equals(personalBestDistance.getText().toString(), personalBest.getDistance())) {
                    if (Objects.equals(personalBestTime.getText().toString(), personalBest.getPerformance())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
