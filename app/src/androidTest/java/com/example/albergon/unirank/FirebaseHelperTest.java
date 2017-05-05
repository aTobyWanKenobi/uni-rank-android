package com.example.albergon.unirank;

import android.content.Context;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;

import com.example.albergon.unirank.Database.CallbackHandlers.OnFirebaseErrorListener;
import com.example.albergon.unirank.Database.CallbackHandlers.OnSharedPoolRetrievalListener;
import com.example.albergon.unirank.Database.FirebaseHelper;
import com.example.albergon.unirank.Model.ShareGeneralStats;
import com.example.albergon.unirank.Model.ShareRank;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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
    }
}
