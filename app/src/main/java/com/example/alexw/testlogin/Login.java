package com.example.alexw.testlogin;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import alexw.classes.Account_Details;
import alexw.classes.DbHandler;
import alexw.classes.StringManipulation;


public class Login extends AppCompatActivity {

    EditText username;
    EditText password;
    Button button;
    DbHandler dbHandler = new DbHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Log In");
        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setSubtitle("Enter your Details");
        setUpNextButton();
        setUpTextView();
        setUpNewUser();
        setUpForgotPassword();

        View current = getCurrentFocus();
        if (current != null) current.clearFocus();
    }

    public void setUpNewUser() {
        //Setup new user button
        TextView textView;
        textView = (TextView) findViewById(R.id.newuserCmd);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, CreateAccount.class));
            }

        });
    }

    public void setUpForgotPassword() {
        //Setup forgot password button
        TextView textView;
        textView = (TextView) findViewById(R.id.forgotPasswordCmd);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ForgotPassword.class));
            }
        });
    }

    public void setUpNextButton() {
        button = (Button) findViewById(R.id.nextCmd);
        button.setClickable(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidLogin()) {
                    if (isFilled()) {
                        final Dialog dialog = loggingInDialog();
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                Intent intent = new Intent(Login.this, MainPage.class);
                                intent.putExtra("AccountDetails", getAccountDetails());
                                startActivity(intent);
                            }
                        });
                        dialog.show();

                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    sleep(500);
                                    dialog.dismiss();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                    }
                } else {
                    inValidDetailsDialog().show();
                    password.setText("");
                    password.requestFocus();
                    buttonAlpha();
                }
            }

        });
    }

    private Account_Details getAccountDetails() {
        //Code to get account details from data base when logging in
        return dbHandler.getAccountDetails(username.getText().toString(), password.getText().toString());
    }

    public boolean isValidLogin() {
        return dbHandler.validLogin(username.getText().toString(), password.getText().toString());
    }

    public boolean isFilled() {
        if (StringManipulation.isNoNothing(username.getText().toString())) {
            if (StringManipulation.isNoNothing(password.getText().toString())) {
                return true;
            }
        }
        return false;
    }

    private Dialog loggingInDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_saving, null);
        TextView title = (TextView) view.findViewById(R.id.saving_title);
        title.setText("Authenticating");
        builder.setView(view);
        builder.setCancelable(false);
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private Dialog inValidDetailsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Invalid log-in details")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Empty
                    }
                });
        return builder.create();
    }

    public void buttonAlpha() {
        if (isFilled()) {
            button.setAlpha(1.0f);
            button.setClickable(true);
        } else {
            button.setAlpha(0.5f);
            button.setClickable(false);
        }
    }

    public void setUpTextView() {
        username = (EditText) findViewById(R.id.editUsername);
        password = (EditText) findViewById(R.id.editPassword);

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
        password.addTextChangedListener(new TextWatcher() {
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
    }
}


