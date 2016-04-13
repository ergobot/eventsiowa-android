package com.placesware.placesiowa.event;


public class EventTextRequest {

    private String clientId;
    public String getclientId(){return this.clientId;}
    public void setClientId(String clientId){this.clientId = clientId;}

    private String searchText;
    private long startDateFilter;
    private long endDateFilter;

    public EventTextRequest(){}

    public long getStartDateFilter() {
        return startDateFilter;
    }
    public void setStartDateFilter(long startDateFilter) {
        this.startDateFilter = startDateFilter;
    }
    public long getEndDateFilter() {
        return endDateFilter;
    }
    public void setEndDateFilter(long endDateFilter) {
        this.endDateFilter = endDateFilter;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }


}