package com.placesware.placesiowa.event;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.placesware.placesiowa.R;

public class EventViewHolder extends RecyclerView.ViewHolder {

    TextView eventTitle;
//    TextView eventDistance;
    TextView eventType;
    TextView eventDate;

    EventViewHolder(View itemView) {
        super(itemView);
        eventTitle = (TextView) itemView.findViewById(R.id.titletextview);
//        eventDistance = (TextView) itemView.findViewById(R.id.distancetextview);
        eventType = (TextView) itemView.findViewById(R.id.typetextview);
        eventDate = (TextView) itemView.findViewById(R.id.datetextview);

    }

}
