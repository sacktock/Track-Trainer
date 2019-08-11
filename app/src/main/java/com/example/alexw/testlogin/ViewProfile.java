package com.example.alexw.testlogin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import alexw.classes.Account_Details;
import alexw.classes.DbHandler;
import alexw.classes.PersonalBest;
import alexw.classes.StringManipulation;

public class ViewProfile extends AppCompatActivity {

    Account_Details accountDetails;
    DbHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dbHandler = new DbHandler(this);
        accountDetails = getIntent().getExtras().getParcelable("AccountDetails");
        setUpButton();
        setUpButtons();
        fillTextViews();
    }

    private void fillTextViews(){
        //Setting details
        TextView name = findViewById(R.id.view_profile_name);
        TextView username = findViewById(R.id.view_profile_username);
        TextView gender = findViewById(R.id.view_profile_gender);
        TextView email = findViewById(R.id.view_profile_email);
        TextView raceDistance = findViewById(R.id.view_profile_race);
        TextView personalBest = findViewById(R.id.view_profile_personalbest);
        TextView distance = findViewById(R.id.view_profile_activity);
        TextView weeks = findViewById(R.id.view_profile_week);

        PersonalBest personalBest1 = dbHandler.getAccountPersonalBest(accountDetails);

        gender.setText(accountDetails.getGender());

        if (personalBest1 == null || personalBest1.getPerformance() == "" || personalBest1.getPerformance() == null) {
            personalBest.setText("");
        } else {
            personalBest.setText("Personal Best: " + personalBest1.getPerformance());
        }

        raceDistance.setText(accountDetails.getRaceDistance() + " Runner");

        int[] weeklyActivity = dbHandler.getPastWeekActivity(accountDetails);

        name.setText(accountDetails.getFirstName() + " " + accountDetails.getLastName());
        username.setText(accountDetails.getUsername());
        email.setText(accountDetails.getEMail());
        distance.setText(Integer.toString(weeklyActivity[1] / 1000) + "km");
        weeks.setText(StringManipulation.dateToRichString(StringManipulation.getLastWeekDate(),
                false) + " - " + StringManipulation.dateToRichString(StringManipulation.getTodayDate(),
                false) );
    }

    private void setUpButtons(){
        Button editPassword = findViewById(R.id.edit_password_btn);
        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfile.this, EditPassword.class);
                intent.putExtra("AccountDetails", accountDetails);
                startActivity(intent);
            }
        });
        Button logOut = findViewById(R.id.log_out_btn);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmLogout().show();
            }
        });
        Button homePage = findViewById(R.id.go_to_homepageCmd);
        homePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfile.this, MainPage.class);
                intent.putExtra("AccountDetails", accountDetails);
                startActivity(intent);
            }
        });
    }

    private void setUpButton(){
        //Setup edit profile button
        Button button = findViewById(R.id.editProfileCmd);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfile.this, EditProfile.class);
                intent.putExtra("AccountDetails", accountDetails);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewProfile.this, MainPage.class);
        intent.putExtra("AccountDetails", accountDetails);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(ViewProfile.this, MainPage.class);
            intent.putExtra("AccountDetails", accountDetails);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Dialog confirmLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbHandler.updateLogout();
                        Intent intent = new Intent(ViewProfile.this, Login.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Empty
                    }
                });
        return builder.create();

    }
}
