package com.example.alexw.testlogin;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Date;
import java.util.Objects;

import alexw.classes.Account_Details;
import alexw.classes.DbHandler;
import alexw.classes.PersonalBest;
import alexw.classes.RaceTime;
import alexw.classes.StringManipulation;

public class EditProfile extends AppCompatActivity {

    Button button;
    Account_Details accountDetails;
    EditText firstName;
    EditText lastName;
    EditText eMail;
    EditText birthDate;
    EditText gender;
    EditText raceDistance;
    EditText racePB;
    EditText username;
    Dialog raceDistanceDialog;
    Dialog genderDialog;
    Dialog raceTimeDialog;
    Dialog birthDateDialog;
    PersonalBest personalBest;
    DbHandler dbHandler = new DbHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountDetails = getIntent().getExtras().getParcelable("AccountDetails");
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        setUpDoneButton();
        setUPEditText();
        buttonAlpha();
        fillEntries(accountDetails);
        setUpDialogs();
        View current = getCurrentFocus();
        if (current != null) current.clearFocus();
        dbHandler.updateLogin(accountDetails);
    }

    private void fillEntries(Account_Details details) {
        //Setting details
        firstName.setText(details.getFirstName());
        lastName.setText(details.getLastName());
        eMail.setText(details.getEMail());
        birthDate.setText(details.getBirthDate());
        gender.setText((details.getGender()));
        raceDistance.setText(details.getRaceDistance());
        dbHandler.refreshPersonalBest(accountDetails);
        personalBest = dbHandler.getAccountPersonalBest(accountDetails);
        if (personalBest == null || personalBest.getPerformance() == "") {
            racePB.setText("");
        } else {
            racePB.setText(personalBest.getPerformance());
        }
        username.setText(details.getUsername());
    }

    private void setUpDoneButton() {
        button = (Button) findViewById(R.id.doneCmd);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFilled()) {
                    if (validEntries()) {
                        if (emailNotInUse()) {
                            if (usernameNotInUse()) {
                                final Account_Details details = new Account_Details(accountDetails.getAccountID(),
                                        firstName.getText().toString(), lastName.getText().toString(), eMail.getText().toString(),
                                        birthDate.getText().toString(), gender.getText().toString(), username.getText().toString(),
                                        accountDetails.getPassword(), raceDistance.getText().toString());
                                final Dialog dialog1 = savingDialog();
                                dialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        Intent intent = new Intent(EditProfile.this, ViewProfile.class);
                                        intent.putExtra("AccountDetails", details);
                                        startActivity(intent);
                                    }
                                });
                                dialog1.show();

                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        dbHandler.updateAccount(details);
                                        dbHandler.updateAccountPersonalBest(details, racePB.getText().toString());
                                        dialog1.dismiss();
                                    }
                                };
                                thread.start();
                            } else {
                                msgDialog("Username already in use").show();
                            }
                        } else {
                            msgDialog("Email already in use").show();
                        }
                    } else {
                        msgDialog("Invalid Entries").show();
                    }
                }
            }

        });
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

    private boolean entriesChanged() {
        //Checks if entries have been changed
        if (Objects.equals(firstName.getText().toString(), accountDetails.getFirstName())) {
            if (Objects.equals(lastName.getText().toString(), accountDetails.getLastName())) {
                if (Objects.equals(eMail.getText().toString(), accountDetails.getEMail())) {
                    if (Objects.equals(birthDate.getText().toString(), accountDetails.getBirthDate())) {
                        if (Objects.equals(gender.getText().toString(), accountDetails.getGender())) {
                            if (Objects.equals(raceDistance.getText().toString(), accountDetails.getRaceDistance())) {
                                if (Objects.equals(username.getText().toString(), accountDetails.getUsername())) {
                                    if (personalBest == null){
                                        return false;
                                    } else if (Objects.equals(racePB.getText().toString(), personalBest.getPerformance())) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean isFilled() {
        //Checks if entries have been filled
        StringManipulation sm = new StringManipulation();
        if (StringManipulation.isNoNothing(firstName.getText().toString())) {
            if (StringManipulation.isNoNothing(lastName.getText().toString())) {
                if (StringManipulation.isNoNothing(eMail.getText().toString())) {
                    if (StringManipulation.isNoNothing(birthDate.getText().toString())) {
                        if (StringManipulation.isNoNothing(gender.getText().toString())) {
                            if (StringManipulation.isNoNothing(raceDistance.getText().toString())) {
                                if (StringManipulation.isNoNothing(racePB.getText().toString())) {
                                    if (StringManipulation.isNoNothing(username.getText().toString())) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (isFilled()) {
            if (entriesChanged()) {
                saveChangesDialog().show();
            } else {
                Intent intent = new Intent(EditProfile.this, ViewProfile.class);
                intent.putExtra("AccountDetails", accountDetails);
                startActivity(intent);
            }
        } else {
            notFilledDialog().show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (isFilled()) {
                if (entriesChanged()) {
                    saveChangesDialog().show();
                } else {
                    Intent intent = new Intent(EditProfile.this, ViewProfile.class);
                    intent.putExtra("AccountDetails", accountDetails);
                    startActivity(intent);
                }
            } else {
                notFilledDialog().show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validEntries() {
         if (firstName.getText().toString().length() >= 3) {
            if (lastName.getText().toString().length() >= 2) {
                if (StringManipulation.validEmail(eMail.getText().toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean usernameNotInUse(){
        if (Objects.equals(username.getText().toString(), accountDetails.getUsername())){
            return true;
        } else {
            return dbHandler.usernameNotInUse(username.getText().toString());
        }
    }

    private boolean emailNotInUse(){
        if (Objects.equals(eMail.getText().toString(), accountDetails.getEMail())){
            return true;
        } else {
            return dbHandler.emailNotInUse(eMail.getText().toString());
        }
    }

    private void setUpDialogs() {
        //Setup primary race distance selector dialog
        View view;
        int position;
        position = StringManipulation.getPosition(getResources().getStringArray(R.array.distance_array),
                raceDistance.getText().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Your Race Distance")
                .setSingleChoiceItems(R.array.distance_array, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        raceDistance.setText(getResources().getStringArray(R.array.distance_array)[which]);
                        raceDistanceDialog.dismiss();

                    }
                });
        raceDistanceDialog = builder.create();

        //Setup gender selector dialog
        position = StringManipulation.getPosition(getResources().getStringArray(R.array.gender_array),
                gender.getText().toString());
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Your Category")
                .setSingleChoiceItems(R.array.gender_array, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gender.setText(getResources().getStringArray(R.array.gender_array)[which]);
                        genderDialog.dismiss();

                    }
                });
        genderDialog = builder.create();

        //Setup personal best performance selector dialog
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
        RaceTime inRaceTime = new RaceTime(racePB.getText().toString());
        hours.setValue(inRaceTime.getHours());
        minutes.setValue(inRaceTime.getMinutes());
        seconds.setValue(inRaceTime.getSeconds());
        milliseconds.setValue(inRaceTime.getMilliseconds());

        Button doneButton = (Button) view.findViewById(R.id.time_picker_done);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RaceTime time = new RaceTime(hours.getValue(), minutes.getValue(), seconds.getValue(), milliseconds.getValue());
                racePB.setText(time.getTime(true, true));
                raceTimeDialog.dismiss();
            }
        });
        builder.setView(view);
        raceTimeDialog = builder.create();

        //Setup birth date selector dialog
        builder = new AlertDialog.Builder(this);
        view = getLayoutInflater().inflate(R.layout.dialog_date_selector, null);
        final NumberPicker day = (NumberPicker) view.findViewById(R.id.day_picker);
        final NumberPicker month = (NumberPicker) view.findViewById(R.id.month_picker);
        final NumberPicker year = (NumberPicker) view.findViewById(R.id.year_picker);

        month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (newVal == 0 || newVal == 2 || newVal == 4 || newVal == 6 || newVal == 7 || newVal == 9 || newVal == 11) {
                    day.setMaxValue(30);
                } else if (newVal == 1) {
                    if (StringManipulation.isGapYear(year.getValue() + 1930))
                        day.setMaxValue(28);
                    else {
                        day.setMaxValue(27);
                    }
                } else {
                    day.setMaxValue(29);
                }
            }
        });

        year.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int value = month.getValue();

                if (value == 0 || value == 2 || value == 4 || value == 6 || value == 7 || value == 9 || value == 11) {
                    day.setMaxValue(30);
                } else if (value == 1) {
                    if (StringManipulation.isGapYear(year.getValue() + 1930))
                        day.setMaxValue(28);
                    else {
                        day.setMaxValue(27);
                    }
                } else {
                    day.setMaxValue(29);
                }
            }
        });

        day.setDisplayedValues(getResources().getStringArray(R.array.day_array));
        month.setDisplayedValues(getResources().getStringArray(R.array.month_array));
        year.setDisplayedValues(getResources().getStringArray(R.array.year_array));
        day.setWrapSelectorWheel(true);
        month.setWrapSelectorWheel(true);
        year.setWrapSelectorWheel(true);

        day.setMinValue(0);
        month.setMinValue(0);
        year.setMinValue(0);

        day.setMaxValue(30);
        month.setMaxValue(11);
        year.setMaxValue(78);

        alexw.classes.Date date = new alexw.classes.Date(birthDate.getText().toString());

        day.setValue(date.getDay() - 1);
        month.setValue(date.getMonth() - 1);
        year.setValue(date.getYear() - 1930);

        Button button = (Button) view.findViewById(R.id.date_picker_done);
        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          birthDate.setText(StringManipulation.makeDate(day.getValue() + 1, month.getValue() + 1, year.getValue() + 1930));
                                          birthDateDialog.dismiss();
                                      }
                                  }
        );

        builder.setView(view);
        birthDateDialog = builder.create();
    }

    private Dialog saveChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Save Changes?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        final Account_Details details = new Account_Details(accountDetails.getAccountID(),
                                firstName.getText().toString(), lastName.getText().toString(), eMail.getText().toString(),
                                birthDate.getText().toString(), gender.getText().toString(), username.getText().toString(),
                                accountDetails.getPassword(), raceDistance.getText().toString());
                        final Dialog dialog1 = savingDialog();
                        dialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                Intent intent = new Intent(EditProfile.this, ViewProfile.class);
                                intent.putExtra("AccountDetails", details);
                                startActivity(intent);
                            }
                        });
                        dialog1.show();

                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                dbHandler.updateAccount(details);
                                dbHandler.updateAccountPersonalBest(details, racePB.getText().toString());
                                dialog1.dismiss();
                            }
                        };
                        thread.start();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(EditProfile.this, ViewProfile.class);
                        intent.putExtra("AccountDetails", accountDetails);
                        startActivity(intent);
                    }
                });
        return builder.create();
    }

    private Dialog savingDialog() {
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

    private Dialog msgDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Empty
                    }
                });
        return builder.create();
    }

    private Dialog notFilledDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Entries Not Filled")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Empty
                    }
                });
        return builder.create();
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

    private void setUPEditText() {
        firstName = (EditText) findViewById(R.id.edit_first_name);
        lastName = (EditText) findViewById(R.id.edit_last_name);
        eMail = (EditText) findViewById(R.id.edit_email);
        birthDate = (EditText) findViewById(R.id.edit_birthdate);
        gender = (EditText) findViewById(R.id.edit_gender);
        raceDistance = (EditText) findViewById(R.id.edit_race_distance);
        racePB = (EditText) findViewById(R.id.edit_race_pb);
        username = (EditText) findViewById(R.id.edit_username);
        raceDistance.setFocusable(false);
        racePB.setFocusable(false);
        gender.setFocusable(false);
        birthDate.setFocusable(false);

        firstName.addTextChangedListener(new TextWatcher() {
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
        lastName.addTextChangedListener(new TextWatcher() {
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
        eMail.addTextChangedListener(new TextWatcher() {
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
        birthDate.addTextChangedListener(new TextWatcher() {
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
        gender.addTextChangedListener(new TextWatcher() {
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
        raceDistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonAlpha();
                racePB.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        racePB.addTextChangedListener(new TextWatcher() {
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
        username.addTextChangedListener(new TextWatcher() {
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
        raceDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                raceDistanceDialog.show();
            }
        });
        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                genderDialog.show();
            }
        });
        racePB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                raceTimeDialog.show();
            }
        });
        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                birthDateDialog.show();
            }
        });
    }
}
