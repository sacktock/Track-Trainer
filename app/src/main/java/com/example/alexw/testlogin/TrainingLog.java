package com.example.alexw.testlogin;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import java.util.ArrayList;

import alexw.classes.Account_Details;
import alexw.classes.DbHandler;
import alexw.classes.Message;
import alexw.classes.RecyclerViewAdapter;
import alexw.classes.TrainingAdapter;
import alexw.classes.VerticalSpaceItemDecoration;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingLog extends Fragment {

    ArrayList<Object> trainings = new ArrayList<Object>();
    Bundle bundle;
    RecyclerViewAdapter recylerViewAdapter;
    SwipeRefreshLayout layout;
    Account_Details accountDetails;
    DbHandler dbHandler;

    private void fillArrayList() {
        //Code to fetch completed training and race objects from database
        ArrayList<Object> allTrainings = dbHandler.getAllTrainings(accountDetails);
        ArrayList<Object> races = dbHandler.getAllRaces(accountDetails);
        trainings.clear();
        trainings.addAll(allTrainings);
        trainings.addAll(races);
        TrainingAdapter.sortRunActivity(trainings);
        trainings.add(0, new Message("Your Trainings"));
    }

    public TrainingLog() {
        //Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        bundle = savedInstanceState;
        dbHandler = ((MainPage) getActivity()).dbHandler;
        return inflater.inflate(R.layout.fragment_training_log, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        accountDetails = ((MainPage) getActivity()).accountDetails;
        setUpRecyclerView();
        setUpLayout();
        layout.setRefreshing(true);
        getData();
        setUpSearchView();
        hideKeyboard();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    private void setUpLayout() {
        layout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_refresh);
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

    private void setUpSearchView(){
        final SearchView searchView = getActivity().findViewById(R.id.training_log_search);
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
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus){
                    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
                    ((MainPage) getActivity()).fabMain.startAnimation(animation);
                } else {
                    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
                    ((MainPage) getActivity()).fabMain.startAnimation(animation);
                }
            }
        });
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.training_log);
        recylerViewAdapter = new RecyclerViewAdapter(getActivity(), trainings, dbHandler);
        recyclerView.setAdapter(recylerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(16, 0));
        registerForContextMenu(recyclerView);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
    }

    public void hideKeyboard() {
        if (!(getActivity().getCurrentFocus() == null)) {
            getActivity().getCurrentFocus().clearFocus();
        }
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
