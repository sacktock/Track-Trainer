package com.example.alexw.testlogin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import alexw.classes.Account_Details;
import alexw.classes.DbHandler;
import alexw.classes.StaticDataBaseAccessor;

public class MainPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton fabMain;
    ConstraintLayout layout;
    Account_Details accountDetails;
    DbHandler dbHandler = new DbHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        setUpFab();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0f);


        accountDetails = getIntent().getExtras().getParcelable("AccountDetails");

        //Setup navigation drawer layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View view) {
                hideKeyboard();
                super.onDrawerClosed(view);

            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(this);

        //updates account logged in locally on the device
        dbHandler.updateLogin(accountDetails);
        setUpHome();
    }

    private void setUpFab() {
        fabMain = (FloatingActionButton) findViewById(R.id.fab_plus);
        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, AddActivity.class);
                intent.putExtra("AccountDetails", accountDetails);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        hideKeyboard();
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle action bar item clicks
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            final Dialog dialog = refreshingDialog();
            dialog.show();
            Thread thread = new Thread(){
                @Override
                public void run() {
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            };
            thread.start();
            return true;
        }
        if (id == R.id.actions_add) {
            Intent intent = new Intent(MainPage.this, AddActivity.class);
            intent.putExtra("AccountDetails", accountDetails);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Dialog refreshingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_saving, null);
        TextView title = (TextView) view.findViewById(R.id.saving_title);
        title.setText("Refreshing");
        builder.setView(view);
        builder.setCancelable(false);
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle navigation drawer item clicks
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            setUpHome();
        } else if (id == R.id.nav_activetraining) {
            setUpActiveTraining();
        } else if (id == R.id.nav_traininglog) {
            setUpTrainingLog();
        } else if (id == R.id.nav_aboutyou) {
            setUpAboutYou();
        } else if (id == R.id.nav_generatetraining) {
            Intent intent = new Intent(MainPage.this, TrainingGenerator.class);
            intent.putExtra("AccountDetails", accountDetails);
            startActivity(intent);
        } else if (id == R.id.nav_personalbest) {
            Intent intent = new Intent(MainPage.this, PersonalBestPredictor.class);
            intent.putExtra("AccountDetails", accountDetails);
            startActivity(intent);
        } else if (id == R.id.nav_viewprofile){
            Intent intent = new Intent(MainPage.this, ViewProfile.class);
            intent.putExtra("AccountDetails", accountDetails);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setUpHome() {
        //Setup home page fragment
        getSupportActionBar().setTitle("Welcome " + accountDetails.getFirstName());
        HomePage homePage = new HomePage();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.home_page_layout, homePage, homePage.getTag()).commit();
    }

    public void setUpActiveTraining() {
        //Setup active training fragment
        getSupportActionBar().setTitle("Active Training");
        ActiveTraining activeTraining = new ActiveTraining();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.home_page_layout, activeTraining, activeTraining.getTag()).commit();
    }

    public void setUpTrainingLog() {
        //Setup training log fragment
        getSupportActionBar().setTitle("Activity Log");
        TrainingLog trainingLog = new TrainingLog();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.home_page_layout, trainingLog, trainingLog.getTag()).commit();
    }

    public void setUpAboutYou() {
        //Setup about you fragment
        getSupportActionBar().setTitle("About You");
        AboutYou aboutYou = new AboutYou();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.home_page_layout, aboutYou, aboutYou.getTag()).commit();
    }

    public void hideKeyboard() {
        if (!(getCurrentFocus() == null)) {
            getCurrentFocus().clearFocus();
        }
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
