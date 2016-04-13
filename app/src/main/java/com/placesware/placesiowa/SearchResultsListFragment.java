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
import com.placesware.placesiowa.event.SearchResultsListAdapter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchResultsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchResultsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SearchResultsListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static String PARENT_TAG = "EventLocatorFragment";

    public static final String TAG = "SearchResultsListFragment";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    RecyclerView eventRecyclerView;
    SearchResultsListAdapter searchResultsListAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventLocatorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchResultsListFragment newInstance(String param1, String param2) {
        SearchResultsListFragment fragment = new SearchResultsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public SearchResultsListFragment() {
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
        onChildViewAttachedListener.OnChildViewAttached(SearchResultsListFragment.TAG);

        Log.i("SearchResultsListF", TAG + " in OnCreate");

    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    // set the parent of the
    public static SearchResultsListFragment newInstance(String param1) {
        SearchResultsListFragment fragment = new SearchResultsListFragment();
        Bundle args = new Bundle();
        PARENT_TAG = param1;
        args.putString(PARENT_TAG, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public static SearchResultsListFragment newInstance() {
        SearchResultsListFragment fragment = new SearchResultsListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, -1);
        fragment.setArguments(args);
        return fragment;
    }

    public void updateSearchResultsList(){
        List<Event> searchResultsList = ((NavigationActivity)getActivity()).getSearchResultsList();

        searchResultsListAdapter.swap(searchResultsList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_results_list,container,false);

        final LinearLayoutManager layoutManager = new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, null));
//        recyclerView.setAdapter(adapter);

        // For the events
        eventRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv);
        eventRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        eventRecyclerView.setHasFixedSize(true);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        eventRecyclerView.setLayoutManager(layoutManager);
        eventRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, null));

        // for debuging, using the current data
        List<Event> eventList = ((NavigationActivity)getActivity()).getSearchResultsList();
//        List<Event> eventList = ((NavigationActivity)getActivity()).getEventsList();

//        testTransactions = setupDummyTranscations();
        searchResultsListAdapter = new SearchResultsListAdapter(eventList,getActivity());
        eventRecyclerView.setAdapter(searchResultsListAdapter);

//        eventRecyclerView.addOnItemTouchListener(new RecyclerItem);

        eventRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), eventRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                CardView cv = ((CardView)view);
//                cv.
                Log.i("test", "test");
                if(((NavigationActivity)getActivity()).isSearchExpanded()){
                    ((NavigationActivity)getActivity()).closeSearchView();
                }
                Event clickedEvent = searchResultsListAdapter.getItem(position);

                FragmentManager fragmentManager = ((NavigationActivity) getActivity()).getSupportFragmentManager();
                EventLocatorFragment eventLocatorFragment = (EventLocatorFragment) fragmentManager.findFragmentByTag(EventLocatorFragment.TAG);

//                eventLocatorFragment.getSlidingUpPanelLayout().setAnchorPoint(0.6f);
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

    public interface OnChildViewClickListener {
        void onChildViewClick(View view);
    }


    public interface OnChildViewAttachedListener {
        void OnChildViewAttached(String fragmentTag);
    }
    OnChildViewAttachedListener onChildViewAttachedListener;
    OnChildViewClickListener onChildViewClickListener;
    public void onAttachFragment(Fragment fragment)
    {
        try
        {
//            mOnPlayerSelectionSetListener = (OnPlayerSelectionSetListener)fragment;
            onChildViewClickListener = (OnChildViewClickListener)fragment;
            onChildViewAttachedListener = (OnChildViewAttachedListener)fragment;

        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(
                    fragment.toString() + " must implement OnChildViewAttachedListener");
        }
    }
}
