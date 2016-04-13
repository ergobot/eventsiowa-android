package com.placesware.placesiowa;

import com.placesware.placesiowa.event.Event;
import com.placesware.placesiowa.event.EventPolygonRequest;
import com.placesware.placesiowa.event.EventTextRequest;
import com.placesware.placesiowa.event.FavoriteRequest;
import com.placesware.placesiowa.event.SignInRequest;
import com.placesware.placesiowa.event.SignInResponse;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

public interface EventServiceApi {

    @POST("geteventsinpolygon")
    Observable<List<Event>> getEventsInPolygon(@Body EventPolygonRequest request);

    @POST("getfavoriteeventsinpolygon")
    Observable<List<Event>> getFavoriteEventsInPolygon(@Body EventPolygonRequest request);

    @POST("geteventsinradius")
    Observable<List<Event>> getEventsInRadius(@Body EventRadiusRequest request);

    @GET("echo")
    Observable<List<Event>> getEcho();

    @POST("geteventsbytext")
    Observable<List<Event>> getEventsInText(@Body EventTextRequest request);

    @POST("getfavoriteeventsbytext")
    Observable<List<Event>> getFavoriteEventsInText(@Body EventTextRequest request);

    @POST("signin")
    Observable<SignInResponse> signin(@Body SignInRequest request);

    @POST("setfavorite")
    Observable<List<Event>> setFavorite(@Body FavoriteRequest request);

    @POST("removefavorite")
    Observable<List<Event>> removeFavorite(@Body FavoriteRequest request);

    @POST("retrievefavorites")
    Observable<List<Event>> retrieveFavorites(@Body SignInRequest request);


}
