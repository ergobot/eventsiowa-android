package com.placesware.placesiowa;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.placesware.placesiowa.event.Event;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EventLocatorFragment
        extends Fragment
        implements
        OpenResultsFragment.OnChildViewAttachedListener,
        SelectedEventFragment.OnChildViewAttachedListener,
        SelectedEventExpandedFragment.OnChildViewAttachedListener,
        SearchResultsListFragment.OnChildViewAttachedListener,
        ListResultsFragment.OnChildViewAttachedListener,
        FilterFragment.OnChildViewAttachedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OpenResultsFragment.OnChildViewClickListener,
        SearchResultsListFragment.OnChildViewClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String TAG = "EventLocatorFragment";

    private GoogleMap mMap;
    public GoogleMap getGoogleMap(){return this.mMap;}
    boolean firstmapmove=true;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    boolean mRequestingLocationUpdates = true;
    Event selectedEvent = null;

    public Event getSelectedEvent(){
//        String selectedEventKey = ((NavigationActivity)getActivity()).getSelectedMarker();
//        Event foundEvent = null;
//        if(selectedEventKey != null && !selectedEventKey.isEmpty()){
//            List<Event> events = ((NavigationActivity)getActivity()).getEventsList();
//
//            for(Event event : events){
//                if(event.getId().equals(selectedEventKey)){
//                    foundEvent = event;
//                }
//            }
//        }
//        return foundEvent;
        return ((NavigationActivity)getActivity()).getSelectedEvent();
    }



    private OnFragmentInteractionListener mListener;

    private SlidingUpPanelLayout slidingUpPanelLayout;

    public SlidingUpPanelLayout getSlidingUpPanelLayout() {
        return this.slidingUpPanelLayout;
    }

    LatLng latlng;
    private LocationRequest lr;
    GoogleApiClient mGoogleApiClient;
    Location currentLocation;
    public Location getCurrentLocation(){return this.currentLocation;}
    public void setCurrentLocation(Location currentLocation){this.currentLocation = currentLocation;}

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    protected String mLastUpdateTime = "";
    @Override
    public void onConnected(Bundle connectionHint) {
        createLocationRequest();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }


    boolean firstLocationUpdate = true;
    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        if(firstLocationUpdate) {
            updateUI();
        }
    }

    private void updateUI() {
//        mLatitudeTextView.setText(String.valueOf(currentLocation.getLatitude()));
//        mLongitudeTextView.setText(String.valueOf(currentLocation.getLongitude()));
//        mLastUpdateTimeTextView.setText(mLastUpdateTime);
        if(mMap != null){
            if(currentLocation != null) {
                LatLng eventll = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//            Marker marker = mMap.addMarker(new MarkerOptions().position(eventll));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(eventll, 11));//,2000,null);
                firstLocationUpdate = false;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    public static final String LAST_UPDATED_TIME_STRING_KEY = "lastupdatekey";
    public static final String LOCATION_KEY = "locationkey";
    public static final String REQUESTING_LOCATION_UPDATES_KEY = "requestlocationupdatekey";

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, currentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 32;
    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }


        }else{
            // We have permission to use location services
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, lr, this);
            if(mMap!= null){
                mMap.setMyLocationEnabled(true);
//                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
        }
    }


    public void getMapsLocationIntent(String lat, String lng){

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?f=d&daddr="+lat +"," + lng));
        intent.setComponent(new ComponentName("com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity"));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startLocationUpdates();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Unable to access current location", Toast.LENGTH_LONG).show();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


//    @Override
//    public void onConnected(Bundle connectionHint) {
//        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (currentLocation != null) {
////            currentLocation.setText(String.valueOf(currentLocation.getLatitude()));
////            currentLocation.setText(String.valueOf(currentLocation.getLongitude()));
//        }
//    }

    protected void createLocationRequest() {
        lr = new LocationRequest();
        lr.setInterval(INTERVAL);
        lr.setFastestInterval(FASTEST_INTERVAL);
        lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventLocatorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventLocatorFragment newInstance(String param1, String param2) {
        EventLocatorFragment fragment = new EventLocatorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public EventLocatorFragment() {
        // Required empty public constructor
    }

    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        // check if play services are available
//        if (!mGoogleApiClient.is) {
//            finish();
//        }

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";


    public static EventLocatorFragment newInstance(int sectionNumber) {
        EventLocatorFragment fragment = new EventLocatorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    // offset
    boolean firstOffsetCheck = true;

    float previousSlideOffSet = 0.0f;

    FloatingActionButton directionsmiddlefab;
    FloatingActionButton currentLocationFab;
    FloatingActionButton directionsfab;
    FloatingActionButton filterFab;
    SupportMapFragment mapFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_event_locator, container, false);
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_event_locator, container, false);

        // update the main content by replacing fragments
        FragmentManager fragmentManager = ((NavigationActivity)getActivity()).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.slidecontainer,OpenResultsFragment.newInstance(EventLocatorFragment.TAG),OpenResultsFragment.TAG).commit();

        currentLocationFab = (FloatingActionButton) rootView.findViewById(R.id.currentlocationfab);
        filterFab = (FloatingActionButton) rootView.findViewById(R.id.filterfab);
        directionsfab = (FloatingActionButton)rootView.findViewById(R.id.directionsfab);
        directionsmiddlefab = (FloatingActionButton)rootView.findViewById(R.id.directionsmiddlefab);

        currentLocationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMap != null){
                    // TODO: current location can be null (can create an NPE on the first time
                    //       the application starts.
                    //       Check for null - if null - tell user the application is waiting for their location
                    if(currentLocation != null) {
                        LatLng eventll = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(eventll, 11));//,2000,null);
                        firstLocationUpdate = false;
                    }
                }
            }
        });

        directionsfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check for a selected event
                Event selectedEvent = getSelectedEvent();
                if(selectedEvent != null){
                    getMapsLocationIntent(String.valueOf(selectedEvent.getPoint().getPosition().getLatitude()), String.valueOf(selectedEvent.getPoint().getPosition().getLongitude()));
                }

            }
        });

        filterFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = ((NavigationActivity) getActivity()).getSupportFragmentManager();

                FilterFragment filterFragment = (FilterFragment) fragmentManager.findFragmentByTag(FilterFragment.TAG);

                if(filterFragment != null){
//                    Log.i("filter open", "Opening filter");
                    changeSliderFragment(OpenResultsFragment.TAG);
//                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//                    slidingUpPanelLayout.setEnabled(false);
                }else {
//                    Toast.makeText(getActivity(), "Filter Fab touched", Toast.LENGTH_LONG).show();
                    changeSliderFragment(FilterFragment.TAG);
//                    slidingUpPanelLayout.setEnabled(false);
                }
                // Check for a selected event
