package com.example.alexw.testlogin;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

import alexw.classes.Account_Details;
import alexw.classes.DbHandler;
import alexw.classes.StringManipulation;

public class EditPassword extends AppCompatActivity {

    EditText currentPassword;
    EditText newPassword;
    EditText retypeNewPassword;
    DbHandler dbHandler;
    Button button;
    Account_Details accountDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Password");
        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dbHandler = new DbHandler(this);
        accountDetails = getIntent().getExtras().getParcelable("AccountDetails");
        setUpEditText();
        setUpButton();
    }

    private void setUpButton(){
        button = findViewById(R.id.editPasswordCmd);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFilled()){
                    if (isRightPassword()){
                        if (passwordsMatch()){
                            if (StringManipulation.validPassword(newPassword.getText().toString())) {
                                final Dialog dialog = savingDialog();
                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        Intent intent = new Intent(EditPassword.this, ViewProfile.class);
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
                                        accountDetails.setPassword(newPassword.getText().toString());
                                        dbHandler.updateAccount(accountDetails);
                                        dialog.dismiss();
                                    }
                                };
                                thread.start();
                            } else{
                                invalidPassword().show();
                                unFillEntries();
                            }
                        } else {
                            passwordNotMatch().show();
                            unFillEntries();
                        }
                    } else {
                        wrongPassword().show();
                        unFillEntries();
                    }
                }
            }
        });
        buttonAlpha();
    }

    private boolean isRightPassword(){
        return dbHandler.validLogin(accountDetails.getUsername(), currentPassword.getText().toString());
    }

    private boolean passwordsMatch(){
        return Objects.equals(newPassword.getText().toString(), retypeNewPassword.getText().toString());
    }

    private void setUpEditText(){
        currentPassword = findViewById(R.id.current_password);
        newPassword = findViewById(R.id.edit_password);
        retypeNewPassword = findViewById(R.id.edit_password_retype);
        currentPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                buttonAlpha();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                buttonAlpha();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        retypeNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                buttonAlpha();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public boolean isFilled() {
        if (!(Objects.equals(currentPassword.getText().toString(), ""))) {
            if (!(Objects.equals(newPassword.getText().toString(), ""))) {
                if (!(Objects.equals(retypeNewPassword.getText().toString(), ""))) {
                    return true;
                }
            }
        }
        return false;
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

    private void unFillEntries(){
        newPassword.setText("");
        retypeNewPassword.setText("");
        currentPassword.setText("");
    }

    private Dialog wrongPassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Wrong Current Password")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //empty
                    }
                });
        return builder.create();
    }

    private Dialog passwordNotMatch(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Passwords Don't Match")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //empty
                    }
                });
        return builder.create();
    }

    private Dialog invalidPassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Invalid New Password")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //empty
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditPassword.this, ViewProfile.class);
        intent.putExtra("AccountDetails", accountDetails);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(EditPassword.this, ViewProfile.class);
            intent.putExtra("AccountDetails", accountDetails);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
