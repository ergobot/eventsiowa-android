package com.placesware.placesiowa.event;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.placesware.placesiowa.R;

import java.util.List;

public class SearchResultsListAdapter extends RecyclerView.Adapter<SearchResultsListViewHolder>{

    List<Event> events;
    public SearchResultsListAdapter(List<Event> events){
        this.events = events;
    }

    public SearchResultsListAdapter(List<Event> events, Activity activity){
        this.events = events;
        this.activity = activity;
    }

    public Event getItem(int position){
        return events.get(position);
    }

    Activity activity;
    @Override
    public SearchResultsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_search_result, parent, false);
        SearchResultsListViewHolder searchResultsListViewHolder = new SearchResultsListViewHolder(v);
        return searchResultsListViewHolder;
    }

    @Override
    public void onBindViewHolder(SearchResultsListViewHolder holder, int position) {

        holder.eventTitle.setText(events.get(position).getTitle());
        holder.eventAddress.setText(events.get(position).getLocation());

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void swap(List<Event> searchResultEvents){
        events.clear();
        events.addAll(searchResultEvents);
        notifyDataSetChanged();
    }

}
