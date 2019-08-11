package alexw.classes;

/**
 * Created by alexw on 11/8/2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class StaticDataBaseAccessor extends SQLiteOpenHelper {

    //Class for accessing static database values in assets folder
    String DB_PATH = null;
    private static String DB_NAME = "staticdb.db";
    //Name of static database to be found in assets folder
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public StaticDataBaseAccessor(Context context) {
        //Constructor
        super(context, DB_NAME, null, 10);
        this.myContext = context;
        this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
        Log.e("Path 1", DB_PATH);
    }

    public void createDataBase() throws IOException {
        //Creates database locally if it doesn't exist
        boolean dbExist = checkDataBase();
        if (!dbExist) {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        //Checks database exists
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            //empty
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    private void copyDataBase() throws IOException {
        //Copies static database values from assets folder into newly created local database
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[10];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public Cursor staticAccountDetails(){
        return myDataBase.rawQuery("SELECT * FROM " + AccountDetailsTable.TABLE_NAME, null);
    }

    public Cursor staticPersonalBests(){
        return myDataBase.rawQuery("SELECT * FROM " + PersonalBestTable.TABLE_NAME, null);
    }

    public Cursor staticAccountStats(){
        return myDataBase.rawQuery("SELECT * FROM " + StatTable.TABLE_NAME, null);
    }

    static abstract class PersonalBestTable implements BaseColumns {
        static final String TABLE_NAME = "PersonalBest";
    }

    static abstract class StatTable implements BaseColumns {
        static final String TABLE_NAME = "Stats";

    }

    static abstract class AccountDetailsTable implements BaseColumns{
        static final String TABLE_NAME = "AccountDetails";
    }
}