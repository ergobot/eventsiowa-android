package com.placesware.placesiowa;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FilterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class FilterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static String PARENT_TAG = "none";

    public static final String TAG = "FilterFragment";


    // TODO: Rename and change types of parameters
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
    public static FilterFragment newInstance(String param1, String param2) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Get the filter settings from NavigationActivity

        Fragment parent = getFragmentManager().findFragmentByTag(PARENT_TAG);

        onAttachFragment(parent);
        onChildViewAttachedListener.OnChildViewAttached(FilterFragment.TAG);

    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    // set the parent of the
    public static FilterFragment newInstance(String param1) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        PARENT_TAG = param1;
        args.putString(PARENT_TAG, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public static FilterFragment newInstance(int sectionNumber) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public static FilterFragment newInstance() {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, -1);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
//        getActivity().invalidateOptionsMenu();
        MenuItem filter = menu.findItem(R.id.action_search);

        filter.setVisible(false);

    }

//    TextView openResults;

    public void showToolbar(){
        CardView toolbar = ((NavigationActivity)getActivity()).getToolbarContainer();
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
    }

    public void hideToolbar(){
        CardView toolbar = ((NavigationActivity)getActivity()).getToolbarContainer();
        toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
    }

    TextView startDate;
    TextView endDate;
    SwitchCompat filterSwitch;
    Button resetFilter;
//    TextView startTime;
//    TextView endTime;

    View.OnClickListener startDateClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ((NavigationActivity)getActivity()).datePickerDialog.setVibrate(true);
            ((NavigationActivity)getActivity()).datePickerDialog.setYearRange(1985, 2028);
            ((NavigationActivity)getActivity()).datePickerDialog.setCloseOnSingleTapDay(true);
            ((NavigationActivity)getActivity()).datePickerDialog.show(navigationActivity.getSupportFragmentManager(), "startDate");
        }
    };

    View.OnClickListener endDateClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ((NavigationActivity)getActivity()).datePickerDialog.setVibrate(true);
            ((NavigationActivity)getActivity()).datePickerDialog.setYearRange(1985, 2028);
            ((NavigationActivity)getActivity()).datePickerDialog.setCloseOnSingleTapDay(true);
            ((NavigationActivity)getActivity()).datePickerDialog.show(navigationActivity.getSupportFragmentManager(), "endDate");
        }
    };

    NavigationActivity navigationActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        FragmentManager fragmentManager = ((NavigationActivity) getActivity()).getSupportFragmentManager();
//        EventLocatorFragment eventLocatorFragment = (EventLocatorFragment) fragmentManager.findFragmentByTag(EventLocatorFragment.TAG);
//        if(eventLocatorFragment != null){
//            eventLocatorFragment.getSlidingUpPanelLayout().setTouchEnabled(false);
//        }

        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_event_locator, container, false);
        setHasOptionsMenu(true);
        final View rootView = inflater.inflate(R.layout.fragment_preferences,container,false);

        navigationActivity = ((NavigationActivity)getActivity());

        startDate = (TextView)rootView.findViewById(R.id.startdateinput);
        endDate = (TextView)rootView.findViewById(R.id.enddateinput);

        long startL = ((NavigationActivity)getActivity()).getStartDate();
        long endL = ((NavigationActivity)getActivity()).getEndDate();
        Date start = null;
        Date end = null;
        if(startL == -1L){
            start = new Date();
        }else{
            start = new Date(startL);
        }

        if(endL == -1L){
            end = new Date();
        }else{
            end = new Date(endL);
        }
//        Date start = new Date(startL);
//        Date end = new Date(endL);
        if(startL == -1L && endL == -1L){
            setStartDate("Today");
            setEndDate("Today");
        }else {
            // End can't be in the future, that would through CommonTimeMovementBeliefException...
            if(end.getTime() < start.getTime()){
                end = start;
            }

            String startText = new SimpleDateFormat("M/dd/yyyy").format(start);
            String endText = new SimpleDateFormat("M/dd/yyyy").format(end);
            setStartDate(startText);
            setEndDate(endText);
        }


