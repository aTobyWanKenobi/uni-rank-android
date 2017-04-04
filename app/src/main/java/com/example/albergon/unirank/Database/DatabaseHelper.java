package com.example.albergon.unirank.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.albergon.unirank.Model.Indicator;
import com.example.albergon.unirank.Model.SaveRank;
import com.example.albergon.unirank.Model.University;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    private final static String TAG = "DatabaseHelper";
    @SuppressLint("SdCardPath")
    private final static String DB_PATH = "/data/data/com.example.albergon.unirank/databases/";
    private final static String DB_NAME = "uni_rank.db";

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
            this.getWritableDatabase();
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
    public boolean databaseExists() {

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

        // check arguments
        if(id < 0) {
            throw new IllegalArgumentException("University id cannot be negative");
        }

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

        // close Cursor
        result.close();

        return (uniAcronym == null)?
                new University(id, uniName, uniCountry):
                new University(id, uniName, uniCountry, uniAcronym);
    }

    /**
     * Retrieve an Indicator by its index in the IndicatorsList enumeration.
     *
     * @param id    index of the indicator
     * @return      an Indicator containing all the respective table data
     */
    @Override
    public Indicator getIndicator(int id) {

        // check arguments
        if(id < 0) {
            throw new IllegalArgumentException("University id cannot be negative");
        }

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
            Map<Integer, Double> entries = new HashMap<>();
            while(result.moveToNext()) {
                int uniID = result.getInt(result.getColumnIndexOrThrow(Tables.IndicatorsList._ID));
                double uniScore = result.getDouble(result.getColumnIndexOrThrow(Tables.IndicatorsList.SCORE));
                entries.put(uniID, uniScore);
            }

            // close Cursor
            result.close();

            return new Indicator(entries, id);
        } else {

            // close Cursor
            result.close();

            throw new IllegalStateException("Empty indicator table, database is corrupted");
        }
    }

    @Override
    public void saveAggregation(SaveRank ranking) {

        // check arguments
        if(ranking == null) {
            throw new IllegalArgumentException("Ranking to be saved cannot be null");
        }

        ContentValues rankings = new ContentValues();
        ContentValues aggregations = new ContentValues();
        ContentValues rankList = new ContentValues();

        if(ranking.getId() != -1) {
            deleteSavedAggregation(ranking.getId());
        }

        // add rankings information to appropriate table
        rankings.put(Tables.SavedRankingsTable.RANKING_NAME, ranking.getName());
        rankings.put(Tables.SavedRankingsTable.RANKING_DATE, ranking.getDate());
        long newRankingID = db.insert(Tables.SavedRankingsTable.TABLE_NAME, null, rankings);

        // add aggregation settings to appropriate table
        for(Map.Entry<Integer, Integer> e : ranking.getSettings().entrySet()) {
            aggregations.put(Tables.SavedAggregationsTable.SAVED_ID, newRankingID);
            aggregations.put(Tables.SavedAggregationsTable.SAVED_INDICATOR, e.getKey());
            aggregations.put(Tables.SavedAggregationsTable.SAVED_WEIGHT, e.getValue());
        }
        db.insert(Tables.SavedAggregationsTable.TABLE_NAME, null, aggregations);

        // add rank list to appropriate table
        List<Integer> ranks = ranking.getResult();
        for(int i = 0; i < ranks.size(); i++) {
            rankList.put(Tables.SavedRankListTable.SAVED_RANKING_ID, newRankingID);
            rankList.put(Tables.SavedRankListTable.SAVED_RANK, i+1);
            rankList.put(Tables.SavedRankListTable.SAVED_UNI_ID, ranks.get(i));
        }
        db.insert(Tables.SavedRankListTable.TABLE_NAME, null, rankList);
    }

    private void deleteSavedAggregation(int id) {

        String selection1 = Tables.SavedRankingsTable._ID + " = " + id;
        String selection2 = Tables.SavedAggregationsTable.SAVED_ID + " = " + id;
        String selection3 = Tables.SavedRankListTable.SAVED_RANKING_ID + " = " + id;

        db.delete(Tables.SavedRankingsTable.TABLE_NAME, selection1, null);
        db.delete(Tables.SavedAggregationsTable.TABLE_NAME, selection2, null);
        db.delete(Tables.SavedRankListTable.TABLE_NAME, selection3, null);
    }

    public List<SaveRank> fetchAllSaves() {
        // specifies which database columns we want from the query
        String[] projection = {
                Tables.SavedRankingsTable._ID
        };

        // perform query in UNIVERSITIES table
        Cursor uniqueIds = db.query(
                Tables.SavedRankingsTable.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);


        if(uniqueIds.getCount() >= 0) {
            List<SaveRank> allSaves = new ArrayList<>();
            while(uniqueIds.moveToNext()) {
                int saveId = uniqueIds.getInt(uniqueIds.getColumnIndexOrThrow(Tables.SavedRankingsTable._ID));
                allSaves.add(getAggregation(saveId));
            }

            // close Cursor
            uniqueIds.close();

            return allSaves;
        } else {

            // close Cursor
            uniqueIds.close();
            throw new IllegalStateException("Negative result count, database corrupted");
        }

    }

    @Override
    public SaveRank getAggregation(int savedId) {

        // arguments check
        if(savedId < 0) {
            throw new IllegalArgumentException("Id of a saved ranking cannot be negative");
        }

        Cursor savedRankingsResult = retrieveSavedRankingsData(savedId);
        Cursor savedAggregationSettings = retrieveSavedAggregationData(savedId);
        Cursor savedRankList = retrieveSavedRankList(savedId);

        // build and return a SaveRank object
        String rName = savedRankingsResult.getString(
                savedRankingsResult.getColumnIndexOrThrow(Tables.SavedRankingsTable.RANKING_NAME));
        String rDate = savedRankingsResult.getString(
                savedRankingsResult.getColumnIndexOrThrow(Tables.SavedRankingsTable.RANKING_DATE));

        Map<Integer, Integer> settings = new HashMap<>();
        while(savedAggregationSettings.moveToNext()) {
            int indicatorID = savedAggregationSettings.
                    getInt(savedAggregationSettings.
                            getColumnIndexOrThrow(Tables.SavedAggregationsTable.SAVED_INDICATOR));
            int weight = savedAggregationSettings.
                    getInt(savedAggregationSettings.
                            getColumnIndexOrThrow(Tables.SavedAggregationsTable.SAVED_WEIGHT));
            settings.put(indicatorID, weight);
        }

        Integer[] rank = new Integer[savedRankList.getCount()];
        while(savedRankList.moveToNext()) {
            int rankPos = savedRankList.
                    getInt(savedRankList.
                            getColumnIndexOrThrow(Tables.SavedRankListTable.SAVED_RANK));
            int uniID = savedRankList.
                    getInt(savedRankList.
                            getColumnIndexOrThrow(Tables.SavedRankListTable.SAVED_UNI_ID));
            rank[rankPos-1] = uniID;
        }
        List<Integer> rankList = Arrays.asList(rank);

        SaveRank savedRanking = new SaveRank(rName, rDate, settings, rankList, savedId);

        // close Cursors
        savedRankingsResult.close();
        savedAggregationSettings.close();
        savedRankList.close();

        return savedRanking;
    }

    private Cursor retrieveSavedRankingsData(int savedId) {
        // specifies which database columns we want from the query
        String[] projection = {
                Tables.SavedRankingsTable._ID,
                Tables.SavedRankingsTable.RANKING_NAME,
                Tables.SavedRankingsTable.RANKING_DATE
        };

        // specifies the WHERE clause of the query
        String selection = Tables.SavedRankingsTable._ID + " = " + savedId;

        // perform query in UNIVERSITIES table
        Cursor result = db.query(
                Tables.SavedRankingsTable.TABLE_NAME,
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
            throw new NoSuchElementException("This id does not correspond to any ranking in database");
        } else {
            throw new IllegalStateException("More rankings with same id, database is corrupted");
        }

        return result;
    }

    private Cursor retrieveSavedAggregationData(int savedId) {
        // specifies which database columns we want from the query
        String[] projection = {
                Tables.SavedAggregationsTable.SAVED_ID,
                Tables.SavedAggregationsTable.SAVED_INDICATOR,
                Tables.SavedAggregationsTable.SAVED_WEIGHT
        };

        // specifies the WHERE clause of the query
        String selection = Tables.SavedAggregationsTable.SAVED_ID + " = " + savedId;

        // perform query in UNIVERSITIES table
        Cursor result = db.query(
                Tables.SavedAggregationsTable.TABLE_NAME,
                projection,
                selection,
                null,
                null,
                null,
                null);

        // check
        if(result.getCount() <= 0) {
            // close Cursor
            result.close();

            throw new IllegalStateException("Empty settings for saved ranking, database is corrupted");

        } else {

            return result;
        }
    }

    private Cursor retrieveSavedRankList(int savedId) {
        // specifies which database columns we want from the query
        String[] projection = {
                Tables.SavedRankListTable.SAVED_RANK,
                Tables.SavedRankListTable.SAVED_UNI_ID
        };

        // specifies the WHERE clause of the query
        String selection = Tables.SavedRankListTable.SAVED_RANKING_ID + " = " + savedId;

        // perform query in UNIVERSITIES table
        Cursor result = db.query(
                Tables.SavedRankListTable.TABLE_NAME,
                projection,
                selection,
                null,
                null,
                null,
                null);

        // check
        if(result.getCount() <= 0) {
            // close Cursor
            result.close();

            throw new IllegalStateException("Empty rank list for saved ranking, database is corrupted");

        } else {

            return result;
        }
    }
}