//                Event selectedEvent = getSelectedEvent();
//                if(selectedEvent != null){
//                    getMapsLocationIntent(String.valueOf(selectedEvent.getPoint().getPosition().getLatitude()), String.valueOf(selectedEvent.getPoint().getPosition().getLongitude()));
//                }



            }
        });

        directionsmiddlefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for a selected event
                Event selectedEvent = getSelectedEvent();
                if(selectedEvent != null){
                    getMapsLocationIntent(String.valueOf(selectedEvent.getPoint().getPosition().getLatitude()), String.valueOf(selectedEvent.getPoint().getPosition().getLongitude()));
                }

            }
        });
        directionsmiddlefab.setVisibility(View.GONE);


//        FloatingActionButton currentLocationFab = (FloatingActionButton) rootView.findViewById(R.id.currentlocationfab);
//        currentLocationFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
//        FloatingActionButton directionsFab = (FloatingActionButton) rootView.findViewById(R.id.directionsfab);

//        fab.setVisibility(View.GONE);

        slidingUpPanelLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
//                Log.i(TAG, "onPanelSlide, offset " + slideOffset);

                FragmentManager fragmentManager = ((NavigationActivity) getActivity()).getSupportFragmentManager();
                if(slideOffset > 0.0f && slideOffset < 1.0f){
                    if(slideOffset==0.0f){
                        currentLocationFab.setAlpha(1.0f);
                    }else{
//                        float max = slideOffset/slidingUpPanelLayout.getAnchorPoint();
//                        float showValue = 1.0f - max;
//                        currentLocationFab.setAlpha(showValue);

                        float showValue = mapRange(0.0f,slidingUpPanelLayout.getAnchorPoint(),0.0f,1.0f,slideOffset);
                        currentLocationFab.setAlpha(inverseAlpha(showValue));


                        // not possible
//                        OpenResultsFragment openResultsFragment = (OpenResultsFragment) fragmentManager.findFragmentByTag(OpenResultsFragment.TAG);
//                        if (openResultsFragment != null) {
//                            // panel is sliding and the openResultsFragment is present
//                            slidingUpPanelLayout.setAnchorPoint(1.0f);
//                            changeSliderFragment(ListResultsFragment.TAG);
//
//                        }


                        ListResultsFragment listResultsFragment = (ListResultsFragment) fragmentManager.findFragmentByTag(ListResultsFragment.TAG);
                        if (listResultsFragment != null) {
                            // panel is sliding and the listResultsFragment is present
                        }

                        SelectedEventFragment selectedEventFragment = (SelectedEventFragment) fragmentManager.findFragmentByTag(SelectedEventFragment.TAG);
                        if (selectedEventFragment != null) {
                            // panel is sliding and the selectedEventFragment is present
//                            hideFabButtoms();
//                            changeSliderFragment(SelectedEventExpandedFragment.TAG);

                            if(slidingUpPanelLayout.getAnchorPoint() == 0.6f && slideOffset < 0.6f){
                                directionsmiddlefab.setVisibility(View.VISIBLE);
                                float middleFabShowValue = mapRange(0.0f,slidingUpPanelLayout.getAnchorPoint(),0.0f,1.0f,slideOffset);
                                directionsmiddlefab.setAlpha(middleFabShowValue);

                            }

                        }

                        SelectedEventExpandedFragment selectedEventExpandedFragment = (SelectedEventExpandedFragment) fragmentManager.findFragmentByTag(SelectedEventExpandedFragment.TAG);
                        if (selectedEventExpandedFragment != null){
                            // panel is sliding and the selectedEventExpandedFragment is present
//                            hideFabButtoms();
                            // set the middle button to show when
                            // achored to middle



                            // when set to PanelState.Achored
                            // bind alpha to inverse of anchored slidOffset (min) to expanded slideOffset (max)
                            if(slidingUpPanelLayout.getAnchorPoint() == 0.6f && slideOffset < 0.6f){
                                directionsmiddlefab.setVisibility(View.VISIBLE);
                                float middleFabShowValue = mapRange(0.0f,slidingUpPanelLayout.getAnchorPoint(),0.0f,1.0f,slideOffset);
                                directionsmiddlefab.setAlpha(middleFabShowValue);

                            }else if(slidingUpPanelLayout.getAnchorPoint() == 0.6f &&
                                    slideOffset > 0.6f &&
                                    slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.DRAGGING
                                    ){
                                directionsmiddlefab.setVisibility(View.VISIBLE);
                                float middleFabShowValue = mapRange(slidingUpPanelLayout.getAnchorPoint(),1.0f,0.0f,1.0f,slideOffset);

                                directionsmiddlefab.setAlpha(inverseAlpha(middleFabShowValue));


                            }else {
                                Log.i(TAG,String.valueOf(slidingUpPanelLayout.getAnchorPoint()));
                            }

                            //


                        }



                    }
                }