//        startTime = (TextView)rootView.findViewById(R.id.starttimeinput);
//        endTime = (TextView)rootView.findViewById(R.id.endtimeinput);

        if(navigationActivity.getFilterEnabled()) {
            rootView.findViewById(R.id.startdatelayout).setOnClickListener(startDateClick);
            rootView.findViewById(R.id.enddatelayout).setOnClickListener(endDateClick);

            CardView datefiltercard = (CardView) rootView.findViewById(R.id.datefiltercard);
            datefiltercard.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
        } else{
            CardView datefiltercard = (CardView) rootView.findViewById(R.id.datefiltercard);
            datefiltercard.setCardBackgroundColor(getResources().getColor(R.color.colorDivider));

            rootView.findViewById(R.id.startdatelayout).setOnClickListener(null);
            rootView.findViewById(R.id.enddatelayout).setOnClickListener(null);
        }


        filterSwitch = (SwitchCompat) rootView.findViewById(R.id.filterswitch);
        filterSwitch.setChecked(navigationActivity.getFilterEnabled());

        filterSwitch.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                navigationActivity.setFilterEnabled(isChecked);
                if(isChecked){
                    CardView datefiltercard = (CardView) rootView.findViewById(R.id.datefiltercard);
                    datefiltercard.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));

                    rootView.findViewById(R.id.startdatelayout).setOnClickListener(startDateClick);
                    rootView.findViewById(R.id.enddatelayout).setOnClickListener(endDateClick);
                }else {
                    CardView datefiltercard = (CardView) rootView.findViewById(R.id.datefiltercard);
                    datefiltercard.setCardBackgroundColor(getResources().getColor(R.color.colorDivider));

                    rootView.findViewById(R.id.startdatelayout).setOnClickListener(null);
                    rootView.findViewById(R.id.enddatelayout).setOnClickListener(null);
                }

            }
        });

        resetFilter = (Button) rootView.findViewById(R.id.resetfilter);
        resetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navigationActivity.resetStartDate();
                navigationActivity.resetEndDate();
                setStartDate("Today");
                setEndDate("Today");

//                FragmentManager fragmentManager = ((NavigationActivity) getActivity()).getSupportFragmentManager();
//                EventLocatorFragment eventLocatorFragment = (EventLocatorFragment) fragmentManager.findFragmentByTag(EventLocatorFragment.TAG);
//                if(eventLocatorFragment != null){
//                    eventLocatorFragment.getSlidingUpPanelLayout().setTouchEnabled(false);
//                }


            }
        });


//        rootView.findViewById(R.id.starttimelayout).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                navigationActivity.startTimePickerDialog.setVibrate(true);
//                navigationActivity.startTimePickerDialog.setCloseOnSingleTapMinute(true);
//                navigationActivity.startTimePickerDialog.show(navigationActivity.getSupportFragmentManager(), "startTime");
//            }
//        });
//
//        rootView.findViewById(R.id.endtimelayout).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                navigationActivity.endTimePickerDialog.setVibrate(true);
//                navigationActivity.endTimePickerDialog.setCloseOnSingleTapMinute(true);
//                navigationActivity.endTimePickerDialog.show(navigationActivity.getSupportFragmentManager(), "endTime");
//            }
//        });


        return rootView;

    }

    public void setStartDate(String incoming){
        startDate.setText(incoming);
    }

    public void setEndDate(String incoming){
        endDate.setText(incoming);
    }


    public void setStartDate(Date incoming){

//        (1+month) + "/" + day + "/" + year

        startDate.setText(new SimpleDateFormat("M/dd/yyyy").format(incoming));
    }

    public void setEndDate(Date incoming){

        endDate.setText(new SimpleDateFormat("M/dd/yyyy").format(incoming));
    }

//    public void setStartTime(String incoming){
//        startTime.setText(incoming);
//    }
//
//    public void setEndTime(String incoming){
//        endTime.setText(incoming);
//    }



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
////    OnChildViewClickListener onChildViewClickListener;
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
