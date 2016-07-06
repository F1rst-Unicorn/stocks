package de.njsm.stocks.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.njsm.stocks.R;

public class IconStringAdapter extends BaseAdapter {

    Context context;
    int layoutId;
    String [] result;
    int [] imageId;
    private LayoutInflater inflater;

    public IconStringAdapter(Activity context, int layoutId, String[] names, int[] images) {
        result = names;
        this.context = context;
        imageId = images;
        this.layoutId = layoutId;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        return result[position];
    }

    @Override
    public long getItemId(int position) {
        return imageId[position];
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView = inflater.inflate(layoutId, null);
        TextView tv = (TextView) rowView.findViewById(R.id.item_name);
        ImageView iv =(ImageView) rowView.findViewById(R.id.item_icon);
        tv.setText(result[position]);
        iv.setImageResource(imageId[position]);
        return rowView;
    }

}