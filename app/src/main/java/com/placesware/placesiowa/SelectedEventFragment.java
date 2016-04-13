package com.placesware.placesiowa;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.placesware.placesiowa.event.Event;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectedEventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectedEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SelectedEventFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static String PARENT_TAG = "none";

    public static final String TAG = "SelectedEventFragment";
    private static Event selectedEvent;



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
    public static SelectedEventFragment newInstance(String param1, String param2) {
        SelectedEventFragment fragment = new SelectedEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public SelectedEventFragment() {
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
        onChildViewAttachedListener.OnChildViewAttached(SelectedEventFragment.TAG);

    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    // set the parent of the
    public static SelectedEventFragment newInstance(String param1) {
        SelectedEventFragment fragment = new SelectedEventFragment();
        Bundle args = new Bundle();
        PARENT_TAG = param1;
        args.putString(PARENT_TAG, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public static SelectedEventFragment newInstance(String param1, Event event) {
        if(event == null){
            throw new NullPointerException("SelectedEventFragment can't have null event");
        }
        SelectedEventFragment fragment = new SelectedEventFragment();
        Bundle args = new Bundle();
        PARENT_TAG = param1;
        args.putString(PARENT_TAG, param1);
        fragment.setArguments(args);
        selectedEvent = event;
        return fragment;
    }

    public static SelectedEventFragment newInstance() {
        SelectedEventFragment fragment = new SelectedEventFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, -1);
        fragment.setArguments(args);
        return fragment;
    }


    TextView selectedEventTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_event_locator, container, false);

        View rootView = inflater.inflate(R.layout.fragment_selected_event,container,false);

        selectedEventTextView = (TextView) rootView.findViewById(R.id.selectedevent);
        selectedEventTextView.setText(selectedEvent.getTitle());
//        selectedEventTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//
////                if (onChildViewClickListener != null)
////                {
////                    onChildViewClickListener.onChildViewClick(v);
////                }
////                ((NavigationActivity)getActivity()).getSlidingUpPanelLayout().setAnchorPoint(0.7f);
////                ((NavigationActivity)getActivity()).getSlidingUpPanelLayout().setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
//            }
//        });

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
