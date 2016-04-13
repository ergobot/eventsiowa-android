package com.placesware.placesiowa;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cocoahero.android.geojson.Point;
import com.cocoahero.android.geojson.Position;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.placesware.placesiowa.event.Event;
import com.placesware.placesiowa.event.EventPolygonRequest;
import com.placesware.placesiowa.event.EventTextRequest;
import com.placesware.placesiowa.event.Favorite;
import com.placesware.placesiowa.event.FavoriteRequest;
import com.placesware.placesiowa.event.SignInRequest;
import com.placesware.placesiowa.event.SignInResponse;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import org.bson.types.ObjectId;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

//import rx.Observer;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//import rx.subscriptions.CompositeSubscription;

// TODO check that you need to update play services
// Reproduce:
/*
    flash factory image onto device (Nexus 5 - android 6 - MMB29S)
    Open application
    Map will show a message and button to update google play service
    Click button, app f/c

 */

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        EventLocatorFragment.OnFragmentInteractionListener,
        OpenResultsFragment.OnFragmentInteractionListener,
        ListResultsFragment.OnFragmentInteractionListener,
        SelectedEventFragment.OnFragmentInteractionListener,
        SelectedEventExpandedFragment.OnFragmentInteractionListener,
        SearchResultsListFragment.OnFragmentInteractionListener,
        FilterFragment.OnFragmentInteractionListener,
        OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "NavigationActivity";

    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";

    boolean favoritesView = false;
    public boolean isFavoritesView(){return this.favoritesView;}
    public void setFavoritesView(boolean favoritesView){this.favoritesView = favoritesView;}

    List<Event> eventsList = new ArrayList<Event>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;


    List<Event> favorites = new ArrayList<Event>();
    public List<Event> getFavorites(){return this.favorites;}
    public void setFavorites(List<Event> favorites){this.favorites = favorites;}

    public List<Event> getEventsList() {
        return eventsList;
    }

    public void setEventsList(List<Event> eventsList) {
        this.eventsList = eventsList;
    }

    List<Event> searchResultsList = new ArrayList<Event>();

    public List<Event> getSearchResultsList() {
        return searchResultsList;
    }

    public void setSearchResultsList(List<Event> searchResultsList) {
        this.searchResultsList = searchResultsList;
    }


    HashMap<String, Marker> markers = new HashMap<String, Marker>();

    public HashMap<String, Marker> getMarkers() {
        return this.markers;
    }

    public void setMarkers(HashMap<String, Marker> markers) {
        this.markers = markers;
    }

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    Event selectedEvent;

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    Toolbar toolbar;


    public CardView getToolbarContainer() {
        return toolbarContainer;
    }

    public void setToolbarContainer(CardView toolbarContainer) {
        this.toolbarContainer = toolbarContainer;
    }

    CardView toolbarContainer;

    public void setApi(EventServiceApi api) {
        this.api = api;
    }

    public final static String ID_TOKEN = "com.placesware.com.placesware.placesiowa.TOKEN";
    public final static String NAME = "NAME";
    public final static String PHOTO_URL = "PHOTO_URL";
    public final static String EMAIL = "EMAIL";
    public String idToken;

    public String getIdToken() {
        return this.idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    private String name = "";
    private String email = "";
    private String photo = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Get the token
        Intent intent = getIntent();
        setIdToken(intent.getStringExtra(NavigationActivity.ID_TOKEN));
        name = intent.getStringExtra(NavigationActivity.NAME);
        photo = intent.getStringExtra(NavigationActivity.PHOTO_URL);
        email = intent.getStringExtra(NavigationActivity.EMAIL);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarContainer = (CardView) findViewById(R.id.map_toolbar_container);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        setApi(_createEventServiceApi());
//        setupFakeData();


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, EventLocatorFragment.newInstance(0), EventLocatorFragment.TAG).commit();

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupDateTimePicker(savedInstanceState);

        signIn(getIdToken());
        setupGoogleApiClient();

        View header = navigationView.getHeaderView(0);

        if(email != null && !email.isEmpty()){
            ((TextView)header.findViewById(R.id.email)).setText(email);
        }

        if(name != null && !name.isEmpty()){
            ((TextView)header.findViewById(R.id.name)).setText(name);
        }

        if(photo != null && !photo.isEmpty()){
            ImageView logo = (ImageView)header.findViewById(R.id.profile_image);
            Picasso.with(this).load(photo).into(logo);

        }




        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        // In life I regret nothing, except maybe what I'm doing here.... (am practicing results oriented programming...)
        FragmentManager fragmentManager = getSupportFragmentManager();

        EventLocatorFragment eventLocatorFragment = (EventLocatorFragment) fragmentManager.findFragmentByTag(EventLocatorFragment.TAG);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (eventLocatorFragment != null) {

            SelectedEventExpandedFragment selectedEventExpandedFragment = (SelectedEventExpandedFragment) fragmentManager.findFragmentByTag(SelectedEventExpandedFragment.TAG);
            if (selectedEventExpandedFragment != null) {
                // selected event expanded is selected, go back to selectedevent
                eventLocatorFragment.changeSliderFragment(SelectedEventFragment.TAG);
                eventLocatorFragment.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }

            SelectedEventFragment selectedEventFragment = (SelectedEventFragment) fragmentManager.findFragmentByTag(SelectedEventFragment.TAG);
            if (selectedEventFragment != null) {
                eventLocatorFragment.changeSliderFragment(OpenResultsFragment.TAG);
                eventLocatorFragment.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                if (getSelectedEvent() != null) {
                    eventLocatorFragment.unselectMarker(getSelectedEvent());
                    setSelectedEvent(null);
                }
            }

            FilterFragment filterFragment = (FilterFragment) fragmentManager.findFragmentByTag(FilterFragment.TAG);
            if (filterFragment != null) {
                eventLocatorFragment.changeSliderFragment(OpenResultsFragment.TAG);
                eventLocatorFragment.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                eventLocatorFragment.getSlidingUpPanelLayout().setEnabled(false);
                if (getSelectedEvent() != null) {
                    eventLocatorFragment.unselectMarker(getSelectedEvent());
                    setSelectedEvent(null);
                }
            }

            ListResultsFragment listResultsFragment = (ListResultsFragment) fragmentManager.findFragmentByTag(ListResultsFragment.TAG);
            if (listResultsFragment != null) {
                eventLocatorFragment.changeSliderFragment(ListResultsFragment.TAG);
                eventLocatorFragment.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                if (getSelectedEvent() != null) {
                    eventLocatorFragment.unselectMarker(getSelectedEvent());
                    setSelectedEvent(null);
                }
            }

            SearchResultsListFragment searchResultsListFragment = (SearchResultsListFragment) fragmentManager.findFragmentByTag(SearchResultsListFragment.TAG);
            if (searchResultsListFragment != null) {
                eventLocatorFragment.changeSliderFragment(OpenResultsFragment.TAG);
                eventLocatorFragment.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                closeSearchView();
                if (getSelectedEvent() != null) {
                    eventLocatorFragment.unselectMarker(getSelectedEvent());
                    setSelectedEvent(null);
                }
            }

        } else {
            super.onBackPressed();
        }

    }

    public boolean isSearchExpanded() {


        return searchMenuItem.isActionViewExpanded();
//        return searchView.isIconified();
    }

    public void closeSearchView() {
//        MenuItemCompat.expandActionView(searchMenuItem);
        searchMenuItem.collapseActionView();
//        onBackPressed();
//        searchView.setIconified(true);

    }


    MenuItem searchMenuItem;


    SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (newText.length() > 2) {
//                getEventsByText(newText);
                if(isFavoritesView()){
                    getFavoriteEventsByText(newText);
                }else {
                    getEventsByText(newText);
                }
            }
            return true;
        }
    };


    MenuItemCompat.OnActionExpandListener onActionExpandListener = new MenuItemCompat.OnActionExpandListener() {
        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            Log.i("NavigationActivity", "Action expand start");
            FragmentManager fragmentManager = getSupportFragmentManager();

            EventLocatorFragment eventLocatorFragment = (EventLocatorFragment) fragmentManager.findFragmentByTag(EventLocatorFragment.TAG);
            if (eventLocatorFragment != null) {
                eventLocatorFragment.changeSliderFragment(SearchResultsListFragment.TAG);
            }
            Log.i("NavigationActivity", "Action expand end");


            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            Log.i("NavigationActivity", "Action collapse end");

            Log.i("NavigationActivity", "Action collapse complete");

            return true;
        }
    };

    SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);

