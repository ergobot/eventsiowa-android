package com.placesware.placesiowa.event;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.placesware.placesiowa.R;

public class SearchResultsListViewHolder extends RecyclerView.ViewHolder {

    TextView eventTitle;
    TextView eventAddress;

    SearchResultsListViewHolder(View itemView) {
        super(itemView);
        eventTitle = (TextView) itemView.findViewById(R.id.titletextview);
        eventAddress = (TextView) itemView.findViewById(R.id.addresstextview);

    }

}
