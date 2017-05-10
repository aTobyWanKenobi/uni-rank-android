package com.example.albergon.unirank.Database.CallbackHandlers;

import com.example.albergon.unirank.Model.ShareRank;

import java.util.List;

/**
 * Callback interface for retrieval of shared pool
 */
public interface OnSharedPoolRetrievalListener {

    void onSharedPoolRetrieved(List<ShareRank> sharedPool);
}
