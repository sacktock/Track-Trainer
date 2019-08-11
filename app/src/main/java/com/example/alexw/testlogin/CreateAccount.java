package com.example.alexw.testlogin;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import alexw.classes.Account_Details;
import alexw.classes.DbHandler;
import alexw.classes.RaceTime;
import alexw.classes.StringManipulation;

public class CreateAccount extends AppCompatActivity {

    EditText editUsername;
    EditText editPassword;
    EditText re_enterPassword;
    EditText editEmail;
    EditText DoB;
    Button button;
    StringManipulation s = new StringManipulation();
    Dialog birthDateDialog;
    DbHandler dbHandler = new DbHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create Account");
        setUpCreateButton();
        setUpTextView();
        buttonALpha();
        setUpDialogs();

        View current = getCurrentFocus();
        if (current != null) current.clearFocus();
    }

    public void setUpCreateButton() {
        button = (Button) findViewById(R.id.createCmd);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFilled()) {
                    if (validEntries()) {
                        if (emailNotInUse()) {
                            if (usernameNotInUse()) {
                                final Dialog dialog = savingDialog();
                                dialog.show();
                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                        Account_Details accountDetails = new Account_Details("", "", "", editEmail.getText().toString(),
                                                DoB.getText().toString(), "", editUsername.getText().toString(),
                                                editPassword.getText().toString(), "");
                                        dbHandler.createAccount(accountDetails);
                                        accountDetails.setAccountID((dbHandler.getAccountDetails(accountDetails.getUsername(),
                                                accountDetails.getPassword()).getAccountID()));
                                        // code to save information to database
                                        dialog.dismiss();

                                        Intent intent = new Intent(CreateAccount.this, EditProfile.class);
                                        intent.putExtra("AccountDetails", accountDetails);
                                        startActivity(intent);
                                    }
                                };
                                thread.start();
                            } else {
                                msgDialog("Username already in use").show();
                                emptyEntries();
                            }
                        } else {
                            msgDialog("Email already in use").show();
                            emptyEntries();
                        }
                    } else {
                        msgDialog("Invalid Entries").show();
                        emptyEntries();
                    }
                }
            }
        });
    }

    private void emptyEntries(){
        editEmail.setText("");
        editUsername.setText("");
        editPassword.setText("");
        DoB.setText("");
        re_enterPassword.setText("");
    }

    private void setUpDialogs() {
        AlertDialog.Builder builder;
        View view;
        builder = new AlertDialog.Builder(this);

        //Setup dialog to select date
        view = getLayoutInflater().inflate(R.layout.dialog_date_selector, null);
        final NumberPicker day = (NumberPicker) view.findViewById(R.id.day_picker);
        final NumberPicker month = (NumberPicker) view.findViewById(R.id.month_picker);
        final NumberPicker year = (NumberPicker) view.findViewById(R.id.year_picker);
        day.setDisplayedValues(getResources().getStringArray(R.array.day_array));
        month.setDisplayedValues(getResources().getStringArray(R.array.month_array));
        year.setDisplayedValues(getResources().getStringArray(R.array.year_array));
        day.setWrapSelectorWheel(true);
        month.setWrapSelectorWheel(true);
        year.setWrapSelectorWheel(true);

        day.setValue(0);
        month.setValue(0);
        year.setValue(70);

        day.setMinValue(0);
        month.setMinValue(0);
        year.setMinValue(0);

        day.setMaxValue(30);
        month.setMaxValue(11);
        year.setMaxValue(78);

        month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int value = newVal;

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

        Button button = (Button) view.findViewById(R.id.date_picker_done);
        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          DoB.setText(StringManipulation.makeDate(day.getValue() + 1, month.getValue() + 1, year.getValue() + 1930));
                                          birthDateDialog.dismiss();
                                      }
                                  }
        );
        builder.setView(view);
        birthDateDialog = builder.create();
    }

    private Dialog savingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_saving, null);
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
                        //empty event
                    }
                });
        return builder.create();
    }

    public boolean isFilled() {
        if (s.isNoNothing(editEmail.getText().toString())) {
            if (s.isNoNothing(editUsername.getText().toString())) {
                if (s.isNoNothing(editPassword.getText().toString())) {
                    if (s.isNoNothing(re_enterPassword.getText().toString())) {
                        if (s.isNoNothing(DoB.getText().toString())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void setUpTextView() {
        editUsername = (EditText) findViewById(R.id.createUsername);
        editPassword = (EditText) findViewById(R.id.createPassword);
        editEmail = (EditText) findViewById(R.id.createEmail);
        re_enterPassword = (EditText) findViewById(R.id.createRe_EnterPassword);
        DoB = (EditText) findViewById(R.id.createDate_of_birth);
        DoB.setFocusable(false);
        editUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonALpha();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonALpha();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonALpha();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        re_enterPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonALpha();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        DoB.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonALpha();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        DoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                birthDateDialog.show();
            }
        });
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

    public boolean validEntries() {
        if (StringManipulation.validEmail(editEmail.getText().toString())) {
            if (StringManipulation.validPassword(editPassword.getText().toString())) {
                if (StringManipulation.equals(editPassword.getText().toString(), re_enterPassword.getText().toString())) {
                    if (StringManipulation.isThisDateValid(DoB.getText().toString(), "dd/MM/yyyy")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean emailNotInUse(){
        return (dbHandler.emailNotInUse(editEmail.getText().toString()));
    }

    private boolean usernameNotInUse(){
        return (dbHandler.usernameNotInUse(editUsername.getText().toString()));
    }

    public void buttonALpha() {
        if (isFilled()) {
            button.setAlpha(1.0f);
            button.setClickable(true);
        } else {
            button.setAlpha(0.5f);
            button.setClickable(false);
        }
    }
}
