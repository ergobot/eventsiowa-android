package com.placesware.placesiowa;

import java.util.ArrayList;
import java.util.List;

public class EventRadiusRequest {

    private String clientId;
    public String getclientId(){return this.clientId;}
    public void setClientId(String clientId){this.clientId = clientId;}

    private List<Double> point;
    private double radius;

    public EventRadiusRequest(){
        point = new ArrayList<Double>();
        radius = 0.0d;
    }

    public EventRadiusRequest(List<Double> location, double radius){
        this.setPoint(location);
        this.setRadius(radius);
    }

    public List<Double> getPoint() {
        return point;
    }

    public void setPoint(List<Double> location) {
        this.point = location;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
