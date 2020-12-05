package com.castelcode.travelcompanion.conversions;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.castelcode.travelcompanion.R;

public class DesiredTimeValueFragment extends Fragment {

    //private OnFragmentInteractionListener mListener;

    public DesiredTimeValueFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OriginalSingleValueFragment.
     */
    public static DesiredTimeValueFragment newInstance() {
        return new DesiredTimeValueFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_desired_time_value, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle resources){
        View viewToUse = this.getView();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