//        searchMenuItem.setOnActionExpandListener(onActionExpandListener);
        SearchView searchView =
                (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(onQueryTextListener);

        if (searchMenuItem != null) {
            MenuItemCompat.setOnActionExpandListener(searchMenuItem, onActionExpandListener);
            MenuItemCompat.setActionView(searchMenuItem, searchView);
        }

        final TextView toolbarEditText = (TextView) findViewById(R.id.toolbar_title);
        toolbarEditText.setOnClickListener(new OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   searchMenuItem.expandActionView();
                                               }

                                           }

        );

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (item.getTitle().equals("Search")) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_camera) {
            EventLocatorFragment eventLocatorFragment = (EventLocatorFragment) fragmentManager.findFragmentByTag(EventLocatorFragment.TAG);
            if (eventLocatorFragment == null) {

//                        hideToolbar();
                setCardToolbar();
//                        showToolbar();
                EventLocatorFragment eventLocatorFragment1 = EventLocatorFragment.newInstance(id);
                setFavoritesView(false);
                eventLocatorFragment1.clearAndRefreshMap();
                fragmentManager.beginTransaction().replace(R.id.container, eventLocatorFragment1, EventLocatorFragment.TAG).commit();
            }else{
                setFavoritesView(false);
                if (getSelectedEvent() != null) {
                    eventLocatorFragment.unselectMarker(getSelectedEvent());
                    setSelectedEvent(null);
                }

                eventLocatorFragment.clearAndRefreshMap();
                eventLocatorFragment.changeSliderFragment(OpenResultsFragment.TAG);
            }
        }else if(id == R.id.nav_favorites){
            EventLocatorFragment eventLocatorFragment = (EventLocatorFragment) fragmentManager.findFragmentByTag(EventLocatorFragment.TAG);
            if (eventLocatorFragment == null) {

                setCardToolbar();
                EventLocatorFragment eventLocatorFragment1 = EventLocatorFragment.newInstance(id);
                setFavoritesView(true);
                eventLocatorFragment1.clearAndRefreshMap();
                fragmentManager.beginTransaction().replace(R.id.container, eventLocatorFragment1, EventLocatorFragment.TAG).commit();
            }else{
                setFavoritesView(true);
                if (getSelectedEvent() != null) {
                    eventLocatorFragment.unselectMarker(getSelectedEvent());
                    setSelectedEvent(null);
                }
                eventLocatorFragment.clearAndRefreshMap();
                eventLocatorFragment.changeSliderFragment(OpenResultsFragment.TAG);
            }
        }else if(id == R.id.nav_settings){
            //Intent settingsIntent = new Intent
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
//        else if (id == R.id.nav_filter) {
//            FilterFragment filterFragment = (FilterFragment) fragmentManager.findFragmentByTag(FilterFragment.TAG);
//            if (filterFragment == null) {
//                hideToolbar();
//                setNormalToolbar();
//                fragmentManager.beginTransaction().replace(R.id.container, FilterFragment.newInstance(NavigationActivity.TAG), FilterFragment.TAG).commit();
//                showToolbar();
//            }
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setCardToolbar() {

        if (((ViewManager) getToolbarContainer().getParent()) == null) {

            LinearLayout ll = (LinearLayout) findViewById(R.id.toolbarlayout);
            TextView tv = (TextView) findViewById(R.id.toolbar_title);
            tv.setVisibility(View.VISIBLE);

            Toolbar tb = getToolbar();
            tb.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            CardView cv = getToolbarContainer();

            ll.addView(cv);

            if (tb != null) {
                ViewGroup parent = (ViewGroup) tb.getParent();
                if (parent != null) {
                    parent.removeView(tb);
                    cv.addView(tb);
                }
            }

        }

    }

    private void setNormalToolbar() {
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        getSupportActionBar().setTitle("Places Filter");

        if (((ViewManager) getToolbarContainer().getParent()) != null) {


            LinearLayout ll = (LinearLayout) findViewById(R.id.toolbarlayout);
            TextView tv = (TextView) findViewById(R.id.toolbar_title);
            tv.setVisibility(View.GONE);
            Toolbar tb = getToolbar();
            tb.setBackgroundColor(getResources().getColor(R.color.color70));
//            tb.getMenu().clear();
            CardView cv = getToolbarContainer();
            ((ViewManager) getToolbarContainer().getParent()).removeView(getToolbarContainer());


            if (tb != null) {
                ViewGroup parent = (ViewGroup) tb.getParent();
                if (parent != null) {
                    parent.removeView(tb);
                }
            }

//                    ll.removeView(cv);
            ll.addView(tb);


        }


    }

    public void showToolbar() {
        CardView toolbar = this.getToolbarContainer();
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
    }

    public void hideToolbar() {
        CardView toolbar = this.getToolbarContainer();
        toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
    }

    public void onSectionAttached(int number) {

        switch (number) {
            case 1:
                // something
                // mTitle = getString(R.string.title_accounts);
                break;
            case 2:
                // something
                // mTitle = getString(R.string.title_sendmonty);
                break;
            case 3:
                // dark side
                // mTitle = getString(R.string.title_managepayments);
                break;
            case 4:
                // stormtropper..
                // mTitle = getString(R.string.title_stormtropper);
                break;
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // this.onSectionAttached(position);
    }


    public void setupFakeData() {
//        Creates the json object which will manage the information received
        Type listType = new TypeToken<List<Event>>() {
        }.getType();
        GsonBuilder gsonBuilder = new GsonBuilder();

// Register an adapter to manage the date types as long values
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });

//        gsonBuilder.registerTypeAdapter(Event.class, new JsonDeserializer<Event>() {
//            public Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
////                return new Date(json.getAsJsonPrimitive().getAsLong());
//                return new Event();
//            }
//        });
//        gsonBuilder.registerTypeAdapter(listType, new EventTypeAdapter());
        gsonBuilder.registerTypeAdapter(Point.class, new JsonDeserializer<Point>() {
            public Point deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

                try {
                    JsonObject jsonObject = json.getAsJsonObject();

                    JsonArray jsonArray = jsonObject.getAsJsonArray("coordinates");
                    double lat = jsonArray.get(1).getAsDouble();
                    double lng = jsonArray.get(0).getAsDouble();


                    Point point = new Point();
                    Position position = new Position(lat, lng);
                    point.setPosition(position);
                    return point;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        Gson gson = gsonBuilder.create();

//        ArrayList<Event> fakeEventsList = gson.fromJson(getRawFakeData(), listType);
//        setEventsList(fakeEventsList);


    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Navigation Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.placesware.com.placesware.placesiowa/http/host/path")
        );
        AppIndex.AppIndexApi.start(client2, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Navigation Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.placesware.com.placesware.placesiowa/http/host/path")
        );
        AppIndex.AppIndexApi.end(client2, viewAction);
        client2.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // just in case
    private static class EventTypeAdapter implements JsonDeserializer<List<Event>> {
        @Override
        public List<Event> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final List<Event> events = new ArrayList<Event>();

            final JsonArray jsonArray = json.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                Event event = new Event();
                JsonObject jsonEvent = jsonArray.get(i).getAsJsonObject();

                event.setId(jsonEvent.get("id").getAsString());
                event.setStartDate(new Date(jsonEvent.get("startDate").getAsLong()));
                event.setEndDate(new Date(jsonEvent.get("endDate").getAsLong()));

                event.setTitle(jsonEvent.get("title").getAsString());
                event.setLocation(jsonEvent.get("location").getAsString());
                event.setDetails(jsonEvent.get("details").getAsString());

                event.setLink(jsonEvent.get("link").getAsString());

                JsonArray jsonPhones = jsonEvent.get("phones").getAsJsonArray();
                ArrayList<String> phones = new ArrayList<String>();
                for (int j = 0; j < jsonPhones.size(); j++) {
                    phones.add(jsonPhones.get(j).getAsString());
                }
                event.setPhones(phones);

                JsonArray jsonEmails = jsonEvent.get("emails").getAsJsonArray();
                ArrayList<String> emails = new ArrayList<String>();
                for (int k = 0; k < jsonEmails.size(); k++) {
                    emails.add(jsonEmails.get(k).getAsString());
                }
                event.setEmails(emails);

                JsonArray jsonWebsites = jsonEvent.get("websites").getAsJsonArray();
                ArrayList<String> websites = new ArrayList<String>();
                for (int m = 0; m < jsonWebsites.size(); m++) {
                    websites.add(jsonWebsites.get(m).getAsString());
                }
                event.setWebsites(websites);
            }

            return events;
        }
    }


    private EventServiceApi _createEventServiceApi() {


//         Creates the json object which will manage the information received
        GsonBuilder gsonBuilder = new GsonBuilder();

// Register an adapter to manage the date types as long values
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });

        gsonBuilder.registerTypeAdapter(ObjectId.class, new JsonDeserializer<ObjectId>() {
            public ObjectId deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new ObjectId();
            }
        });


        gsonBuilder.registerTypeAdapter(Favorite.class, new JsonSerializer<Favorite>() {
			@Override
			public JsonElement serialize(Favorite favorite, Type typOfT, JsonSerializationContext context) {

//				Date d = (Date)startDate;
//	        	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		        JsonObject jo = new JsonObject();
		        jo.addProperty("sub", favorite.getSub());
                jo.addProperty("eventId", favorite.getEventId().toString());
				return jo;

			}
        });

