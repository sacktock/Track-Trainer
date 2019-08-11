package com.example.alexw.testlogin;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Stack;

import alexw.classes.Account_Details;
import alexw.classes.Account_Stats;
import alexw.classes.Active_Training;
import alexw.classes.AthleteLactateModel;
import alexw.classes.DbHandler;
import alexw.classes.DiscreteDistribution;
import alexw.classes.MathFunc;
import alexw.classes.RaceTime;
import alexw.classes.RecyclerViewAdapter;
import alexw.classes.StringManipulation;
import alexw.classes.Training;
import alexw.classes.TrainingAdapter;
import alexw.classes.Training_Set;
import alexw.classes.VerticalSpaceItemDecoration;

public class TrainingGenerator extends AppCompatActivity {

    ArrayList<Object> trainingSets = new ArrayList<Object>();
    Training.TrainingType trainingType;
    EditText editText;
    TextView totalTime;
    TextView totalDistance;
    Button button;
    Account_Details accountDetails;
    Stack<ArrayList<Object>> trainingStack = new Stack<ArrayList<Object>>();
    RecyclerViewAdapter recylerViewAdapter;
    DbHandler dbHandler = new DbHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_generator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Training Generator");
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0f);

        Thread thread = new Thread() {
            @Override
            public void run() {
                //Thread to set up activity
                Looper.prepare();
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                setUpActionButton();
                accountDetails = getIntent().getExtras().getParcelable("AccountDetails");
                fillArrayList();
                setUpRecyclerView();
                setUpSpinner();
                setUpTextView();
                setUpButtons();
                hideKeyboard();
                if (!dbHandler.spaceForActiveTraining(accountDetails)){
                    warningDialog().show();
                }
                Looper.loop();
            }
        };
        thread.start();
    }

    public void hideKeyboard() {
        if (!(getCurrentFocus() == null)) {
            getCurrentFocus().clearFocus();
        }
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private Dialog confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are your sure you want to use this training")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHandler.insertActiveTraining(new Active_Training("", accountDetails,
                                editText.getText().toString(), StringManipulation.getTodayDate(), trainingSets.toArray(),
                                TrainingAdapter.getStringFromType(trainingType)));
                        Intent intent = new Intent(TrainingGenerator.this, MainPage.class);
                        intent.putExtra("AccountDetails", accountDetails);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Empty
                    }
                });
        return builder.create();
    }

    private Dialog confirmReturnDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to leave")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TrainingGenerator.this, MainPage.class);
                        intent.putExtra("AccountDetails", accountDetails);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //empty
                    }
                });
        return builder.create();
    }

    private void setUpTextView() {
        editText = (EditText) findViewById(R.id.training_generator_name);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonAlpha();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        totalDistance = (TextView) findViewById(R.id.training_generator_total_distance);
        totalDistance.setText("Total Distance: 0m");
        totalTime = (TextView) findViewById(R.id.training_generator_total_time);
        totalTime.setText("Total Time: 0");
    }

    private void generateTraining() {
        final Dialog dialog = generatingDialog();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                recylerViewAdapter.setCollection(trainingSets);
                buttonAlpha();
                setTextViewValues();
            }
        });
        dialog.show();

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(100);
                    trainingSets.clear();
                    //Code to generate training
                    DistributionGenerator generator = new DistributionGenerator(trainingType, accountDetails);
                    TrainingOptimizer optimizer = new TrainingOptimizer(generator.generateTraining(), trainingType);
                    trainingSets = optimizer.getOptimizedTraining();
                    dialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private void setUpButtons() {
        Button next = (Button) findViewById(R.id.training_generator_next);
        Button previous = (Button) findViewById(R.id.training_generator_previous);
        ImageView info = findViewById(R.id.training_generator_info);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trainingType == Training.TrainingType.None) {
                    selectTypeDialog().show();
                } else {
                    //Push current training onto the stack to generate next training
                    ArrayList<Object> temp = (ArrayList<Object>) trainingSets.clone();
                    trainingStack.push(temp);
                    generateTraining();
                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(trainingStack.isEmpty())) {
                    //Pop previously generated training off the stack
                    trainingSets = trainingStack.pop();
                    recylerViewAdapter.setCollection(trainingSets);
                    setTextViewValues();
                }
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoDialog().show();
            }
        });
    }

    private Dialog infoDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Account_Stats stats = dbHandler.getAccountStats(accountDetails);
        builder.setMessage("Track Trainer will assume you take a 5 minute rest in-between each training set, this allows your heart-rate to return to normal");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
            }
        });
        return builder.create();
    }

    private void setTextViewValues() {
        int totalTime = 0;
        int totalDistance = 0;
        for (int i = 0; i < trainingSets.size(); i = i + 1) {
            if (trainingSets.get(i) instanceof Training_Set) {
                Training_Set set = (Training_Set) trainingSets.get(i);
                totalDistance = totalDistance + set.getTotalDistance();
                totalTime = totalTime + set.getTotalTime();
            }
        }
        totalTime = totalTime + 300 * (trainingSets.size() - 1);
        this.totalDistance.setText("Total Distance: " + totalDistance + "m");
        this.totalTime.setText("Total Time: " + new RaceTime(totalTime).getTime(true, false));
    }

    private void setUpSpinner() {
        //Setup training type spinner
        final Spinner spinner = (Spinner) findViewById(R.id.training_generator_type);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.activity_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] strings = getResources().getStringArray(R.array.activity_types);
                trainingType = TrainingAdapter.getTypeFromString(strings[position]);
                buttonAlpha();
                trainingSets.clear();
                recylerViewAdapter.setCollection(trainingSets);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setSelection(0);
            }
        });
    }

    private void fillArrayList() {
        //Empty
    }

    @Override
    public void onBackPressed() {
        if (fieldsEmpty()) {
            Intent intent = new Intent(TrainingGenerator.this, MainPage.class);
            intent.putExtra("AccountDetails", accountDetails);
            startActivity(intent);
        } else {
            confirmReturnDialog().show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (fieldsEmpty()) {
                Intent intent = new Intent(TrainingGenerator.this, MainPage.class);
                intent.putExtra("AccountDetails", accountDetails);
                startActivity(intent);
            } else {
                confirmReturnDialog().show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setUpActionButton() {
        button = (Button) findViewById(R.id.addCmd);
        button.setAlpha(0.5f);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFilled()) {
                    if (dbHandler.spaceForActiveTraining(accountDetails)) {
                        confirmDialog().show();
                    } else{
                        warningDialog().show();
                    }
                }
            }

        });
    }

    private Dialog warningDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning")
               .setMessage("You cannot have more than 5 Active Trainings at any one time")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                });
        return builder.create();
    }

    private void setUpRecyclerView() {
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.sets_recyclerview);
        recylerViewAdapter = new RecyclerViewAdapter(this, trainingSets, dbHandler);
        recyclerView.setAdapter(recylerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(16, 16));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        recylerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    //Suppress long click listener by setting view holder enabled to false
                    recyclerView.getChildAt(i).setEnabled(false);
                }
            }
        });
    }


    private boolean trainingGenerated() {
        //Checking training has been generated and is not null
        return !trainingSets.isEmpty();
    }

    private boolean isFilled() {
        StringManipulation sm = new StringManipulation();
        if (sm.isNoNothing(editText.getText().toString()) &&
                !(trainingType == Training.TrainingType.None) && trainingGenerated()) {
            return true;
        } else {
            return false;
        }
    }

    private void buttonAlpha() {
        if (isFilled()) {
            button.setAlpha(1.0f);
            button.setClickable(true);
        } else {
            button.setAlpha(0.5f);
            button.setClickable(false);
        }
    }

    private Dialog selectTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please select a Training Type")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

    private Dialog generatingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_saving, null);
        TextView title = (TextView) view.findViewById(R.id.saving_title);
        title.setText("Generating");
        builder.setView(view);
        builder.setCancelable(false);
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private boolean fieldsEmpty() {
        if (editText.getText().toString().equals("") && trainingType == Training.TrainingType.None
                && !trainingGenerated()) {
            return true;
        } else {
            return false;
        }
    }

    private class DistributionGenerator {

        DiscreteDistribution distanceDistribution;
        Training.TrainingType type;
        double[][][] repDistribution;
        double[][] restDistribution;
        int reps;
        int sets;

        DistributionGenerator(Training.TrainingType type, Account_Details details) {
            this.type = type;
            //Switch statement gets corresponding distance distribution based on training type
            //Distance distribution is personalised to the account based on their history
            switch (type) {
                case Track:
                    repDistribution = dbHandler.getDistanceRepsDistribution(accountDetails, getResources().getStringArray(R.array.set_distance_array));
                    //Code to get personalized distributions for sets and distances from database
                    restDistribution = dbHandler.getRestDistanceDistribution(details, getResources().getStringArray(R.array.set_distance_array),
                            getResources().getStringArray(R.array.set_rest_array));
                    distanceDistribution = dbHandler.getTrainingTypeDistribution(details, type,
                            getResources().getStringArray(R.array.set_distance_array));
                    sets = (dbHandler.getTrackSetsDistribution(details)).getX();
                    break;
                case Fartlek:
                    distanceDistribution = dbHandler.getTrainingTypeDistribution(details, type,
                            getResources().getStringArray(R.array.set_distance_array));
                    sets = 1;
                    reps = 1;
                    break;
                case RecoveryRun:
                    distanceDistribution = dbHandler.getTrainingTypeDistribution(details, type,
                            getResources().getStringArray(R.array.set_distance_array));
                    sets = 1;
                    reps = 1;
                    break;
                case LongRun:
                    distanceDistribution = dbHandler.getTrainingTypeDistribution(details, type,
                            getResources().getStringArray(R.array.set_distance_array));
                    sets = 1;
                    reps = 1;
                    break;
                case TempoRun:
                    distanceDistribution = dbHandler.getTrainingTypeDistribution(details, type,
                            getResources().getStringArray(R.array.set_distance_array));
                    sets = 1;
                    reps = 1;
                    break;
                case ProgressionRun:
                    distanceDistribution = dbHandler.getTrainingTypeDistribution(details, type,
                            getResources().getStringArray(R.array.set_distance_array));
                    sets = MathFunc.getRandomNumber(2, 3);
                    reps = 1;
                    break;
                case HillReps:
                    distanceDistribution = dbHandler.getTrainingTypeDistribution(details, type,
                            getResources().getStringArray(R.array.set_distance_array));
                    sets = 1;
                    reps = MathFunc.getRandomNumber(10, 20);
                    break;
                default:
                    distanceDistribution = dbHandler.getTrainingTypeDistribution(details, type,
                            getResources().getStringArray(R.array.set_distance_array));
                    reps = 1;
                    sets = 1;
            }
        }

        private ArrayList<Object> generateTraining() {
            ArrayList<Object> out = new ArrayList<Object>();
            for (int i = 0; i < this.sets; i++) {
                //Generates a training set for each number of sets
                int rest = 0;
                int d = distanceDistribution.getX();
                //Gets the index of the distance in the distance array from getX() function
                int distance = StringManipulation.getDistanceInt(getResources().getStringArray(R.array.set_distance_array)[d]);
                if (type == Training.TrainingType.Track) {
                    //Create rep distribution for corresponding distance
                    DiscreteDistribution repsDistribution = new DiscreteDistribution(new int[]{1, 2, 3, 4, 5, 6, 7,
                            8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30},
                            repDistribution[sets - 1][d]);
                    reps = repsDistribution.getX();

                    //Create rest distribution for corresponding distance
                    DiscreteDistribution restDistribution = new DiscreteDistribution(new int[]{0, 1, 2, 3, 4, 5, 6, 7,
                            8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20}, this.restDistribution[d]);
                    rest = RaceTime.simpleStringToSeconds(getResources().getStringArray(R.array.set_rest_array)[restDistribution.getX()]);

                }
                //Write training set to array list
                out.add(new Training_Set(distance, reps, 0, rest));
            }
            return out;
        }
    }

    private class TrainingOptimizer {

        AthleteLactateModel model;
        ArrayList<Object> sets;
        double lactateBracket;

        TrainingOptimizer(ArrayList<Object> sets, Training.TrainingType type) {
            this.sets = sets;
            //Switch statement gets intended difficulty or lactate level given the training type
            switch (type) {
                case Track:
                    lactateBracket = 10;
                    break;
                case Fartlek:
                    lactateBracket = 3;
                    break;
                case RecoveryRun:
                    lactateBracket = 2;
                    break;
                case LongRun:
                    lactateBracket = 3;
                    break;
                case TempoRun:
                    lactateBracket = 5;
                    break;
                case ProgressionRun:
                    lactateBracket = 4;
                    break;
                case HillReps:
                    lactateBracket = 8;
                    break;
                default:
                    lactateBracket = dbHandler.getAccountStats(accountDetails).getLactateThreshold();
            }
            model = dbHandler.getLactateModel(accountDetails);
        }

        private void optimizeTraining() {
            //Method optimises an incomplete training program by setting
            //the speed at which each distance is run
            Training_Set set;
            ArrayList<Object> solution = new ArrayList<Object>();
            for (int i = 0; i < sets.size(); i++) {
                if (sets.get(i) instanceof Training_Set) {
                    set = (Training_Set) sets.get(i);
                    //For loop to inspect and optimise each training set
                    for (int j = 10; j < 100; j++) {
                        //For loop varies speed from 10s per 100m to 100s per 100m
                        set.setDistanceTime(j * (set.getDistance() / 100));
                        solution.add(set);
                        if (model.avgLactateForTraining(solution) <= lactateBracket) {
                            //Training set is optimised so break loop
                            break;
                        } else {
                            //Training set is not optimised so remove
                            solution.remove(set);
                        }
                    }
                }
            }
            sets = solution;
        }

        public ArrayList<Object> getOptimizedTraining() {
            optimizeTraining();
            return sets;
        }
    }
}
