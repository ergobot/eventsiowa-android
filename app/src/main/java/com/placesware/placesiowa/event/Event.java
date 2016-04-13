package com.placesware.placesiowa.event;

import com.cocoahero.android.geojson.Point;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;

public class Event {


//	List<Double> coordsList = new ArrayList<Double>();

//	Position coord = new Position(coordsList);

//	com.mongodb.client.model.geojson.Point p2 =  new com.mongodb.client.model.geojson.Point(coord);

    public Event(){
//		this.point = new Point();
    }

    public Event(String link){
        this.link = link;
//		this.point = new Point();
    }


    private Date startDate;
    private Date endDate;
    private String title;
    private String location;
    private String details;
    private String link;
    private ArrayList<String> phones;
    private ArrayList<String> emails;
    private ArrayList<String> websites;
    private Point point;
    private String markerId; public void setMarkerId(String markerId){this.markerId = markerId;}public String getMarkerId(){return this.markerId;}
    private String id;

    public ObjectId getObjectId() {
        return new ObjectId(this.id);
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    private ObjectId objectId;

    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public ArrayList<String> getPhones() {
        return phones;
    }
    public void setPhones(ArrayList<String> phones) {
        this.phones = phones;
    }
    public ArrayList<String> getEmails() {
        return emails;
    }
    public void setEmails(ArrayList<String> emails) {
        this.emails = emails;
    }
    public ArrayList<String> getWebsites() {
        return websites;
    }
    public void setWebsites(ArrayList<String> websites) {
        this.websites = websites;
    }

    public Point getPoint() {
        return point;
    }
    public void setPoint(Point point) {
        this.point = point;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