//                Log.i(TAG, "FAB alpha: " + currentLocationFab.getAlpha());


//                currentLocationFab.setAlpha(slideOffset);
                if(slideOffset > 0){
                    if(firstOffsetCheck) {
                        // here


                        OpenResultsFragment openResultsFragment = (OpenResultsFragment) fragmentManager.findFragmentByTag(OpenResultsFragment.TAG);
                        FilterFragment filterFragment = (FilterFragment) fragmentManager.findFragmentByTag(FilterFragment.TAG);
                        if(filterFragment != null){
                            Log.i("filter open", "Opening filter");

                        }
                        if (openResultsFragment != null) {
                            // panel is expanding and the openResultsFragment is present
                            slidingUpPanelLayout.setAnchorPoint(1.0f);
                            filterFab.setVisibility(View.GONE);
                            changeSliderFragment(ListResultsFragment.TAG);

                        }

                        ListResultsFragment listResultsFragment = (ListResultsFragment) fragmentManager.findFragmentByTag(ListResultsFragment.TAG);
                        if (listResultsFragment != null) {
                            // panel is expanding and the listResultsFragment is present
                        }

                        SelectedEventFragment selectedEventFragment = (SelectedEventFragment) fragmentManager.findFragmentByTag(SelectedEventFragment.TAG);
                        if (selectedEventFragment != null) {
                            // panel is expanding and the selectedEventFragment is present
                            hideFabButtoms();
                            changeSliderFragment(SelectedEventExpandedFragment.TAG);

                        }

                        SelectedEventExpandedFragment selectedEventExpandedFragment = (SelectedEventExpandedFragment) fragmentManager.findFragmentByTag(SelectedEventExpandedFragment.TAG);
                        if (selectedEventExpandedFragment != null){
                            // panel is expanding and the selectedEventExpandedFragment is present
                            hideFabButtoms();

                        }


                        firstOffsetCheck = false;

                    }

                }
            }



            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");
                firstOffsetCheck = true;

                FragmentManager fragmentManager = ((NavigationActivity) getActivity()).getSupportFragmentManager();
                SelectedEventExpandedFragment selectedEventExpandedFragment = (SelectedEventExpandedFragment) fragmentManager.findFragmentByTag(SelectedEventExpandedFragment.TAG);
                if (selectedEventExpandedFragment != null){
                    // panel is expanded and the selectedEventExpandedFragment is present
                    directionsmiddlefab.setVisibility(View.GONE);

                }
                SearchResultsListFragment searchResultsListFragment = (SearchResultsListFragment) fragmentManager.findFragmentByTag(SearchResultsListFragment.TAG);
                if(searchResultsListFragment != null){
                    Log.i("onPanelExpanded", "isActionViewExpanded: " + ((NavigationActivity)getActivity()).searchMenuItem.isActionViewExpanded());
                }
                FilterFragment filterFragment = (FilterFragment) fragmentManager.findFragmentByTag(FilterFragment.TAG);
                if(filterFragment!= null){
//                    slidingUpPanelLayout.setEnabled(false);
                }

            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");
                firstOffsetCheck = true;

                directionsmiddlefab.setVisibility(View.GONE);
                currentLocationFab.setVisibility(View.VISIBLE);


                FragmentManager fragmentManager = ((NavigationActivity) getActivity()).getSupportFragmentManager();
                ListResultsFragment listResultsFragment = (ListResultsFragment) fragmentManager.findFragmentByTag(ListResultsFragment.TAG);
                if (listResultsFragment != null) {
                    // panel is expanding and the listResultsFragment is present
                    changeSliderFragment(OpenResultsFragment.TAG);
                }

                SelectedEventExpandedFragment selectedEventExpandedFragment = (SelectedEventExpandedFragment) fragmentManager.findFragmentByTag(SelectedEventExpandedFragment.TAG);
                if (selectedEventExpandedFragment != null){
                    // panel is expanding and the selectedEventExpandedFragment is present
                    // hide mid-fab
                    changeSliderFragment(SelectedEventFragment.TAG);

                }

                FilterFragment filterFragment = (FilterFragment) fragmentManager.findFragmentByTag(FilterFragment.TAG);
                if(filterFragment != null){
                    changeSliderFragment(OpenResultsFragment.TAG);
                }
                SearchResultsListFragment searchResultsListFragment = (SearchResultsListFragment) fragmentManager.findFragmentByTag(SearchResultsListFragment.TAG);
                if(searchResultsListFragment != null){
                    ((NavigationActivity)getActivity()).closeSearchView();
                    changeSliderFragment(OpenResultsFragment.TAG);
                }
                reloadMarkers();
            }

            @Override
            public void onPanelAnchored(View panel) {

                Log.i(TAG, "onPanelAnchored");
//                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                FragmentManager fragmentManager = ((NavigationActivity) getActivity()).getSupportFragmentManager();
                SelectedEventExpandedFragment selectedEventExpandedFragment = (SelectedEventExpandedFragment) fragmentManager.findFragmentByTag(SelectedEventExpandedFragment.TAG);
                if (selectedEventExpandedFragment != null){
                    // panel is expanding and the selectedEventExpandedFragment is present
                    // show mid-fab
//                    directionsmiddlefab.setVisibility(View.VISIBLE);
                    if(mMap!= null){
                        Event event = getSelectedEvent();
                        if(event != null) {
                            LatLng eventll = new LatLng(event.getPoint().getPosition().getLatitude(), event.getPoint().getPosition().getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(eventll,11));//,2000,null);
//                    mMap.animateCamera(CameraUpdateFactory.zoomBy(13));
                        }
                    }



                }


                firstOffsetCheck = true;

            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
                firstOffsetCheck = true;

            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        NavigationActivity navigationActivity = (NavigationActivity) getActivity();
        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // move the map up
//                    SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
//                            .findFragmentById(R.id.map);
//                    mapFragment.setLa
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mapFragment.getView().getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    layoutParams.weight = 4;
                    mapFragment.getView().setLayoutParams(layoutParams);

        updateValuesFromBundle(savedInstanceState);


        // Get the button view
//        View locationButton = ((View) rootView.findViewById(R.id.map).findViewById(1).getParent()).findViewById(2);
//
//        // and next place it, for exemple, on bottom right (as Google Maps app)
//        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
//        // position on right bottom
//        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
//        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//        rlp.setMargins(0, 0, 30, 30);

        return rootView;

    }


    SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(newText.length() > 2) {
                if(((NavigationActivity)getActivity()).isFavoritesView()){
                    ((NavigationActivity) getActivity()).getFavoriteEventsByText(newText);
                }else {
                    ((NavigationActivity) getActivity()).getEventsByText(newText);
                }
            }
            return true;
        }
    };



    MenuItemCompat.OnActionExpandListener onActionExpandListener = new MenuItemCompat.OnActionExpandListener() {
        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            Log.i("NavigationActivity","Action expand start");
            FragmentManager fragmentManager = ((NavigationActivity)getActivity()).getSupportFragmentManager();

            EventLocatorFragment eventLocatorFragment = (EventLocatorFragment) fragmentManager.findFragmentByTag(EventLocatorFragment.TAG);
            if(eventLocatorFragment != null) {
                eventLocatorFragment.changeSliderFragment(SearchResultsListFragment.TAG);
            }
            Log.i("NavigationActivity","Action expand end");


            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            Log.i("NavigationActivity","Action collapse end");

            Log.i("NavigationActivity","Action collapse complete");

            return true;
        }
    };

    SearchView searchView;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