//        gsonBuilder.registerTypeAdapter(Event.class, new JsonDeserializer<Event>() {
//            public Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
////                return new Date(json.getAsJsonPrimitive().getAsLong());
//                return new Event();
//            }
//        });

        gsonBuilder.registerTypeAdapter(Point.class, new JsonDeserializer<Point>() {
            public Point deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

                try {
                    JsonObject jsonObject = json.getAsJsonObject();

                    JsonArray jsonArray = jsonObject.getAsJsonArray("coordinates");
                    double lat = jsonArray.get(1).getAsDouble();
                    double lng = jsonArray.get(0).getAsDouble();


                    Point point = new Point();
                    Position position = new Position(lat, lng);
                    point.setPosition(position);
                    return point;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        Gson gson = gsonBuilder.create();

        // Add cert to trust

//        SSLContext sslContext = sslContextForTrustedCertificates(this.getResources().openRawResource(R.raw.com.placesware.placesiowa));//trustedCertificatesInputStream());
        SSLContext sslContext = sslContextForTrustedCertificates(this.getResources().openRawResource(R.raw.clientp));

//        String hostname = "192.168.1.124";

//        CertificatePinner certificatePinner = new CertificatePinner.Builder()
//                .add(hostname, "sha1/57BukpuusoI7miyccG0o5O+5Mp8=")
//                .add(hostname, "sha1/D4UQ1AazABZaZJZrbgi1/fqnTrA=")
//                .build();
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                boolean verified = false;
                if(hostname.equals("192.168.1.124")) {
                    verified = true;
                }
                if(hostname.equals("ec2-54-68-23-127.us-west-2.compute.amazonaws.com")){
                    verified = true;
                }
                return verified;
            }
        };

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        OkHttpClient clientBuilder = new OkHttpClient();
//        OkHttpClient client = clientBuilder.newBuilder().certificatePinner(certificatePinner).addInterceptor(interceptor).build();
        OkHttpClient client = clientBuilder
                .newBuilder()
//                .certificatePinner(certificatePinner)
                .addInterceptor(interceptor)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier(hostnameVerifier)
                .build();

        String test = "";


        String serviceHost = getResources().getString(R.string.service_host);

        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(
                serviceHost).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create(gson))
                .client(client);


        return builder.build().create(EventServiceApi.class);

    }

    public SSLContext sslContextForTrustedCertificates(InputStream in) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
            if (certificates.isEmpty()) {
                throw new IllegalArgumentException("expected non-empty set of trusted certificates");
            }

            // Put the certificates a key store.
            char[] password = "youtube-rvb-episode49".toCharArray(); // Any password will work.
            KeyStore keyStore = newEmptyKeyStore(password);
            int index = 0;
            for (Certificate certificate : certificates) {
                byte[] pubKey = certificate.getPublicKey().getEncoded();
                final byte[] hash = MessageDigest.getInstance("SHA-1").digest(pubKey);
                String test2 = Base64.encodeToString(hash, Base64.DEFAULT);

//                String out1 = "sha1/"+ Base64.encode(MessageDigest.getInstance("SHA-1").digest(certificate.getPublicKey().getEncoded()));
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificate);
            }

            // Wrap it up in an SSL context.
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(),
                    new SecureRandom());
            return sslContext;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream in = null; // By convention, 'null' creates an empty key store.
            keyStore.load(in, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }



    public EventServiceApi getApi() {
        return api;
    }

    private EventServiceApi api;
    private CompositeSubscription _subscriptions = new CompositeSubscription();

    public void getEventsByPolygon(List<Double[]> polygon) {
//        EventRadiusRequest eventRadiusRequest = createEventRadiusRequest();
        if(getIdToken().isEmpty()){
            // go back to the login screen
            Intent signIn = new Intent(NavigationActivity.this, IdTokenActivity.class);
            startActivity(signIn);
        }

        EventPolygonRequest epr = new EventPolygonRequest();
        epr.setClientId(getIdToken());
        epr.setPolygon(polygon);
        epr.setStartDateFilter(getStartDate());
        epr.setEndDateFilter(getEndDate());
        _subscriptions.add(
                getApi().getEventsInPolygon(epr)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<List<Event>>() {
                            @Override
                            public void onCompleted() {
                                Timber.d("Call 1 completed");
                                // Tell the EventLocatorFragment that the events are updated
                                FragmentManager fragmentManager = getSupportFragmentManager();

                                EventLocatorFragment eventLocatorFragment = (EventLocatorFragment) fragmentManager.findFragmentByTag(EventLocatorFragment.TAG);
                                if (eventLocatorFragment != null) {
                                    eventLocatorFragment.updateMapWithPlaces();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                if(e != null && ("HTTP 500 Server Error").equals(e.getMessage())){
                                    signin();
                                }

                                Timber.d("Call 1 completed in ERROR");
                                Timber.d("error", e);
//                                boolean useFakeData = true;
//                                if(useFakeData){
//                                    setupFakeData();
//                                }

                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(List<Event> events) {

                                Timber.d("login response received: ");

                                setEventsList(events);
                            }
                        }));

    }


    public void getFavoriteEventsByPolygon(List<Double[]> polygon) {
//        EventRadiusRequest eventRadiusRequest = createEventRadiusRequest();
        if(getIdToken().isEmpty()){
            // go back to the login screen
            Intent signIn = new Intent(NavigationActivity.this, IdTokenActivity.class);
            startActivity(signIn);
        }

        EventPolygonRequest epr = new EventPolygonRequest();
        epr.setClientId(getIdToken());
        epr.setPolygon(polygon);
        epr.setStartDateFilter(getStartDate());
        epr.setEndDateFilter(getEndDate());
        _subscriptions.add(
                getApi().getFavoriteEventsInPolygon(epr)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<List<Event>>() {
                            @Override
                            public void onCompleted() {
                                Timber.d("Call 1 completed");
                                // Tell the EventLocatorFragment that the events are updated
                                FragmentManager fragmentManager = getSupportFragmentManager();

                                EventLocatorFragment eventLocatorFragment = (EventLocatorFragment) fragmentManager.findFragmentByTag(EventLocatorFragment.TAG);
                                if (eventLocatorFragment != null) {
                                    eventLocatorFragment.updateMapWithPlaces();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {


                                Timber.d("Call 1 completed in ERROR");
                                Timber.d("error", e);
//                                boolean useFakeData = true;
//                                if(useFakeData){
//                                    setupFakeData();
//                                }
                                if(e != null && ("HTTP 500 Server Error").equals(e.getMessage())){
                                    signin();
                                }

                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(List<Event> events) {

                                Timber.d("login response received: ");

                                setEventsList(events);
                            }
                        }));

    }


    public void signIn(String idToken) {

        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setClientId(idToken);

        _subscriptions.add(
                getApi()
                        .signin(signInRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<SignInResponse>() {
                            @Override
                            public void onCompleted() {
                                Timber.d("Call 1 completed");
                                retrieveFavorites();
                            }

                            @Override
                            public void onError(Throwable e) {


                                Timber.d("Call 1 completed in ERROR");
                                Timber.d("error", e);

                                if(e != null && ("HTTP 500 Server Error").equals(e.getMessage())){
                                    signin();
                                }

                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(SignInResponse response) {

                                Timber.d("login response received: ");

                            }
                        }));

    }

    public void setFavorite(Favorite favorite) {

        FavoriteRequest favoriteRequest = new FavoriteRequest();
        favoriteRequest.setClientId(getIdToken());
        favorite.setSub("");
//        favorite.set_id(null);
        favoriteRequest.setFavorite(favorite);

        _subscriptions.add(
                getApi()
                        .setFavorite(favoriteRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<List<Event>>() {
                            @Override
                            public void onCompleted() {
                                Timber.d("Call 1 completed");

                                FragmentManager fragmentManager = getSupportFragmentManager();

                                SelectedEventExpandedFragment selectedEventExpandedFragment = (SelectedEventExpandedFragment) fragmentManager.findFragmentByTag(SelectedEventExpandedFragment.TAG);
                                if (selectedEventExpandedFragment != null) {
                                    selectedEventExpandedFragment.refreshFavorite();
                                }


                            }
                            @Override
                            public void onError(Throwable e) {
                                Timber.d("Call 1 completed in ERROR");
                                Timber.d("error", e);
                                if(e != null && ("HTTP 500 Server Error").equals(e.getMessage())){
                                    signin();
                                }

                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(List<Event> response) {
                                Timber.d("login response received: ");

                                setFavorites(response);

                            }
                        }));

    }

    public void retrieveFavorites() {

        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setClientId(getIdToken());

        _subscriptions.add(
                getApi()
                        .retrieveFavorites(signInRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<List<Event>>() {
                            @Override
                            public void onCompleted() {
                                Timber.d("Call 1 completed");

                                FragmentManager fragmentManager = getSupportFragmentManager();

                                SelectedEventExpandedFragment selectedEventExpandedFragment = (SelectedEventExpandedFragment) fragmentManager.findFragmentByTag(SelectedEventExpandedFragment.TAG);
                                if (selectedEventExpandedFragment != null) {
                                    selectedEventExpandedFragment.refreshFavorite();
                                }


                            }
                            @Override
                            public void onError(Throwable e) {
                                Timber.d("Call 1 completed in ERROR");
                                Timber.d("error", e);

                                if(e != null && ("HTTP 500 Server Error").equals(e.getMessage())){
                                    signin();
                                }

                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(List<Event> response) {
                                Timber.d("login response received: ");

                                setFavorites(response);

                            }
                        }));

    }

    public void removeFavorite(Favorite favorite) {

        FavoriteRequest favoriteRequest = new FavoriteRequest();
        favoriteRequest.setClientId(getIdToken());
        favoriteRequest.setFavorite(favorite);

        _subscriptions.add(
                getApi()
                        .removeFavorite(favoriteRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<List<Event>>() {
                            @Override
                            public void onCompleted() {
                                Timber.d("Call 1 completed");
//                                refreshFavoritesList();
                                FragmentManager fragmentManager = getSupportFragmentManager();

                                SelectedEventExpandedFragment selectedEventExpandedFragment = (SelectedEventExpandedFragment) fragmentManager.findFragmentByTag(SelectedEventExpandedFragment.TAG);
                                if (selectedEventExpandedFragment != null) {
                                    selectedEventExpandedFragment.refreshFavorite();
                                }


                            }
                            @Override
                            public void onError(Throwable e) {
                                Timber.d("Call 1 completed in ERROR");
                                Timber.d("error", e);
                                if(e != null && ("HTTP 500 Server Error").equals(e.getMessage())){
                                    signin();
                                }
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(List<Event> response) {
                                Timber.d("login response received: ");

                                setFavorites(response);


                            }
                        }));

    }




    private boolean filterEnabled;

    public boolean getFilterEnabled() {
        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        return prefs.getBoolean("filterEnabled", false);

    }

    public void setFilterEnabled(boolean filterEnabled) {
        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("filterEnabled", filterEnabled);
        editor.commit();
    }

    public void resetStartDate() {

        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("startDate", -1);
        editor.commit();
    }

    public void resetEndDate() {

        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("endDate", -1);
        editor.commit();
    }

    public void persistStartDate(int year, int month, int day) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);


        Date start = calendar.getTime();
        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("startDate", start.getTime());
        editor.commit();
    }

    public void persistEndDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);


        Date start = calendar.getTime();
        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("endDate", start.getTime());
        editor.commit();
    }

    public void persistEndDate(long time) {
        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("endDate", time);
        editor.commit();
    }

    public void persistStartDate(long time) {
        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("startDate", time);
        editor.commit();
    }

    public long getStartDate() {
        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        return prefs.getLong("startDate", -1);
////        editor.putInt(getString(R.string.saved_high_score), newHighScore);
//        editor.commit();

//        return -1L;
    }

    public long getEndDate() {
        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        return prefs.getLong("endDate", -1);


//        return -1L;
    }

    public void getEventsByText(String searchText) {
//        EventRadiusRequest eventRadiusRequest = createEventRadiusRequest();

        if(getIdToken().isEmpty()){
            // go back to the login screen
            Intent signIn = new Intent(NavigationActivity.this, IdTokenActivity.class);
            startActivity(signIn);
        }

        EventTextRequest etr = new EventTextRequest();
        etr.setClientId(getIdToken());
        etr.setSearchText(searchText);
        etr.setStartDateFilter(getStartDate());
        etr.setEndDateFilter(getEndDate());
        _subscriptions.add(
                getApi().getEventsInText(etr)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<List<Event>>() {
                            @Override
                            public void onCompleted() {
                                Timber.d("Call 1 completed");
                                // Tell the EventLocatorFragment that the events are updated
                                FragmentManager fragmentManager = getSupportFragmentManager();

                                EventLocatorFragment eventLocatorFragment = (EventLocatorFragment) fragmentManager.findFragmentByTag(EventLocatorFragment.TAG);
                                if (eventLocatorFragment != null) {
                                    eventLocatorFragment.updateSearchResultsList();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.d("Call 1 completed in ERROR");
                                Timber.d("error", e);

                                if(e != null && ("HTTP 500 Server Error").equals(e.getMessage())){
                                    signin();
                                }
//                                boolean useFakeData = true;
//                                if(useFakeData){
//                                    setupFakeData();
//                                }

                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(List<Event> events) {

                                Timber.d("login response received: ");
                                setSearchResultsList(events);
//                                setEventsList(events);
                            }
                        }));

    }

    public void getFavoriteEventsByText(String searchText) {
//        EventRadiusRequest eventRadiusRequest = createEventRadiusRequest();

        if(getIdToken().isEmpty()){
            // go back to the login screen
            Intent signIn = new Intent(NavigationActivity.this, IdTokenActivity.class);
            startActivity(signIn);
        }

        EventTextRequest etr = new EventTextRequest();
        etr.setClientId(getIdToken());
        etr.setSearchText(searchText);
        etr.setStartDateFilter(getStartDate());
        etr.setEndDateFilter(getEndDate());
        _subscriptions.add(
                getApi().getFavoriteEventsInText(etr)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<List<Event>>() {
                            @Override
                            public void onCompleted() {
                                Timber.d("Call 1 completed");
                                // Tell the EventLocatorFragment that the events are updated
                                FragmentManager fragmentManager = getSupportFragmentManager();

                                EventLocatorFragment eventLocatorFragment = (EventLocatorFragment) fragmentManager.findFragmentByTag(EventLocatorFragment.TAG);
                                if (eventLocatorFragment != null) {
                                    eventLocatorFragment.updateSearchResultsList();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.d("Call 1 completed in ERROR");
                                Timber.d("error", e);
//                                boolean useFakeData = true;
//                                if(useFakeData){
//                                    setupFakeData();
//                                }
                                if(e != null && ("HTTP 500 Server Error").equals(e.getMessage())){
                                    signin();
                                }
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(List<Event> events) {

                                Timber.d("login response received: ");
                                setSearchResultsList(events);
//                                setEventsList(events);
                            }
                        }));

    }


    @Override
    public void onResume() {
        super.onResume();
//        setupEvents();
        _subscriptions = RxUtils.getNewCompositeSubIfUnsubscribed(_subscriptions);
    }

    @Override
    public void onPause() {
        super.onPause();
        RxUtils.unsubscribeIfNotNull(_subscriptions);
    }


    DatePickerDialog datePickerDialog;

    //    TimePickerDialog startTimePickerDialog;
//    TimePickerDialog endTimePickerDialog;
    public void setupDateTimePicker(Bundle savedInstanceState) {


        final Calendar calendar = Calendar.getInstance();

//        TimePickerDialog.OnTimeSetListener startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
//
//                FragmentManager fragmentManager = getSupportFragmentManager();
//
//                FilterFragment filterFragment = (FilterFragment) fragmentManager.findFragmentByTag(FilterFragment.TAG);
//                if (filterFragment != null) {
//                    String time = hourOfDay + ":" + minute;
//
//                    try {
//                        final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
//                        final Date dateObj = sdf.parse(time);
//                        String newTime = new SimpleDateFormat("h:mm a").format(dateObj);
////                        filterFragment.setStartTime(newTime);
//                    } catch (final ParseException e) {
////                        filterFragment.setStartTime(hourOfDay + ":" + minute);
//                    }
//
//                }
//
//                }
//
//            };


//        TimePickerDialog.OnTimeSetListener endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
//
//                FragmentManager fragmentManager = getSupportFragmentManager();
//
//                FilterFragment filterFragment = (FilterFragment) fragmentManager.findFragmentByTag(FilterFragment.TAG);
//                if (filterFragment != null) {
//                        String time = hourOfDay + ":" + minute;
//
//                    try {
//                        final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
//                        final Date dateObj = sdf.parse(time);
//                        String newTime = new SimpleDateFormat("h:mm a").format(dateObj);
//                        filterFragment.setEndTime(newTime);
//                    } catch (final ParseException e) {
////                        e.printStackTrace();
//                        filterFragment.setEndTime(hourOfDay + ":" + minute);
//                    }
//
//
//
//                    }
//
//                }
//
//
//        };

        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), true);
//        timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
//        startTimePickerDialog= TimePickerDialog.newInstance(startTimeSetListener,calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
//        endTimePickerDialog = TimePickerDialog.newInstance(endTimeSetListener,calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);


        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }

            TimePickerDialog tpd = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
            if (tpd != null) {
                tpd.setOnTimeSetListener(this);
            }
        }

    }


    public BitmapDescriptor getMapIconWithColor(int drawableId,float color){

        int color_green = Color.HSVToColor(new float[] { color, 1.0f, 1.0f });
        Drawable wrapDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_event_black_24dp));
        DrawableCompat.setTint(wrapDrawable, color_green);
        final int widthDip = (int) TypedValue.applyDimension(1, wrapDrawable.getIntrinsicWidth(), getResources()
                .getDisplayMetrics());
        final int heightDip = (int) TypedValue.applyDimension(1, wrapDrawable.getIntrinsicWidth(), getResources().getDisplayMetrics());
        final Bitmap bitmap = Bitmap.createBitmap(wrapDrawable.getIntrinsicWidth(), wrapDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        wrapDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        wrapDrawable.draw(canvas);

        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
        return icon;

    }

    public BitmapDescriptor getMapIcon(int drawableId){

//        int color_green = Color.HSVToColor(new float[] { BitmapDescriptorFactory.HUE_RED, 1.0f, 1.0f });
        Drawable wrapDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_event_black_24dp));
