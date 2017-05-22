package com.example.albergon.unirank.Model;

import com.example.albergon.unirank.LayoutAdapters.CurationGridAdapter;

import java.util.List;
import java.util.Map;

/**
 * This interface implements activity callbacks for curation download and generation
 */
public interface OnCurationDownloadNotifier {

    void onSettingsDownloadCompleted(CurationGridAdapter.Curations curation, Map<Integer, Integer> settings);

    void onRankingDownloadCompleted(CurationGridAdapter.Curations curation, List<Integer> ranking);
}
