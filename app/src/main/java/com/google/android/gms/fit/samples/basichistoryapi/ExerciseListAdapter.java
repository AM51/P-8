package com.google.android.gms.fit.samples.basichistoryapi;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by archit.m on 02/08/16.
 */
public class ExerciseListAdapter extends BaseAdapter{

    ArrayList<String> strings =new ArrayList<>();
    Context context;

    public ExerciseListAdapter(Context context) {
        this.context = context;
        strings.add("Curl");
        strings.add("Press");
    }


    @Override
    public int getCount() {
        //Log.v("archit",""+strings.size());
        return strings.size();
    }

    @Override
    public Object getItem(int position) {
        return strings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView textView = new TextView(context);
        textView.setText(strings.get(position));
        textView.setTextColor(Color.BLACK);
        textView.setHeight(100);
        //Log.v("archit",""+position+" "+strings.get(position));
        return textView;
    }



}
