package com.example.albergon.unirank.Database;

import android.content.Context;

import com.example.albergon.unirank.Database.CallbackHandlers.OnFirebaseErrorListener;
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

    public final static String SHARED_NODE = "shared";
    public final static String GENERAL_STATISTICS_NODE = "general_statistics";

    private DatabaseHelper databaseHelper = null;
    private DatabaseReference firebase = null;

    private Context context = null;

    public FirebaseHelper(Context context) {

        // arguments check
        if(context == null) {
            throw new IllegalArgumentException("Context for FirebaseHelper cannot be null");
        }

        this.context = context;

        databaseHelper = DatabaseHelper.getInstance(context);
        firebase = FirebaseDatabase.getInstance().getReference();
    }

    public void uploadAggregation(List<Integer> result, Map<Integer, Integer> settings,
                                  OnShareRankUploadListener callbackHandler) {

        // arguments check
        if(result == null || settings == null || callbackHandler == null) {
            throw new IllegalArgumentException("Arguments for an aggregation upload cannot be null");
        } else if(result.isEmpty() || settings.isEmpty()) {
            throw new IllegalArgumentException(("Cannot upload an empty aggregation"));
        }

        Settings userSettings = databaseHelper.retriveSettings(false);
        ShareRank toShare = new ShareRank(generateDate(), result, settings, userSettings);
        String randomId = String.valueOf(generateRandomId());


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

    private void updateGeneralStatistics(Settings userSettings, OnShareRankUploadListener callbackHandler) {

        firebase.child(GENERAL_STATISTICS_NODE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    ShareGeneralStats stats = dataSnapshot.getValue(ShareGeneralStats.class);
                    stats.update(
                            userSettings.getGender(),
                            userSettings.getType(),
                            userSettings.getYearOfBirth(),
                            generateDate(),
                            userSettings.getCountryCode());
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

    private void uploadNewStats(ShareGeneralStats toShare, OnShareRankUploadListener callbackHandler) {

        firebase.child(GENERAL_STATISTICS_NODE).setValue(toShare);
        firebase.child(GENERAL_STATISTICS_NODE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
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

    public void testDateRetriever(OnSharedPoolRetrievalListener successHandler,
                                  OnFirebaseErrorListener error) {

        retrieveSharedPool(successHandler, error);
    }

    public static String generateDate() {
        // generate date in string format
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
        String date = dateFormat.format(new Date());

        return date;
    }

    public static int generateRandomId() {
        Random rnd = new Random();
        int rndNumber = 100000 + rnd.nextInt(900000);

        return rndNumber;
    }
}
