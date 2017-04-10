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
import java.util.Comparator;
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
     * Open local database in read/write mode.
     *
     * @throws SQLiteException propagated from openDatabase()
     */
    public void openDatabase() throws SQLiteException{

        db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    }


    /**
     * Utility method to check if database initialization has already been performed.
     *
     * @return  true if it exists, false otherwise
     */
    public boolean databaseExists() {

        SQLiteDatabase DBCheck = null;

        try {
            DBCheck = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
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
            @SuppressLint("UseSparseArrays") Map<Integer, Double> entries = new HashMap<>();
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

    /**
     * Writes a SaveRank object in the local database by storing all the information about an
     * aggregation in the appropriate tables.
     *
     * @param toSave   aggregation to be saved
     */
    @Override
    public void saveAggregation(SaveRank toSave) {

        // check arguments
        if(toSave == null) {
            throw new IllegalArgumentException("Ranking to be saved cannot be null");
        }

        // initialize rows to insert
        ContentValues save = new ContentValues();
        ContentValues settings = new ContentValues();
        ContentValues ranking = new ContentValues();

        // TODO: implement correct system
        // verify if the current writing is an update of an already present save
        /*if(saveAlreadyPresent(toSave.getName())) {
            // in that case delete old data
            deleteSavedAggregation(toSave.getName());
        }*/

        // add rankings information to appropriate table
        save.put(Tables.Saves.RANKING_NAME, toSave.getName());
        save.put(Tables.Saves.RANKING_DATE, toSave.getDate());
        db.insert(Tables.Saves.TABLE_NAME, null, save);

        // add aggregation settings to appropriate table
        for(Map.Entry<Integer, Integer> e : toSave.getSettings().entrySet()) {
            settings.put(Tables.SavesSettings.SAVED_NAME, toSave.getName());
            settings.put(Tables.SavesSettings.SAVED_INDICATOR, e.getKey());
            settings.put(Tables.SavesSettings.SAVED_WEIGHT, e.getValue());
        }
        db.insert(Tables.SavesSettings.TABLE_NAME, null, settings);

        // add rank list to appropriate table
        List<Integer> ranks = toSave.getResult();
        for(int i = 0; i < ranks.size(); i++) {
            ranking.put(Tables.SavesRankings.SAVED_NAME, toSave.getName());
            ranking.put(Tables.SavesRankings.SAVED_RANK, i);
            ranking.put(Tables.SavesRankings.SAVED_UNI_ID, ranks.get(i));
        }
        db.insert(Tables.SavesRankings.TABLE_NAME, null, ranking);
    }

    public boolean saveAlreadyPresent(String name) {

        return retrieveSaveData(name) != null;
    }

    /**
     * Method that deletes all information of a saved aggregation given it's name in the
     * database.
     *
     * @param name    name of the save to delete
     */
    public void deleteSavedAggregation(String name) {

        // arguments check
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Save name cannot be empty or null");
        }

        String[] selectionArgs = {name};

        String query1 = "DELETE FROM " + Tables.Saves.TABLE_NAME +
                " WHERE "+Tables.Saves.RANKING_NAME+" = ?;";
        db.execSQL(query1, selectionArgs);

        String query2 = "DELETE FROM " + Tables.SavesSettings.TABLE_NAME +
                " WHERE "+Tables.SavesSettings.SAVED_NAME+" = ?;";
        db.execSQL(query2, selectionArgs);

        String query3 = "DELETE FROM " + Tables.SavesRankings.TABLE_NAME +
                " WHERE "+Tables.SavesRankings.SAVED_NAME+" = ?;";
        db.execSQL(query3, selectionArgs);
    }

    /**
     * This method fetches all the current saved aggregations in database.
     *
     * @return  a list of SaveRank
     */
    public List<SaveRank> fetchAllSaves() {

        List<String> names = fetchAllSavesName();

        List<SaveRank> allSaves = new ArrayList<>();
        //noinspection Convert2streamapi
        for(String name : names) {
            allSaves.add(getSave(name));
        }

        return allSaves;
    }

    public List<String> fetchAllSavesName() {
        // specifies which database columns we want from the query
        String[] projection = {
                Tables.Saves.RANKING_NAME
        };

        // perform query of saved names
        Cursor uniqueNames = db.query(
                Tables.Saves.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        if(uniqueNames.getCount() >= 0) {
            List<String> allNames = new ArrayList<>();
            while(uniqueNames.moveToNext()) {
                String saveName = uniqueNames.getString(uniqueNames.getColumnIndexOrThrow(Tables.Saves.RANKING_NAME));
                allNames.add(saveName);
            }

            // close Cursor
            uniqueNames.close();

            return allNames;
        } else {

            // close Cursor
            uniqueNames.close();
            throw new IllegalStateException("Negative result count, database corrupted");
        }
    }

    /**
     * Retrieve a single saved aggregation from database given its name.
     *
     * @param name      name of the save in the database
     * @return          a SaveRank object containing aggregation information
     */
    @Override
    public SaveRank getSave(String name) {

        // arguments check
        //noinspection ConstantConditions
        if(name == null | name.isEmpty()) {
            throw new IllegalArgumentException("Name a saved ranking cannot be null or empty");
        }

        // call private methods to perform queries on each interested table
        Cursor savedRankingsResult = retrieveSaveData(name);

        if(savedRankingsResult == null) {
            throw new IllegalArgumentException("Save was not found in the database");
        }

        Cursor savedAggregationSettings = retrieveSaveSettings(name);
        Cursor savedRankList = retrieveSaveRanking(name);

        // build string parameters
        String rName = savedRankingsResult.getString(
                savedRankingsResult.getColumnIndexOrThrow(Tables.Saves.RANKING_NAME));
        String rDate = savedRankingsResult.getString(
                savedRankingsResult.getColumnIndexOrThrow(Tables.Saves.RANKING_DATE));

        // build settings parameter
        @SuppressLint("UseSparseArrays") Map<Integer, Integer> settings = new HashMap<>();
        savedAggregationSettings.moveToFirst();
        while(!savedAggregationSettings.isAfterLast()) {
            int indicatorID = savedAggregationSettings.
                    getInt(savedAggregationSettings.
                            getColumnIndexOrThrow(Tables.SavesSettings.SAVED_INDICATOR));
            int weight = savedAggregationSettings.
                    getInt(savedAggregationSettings.
                            getColumnIndexOrThrow(Tables.SavesSettings.SAVED_WEIGHT));
            settings.put(indicatorID, weight);
            savedAggregationSettings.moveToNext();
        }

        // build rank list
        List<Integer> unsortedUniIds = new ArrayList<>();

        System.out.println("Log count: " + savedAggregationSettings.getCount());

        @SuppressLint("UseSparseArrays") Map<Integer, Integer> idsWithRank = new HashMap<>();
        while(savedRankList.moveToNext()) {
            int rankPos = savedRankList.
                    getInt(savedRankList.
                            getColumnIndexOrThrow(Tables.SavesRankings.SAVED_RANK));
            int uniID = savedRankList.
                    getInt(savedRankList.
                            getColumnIndexOrThrow(Tables.SavesRankings.SAVED_UNI_ID));
            idsWithRank.put(uniID, rankPos);
            unsortedUniIds.add(uniID);
        }
        unsortedUniIds.sort(new RetrieveRankComparator(idsWithRank));

        // build and return a SaveRank object with the fetched parameters
        SaveRank savedRanking = new SaveRank(rName, rDate, settings, unsortedUniIds);

        // close Cursors
        savedRankingsResult.close();
        savedAggregationSettings.close();
        savedRankList.close();

        return savedRanking;
    }

    /**
     * Private method that queries name and date of a saved aggregation given its name.
     *
     * @param name      name of the saved aggregation in the database
     * @return          the Cursor containing the query result
     */
    private Cursor retrieveSaveData(String name) {

        String query = "SELECT " +
                Tables.Saves.RANKING_NAME + ", "+Tables.Saves.RANKING_DATE +
                " FROM " + Tables.Saves.TABLE_NAME +
                " WHERE "+Tables.Saves.RANKING_NAME+" = ?;";

        String[] selectionArgs = {name};
        Cursor result = db.rawQuery(query, selectionArgs);

        // check that we retrieved a unique save
        if(result.getCount() == 1) {
            result.moveToNext();
        } else if(result.getCount() == 0) {
            return null;
        } else {
            throw new IllegalStateException("More rankings with same name, database is corrupted");
        }

        return result;
    }

    /**
     * Private method that queries the indicator and weights settings of a saved aggregation
     * given its name.
     *
     * @param name      name of the saved aggregation in the database
     * @return          the Cursor containing the query result
     */
    private Cursor retrieveSaveSettings(String name) {

        String query = "SELECT " +
                Tables.SavesSettings.SAVED_NAME + ", "+ Tables.SavesSettings.SAVED_INDICATOR + ", " + Tables.SavesSettings.SAVED_WEIGHT +
                " FROM " + Tables.SavesSettings.TABLE_NAME +
                " WHERE "+ Tables.SavesSettings.SAVED_NAME+" = ?;";
        String[] selectionArgs = {name};
        Cursor result = db.rawQuery(query, selectionArgs);

        // check
        if(result.getCount() <= 0) {
            // close Cursor
            result.close();

            throw new IllegalStateException("Empty settings for saved ranking, database is corrupted");

        } else {

            return result;
        }
    }

    /**
     * Private method that queries the resulting rank list of a saved aggregation given its name.
     *
     * @param name      name of the saved aggregation in the database
     * @return          the Cursor containing the query result
     */
    private Cursor retrieveSaveRanking(String name) {

        String query = "SELECT " +
                Tables.SavesRankings.SAVED_NAME + ", " + Tables.SavesRankings.SAVED_RANK + ", "+ Tables.SavesRankings.SAVED_UNI_ID +
                " FROM " + Tables.SavesRankings.TABLE_NAME +
                " WHERE "+ Tables.SavesRankings.SAVED_NAME+" = ?;";
        String[] selectionArgs = {name};
        Cursor result = db.rawQuery(query, selectionArgs);

        // check
        if(result.getCount() <= 0) {
            // close Cursor
            result.close();

            throw new IllegalStateException("Empty rank list for saved ranking, database is corrupted");

        } else {

            return result;
        }
    }

    /**
     * Nested comparator class that reconstructs a rank list of universities ids given database data.
     */
    public static class RetrieveRankComparator implements Comparator<Integer> {

        private Map<Integer, Integer> pairings = null;

        @SuppressLint("UseSparseArrays")
        public RetrieveRankComparator(Map<Integer, Integer> pairings) {

            // check arguments
            if(pairings == null) {
                throw new IllegalArgumentException("Cannot create a comparator with a null pairing map");
            }

            this.pairings = new HashMap<>(pairings);
        }

        @Override
        public int compare(Integer o1, Integer o2) {

            if(pairings.get(o1) < pairings.get(o2)) {
                return -1;
            } else //noinspection NumberEquality
                if(pairings.get(o1) == pairings.get(o2)) {
                return 0;
            } else {
                return 1;
            }
        }
    }
}
