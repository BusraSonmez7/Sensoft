package com.sensofttakimi.sensoft;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.sensofttakimi.sensoft.Bildirimler.Bildirimler;

import java.util.ArrayList;

public class ListviewAdapter extends BaseAdapter implements Filterable {
    private LayoutInflater layoutInflater;
    private ArrayList<Bildirimler> bildirimler;
    private ArrayList<Bildirimler> bildirimlerFiltre;
    private Context context;

    public ListviewAdapter(Activity activity, ArrayList<Bildirimler> bildirim){
        this.layoutInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bildirimler=bildirim;
        this.bildirimlerFiltre=bildirim;
        this.context=activity;
    }
    @Override
    public int getCount() {
        return bildirimlerFiltre.size();
    }

    @Override
    public Object getItem(int position) {
        return bildirimlerFiltre.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
