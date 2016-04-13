package com.placesware.placesiowa;


public class ObjectIdCustom {

    public ObjectIdCustom(){}

    // timestamp
    private int timestamp;
    // machineIdentifier
    private int machineIdentifier;
    // processIdentifier
    private short processIdentifier;
    // counter
    private int counter;
    // time
    private long time;
    // date
    private long date;
    // timeSecond
    private int getTimeSecond;


    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getMachineIdentifier() {
        return machineIdentifier;
    }

    public void setMachineIdentifier(int machineIdentifier) {
        this.machineIdentifier = machineIdentifier;
    }

    public short getProcessIdentifier() {
        return processIdentifier;
    }

    public void setProcessIdentifier(short processIdentifier) {
        this.processIdentifier = processIdentifier;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getGetTimeSecond() {
        return getTimeSecond;
    }

    public void setGetTimeSecond(int getTimeSecond) {
        this.getTimeSecond = getTimeSecond;
    }
}
