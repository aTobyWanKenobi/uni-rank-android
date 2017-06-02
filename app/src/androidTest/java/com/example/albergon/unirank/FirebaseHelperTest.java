package com.example.albergon.unirank;

import android.content.Context;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;

import com.example.albergon.unirank.Database.CallbackHandlers.OnFirebaseErrorListener;
import com.example.albergon.unirank.Database.CallbackHandlers.OnShareRankUploadListener;
import com.example.albergon.unirank.Database.CallbackHandlers.OnSharedPoolRetrievalListener;
import com.example.albergon.unirank.Database.FirebaseHelper;
import com.example.albergon.unirank.Model.Ranking;
import com.example.albergon.unirank.Model.ShareGeneralStats;
import com.example.albergon.unirank.Model.ShareRank;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test suite for the firebase helper
 */
public class FirebaseHelperTest {

    FirebaseHelper firebaseHelper = null;

    @Before
    public void setUp() {
        Context testContext = InstrumentationRegistry.getTargetContext();
        firebaseHelper = new FirebaseHelper(testContext);
    }

    @Test
    public void retrieveDatesTest() {

        OnSharedPoolRetrievalListener listener = new OnSharedPoolRetrievalListener() {
           @Override
           public void onSharedPoolRetrieved(List<ShareRank> sharedPool) {
               for(ShareRank rank : sharedPool) {
                   System.out.print("Loggeteeee: " + rank.date);
               }
           }
        };

        OnFirebaseErrorListener error = new OnFirebaseErrorListener() {
           @Override
           public void onError(String message) {
               Assert.assertEquals(1, 2);
           }
         };

        firebaseHelper.testDateRetriever(listener, error);
        SystemClock.sleep(5000);
        System.out.print("Ci arrivo");

    }

    @Test
    public void initialGeneralStatsUpload() {

        ShareGeneralStats toShare = new ShareGeneralStats();

        FirebaseDatabase.getInstance().getReference().child(FirebaseHelper.GENERAL_STATISTICS_NODE).setValue(toShare);
        FirebaseDatabase.getInstance().getReference().child(FirebaseHelper.GENERAL_STATISTICS_NODE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // nothing, just a test
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // nothing, just a test
            }
        });

        SystemClock.sleep(3000);
    }

    @Test
    public void uploadAggregation() {

        List<Integer> rankList = new ArrayList<>();
        rankList.add(12);
        rankList.add(3);

        Map<Integer, Integer> settings = new HashMap<>();
        settings.put(1, 3);
        settings.put(3, 2);
        settings.put(2, 2);

        Ranking<Integer> ranking = new Ranking<Integer>(rankList);

        firebaseHelper.uploadAggregation(rankList, settings,
                new OnShareRankUploadListener() {
                    @Override
                    public void onUploadCompleted(boolean successful) {
                        // do nothing
                    }
                });
    }
}
