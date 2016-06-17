package de.njsm.stocks;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import de.njsm.stocks.backend.data.Location;
import de.njsm.stocks.backend.db.DatabaseHandler;

public class LocationListFragment extends Fragment implements AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener {

    ListView list;
    Location[] locations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_location_list, container, false);

        list = (ListView) result.findViewById(R.id.location_list);
        list.setOnItemClickListener(this);
        list.setOnScrollListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                locations = DatabaseHandler.h.getLocations();
                String[] names = new String[locations.length];
                for (int i = 0; i < locations.length; i++){
                    names[i] = locations[i].name;
                }

                final ListAdapter content = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1,
                        names);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        list.setAdapter(content);
                    }
                });
            }
        }).start();

        return result;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (view == list) {


        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        SwipeRefreshLayout swiper = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_overlay);
        boolean enable = false;
        if(list != null && list.getChildCount() > 0){
            // check if the first item of the list is visible
            boolean firstItemVisible = list.getFirstVisiblePosition() == 0;
            // check if the top of the first item is visible
            boolean topOfFirstItemVisible = list.getChildAt(0).getTop() == 0;
            // enabling or disabling the refresh layout
            enable = firstItemVisible && topOfFirstItemVisible;
        }
        swiper.setEnabled(enable);
    }
}