//        DrawableCompat.setTint(wrapDrawable, color_green);
        final int widthDip = (int) TypedValue.applyDimension(1, wrapDrawable.getIntrinsicWidth(), getResources()
                .getDisplayMetrics());
        final int heightDip = (int) TypedValue.applyDimension(1, wrapDrawable.getIntrinsicWidth(), getResources().getDisplayMetrics());
        final Bitmap bitmap = Bitmap.createBitmap(wrapDrawable.getIntrinsicWidth(), wrapDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        wrapDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        wrapDrawable.draw(canvas);

        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
        return icon;

    }
    private GoogleApiClient mGoogleApiClient;

    public void setupGoogleApiClient(){
        // [START configure_signin]
        // Request only the user's ID token, which can be used to identify the
        // user securely to your backend. This will contain the user's basic
        // profile (name, profile picture URL, etc) so you should not need to
        // make an additional call to personalize your application.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail().requestProfile()
                .build();

        // [END configure_signin]

        // Build GoogleAPIClient with the Google Sign-In API and the above options.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    public void signin(){
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
//            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
//                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        // [START get_id_token]

        Log.d(TAG, "onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess());

        if (result.isSuccess()) {


            GoogleSignInAccount acct = result.getSignInAccount();
            String idToken = acct.getIdToken();

            // Show signed-in UI.
            Log.d(TAG, "idToken:" + idToken);
//            mIdTokenTextView.setText(getString(R.string.id_token_fmt, idToken));
//                updateUI(true);
            setIdToken(idToken);



        } else {
            // Show signed-out UI.
//            updateUI(false);
            Intent intent = new Intent(this, IdTokenActivity.class);
            intent.putExtra(ID_TOKEN, idToken);
            startActivity(intent);
        }
        // [END get_id_token]
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {

//        month++;
        FragmentManager fragmentManager = getSupportFragmentManager();

        FilterFragment filterFragment = (FilterFragment) fragmentManager.findFragmentByTag(FilterFragment.TAG);
        if (filterFragment != null) {
            switch (datePickerDialog.getTag()) {
                case ("startDate"):
                    persistStartDate(year, month, day);
                    filterFragment.setStartDate((1 + month) + "/" + day + "/" + year);
                    // end date must be greater than or equal new date
                    if (getEndDate() == -1) {
                        persistEndDate(year, month, day);
                        filterFragment.setEndDate(new Date(getStartDate()));
                    } else if (getEndDate() < getStartDate()) {
                        persistEndDate(getStartDate());
                        filterFragment.setEndDate(new Date(getStartDate()));
                    }
                    break;
                case ("endDate"):
                    persistEndDate(year, month, day);
                    filterFragment.setEndDate((1 + month) + "/" + day + "/" + year);

                    if (getStartDate() == -1) {
                        persistStartDate(year, month, day);
                        filterFragment.setStartDate(new Date(getEndDate()));
                    } else if (getEndDate() < getStartDate()) {
                        persistStartDate(getEndDate());
                        filterFragment.setStartDate(new Date(getEndDate()));
                    }

                    break;
            }

        }


//        Toast.makeText(NavigationActivity.this, "new date:" + year + "-" + month + "-" + day, Toast.LENGTH_LONG).show();
    }


}

