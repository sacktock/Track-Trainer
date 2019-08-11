package com.example.alexw.testlogin;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import alexw.classes.Account_Details;
import alexw.classes.Active_Training;
import alexw.classes.AddTrainingSet;
import alexw.classes.Completed_Training;
import alexw.classes.DbHandler;
import alexw.classes.DifficultyChooser;
import alexw.classes.PersonalBest;
import alexw.classes.Race;
import alexw.classes.RaceTime;
import alexw.classes.RecyclerViewAdapter;
import alexw.classes.StatsUpdater;
import alexw.classes.StringManipulation;
import alexw.classes.Training;
import alexw.classes.TrainingAdapter;
import alexw.classes.Training_Set;
import alexw.classes.VerticalSpaceItemDecoration;

public class AddActivity extends AppCompatActivity {

    Account_Details accountDetails;
    StatsUpdater statsUpdater;
    EditText trainingName;
    EditText trainingDate;
    EditText activeTrainingName;
    EditText raceName;
    EditText raceDate;
    EditText raceTime;
    EditText personalBestTime;
    EditText personalBestDate;
    EditText personalBestDistance;
    TextView activeTrainingTime;
    TextView activeTrainingDistance;
    Spinner trainingSpinnerType;
    Spinner raceSpinnerDistance;
    DifficultyChooser difficultyChooser;
    Button button;
    ArrayList<Object> trainingSets = new ArrayList<Object>();
    ArrayList<Object> activeTrainingSets = new ArrayList<Object>();
    Training.TrainingType trainingType;
    Training.TrainingType activeTrainingType;
    RecyclerViewAdapter trainingAdapter;
    RecyclerViewAdapter activeTrainingAdapter;
    TabHost tabHost;
    Dialog distanceSelectorDialog;
    Dialog daySelectorDialog;
    Dialog timeSelectorDialog;
    DbHandler dbHandler = new DbHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Activity");
        getSupportActionBar().setElevation(0f);
        setUpAddButton();
        accountDetails = getIntent().getExtras().getParcelable("AccountDetails");
        setUpSpinner();
        setUpTextView();
        setUpRecyclerView();
        setUpTabHost();
        setUpDialogs();
        setUpButton();
        buttonAlpha();
        setUpImageView();
        View current = getCurrentFocus();
        if (current != null) current.clearFocus();
        clearTrainingSets();
        clearActiveTrainingSets();
        statsUpdater = new StatsUpdater(accountDetails, this);
    }

    private void clearTrainingSets() {
        trainingSets.clear();
        trainingSets.add(new AddTrainingSet());
    }

    private void clearActiveTrainingSets() {
        activeTrainingSets.clear();
        activeTrainingSets.add(new AddTrainingSet());
    }

    private void setUpImageView() {
        ImageView easy = (ImageView) findViewById(R.id.add_ic_easy);
        ImageView good = (ImageView) findViewById(R.id.add_ic_good);
        ImageView challenging = (ImageView) findViewById(R.id.add_ic_challenging);
        ImageView hard = (ImageView) findViewById(R.id.add_ic_hard);

        difficultyChooser = new DifficultyChooser(easy, good, challenging, hard);
        difficultyChooser.setUpImageView();
    }

    private void setUpButton(){
        ImageView info = findViewById(R.id.add_training_info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoDialog().show();
            }
        });
    }

    private void setUpTabHost() {
        tabHost = (TabHost) findViewById(R.id.add_activity_tabhost);
        tabHost.setup();

        //tab1
        TabHost.TabSpec spec = tabHost.newTabSpec("Training");
        spec.setContent(R.id.tab1);
        spec.setIndicator(getTabView(R.drawable.training_focused));
        tabHost.addTab(spec);
        //tab2
        spec = tabHost.newTabSpec("Active Training");
        spec.setContent(R.id.tab2);
        spec.setIndicator(getTabView(R.drawable.active_training_focused));
        tabHost.addTab(spec);
        //tab3
        spec = tabHost.newTabSpec("Race");
        spec.setContent(R.id.tab3);
        spec.setIndicator(getTabView(R.drawable.ic_directions_run_black_24dp));
        tabHost.addTab(spec);
        //tab4
        spec = tabHost.newTabSpec("Personal Best");
        spec.setContent(R.id.tab4);
        spec.setIndicator(getTabView(R.drawable.personal_best_focused));
        tabHost.addTab(spec);

        //Setup tab host widget interface
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            ImageView iv = (ImageView) tabHost.getTabWidget().getChildAt(i).findViewById(R.id.tab_imageView);
            iv.getDrawable().clearColorFilter();
            iv.setAlpha(0.5f);
        }

        ImageView iv = (ImageView) tabHost.getCurrentTabView().findViewById(R.id.tab_imageView);
        iv.setColorFilter(R.color.colorSecondaryLight);
        iv.setAlpha(1f);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                tabHostChange();
                buttonAlpha();
                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                    ImageView iv = (ImageView) tabHost.getTabWidget().getChildAt(i).findViewById(R.id.tab_imageView);
                    iv.getDrawable().clearColorFilter();
                    iv.setAlpha(0.5f);
                }
                ImageView iv = (ImageView) tabHost.getCurrentTabView().findViewById(R.id.tab_imageView);
                iv.setColorFilter(R.color.colorSecondaryLight);
                iv.setAlpha(1f);
            }
        });
    }

    protected void tabHostChange() {
        //Called when tab is changed
        emptyEditText();
        clearActiveTrainingSets();
        clearTrainingSets();
    }

    private void emptyEditText() {
        trainingName.setText("");
        trainingDate.setText("");
        activeTrainingName.setText("");
        raceName.setText("");
        raceDate.setText("");
        raceTime.setText("");
        personalBestTime.setText("");
        personalBestDate.setText("");
        personalBestDistance.setText("");
        trainingSpinnerType.setSelection(0);
        raceSpinnerDistance.setSelection(0);
        difficultyChooser.setDifficulty(Completed_Training.Difficulty.Easy);
        trainingType = Training.TrainingType.None;
        activeTrainingType = Training.TrainingType.None;
    }

    protected boolean entriesChanged() {
        //Checks if any entreis have been changed
        switch (tabHost.getCurrentTabTag()) {
            case "Training":
                if (Objects.equals(trainingName.getText().toString(), "")) {
                    if (Objects.equals(trainingDate.getText().toString(), "")) {
                        if (trainingSets.size() == 1) {
                            if (trainingType == Training.TrainingType.None) {
                                if (difficultyChooser.getDifficulty() == Completed_Training.Difficulty.Easy) {
                                    return false;
                                }
                            }
                        }
                    }
                }
                return true;
            case "Active Training":
                if (Objects.equals(activeTrainingName.getText().toString(), "")) {
                    if (activeTrainingType == Training.TrainingType.None) {
                        if (activeTrainingSets.size() == 1) {
                            return false;
                        }
                    }
                }
                return true;
            case "Race":
                if (Objects.equals(raceName.getText().toString(), "")) {
                    if (Objects.equals(raceDate.getText().toString(), "")) {
                        if (Objects.equals(raceTime.getText().toString(), "")) {
                            if (raceSpinnerDistance.getSelectedItemPosition() == 0) {
                                return false;
                            }
                        }
                    }
                }
                return true;
            case "Personal Best":
                if (Objects.equals(personalBestDate.getText().toString(), "")) {
                    if (Objects.equals(personalBestDistance.getText().toString(), "")) {
                        if (Objects.equals(personalBestTime.getText().toString(), "")) {
                            return false;
                        }
                    }
                }
                return true;
        }
        return false;
    }

    private View getTabView(int resID) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.tab_imageView);
        imageView.setImageResource(resID);
        return view;
    }

    protected void setUpAddButton() {
        button = (Button) findViewById(R.id.addActivity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFilled()) {
                    final Dialog dialog = savingDialog();
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Intent intent = new Intent(AddActivity.this, MainPage.class);
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
                                    dbHandler.insertTraining(new Completed_Training("", accountDetails, trainingName.getText().toString(),
                                            trainingDate.getText().toString(), trainingSets.toArray(), TrainingAdapter.getStringFromType(trainingType),
                                            difficultyChooser.getDifficulty().toString()));
                                    statsUpdater.update();
                                    break;
                                case "Active Training":
                                    if (dbHandler.spaceForActiveTraining(accountDetails)){
                                        dbHandler.insertActiveTraining(new Active_Training("", accountDetails, activeTrainingName.getText().toString(),
                                                StringManipulation.getTodayDate(), activeTrainingSets.toArray(), TrainingAdapter.getStringFromType(activeTrainingType)));
                                    } else {
                                        sayToast("You cannot have more than 5 Active Trainings at any one time");
                                    }
                                    break;
                                case "Race":
                                    dbHandler.insertRace(new Race("", accountDetails, raceName.getText().toString(), raceDate.getText().toString(),
                                            "", raceTime.getText().toString(), raceSpinnerDistance.getSelectedItem().toString()));
                                    break;
                                case "Personal Best":
                                    dbHandler.insertPersonalBest(new PersonalBest("", accountDetails, personalBestDistance.getText().toString(),
                                            personalBestTime.getText().toString(), personalBestDate.getText().toString(), ""));
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

    private void setUpSpinner() {
        trainingSpinnerType = (Spinner) findViewById(R.id.add_activity_spinner_type);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.activity_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trainingSpinnerType.setAdapter(adapter);
        trainingSpinnerType.setSelection(0);
        trainingSpinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] strings = getResources().getStringArray(R.array.activity_types);
                trainingType = TrainingAdapter.getTypeFromString(strings[position]);
                buttonAlpha();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Empty
            }
        });

        raceSpinnerDistance = (Spinner) findViewById(R.id.add_race_distance);
        adapter = ArrayAdapter.createFromResource(this, R.array.distance_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        raceSpinnerDistance.setAdapter(adapter);
        raceSpinnerDistance.setSelection(StringManipulation.getPosition
                (getResources().getStringArray(R.array.distance_array), accountDetails.getRaceDistance()));

        raceSpinnerDistance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                buttonAlpha();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Empty
            }
        });

        //Setup active training type spinner
        Spinner spinner2 = (Spinner) findViewById(R.id.add_active_training_type);
        adapter = ArrayAdapter.createFromResource(this, R.array.activity_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter);
        spinner2.setSelection(0);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] strings = getResources().getStringArray(R.array.activity_types);
                activeTrainingType = TrainingAdapter.getTypeFromString(strings[position]);
                buttonAlpha();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    protected boolean isFilled() {
        switch (tabHost.getCurrentTabTag()) {
            case "Training":
                if (StringManipulation.isNoNothing(trainingName.getText().toString())) {
                    if (StringManipulation.isNoNothing(trainingDate.getText().toString())) {
                        if (!(trainingType == Training.TrainingType.None)) {
                            if (trainingSets.size() > 1) {
                                return true;
                            }
                        }
                    }
                }
                break;
            case "Active Training":
                if (StringManipulation.isNoNothing(activeTrainingName.getText().toString())) {
                    if (!(activeTrainingType == Training.TrainingType.None)) {
                        if (activeTrainingSets.size() > 1) {
                            return true;
                        }
                    }
                }
                break;
            case "Race":
                if (StringManipulation.isNoNothing(raceName.getText().toString())) {
                    if (StringManipulation.isNoNothing(raceDate.getText().toString())) {
                        if (StringManipulation.isNoNothing(raceTime.getText().toString())) {
                            return true;
                        }
                    }
                }
                break;
            case "Personal Best":
                if (StringManipulation.isNoNothing(personalBestDate.getText().toString())) {
                    if (StringManipulation.isNoNothing(personalBestDistance.getText().toString())) {
                        if (StringManipulation.isNoNothing(personalBestTime.getText().toString())) {
                            return true;
                        }
                    }
                }
                break;
            default:
                return false;
        }
        return false;
    }

    private void buttonAlpha() {
        if (isFilled()) {
            button.setAlpha(1f);
        } else {
            button.setAlpha(0.5f);
        }
    }

    private void setUpTextView() {
        trainingName = (EditText) findViewById(R.id.add_activity_name);
        trainingName.addTextChangedListener(new TextWatcher() {
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
        trainingDate = (EditText) findViewById(R.id.add_activity_date);
        trainingDate.addTextChangedListener(new TextWatcher() {
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
        trainingDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard();
                    daySelectorDialog.show();
                }
            }
        });
        activeTrainingName = (EditText) findViewById(R.id.add_active_training_name);
        activeTrainingName.addTextChangedListener(new TextWatcher() {
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
        raceName = (EditText) findViewById(R.id.add_race_name);
        raceName.addTextChangedListener(new TextWatcher() {
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
        raceDate = (EditText) findViewById(R.id.add_race_date);
        raceDate.addTextChangedListener(new TextWatcher() {
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
        raceDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard();
                    daySelectorDialog.show();
                }
            }
        });
        raceTime = (EditText) findViewById(R.id.add_race_time);
        raceTime.addTextChangedListener(new TextWatcher() {
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
        raceTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard();
                    timeSelectorDialog.show();
                }
            }
        });
        personalBestDistance = (EditText) findViewById(R.id.add_personal_best_distance);
        personalBestDistance.addTextChangedListener(new TextWatcher() {
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
        personalBestDistance.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard();
                    distanceSelectorDialog.show();
                }

            }
        });
        personalBestTime = (EditText) findViewById(R.id.add_personal_best_time);
        personalBestTime.addTextChangedListener(new TextWatcher() {
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
        personalBestTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard();
                    timeSelectorDialog.show();
                }

            }
        });
        personalBestDate = (EditText) findViewById(R.id.add_personal_best_date);
        personalBestDate.addTextChangedListener(new TextWatcher() {
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
        personalBestDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard();
                    daySelectorDialog.show();
                }
            }
        });

        activeTrainingDistance = (TextView) findViewById(R.id.add_training_total_distance);
        activeTrainingTime = (TextView) findViewById(R.id.add_training_total_time);
        setTextViewValues();
    }

    private void setTextViewValues() {
        int totalTime = 0;
        int totalDistance = 0;
        for (int i = 0; i < activeTrainingSets.size(); i = i + 1) {
            if (activeTrainingSets.get(i) instanceof Training_Set) {
                Training_Set set = (Training_Set) activeTrainingSets.get(i);
                totalDistance = totalDistance + set.getTotalDistance();
                totalTime = totalTime + set.getTotalTime();
            }
        }
        activeTrainingDistance.setText("Total Distance: " + Integer.toString(totalDistance) + "m");
        String string = new RaceTime(totalTime).getTime(true, false);
        if (string == "") {
            string = "0";
        }
        activeTrainingTime.setText("Total Time: " + string);
    }

    protected void setUpDialogs() {
        //Setup dialog to select date
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_day_selector, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        Button button = (Button) view.findViewById(R.id.day_picker_done);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = StringManipulation.makeDate(datePicker.getDayOfMonth(), datePicker.getMonth() + 1, datePicker.getYear());
                switch (tabHost.getCurrentTabTag()) {
                    case "Training":
                        if (StringManipulation.isThisDateValid(date, "dd/MM/yyyy")) {
                            trainingDate.setText(date);
                        } else {
                            sayToast("This date is in the future");
                        }
                        break;
                    case "Race":
                        if (StringManipulation.isThisDateValid(date, "dd/MM/yyyy")) {
                            raceDate.setText(date);
                        } else {
                            sayToast("This date is in the future");
                        }
                        break;
                    case "Personal Best":
                        if (StringManipulation.isThisDateValid(date, "dd/MM/yyyy")) {
                            personalBestDate.setText(date);
                        } else {
                            sayToast("This date is in the future");
                        }
                        break;
                }
                daySelectorDialog.dismiss();
            }
        });
        builder.setView(view);
        daySelectorDialog = builder.create();
        daySelectorDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alexw.classes.Date date;
                switch (tabHost.getCurrentTabTag()) {
                    case "Training":
                        if (StringManipulation.isThisDateValid(trainingDate.getText().toString(), "dd/MM/yyyy")) {
                            date = new alexw.classes.Date(trainingDate.getText().toString());
                            datePicker.updateDate(date.getYear(), date.getMonth() - 1, date.getDay());
                        }
                        break;
                    case "Race":
                        if (StringManipulation.isThisDateValid(raceDate.getText().toString(), "dd/MM/yyyy")) {
                            date = new alexw.classes.Date(raceDate.getText().toString());
                            datePicker.updateDate(date.getYear(), date.getMonth() - 1, date.getDay());
                        }
                        break;
                    case "Personal Best":
                        if (StringManipulation.isThisDateValid(personalBestDate.getText().toString(), "dd/MM/yyyy")) {
                            date = new alexw.classes.Date(personalBestDate.getText().toString());
                            datePicker.updateDate(date.getYear(), date.getMonth() - 1, date.getDay());
                        }
                        break;
                }
            }
        });

        //Setup dialog to select race performance (time)
        builder = new AlertDialog.Builder(this);
        view = getLayoutInflater().inflate(R.layout.dialog_time_selector, null);
        final NumberPicker hours = (NumberPicker) view.findViewById(R.id.hour_picker);
        final NumberPicker minutes = (NumberPicker) view.findViewById(R.id.minute_picker);
        final NumberPicker seconds = (NumberPicker) view.findViewById(R.id.second_picker);
        final NumberPicker milliseconds = (NumberPicker) view.findViewById(R.id.millisecond_picker);
        hours.setMinValue(0);
        minutes.setMinValue(0);
        seconds.setMinValue(0);
        milliseconds.setMinValue(0);
        hours.setDisplayedValues(getResources().getStringArray(R.array.time_array2));
        minutes.setDisplayedValues(getResources().getStringArray(R.array.time_array1));
        seconds.setDisplayedValues(getResources().getStringArray(R.array.time_array1));
        milliseconds.setDisplayedValues(getResources().getStringArray(R.array.time_array2));
        hours.setMaxValue(99);
        minutes.setMaxValue(59);
        seconds.setMaxValue(59);
        milliseconds.setMaxValue(99);
        hours.setWrapSelectorWheel(true);
        minutes.setWrapSelectorWheel(true);
        seconds.setWrapSelectorWheel(true);
        milliseconds.setWrapSelectorWheel(true);
        // code to get set values from database
        switch (tabHost.getCurrentTabTag()) {
            case "Race":
                RaceTime inRaceTime = new RaceTime(raceTime.getText().toString());
                hours.setValue(inRaceTime.getHours());
                minutes.setValue(inRaceTime.getMinutes());
                seconds.setValue(inRaceTime.getSeconds());
                milliseconds.setValue(inRaceTime.getMilliseconds());
                break;
            case "Personal Best":
                inRaceTime = new RaceTime(personalBestTime.getText().toString());
                hours.setValue(inRaceTime.getHours());
                minutes.setValue(inRaceTime.getMinutes());
                seconds.setValue(inRaceTime.getSeconds());
                milliseconds.setValue(inRaceTime.getMilliseconds());
                break;
        }

        Button doneButton = (Button) view.findViewById(R.id.time_picker_done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (tabHost.getCurrentTabTag()) {
                    case "Race":
                        RaceTime time = new RaceTime(hours.getValue(), minutes.getValue(), seconds.getValue(), milliseconds.getValue());
                        raceTime.setText(time.getTime(true, true));
                        timeSelectorDialog.dismiss();
                        break;
                    case "Personal Best":
                        time = new RaceTime(hours.getValue(), minutes.getValue(), seconds.getValue(), milliseconds.getValue());
                        personalBestTime.setText(time.getTime(true, true));
                        timeSelectorDialog.dismiss();
                        break;
                }
            }
        });
        builder.setView(view);
        timeSelectorDialog = builder.create();

        //Setup dialog to select personal best distance
        int position = StringManipulation.getPosition(getResources().getStringArray(R.array.distance_array),
                personalBestDistance.getText().toString());
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Your Race Distance")
                .setSingleChoiceItems(R.array.distance_array, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        personalBestDistance.setText(getResources().getStringArray(R.array.distance_array)[which]);
                        distanceSelectorDialog.dismiss();

                    }
                });
        distanceSelectorDialog = builder.create();
    }

    private void setUpRecyclerView() {
        //Setup completed training recycler view for displaying training set objects
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.add_sets_recyclerview);
        trainingAdapter = new RecyclerViewAdapter(this, trainingSets, dbHandler);
        recyclerView.setAdapter(trainingAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(16, 16));

        //Setup active training recycler view for diplaying training set objects
        RecyclerView recyclerView1 = (RecyclerView) findViewById(R.id.add_active_training_sets_recyclerview);
        activeTrainingAdapter = new RecyclerViewAdapter(this, activeTrainingSets, dbHandler);
        recyclerView1.setAdapter(activeTrainingAdapter);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView1.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView1.addItemDecoration(new VerticalSpaceItemDecoration(16, 16));

        trainingAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                buttonAlpha();
            }
        });

        activeTrainingAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                buttonAlpha();
                setTextViewValues();
            }
        });
    }

    public void sayToast(String string) {
        //Display short pop-up toast message
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (entriesChanged()) {
            discardDialog().show();
        } else {
            Intent intent = new Intent(AddActivity.this, MainPage.class);
            intent.putExtra("AccountDetails", accountDetails);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (entriesChanged()) {
                discardDialog().show();
            } else {
                Intent intent = new Intent(AddActivity.this, MainPage.class);
                intent.putExtra("AccountDetails", accountDetails);
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private Dialog infoDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Track Trainer will assume you take a 5 minute rest in between each training" +
                " set, this allows your heart-rate to return to normal");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
            }
        });
        return builder.create();
    }

    protected Dialog savingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_saving, null);
        TextView title = (TextView) view.findViewById(R.id.saving_title);
        title.setText("Saving");
        builder.setView(view);
        builder.setCancelable(false);
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    protected Dialog discardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard Activity?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(AddActivity.this, MainPage.class);
                        intent.putExtra("AccountDetails", accountDetails);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //nothing
                    }
                });
        return builder.create();
    }
}
