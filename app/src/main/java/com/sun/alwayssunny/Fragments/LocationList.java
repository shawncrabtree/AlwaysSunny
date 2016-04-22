package com.sun.alwayssunny.Fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.sun.alwayssunny.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Shawn on 4/21/2016.
 */
public class LocationList extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        List<HashMap<String, String>> rows = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < 5; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("title", "Title of book #"+i);
            hm.put("subtitle", "Author of book #"+i);
            hm.put("logo", "1");
            rows.add(hm);
        }
        String[] from = {"logo", "title", "subtitle"};
        int[] to = {R.id.logo, R.id.title, R.id.subtitle};

        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), rows, R.layout.location_listitem, from, to);
        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
