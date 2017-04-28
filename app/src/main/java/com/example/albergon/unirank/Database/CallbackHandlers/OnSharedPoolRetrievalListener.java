package com.example.albergon.unirank.Database.CallbackHandlers;

import com.example.albergon.unirank.Model.ShareRank;

import java.util.List;

/**
 * Created by Tobia Albergoni on 28.04.2017.
 */

public interface OnSharedPoolRetrievalListener {

    void onSharedPoolRetrieved(List<ShareRank> sharedPool);
}
