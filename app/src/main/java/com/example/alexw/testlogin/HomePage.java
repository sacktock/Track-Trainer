package com.example.alexw.testlogin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import alexw.classes.Account_Details;
import alexw.classes.DbHandler;
import alexw.classes.HomePageItem;
import alexw.classes.Message;
import alexw.classes.RecyclerViewAdapter;
import alexw.classes.TrainingAdapter;
import alexw.classes.VerticalSpaceItemDecoration;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomePage extends Fragment {

    ArrayList<Object> homePageItems = new ArrayList<Object>();
    Bundle bundle;
    RecyclerViewAdapter recylerViewAdapter;
    SwipeRefreshLayout layout;
    Account_Details accountDetails;
    DbHandler dbHandler;

    private void fillArrayList() {
        //Code to fetch objects from the database
        int[] weeklyActivity = dbHandler.getPastWeekActivity(accountDetails);
        ArrayList<Object> list = dbHandler.getAllRaces(accountDetails);
        TrainingAdapter.sortRunActivity(list);
        homePageItems.clear();
        homePageItems.add(new HomePageItem("This Week",
                "Runs: " + Integer.toString(weeklyActivity[0]) + " Distance : " +
                        Integer.toString(weeklyActivity[1] / 1000) + "km", R.drawable.training_focused));

        int activeTrainings = dbHandler.getAllActiveTrainings(accountDetails).size();
        String s = "s";
        if (activeTrainings == 1) {
            s = "";
        }
        homePageItems.add(new HomePageItem("Active Trainings", "You have " + activeTrainings
                + " Active Training" + s, R.drawable.active_training_focused));
        homePageItems.add(new Message("Recent Activities"));
        homePageItems.addAll(list);
    }

    public HomePage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bundle = savedInstanceState;
        dbHandler = ((MainPage) getActivity()).dbHandler;
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        accountDetails = ((MainPage) getActivity()).accountDetails;
        setUpRecyclerView();
        setUpLayout();
        layout.setRefreshing(true);
        getData();
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.home_page_recyclerview);
        recylerViewAdapter = new RecyclerViewAdapter(getActivity(), homePageItems, dbHandler);
        recyclerView.setAdapter(recylerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(16, 0));
        registerForContextMenu(recyclerView);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
    }

    private void setUpLayout() {
        layout = (SwipeRefreshLayout) getActivity().findViewById(R.id.home_layout);
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
