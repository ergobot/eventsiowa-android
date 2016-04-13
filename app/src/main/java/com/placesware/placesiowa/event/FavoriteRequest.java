package com.placesware.placesiowa.event;

/**
 * Created by morbo on 2/16/16.
 */
public class FavoriteRequest {

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    String clientId;

    public Favorite getFavorite() {
        return favorite;
    }

    public void setFavorite(Favorite favorite) {
        this.favorite = favorite;
    }

    Favorite favorite;

}
