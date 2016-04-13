package com.placesware.placesiowa;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.placesware.placesiowa.event.Event;
import com.placesware.placesiowa.event.EventAdapter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListResultsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ListResultsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static String PARENT_TAG = "none";

    public static final String TAG = "ListResultsFragment";

    RecyclerView eventRecyclerView;
    EventAdapter eventAdapter;


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
    public static ListResultsFragment newInstance(String param1, String param2) {
        ListResultsFragment fragment = new ListResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public ListResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Log.i(TAG,TAG + " in onCreate()");

        Fragment parent = getFragmentManager().findFragmentByTag(PARENT_TAG);

        onAttachFragment(parent);
        onChildViewAttachedListener.OnChildViewAttached(ListResultsFragment.TAG);

    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    // set the parent of the
    public static ListResultsFragment newInstance(String param1) {
        ListResultsFragment fragment = new ListResultsFragment();
        Bundle args = new Bundle();
        PARENT_TAG = param1;
        args.putString(PARENT_TAG, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public static ListResultsFragment newInstance() {
        ListResultsFragment fragment = new ListResultsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, -1);
        fragment.setArguments(args);
        return fragment;
    }


//    ListView lv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list_results,container,false);

// For the transactions
        eventRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv);
        eventRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        eventRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,500);
//        FrameLayout.LayoutParams lp =
//                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 500);
//        LinearLayoutManager layoutManager = (LinearLayout)rootView.findViewById(R.id.transcationsLayout);
        eventRecyclerView.setLayoutManager(layoutManager);
//        eventRecyclerView.setLayoutParams(lp);
//        FragmentManager fragmentManager = ((NavigationActivity) getActivity()).getSupportFragmentManager();
//        EventLocatorFragment eventLocatorFragment = (EventLocatorFragment) fragmentManager.findFragmentByTag(EventLocatorFragment.TAG);
//        OpenResultsFragment openResultsFragment = (OpenResultsFragment) fragmentManager.findFragmentByTag(OpenResultsFragment.TAG);
        eventRecyclerView.setItemAnimator(new DefaultItemAnimator());
        List<Event> eventList = ((NavigationActivity)getActivity()).getEventsList();

//        testTransactions = setupDummyTranscations();
        eventAdapter = new EventAdapter(eventList,getActivity());
        eventRecyclerView.setAdapter(eventAdapter);

//        eventRecyclerView.addOnItemTouchListener(new RecyclerItem);

        eventRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), eventRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                CardView cv = ((CardView)view);
//                cv.
                Log.i("test", "test");
                Event clickedEvent = eventAdapter.getItem(position);

                FragmentManager fragmentManager = ((NavigationActivity) getActivity()).getSupportFragmentManager();
                EventLocatorFragment eventLocatorFragment = (EventLocatorFragment) fragmentManager.findFragmentByTag(EventLocatorFragment.TAG);
                eventLocatorFragment.getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                // TODO:
                // Decouple this
                // Fire event with new event key to parent, have parent set id when heard
                ((NavigationActivity)getActivity()).setSelectedEvent(clickedEvent);
                // first attempt
//                eventLocatorFragment.selectedEvent = clickedEvent;

//                Toast.makeText(getActivity(), "Clicked: " + clickedEvent.getTitle(), Toast.LENGTH_LONG).show();
                eventLocatorFragment.changeSliderFragment(SelectedEventExpandedFragment.TAG);


            }

            @Override
            public void onItemLongClick(View view, int position) {
                // ...nothing
            }
        }));


        return rootView;

    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
////        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
////                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
////                "Linux", "OS/2" };
////        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
////                android.R.layout.simple_list_item_1, values);
////        setListAdapter(adapter);
//
//        List<Event> eventList = ((NavigationActivity)getActivity()).getEventsList();
//        List<String> eventTitles = new ArrayList<String>();
//        for(Event event : eventList){
//            eventTitles.add(event.getTitle());
//        }
//
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
//                getActivity(),
//                android.R.layout.simple_list_item_1,
//                eventTitles );
//        setListAdapter(arrayAdapter);
//
//    }
//
//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        String item = (String) getListAdapter().getItem(position);
//
//        Toast.makeText(getActivity(), "You selected: " + item, Toast.LENGTH_SHORT).show();    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

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
