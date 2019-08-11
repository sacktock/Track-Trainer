package alexw.classes;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.alexw.testlogin.EditActivity;
import com.example.alexw.testlogin.R;
import com.example.alexw.testlogin.TrainingGenerator;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by alexw on 9/6/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Object> collection = new ArrayList<>();
    private ArrayList<Object> actualData = new ArrayList<>();
    private Dialog dialog;
    private DbHandler dbHandler;
    private Context context;
    private StatsUpdater statsUpdater;
    //Unique instance identifiers
    private final int TRAINING= 0, HOMEPAGE= 1, ACTIVETRAINING =2, HEADER=3, PERSONALBEST = 4,STATISTIC = 5, TRAININGSET =6, MESSAGE = 7, ADDTRAININGSET = 8;

    public RecyclerViewAdapter(Context context, ArrayList<Object> collection, DbHandler dbHandler){
        this.collection = collection;
        this.actualData = collection;
        this.context = context;
        this.dbHandler = dbHandler;
    }

    public void setCollection(ArrayList<Object> collection){
        this.collection = collection;
        this.actualData = collection;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Method decides which ViewHolder to instantiate
        int ResID;
        View view;
        switch(viewType){
            case TRAINING:
                ResID = R.layout.item_layout;
                view = LayoutInflater.from(context).inflate(ResID, parent, false);
                return new TrainingViewHolder(view);
            case HOMEPAGE:
                ResID = R.layout.home_page_list_layout;
                view = LayoutInflater.from(context).inflate(ResID, parent, false);
                return new HomePageViewHolder(view);
            case ACTIVETRAINING:
                ResID = R.layout.active_training_layout;
                view = LayoutInflater.from(context).inflate(ResID, parent, false);
                return new ActiveTrainingViewHolder(view);
            case HEADER:
                ResID = R.layout.list_view_header;
                view = LayoutInflater.from(context).inflate(ResID, parent, false);
                return new StringViewHolder(view);
            case PERSONALBEST:
                ResID = R.layout.list_view_personal_best;
                view = LayoutInflater.from(context).inflate(ResID, parent, false);
                return new PersonalBestViewHolder(view);
            case STATISTIC:
                ResID = R.layout.list_view_statistic;
                view = LayoutInflater.from(context).inflate(ResID, parent, false);
                return new StatisticViewHolder(view);
            case TRAININGSET:
                ResID = R.layout.list_view_training_set;
                view = LayoutInflater.from(context).inflate(ResID, parent, false);
                return new Training_SetViewHolder(view);
            case MESSAGE:
                ResID = R.layout.list_view_message;
                view = LayoutInflater.from(context).inflate(ResID, parent, false);
                return new MessageViewHolder(view);
            case ADDTRAININGSET:
                ResID = R.layout.list_view_add_set;
                view = LayoutInflater.from(context).inflate(ResID, parent, false);
                return new AddTrainingSetViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //Method sets up each ViewHolder using details from the corresponding objects
        final Object object = collection.get(position);
        switch (getItemViewType(position)){
            case TRAINING:
                final TrainingViewHolder trainingViewHolder = (TrainingViewHolder) holder;
                if (object instanceof Completed_Training){
                    final Completed_Training training = (Completed_Training) object;
                    //Setting values
                    trainingViewHolder.name.setText(training.getName());
                    TrainingAdapter.setImageView(trainingViewHolder.difficulty, (TrainingAdapter.getDifficultyFromString(training.getDifficulty())));
                    ArrayList<Object> temp = new ArrayList<Object>();
                    TrainingAdapter.getTrainingSetsFromString(training.getDescription(),temp);
                    trainingViewHolder.description.setText(TrainingAdapter.getDisplayDescription(temp));
                    trainingViewHolder.date.setText(training.getDate());
                    trainingViewHolder.type.setText(training.getTrainingType());
                    trainingViewHolder.accountName.setText(training.getAccountDetails().getFirstName() +" " + training.getAccountDetails().getLastName());
                    trainingViewHolder.username.setText(training.getAccountDetails().getUsername());
                    trainingViewHolder.menuButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Setup menu button
                            PopupMenu popupMenu = new PopupMenu(context, trainingViewHolder.menuButton);
                            popupMenu.inflate(R.menu.list_view_menu);
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()){
                                        case R.id.menu_delete:
                                            //Delete training
                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            builder.setMessage("Are you sure you want to delete this activity")
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //Setup deleting dialog
                                                            AlertDialog.Builder deletingBuilder = new AlertDialog.Builder(context);
                                                            View deletingView = View.inflate(context, R.layout.dialog_saving, null);
                                                            TextView title = (TextView) deletingView.findViewById(R.id.saving_title);
                                                            title.setText("Removing");
                                                            deletingBuilder.setView(deletingView);
                                                            deletingBuilder.setCancelable(false);
                                                            final Dialog deletingDialog = deletingBuilder.create();
                                                            deletingDialog.setCancelable(false);
                                                            deletingDialog.setCanceledOnTouchOutside(false);
                                                            deletingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                                @Override
                                                                public void onDismiss(DialogInterface dialogInterface) {
                                                                    notifyDataSetChanged();
                                                                }
                                                            });

                                                            deletingDialog.show();
                                                            Thread thread = new Thread(){
                                                                @Override
                                                                public void run() {
                                                                    //Thread to delete training and update stats
                                                                    Looper.prepare();
                                                                    collection.remove(position);
                                                                    actualData.remove(training);
                                                                    dbHandler.deleteTraining(training);
                                                                    //Code to update stats
                                                                    statsUpdater = new StatsUpdater(training.getAccountDetails(), context);
                                                                    statsUpdater.update();
                                                                    deletingDialog.dismiss();
                                                                    Looper.loop();
                                                                }
                                                            };
                                                            thread.start();
                                                        }
                                                    })
                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //Empty
                                                        }
                                                    });
                                            builder.create().show();
                                            break;
                                        case R.id.menu_edit:
                                            //Edit training
                                            Intent intent = new Intent(context, EditActivity.class);
                                            intent.putExtra("AccountDetails", training.getAccountDetails());
                                            intent.putExtra("Object", training);
                                            context.startActivity(intent);
                                            break;
                                    }
                                    return false;
                                }
                            });
                            popupMenu.show();
                        }
                    });
                    break;
                } else if (object instanceof Race){
                    final Race race = (Race) object;
                    //Setting values
                    trainingViewHolder.name.setText(race.getName());
                    trainingViewHolder.description.setText(race.getDescription());
                    trainingViewHolder.date.setText(race.getDate());
                    trainingViewHolder.type.setText("Race");
                    trainingViewHolder.accountName.setText(race.getAccountDetails().getFirstName() + " " + race.getAccountDetails().getLastName());
                    trainingViewHolder.username.setText(race.getAccountDetails().getUsername());
                    trainingViewHolder.menuButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Setup menu button
                            PopupMenu popupMenu = new PopupMenu(context, trainingViewHolder.menuButton);
                            popupMenu.inflate(R.menu.list_view_menu);
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()){
                                        case R.id.menu_delete:
                                            //Delete race
                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            builder.setMessage("Are you sure you want to delete this activity")
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            collection.remove(position);
                                                            actualData.remove(race);
                                                            dbHandler.deleteRace(race);
                                                            notifyDataSetChanged();
                                                        }
                                                    })
                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    });
                                            builder.create().show();
                                            break;
                                        case R.id.menu_edit:
                                            //Edit race
                                            Intent intent = new Intent(context, EditActivity.class);
                                            intent.putExtra("AccountDetails", race.getAccountDetails());
                                            intent.putExtra("Object", race);
                                            context.startActivity(intent);
                                            break;
                                    }
                                    return false;
                                }
                            });
                            popupMenu.show();
                        }
                    });
                    break;
                }

            case HOMEPAGE:
                HomePageViewHolder homePageViewHolder = (HomePageViewHolder) holder;
                if (object instanceof HomePageItem){
                    HomePageItem homePageItem = (HomePageItem) object;
                    //Setting values
                    homePageViewHolder.description.setText(homePageItem.getDescription());
                    homePageViewHolder.text.setText(homePageItem.getText());
                    homePageViewHolder.image.setImageResource(homePageItem.getImgResource());
                }
                break;
            case ACTIVETRAINING:
                final ActiveTrainingViewHolder activeTrainingViewHolder = (ActiveTrainingViewHolder) holder;
                if (object instanceof Active_Training) {
                    final Active_Training activeTraining = (Active_Training) object;
                    //Setting values
                    activeTrainingViewHolder.date.setText("Date Set: " + activeTraining.getDateIssued());
                    ArrayList<Object> temp = new ArrayList<Object>();
                    TrainingAdapter.getTrainingSetsFromString(activeTraining.getDescription(),temp);
                    activeTrainingViewHolder.description.setText(
                            TrainingAdapter.getDisplayDescription(temp));
                    activeTrainingViewHolder.isCompleted.setText("To Be Completed");
                    activeTrainingViewHolder.trainingType.setText(activeTraining.getTrainingType());
                    activeTrainingViewHolder.totalTime.setText("Total Time: " + new RaceTime(activeTraining.getTotalTime()).getTime(true, false));
                    activeTrainingViewHolder.totalDistance.setText("Total Distance: " + Integer.toString(activeTraining.getTotalDistance()) + "m");
                    activeTrainingViewHolder.accountName.setText(activeTraining.getAccountDetails().getFirstName() + " " + activeTraining.getAccountDetails().getLastName());
                    activeTrainingViewHolder.username.setText(activeTraining.getAccountDetails().getUsername());

                    activeTrainingViewHolder.completeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Setup complete button
                            View itemView = View.inflate(context, R.layout.dialog_difficulty, null);
                            //Set up difficulty chooser interface
                            ImageView easy = (ImageView) itemView.findViewById(R.id.complete_ic_easy);
                            ImageView good = (ImageView) itemView.findViewById(R.id.complete_ic_good);
                            ImageView challenging = (ImageView) itemView.findViewById(R.id.complete_ic_challenging);
                            ImageView hard = (ImageView) itemView.findViewById(R.id.complete_ic_hard);
                            final DifficultyChooser difficultyChooser = new DifficultyChooser(easy, good, challenging, hard);
                            difficultyChooser.setUpImageView();

                            //Setup saving dialog
                            AlertDialog.Builder savingBuilder = new AlertDialog.Builder(context);
                            View savingView = View.inflate(context, R.layout.dialog_saving, null);
                            TextView title = (TextView) savingView.findViewById(R.id.saving_title);
                            title.setText("Saving");
                            savingBuilder.setView(savingView);
                            savingBuilder.setCancelable(false);
                            final Dialog savingDialog = savingBuilder.create();
                            savingDialog.setCancelable(false);
                            savingDialog.setCanceledOnTouchOutside(false);
                            savingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    notifyDataSetChanged();
                                }
                            });

                            //Setup complete dialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Training completed?")
                                    .setMessage("select how hard was the training")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            savingDialog.show();
                                            Thread thread = new Thread(){
                                                @Override
                                                public void run() {
                                                    //Thread to save data while displaying saving dialog
                                                    Looper.prepare();
                                                    collection.remove(position);
                                                    actualData.remove(activeTraining);
                                                    dbHandler.completeActiveTraining(activeTraining, difficultyChooser.getDifficulty().toString());
                                                    actualData.remove(0);
                                                    actualData.add(0, new Message("You have " + Integer.toString(actualData.size())
                                                            + " Active Training", "Add more using the Training Generator"));
                                                    collection.remove(0);
                                                    collection.add(0, new Message("You have " + Integer.toString(collection.size())
                                                            + " Active Training", "Add more using the Training Generator"));
                                                    savingDialog.dismiss();
                                                    Looper.loop();
                                                }
                                            };
                                            thread.start();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Empty
                                        }
                                    })
                                    .setView(itemView);
                            builder.create().show();
                        }
                    });
                    activeTrainingViewHolder.menuButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Setup menu button
                            PopupMenu popupMenu = new PopupMenu(context, activeTrainingViewHolder.menuButton);
                            popupMenu.inflate(R.menu.active_training_menu);
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.active_training_complete:
                                            activeTrainingViewHolder.completeButton.callOnClick();
                                            //Call complete code
                                            break;
                                        case R.id.active_training_replace:
                                            //Delete active training
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                            builder1.setMessage("Are you sure you want to replace this training")
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            collection.remove(position);
                                                            actualData.remove(activeTraining);
                                                            dbHandler.deleteActiveTraining(activeTraining);
                                                            notifyDataSetChanged();
                                                            Intent intent = new Intent(context, TrainingGenerator.class);
                                                            intent.putExtra("AccountDetails", activeTraining.getAccountDetails());
                                                            context.startActivity(intent);
                                                        }
                                                    })
                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //Empty
                                                        }
                                                    });
                                            builder1.create().show();
                                            break;
                                        case R.id.active_training_delete:
                                            //Delete active training
                                            AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                                            builder2.setMessage("Are you sure you want to delete this training")
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            collection.remove(position);
                                                            actualData.remove(activeTraining);
                                                            dbHandler.deleteActiveTraining(activeTraining);
                                                            actualData.remove(0);
                                                            actualData.add(0, new Message("You have " + Integer.toString(actualData.size())
                                                                    + " Active Training", "Add more using the Training Generator"));
                                                            collection.remove(0);
                                                            collection.add(0, new Message("You have " + Integer.toString(collection.size())
                                                                    + " Active Training", "Add more using the Training Generator"));
                                                            notifyDataSetChanged();
                                                        }
                                                    })
                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //Empty
                                                        }
                                                    });
                                            builder2.create().show();
                                            break;
                                    }
                                    return false;
                                }
                            });
                            popupMenu.show();
                        }
                    });
                    break;
                }
            case HEADER:
                StringViewHolder stringViewHolder = (StringViewHolder) holder;
                if (object instanceof String){
                    String string = (String) object;
                    //Setting value
                    stringViewHolder.string.setText(string);
                }
                break;
            case PERSONALBEST:
                final PersonalBestViewHolder personalBestViewHolder = (PersonalBestViewHolder) holder;
                if (object instanceof  PersonalBest) {
                    final PersonalBest personalBest = (PersonalBest) object;
                    //Setting values
                    personalBestViewHolder.performance.setText("Performance: " + personalBest.getPerformance());
                    personalBestViewHolder.distance.setText(personalBest.getDistance());
                    if (personalBest.getRichDate().length() == 0 || personalBest.getDate() == null){
                        personalBestViewHolder.date.setText("Date Set: No Date");
                    } else {
                        personalBestViewHolder.date.setText("Date Set: " + personalBest.getRichDate());
                    }
                    personalBestViewHolder.username.setText(personalBest.getAccountDetails().getUsername());
                    if (personalBest.getPrediction().length() == 0) {
                        personalBestViewHolder.prediction.setHeight(0);
                    } else {
                        personalBestViewHolder.prediction.setText("Personal Best Prediction: " + personalBest.getPrediction());
                    }
                    personalBestViewHolder.accountName.setText(personalBest.getAccountDetails().getFirstName() + " " + personalBest.getAccountDetails().getLastName());

                    personalBestViewHolder.menuButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Setup menu button
                            PopupMenu popupMenu = new PopupMenu(context, personalBestViewHolder.menuButton);
                            popupMenu.inflate(R.menu.personal_best_menu);
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.personal_best_menu_delete:
                                            //Delete personal best
                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            builder.setTitle("Are you sure you want to delete this record")
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            collection.remove(position);
                                                            actualData.remove(personalBest);
                                                            dbHandler.deletePersonalBest(personalBest);
                                                            notifyDataSetChanged();
                                                        }
                                                    })
                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    });
                                            builder.create().show();
                                            break;
                                        case R.id.personal_best_menu_edit:
                                            //Edit personal best
                                            Intent intent = new Intent(context, EditActivity.class);
                                            intent.putExtra("AccountDetails", personalBest.getAccountDetails());
                                            intent.putExtra("Object", personalBest);
                                            context.startActivity(intent);
                                            break;
                                        case R.id.personal_best_menu_predict:
                                            //Predict personal best
                                            //Setup "Predicting" dialog
                                            builder = new AlertDialog.Builder(context);
                                            View view = View.inflate(context,R.layout.dialog_saving, null);
                                            TextView title = (TextView) view.findViewById(R.id.saving_title);
                                            title.setText("Predicting");
                                            builder.setView(view);
                                            builder.setCancelable(false);
                                            final Dialog dialog = builder.create();
                                            dialog.setCancelable(false);
                                            dialog.setCanceledOnTouchOutside(false);
                                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                @Override
                                                public void onDismiss(DialogInterface dialog) {
                                                    dbHandler.updatePersonalBest(personalBest);
                                                    notifyItemChanged(position);
                                                }
                                            });

                                            //Get distance array from resources
                                            final String[] distanceArray = context.getResources().getStringArray(R.array.distance_array);
                                            final String distance = personalBest.getDistance();

                                            dialog.show();

                                            Thread thread = new Thread(){
                                                @Override
                                                public void run() {
                                                    //Thread to calculate prediction while showing "Predicting" dialog
                                                    try {
                                                        Looper.prepare();
                                                        sleep(1000);
                                                        int i = StringManipulation.getPosition(distanceArray, distance);
                                                        //Current distance being predicted
                                                        int d;
                                                        int s;
                                                        //2 other distances that will be used to predict and compare

                                                        //Choosing 2 other distances based on the current distance
                                                        if (i == 0){
                                                            d = i +1;
                                                            s = i +2;
                                                            //If we are predicting 100m, then we must choose 200m and 400m
                                                            //as there is no distance below 100m in the distance array
                                                        } else if (i == distanceArray.length-1){
                                                            d = i -1;
                                                            s = i -2;
                                                            //If we are predicting Marathon, then we must choose Half Marathon and 10000m
                                                            //as there is no distance above Marathon in the distance array
                                                        } else {
                                                            d = i-1;
                                                            s = i+1;
                                                           //Default
                                                        }

                                                        double XA = (new RaceTime(dbHandler.getPersonalBest
                                                                (personalBest.getAccountDetails(), personalBest.getDistance())
                                                                .getPerformance())).getTimeInDouble();
                                                        //XA is our actual value for X or our personal best time for the given distance

                                                        double YA1;
                                                        double YA2;

                                                        try {
                                                            YA1 = (new RaceTime(dbHandler.getPersonalBest
                                                                    (personalBest.getAccountDetails(), distanceArray[d])
                                                                    .getPerformance())).getTimeInDouble();
                                                            //If a personal best exists for a distance d then YA1 defines that value
                                                        } catch (NullPointerException e){
                                                            e.printStackTrace();
                                                            YA1 = 0;
                                                            //Else YA1 is 0
                                                        }

                                                        try {
                                                            YA2 = (new RaceTime(dbHandler.getPersonalBest
                                                                    (personalBest.getAccountDetails(), distanceArray[s])
                                                                    .getPerformance())).getTimeInDouble();
                                                            //If a personal best exists for a distance s then YA2 defines that value
                                                        } catch (NullPointerException e){
                                                            e.printStackTrace();
                                                            YA2 = 0;
                                                            //Else YA2 is 0
                                                        }

                                                        RegressionLine regressionLine1 = dbHandler.getPersonalBestRegressionLine(distance, distanceArray[d]);
                                                        RegressionLine regressionLine2 = dbHandler.getPersonalBestRegressionLine(distance, distanceArray[s]);
                                                        //Must have data in database
                                                        //Initial data will be used from static database
                                                        //So regression lines are never null objects

                                                        double XP;
                                                        //XP is our predicted value for X or our predicted personal best for the given distance

                                                        if (YA1 == 0 && YA2 ==0){
                                                            XP =0;
                                                            //If no personal best exists for distance a d or s then no prediction can be made
                                                        } else {
                                                            if (YA1 == 0){
                                                                //If no personal best exists for a distance d
                                                                //Prediction made solely off YA2
                                                                XP = regressionLine2.getX(YA2);
                                                            } else if (YA2 == 0){
                                                                //If no personal best exists for a distance s
                                                                //Prediction made solely off YA1
                                                                XP = regressionLine1.getX(YA1);
                                                            } else {
                                                                //Prediction made off YA1 and YA2
                                                                double XP1 = regressionLine1.getX(YA1);
                                                                double XP2 = regressionLine2.getX(YA2);
                                                                //2 predictions for X generated

                                                                //Check which prediction is most accurate
                                                                //based on deviation from line in terms of standard deviations
                                                                double XDevs1 = Math.abs(XA - XP1)/regressionLine1.getStandardDeviation();
                                                                double XDevs2 = Math.abs(XA - XP2)/regressionLine2.getStandardDeviation();

                                                                XP = (XDevs1 * XP1)/(XDevs1 + XDevs2) + (XDevs2* XP2) / (XDevs1 + XDevs2);
                                                                //Create prediction based on XP1 and XP2
                                                                //the more accurate prediction is taken into account more
                                                            }
                                                        }

                                                        String prediction;

                                                        double multiplier = dbHandler.getAccountStats(personalBest.getAccountDetails()).getMultiplier();

                                                        if (XA < XP){
                                                            //If prediction is worse than the actual value then discard the prediction
                                                            prediction = new RaceTime(XA / ((multiplier/100) +1)).getTime(true,true);
                                                            //Multiplier is used to slightly modify the actual value making the prediction slightly quicker
                                                        } else {
                                                            prediction = new RaceTime(XP / ((multiplier/100) +1)).getTime(true ,true);
                                                            //Multiplier is used to slightly modify the prediction
                                                        }

                                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                                        if (!(prediction == null || prediction == "")) {
                                                            //Display prediction in dialog
                                                            View view = View.inflate(context, R.layout.dialog_prediction, null);
                                                            TextView distanceTV = (TextView) view.findViewById(R.id.personal_best_dialog_prediction_distance);
                                                            TextView predictionTV = (TextView) view.findViewById(R.id.personal_best_dialog_prediction_performance);
                                                            distanceTV.setText(distance);
                                                            predictionTV.setText("Prediction: " +prediction);
                                                            builder.setView(view);
                                                            personalBest.setPrediction(prediction);
                                                        } else {
                                                            //If XP=0 then no prediction so "Insufficient Data" dialog displayed
                                                            builder.setMessage("Insufficient Data");
                                                        }

                                                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                             //Empty
                                                            }
                                                        });
                                                        dialog.dismiss();
                                                        builder.create().show();
                                                        Looper.loop();
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };
                                            thread.start();
                                            break;
                                    }
                                    return false;
                                }
                            });
                            popupMenu.show();
                        }
                    });
                }
                break;
            case STATISTIC:
                StatisticViewHolder statisticViewHolder = (StatisticViewHolder) holder;
                if (object instanceof Statistic){
                    Statistic statistic = (Statistic) object;
                    //Setting values
                    statisticViewHolder.value.setText(Integer.toString(statistic.getValue()) + statistic.getUnits());
                    statisticViewHolder.valueName.setText(statistic.getValueName());
                }
                break;
            case TRAININGSET:
                final Training_SetViewHolder training_SetViewHolder = (Training_SetViewHolder) holder;
                if (object instanceof Training_Set){
                    final Training_Set training_Set = (Training_Set) object;
                    //Setting values
                    if (training_Set.getReps() ==1){
                        training_SetViewHolder.description.setText(training_Set.getDistance() + " Pace: " + training_Set.getPace());
                    } else {
                        training_SetViewHolder.description.setText(training_Set.getRepDesc());
                    }
                    training_SetViewHolder.totalTime.setText("Total Time: "+ new RaceTime(training_Set.getTotalTime()).getTime(true, false));
                    training_SetViewHolder.totalDistance.setText("Total Distance: "+ Integer.toString(training_Set.getTotalDistance())+ "m");
                    training_SetViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            PopupMenu menu = new PopupMenu(context, training_SetViewHolder.itemView);
                            menu.inflate(R.menu.list_view_menu);
                            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    //Popup menu on long press
                                    switch (item.getItemId()){
                                        case R.id.menu_delete:
                                            //Delete training set
                                            collection.remove(training_Set);
                                            actualData.remove(training_Set);
                                            notifyDataSetChanged();
                                            break;
                                        case R.id.menu_edit:
                                            //Edit Training set
                                            //Setup edit training set dialog
                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            View view = View.inflate(context, R.layout.dialog_add_training_set, null);
                                            final NumberPicker reps = (NumberPicker) view.findViewById(R.id.reps_picker);
                                            final NumberPicker distance = (NumberPicker) view.findViewById(R.id.distance_picker);
                                            final NumberPicker time = (NumberPicker) view.findViewById(R.id.time_picker);
                                            final NumberPicker restTime = (NumberPicker) view.findViewById(R.id.rest_picker);

                                            //Setting selector wheel arrays
                                            reps.setDisplayedValues(context.getResources().getStringArray(R.array.reps_array));
                                            distance.setDisplayedValues(context.getResources().getStringArray(R.array.set_distance_array));
                                            time.setDisplayedValues(context.getResources().getStringArray(R.array.set_pace_array));
                                            restTime.setDisplayedValues(context.getResources().getStringArray(R.array.set_rest_array));

                                            reps.setWrapSelectorWheel(true);
                                            distance.setWrapSelectorWheel(true);
                                            time.setWrapSelectorWheel(true);
                                            restTime.setWrapSelectorWheel(true);

                                            //Setting selector wheel min values to 0
                                            reps.setMinValue(0);
                                            distance.setMinValue(0);
                                            time.setMinValue(0);
                                            restTime.setMinValue(0);

                                            //Setting selector wheel max values
                                            reps.setMaxValue(context.getResources().getStringArray(R.array.reps_array).length -1);
                                            distance.setMaxValue(context.getResources().getStringArray(R.array.set_distance_array).length -1);
                                            time.setMaxValue(context.getResources().getStringArray(R.array.set_pace_array).length -1);
                                            restTime.setMaxValue(context.getResources().getStringArray(R.array.set_rest_array).length -1);

                                            //Setting values
                                            reps.setValue(StringManipulation.getPosition(context.getResources().getStringArray(R.array.reps_array),
                                                    Integer.toString(training_Set.getReps())));
                                            distance.setValue(StringManipulation.getPosition(context.getResources().getStringArray(R.array.set_distance_array),
                                                    Integer.toString(training_Set.getDistance()) + "m"));
                                            restTime.setValue(StringManipulation.getPosition(context.getResources().getStringArray(R.array.set_rest_array),
                                                    (new RaceTime(training_Set.getRestTime()).getTime(true,false))));
                                            time.setValue(StringManipulation.getPosition(context.getResources().getStringArray(R.array.set_pace_array),
                                                    training_Set.getPace()));

                                            Button button = (Button) view.findViewById(R.id.training_set_add);
                                            button.setText("Done");
                                            button.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    //Setting values on done click
                                                    training_Set.setReps(reps.getValue() + 1);
                                                    String distanceString = context.getResources().getStringArray(R.array.set_distance_array)[distance.getValue()];
                                                    training_Set.setDistance(Integer.parseInt(distanceString.substring(0, distanceString.length()-1)));
                                                    training_Set.setRestTime(RaceTime.simpleStringToSeconds(
                                                            context.getResources().getStringArray(R.array.set_rest_array)[restTime.getValue()]));
                                                    String timeString = context.getResources().getStringArray(R.array.set_pace_array)[time.getValue()];
                                                    training_Set.setDistanceTime((((RaceTime.simpleStringToSeconds(timeString.substring(0, timeString.length()-3)))* training_Set.getDistance()))/1000);
                                                    training_Set.notifyDescription();
                                                    notifyDataSetChanged();

                                                    dialog.dismiss();

                                                }
                                            });

                                            builder.setView(view);
                                            dialog = builder.create();
                                            dialog.show();
                                            break;
                                    }
                                    return false;
                                }
                            });
                            menu.show();
                            return false;
                        }
                    });
                }
                break;
            case MESSAGE:
                MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
                if (object instanceof  Message){
                    Message message = (Message) object;
                    //Setting values
                    switch (message.getBody()){
                        case "":
                            messageViewHolder.title.setText(message.getTitle());
                            messageViewHolder.body.setHeight(0);
                        default:
                            messageViewHolder.body.setText(message.getBody());
                            messageViewHolder.title.setText(message.getTitle());
                    }
                }
                break;
            case ADDTRAININGSET:
                AddTrainingSetViewHolder addTrainingSetViewHolder = (AddTrainingSetViewHolder) holder;
                if (object instanceof AddTrainingSet){
                    addTrainingSetViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Setup add training set dialog on object click
                            final Training_Set training_Set = new Training_Set();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View view = View.inflate(context, R.layout.dialog_add_training_set, null);
                            final NumberPicker reps = (NumberPicker) view.findViewById(R.id.reps_picker);
                            final NumberPicker distance = (NumberPicker) view.findViewById(R.id.distance_picker);
                            final NumberPicker time = (NumberPicker) view.findViewById(R.id.time_picker);
                            final NumberPicker restTime = (NumberPicker) view.findViewById(R.id.rest_picker);

                            //Setting selector wheel arrays
                            reps.setDisplayedValues(context.getResources().getStringArray(R.array.reps_array));
                            distance.setDisplayedValues(context.getResources().getStringArray(R.array.set_distance_array));
                            time.setDisplayedValues(context.getResources().getStringArray(R.array.set_pace_array));
                            restTime.setDisplayedValues(context.getResources().getStringArray(R.array.set_rest_array));

                            reps.setWrapSelectorWheel(true);
                            distance.setWrapSelectorWheel(true);
                            time.setWrapSelectorWheel(true);
                            restTime.setWrapSelectorWheel(true);

                            //Setting selector wheel min values to 0
                            reps.setMinValue(0);
                            distance.setMinValue(0);
                            time.setMinValue(0);
                            restTime.setMinValue(0);

                            //Setting selector wheel max values
                            reps.setMaxValue(context.getResources().getStringArray(R.array.reps_array).length -1);
                            distance.setMaxValue(context.getResources().getStringArray(R.array.set_distance_array).length -1);
                            time.setMaxValue(context.getResources().getStringArray(R.array.set_pace_array).length -1);
                            restTime.setMaxValue(context.getResources().getStringArray(R.array.set_rest_array).length -1);

                            //Setting initial values to 0
                            reps.setValue(0);
                            distance.setValue(0);
                            time.setValue(0);
                            restTime.setValue(0);

                            Button button = (Button) view.findViewById(R.id.training_set_add);
                            button.setText("Add");
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Adding a training set object to the collection
                                    //Setting values on done click
                                    training_Set.setReps(reps.getValue() + 1);
                                    String distanceString = context.getResources().getStringArray(R.array.set_distance_array)[distance.getValue()];
                                    training_Set.setDistance(Integer.parseInt(distanceString.substring(0, distanceString.length()-1)));
                                    training_Set.setRestTime(RaceTime.simpleStringToSeconds(
                                            context.getResources().getStringArray(R.array.set_rest_array)[restTime.getValue()]));
                                    String timeString = context.getResources().getStringArray(R.array.set_pace_array)[time.getValue()];
                                    training_Set.setDistanceTime((((RaceTime.simpleStringToSeconds(timeString.substring(0, timeString.length()-3)))* training_Set.getDistance()))/1000);
                                    training_Set.notifyDescription();
                                    collection.add(collection.size() - 1,training_Set);
                                    notifyDataSetChanged();

                                    dialog.dismiss();
                                }
                            });

                            builder.setView(view);
                            dialog = builder.create();
                            dialog.show();
                        }
                    });
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return collection.size();
    }

    @Override
    public int getItemViewType(int position) {
        //Method decides which ViewType to return
        //based on the instance of the object at the corresponding position
        Object object = collection.get(position);
        if (object instanceof Completed_Training){
            return TRAINING;
        } else if (object instanceof Race){
            return TRAINING;
        } else if (object instanceof HomePageItem){
            return HOMEPAGE;
        } else if (object instanceof Active_Training){
            return ACTIVETRAINING;
        } else if (object instanceof String){
            return HEADER;
        } else if (object instanceof PersonalBest){
            return PERSONALBEST;
        } else if (object instanceof Statistic){
            return STATISTIC;
        } else if (object instanceof Training_Set){
            return TRAININGSET;
        } else if (object instanceof  Message){
            return MESSAGE;
        } else if (object instanceof AddTrainingSet) {
            return ADDTRAININGSET;
        }else {
            return -1;
        }
    }

    private class TrainingViewHolder extends RecyclerView.ViewHolder {
        //Class for displaying the details of a Completed_Training or Race object
        TextView name;
        ImageView difficulty;
        TextView description;
        TextView date;
        TextView type;
        TextView menuButton;
        TextView accountName;
        TextView username;

        private TrainingViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.training_name);
            difficulty = (ImageView) itemView.findViewById(R.id.training_difficulty);
            description = (TextView) itemView.findViewById(R.id.training_description);
            date = (TextView) itemView.findViewById(R.id.date_completed);
            type = (TextView) itemView.findViewById(R.id.training_type);
            menuButton = (TextView) itemView.findViewById(R.id.item_options_menu);
            accountName = (TextView) itemView.findViewById(R.id.training_account_name);
            username = (TextView) itemView.findViewById(R.id.training_account_username);
        }
    }

    private class HomePageViewHolder extends RecyclerView.ViewHolder{
        //Class for displaying the details of a HomePageItem object
        TextView text;
        TextView description;
        ImageView image;

        private HomePageViewHolder(View itemView){
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.view_profile_week);
            description = (TextView) itemView.findViewById(R.id.view_profile_activity);
            image = (ImageView) itemView.findViewById(R.id.home_page_img);
        }
    }

    private class ActiveTrainingViewHolder extends RecyclerView.ViewHolder{
        //Class for displaying the details of a Active_Training object
        TextView totalTime;
        TextView totalDistance;
        TextView isCompleted;
        TextView description;
        TextView trainingType;
        TextView date;
        TextView menuButton;
        TextView accountName;
        TextView username;
        Button completeButton;

        private ActiveTrainingViewHolder(View itemView){
            super(itemView);
            totalTime = (TextView) itemView.findViewById(R.id.active_training_total_time);
            totalDistance = (TextView) itemView.findViewById(R.id.active_training_total_distance);
            isCompleted = (TextView) itemView.findViewById(R.id.active_training_iscompleted);
            description = (TextView) itemView.findViewById(R.id.active_training_desc);
            trainingType = (TextView) itemView.findViewById(R.id.active_training_type);
            date = (TextView) itemView.findViewById(R.id.active_training_date_issued);
            menuButton = (TextView) itemView.findViewById(R.id.active_training_options);
            completeButton = (Button) itemView.findViewById(R.id.active_training_complete_button);
            accountName = (TextView) itemView.findViewById(R.id.active_training_account_name);
            username = (TextView) itemView.findViewById(R.id.active_training_account_username);
        }
    }

    private class StringViewHolder extends RecyclerView.ViewHolder {
        //Class for displaying a string
        TextView string;

        private StringViewHolder(View itemView){
            super(itemView);
            string = (TextView) itemView.findViewById(R.id.header_text);
        }
    }

    private class PersonalBestViewHolder extends RecyclerView.ViewHolder{
        //Class for displaying the details of a Personal_Best object
        TextView distance;
        TextView prediction;
        TextView performance;
        TextView date;
        TextView menuButton;
        TextView accountName;
        TextView username;

        private PersonalBestViewHolder(View itemView){
            super(itemView);
            distance = (TextView) itemView.findViewById(R.id.personal_best_distance);
            performance = (TextView) itemView.findViewById(R.id.personal_best_time);
            date = (TextView) itemView.findViewById(R.id.personal_best_date);
            menuButton = (TextView) itemView.findViewById(R.id.personal_best_options);
            accountName = (TextView) itemView.findViewById(R.id.personal_best_account_name);
            username = (TextView) itemView.findViewById(R.id.personal_best_account_username);
            prediction = (TextView) itemView.findViewById(R.id.personal_best_prediction);
        }
    }

    private class StatisticViewHolder extends RecyclerView.ViewHolder{
        //Class for displaying the details of a Statistic object
        TextView value;
        TextView valueName;

        private  StatisticViewHolder(View itemView){
            super(itemView);
            value = (TextView) itemView.findViewById(R.id.statistic_value);
            valueName = (TextView) itemView.findViewById(R.id.statistic_name);
        }
    }

    private class Training_SetViewHolder extends RecyclerView.ViewHolder{
        //Class for displaying the details of a Training_Set object
        TextView description;
        TextView totalTime;
        TextView totalDistance;

        private Training_SetViewHolder(View itemView){
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.training_set_description);
            totalTime = (TextView) itemView.findViewById(R.id.training_set_time);
            totalDistance = (TextView) itemView.findViewById(R.id.training_set_distance);
        }
    }

    private class MessageViewHolder extends RecyclerView.ViewHolder {
        //Class for displaying the details of a Message object
        TextView title;
        TextView body;

        private MessageViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.message_title);
            body = (TextView) itemView.findViewById(R.id.message_body);
        }
    }

    private class AddTrainingSetViewHolder extends RecyclerView.ViewHolder {
        //Class for displaying a AddTrainingSet interface
        AddTrainingSetViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void filter(String query){
        //Method for filtering the displayed items based of a search query in a search view widget
        if ((Objects.equals(query, "")) || (query == null)) {
            collection = actualData;
        } else {
            ArrayList<Object> searchList = new ArrayList<Object>();
            for (int i = 0; i < actualData.size(); i++) {
                Object object = actualData.get(i);
                if (object instanceof Completed_Training) {
                    //Search by name
                    //Search by description
                    Completed_Training training = (Completed_Training) object;
                    ArrayList<Object> list = new ArrayList<>();
                    TrainingAdapter.getTrainingSetsFromString(training.getDescription(), list);
                    if (StringManipulation.typingEquals(query,training.getName()) ||
                            StringManipulation.typingEquals(query, TrainingAdapter.getDisplayDescription(list))) {
                        searchList.add(object);
                    }
                } else if (object instanceof Race) {
                    //Search by name
                    Race race = (Race) object;
                    if (StringManipulation.typingEquals(query, race.getName())) {
                        searchList.add(object);
                    }
                } else if (object instanceof  PersonalBest){
                    //Search by distance
                    PersonalBest personalBest = (PersonalBest) object;
                    if (StringManipulation.typingEquals(query,personalBest.getDistance())){
                        searchList.add(object);
                    }
                }
            }
            collection = searchList;
        }
        notifyDataSetChanged();
    }

}
