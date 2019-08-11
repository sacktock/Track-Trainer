package com.example.alexw.testlogin;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import alexw.classes.DbHandler;
import alexw.classes.GMailSender;
import alexw.classes.StringManipulation;

public class ForgotPassword extends AppCompatActivity {

    EditText email;
    StringManipulation s = new StringManipulation();
    Button sendButton;
    Dialog dialog;
    DbHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Forgot Password");
        setUpButton();
        setUpEditText();

        dbHandler = new DbHandler(this);

        dialog = sendingDialog();
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

    public void setUpButton() {
        sendButton = (Button) findViewById(R.id.sendCmd);
        sendButton.setClickable(false);
        sendButton.setAlpha(0.5f);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFilled()) {
                    hideKeyboard();
                    final String[] information = dbHandler.getForgottenPassword(email.getText().toString());
                    if (validEntries()) {
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                GMailSender sender = new GMailSender("track.trainer.app@gmail.com", "tracktraineradmin", ForgotPassword.this);
                                try {
                                    sender.sendMail("Track Trainer Forgotten Password", "Dear " + information[1] + ", " +
                                                    "This is your password for Track Trainer: " + information[0],
                                            "track.trainer.app@gmail.com", email.getText().toString());
                                    emailSent().show();
                                } catch (Exception e) {
                                    Log.e("SendMail", e.getMessage(), e);
                                    Toast.makeText(ForgotPassword.this,
                                            "E-mail not sent.", Toast.LENGTH_SHORT).show();
                                }

                                dialog.dismiss();
                                Looper.loop();
                            }
                        };
                        if (information == null) {
                            Toast.makeText(ForgotPassword.this,
                                    "This E-mail does not own an account.", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.show();
                            thread.start();
                        }
                    } else {
                        email.setText("");
                        inValidEmailDialog().show();
                    }
                }
            }

        });
    }

    private Dialog sendingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_saving, null);
        TextView title = (TextView) view.findViewById(R.id.saving_title);
        title.setText("Sending");
        builder.setView(view);
        builder.setCancelable(false);
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private Dialog inValidEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Invalid Email")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Empty
                    }
                });
        return builder.create();
    }

    private Dialog emailSent(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Email sent")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ForgotPassword.this, Login.class);
                        startActivity(intent);
                    }
                });
        return builder.create();
    }

    public void setUpEditText() {
        email = (EditText) findViewById(R.id.forgotEmail);
        email.addTextChangedListener(new TextWatcher() {
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

    public void buttonAlpha() {
        if (isFilled()) {
            sendButton.setAlpha(1.0f);
            sendButton.setClickable(true);
        } else {
            sendButton.setAlpha(0.5f);
            sendButton.setClickable(false);
        }
    }

    public boolean isFilled() {
        if (s.isNoNothing(email.getText().toString())) {
            return true;
        }
        return false;
    }

    public boolean validEntries() {
        if (s.validEmail(email.getText().toString())) {
            return true;
        }
        return false;
    }
}
