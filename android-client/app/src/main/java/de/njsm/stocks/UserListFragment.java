package de.njsm.stocks;


import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;

import de.njsm.stocks.adapters.IconStringAdapter;
import de.njsm.stocks.backend.data.User;
import de.njsm.stocks.backend.db.DatabaseHandler;

public class UserListFragment extends ListFragment implements AbsListView.OnScrollListener {

    User[] users;
    ListView mList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);
        reload();

        return result;
    }

    public void reload() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                users = DatabaseHandler.h.getUsers();
                String[] userNames = new String[users.length];
                int[] imageIds = new int[users.length];
                for (int i = 0; i < users.length; i++){
                    userNames[i] = users[i].name;
                    imageIds[i] = R.drawable.ic_person_black_24dp;
                }

                final IconStringAdapter content = new IconStringAdapter(getActivity(),
                        R.layout.icon_list_item,
                        userNames,
                        imageIds);


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setListAdapter(content);
                        content.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();
        mList = getListView();
        mList.setOnScrollListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mList.setOnScrollListener(null);
        mList = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent(getActivity(), UserActivity.class);
        i.putExtra(UserActivity.KEY_USER_ID, users[position].id);
        i.putExtra(UserActivity.KEY_USER_NAME, users[position].name);
        startActivity(i);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        SwipeRefreshLayout swiper = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_overlay);
        boolean enable = false;
        if(mList != null && mList.getChildCount() > 0){
            // check if the first item of the mList is visible
            boolean firstItemVisible = mList.getFirstVisiblePosition() == 0;
            // check if the top of the first item is visible
            boolean topOfFirstItemVisible = mList.getChildAt(0).getTop() == 0;
            // enabling or disabling the refresh layout
            enable = firstItemVisible && topOfFirstItemVisible;
        }
        swiper.setEnabled(enable);
    }
}
