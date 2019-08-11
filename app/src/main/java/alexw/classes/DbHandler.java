package alexw.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by alexw on 9/29/2017.
 */

public class DbHandler extends SQLiteOpenHelper {

    public static final int DATA_BASE_VERSION = 1;
    public static final String DATA_BASE_NAME = "workingdb8.db";

    private SQLiteDatabase database;
    private Context context;

    public DbHandler(Context context) {
        super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create account details table
        db.execSQL("CREATE TABLE " + AccountDetailsTable.TABLE_NAME +" ( " +
        AccountDetailsTable.ACCOUNT_ID + " INTEGER PRIMARY KEY, " + AccountDetailsTable.FIRST_NAME + " TEXT, "+
        AccountDetailsTable.LAST_NAME + " TEXT, " + AccountDetailsTable.EMAIL + " TEXT, " + AccountDetailsTable.DATE_OF_BIRTH +
        " TEXT, " + AccountDetailsTable.GENDER + " TEXT, " + AccountDetailsTable.USERNAME + " TEXT, " +
        AccountDetailsTable.PASSWORD + " TEXT, " + AccountDetailsTable.RACE_DISTANCE + " TEXT);");

        //Create training table
        db.execSQL("CREATE TABLE " + TrainingTable.TABLE_NAME + " ( " +
                TrainingTable.TRAINING_ID + " INTEGER PRIMARY KEY, " + TrainingTable.ACCOUNT_ID + " INTEGER, " +
        TrainingTable.TRAINING_NAME + " TEXT, " + TrainingTable.TRAINING_DATE + " TEXT, " + TrainingTable.TRAINING_DESCRIPTION +
        " TEXT, " + TrainingTable.TRAINING_TYPE + " TEXT, " + TrainingTable.TRAINING_DIFFICULTY + " TEXT);");

        //Create race table
        db.execSQL("CREATE TABLE " + RaceTable.TABLE_NAME + " ( " + RaceTable.RUN_ID + " INTEGER PRIMARY KEY, " +
        RaceTable.ACCOUNT_ID + " INTEGER, " + RaceTable.RACE_NAME + " TEXT);");

        //Create personal best table
        db.execSQL("CREATE TABLE " + PersonalBestTable.TABLE_NAME + " ( " + PersonalBestTable.RUN_ID +
                " INTEGER PRIMARY KEY, " + PersonalBestTable.ACCOUNT_ID + " INTEGER, "
                + PersonalBestTable.PERSONAL_BEST_PREDICTION + " TEXT);");

        //Create active training table
        db.execSQL("CREATE TABLE " + ActiveTrainingTable.TABLE_NAME + " ( " + ActiveTrainingTable.ACTIVE_TRAINING_ID +
        " INTEGER PRIMARY KEY, " + ActiveTrainingTable.ACCOUNT_ID + " INTEGER, " + ActiveTrainingTable.ACTIVE_TRAINING_NAME
        + " TEXT, " + ActiveTrainingTable.ACTIVE_TRAINING_DATE + " TEXT, " + ActiveTrainingTable.ACTIVE_TRAINING_DESCRIPTION + " TEXT, "
        + ActiveTrainingTable.ACTIVE_TRAINING_TYPE + " TEXT);");

        //Create stats table
        db.execSQL("CREATE TABLE " + StatTable.TABLE_NAME + " ( " + StatTable.ACCOUNT_ID + " INTEGER PRIMARY KEY, "
                + StatTable.MULTIPLIER + " REAL, " + StatTable.V4SPEED + " REAL, " + StatTable.LACTATE_THRESHOLD +
                " REAL, " + StatTable.VO2MAX + " REAL, " + StatTable.RECOVERY_RATE + " REAL);");

        //Create login table
        db.execSQL("CREATE TABLE " + LoginTable.TABLE_NAME + " ( " + LoginTable.ACCOUNT_ID + " INTEGER PRIMARY KEY);");

        //Create run instance table
        db.execSQL("CREATE TABLE " + RunInstanceTable.TABLE_NAME + " ( " + RunInstanceTable.RUN_ID + " INTEGER PRIMARY KEY, " +
                RunInstanceTable.DISTANCE + " TEXT, " + RunInstanceTable.PERFORMANCE + " TEXT, "+ RunInstanceTable.DATE +" TEXT);");


        //Inserting initial static data
        StaticDataBaseAccessor accessor = new StaticDataBaseAccessor(context);

        //Opening initial static database
        try {
            accessor .createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        accessor .openDataBase();

        //Inserting initial static account details
        Cursor detailsCursor = accessor.staticAccountDetails();
        if (detailsCursor.getCount() > 0){
            for (int i = 0; i <detailsCursor.getCount() ; i++) {
                detailsCursor.moveToNext();
                db.execSQL("INSERT INTO " + AccountDetailsTable.TABLE_NAME + " VALUES(" +
                        detailsCursor.getInt(0) + ", '" + detailsCursor.getString(1) + "', '" + detailsCursor.getString(2) + "', '"
                        + detailsCursor.getString(3) + "', '"+ detailsCursor.getString(4) + "', '"+ detailsCursor.getString(5) +
                        "', '"+ detailsCursor.getString(6) + "', '"+ detailsCursor.getString(7) + "', '"+ detailsCursor.getString(8) + "');");
            }
        }
        detailsCursor.close();

        //Inserting initial static personal bests
        Cursor bestCursor = accessor.staticPersonalBests();
        if (bestCursor.getCount() > 0){
            for (int i = 0; i < bestCursor.getCount(); i++) {
                bestCursor.moveToNext();
                db.execSQL("INSERT INTO " + RunInstanceTable.TABLE_NAME + " VALUES(" + bestCursor.getInt(0) + ", '" +
                        bestCursor.getString(2) + "', '" + bestCursor.getString(3) + "', '"+ bestCursor.getString(4) +"');");
                db.execSQL("INSERT INTO " + PersonalBestTable.TABLE_NAME + " VALUES(" + bestCursor.getInt(0) + ", '" +
                        bestCursor.getString(1) + "', '" + bestCursor.getString(5) + "');");
            }
        }
        bestCursor.close();

        //Inserting initial static statistics
        Cursor statCursor = accessor.staticAccountStats();
        if (statCursor.getCount() > 0){
            for (int i = 0; i < statCursor.getCount(); i++) {
                statCursor.moveToNext();
                db.execSQL("INSERT INTO " + StatTable.TABLE_NAME + " VALUES(" + statCursor.getInt(0) + ", " + statCursor.getDouble(1)
                        + ", " + statCursor.getDouble(2)+ ", " + statCursor.getDouble(3)+ ", " + statCursor.getDouble(4)+ ", " +
                        statCursor.getDouble(5) + ");");
            }
        }
        statCursor.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AccountDetailsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrainingTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ActiveTrainingTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PersonalBestTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RaceTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + StatTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RunInstanceTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LoginTable.TABLE_NAME);
        onCreate(db);
    }

    public void updateLogin(Account_Details accountDetails){
        //Saving the account id of the account logged into the device
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + LoginTable.TABLE_NAME, null);
        ContentValues contentValues = new ContentValues();
        contentValues.put(LoginTable.ACCOUNT_ID, accountDetails.getAccountID());
        if (cursor.getCount() == 0){
            database.insert(LoginTable.TABLE_NAME, null ,contentValues);
        } else {
            cursor.moveToFirst();
            database.update(LoginTable.TABLE_NAME, contentValues, LoginTable.ACCOUNT_ID + " = ?",
                    new String[]{cursor.getString(0)});
        }