////        getActivity().invalidateOptionsMenu();
//        final MenuItem filter = menu.findItem(R.id.action_search);
//
//        filter.setVisible(true);
//
//        // Associate searchable configuration with the SearchView
//        SearchManager searchManager =
//                (SearchManager) ((NavigationActivity)getActivity()).getSystemService(Context.SEARCH_SERVICE);
//
////        searchMenuItem.setOnActionExpandListener(onActionExpandListener);
//        searchView =
//                (SearchView) filter.getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(((NavigationActivity)getActivity()).getComponentName()));
//
//        searchView.setOnQueryTextListener(onQueryTextListener);
//
//        if (filter != null) {
//            MenuItemCompat.setOnActionExpandListener(filter,onActionExpandListener);
//            MenuItemCompat.setActionView(filter, searchView);
//        }
//
//        final TextView toolbarEditText = (TextView)((NavigationActivity)getActivity()).findViewById(R.id.toolbar_title);
//        toolbarEditText.setOnClickListener(new View.OnClickListener() {
//                                               @Override
//                                               public void onClick(View v) {
//                                                   filter.expandActionView();
//                                               }
//
//                                           }
//
//        );

        super.onPrepareOptionsMenu(menu);
//        getActivity().invalidateOptionsMenu();
        MenuItem filter = menu.findItem(R.id.action_search);

        filter.setVisible(true);

    }


    public float mapRange(float oldMin,
                                 float oldMax,
                                 float newMin,
                                 float newMax,
                                 float incomingValue){
        // check for 0
        if(incomingValue == 0.0f){
            return newMin;
        }else{
            return (incomingValue-oldMin)/(oldMax-oldMin)*(newMax-newMin)+newMin;
        }
    }

    public float inverseAlpha(float incoming){
        return 1.0f - incoming;
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
//                setButtonsEnabledState();
            }

            // Update the value of currentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                currentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }
            updateUI();
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;

        ((NavigationActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onChildViewClick(View view) {
        switch (view.getId())
        {
            case R.id.openresults:
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
//                slidingUpPanelLayout.setAnchorPoint(0.7f);
//                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                break;
        }

    }

    @Override
    public void OnChildViewAttached(String fragmentTag) {

        if(slidingUpPanelLayout != null) {
            switch (fragmentTag) {
                case OpenResultsFragment.TAG:
                    slidingUpPanelLayout.setEnabled(true);
                    hideFabButtoms();
                    filterFab.setVisibility(View.VISIBLE);
                    slidingUpPanelLayout.setAnchorPoint(1.0f);
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                    break;
                case ListResultsFragment.TAG:
//                directionsmiddlefab.setVisibility(View.GONE);
                    hideFabButtoms();
                    filterFab.setVisibility(View.GONE);
                    slidingUpPanelLayout.setAnchorPoint(1.0f);
                    break;
                case SelectedEventFragment.TAG:
//                directionsmiddlefab.setVisibility(View.GONE);
                    showFabButtons();
                    slidingUpPanelLayout.setAnchorPoint(0.6f);
                    break;
                case SelectedEventExpandedFragment.TAG:
                    hideFabButtoms();
                    filterFab.setVisibility(View.GONE);
                    slidingUpPanelLayout.setAnchorPoint(0.6f);
                    break;
                case SearchResultsListFragment.TAG:
                    directionsmiddlefab.setVisibility(View.GONE);
                    directionsfab.setVisibility(View.GONE);
                    filterFab.setVisibility(View.GONE);
                    currentLocationFab.setVisibility(View.GONE);
                    slidingUpPanelLayout.setAnchorPoint(1.0f);
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    break;
                case FilterFragment.TAG:
                    slidingUpPanelLayout.setAnchorPoint(1.0f);
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    filterFab.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    public void changeSliderFragment(String fragmentTag){

        FragmentManager fragmentManager = ((NavigationActivity)getActivity()).getSupportFragmentManager();

        switch (fragmentTag){
            case OpenResultsFragment.TAG:
                //something
//                ((NavigationActivity)getActivity()).getSupportActionBar().show();
                showToolbar();
                fragmentManager.beginTransaction().replace(R.id.slidecontainer,OpenResultsFragment.newInstance(EventLocatorFragment.TAG),OpenResultsFragment.TAG).commit();
                break;
            case ListResultsFragment.TAG:
                // something
                slidingUpPanelLayout.setAnchorPoint(1.0f);
//                ((NavigationActivity)getActivity()).getSupportActionBar().hide();
                hideToolbar();
                fragmentManager.beginTransaction().replace(R.id.slidecontainer,ListResultsFragment.newInstance(EventLocatorFragment.TAG),ListResultsFragment.TAG).commit();

                break;
            case SelectedEventFragment.TAG:
                // something

//                ((NavigationActivity)getActivity()).getSupportActionBar().show();
                showToolbar();
                fragmentManager.beginTransaction().replace(R.id.slidecontainer,SelectedEventFragment.newInstance(EventLocatorFragment.TAG, getSelectedEvent()),SelectedEventFragment.TAG).commit();
                break;

            case SelectedEventExpandedFragment.TAG:
//                ((NavigationActivity)getActivity()).getSupportActionBar().hide();
                hideToolbar();
                slidingUpPanelLayout.setAnchorPoint(0.6f);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                fragmentManager.beginTransaction().replace(R.id.slidecontainer, SelectedEventExpandedFragment.newInstance(EventLocatorFragment.TAG, getSelectedEvent()), SelectedEventExpandedFragment.TAG).commit();
                break;
            case SearchResultsListFragment.TAG:

                fragmentManager.beginTransaction().replace(R.id.slidecontainer,SearchResultsListFragment.newInstance(EventLocatorFragment.TAG),SearchResultsListFragment.TAG).commit();


                break;
            case FilterFragment.TAG:

                // change frag
                hideToolbar();

                fragmentManager.beginTransaction().replace(R.id.slidecontainer,FilterFragment.newInstance(EventLocatorFragment.TAG),FilterFragment.TAG).commit();

                break;

        }



    }




    public void hideFabButtoms(){
//        currentLocationFab.setVisibility(View.GONE);
        if(directionsfab != null) {
            directionsfab.setVisibility(View.GONE);
        }
    }

    public void showFabButtons(){
//        currentLocationFab.setVisibility(View.VISIBLE);
        directionsfab.setVisibility(View.VISIBLE);
    }

    public void showToolbar(){
        CardView toolbar = ((NavigationActivity)getActivity()).getToolbarContainer();
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
    }

    public void hideToolbar(){
        CardView toolbar = ((NavigationActivity)getActivity()).getToolbarContainer();
        toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final List<Event> eventList = ((NavigationActivity)getActivity()).getEventsList();



//        getEventsFromPolygon();

//        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setInfoWindowAdapter(new MapWindowAdapter(getActivity()));


        GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                FragmentManager fragmentManager = ((NavigationActivity)getActivity()).getSupportFragmentManager();
                OpenResultsFragment openResultsFragment = (OpenResultsFragment) fragmentManager.findFragmentByTag(OpenResultsFragment.TAG);


                NavigationActivity navigationActivity = ((NavigationActivity)getActivity());



                Log.i(TAG, "map clicked");
                Event event = getSelectedEvent();
                if(navigationActivity.isSearchExpanded()){
                    navigationActivity.closeSearchView();

                } else if(slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED && event != null){

                    changeSliderFragment(SelectedEventFragment.TAG);
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    CardView toolbar = ((NavigationActivity)getActivity()).getToolbarContainer();
                    toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                    currentLocationFab.setVisibility(View.VISIBLE);

                }else if(event != null){

                    changeSliderFragment(OpenResultsFragment.TAG);
                    HashMap<String,Marker> markers = ((NavigationActivity)getActivity()).getMarkers();
                    String selectedMarkerKey = event.getId();
                    if(markers.containsKey(selectedMarkerKey)){
                        // get the selected marker
                        Marker selectedMarker = markers.get(selectedMarkerKey);
//
//                        // unselect the marker (set to red)
                        selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                        // Empty the current marker key (there is nothing selected)
                        ((NavigationActivity)getActivity()).setSelectedEvent(null);

                    }
                }else if(slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED && getSelectedEvent() == null){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);


                    hideToolbar();
//                    ((NavigationActivity) getActivity()).getSupportActionBar().hide();
                    currentLocationFab.setVisibility(View.GONE);

                    filterFab.setVisibility(View.GONE);
                    hideFabButtoms();


                }else if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                   showToolbar();
//                    ((NavigationActivity) getActivity()).getSupportActionBar().show();
                    currentLocationFab.setVisibility(View.VISIBLE);
                    if (openResultsFragment != null) {
                        filterFab.setVisibility(View.VISIBLE);
                    }
//                    showFabButtons();
                }else if(slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED){
                    currentLocationFab.setVisibility(View.VISIBLE);
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);



                }




            }
        };

        mMap.setOnMapClickListener(mapClickListener);


        GoogleMap.OnCameraChangeListener cameraChangeListener = new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if(!firstmapmove) {
                    if(((NavigationActivity)getActivity()).isFavoritesView()){
                        getFavoriteEventsFromPolygon();
                    }else {
                        getEventsFromPolygon();
                    }
                }else{
                    firstmapmove = false;
                }
            }
        };
        mMap.setOnCameraChangeListener(cameraChangeListener);

        GoogleMap.OnMarkerClickListener listener = new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                HashMap<String, Marker> markers = ((NavigationActivity)getActivity()).getMarkers();
                Event currentEvent = ((NavigationActivity)getActivity()).getSelectedEvent();
