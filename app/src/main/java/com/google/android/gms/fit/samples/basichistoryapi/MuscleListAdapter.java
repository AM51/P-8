package com.google.android.gms.fit.samples.basichistoryapi;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.fit.samples.utils.Utils;

import java.util.ArrayList;

/**
 * Created by archit.m on 02/08/16.
 */
public class MuscleListAdapter extends BaseAdapter {

    ArrayList<String> strings =new ArrayList<>();
    Context context;

    public MuscleListAdapter(Context context) {
        this.context = context;
        strings.add(Utils.MUSCLE_CHEST);
        strings.add(Utils.MUSCLE_BICEPS);
        strings.add(Utils.MUSCLE_BACK);
        strings.add(Utils.MUSCLE_SHOULDERS);
        strings.add(Utils.MUSCLE_TRICEPS);
        strings.add(Utils.MUSCLE_LEGS);
    }


    @Override
    public int getCount() {
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
