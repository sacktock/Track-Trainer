package com.example.alexw.testlogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.MenuItem;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Arrays;

import alexw.classes.Account_Details;
import alexw.classes.Date;
import alexw.classes.DbHandler;
import alexw.classes.Message;
import alexw.classes.PersonalBest;
import alexw.classes.RecyclerViewAdapter;
import alexw.classes.StringManipulation;
import alexw.classes.VerticalSpaceItemDecoration;
import alexw.classes.run_Activity;

public class PersonalBestPredictor extends AppCompatActivity {

    ArrayList<Object> personalBests = new ArrayList<Object>();
    RecyclerViewAdapter recylerViewAdapter;
    SwipeRefreshLayout layout;
    Account_Details accountDetails;
    DbHandler dbHandler = new DbHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_best_predictor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Personal Bests");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0f);

        accountDetails = getIntent().getExtras().getParcelable("AccountDetails");

        fillArrayList();
        setUpRecyclerView();
        setUpLayout();
        setUpSearchView();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PersonalBestPredictor.this, MainPage.class);
        intent.putExtra("AccountDetails", accountDetails);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(PersonalBestPredictor.this, MainPage.class);
            intent.putExtra("AccountDetails", accountDetails);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillArrayList() {
        //Code to fetch personal best objects from database
        dbHandler.refreshPersonalBest(accountDetails);
        ArrayList<Object> list = dbHandler.getAllPersonalBest(accountDetails);
        personalBests.clear();
        personalBests.addAll(list);
        sortPersonalBests(personalBests);
        personalBests.add(0, new Message("Get Prediction", "Press the menu button to get a record prediction"));
    }

    public void sortPersonalBests(ArrayList<Object> list) {
        //Merge sort personal best objects by ascending distance
        //Shortest distance first
        class Mergesort {
            private PersonalBest[] activities;
            private PersonalBest[] helper;

            private String[] distances = getResources().getStringArray(R.array.distance_array);
            private int number;

            public void sort(PersonalBest[] values) {
                this.activities = values;
                number = values.length;
                this.helper = new PersonalBest[number];
                mergesort(0, number - 1);
            }

            private void mergesort(int low, int high) {
                // check if low is smaller than high, if not then the array is sorted
                if (low < high) {
                    // Get the index of the element which is in the middle
                    int middle = low + (high - low) / 2;
                    // Sort the left side of the array
                    mergesort(low, middle);
                    // Sort the right side of the array
                    mergesort(middle + 1, high);
                    // Combine them both
                    merge(low, middle, high);
                }
            }

            private void merge(int low, int middle, int high) {
                // Copy both parts into the helper array
                for (int i = low; i <= high; i++) {
                    helper[i] = activities[i];
                }

                int i = low;
                int j = middle + 1;
                int k = low;
                // Copy the smallest values from either the left or the right side back
                // to the original array
                while (i <= middle && j <= high) {
                    if (StringManipulation.getPosition(distances, helper[i].getDistance()) <
                            StringManipulation.getPosition(distances, helper[j].getDistance())) {
                        activities[k] = helper[i];
                        i++;
                    } else {
                        activities[k] = helper[j];
                        j++;
                    }
                    k++;
                }
                // Copy the rest of the left side of the array into the target array
                while (i <= middle) {
                    activities[k] = helper[i];
                    k++;
                    i++;
                }
                // Since we are sorting in-place any leftover elements from the right side
                // are already at the right position.
            }
        }

        PersonalBest[] pbs = new PersonalBest[list.size()];

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof PersonalBest){
                pbs[i] = (PersonalBest) list.get(i);
            }
        }

        Mergesort mergesort = new Mergesort();
        mergesort.sort(pbs);

        list.clear();
        list.addAll(Arrays.asList(pbs));
    }

    private void setUpSearchView(){
        final SearchView searchView = findViewById(R.id.personal_best_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (searchView.getQuery().length() == 0) {
                    //If search string is ""
                    recylerViewAdapter.filter("");
                } else {
                    recylerViewAdapter.filter(s);
                }
                return false;
            }});
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                recylerViewAdapter.filter("");
                return false;
            }
        });
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.personal_best_recyclerview);
        recylerViewAdapter = new RecyclerViewAdapter(this, personalBests, dbHandler);
        recyclerView.setAdapter(recylerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(16, 0));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        registerForContextMenu(recyclerView);
    }

    private void setUpLayout() {
        layout = (SwipeRefreshLayout) findViewById(R.id.personal_best_layout);
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                layout.setRefreshing(true);
                getData();
            }
        });
    }

    private void getData() {
        fillArrayList();
        loadData();
    }

    private void loadData() {
        recylerViewAdapter.notifyDataSetChanged();
        layout.setRefreshing(false);
    }
}