//                String currentMarkerKey = currentEvent.getId();
                // For debugging
                List<Event> debugEvents = ((NavigationActivity)getActivity()).getEventsList();

                // Using the listener marker get the marker key (event id) in the Hashmap
                for (Map.Entry<String, Marker> entry : markers.entrySet()) {
                    if(entry.getValue().getId().equals(marker.getId())){
                        // Store the selected marker key
                        String newMarkerKey = entry.getKey();
                        // Only change the markerkey if it is new
                        if(currentEvent == null){
                            // get the selected event
                            for(Event event : ((NavigationActivity)getActivity()).getEventsList()){
                                if(event.getId().equals(newMarkerKey)){
                                    // Store the selected event
                                    ((NavigationActivity) getActivity()).setSelectedEvent(event);

                                }
                            }
                            // Set the selected marker to green
//                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        }else if(!newMarkerKey.equals(currentEvent.getId())) {
                            // Change the previous selected marker back to red
                            if(markers.containsKey(currentEvent.getId())) {
                                Marker previousSelectedMarker = markers.get(currentEvent.getId());
                                previousSelectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                // Set the selected marker to green
//                                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            }

                            // get the selected event
                            for(Event event : ((NavigationActivity)getActivity()).getEventsList()){
                                if(event.getId().equals(newMarkerKey)){
                                    // Store the selected event
                                    ((NavigationActivity) getActivity()).setSelectedEvent(event);
                                }
                            }

                            // Zoom to selected marker
//                            marker.showInfoWindow();
                            LatLng markerLatLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,11));
                        }
                    }
                }


                //If you touch a marker and the pannel is hidden
                if(slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN){
                    //Set the pannel to collapsed
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }


                // change the selected
