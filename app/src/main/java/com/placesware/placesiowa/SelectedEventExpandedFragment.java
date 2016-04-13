package com.placesware.placesiowa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.placesware.placesiowa.event.Event;
import com.placesware.placesiowa.event.Favorite;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectedEventExpandedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectedEventExpandedFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SelectedEventExpandedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static String PARENT_TAG = "none";

    public static final String TAG = "SelectedEventExpandedFragment";
    private static Event selectedEvent;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventLocatorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectedEventExpandedFragment newInstance(String param1, String param2) {
        SelectedEventExpandedFragment fragment = new SelectedEventExpandedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public SelectedEventExpandedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Fragment parent = getFragmentManager().findFragmentByTag(PARENT_TAG);

        onAttachFragment(parent);
        onChildViewAttachedListener.OnChildViewAttached(SelectedEventExpandedFragment.TAG);

    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    // set the parent of the
    public static SelectedEventExpandedFragment newInstance(String param1) {
        SelectedEventExpandedFragment fragment = new SelectedEventExpandedFragment();
        Bundle args = new Bundle();
        PARENT_TAG = param1;
        args.putString(PARENT_TAG, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public static SelectedEventExpandedFragment newInstance(String param1, Event event) {
        if(event == null){
            throw new NullPointerException("SelectedEventExpandedFragment can't have null event");
        }
        SelectedEventExpandedFragment fragment = new SelectedEventExpandedFragment();
        Bundle args = new Bundle();
        PARENT_TAG = param1;
        args.putString(PARENT_TAG, param1);
        fragment.setArguments(args);
        selectedEvent = event;
        return fragment;
    }

    public static SelectedEventExpandedFragment newInstance() {
        SelectedEventExpandedFragment fragment = new SelectedEventExpandedFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, -1);
        fragment.setArguments(args);
        return fragment;
    }

    // header
    TextView eventTitleHeader;
    TextView eventTypeHeader;

    // middle buttons
    Button leftButton;
    Button middleButton;
    Button rightButton;

    // Event information in rows
    TextView eventAddressRow;
    TextView eventDateRow;
    TextView eventPhoneRow;
    TextView eventWebsiteRow;
    TextView eventEmailRow;
    TextView eventDetailsRow;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_event_locator, container, false);

        View rootView = inflater.inflate(R.layout.fragment_selected_event_expanded,container,false);

        // header
        eventTitleHeader = (TextView)rootView.findViewById(R.id.eventtitle);
        eventTypeHeader = (TextView)rootView.findViewById(R.id.eventtype);

        // middle buttons
        leftButton = (Button)rootView.findViewById(R.id.mainbuttonone);
        middleButton = (Button)rootView.findViewById(R.id.mainbuttontwo);
        rightButton = (Button)rootView.findViewById(R.id.mainbuttonthree);

        // event information in rows
        eventAddressRow = (TextView)rootView.findViewById(R.id.eventrowaddress);
        eventDateRow = (TextView)rootView.findViewById(R.id.eventrowdate);
        eventPhoneRow = (TextView)rootView.findViewById(R.id.eventrowphone);
        eventWebsiteRow = (TextView)rootView.findViewById(R.id.eventrowwebsite);
        eventEmailRow = (TextView)rootView.findViewById(R.id.eventrowemail);
        eventDetailsRow = (TextView)rootView.findViewById(R.id.eventrowdetails);


        // header
        if(!selectedEvent.getTitle().isEmpty()){eventTitleHeader.setText(selectedEvent.getTitle());}
        eventTypeHeader.setText("Event");

        // middle buttons
        // for now its - call, save, website


        // event information in rows
        if(!selectedEvent.getLocation().isEmpty()){eventAddressRow.setText(selectedEvent.getLocation());}
        eventDateRow.setText(selectedEvent.getStartDate().toString());
        if(selectedEvent.getPhones() != null && selectedEvent.getPhones().size() > 0){
            eventPhoneRow.setText(selectedEvent.getPhones().get(0));
        }else{
            eventPhoneRow.setVisibility(View.GONE);
        }

        if(selectedEvent.getWebsites() != null && selectedEvent.getWebsites().size() > 0){
            eventWebsiteRow.setText(selectedEvent.getWebsites().get(0));
            rightButton.setEnabled(true);
            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = selectedEvent.getWebsites().get(0);
                    Intent i = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(i);
                }
            });
            eventWebsiteRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = selectedEvent.getWebsites().get(0);
                    Intent i = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(i);
                }
            });

        }else{
            eventWebsiteRow.setVisibility(View.GONE);
        }

        middleButton.setEnabled(true);
        middleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(getActivity(), "This is not available in the beta version.", Toast.LENGTH_SHORT).show();
