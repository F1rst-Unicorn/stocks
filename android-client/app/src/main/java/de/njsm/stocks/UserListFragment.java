package de.njsm.stocks;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import de.njsm.stocks.backend.data.User;
import de.njsm.stocks.backend.db.DatabaseHandler;

public class UserListFragment extends Fragment implements AdapterView.OnItemClickListener {

    ListView list;
    User[] users;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_user_list, container, false);

        list = (ListView) result.findViewById(R.id.user_list);
        list.setOnItemClickListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                users = DatabaseHandler.h.getUsers();
                String[] userNames = new String[users.length];
                for (int i = 0; i < users.length; i++){
                    userNames[i] = users[i].name;
                }

                final ListAdapter content = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1,
                        userNames);

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
}