//                selectedEvent = clickedEvent;
                // Change Fragment if needed
                if(slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED){
                    changeSliderFragment(SelectedEventExpandedFragment.TAG);
                }else{
                    changeSliderFragment(SelectedEventFragment.TAG);
                }
                return false;

                // first attempt
//                Event clickedEvent = null;
//                List<Event> eventList = ((NavigationActivity)getActivity()).getEventsList();
//                for(Event event: eventList){
//                    if (event.getMarkerId() != null && event.getMarkerId().equals(marker.getId())){
//                        clickedEvent = event;
//                    }
//                }
//                if(slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN){
//                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//                }
//
//                if(selectedMarker != null){
//                    // change the currently selected item back to red
//                    selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//
//                }
//                selectedMarker = marker;
//                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//
//                if(clickedEvent != null) {
//                    LatLng eventll = new LatLng(clickedEvent.getPoint().getPosition().getLatitude(), clickedEvent.getPoint().getPosition().getLongitude());
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(eventll,11));//,2000,null);
////                    mMap.animateCamera(CameraUpdateFactory.zoomBy(13));
//                }
//
//                // Change Fragment;
//                selectedEvent = clickedEvent;
//                if(slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED){
//                    changeSliderFragment(SelectedEventExpandedFragment.TAG);
//                }else{
//                    changeSliderFragment(SelectedEventFragment.TAG);
//                }
//                return false;
            }
        };
        mMap.setOnMarkerClickListener(listener);


