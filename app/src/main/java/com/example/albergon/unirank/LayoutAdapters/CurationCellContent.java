package com.example.albergon.unirank.LayoutAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.albergon.unirank.R;

import java.util.List;

/**
 * This class models the content of a curation cell in the CurationFragment
 */
public class CurationCellContent {

    // layout elements
    private View convertView = null;
    private TextView bestTxt = null;
    private TextView curTxt = null;
    private ImageView curationIcon = null;

    private Context context = null;
    private CurationGridAdapter.Curations curation = null;

    public CurationCellContent(CurationGridAdapter.Curations curation, Context context) {

        this.context = context;
        this.curation = curation;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.cell_curation_element, null);

        bestTxt = (TextView) convertView.findViewById(R.id.curation_best_of);
        curTxt = (TextView) convertView.findViewById(R.id.curation_category);
        curationIcon = (ImageView) convertView.findViewById(R.id.curation_icon);

        setLayoutContent();
    }

    public View getLayout() {
        return convertView;
    }

    private void setLayoutContent() {

        switch(curation) {

            case BEST_COUNTRY:
                bestTxt.setText("Popular");
                curTxt.setText("in your country");
                curationIcon.setBackgroundResource(R.drawable.icon_curation_country);
                break;
            case TYPE_AND_AGE:
                bestTxt.setText("Popular among");
                curTxt.setText("your peers");
                curationIcon.setBackgroundResource(R.drawable.icon_curation_peers);
                break;
            case LAST_MONTH:
                bestTxt.setText("Popular during");
                curTxt.setText("last month");
                curationIcon.setBackgroundResource(R.drawable.icon_curation_month);
                break;
            case BEST_OVERALL:
                bestTxt.setText("Most popular");
                curTxt.setText("overall");
                curationIcon.setBackgroundResource(R.drawable.icon_curation_top);
                break;
            case EMPTY:
                bestTxt.setText("Create");
                curTxt.setText("your ranking");
                curationIcon.setBackgroundResource(R.drawable.icon_curation_empty);
                break;
            default:
                throw new IllegalStateException("Unknown element in Curations enum");
        }
    }
}
