package com.example.albergon.unirank.Database.CallbackHandlers;

/**
 * Callback interface for firebase database errors.
 */
public interface OnFirebaseErrorListener {

    void onError(String message);
}