//        updateMapWithPlaces();

    }

    public void unselectMarker(Event event){
        HashMap<String,Marker> markers = ((NavigationActivity)getActivity()).getMarkers();
        String selectedMarkerKey = event.getId();
        if(markers.containsKey(selectedMarkerKey)){
            // get the selected marker
            Marker selectedMarker = markers.get(selectedMarkerKey);
//
//                        // unselect the marker (set to red)
            selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                        // Empty the current marker key (there is nothing selected)
            ((NavigationActivity)getActivity()).setSelectedEvent(null);

        }
    }


    public List<Double[]> getCorners(){
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

                /*
                *
                * [0] = ne
		        * [1] = nw
		        * [2] = sw
		        * [3] = se
		        *
		        */

        Double[] neCorner = {bounds.northeast.longitude, bounds.northeast.latitude};
        Double[] nwCorner = {bounds.southwest.longitude, bounds.northeast.latitude};
        Double[] swCorner = {bounds.southwest.longitude, bounds.southwest.latitude};
        Double[] seCorner = {bounds.northeast.longitude, bounds.southwest.latitude};

        List<Double[]> corners = new ArrayList<Double[]>();
        corners.add(neCorner);
        corners.add(nwCorner);
        corners.add(swCorner);
        corners.add(seCorner);
        return corners;
    }

    public void getEventsFromPolygon(){

        Log.i(EventLocatorFragment.TAG,"getEventsFromPolygon");
        // Call api
        ((NavigationActivity)getActivity()).getEventsByPolygon(getCorners());
    }
    public void getFavoriteEventsFromPolygon(){
        Log.i(EventLocatorFragment.TAG,"getEventsFromPolygon");
        // Call api
        ((NavigationActivity)getActivity()).getFavoriteEventsByPolygon(getCorners());
    }

    public void updateSearchResultsList(){

        // get the fragment

        FragmentManager fragmentManager = ((NavigationActivity) getActivity()).getSupportFragmentManager();
        SearchResultsListFragment searchResultsListFragment = (SearchResultsListFragment) fragmentManager.findFragmentByTag(SearchResultsListFragment.TAG);
        if(searchResultsListFragment != null){
            Log.i("updateSearchResults", "notifydatachanged");
            searchResultsListFragment.updateSearchResultsList();
        }

        // notifydatachanged

    }



    public void reloadMarkers(){
        if(((NavigationActivity)getActivity()).isFavoritesView()){
            getFavoriteEventsFromPolygon();
        }else {
            getEventsFromPolygon();
        }
    }

    public void clearAndRefreshMap(){
        if(mMap != null) {
            mMap.clear();
        }

        ((NavigationActivity)getActivity()).setMarkers(new HashMap<String, Marker>());

        if(((NavigationActivity)getActivity()).isFavoritesView()){
            getFavoriteEventsFromPolygon();
        }else {
            getEventsFromPolygon();
        }
    }

    public void updateMapWithPlaces(){

        // http://stackoverflow.com/questions/30548694/android-maps-refresh-position-of-marker
        if(mMap != null) {
            HashMap<String, Marker> updatedMarkers = new HashMap<String, Marker>();
            HashMap<String, Marker> markers = ((NavigationActivity) getActivity()).getMarkers();
            for (Event event : ((NavigationActivity) getActivity()).getEventsList()) {
                // if marker exists move its location, if not add new marker
                Marker marker = markers.get(event.getId());
                if (marker == null) {
                    LatLng eventll = new LatLng(event.getPoint().getPosition().getLatitude(), event.getPoint().getPosition().getLongitude());
                    marker = mMap.addMarker(new MarkerOptions().position(eventll));
                } else {
                    LatLng eventll = new LatLng(event.getPoint().getPosition().getLatitude(), event.getPoint().getPosition().getLongitude());
                    marker.setPosition(eventll);
                    markers.remove(event.getId());
                }
                updatedMarkers.put(event.getId(), marker);
            }
            // all markers that are left in markers list need to be deleted from the map
            for (Marker marker : markers.values()) {
                marker.remove();
            }
            ((NavigationActivity) getActivity()).setMarkers(updatedMarkers);
            HashMap<String,Marker> debugMap = ((NavigationActivity)getActivity()).getMarkers();
            Event selectedEvent = getSelectedEvent();
            if(selectedEvent != null){
                // check if in markers
                if(updatedMarkers.containsKey(selectedEvent.getId())){
                    // if marker is not selected, color as a selected item
                    Marker selectedMarker = updatedMarkers.get(selectedEvent.getId());
                    selectedMarker.setPosition(selectedMarker.getPosition());
                    selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                    mMap.setInfoWindowAdapter(new MapWindowAdapter(getActivity()));
//                    selectedMarker.showInfoWindow();
//                    selectedMarker.remove();
//                    mMap.addMarker(new MarkerOptions().position(selectedMarker.getPosition()));
//                    selectedMarker.setIcon(((NavigationActivity)getActivity()).getMapIconWithColor(R.drawable.ic_event_black_24dp,BitmapDescriptorFactory.HUE_GREEN));
                }

            }


//        markers = updatedMarkers;
        }

        // first attempt - having problems keeping track of map items
        // clear and recreate markers doesn't work well
//        List<Event> eventList = ((NavigationActivity)getActivity()).getEventsList();
//        if(mMap != null) {
//            LatLng lastEvent = null;
//            mMap.clear();
//            for (int i = 0; i < eventList.size(); i++){
//                Event event = eventList.get(i);
//
//                LatLng eventll = new LatLng(event.getPoint().getPosition().getLatitude(), event.getPoint().getPosition().getLongitude());
//                Marker marker = mMap.addMarker(new MarkerOptions().position(eventll));//.title(event.getTitle()));
//                event.setMarkerId(marker.getId());
//                if(marker.getPosition().equals(selectedMarker.getPosition())){
//                    selectedMarker = null;
//                    selectedMarker = marker;
//                }
//                lastEvent = eventll;
//            }
//
////            if (lastEvent != null) {
////                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastEvent, 9));//,2000,null);
////            }
//
//            if(selectedMarker != null){
//                // change the currently selected item back to red
//                selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//
//            }
//        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }





}
