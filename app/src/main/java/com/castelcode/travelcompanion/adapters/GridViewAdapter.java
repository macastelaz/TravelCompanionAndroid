package com.castelcode.travelcompanion.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.castelcode.travelcompanion.TileController;

import java.util.ArrayList;


public class GridViewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<TileController> tiles = new ArrayList<>();


    public GridViewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return tiles.size();
    }

    @Override
    public TileController getItem(int position) {
        return tiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return tiles.get(position).getItemId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout = getItem(position);
        layout.setOnClickListener(new GridViewClickListener(position, mContext));
       return getItem(position);
    }

    public void addTile(TileController controller) {
        tiles.add(controller);
    }

    @SuppressWarnings("unused")
    public boolean removeTile(TileController controller){
        boolean tileRemoved = false;
        int index = tiles.indexOf(controller);
        if(index >= 0) {
            tiles.remove(index);
            tileRemoved = true;
        }
        return tileRemoved;
    }

}