//                selectedEvent.getObjectId()

                boolean found = false;
                for(Event event: ((NavigationActivity)getActivity()).getFavorites()){
                    if(event.getObjectId().equals(selectedEvent.getObjectId())){
                     found = true;
                    }
                }

                if(found){
                    Favorite favorite = new Favorite();
                    favorite.setEventId(selectedEvent.getObjectId().toString());
                    ((NavigationActivity)getActivity()).removeFavorite(favorite);


                }else {
                    Favorite favorite = new Favorite();
                    favorite.setEventId(selectedEvent.getObjectId().toString());
                    ((NavigationActivity) getActivity()).setFavorite(favorite);

                }
            }
        });

        refreshFavorite();

        if(selectedEvent.getEmails() != null && selectedEvent.getEmails().size() > 0){
            eventEmailRow.setText(selectedEvent.getEmails().get(0));
        }else {
            eventEmailRow.setVisibility(View.GONE);
        }

        if(!selectedEvent.getDetails().isEmpty()){
            eventDetailsRow.setText(selectedEvent.getDetails());
        }


        if(selectedEvent.getPhones() != null && selectedEvent.getPhones().size() > 0) {

            eventPhoneRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_DIAL);

                    intent.setData(Uri.parse("tel:" + selectedEvent.getPhones().get(0)));
                    startActivity(intent);
                }
            });
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);


                    intent.setData(Uri.parse("tel:" + selectedEvent.getPhones().get(0)));
                    startActivity(intent);
                }
            });
        } else{
            leftButton.setEnabled(false);

        }



        return rootView;

    }

    public void refreshFavorite(){

        List<Event> favoriteEvents = ((NavigationActivity)getActivity()).getFavorites();


        boolean found = false;
        for(Event favoriteEvent : favoriteEvents){

            if(selectedEvent.getObjectId().equals(favoriteEvent.getObjectId())){
                found = true;
            }

        }

        if(found){
            Drawable[] drawables = middleButton.getCompoundDrawables();
            Drawable wrapDrawable = DrawableCompat.wrap(drawables[1]);
            DrawableCompat.setTint(wrapDrawable, getResources().getColor(R.color.colorStar));
        }else{
            Drawable[] drawables = middleButton.getCompoundDrawables();
            Drawable wrapDrawable = DrawableCompat.wrap(drawables[1]);
            DrawableCompat.setTint(wrapDrawable, getResources().getColor(R.color.colorAccent));
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
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

//    public interface OnChildViewClickListener {
//        void onChildViewClick(View view);
//    }


    public interface OnChildViewAttachedListener {
        void OnChildViewAttached(String fragmentTag);
    }
    OnChildViewAttachedListener onChildViewAttachedListener;
//    OnChildViewClickListener onChildViewClickListener;
    public void onAttachFragment(Fragment fragment)
    {
        try
        {
//            mOnPlayerSelectionSetListener = (OnPlayerSelectionSetListener)fragment;
//            onChildViewClickListener = (OnChildViewClickListener)fragment;
            onChildViewAttachedListener = (OnChildViewAttachedListener)fragment;

        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(
                    fragment.toString() + " must implement OnPlayerSelectionSetListener");
        }
    }
}
