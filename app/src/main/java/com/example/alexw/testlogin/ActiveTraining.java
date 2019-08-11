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
import alexw.classes.Message;
import alexw.classes.RecyclerViewAdapter;
import alexw.classes.VerticalSpaceItemDecoration;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveTraining extends Fragment {

    ArrayList<Object> activeTraining = new ArrayList<Object>();
    Bundle bundle;
    RecyclerViewAdapter recyclerViewAdapter;
    SwipeRefreshLayout layout;
    Account_Details accountDetails;
    DbHandler dbHandler;

    private void fillArrayList() {
        //Code to fetch active training objects from the database
        ArrayList<Object> objects = dbHandler.getAllActiveTrainings(accountDetails);
        activeTraining.clear();
        activeTraining.add(new Message("You have " + Integer.toString(objects.size())
                + " Active Training", "Add more using the Training Generator"));
        activeTraining.addAll(objects);
    }

    public ActiveTraining() {
        //Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        bundle = savedInstanceState;
        dbHandler = ((MainPage) getActivity()).dbHandler;
        return inflater.inflate(R.layout.fragment_active_training, container, false);
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
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.active_training_recyclerview);
        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), activeTraining, dbHandler);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(16, 0));
        registerForContextMenu(recyclerView);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
    }

    private void setUpLayout() {
        layout = (SwipeRefreshLayout) getActivity().findViewById(R.id.active_training_swipe);
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
        // code to notify adapter
        recyclerViewAdapter.notifyDataSetChanged();
        layout.setRefreshing(false);
    }
}