        cursor.close();
        database.close();
    }

    public void updateLogout(){
        //Removing the account id of the account that was just logged into the device
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + LoginTable.TABLE_NAME, null);
        if (cursor.getCount() > 0){
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                database.execSQL("DELETE FROM " + LoginTable.TABLE_NAME + " WHERE " + LoginTable.ACCOUNT_ID
                        + " = '" + cursor.getString(0) + "'");
            }
        }
        cursor.close();
        database.close();
    }

    public Account_Details getLoginAccount(){
        //Retrieving the account logged into the device
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + LoginTable.TABLE_NAME, null);
        if (cursor.getCount() == 0){
            database.close();
            cursor.close();
            return null;
        } else {
            cursor.moveToNext();
            Account_Details account_details = getAccountDetails(cursor.getString(0));
            database.close();
            cursor.close();
            return account_details;
        }
    }

    public boolean validLogin(String username, String password){
        //Checking if the login details are valid
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + AccountDetailsTable.TABLE_NAME +
                " WHERE " + AccountDetailsTable.USERNAME + " = '" + username + "' AND " +
                AccountDetailsTable.PASSWORD + " = '" + password + "'", null);

        Boolean b;
        if (cursor.getCount() == 1){
            b = true;
        } else {
            b = false;
        }
        database.close();
        cursor.close();
        return b;
    }

    public Account_Details getAccountDetails(String username, String password){
        //Retrieving the account details using username and password (username is also a unique identifier)
        if(validLogin(username, password)){
            String ID;
            database = this.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM " + AccountDetailsTable.TABLE_NAME +
                    " WHERE " + AccountDetailsTable.USERNAME + " = '" + username + "' AND " +
                    AccountDetailsTable.PASSWORD + " = '" + password + "'", null);
            cursor.moveToNext();
            ID = cursor.getString(0);
            database.close();
            cursor.close();
            return getAccountDetails(ID);
        } else {
            return null;
        }
    }

    public boolean emailNotInUse(String Email){
        //Checking if an email is not being used by another account
        this.database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + AccountDetailsTable.TABLE_NAME + " WHERE " +
                AccountDetailsTable.EMAIL + " = '" + Email + "'", null);
        if (cursor.getCount() > 0){
            cursor.close();
            database.close();
            return false;
        } else {
            cursor.close();
            database.close();
            return true;
        }
    }

    public boolean usernameNotInUse(String username){
        //Checking if a username is not being used by another account
        this.database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + AccountDetailsTable.TABLE_NAME + " WHERE " +
                AccountDetailsTable.USERNAME + " = '" + username + "'", null);
        if (cursor.getCount() > 0){
            cursor.close();
            database.close();
            return false;
        } else {
            cursor.close();
            database.close();
            return true;
        }
    }

    public String[] getForgottenPassword(String Email){
        //Getting the password of an account using the E-Mail (unique identifier)
        this.database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + AccountDetailsTable.TABLE_NAME + " WHERE " +
                AccountDetailsTable.EMAIL + " = '" + Email + "'", null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            String password = cursor.getString(7);
            String username = cursor.getString(6);
            cursor.close();
            database.close();
            return new String[]{password, username};
        } else {
            cursor.close();
            database.close();
            return null;
        }
    }

    public void createAccount(Account_Details accountDetails){
        //Creating a new account using an account details object
        insertAccount(accountDetails);
        String myID = (getAccountDetails(accountDetails.getUsername(), accountDetails.getPassword())).getAccountID();
        // code to check email is not already in use

        //code using regression model to generate stats
        //these are default values for creating an account stats table
        insertStats(new Account_Stats(myID, 1, 4, 4, 55, 0.1));
    }

    private void insertStats(Account_Stats accountStats){
        database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(StatTable.ACCOUNT_ID, accountStats.getID());
        contentValues.put(StatTable.MULTIPLIER, accountStats.getMultiplier());
        contentValues.put(StatTable.V4SPEED, accountStats.getV4Speed());
        contentValues.put(StatTable.LACTATE_THRESHOLD, accountStats.getLactateThreshold());
        contentValues.put(StatTable.VO2MAX, accountStats.getVO2Max());
        contentValues.put(StatTable.RECOVERY_RATE, accountStats.getRecoveryRate());


        database.insert(StatTable.TABLE_NAME, null, contentValues);
        database.close();
    }

    public void updateStats(Account_Stats accountStats){
        database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(StatTable.ACCOUNT_ID, accountStats.getID());
        contentValues.put(StatTable.MULTIPLIER, accountStats.getMultiplier());
        contentValues.put(StatTable.V4SPEED, accountStats.getV4Speed());
        contentValues.put(StatTable.LACTATE_THRESHOLD, accountStats.getLactateThreshold());
        contentValues.put(StatTable.VO2MAX, accountStats.getVO2Max());
        contentValues.put(StatTable.RECOVERY_RATE, accountStats.getRecoveryRate());

        database.update(StatTable.TABLE_NAME, contentValues, StatTable.ACCOUNT_ID + " = ?",
                new String[]{accountStats.getID()});
        database.close();
    }

    public Account_Stats getAccountStats(Account_Details accountDetails){
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + StatTable.TABLE_NAME + " WHERE " +
                StatTable.ACCOUNT_ID + " = '" + accountDetails.getAccountID() + "'", null);
        if (cursor.getCount() > 0){
            cursor.moveToNext();
            Account_Stats accountStats = new Account_Stats(cursor.getString(0), cursor.getDouble(1), cursor.getDouble(2),
                    cursor.getDouble(3), cursor.getDouble(4), cursor.getDouble(5));
            database.close();
            cursor.close();
            return accountStats;
        } else {
            database.close();
            cursor.close();
            return null;
        }
    }

    public AthleteLactateModel getLactateModel(Account_Details accountDetails){
        //Creating an athlete lactate model object from an account stats object retreived form the database
        Account_Stats stats = getAccountStats(accountDetails);
        if (!(stats == null)){
            return new AthleteLactateModel(stats.getV4Speed(),
                    stats.getLactateThreshold(), stats.getVO2Max(), stats.getRecoveryRate());
        } else {
            return null;
        }

    }

    private void deleteStats(Account_Details accountDetails){
        database = this.getReadableDatabase();
        database.execSQL("DELETE FROM " + StatTable.TABLE_NAME + " WHERE " +
                StatTable.ACCOUNT_ID + " = '" + accountDetails.getAccountID() + "'");
        database.close();
    }

    private void insertAccount(Account_Details accountDetails){
        database = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(AccountDetailsTable.FIRST_NAME , accountDetails.getFirstName());
        contentValues.put(AccountDetailsTable.LAST_NAME , accountDetails.getLastName());
        contentValues.put(AccountDetailsTable.EMAIL , accountDetails.getEMail());
        contentValues.put(AccountDetailsTable.DATE_OF_BIRTH , accountDetails.getBirthDate());
        contentValues.put(AccountDetailsTable.GENDER , accountDetails.getGender());
        contentValues.put(AccountDetailsTable.USERNAME , accountDetails.getUsername());
        contentValues.put(AccountDetailsTable.PASSWORD , accountDetails.getPassword());
        contentValues.put(AccountDetailsTable.RACE_DISTANCE, accountDetails.getRaceDistance());

        database.insert(AccountDetailsTable.TABLE_NAME, null, contentValues);
        database.close();
    }

    public void updateAccount(Account_Details accountDetails){
        database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(AccountDetailsTable.FIRST_NAME , accountDetails.getFirstName());
        contentValues.put(AccountDetailsTable.LAST_NAME , accountDetails.getLastName());
        contentValues.put(AccountDetailsTable.EMAIL , accountDetails.getEMail());
        contentValues.put(AccountDetailsTable.DATE_OF_BIRTH , accountDetails.getBirthDate());
        contentValues.put(AccountDetailsTable.GENDER , accountDetails.getGender());
        contentValues.put(AccountDetailsTable.USERNAME , accountDetails.getUsername());
        contentValues.put(AccountDetailsTable.PASSWORD , accountDetails.getPassword());
        contentValues.put(AccountDetailsTable.RACE_DISTANCE, accountDetails.getRaceDistance());

        database.update(AccountDetailsTable.TABLE_NAME, contentValues, AccountDetailsTable.ACCOUNT_ID + " = ?",
                new String[]{accountDetails.getAccountID()});
        database.close();

    }

    public void updateAccountPersonalBest(Account_Details accountDetails, String performance){
        //Updating the primary race distance personal best of an account
        PersonalBest personalBest = getAccountPersonalBest(accountDetails);
        if (personalBest == null) {
            personalBest = new PersonalBest("", accountDetails, accountDetails.getRaceDistance(),
                    performance, "", "");
            insertPersonalBest(personalBest);
        } else {
            personalBest.setPerformance(performance);
            updatePersonalBest(personalBest);
        }

        RegressionLine[] lines = getAthleteStatsRegressionLine(accountDetails.getRaceDistance());
        double perf = new RaceTime(performance).getTimeInDouble();
        Account_Stats stats = new Account_Stats(accountDetails.getAccountID(), getAccountStats(accountDetails).getMultiplier(),
                lines[0].getY(perf), lines[1].getY(perf), lines[2].getY(perf),lines[3].getY(perf));
        updateStats(stats);
    }

    private RegressionLine[] getAthleteStatsRegressionLine(String raceDistance){
        //Creating a regression line using multiple athletes' personal best time of a given distance and their account stats
        this.database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + PersonalBestTable.TABLE_NAME + "."+ PersonalBestTable.ACCOUNT_ID +
                ", "+ RunInstanceTable.TABLE_NAME + "." + RunInstanceTable.PERFORMANCE +
                " FROM "+ PersonalBestTable.TABLE_NAME + ", "+ RunInstanceTable.TABLE_NAME + " WHERE "+
                RunInstanceTable.TABLE_NAME+"."+ RunInstanceTable.DISTANCE+ " = '" + raceDistance + "'" + " AND " +
                RunInstanceTable.TABLE_NAME + "." + RunInstanceTable.RUN_ID + " = " + PersonalBestTable.TABLE_NAME +"."+
                PersonalBestTable.RUN_ID, null);

        double[] personalBests = new double[cursor.getCount()];
        double[] V4Speeds = new double[cursor.getCount()];
        double[] lactateThresholds = new double[cursor.getCount()];
        double[] VO2Maxes = new double[cursor.getCount()];
        double[] recoveryRates = new double[cursor.getCount()];

        String[] accountIDs = new String[cursor.getCount()];

        if (cursor.getCount() > 0){
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                accountIDs[i] = cursor.getString(0);
                personalBests[i] = new RaceTime(cursor.getString(1)).getTimeInDouble();
            }
        }
        cursor.close();
        database.close();

        for (int i = 0; i < accountIDs.length ; i++) {

            Account_Details accountDetails = getAccountDetails(accountIDs[i]);
            Account_Stats accountStats = getAccountStats(accountDetails);

            V4Speeds[i] = accountStats.getV4Speed();
            lactateThresholds[i] = accountStats.getLactateThreshold();
            VO2Maxes[i] = accountStats.getVO2Max();
            recoveryRates[i] = accountStats.getRecoveryRate();

        }

        return new RegressionLine[]
                {MathFunc.createRegressionLine(personalBests, V4Speeds),
                        MathFunc.createRegressionLine(personalBests, lactateThresholds),
                        MathFunc.createRegressionLine(personalBests, VO2Maxes),
                        MathFunc.createRegressionLine(personalBests, recoveryRates)};

    }

    private void deleteAccount(Account_Details accountDetails){
        database = this.getReadableDatabase();
        database.execSQL("DELETE FROM " + AccountDetailsTable.TABLE_NAME + " WHERE " +
                AccountDetailsTable.ACCOUNT_ID + " = '" + accountDetails.getAccountID() + "'");
        database.close();
    }

    public void insertTraining(Completed_Training training){
        database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(TrainingTable.ACCOUNT_ID, training.getAccountDetails().getAccountID());
        contentValues.put(TrainingTable.TRAINING_NAME, training.getName());
        contentValues.put(TrainingTable.TRAINING_DATE, training.getDate());
        contentValues.put(TrainingTable.TRAINING_DESCRIPTION, training.getDescription());
        contentValues.put(TrainingTable.TRAINING_TYPE, training.getTrainingType());
        contentValues.put(TrainingTable.TRAINING_DIFFICULTY, training.getDifficulty());

        database.insert(TrainingTable.TABLE_NAME, null, contentValues);
        database.close();
    }

    public void updateTraining(Completed_Training training){
        database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(TrainingTable.ACCOUNT_ID, training.getAccountDetails().getAccountID());
        contentValues.put(TrainingTable.TRAINING_NAME, training.getName());
        contentValues.put(TrainingTable.TRAINING_DATE, training.getDate());
        contentValues.put(TrainingTable.TRAINING_DESCRIPTION, training.getDescription());
        contentValues.put(TrainingTable.TRAINING_TYPE, training.getTrainingType());
        contentValues.put(TrainingTable.TRAINING_DIFFICULTY, training.getDifficulty());

        database.update(TrainingTable.TABLE_NAME, contentValues, TrainingTable.TRAINING_ID + " = ?",
                new String[]{training.getID()});
        database.close();
    }

    public void deleteTraining(Completed_Training training){
        database = this.getReadableDatabase();
        database.execSQL("DELETE FROM " + TrainingTable.TABLE_NAME + " WHERE " +
                TrainingTable.TRAINING_ID + " = '" + training.getID() + "'");
        database.close();
    }

    public void insertRace(Race race){
        database = this.getReadableDatabase();

        //Insert run instance
        ContentValues contentValues = new ContentValues();
        contentValues.put(RunInstanceTable.DISTANCE, race.getDistance());
        contentValues.put(RunInstanceTable.PERFORMANCE, race.getPerformance());
        contentValues.put(RunInstanceTable.DATE, race.getDate());

        long id = database.insert(RunInstanceTable.TABLE_NAME, null, contentValues);

        //Insert race info
        database.execSQL("INSERT INTO " + RaceTable.TABLE_NAME + " VALUES(" + id + ", '" +
                race.getAccountDetails().getAccountID() + "', '" + race.getName() + "');");
        database.close();

        race.setID(Long.toString(id));
        //Insert personal best object
        insertPersonalBestObject(race.createPersonalBestObject());
    }

    public void updateRace(Race race){
        database = this.getReadableDatabase();

        //update run instance
        database.execSQL("UPDATE "+ RunInstanceTable.TABLE_NAME + " SET "+ RunInstanceTable.DISTANCE + " = '" +
                race.getDistance() +"', "+ RunInstanceTable.PERFORMANCE + " = '" + race.getPerformance() + "', "
                + RunInstanceTable.DATE +" = '"+ race.getDate() + "' "+ " WHERE " +
                RunInstanceTable.RUN_ID + " = " + race.getID()+ ";");

        //Update race info
        database.execSQL("UPDATE "+ RaceTable.TABLE_NAME + " SET "+ RaceTable.RACE_NAME + " = '" +
                race.getName() + "' " + " WHERE " +
                RunInstanceTable.RUN_ID + " = " + race.getID()+ ";");
        database.close();

        //Insert personal best object
        insertPersonalBestObject(race.createPersonalBestObject());
    }

    public PersonalBest getPersonalBest(Account_Details accountDetails, String distance){
        //Returns personal best of a given distance and a given account
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + PersonalBestTable.TABLE_NAME + "."+ PersonalBestTable.RUN_ID +
                ", " + PersonalBestTable.TABLE_NAME + "." + PersonalBestTable.ACCOUNT_ID + ", " + RunInstanceTable.TABLE_NAME + "." +
                RunInstanceTable.DISTANCE + ", " + RunInstanceTable.TABLE_NAME + "." + RunInstanceTable.PERFORMANCE + ", "+
                RunInstanceTable.TABLE_NAME +"."+RunInstanceTable.DATE +", "+PersonalBestTable.TABLE_NAME+"."+PersonalBestTable.PERSONAL_BEST_PREDICTION+" FROM "+ PersonalBestTable.TABLE_NAME + ", "
                + RunInstanceTable.TABLE_NAME + " WHERE "+
                RunInstanceTable.TABLE_NAME+"."+ RunInstanceTable.DISTANCE+ " = '" + distance + "'" + " AND " +
                RunInstanceTable.TABLE_NAME + "." + RunInstanceTable.RUN_ID + " = " + PersonalBestTable.TABLE_NAME +"."+
                PersonalBestTable.RUN_ID + " AND " + PersonalBestTable.TABLE_NAME + "."+PersonalBestTable.ACCOUNT_ID + " = '" +
                accountDetails.getAccountID() + "'", null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            PersonalBest personalBest = new PersonalBest(cursor.getString(0), accountDetails ,cursor.getString(2),cursor.getString(3),
                    cursor.getString(4),cursor.getString(5));
            database.close();
            cursor.close();
            return personalBest;
        } else {
            database.close();
            cursor.close();
            return null;
        }
    }

    public void deleteRace(Race race){
        database = this.getReadableDatabase();
        database.execSQL("DELETE FROM " + RaceTable.TABLE_NAME + " WHERE " +
        RaceTable.RUN_ID + " = '" + race.getID() + "'");
        database.execSQL("DELETE FROM " + RunInstanceTable.TABLE_NAME + " WHERE " +
                RunInstanceTable.RUN_ID + " = '" + race.getID() + "'");
        database.execSQL("DELETE FROM " + PersonalBestTable.TABLE_NAME + " WHERE " +
                PersonalBestTable.RUN_ID + " = '" + race.getID() + "'");
        database.close();
    }

    public void insertPersonalBest(PersonalBest personalBest){
        PersonalBest originalBest = getPersonalBest(personalBest.getAccountDetails(), personalBest.getDistance());
        if (originalBest == null){
            //If personal best for this distance doesn't exist then insert
            database = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(RunInstanceTable.DISTANCE, personalBest.getDistance());
            contentValues.put(RunInstanceTable.PERFORMANCE, personalBest.getPerformance());
            contentValues.put(RunInstanceTable.DATE, personalBest.getDate());

            long id = database.insert(RunInstanceTable.TABLE_NAME,null, contentValues);

            database.execSQL("INSERT INTO " + PersonalBestTable.TABLE_NAME + " VALUES(" + id + ", '" +
                    personalBest.getAccountDetails().getAccountID() + "', '" + personalBest.getPrediction() + "');");

            database.close();
        } else {
            RaceTime raceTime = new RaceTime(personalBest.getPerformance());
            RaceTime bestTime = new RaceTime(originalBest.getPerformance());
            if (raceTime.isQuicker(bestTime)){
                //If personal best for this distance does exist and is slower then update
                originalBest.setPerformance(personalBest.getPerformance());
                updatePersonalBest(originalBest);
            }
                //do nothing
        }
    }

    private void insertPersonalBestObject(PersonalBest personalBest){
        PersonalBest originalBest = getPersonalBest(personalBest.getAccountDetails(), personalBest.getDistance());
        database = this.getReadableDatabase();
        if (originalBest == null){
            //If personal best for this distance doesn't exist then insert
            database.execSQL("INSERT INTO " + PersonalBestTable.TABLE_NAME + " VALUES(" + personalBest.getID() + ", '" +
                    personalBest.getAccountDetails().getAccountID() + "', '" + personalBest.getPrediction() + "');");
        } else {
            RaceTime raceTime = new RaceTime(personalBest.getPerformance());
            RaceTime bestTime = new RaceTime(originalBest.getPerformance());
            if (raceTime.isQuicker(bestTime)){
                //If personal best for this distance does exist and is slower then delete and insert
                database.execSQL("DELETE FROM " + PersonalBestTable.TABLE_NAME + " WHERE " +
                        PersonalBestTable.RUN_ID + " = '" + originalBest.getID() + "'");
                database.execSQL("INSERT INTO " + PersonalBestTable.TABLE_NAME + " VALUES(" + personalBest.getID() + ", '" +
                        personalBest.getAccountDetails().getAccountID() + "', '" + personalBest.getPrediction() + "');");
            }
            //do nothing
        }
        database.close();
    }

    public void updatePersonalBest(PersonalBest personalBest){
        database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM "+ RaceTable.TABLE_NAME + ", "+ RunInstanceTable.TABLE_NAME +
                " WHERE "+ RaceTable.TABLE_NAME+"."+RaceTable.RUN_ID + " = '" + personalBest.getID()+"' AND "+
                RunInstanceTable.TABLE_NAME+"."+RunInstanceTable.DISTANCE + " = '"+ personalBest.getDistance()+"'", null);

        if (cursor.getCount() == 0){
            //If personal best run instance isn't associated the a race then update
            database.execSQL("UPDATE "+ RunInstanceTable.TABLE_NAME + " SET "+ RunInstanceTable.DISTANCE + " = '" +
                    personalBest.getDistance() +"', "+ RunInstanceTable.PERFORMANCE + " = '" + personalBest.getPerformance() + "', "
                    + RunInstanceTable.DATE +" = '"+ personalBest.getDate() + "' "+ " WHERE " +
                    RunInstanceTable.RUN_ID + " = " + personalBest.getID()+ ";");

            database.execSQL("UPDATE "+ PersonalBestTable.TABLE_NAME + " SET "+ PersonalBestTable.PERSONAL_BEST_PREDICTION + " = '" +
                    personalBest.getPrediction() + "' " + " WHERE " +
                    PersonalBestTable.RUN_ID + " = " + personalBest.getID()+ ";");
        } else {
            //If personal best run instance is associated with a race then delete and insert
            database.execSQL("DELETE FROM " + PersonalBestTable.TABLE_NAME + " WHERE " +
                    PersonalBestTable.RUN_ID + " = '" + personalBest.getID() + "'");

            //Insert new run instance
            ContentValues contentValues = new ContentValues();
            contentValues.put(RunInstanceTable.DISTANCE, personalBest.getDistance());
            contentValues.put(RunInstanceTable.PERFORMANCE, personalBest.getPerformance());
            contentValues.put(RunInstanceTable.DATE, personalBest.getDate());

            long id = database.insert(RunInstanceTable.TABLE_NAME,null, contentValues);

            database.execSQL("INSERT INTO " + PersonalBestTable.TABLE_NAME + " VALUES(" + id + ", '" +
                    personalBest.getAccountDetails().getAccountID() + "', '" + personalBest.getPrediction() + "');");
        }
        database.close();

        if (Objects.equals(personalBest.getAccountDetails().getRaceDistance(), personalBest.getDistance())){
            //If personal best is the primary race distance then update stats for the account
            RegressionLine[] lines = getAthleteStatsRegressionLine(personalBest.getAccountDetails().getRaceDistance());
            double perf = new RaceTime(personalBest.getPerformance()).getTimeInDouble();
            Account_Stats stats = new Account_Stats(personalBest.getAccountDetails().getAccountID(),
                    getAccountStats(personalBest.getAccountDetails()).getMultiplier(),
                    lines[0].getY(perf), lines[1].getY(perf), lines[2].getY(perf),lines[3].getY(perf));
            updateStats(stats);
        }
    }

    public void refreshPersonalBest(Account_Details accountDetails){
        //Checking if any races in the database for a given account
        //are faster than their personal best at that distance
        //Amending the problem if existent
        ArrayList<Object> races = getAllRaces(accountDetails);
        Race race;
        for (int i = 0; i < races.size(); i++) {
            if (races.get(i) instanceof  Race) {
                race = (Race) races.get(i);
                updateRace(race);
            }
        }
    }

    public void deletePersonalBest(PersonalBest personalBest){
        database = this.getReadableDatabase();
        database.execSQL("DELETE FROM " + RaceTable.TABLE_NAME + " WHERE " +
                RaceTable.RUN_ID + " = '" + personalBest.getID() + "'");
        database.execSQL("DELETE FROM " + RunInstanceTable.TABLE_NAME + " WHERE " +
                RunInstanceTable.RUN_ID + " = '" + personalBest.getID() + "'");
        database.execSQL("DELETE FROM " + PersonalBestTable.TABLE_NAME + " WHERE " +
                PersonalBestTable.RUN_ID + " = '" + personalBest.getID() + "'");
        database.close();
    }

    public void insertActiveTraining(Active_Training activeTraining){
        database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(ActiveTrainingTable.ACCOUNT_ID, activeTraining.getAccountDetails().getAccountID());
        contentValues.put(ActiveTrainingTable.ACTIVE_TRAINING_NAME, activeTraining.getName());
        contentValues.put(ActiveTrainingTable.ACTIVE_TRAINING_DATE, activeTraining.getDateIssued());
        contentValues.put(ActiveTrainingTable.ACTIVE_TRAINING_DESCRIPTION, activeTraining.getDescription());
        contentValues.put(ActiveTrainingTable.ACTIVE_TRAINING_TYPE, activeTraining.getTrainingType());

        database.insert(ActiveTrainingTable.TABLE_NAME, null, contentValues);
        database.close();
    }

    public boolean spaceForActiveTraining(Account_Details accountDetails){
        //Checking if the account has less than 5 active trainings in the database
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM "+ ActiveTrainingTable.TABLE_NAME + " WHERE " +
                ActiveTrainingTable.ACCOUNT_ID + " = '" + accountDetails.getAccountID() + "'", null);
        if (cursor.getCount() >= 5){
            cursor.close();
            database.close();
            return false;
        } else {
            cursor.close();
            database.close();
            return true;
        }
    }

    public void completeActiveTraining(Active_Training activeTraining, String difficulty){
        insertTraining(activeTraining.completeTraining(difficulty));
        deleteActiveTraining(activeTraining);
    }

    public void deleteActiveTraining(Active_Training activeTraining){
        database = this.getReadableDatabase();
        database.execSQL("DELETE FROM "+ ActiveTrainingTable.TABLE_NAME + " WHERE " +
        ActiveTrainingTable.ACTIVE_TRAINING_ID + " = '" + activeTraining.getID() + "'");
        database.close();
    }

    public ArrayList<Object> getAllTrainings(Account_Details accountDetails){
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TrainingTable.TABLE_NAME + " WHERE " +
                TrainingTable.ACCOUNT_ID + " = '" + accountDetails.getAccountID() + "'", null);
        Completed_Training training;
        ArrayList<Object> list = new ArrayList<>();
        if (cursor.getCount() > 0){
            for (int i = 0; i < cursor.getCount() ; i++) {
                cursor.moveToNext();
                training = new Completed_Training(cursor.getString(0),accountDetails,cursor.getString(2)
                        ,cursor.getString(3),cursor.getString(4), cursor.getString(5), cursor.getString(6));
                list.add(training);
            }
        }

        cursor.close();
        database.close();
        return list;
    }

    public ArrayList<Object> getAllRaces(Account_Details accountDetails){
        //Returns all Races associated to a given account
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT "+ RunInstanceTable.TABLE_NAME + "."+
                RunInstanceTable.RUN_ID + ", "+ RaceTable.TABLE_NAME+"."+RaceTable.ACCOUNT_ID +", "+
                RaceTable.TABLE_NAME+"."+RaceTable.RACE_NAME+ ", "+ RunInstanceTable.TABLE_NAME+"."+
                RunInstanceTable.DATE + ", "+ RunInstanceTable.TABLE_NAME+"."+RunInstanceTable.PERFORMANCE+
                ", "+ RunInstanceTable.TABLE_NAME+"."+RunInstanceTable.DISTANCE+" FROM " + RaceTable.TABLE_NAME+", "+RunInstanceTable.TABLE_NAME
                + " WHERE " +RaceTable.TABLE_NAME+"."+RaceTable.ACCOUNT_ID + " = '" + accountDetails.getAccountID() + "' AND " +
                RunInstanceTable.TABLE_NAME+"."+RunInstanceTable.RUN_ID +" = "+RaceTable.TABLE_NAME+"."+RaceTable.RUN_ID, null);
        Race race;
        ArrayList<Object> list = new ArrayList<>();
        if (cursor.getCount() > 0){
            for (int i = 0; i < cursor.getCount() ; i++) {
                cursor.moveToNext();
                race = new Race(cursor.getString(0), accountDetails, cursor.getString(2)
                        , cursor.getString(3), "", cursor.getString(4), cursor.getString(5));
                list.add(race);
            }
        }
        cursor.close();
        database.close();
        return list;
    }

    public ArrayList<Object> getAllActiveTrainings(Account_Details accountDetails){
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + ActiveTrainingTable.TABLE_NAME + " WHERE " +
                ActiveTrainingTable.ACCOUNT_ID + " = '" + accountDetails.getAccountID() + "'", null);
        Active_Training activeTraining;
        ArrayList<Object> list = new ArrayList<>();
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                ArrayList<Object> trainingSets = new ArrayList<>();
                TrainingAdapter.getTrainingSetsFromString(cursor.getString(4), trainingSets);
                activeTraining = new Active_Training(cursor.getString(0), accountDetails, cursor.getString(2)
                        , cursor.getString(3), trainingSets.toArray(), cursor.getString(5));
                list.add(activeTraining);
            }
        }

        cursor.close();
        database.close();
        return list;
    }

    public ArrayList<Object> getAllPersonalBest(Account_Details accountDetails){
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + PersonalBestTable.TABLE_NAME + "."+ PersonalBestTable.RUN_ID +
                ", " + PersonalBestTable.TABLE_NAME + "." + PersonalBestTable.ACCOUNT_ID + ", " + RunInstanceTable.TABLE_NAME + "." +
                RunInstanceTable.DISTANCE + ", " + RunInstanceTable.TABLE_NAME + "." + RunInstanceTable.PERFORMANCE + ", " +
                RunInstanceTable.TABLE_NAME +"."+RunInstanceTable.DATE + ", " + PersonalBestTable.TABLE_NAME + "."+
                PersonalBestTable.PERSONAL_BEST_PREDICTION +
                " FROM "+ PersonalBestTable.TABLE_NAME + ", "
                + RunInstanceTable.TABLE_NAME + " WHERE "+
                RunInstanceTable.TABLE_NAME + "." + RunInstanceTable.RUN_ID + " = " + PersonalBestTable.TABLE_NAME +"."+
                PersonalBestTable.RUN_ID + " AND " + PersonalBestTable.TABLE_NAME + "."+PersonalBestTable.ACCOUNT_ID + " = '" +
                accountDetails.getAccountID() + "'", null);
        PersonalBest personalBest;
        ArrayList<Object> list = new ArrayList<>();
        if (cursor.getCount() > 0){
            for (int i = 0; i < cursor.getCount() ; i++) {
                cursor.moveToNext();
                personalBest = new PersonalBest(cursor.getString(0),accountDetails,cursor.getString(2)
                        ,cursor.getString(3),cursor.getString(4), cursor.getString(5));
                list.add(personalBest);
            }
        }

        cursor.close();
        database.close();
        return list;
    }

    public Account_Details getAccountDetails(String ID){
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM "+ AccountDetailsTable.TABLE_NAME + " WHERE "
        + AccountDetailsTable.ACCOUNT_ID + " = '" + ID + "'", null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            Account_Details account_details = new Account_Details(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)
                    ,cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8));
            cursor.close();
            database.close();
            return account_details;
        } else {
            cursor.close();
            database.close();
            return null;
        }
    }

    public PersonalBest getAccountPersonalBest(Account_Details accountDetails){
        //Returns the primary race distance personal best of a given account
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + PersonalBestTable.TABLE_NAME + "."+ PersonalBestTable.RUN_ID +
                ", " + PersonalBestTable.TABLE_NAME + "." + PersonalBestTable.ACCOUNT_ID + ", " + RunInstanceTable.TABLE_NAME + "." +
                RunInstanceTable.DISTANCE + ", " + RunInstanceTable.TABLE_NAME + "." + RunInstanceTable.PERFORMANCE + ", "+
                RunInstanceTable.TABLE_NAME +"."+RunInstanceTable.DATE + ", " + PersonalBestTable.TABLE_NAME+"." +PersonalBestTable.PERSONAL_BEST_PREDICTION +
                " FROM "+ PersonalBestTable.TABLE_NAME + ", " + RunInstanceTable.TABLE_NAME + " WHERE "+
                RunInstanceTable.TABLE_NAME+"."+ RunInstanceTable.DISTANCE+ " = '" + accountDetails.getRaceDistance() + "'" + " AND " +
                RunInstanceTable.TABLE_NAME + "." + RunInstanceTable.RUN_ID + " = " + PersonalBestTable.TABLE_NAME +"."+
                PersonalBestTable.RUN_ID + " AND " + PersonalBestTable.TABLE_NAME + "."+PersonalBestTable.ACCOUNT_ID + " = '" +
                accountDetails.getAccountID() + "'", null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            PersonalBest personalBest = new PersonalBest(cursor.getString(0), accountDetails ,cursor.getString(2),cursor.getString(3),
                    cursor.getString(4),cursor.getString(5));
            database.close();
            cursor.close();
            return personalBest;
        } else {
            database.close();
            cursor.close();
            return null;
        }
    }

    public int[] getAllTimeActivity(Account_Details accountDetails){
        //Returns the numbers of trainings and distance run of a given account
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM "+ TrainingTable.TABLE_NAME + " WHERE " +
        TrainingTable.ACCOUNT_ID + " = '" + accountDetails.getAccountID() + "'", null);

        int[] integer = new int[2];
        integer[0] = cursor.getCount();

        int distance = 0;
        String description;

        if (cursor.getCount() > 0){
            for (int i = 0; i < cursor.getCount() ; i++) {
                cursor.moveToNext();
                description = cursor.getString(4);
                distance = distance + TrainingAdapter.getTotalDistanceFromString(description);
            }
        }

        integer[1] = distance;

        cursor.close();
        database.close();
        return integer;
    }

    public int[] getAvgWeeklyActivity(Account_Details accountDetails){
        //Returns the average amount of trainings, distance and time run each week for a given account
        Date oldestDate = TrainingAdapter.getOldestTraining(getAllTrainings(accountDetails));
        long weeks = (StringManipulation.dateDifference(oldestDate.makeDate()) / 7);
        if (weeks < 1){
            weeks = 1;
        }
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM "+ TrainingTable.TABLE_NAME + " WHERE " +
                TrainingTable.ACCOUNT_ID + " = '" + accountDetails.getAccountID() + "'", null);



        int[] integer = new int[3];
        integer[0] = (int)  Math.round(cursor.getCount() / weeks);

        int distance = 0;
        int time = 0;
        String description;

        if (cursor.getCount() > 0){
            for (int i = 0; i < cursor.getCount() ; i++) {
                cursor.moveToNext();
                description = cursor.getString(4);
                distance = distance + TrainingAdapter.getTotalDistanceFromString(description);
                time = time + TrainingAdapter.getTotalTimeFromString(description);
            }
        }

        integer[1] = (int) Math.round(distance / weeks);
        integer[2] = (int) Math.round(time / weeks);

        cursor.close();
        database.close();
        return integer;
    }

    public int[] getPastYearActivity(Account_Details accountDetails){
        //Returns the amount of trainings, distance and time run in the last year of a given account
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM "+ TrainingTable.TABLE_NAME + " WHERE " +
                TrainingTable.ACCOUNT_ID + " = '" + accountDetails.getAccountID() + "'", null);

        int[] integer = new int[3];
        int total = 0;
        int time = 0;
        int distance = 0;
        String description;

        if (cursor.getCount() > 0){
            for (int i = 0; i < cursor.getCount() ; i++) {
                cursor.moveToNext();
                if (StringManipulation.isWithinAYear(cursor.getString(3))) {
                    description = cursor.getString(4);
                    distance = distance + TrainingAdapter.getTotalDistanceFromString(description);
                    time = time + TrainingAdapter.getTotalTimeFromString(description);
                    total = total + 1;
                }
            }
        }

        integer[0] = total;
        integer[1] = distance;
        integer[2] = time;

        cursor.close();
        database.close();
        return integer;
    }

    public int[] getPastWeekActivity(Account_Details accountDetails){
        //Returns the amount of trainings, distance and time run in the last week of a given account
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM "+ TrainingTable.TABLE_NAME + " WHERE " +
                TrainingTable.ACCOUNT_ID + " = '" + accountDetails.getAccountID() + "'", null);

        int[] integer = new int[3];
        int total = 0;
        int time = 0;
        int distance = 0;
        String description;

        if (cursor.getCount() > 0){
            for (int i = 0; i < cursor.getCount() ; i++) {
                cursor.moveToNext();
                if (StringManipulation.isWithinAWeek(cursor.getString(3))) {
                    description = cursor.getString(4);
                    distance = distance + TrainingAdapter.getTotalDistanceFromString(description);
                    total = total + 1;
                    time = time + TrainingAdapter.getTotalTimeFromString(description);
                }
            }
        }

        integer[0] = total;
        integer[1] = distance;
        integer[2] = time;

        cursor.close();
        database.close();
        return integer;
    }

    public RegressionLine getPersonalBestRegressionLine(String distance1, String distance2){
        //Creates a regression line between 2 given distances
        double[] distance1Array = new double[0];
        double[] distance2Array = new double[0];
        int count = 0;
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + PersonalBestTable.TABLE_NAME + "."+ PersonalBestTable.RUN_ID +
                ", " + PersonalBestTable.TABLE_NAME + "." + PersonalBestTable.ACCOUNT_ID + ", " + RunInstanceTable.TABLE_NAME + "." +
                RunInstanceTable.DISTANCE + ", " + RunInstanceTable.TABLE_NAME + "." + RunInstanceTable.PERFORMANCE + ", "+
                RunInstanceTable.TABLE_NAME +"."+RunInstanceTable.DATE + ", " + PersonalBestTable.TABLE_NAME+"." +PersonalBestTable.PERSONAL_BEST_PREDICTION +
                " FROM "+ PersonalBestTable.TABLE_NAME + ", " + RunInstanceTable.TABLE_NAME + " WHERE "+
                RunInstanceTable.TABLE_NAME+"."+ RunInstanceTable.DISTANCE+ " = '" + distance1 + "'" + " AND " +
                RunInstanceTable.TABLE_NAME + "." + RunInstanceTable.RUN_ID + " = " + PersonalBestTable.TABLE_NAME +"."+
                PersonalBestTable.RUN_ID , null);

        ArrayList<PersonalBest> list = new ArrayList<PersonalBest>();

        PersonalBest personalBest;

        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                personalBest = new PersonalBest(cursor.getString(0), getAccountDetails(cursor.getString(1)),
                        cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
                list.add(personalBest);
            }
        }

        cursor.close();
        database.close();

        database = this.getReadableDatabase();
        Cursor cursor1 = database.rawQuery("SELECT " + PersonalBestTable.TABLE_NAME + "."+ PersonalBestTable.RUN_ID +
                ", " + PersonalBestTable.TABLE_NAME + "." + PersonalBestTable.ACCOUNT_ID + ", " + RunInstanceTable.TABLE_NAME + "." +
                RunInstanceTable.DISTANCE + ", " + RunInstanceTable.TABLE_NAME + "." + RunInstanceTable.PERFORMANCE + ", "+
                RunInstanceTable.TABLE_NAME +"."+RunInstanceTable.DATE + ", " + PersonalBestTable.TABLE_NAME+"." +PersonalBestTable.PERSONAL_BEST_PREDICTION +
                " FROM "+ PersonalBestTable.TABLE_NAME + ", " + RunInstanceTable.TABLE_NAME + " WHERE "+
                RunInstanceTable.TABLE_NAME+"."+ RunInstanceTable.DISTANCE+ " = '" + distance2 + "'" + " AND " +
                RunInstanceTable.TABLE_NAME + "." + RunInstanceTable.RUN_ID + " = " + PersonalBestTable.TABLE_NAME +"."+
                PersonalBestTable.RUN_ID , null);

        if (cursor1.getCount() > 0){
            for (int i = 0; i < cursor1.getCount() ; i++) {
                cursor1.moveToNext();
                String accountID = cursor1.getString(1);
                for (int j = 0; j < list.size() ; j++) {
                    if (Objects.equals(accountID, list.get(j).getAccountDetails().getAccountID())){
                        distance1Array = MathFunc.redefineArray(distance1Array, distance1Array.length + 1);
                        distance1Array[count] = new RaceTime(list.get(j).getPerformance()).getTimeInDouble();
                        distance2Array = MathFunc.redefineArray(distance2Array, distance2Array.length +1);
                        distance2Array[count] = new RaceTime(cursor1.getString(3)).getTimeInDouble();
                        count = count + 1;
                        break;
                    }
                }
            }
        }

        database.close();
        cursor1.close();

        return MathFunc.createRegressionLine(distance1Array, distance2Array);
    }

    public DiscreteDistribution getTrainingTypeDistribution(Account_Details accountDetails, Training.TrainingType type, String[] distanceArray){
        //Returns a discrete distribution of distances to run based on a training type and a given account's history
        this.database = getReadableDatabase();
        double[] frequency = new double[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        Cursor cursor = database.rawQuery("SELECT * FROM " + TrainingTable.TABLE_NAME + " WHERE " + TrainingTable.ACCOUNT_ID +
                " = '" + accountDetails.getAccountID() + "' AND " + TrainingTable.TRAINING_TYPE + " = '" +
                TrainingAdapter.getStringFromType(type) + "'", null);
        if (cursor.getCount() >= 1){
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                ArrayList<Object> sets = new ArrayList<Object>();
                TrainingAdapter.getTrainingSetsFromString(cursor.getString(4), sets);
                for (int j = 0; j < sets.size(); j++) {
                    if (sets.get(j) instanceof Training_Set) {
                        Training_Set set = (Training_Set) sets.get(j);
                        frequency[StringManipulation.getPosition(distanceArray, Integer.toString(set.getDistance())+ "m")]++;
                    }
                }
            }
            optimizeTypeProbability(type,accountDetails,frequency);
            double s = MathFunc.arrayTotal(frequency);
            for (int i = 0; i < frequency.length; i++) {
                frequency[i] = frequency[i] / (s);
            }
            cursor.close();
            database.close();
            return new DiscreteDistribution(new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26},
                    frequency);
        } else {
            cursor.close();
            database.close();
            return getTypeDistribution(accountDetails, type);
        }
    }

    public DiscreteDistribution getTrackSetsDistribution(Account_Details accountDetails){
        //Returns a discrete distribution of numbers of training sets for a track training calculated from a given account's history
        this.database = getReadableDatabase();
        double numTrainings = 0;
        double[] frequency = new double[]{0,0,0,0};
        Cursor cursor = database.rawQuery("SELECT * FROM " + TrainingTable.TABLE_NAME + " WHERE " + TrainingTable.ACCOUNT_ID +
                " = '" + accountDetails.getAccountID() + "' AND " + TrainingTable.TRAINING_TYPE + " = '" +
                TrainingAdapter.getStringFromType(Training.TrainingType.Track) + "'", null);
        if (cursor.getCount() >= 1){
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                ArrayList<Object> sets = new ArrayList<Object>();
                TrainingAdapter.getTrainingSetsFromString(cursor.getString(4), sets);
                numTrainings++;
                frequency[sets.size()-1]++;
            }
            numTrainings = numTrainings + ((double) frequency.length);
            for (int i = 0; i < frequency.length; i++) {
                frequency[i] = (frequency[i] + 1) / (numTrainings);
                //add probability to values taht dont have any frequency
            }
            cursor.close();
            database.close();
            return new DiscreteDistribution(new int[]{1,2,3,4},
                    frequency);
        } else {
            cursor.close();
            database.close();
            return new DiscreteDistribution(new int[]{1,2,3,4}, new double[]{0.4,0.3,0.2,0.1});
        }
    }

    public double[][][] getDistanceRepsDistribution(Account_Details accountDetails, String[] distanceArray){
        //Returns a 3 dimensional array of number of sets of training sets against distance against number of reps
        //Information is based on a given account's hostory
        this.database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TrainingTable.TABLE_NAME + " WHERE " + TrainingTable.ACCOUNT_ID +
        " = '" + accountDetails.getAccountID() + "' AND " + TrainingTable.TRAINING_TYPE + " = '"
                + TrainingAdapter.getStringFromType(Training.TrainingType.Track) + "'", null);
        double[][][] frequency = new double[4][27][30];

        if (cursor.getCount() >= 1){
            cursor.moveToNext();
            ArrayList<Object> sets = new ArrayList<Object>();
            TrainingAdapter.getTrainingSetsFromString(cursor.getString(4), sets);
            for (int i = 0; i < sets.size() ; i++) {
                if (sets.get(i) instanceof  Training_Set){
                    Training_Set set = (Training_Set) sets.get(i);
                    frequency[sets.size()-1][StringManipulation.getPosition(distanceArray, Integer.toString(set.getDistance())+ "m")][set.getReps()-1]++;
                }
            }
        }
        for (int i = 0; i < 4 ; i++) {
            double[] defaultDistribution = getDefaultRepDistribution(i +1);
            for (int j = 0; j < 27; j++) {
                for (int k = 0; k < 30; k++) {
                    frequency[i][j][k] = frequency[i][j][k] + 10* defaultDistribution[k];
                }
            }
        }
        for (int i = 0; i < 4 ; i++) {
            for (int j = 0; j < 27; j++) {
                double[] probability = frequency[i][j];
                double d = MathFunc.arrayTotal(probability);
                for (int k = 0; k < 30; k++) {
                    frequency[i][j][k] = frequency[i][j][k]/d;
                }
            }
        }
        cursor.close();
        database.close();
        return frequency;
    }

    public double[][] getRestDistanceDistribution(Account_Details accountDetails, String[] distanceArray, String[] restArray){
        //Returns a 2 dimensional array of distance against rest based on an account's history
        this.database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TrainingTable.TABLE_NAME + " WHERE " + TrainingTable.ACCOUNT_ID +
                " = '" + accountDetails.getAccountID() + "' AND " + TrainingTable.TRAINING_TYPE + " = '"
                + TrainingAdapter.getStringFromType(Training.TrainingType.Track) + "'", null);
        double[][] frequency = new double[27][21];
        if (cursor.getCount() >= 1){
            cursor.moveToNext();
            ArrayList<Object> sets = new ArrayList<Object>();
            TrainingAdapter.getTrainingSetsFromString(cursor.getString(4), sets);
            for (int i = 0; i < sets.size() ; i++) {
                if (sets.get(i) instanceof  Training_Set){
                    Training_Set set = (Training_Set) sets.get(i);
                    frequency[StringManipulation.getPosition(distanceArray, Integer.toString(set.getDistance())+ "m")]
                            [StringManipulation.getPosition(restArray, (new RaceTime(set.getRestTime())).getTime(true,false))]++;
                }
            }
        }
        double[] defaultDistribution = getDefaultRestDistribution();
        for (int i = 0; i <27 ; i++) {
            for (int j = 0; j < 21; j++) {
                frequency[i][j] = frequency[i][j] + 10* defaultDistribution[j];
            }
        }
        for (int i = 0; i < 27; i++) {
            double d = MathFunc.arrayTotal(frequency[i]);
            for (int j = 0; j < 21; j++) {
                frequency[i][j] = frequency[i][j]/ d;
            }
        }
        cursor.close();
        database.close();
        return frequency;
    }

    private double[] getDefaultRestDistribution(){
        //Returns a default rest distribution for all types of athletes
        return new double[]{0,0,0.05,0,0,0.2,0,0,0.15,0.2,0.1,0.1,0.05,0.05,0,0.1,0,0,0,0,0};
    }

    private double[] getDefaultRepDistribution(int sets){
        //Returns a default rep distribution based on the number of sets in a training
        switch (sets) {
            case 1:
                return new double[]{0,0,0.05,0.05,0.05,0.1,0,0.25,0,0.2,0,0.15,0,0,0.15,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
            case 2:
                return new double[]{0,0,0.1,0.1,0.2,0.2,0,0.2,0,0.1,0,0.05,0,0,0.05,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
            case 3:
                return new double[]{0,0,0.15,0.25,0.25,0.2,0,0.1,0,0.05,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
            case 4:
                return new double[]{0,0,0.3,0.4,0.2,0.1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
            default:
                return new double[]{0,0,0.05,0.05,0.05,0.1,0,0.25,0,0.2,0,0.15,0,0,0.15,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        }
    }

    private void optimizeTypeProbability(Training.TrainingType type, Account_Details details, double[] frequency){
        //Method adds probability to distances that have no frequency to create more randomness
        if (type == Training.TrainingType.Track){
            double[] defaultDistribution = getDefaultTrackDistribution(details).getPXisx();

            for (int i = 0; i < frequency.length; i++) {
                if (defaultDistribution[i] > 0){
                    frequency[i] = frequency[i] + (100*defaultDistribution[i]);
                }
            }
        } else if (type == Training.TrainingType.Fartlek){
            for (int i = 0; i < frequency.length; i++) {
                if (i >= 18){
                    frequency[i]++;
                }
            }
        } else if (type == Training.TrainingType.RecoveryRun){
            for (int i = 0; i < frequency.length; i++) {
                if (i >= 18){
                    frequency[i]++;
                }
            }
        } else if (type == Training.TrainingType.LongRun){
            for (int i = 0; i < frequency.length; i++) {
                if (i >= 18){
                    frequency[i]++;
                }
            }
        } else if (type == Training.TrainingType.TempoRun){
            for (int i = 0; i < frequency.length; i++) {
                if (i >= 16){
                    frequency[i]++;
                }
            }
        } else if (type == Training.TrainingType.ProgressionRun){
            for (int i = 0; i < frequency.length; i++) {
                if (i >= 16){
                    frequency[i]++;
                }
            }
        } else if (type == Training.TrainingType.HillReps){
            for (int i = 0; i < frequency.length; i++) {
                if (i <= 5){
                    frequency[i]++;
                }
            }
        }

    }

    private DiscreteDistribution getTypeDistribution(Account_Details details, Training.TrainingType type){
        //Returns a default distance distribution based on a given training type and account
        switch(type){
            case Track:
                return getDefaultTrackDistribution(details);
            case Fartlek:
                return new DiscreteDistribution(new int[]{18,19,20,21,22,23,24,25,26});
            case RecoveryRun:
                return new DiscreteDistribution(new int[]{18,19,20,21,22,23,24,25,26});
            case LongRun:
                return new DiscreteDistribution(new int[]{18,19,20,21,22,23,24,25,26});
            case TempoRun:
                return new DiscreteDistribution(new int[]{16,17,18,19,20,21,22,23,24,25,26});
            case ProgressionRun:
                return new DiscreteDistribution(new int[]{16,17,18,19,20,21,22,23,24,25,26});
            case HillReps:
                return new DiscreteDistribution(new int[]{0,1,2,3,4,5});
            default:
                return new DiscreteDistribution(
                        new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26},
                        new double[]{0,0,0,0,0.3,0.4,0.1,0.2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});

        }
    }

    private DiscreteDistribution getDefaultTrackDistribution(Account_Details details){
        //Returns a default distance distribution for track trainings based on a given accounts primary race distance
        int[] intArray = new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26};
        switch (details.getRaceDistance()){
            case "100m":
                return new DiscreteDistribution(intArray,
                        new double[]{0.2,0.2,0.2,0.15,0.1,0.1,0.05,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
            case "200m":
                return new DiscreteDistribution(intArray,
                        new double[]{0.05,0.2,0.2,0.2,0.1,0.1,0.15,0,0,0,0,0,0,0,0.,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
            case "400m":
                return new DiscreteDistribution(intArray,
                        new double[]{0.05,0.05,0.05,0.3,0.05,0.2,0.3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
            case "1500m":
                return new DiscreteDistribution(intArray,
                        new double[]{0,0,0,0.05,0,0.1,0.15,0.1,0.1,0.2,0.13,0.08,0.05,0.01,0.03,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
            case "3000m Steeple":
                return new DiscreteDistribution(intArray,
                        new double[]{0,0,0,0.02,0,0,0.1,0.02,0.02,0.1,0.2,0.1,0.05,0.1,0.2,0.02,0.2,0.1,0.05,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
            case "800m":
                return new DiscreteDistribution(intArray,
                        new double[]{0.01,0.01,0.02,0.15,0.02,0.1,0.15,0.13,0.13,0.1,0.1,0.05,0.01,0.01,0.01,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
            case "5000m":
                return new DiscreteDistribution(intArray,
                        new double[]{0,0,0,0,0,0,0,0,0,0.05,0.05,0.05,0.05,0.1,0.2,0,0.2,0.1,0.2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
            case "10000":
                return new DiscreteDistribution(intArray,
                        new double[]{0,0,0,0,0,0,0,0,0,0.05,0.05,0.05,0.05,0.1,0.2,0,0.2,0.1,0.2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
            case "Half Marathon":
                return new DiscreteDistribution(intArray,
                        new double[]{0,0,0,0,0,0,0,0,0,0.05,0.05,0.05,0.05,0.1,0.2,0,0.2,0.1,0.2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
            case "Marathon":
                return new DiscreteDistribution(intArray,
                        new double[]{0,0,0,0,0,0,0,0,0,0.05,0.05,0.05,0.05,0.1,0.2,0,0.2,0.1,0.2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
            default:
                return new DiscreteDistribution(intArray,
                        new double[]{0.01,0.01,0.02,0.15,0.02,0.1,0.15,0.13,0.13,0.1,0.1,0.05,0.01,0.01,0.01,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
        }
    }

    public ArrayList<Object> getPastMonthTrainings(Account_Details accountDetails){
        //Returns all trainings within the past month
        ArrayList<Object> trainings = getAllTrainings(accountDetails);

        for (int i = 0; i < trainings.size() ; i++) {
            if (trainings.get(i) instanceof Completed_Training){
                Completed_Training training = (Completed_Training) trainings.get(i);
                if (!(StringManipulation.isWithinAMonth(training.getDate()))) {
                    trainings.remove(training);
                }
            }
        }
        return trainings;
    }

    public ArrayList<Object> getPastWeekTrainings(Account_Details accountDetails){
        //Returns all trainings within the past week
        ArrayList<Object> trainings = getAllTrainings(accountDetails);

        for (int i = 0; i < trainings.size() ; i++) {
            if (trainings.get(i) instanceof Completed_Training){
                Completed_Training training = (Completed_Training) trainings.get(i);
                if (!(StringManipulation.isWithinAWeek(training.getDate()))) {
                    trainings.remove(training);
                }
            }
        }
        return trainings;
    }

    public static abstract class AccountDetailsTable implements BaseColumns{
        public static final String TABLE_NAME = "AccountDetails";
        public static final String ACCOUNT_ID = "AccountID";
        public final static String FIRST_NAME = "FirstName";
        public final static String LAST_NAME = "LastName";
        public final static String USERNAME = "Username";
        public final static String PASSWORD = "Password";
        public final static String GENDER = "Gender";
        public final static String EMAIL = "Email";
        public final static String DATE_OF_BIRTH = "DateOfBirth";
        public static final String RACE_DISTANCE = "RaceDistance";
    }

    public static abstract class TrainingTable implements BaseColumns{
        public static final String TABLE_NAME = "Training";
        public static final String TRAINING_ID = "TrainingID";
        public static final String ACCOUNT_ID = "AccountID";
        public static final String TRAINING_NAME = "TrainingName";
        public static final String TRAINING_DATE = "TrainingDate";
        public static final String TRAINING_DESCRIPTION = "TrainingDescription";
        public static final String TRAINING_TYPE = "TrainingType";
        public static final String TRAINING_DIFFICULTY = "TrainingDifficulty";
    }

    public static abstract class ActiveTrainingTable implements BaseColumns{
        public static final String TABLE_NAME = "ActiveTraining";
        public static final String ACTIVE_TRAINING_ID = "ActiveTrainingID";
        public static final String ACCOUNT_ID = "AccountID";
        public static final String ACTIVE_TRAINING_NAME = "ActiveTrainingName";
        public static final String ACTIVE_TRAINING_DATE = "ActiveTrainingDate";
        public static final String ACTIVE_TRAINING_DESCRIPTION = "ActiveTrainingDescription";
        public static final String ACTIVE_TRAINING_TYPE = "ActiveTrainingType";

    }

    public static abstract class PersonalBestTable implements BaseColumns{
        public static final String TABLE_NAME = "PersonalBest";
        public static final String RUN_ID = "RunID";
        public static final String ACCOUNT_ID = "AccountID";
        public static final String PERSONAL_BEST_PREDICTION = "PersonalBestPrediction";
    }

    public static abstract class RaceTable implements BaseColumns{
        public static final String TABLE_NAME = "Race";
        public static final String RUN_ID = "RunID";
        public static final String ACCOUNT_ID = "AccountID";
        public static final String RACE_NAME = "RaceName";
    }

    public static abstract class StatTable implements BaseColumns {
        public static final String TABLE_NAME = "Stats";
        public static final String ACCOUNT_ID = "AccountID";
        public static final String MULTIPLIER = "Multiplier";
        public static final String V4SPEED = "V4Speed";
        public static final String LACTATE_THRESHOLD = "LactateThreshold";
        public static final String VO2MAX = "VO2Max";
        public static final String RECOVERY_RATE = "RecoveryRate";
    }

    public static abstract class LoginTable implements BaseColumns {
        public static final String TABLE_NAME = "Login";
        public static final String ACCOUNT_ID = "AccountID";
    }

    public static abstract class RunInstanceTable implements BaseColumns {
        public static final String TABLE_NAME = "RunInstance";
        public static final String RUN_ID = "RunID";
        public static final String DISTANCE = "Distance";
        public static final String PERFORMANCE = "Performance";
        public static final String DATE = "Date";
    }
}
