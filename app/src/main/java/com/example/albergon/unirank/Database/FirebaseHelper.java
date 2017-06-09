package com.example.albergon.unirank.Database;

import android.content.Context;

import com.example.albergon.unirank.Database.CallbackHandlers.OnFirebaseErrorListener;
import com.example.albergon.unirank.Database.CallbackHandlers.OnGeneralStatisticsRetrievalListener;
import com.example.albergon.unirank.Database.CallbackHandlers.OnShareRankUploadListener;
import com.example.albergon.unirank.Database.CallbackHandlers.OnSharedPoolRetrievalListener;
import com.example.albergon.unirank.Model.Settings;
import com.example.albergon.unirank.Model.ShareGeneralStats;
import com.example.albergon.unirank.Model.ShareRank;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * This class provides methods to safely access remote Firebase instance
 */
public class FirebaseHelper {

    // firebase database instance structure attributes
    private final static String SHARED_NODE = "shared";
    public final static String GENERAL_STATISTICS_NODE = "general_statistics";

    private DatabaseHelper databaseHelper = null;
    private DatabaseReference firebase = null;
    private Context context = null;

    /**
     * Public constructor, retrieves local SQLite database instance and remote Firebase instance
     *
     * @param context   Activity Context of place where this FirebaseHelper is instantiated
     */
    public FirebaseHelper(Context context) {

        // arguments check
        if(context == null) {
            throw new IllegalArgumentException("Context for FirebaseHelper cannot be null");
        }

        this.context = context;

        // retrieve instances
        databaseHelper = DatabaseHelper.getInstance(context);
        firebase = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Upload an aggregation to the remote shared pool. Takes only ranking result and settings as
     * parameters, but retrieves user settings from local database and attaches them to the aggregation.
     *
     * @param result            sorted list of university ids, representing aggregation result
     * @param settings          indicators and weights used in the aggregation
     * @param callbackHandler   callback listener provided by caller
     */
    public void uploadAggregation(List<Integer> result, Map<Integer, Integer> settings,
                                  OnShareRankUploadListener callbackHandler) {

        // arguments check
        if(result == null || settings == null || callbackHandler == null) {
            throw new IllegalArgumentException("Arguments for an aggregation upload cannot be null");
        } else if(result.isEmpty() || settings.isEmpty()) {
            throw new IllegalArgumentException(("Cannot upload an empty aggregation"));
        }

        // retrieve user settings and build ShareRank object
        Settings userSettings = databaseHelper.retrieveSettings(false);
        ShareRank toShare = new ShareRank(generateDate(), result, settings, userSettings);
        String randomId = String.valueOf(generateRandomId());

        // upload to firebase
        firebase.child(SHARED_NODE).child(randomId).setValue(toShare);
        firebase.child(SHARED_NODE).child(randomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    // update pool statistics
                    updateGeneralStatistics(userSettings, callbackHandler);
                } else {
                    callbackHandler.onUploadCompleted(false);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callbackHandler.onUploadCompleted(false);
            }
        });

    }

    /**
     * Private method called on each aggregation upload. Keeps the general statistics about the
     * shared pool updated, in order to avoid computing them each time.
     *
     * @param userSettings      settings used for the upload that triggered the call
     * @param callbackHandler   callback listener provided by caller
     */
    private void updateGeneralStatistics(Settings userSettings, OnShareRankUploadListener callbackHandler) {

        // update correct firebase node
        firebase.child(GENERAL_STATISTICS_NODE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    //retrieve current stats
                    ShareGeneralStats stats = dataSnapshot.getValue(ShareGeneralStats.class);

                    // update them
                    stats.update(
                            userSettings.getGender(),
                            userSettings.getType(),
                            userSettings.getYearOfBirth(),
                            generateDate(),
                            userSettings.getCountryCode());

                    // delegate new stats upload
                    uploadNewStats(stats, callbackHandler);
                } else {
                    callbackHandler.onUploadCompleted(false);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callbackHandler.onUploadCompleted(false);
            }
        });


    }

    /**
     * Part of the general statistics update system, performs the upload of the new statistics.
     *
     * @param toShare              statistics to be uploaded
     * @param callbackHandler      callback listener provided by caller
     */
    private void uploadNewStats(ShareGeneralStats toShare, OnShareRankUploadListener callbackHandler) {

        firebase.child(GENERAL_STATISTICS_NODE).setValue(toShare);
        firebase.child(GENERAL_STATISTICS_NODE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    // confirm end of correct aggregation upload
                    callbackHandler.onUploadCompleted(true);
                } else {
                    callbackHandler.onUploadCompleted(false);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callbackHandler.onUploadCompleted(false);
            }
        });

    }

    /**
     * Retrieves the current statistics concerning the state of the remote share pool.
     *
     * @param successListener   success callback listener provided by caller
     * @param errorListener     failure callback listener provided by caller
     */
    public void retrieveGeneralStats(OnGeneralStatisticsRetrievalListener successListener,
                                     OnFirebaseErrorListener errorListener) {

        // retrieve from general stats node
        firebase.child(GENERAL_STATISTICS_NODE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    ShareGeneralStats stats = dataSnapshot.getValue(ShareGeneralStats.class);
                    successListener.onGeneralStatisticsRetrieved(stats);
                } else {
                    errorListener.onError("Retrieved empty snapshot from Firebase. Corrupted remote database.");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                errorListener.onError(databaseError.getMessage());
            }
        });
    }

    /**
     * Retrieve the entire remote database under the form a list of ShareRank objects.
     *
     * @param successHandler    success callback listener provided by caller
     * @param errorListener     failure callback listener provided by caller
     */
    public void retrieveSharedPool(OnSharedPoolRetrievalListener successHandler,
                                               OnFirebaseErrorListener errorListener) {

        firebase.child(SHARED_NODE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    // add all shared ranks to collection
                    List<ShareRank> shareRanks = new ArrayList<>();
                    for(DataSnapshot child : dataSnapshot.getChildren()) {
                        shareRanks.add(child.getValue(ShareRank.class));
                    }

                    // callback on retrieval
                    successHandler.onSharedPoolRetrieved(shareRanks);

                } else {
                    errorListener.onError("Empty snapshot retrieved from Firebase");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                    errorListener.onError(databaseError.getMessage());
            }
        });

    }

    /**
     * Static utility method that computes current date in the format D MMM YYYY.
     *
     * @return  string representation of current date
     */
    public static String generateDate() {
        // generate date in string format
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);

        return dateFormat.format(new Date());
    }

    /**
     * Static utility method that generates a random id for uploaded aggregations.
     *
     * @return  a randomly generated 6 digits integer
     */
    private static int generateRandomId() {
        Random rnd = new Random();

        return 100000 + rnd.nextInt(900000);
    }
}
