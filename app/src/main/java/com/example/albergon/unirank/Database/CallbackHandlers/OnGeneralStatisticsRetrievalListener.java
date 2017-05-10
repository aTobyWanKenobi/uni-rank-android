package com.example.albergon.unirank.Database.CallbackHandlers;

import com.example.albergon.unirank.Model.ShareGeneralStats;

/**
 * Callback interface for retrieval of general statistics from firebase
 */
public interface OnGeneralStatisticsRetrievalListener {

    void onGeneralStatisticsRetrieved(ShareGeneralStats stats);
}
