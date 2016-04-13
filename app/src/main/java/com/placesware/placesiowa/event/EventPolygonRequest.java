package com.placesware.placesiowa.event;

import java.util.List;

public class EventPolygonRequest {

    private String clientId;
    public String getclientId(){return this.clientId;}
    public void setClientId(String clientId){this.clientId = clientId;}

    private List<Double[]> polygon;
    private long startDateFilter;
    private long endDateFilter;

    public EventPolygonRequest(){}

    public List<Double[]> getPolygon() {
        return polygon;
    }
    public void setPolygon(List<Double[]> polygon) {
        this.polygon = polygon;
    }
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


}