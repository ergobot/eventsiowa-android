package com.placesware.placesiowa.event;


public class FavoriteResponse {

    public FavoriteResponse(){}

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    private boolean success;
}
