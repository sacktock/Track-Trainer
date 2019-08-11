package com.example.alexw.testlogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import alexw.classes.Account_Details;
import alexw.classes.DbHandler;
import alexw.classes.StringManipulation;

public class SplashScreen extends AppCompatActivity {

    private DbHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler = new DbHandler(this);
        autoLogin();
    }

    public void autoLogin() {
        Account_Details accountDetails = dbHandler.getLoginAccount();
        if (accountDetails == null) {
            //If no account logged into device start login activity
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        } else {
            if (StringManipulation.isNoNothing(accountDetails.getGender())) {
                //If account is logged in start main page activity
                Intent intent = new Intent(SplashScreen.this, MainPage.class);
                intent.putExtra("AccountDetails", accountDetails);
                startActivity(intent);
                finish();
            } else {
                //If account is incomplete start edit profile activity
                Intent intent = new Intent(SplashScreen.this, EditProfile.class);
                intent.putExtra("AccountDetails", accountDetails);
                startActivity(intent);
                finish();
            }
        }
    }
}
