package com.google.android.gms.fit.samples.basichistoryapi;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.fit.samples.common.logger.Log;

/**
 * Created by archit.m on 02/08/16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MuscleListFragment extends ListFragment implements AdapterView.OnItemClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.exercise_list_fragment, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setListAdapter(new MuscleListAdapter(getActivity().getApplicationContext()));
        getListView().setOnItemClickListener(this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("test","Inside On Item Click");
        Toast.makeText(getActivity().getApplicationContext(), "Itemm: " + position, Toast.LENGTH_SHORT).show();
        String muscleSelected = (String)getListAdapter().getItem(position);
        Log.e("test",muscleSelected);
        ((MuscleFragmentCallback)getActivity()).onItemSelected(muscleSelected);
        Fragment newFragment = new ExerciseListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        Fragment ml = getFragmentManager().findFragmentByTag("ML");
//        if(ml!=null){
//            transaction.remove(ml);
//        }
//        getFragmentManager().popBackStack();
        transaction.replace(R.id.frameLayout, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public interface MuscleFragmentCallback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(String muscle);
    }


}
