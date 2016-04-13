package com.placesware.placesiowa.event;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.placesware.placesiowa.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventViewHolder>{

    List<Event> events;
    public EventAdapter(List<Event> events){
        this.events = events;
    }

    public EventAdapter(List<Event> events, Activity activity){
        this.events = events;
        this.activity = activity;
    }

    public Event getItem(int position){
        return events.get(position);
    }

    Activity activity;
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_event, parent, false);
        EventViewHolder eventViewHolder = new EventViewHolder(v);
        return eventViewHolder;
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(events.get(position).getStartDate());

        holder.eventTitle.setText(events.get(position).getTitle());

//        FragmentManager fragmentManager = ((NavigationActivity) activity).getSupportFragmentManager();
//        EventLocatorFragment eventLocatorFragment = (EventLocatorFragment) fragmentManager.findFragmentByTag(EventLocatorFragment.TAG);

//        if(eventLocatorFragment != null){
//            Location eventLocation = new Location("Event Location");
//            eventLocation.setLatitude(events.get(position).getPoint().getPosition().getLatitude());
//            eventLocation.setLongitude(events.get(position).getPoint().getPosition().getLongitude());
//            Location currentLocation = eventLocatorFragment.getCurrentLocation();
//            try {
//                float distance = currentLocation.distanceTo(eventLocation);
//                holder.eventDistance.setText(String.valueOf(distance) + " meters");
//            }catch(Exception ex){
//                holder.eventDistance.setText("Unknown");
//            }
//        }else{ http
//            holder.eventDistance.setText("Unknown");
//        }

        holder.eventType.setText("Event");
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy");
        sdf.setTimeZone(calendar.getTimeZone());
        String customDate = sdf.format(calendar.getTime());
        holder.eventDate.setText(customDate);


    }

    @Override
    public int getItemCount() {
        return events.size();
    }

}
