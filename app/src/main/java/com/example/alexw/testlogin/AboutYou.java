package com.example.alexw.testlogin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import alexw.classes.Account_Details;
import alexw.classes.DbHandler;
import alexw.classes.Message;
import alexw.classes.RecyclerViewAdapter;
import alexw.classes.Statistic;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutYou extends Fragment {

    ArrayList<Object> aboutYouList = new ArrayList<Object>();
    Bundle bundle;
    RecyclerViewAdapter recylerViewAdapter;
    Account_Details accountDetails;
    DbHandler dbHandler;


    public AboutYou() {
        //Required empty public constructor
    }

    private void fillArrayList() {
        //Code to fetch statistics from the database
        int[] allTimeActivity = dbHandler.getAllTimeActivity(accountDetails);
        int[] pastYearActivity = dbHandler.getPastYearActivity(accountDetails);
        int[] avgWeeklyActivity = dbHandler.getAvgWeeklyActivity(accountDetails);

        //Code to add objects from database into array list to be displayed in the recycler view
        aboutYouList.add(new Message("Avg Weekly Activity".toUpperCase()));
        aboutYouList.add(new Statistic("Time", avgWeeklyActivity[2] / 3600, "h"));
        aboutYouList.add(new Statistic("Distance", avgWeeklyActivity[1] / 1000, "km"));
        aboutYouList.add(new Statistic("Trainings", avgWeeklyActivity[0], ""));
        aboutYouList.add(new Message("Past Year Activity".toUpperCase()));
        aboutYouList.add(new Statistic("Time", pastYearActivity[2] / 3600, "h"));
        aboutYouList.add(new Statistic("Distance", pastYearActivity[1] / 1000, "km"));
        aboutYouList.add(new Statistic("Trainings", pastYearActivity[0], ""));
        aboutYouList.add(new Message("All Time Activity".toUpperCase()));
        aboutYouList.add(new Statistic("Distance", allTimeActivity[1] / 1000, "km"));
        aboutYouList.add(new Statistic("Trainings", allTimeActivity[0], ""));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bundle = savedInstanceState;
        dbHandler = ((MainPage) getActivity()).dbHandler;
        return inflater.inflate(R.layout.fragment_about_you, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        accountDetails = ((MainPage) getActivity()).accountDetails;
        fillArrayList();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.about_you_recyclerview);
        recylerViewAdapter = new RecyclerViewAdapter(getActivity(), aboutYouList, dbHandler);
        recyclerView.setAdapter(recylerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
    }
}
