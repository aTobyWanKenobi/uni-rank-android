package com.example.albergon.unirank.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.albergon.unirank.Model.Indicator;
import com.example.albergon.unirank.Model.University;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This helper class provides methods to interact with a local SQLite database.
 *
 * It allows to copy a database present in the application assets to the default system folder. This
 * ensures that the university data can be initialized in the application from an external file
 * which can be substituted.
 *
 * Once initialized, the copied database can be opened and queried from the application. This is
 * done through the interface UniRankDatabase that this class implements. This ensures that future
 * modifications to the database structure will not affect the application behavior.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements UniRankDatabase {

    private Context context = null;
    private SQLiteDatabase db = null;

    private static String TAG = "DatabaseHelper";
    private static String DB_PATH = "/data/data/com.example.albergon.unirank/databases/";
    private static String DB_NAME = "uni_rank.db";

    /**
     * Public constructor that takes a Context as parameter and stores it in the proper field for
     * future usage. Then it simply calls the superclass constructor to initialize an
     * SQLiteOpenHelper.
     *
     * @param context caller's context
     */
    public DatabaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // nothing to do for now
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: drop database and recreate
    }

    /**
     * Check if database initialization from assets has already been done. If not, create an empty
     * database in the default system directory and copy the assets database in it.
     *
     * @throws IOException  propagated from copyDatabase()
     */
    public void createDatabase() throws IOException{

        if(databaseExists()) {
            Log.i(TAG, "Database already exists");
        } else {
            // TODO: remember to implement calls to this method as background service (long-running)
            // create empty database in default system folder
            this.getReadableDatabase();
            // overwrite it with desired database
            copyDatabase();
        }
    }

    // TODO: verify that it has been created?
    /**
     * Open local database in read mode.
     *
     * @throws SQLiteException propagated from openDatabase()
     */
    public void openDatabase() throws SQLiteException{

        db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
    }


    /**
     * Utility method to check if database initialization has already been performed.
     *
     * @return  true if it exists, false otherwise
     */
    private boolean databaseExists() {

        SQLiteDatabase DBCheck = null;

        try {
            DBCheck = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.i(TAG, "Database does not exist yet (exception : " + e.getMessage());
        }

        if(DBCheck != null) {
            DBCheck.close();
        }

        return DBCheck != null;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transferring byte stream.
     *
     * @throws IOException propagate from Stream method calls
     */
    private void copyDatabase() throws IOException {

        InputStream inputDB = context.getAssets().open(DB_NAME);

        OutputStream outputDB = new FileOutputStream(DB_PATH + DB_NAME);

        byte[] buffer = new byte[1024];
        int length;
        while((length = inputDB.read(buffer)) > 0) {
            outputDB.write(buffer, 0, length);
        }

        outputDB.flush();
        outputDB.close();
        inputDB.close();
    }

    /**
     * Retrieve a University from the database by its unique id.
     *
     * @param id    unique identifier for universities in the database
     * @return      a University object containing all the database information
     */
    @Override
    public University getUniversity(int id) {

        // specifies which database columns we want from the query
        String[] projection = {
                Tables.UniversitiesTable.UNI_NAME,
                Tables.UniversitiesTable.COUNTRY,
                Tables.UniversitiesTable.ACRONYM
        };

        // specifies the WHERE clause of the query
        String selection = Tables.UniversitiesTable._ID + " = " + id;

        // perform query in UNIVERSITIES table
        Cursor result = db.query(
                Tables.UniversitiesTable.TABLE_NAME,
                projection,
                selection,
                null,
                null,
                null,
                null);

        // check that we retrieved a unique university
        if(result.getCount() == 1) {
            result.moveToNext();
        } else if(result.getCount() == 0) {
            throw new NoSuchElementException("This id does not correspond to any university in database");
        } else {
            throw new IllegalStateException("More universities with same id, database is corrupted");
        }

        // build and return a University object
        String uniName = result.getString(result.getColumnIndexOrThrow(Tables.UniversitiesTable.UNI_NAME));
        String uniCountry = result.getString(result.getColumnIndexOrThrow(Tables.UniversitiesTable.COUNTRY));
        String uniAcronym = result.getString(result.getColumnIndexOrThrow(Tables.UniversitiesTable.ACRONYM));

        University fetchUni = (uniAcronym == null)?
                                new University(id, uniName, uniCountry):
                                new University(id, uniName, uniCountry, uniAcronym);

        return fetchUni;
    }

    /**
     * Retrieve an Indicator by its index in the IndicatorsList enumeration.
     *
     * @param id    index of the indicator
     * @return      an Indicator containing all the respective table data
     */
    @Override
    public Indicator getIndicator(int id) {

        // specifies which database columns we want from the query
        String[] projection = {
                Tables.IndicatorsList._ID,
                Tables.IndicatorsList.SCORE
        };

        // specifies the WHERE clause of the query
        Cursor result = db.query(
                Tables.IndicatorsList.values()[id].TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        // iterate through the result to build the Map containing all pairs university/score
        // build and return Indicator object
        if(result.getCount() > 0) {
            Map<University, Double> entries = new HashMap<>();
            while(result.moveToNext()) {
                int uniID = result.getInt(result.getColumnIndexOrThrow(Tables.IndicatorsList._ID));
                double uniScore = result.getDouble(result.getColumnIndexOrThrow(Tables.IndicatorsList.SCORE));
                University uni = getUniversity(uniID);
                entries.put(uni, uniScore);
            }

            return new Indicator(entries, id);
        } else {

            throw new IllegalStateException("Empty indicator table, database is corrupted");
        }
    }
}
