package com.example.albergon.unirank.Fragments;

import com.example.albergon.unirank.Model.SharedRankFilters.ShareRankFilter;

/**
 * This simple callback interface is used to retrieve category and parameters from the AddFilterDialog
 */
public interface OnAddFilterReturn {

    void onFilterReady(ShareRankFilter filter);

    void updateQuery();
}
